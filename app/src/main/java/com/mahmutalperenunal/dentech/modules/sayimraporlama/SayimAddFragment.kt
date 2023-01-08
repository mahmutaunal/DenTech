package com.mahmutalperenunal.dentech.modules.sayimraporlama

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.mahmutalperenunal.dentech.BarcodeScannerFragment
import com.mahmutalperenunal.dentech.HomeActivity
import com.mahmutalperenunal.dentech.R
import com.mahmutalperenunal.dentech.api.RetrofitInstance
import com.mahmutalperenunal.dentech.databinding.FragmentSayimAddBinding
import com.mahmutalperenunal.dentech.model.NetworkConnection
import com.mahmutalperenunal.dentech.model.SayimModel
import com.mahmutalperenunal.dentech.modules.stoksorgulama.StokSorguActivity
import com.google.android.material.tabs.TabLayout
import retrofit2.Call
import java.text.SimpleDateFormat
import java.util.*
import retrofit2.Callback
import retrofit2.Response

class SayimAddFragment : Fragment() {

    private var _binding: FragmentSayimAddBinding? = null
    private val binding get() = _binding!!

    private var sayimNo: String = ""
    private var materialBarcode: String = ""
    private var lotBatchNo: String = ""
    private var configurationNo: String = ""
    private var serialNo: String = ""
    private var locationBarcode: String = ""
    private var amount: String = ""

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

    private lateinit var editorSayimNo: SharedPreferences.Editor
    private lateinit var editorMaterialBarcode: SharedPreferences.Editor
    private lateinit var editorLotBatchNo: SharedPreferences.Editor
    private lateinit var editorConfigurationNo: SharedPreferences.Editor
    private lateinit var editorSerialNo: SharedPreferences.Editor
    private lateinit var editorLocationBarcode: SharedPreferences.Editor
    private lateinit var editorAmount: SharedPreferences.Editor
    private lateinit var editorDate: SharedPreferences.Editor
    private lateinit var editorCount: SharedPreferences.Editor

    private var count: Int = 0

    private var type: String = ""

    private var materialBarcodeCodeClicked: Boolean = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSayimAddBinding.inflate(inflater, container, false)
        val view = binding.root

        //set search view visibility
        if (requireActivity().findViewById<SearchView>(R.id.searchId) != null) {
            requireActivity().findViewById<SearchView>(R.id.searchId).visibility = View.GONE
        }

        //set arrayList
        sayimData = ArrayList()

        //get and set data
        binding.sayimAddSayimNoEditText.setText(requireArguments().getString("Sayim No"))
        binding.sayimAddMaterialBarcodeEditText.setText(requireArguments().getString("Material Barcode"))
        binding.sayimAddLotBatchNoEditText.setText(requireArguments().getString("Lot Batch No"))
        binding.sayimAddConfigurationNoEditText.setText(requireArguments().getString("Configuration No"))
        binding.sayimAddSerialNoEditText.setText(requireArguments().getString("Serial No"))
        binding.sayimAddLocationBarcodeEditText.setText(requireArguments().getString("Location Barcode"))
        binding.sayimAddAmountEditText.setText(requireArguments().getString("Amount"))
        type = requireArguments().getString("Type").toString()
        materialBarcodeCodeClicked = requireArguments().getBoolean("Material Barcode Confirm Button Clicked")

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

        //get count data
        count = sharedPreferencesCount.getInt("count", 0)

        //add button visibility view
        binding.sayimAddBottomNavigationView.visibility = View.VISIBLE

        //tab layout visibility view
        requireActivity().findViewById<TabLayout>(R.id.sayim_main_tabLayout).visibility = View.VISIBLE

        //set visibility if barcode scanned
        if (materialBarcodeCodeClicked) {

            binding.sayimAddConfigurationNoEditTextLayout.visibility = View.VISIBLE
            binding.sayimAddLotBatchNoEditTextLayout.visibility = View.VISIBLE
            binding.sayimAddSerialNoEditTextLayout.visibility = View.VISIBLE

        }

        getEnteredData()

        checkBarcodeNo()

        //go to barcodeScanner for material barcode
        binding.sayimAddMaterialBarcodeImageButton.setOnClickListener {
            type = "Sayim Material"
            goToBarcodeScannerFragment()
        }

        //go to barcodeScanner for location barcode
        binding.sayimAddLocationBarcodeImageButton.setOnClickListener {
            type = "Sayim Location"
            goToBarcodeScannerFragment()
        }

