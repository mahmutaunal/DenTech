package com.mahmutalperenunal.dentech.modules.sayimraporlama

import android.app.AlertDialog
import android.app.SearchManager
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.CheckBox
import android.widget.TextView
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuItemCompat
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import com.mahmutalperenunal.dentech.R
import com.mahmutalperenunal.dentech.adapter.SayimListAdapter
import com.mahmutalperenunal.dentech.api.RetrofitInstance
import com.mahmutalperenunal.dentech.databinding.FragmentSayimListBinding
import com.mahmutalperenunal.dentech.model.NetworkConnection
import com.mahmutalperenunal.dentech.model.SayimModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SayimListFragment : Fragment() {

    private var _binding: FragmentSayimListBinding? = null
    private val binding get() = _binding!!

    private lateinit var sayimAdapter: SayimListAdapter

    private lateinit var sayimData: ArrayList<SayimModel>

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

    private lateinit var editorSayimNo: SharedPreferences.Editor
    private lateinit var editorMaterialBarcode: SharedPreferences.Editor
    private lateinit var editorLotBatchNo: SharedPreferences.Editor
    private lateinit var editorConfigurationNo: SharedPreferences.Editor
    private lateinit var editorSerialNo: SharedPreferences.Editor
    private lateinit var editorLocationBarcode: SharedPreferences.Editor
    private lateinit var editorAmount: SharedPreferences.Editor
    private lateinit var editorDate: SharedPreferences.Editor
    private lateinit var editorCount: SharedPreferences.Editor


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSayimListBinding.inflate(inflater, container, false)
        val view = binding.root

        //set search view visibility
        if (requireActivity().findViewById<SearchView>(R.id.searchId) != null) {
            requireActivity().findViewById<SearchView>(R.id.searchId).visibility = View.GONE
        }

        //set arrayList
        sayimData = ArrayList()

        //set adapter
        sayimAdapter = SayimListAdapter(sayimData)

        //set recyclerview
        binding.sayimListRecyclerView.adapter = sayimAdapter
        binding.sayimListRecyclerView.layoutManager = LinearLayoutManager(requireActivity())

        //set sharedPreferences
        sharedPreferencesSayimNo = requireActivity().getSharedPreferences("Sayim No", AppCompatActivity.MODE_PRIVATE)
        sharedPreferencesMaterialBarcode = requireActivity().getSharedPreferences("Material Barcode", AppCompatActivity.MODE_PRIVATE)
        sharedPreferencesLotBatchNo = requireActivity().getSharedPreferences("Lot Batch No", AppCompatActivity.MODE_PRIVATE)
        sharedPreferencesConfigurationNo = requireActivity().getSharedPreferences("Configuration No", AppCompatActivity.MODE_PRIVATE)
        sharedPreferencesSerialNo = requireActivity().getSharedPreferences("Serial No", AppCompatActivity.MODE_PRIVATE)
        sharedPreferencesLocationBarcode = requireActivity().getSharedPreferences("Location Barcode", AppCompatActivity.MODE_PRIVATE)
        sharedPreferencesAmount = requireActivity().getSharedPreferences("Amount", AppCompatActivity.MODE_PRIVATE)
        sharedPreferencesDate = requireActivity().getSharedPreferences("Date", AppCompatActivity.MODE_PRIVATE)
        sharedPreferencesCount = requireActivity().getSharedPreferences("Count", AppCompatActivity.MODE_PRIVATE)
        sharedPreferencesItemClickedCount = requireActivity().getSharedPreferences("Item Counter", AppCompatActivity.MODE_PRIVATE)

        //set editor
        editorSayimNo = sharedPreferencesSayimNo.edit()
        editorMaterialBarcode = sharedPreferencesMaterialBarcode.edit()
        editorLotBatchNo = sharedPreferencesLotBatchNo.edit()
        editorConfigurationNo = sharedPreferencesConfigurationNo.edit()
        editorSerialNo = sharedPreferencesSerialNo.edit()
        editorLocationBarcode = sharedPreferencesLocationBarcode.edit()
        editorAmount = sharedPreferencesAmount.edit()
        editorDate = sharedPreferencesDate.edit()
        editorCount = sharedPreferencesCount.edit()

        //get data from sharedPreferences
        for (i in sharedPreferencesCount.getInt("count", 0) downTo 1) {
            sayimData.add(SayimModel(sharedPreferencesSayimNo.getString("sayim-${i}", "").toString(),
                sharedPreferencesMaterialBarcode.getString("materialBarcode-${i}", "").toString(),
                sharedPreferencesLotBatchNo.getString("lotBatch-${i}", "").toString(),
                sharedPreferencesConfigurationNo.getString("configuration-${i}", "").toString(),
                sharedPreferencesSerialNo.getString("serial-${i}", "").toString(),
                sharedPreferencesLocationBarcode.getString("locationBarcode-${i}", "").toString(),
                sharedPreferencesAmount.getString("amount-${i}", "").toString(),
                sharedPreferencesDate.getString("date-${i}", "").toString(),
                false))
        }

        //delete button visibility
        binding.sayimListBottomNavigationView.visibility = View.GONE

        //progress bar visibility
        binding.sayimListProgressBar.visibility = View.GONE

        checkConnection()

        longClickProcess()
        clickProcess()

        //refresh page
        binding.sayimListSwipeRefreshLayout.setOnRefreshListener {

            //open sayimListFragment
            val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
            val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.sayim_constraintLayout, SayimListFragment(), "Sayim List")
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()

            binding.sayimListSwipeRefreshLayout.isRefreshing = false

        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    //check network connection
    private fun checkConnection() {

        val networkConnection = NetworkConnection(requireActivity())
        networkConnection.observe(viewLifecycleOwner) { isConnected ->

            if (!isConnected) {

                AlertDialog.Builder(requireActivity(), R.style.CustomAlertDialog)
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


    //show delete dialog
    private fun deleteDataDialogBox(position: Int) {

        AlertDialog.Builder(requireActivity(), R.style.CustomAlertDialog)
            .setTitle(R.string.delete_text)
            .setMessage(R.string.delete_description_text)
            .setIcon(R.drawable.close_window)
            .setPositiveButton(R.string.delete_text) {
                    dialog, _ ->

                deleteData(position)

                dialog.dismiss()
            }
            .setNegativeButton(R.string.cancel_text) {
                dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()

    }


    //delete data
    private fun deleteData(position: Int) {

        //remove data from array list
        sayimData.removeAt(position)

        editorSayimNo.remove("sayim-${position + 1}")
        editorMaterialBarcode.remove("materialBarcode-${position + 1}")
        editorLotBatchNo.remove("lotBatch-${position + 1}")
        editorConfigurationNo.remove("configuration-${position + 1}")
        editorSerialNo.remove("serial-${position + 1}")
        editorLocationBarcode.remove("locationBarcode-${position + 1}")
        editorAmount.remove("amount-${position + 1}")
        editorDate.remove("date-${position + 1}")

        editorSayimNo.apply()
        editorMaterialBarcode.apply()
        editorLotBatchNo.apply()
        editorConfigurationNo.apply()
        editorSerialNo.apply()
        editorLocationBarcode.apply()
        editorAmount.apply()
        editorDate.apply()

        //show success message
        Toast.makeText(requireActivity().applicationContext, R.string.deleted_text, Toast.LENGTH_SHORT).show()

        //open sayimListFragment
        val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.sayim_list_fragmentLayout, SayimListFragment(), "Sayim List")
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()

        /*RetrofitInstance.apiSayim.putSayimData("", position, SayimModel("0", "0", "0", "0", "0", "0", "0", "0", false)).enqueue(object : Callback<SayimModel> {
                override fun onResponse(call: Call<SayimModel>, response: Response<SayimModel>) {

                    //show success message
                    Toast.makeText(requireActivity().applicationContext, R.string.deleted_text, Toast.LENGTH_SHORT).show()

                    //open sayimListFragment
                    val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
                    val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
                    fragmentTransaction.replace(R.id.sayim_list_fragmentLayout, SayimListFragment(), "Sayim List")
                    fragmentTransaction.addToBackStack(null)
                    fragmentTransaction.commit()

                }

                override fun onFailure(call: Call<SayimModel>, t: Throwable) {

                    Log.e("Sayim Delete Error", t.printStackTrace().toString())
                    Toast.makeText(requireActivity().applicationContext, R.string.operation_failed_text, Toast.LENGTH_SHORT).show()

                }

            })*/

    }


    private fun longClickProcess() {

        sayimAdapter.setOnItemLongClickListener(object : SayimListAdapter.OnItemLongClickListener {
            override fun onItemLongClick(position: Int) {

                if (sharedPreferencesItemClickedCount.getInt("count", 0) <= 0) {

                    binding.sayimListBottomNavigationView.visibility = View.GONE
                    requireActivity().findViewById<CheckBox>(R.id.sayim_item_checkBox).visibility = View.GONE

                } else {

                    binding.sayimListBottomNavigationView.visibility = View.VISIBLE
                    requireActivity().findViewById<CheckBox>(R.id.sayim_item_checkBox).visibility = View.VISIBLE

                    //delete data onClick
                    binding.sayimListBottomNavigationView.setOnItemSelectedListener {

                        when (it. itemId) {

                            R.id.bottom_nav_delete -> deleteDataDialogBox(position)

                            else -> {}

                        }

                        true

                    }

                }

            }

        })

    }


    private fun clickProcess() {

        sayimAdapter.setOnItemClickListener(object : SayimListAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {

                if (sharedPreferencesItemClickedCount.getInt("count", 0) <= 0) {

                    //delete button visibility
                    binding.sayimListBottomNavigationView.visibility = View.GONE

                } else {

                    //delete button visibility
                    binding.sayimListBottomNavigationView.visibility = View.VISIBLE

                    //delete data onClick
                    binding.sayimListBottomNavigationView.setOnItemSelectedListener {

                        when (it. itemId) {

                            R.id.bottom_nav_delete -> deleteDataDialogBox(position)

                            else -> {}

                        }

                        true

                    }

                }

            }

        })

    }

}