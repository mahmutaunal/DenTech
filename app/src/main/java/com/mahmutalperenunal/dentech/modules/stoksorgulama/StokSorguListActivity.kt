package com.mahmutalperenunal.dentech.modules.stoksorgulama

import android.app.AlertDialog
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuItemCompat
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import com.mahmutalperenunal.dentech.R
import com.mahmutalperenunal.dentech.adapter.SayimListAdapter
import com.mahmutalperenunal.dentech.adapter.StokSorguAdapter
import com.mahmutalperenunal.dentech.api.RetrofitInstance
import com.mahmutalperenunal.dentech.databinding.ActivityStokSorguListBinding
import com.mahmutalperenunal.dentech.model.NetworkConnection
import com.mahmutalperenunal.dentech.model.SayimModel
import com.mahmutalperenunal.dentech.modules.sayimraporlama.SayimListFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StokSorguListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStokSorguListBinding

    private var materialBarcode: String = ""
    private var locationBarcode: String = ""

    private lateinit var stokSorguAdapter: StokSorguAdapter

    private lateinit var stokSorguData: ArrayList<SayimModel>

    private lateinit var sharedPreferencesSayimNo: SharedPreferences
    private lateinit var sharedPreferencesMaterialBarcode: SharedPreferences
    private lateinit var sharedPreferencesLotBatchNo: SharedPreferences
    private lateinit var sharedPreferencesConfigurationNo: SharedPreferences
    private lateinit var sharedPreferencesSerialNo: SharedPreferences
    private lateinit var sharedPreferencesLocationBarcode: SharedPreferences
    private lateinit var sharedPreferencesAmount: SharedPreferences
    private lateinit var sharedPreferencesDate: SharedPreferences
    private lateinit var sharedPreferencesCount: SharedPreferences
    private lateinit var sharedPreferencesItemClickedCount: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStokSorguListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //setup toolbar
        binding.stokSorguListToolbar.title = resources.getString(R.string.stokSorgu_text)
        setSupportActionBar(binding.stokSorguListToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        //get barcode data from stokSorgulamaActivity
        materialBarcode = intent.getStringExtra("Material Code").toString()
        locationBarcode = intent.getStringExtra("Location Code").toString()

        //set arrayList
        stokSorguData = ArrayList()

        //set adapter
        stokSorguAdapter = StokSorguAdapter(stokSorguData)

        //set recyclerview
        binding.stokSorguListRecyclerView.adapter = stokSorguAdapter
        binding.stokSorguListRecyclerView.layoutManager = LinearLayoutManager(this)

        //set sharedPreferences
        sharedPreferencesSayimNo = getSharedPreferences("Sayim No", MODE_PRIVATE)
        sharedPreferencesMaterialBarcode = getSharedPreferences("Material Barcode", MODE_PRIVATE)
        sharedPreferencesLotBatchNo = getSharedPreferences("Lot Batch No", MODE_PRIVATE)
        sharedPreferencesConfigurationNo = getSharedPreferences("Configuration No", MODE_PRIVATE)
        sharedPreferencesSerialNo = getSharedPreferences("Serial No", MODE_PRIVATE)
        sharedPreferencesLocationBarcode = getSharedPreferences("Location Barcode", MODE_PRIVATE)
        sharedPreferencesAmount = getSharedPreferences("Amount", MODE_PRIVATE)
        sharedPreferencesDate = getSharedPreferences("Date", MODE_PRIVATE)
        sharedPreferencesCount = getSharedPreferences("Count", MODE_PRIVATE)
        sharedPreferencesItemClickedCount = getSharedPreferences("Item Counter", MODE_PRIVATE)

        //get data from sharedPreferences
        for (i in sharedPreferencesCount.getInt("count", 0) downTo 0) {
            stokSorguData.add(SayimModel(sharedPreferencesSayimNo.getString("sayim-${i}", "").toString(),
                sharedPreferencesMaterialBarcode.getString("materialBarcode-${i}", "").toString(),
                sharedPreferencesLotBatchNo.getString("lotBatch-${i}", "").toString(),
                sharedPreferencesConfigurationNo.getString("configuration-${i}", "").toString(),
                sharedPreferencesSerialNo.getString("serial-${i}", "").toString(),
                sharedPreferencesLocationBarcode.getString("locationBarcode-${i}", "").toString(),
                sharedPreferencesAmount.getString("amount-${i}", "").toString(),
                sharedPreferencesDate.getString("date-${i}", "").toString(),
                false))
        }

        //progress bar visibility
        binding.stokSorguListProgressBar.visibility = View.GONE

        checkConnection()

        //refresh page
        binding.stokSorguListSwipeRefreshLayout.setOnRefreshListener {

            //refresh stokSorguListActivity
            val intent = Intent(applicationContext, StokSorguListActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            startActivity(intent)
            finish()

            binding.stokSorguListSwipeRefreshLayout.isRefreshing = false

        }

        //back
        binding.stokSorguListToolbar.setNavigationOnClickListener { onBackPressed() }
    }


    @Deprecated("Deprecated in Java")
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search, menu)
        val menuItem = menu?.findItem(R.id.searchId)
        val searchView: SearchView = MenuItemCompat.getActionView(menuItem) as SearchView
        searchView.isIconified = true

        val searchManager: SearchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))

        searchView.queryHint = "Arama"

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchView.clearFocus()
                stokSorguAdapter.filter.filter(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                stokSorguAdapter.filter.filter(newText)
                return true
            }

        })

        return true
    }


    //get data
    /*private fun getData() {

        RetrofitInstance.apiStokSorgu.getStokSorguData("", materialBarcode, locationBarcode).enqueue(object : Callback<List<SayimModel>> {
            override fun onResponse(
                call: Call<List<SayimModel>>,
                response: Response<List<SayimModel>>
            ) {

                binding.stokSorguListProgressBar.visibility = View.GONE

                binding.stokSorguListDeniedImageView.visibility = View.GONE
                binding.stokSorguListDeniedTextView.visibility = View.GONE

                response.body().let {

                    stokSorguAdapter.setData(it!!)

                }

            }

            override fun onFailure(call: Call<List<SayimModel>>, t: Throwable) {

                binding.stokSorguListProgressBar.visibility = View.GONE

                binding.stokSorguListDeniedImageView.visibility = View.VISIBLE
                binding.stokSorguListDeniedTextView.visibility = View.VISIBLE

                Log.e("Stok Sorgu List Error", t.printStackTrace().toString())
                Toast.makeText(applicationContext, R.string.operation_failed_text, Toast.LENGTH_SHORT).show()

            }
        })

    }*/


    //check network connection
    private fun checkConnection() {

        val networkConnection = NetworkConnection(applicationContext)
        networkConnection.observe(this) { isConnected ->

            if (!isConnected) {

                AlertDialog.Builder(applicationContext, R.style.CustomAlertDialog)
                    .setTitle(R.string.no_internet_connection_title_text)
                    .setMessage(R.string.no_internet_connection_description_text)
                    .setIcon(R.drawable.without_internet)
                    .setPositiveButton(R.string.ok_text) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .create()
                    .show()

            } else {

                //getData()

            }

        }

    }


    //back to stokSorguActivity
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        val intent = Intent(applicationContext, StokSorguActivity::class.java)
        intent.putExtra("Material Barcode", materialBarcode)
        intent.putExtra("Location Barcode", locationBarcode)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        finish()
    }
}