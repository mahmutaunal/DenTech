package com.mahmutalperenunal.dentech.modules.stoksorgulama

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.mahmutalperenunal.dentech.modules.stoksorgulama.StokSorguListActivity
import com.mahmutalperenunal.dentech.BarcodeScannerFragment
import com.mahmutalperenunal.dentech.R
import com.mahmutalperenunal.dentech.databinding.ActivityStokSorguBinding

class StokSorguActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStokSorguBinding

    private var materialCode: String? = null
    private var locationCode: String? = null

    private var materialBarcode: String = ""
    private var locationBarcode: String = ""

    private var type: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStokSorguBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //setup toolbar
        binding.stokSorguToolbar.title = resources.getString(R.string.stokSorgu_text)
        setSupportActionBar(binding.stokSorguToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        materialCode = intent.extras?.getString("Material Barcode")
        locationCode = intent.extras?.getString("Location Barcode")

        //set barcode data to edittext, if not null
        if (materialCode != null || locationCode != null) {
            binding.stokSorguMaterialBarcodeEditText.setText(materialCode)
            binding.stokSorguLocationBarcodeEditText.setText(locationCode)
        }

        //go to barcodeScannerFragment
        binding.stokSorguMaterialBarcodeImageButton.setOnClickListener {
            type = "Sorgu Material"
            goToBarcodeScannerFragment()
        }
        binding.stokSorguLocationBarcodeImageButton.setOnClickListener {
            type = "Sorgu Location"
            goToBarcodeScannerFragment()
        }

        //get data
        binding.stokSorguBottomNavigationView.setOnItemSelectedListener {

            when (it.itemId) {

                R.id.bottom_nav_sorgu -> goToListActivity()

            }

            true

        }

        //back to homeActivity
        binding.stokSorguToolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }


    //go to barcodeScanner
    private fun goToBarcodeScannerFragment() {

        //get entered edittext data
        getEnteredData()

        //set barcodeScanner bundle
        val bundle = Bundle()
        bundle.putString("Sayim No", "")
        bundle.putString("Material Barcode", materialBarcode)
        bundle.putString("Lot Batch No", "")
        bundle.putString("Configuration No", "")
        bundle.putString("Serial No", "")
        bundle.putString("Location Barcode", locationBarcode)
        bundle.putString("Amount", "")
        bundle.putString("Type", type)
        bundle.putBoolean("Material Barcode Confirm Button Clicked", false)

        val barcodeScannerFragment = BarcodeScannerFragment()

        //set barcodeScanner argument
        barcodeScannerFragment.arguments = bundle

        //open barcodeScannerFragment
        val fragmentManager: FragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
        fragmentTransaction.add(R.id.stokSorgu_constraintLayout, barcodeScannerFragment, "Barcode Scanner")
        fragmentTransaction.commit()

        //add button visibility gone
        binding.stokSorguBottomNavigationView.visibility = View.GONE

    }


    //get entered data from edittext
    private fun getEnteredData() {

        //get edittext data
        materialBarcode = binding.stokSorguMaterialBarcodeEditText.text.toString().trim()
        locationBarcode = binding.stokSorguLocationBarcodeEditText.text.toString().trim()

    }


    //get data from backend
    private fun goToListActivity() {

        //get entered edittext data
        getEnteredData()

        //clear edittext error
        binding.stokSorguMaterialBarcodeEditTextLayout.error = null
        binding.stokSorguLocationBarcodeEditTextLayout.error = null

        //set error, if data is empty
        if (materialBarcode == "" && locationBarcode == "") {

            //set error
            binding.stokSorguMaterialBarcodeEditTextLayout.error = resources.getString(R.string.compulsory_text)
            binding.stokSorguLocationBarcodeEditTextLayout.error = resources.getString(R.string.compulsory_text)
            Toast.makeText(applicationContext, R.string.fill_all_blank_text, Toast.LENGTH_SHORT).show()

        } else {

            //start list activity
            val intent = Intent(applicationContext, StokSorguListActivity::class.java)
            intent.putExtra("Material Code", materialBarcode)
            intent.putExtra("Location Code", locationBarcode)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            finish()

        }

    }

}