        //add data to backend onClick
        binding.sayimAddBottomNavigationView.setOnItemSelectedListener {

            when (it.itemId) {

                R.id.bottom_nav_add -> addDataToBackend()

                else -> { }

            }

            true

        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    //go to barcodeScanner
    private fun goToBarcodeScannerFragment() {

        //get entered edittext data
        getEnteredData()

        //send edittext data to barcodeScannerFragment
        val bundle = Bundle()
        bundle.putString("Sayim No", sayimNo)
        bundle.putString("Material Barcode", materialBarcode)
        bundle.putString("Lot Batch No", lotBatchNo)
        bundle.putString("Configuration No", configurationNo)
        bundle.putString("Serial No", serialNo)
        bundle.putString("Location Barcode", locationBarcode)
        bundle.putString("Amount", amount)
        bundle.putString("Type", type)
        bundle.putBoolean("Material Barcode Confirm Button Clicked", materialBarcodeCodeClicked)

        val barcodeScannerFragment = BarcodeScannerFragment()

        //set fragment arguments
        barcodeScannerFragment.arguments = bundle

        //start barcodeScannerFragment
        val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
        fragmentTransaction.replace(R.id.sayim_add_frameLayout, barcodeScannerFragment, "Barcode Scanner")
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()

        //add button visibility gone
        binding.sayimAddBottomNavigationView.visibility = View.GONE

        //tab layout visibility gone
        requireActivity().findViewById<TabLayout>(R.id.sayim_main_tabLayout).visibility = View.GONE

    }


    //get entered data from edittext
    private fun getEnteredData() {

        //get edittext data
        sayimNo = binding.sayimAddSayimNoEditText.text.toString().trim()
        materialBarcode = binding.sayimAddMaterialBarcodeEditText.text.toString().trim()
        lotBatchNo = binding.sayimAddLotBatchNoEditText.text.toString().trim()
        configurationNo = binding.sayimAddConfigurationNoEditText.text.toString().trim()
        serialNo = binding.sayimAddSerialNoEditText.text.toString().trim()
        locationBarcode = binding.sayimAddLocationBarcodeEditText.text.toString().trim()
        amount = binding.sayimAddAmountEditText.text.toString().trim()

    }


    //add data to backend
    @SuppressLint("SimpleDateFormat")
    private fun addDataToBackend() {

        //get entered edittext data
        getEnteredData()

        //clear edittext error
        binding.sayimAddSayimNoEditTextLayout.error = null
        binding.sayimAddMaterialBarcodeEditTextLayout.error = null
        binding.sayimAddLotBatchNoEditTextLayout.error = null
        binding.sayimAddConfigurationNoEditTextLayout.error = null
        binding.sayimAddSerialNoEditTextLayout.error = null
        binding.sayimAddLocationBarcodeEditTextLayout.error = null
        binding.sayimAddAmountEditTextLayout.error = null

        //set error, if data is empty
        if (sayimNo == "") {

            binding.sayimAddSayimNoEditTextLayout.error = resources.getString(R.string.compulsory_text)
            Toast.makeText(requireActivity().applicationContext, R.string.fill_all_blank_text, Toast.LENGTH_SHORT).show()

        } else if (materialBarcode == "") {

            binding.sayimAddMaterialBarcodeEditTextLayout.error = resources.getString(R.string.compulsory_text)
            Toast.makeText(requireActivity().applicationContext, R.string.fill_all_blank_text, Toast.LENGTH_SHORT).show()

        } else if (lotBatchNo == "") {

            binding.sayimAddLotBatchNoEditTextLayout.error = resources.getString(R.string.compulsory_text)
            Toast.makeText(requireActivity().applicationContext, R.string.fill_all_blank_text, Toast.LENGTH_SHORT).show()

        } else if (configurationNo == "") {

            binding.sayimAddConfigurationNoEditTextLayout.error = resources.getString(R.string.compulsory_text)
            Toast.makeText(requireActivity().applicationContext, R.string.fill_all_blank_text, Toast.LENGTH_SHORT).show()

        } else if (serialNo == "") {

            binding.sayimAddSerialNoEditTextLayout.error = resources.getString(R.string.compulsory_text)
            Toast.makeText(requireActivity().applicationContext, R.string.fill_all_blank_text, Toast.LENGTH_SHORT).show()

        } else if (locationBarcode == "") {

            binding.sayimAddLocationBarcodeEditTextLayout.error = resources.getString(R.string.compulsory_text)
            Toast.makeText(requireActivity().applicationContext, R.string.fill_all_blank_text, Toast.LENGTH_SHORT).show()

        } else if (amount == "") {

            binding.sayimAddAmountEditTextLayout.error = resources.getString(R.string.compulsory_text)
            Toast.makeText(requireActivity().applicationContext, R.string.fill_all_blank_text, Toast.LENGTH_SHORT).show()

        } else if (!materialBarcodeCodeClicked) {

            Toast.makeText(requireActivity().applicationContext, "Lütfen Barkodu Kontrol Edin!", Toast.LENGTH_SHORT).show()

        } else {

            //if don't empty send data to backend

            count++

            //set current date and time
            val sdf = SimpleDateFormat("dd/MM/yyyy - HH:mm:ss")
            val currentDateAndTime = sdf.format(Date())

            //save count data with sharedPreferences
            editorCount.putInt("count", count)
            editorCount.apply()

            //save data with sharedPreferences
            editorSayimNo.putString("sayim-${count}", sayimNo)
            editorMaterialBarcode.putString("materialBarcode-${count}", materialBarcode)
            editorLotBatchNo.putString("lotBatch-${count}", lotBatchNo)
            editorConfigurationNo.putString("configuration-${count}", configurationNo)
            editorSerialNo.putString("serial-${count}", serialNo)
            editorLocationBarcode.putString("locationBarcode-${count}", locationBarcode)
            editorAmount.putString("amount-${count}", amount)
            editorDate.putString("date-${count}", currentDateAndTime)

            editorSayimNo.apply()
            editorMaterialBarcode.apply()
            editorLotBatchNo.apply()
            editorConfigurationNo.apply()
            editorSerialNo.apply()
            editorLocationBarcode.apply()
            editorAmount.apply()
            editorDate.apply()

            /*RetrofitInstance.apiSayim.postSayimData("", SayimModel(sayimNo, materialBarcode, lotBatchNo, configurationNo, serialNo, locationBarcode, amount, currentDateAndTime, false)).enqueue(object : Callback<SayimModel> {
                override fun onResponse(call: Call<SayimModel>, response: Response<SayimModel>) {

                    //show success message for user
                    Toast.makeText(requireActivity().applicationContext, R.string.saved_text, Toast.LENGTH_SHORT).show()

                    //open listFragment
                    val sayimListFragment = SayimListFragment()
                    val fragmentListManager: FragmentManager = requireActivity().supportFragmentManager
                    val fragmentListTransaction: FragmentTransaction = fragmentListManager.beginTransaction()
                    fragmentListTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                    fragmentListTransaction.replace(R.id.sayim_main_fragment_layout, sayimListFragment, "Sayim List")
                    fragmentListTransaction.commit()

                }

                override fun onFailure(call: Call<SayimModel>, t: Throwable) {

                    Log.e("Sayim Add Error", t.printStackTrace().toString())
                    Toast.makeText(requireActivity().applicationContext, R.string.operation_failed_text, Toast.LENGTH_SHORT).show()

                }

            })*/

            //show success message for user
            Toast.makeText(requireActivity().applicationContext, R.string.saved_text, Toast.LENGTH_SHORT).show()

            //open listFragment
            val sayimListFragment = SayimListFragment()
            val fragmentListManager: FragmentManager = requireActivity().supportFragmentManager
            val fragmentListTransaction: FragmentTransaction = fragmentListManager.beginTransaction()
            fragmentListTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
            fragmentListTransaction.replace(R.id.sayim_constraintLayout, sayimListFragment, "Sayim List")
            fragmentListTransaction.commit()

        }

    }


    //check material barcode data for lot batch no, configuration no and serial no
    private fun checkBarcodeNo() {

        //set visibility
        if (binding.sayimAddMaterialBarcodeEditText.text.toString() != "" && materialBarcodeCodeClicked) {
            binding.sayimAddLotBatchNoEditTextLayout.visibility = View.VISIBLE
            binding.sayimAddConfigurationNoEditTextLayout.visibility = View.VISIBLE
            binding.sayimAddSerialNoEditTextLayout.visibility = View.VISIBLE
        }

        //end icon onClickListener
        binding.sayimAddMaterialBarcodeEditTextLayout.setEndIconOnClickListener {

            materialBarcodeCodeClicked = true

            binding.sayimAddLotBatchNoEditTextLayout.visibility = View.VISIBLE
            binding.sayimAddConfigurationNoEditTextLayout.visibility = View.VISIBLE
            binding.sayimAddSerialNoEditTextLayout.visibility = View.VISIBLE

        }

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

            }

            else {

                //if phone connect to internet, save data
                addDataToBackend()

            }

        }

    }


    //back to homeActivity
    override fun onAttach(context: Context) {
        super.onAttach(context)

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {

                requireActivity().let {
                    val intent = Intent(it, HomeActivity::class.java)
                    it.startActivity(intent)
                    it.finish()
                    it.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
                }

            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)

    }

}