package com.mahmutalperenunal.dentech

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ScanMode
import com.mahmutalperenunal.dentech.databinding.FragmentBarcodeScannerBinding
import com.mahmutalperenunal.dentech.modules.sayimraporlama.SayimAddFragment
import com.mahmutalperenunal.dentech.modules.stoksorgulama.StokSorguActivity

class BarcodeScannerFragment : Fragment() {

    private val cameraRequestCode = 101

    private var _binding: FragmentBarcodeScannerBinding? = null
    private val binding get() = _binding!!

    private lateinit var codeScanner: CodeScanner

    private var barcodeCode: String = ""
    private var lotBatchCode: String = ""
    private var configurationCode: String = ""
    private var serialCode: String = ""

    private var sayimNo: String = ""
    private var materialBarcode: String = ""
    private var lotBatchNo: String = ""
    private var configurationNo: String = ""
    private var serialNo: String = ""
    private var locationBarcode: String = ""
    private var amount: String = ""

    private var type: String = ""

    private var materialBarcodeCodeClicked: Boolean = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentBarcodeScannerBinding.inflate(inflater, container, false)
        val view = binding.root

        //get data from sayimAddFragment with bundle
        sayimNo = requireArguments().getString("Sayim No").toString()
        materialBarcode = requireArguments().getString("Material Barcode").toString()
        lotBatchNo = requireArguments().getString("Lot Batch No").toString()
        configurationNo = requireArguments().getString("Configuration No").toString()
        serialNo = requireArguments().getString("Serial No").toString()
        locationBarcode = requireArguments().getString("Location Barcode").toString()
        amount = requireArguments().getString("Amount").toString()
        type = requireArguments().getString("Type").toString()
        materialBarcodeCodeClicked = requireArguments().getBoolean("Material Barcode Confirm Button Clicked")

        setupPermission()

        codeScan()

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    //barcode scanner
    private fun codeScan() {

        codeScanner = CodeScanner(requireActivity(), binding.scannerView)

        codeScanner.apply {
            camera = CodeScanner.CAMERA_BACK
            formats = CodeScanner.ALL_FORMATS

            autoFocusMode = AutoFocusMode.SAFE
            scanMode = ScanMode.CONTINUOUS
            isAutoFocusEnabled = true
            isFlashEnabled = false

            decodeCallback = DecodeCallback {
                requireActivity().runOnUiThread {

                    //get scanned data
                    barcodeCode = it.text
                    lotBatchCode = it.text
                    configurationCode = it.text
                    serialCode = it.text

                    //define back to which fragment
                    if (type == "Sayim Material" || type == "Sayim Location") {

                        //set sayimAddFragment bundle
                        val bundle = Bundle()
                        bundle.putString("Sayim No", sayimNo)
                        bundle.putString("Amount", amount)
                        bundle.putString("Type", type)
                        bundle.putBoolean("Material Barcode Confirm Button Clicked", materialBarcodeCodeClicked)

                        if (barcodeCode != "") {

                            if (type == "Sayim Material") {
                                bundle.putString("Material Barcode", barcodeCode)
                                bundle.putString("Lot Batch No", lotBatchCode)
                                bundle.putString("Configuration No", configurationCode)
                                bundle.putString("Serial No", serialCode)
                                bundle.putString("Location Barcode", locationBarcode)
                            } else {
                                bundle.putString("Material Barcode", materialBarcode)
                                bundle.putString("Lot Batch No", lotBatchNo)
                                bundle.putString("Configuration No", configurationNo)
                                bundle.putString("Serial No", serialNo)
                                bundle.putString("Location Barcode", barcodeCode)
                            }

                        } else {

                            if (type == "Sayim Material") {
                                bundle.putString("Material Barcode", materialBarcode)
                                bundle.putString("Lot Batch No", lotBatchNo)
                                bundle.putString("Configuration No", configurationNo)
                                bundle.putString("Serial No", serialNo)
                                bundle.putString("Location Barcode", locationBarcode)
                            } else {
                                bundle.putString("Material Barcode", materialBarcode)
                                bundle.putString("Lot Batch No", lotBatchNo)
                                bundle.putString("Configuration No", configurationNo)
                                bundle.putString("Serial No", serialNo)
                                bundle.putString("Location Barcode", locationBarcode)
                            }

                        }

                        //sayimRaporlamaFragment
                        val sayimAddFragment = SayimAddFragment()

                        //set sayimAddFragment argument
                        sayimAddFragment.arguments = bundle

                        //open sayimAddFragment after barcode scanned
                        val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
                        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
                        fragmentTransaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right)
                        fragmentTransaction.replace(R.id.barcodeScanner_layout_fragment, sayimAddFragment, "Sayim Add")
                        fragmentTransaction.addToBackStack(null)
                        fragmentTransaction.commit()

                    } else {

                        //stokSorguActivity
                        requireActivity().let {
                            val intent = Intent(it, StokSorguActivity::class.java)

                            val extras: Bundle? = intent.extras

                            //send barcode data to stokSorguActivity
                            if (type == "Sorgu Material") {

                                extras?.putString("Material Barcode", barcodeCode)
                                extras?.putString("Location Barcode", locationBarcode)

                                intent.putExtra("Material Barcode", barcodeCode)
                                intent.putExtra("Location Barcode", locationBarcode)

                            } else {

                                extras?.putString("Material Barcode", materialBarcode)
                                extras?.putString("Location Barcode", barcodeCode)

                                intent.putExtra("Material Barcode", materialBarcode)
                                intent.putExtra("Location Barcode", barcodeCode)

                                Toast.makeText(requireActivity().applicationContext, materialBarcode, Toast.LENGTH_SHORT).show()

                            }

                            //start stokSorguActivity
                            it.startActivity(intent)
                            it.finish()
                            it.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
                        }

                    }

                }
            }

            binding.scannerView.setOnClickListener {
                codeScanner.startPreview()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }


    //setup camera request permissions
    private fun setupPermission() {
        val permission = ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.CAMERA)

        if (permission != PackageManager.PERMISSION_GRANTED) {
            makeRequest()
        }
    }

    private fun makeRequest() {
        ActivityCompat.requestPermissions(requireActivity(),
            arrayOf(Manifest.permission.CAMERA),
            cameraRequestCode
        )
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            cameraRequestCode ->
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(requireActivity().applicationContext, R.string.operation_failed_text, Toast.LENGTH_SHORT).show()
                } else {
                    //successful
                }
        }
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {

                //define back to which fragment
                if (type == "Sayim Material" || type == "Sayim Location") {

                    //set sayimAddFragment bundle
                    val bundle = Bundle()
                    bundle.putString("Sayim No", sayimNo)
                    bundle.putString("Amount", amount)
                    bundle.putString("Type", type)
                    bundle.putBoolean("Material Barcode Confirm Button Clicked", materialBarcodeCodeClicked)

                    if (barcodeCode != "") {

                        if (type == "Sayim Material") {
                            bundle.putString("Material Barcode", barcodeCode)
                            bundle.putString("Lot Batch No", lotBatchCode)
                            bundle.putString("Configuration No", configurationCode)
                            bundle.putString("Serial No", serialCode)
                            bundle.putString("Location Barcode", locationBarcode)
                        } else {
                            bundle.putString("Material Barcode", materialBarcode)
                            bundle.putString("Lot Batch No", lotBatchNo)
                            bundle.putString("Configuration No", configurationNo)
                            bundle.putString("Serial No", serialNo)
                            bundle.putString("Location Barcode", barcodeCode)
                        }

                    } else {

                        if (type == "Sayim Material") {
                            bundle.putString("Material Barcode", materialBarcode)
                            bundle.putString("Lot Batch No", lotBatchNo)
                            bundle.putString("Configuration No", configurationNo)
                            bundle.putString("Serial No", serialNo)
                            bundle.putString("Location Barcode", locationBarcode)
                        } else {
                            bundle.putString("Material Barcode", materialBarcode)
                            bundle.putString("Lot Batch No", lotBatchNo)
                            bundle.putString("Configuration No", configurationNo)
                            bundle.putString("Serial No", serialNo)
                            bundle.putString("Location Barcode", locationBarcode)
                        }

                    }

                    //sayimRaporlamaFragment
                    val sayimAddFragment = SayimAddFragment()

                    //set sayimAddFragment argument
                    sayimAddFragment.arguments = bundle

                    //open sayimAddFragment after barcode scanned
                    val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
                    val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
                    fragmentTransaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right)
                    fragmentTransaction.replace(R.id.barcodeScanner_layout_fragment, sayimAddFragment, "Sayim Add")
                    fragmentTransaction.addToBackStack(null)
                    fragmentTransaction.commit()

                } else {

                    //stokSorguActivity
                    requireActivity().let {
                        val intent = Intent(it, StokSorguActivity::class.java)

                        val extras: Bundle? = intent.extras

                        //send barcode data to stokSorguActivity
                        if (type == "Sorgu Material") {

                            //send barcode data
                            if (barcodeCode != "") {

                                extras?.putString("Material Barcode", barcodeCode)
                                extras?.putString("Location Barcode", locationBarcode)

                                intent.putExtra("Material Barcode", barcodeCode)
                                intent.putExtra("Location Barcode", locationBarcode)

                            } else {

                                extras?.putString("Material Barcode", materialBarcode)
                                extras?.putString("Location Barcode", locationBarcode)

                                intent.putExtra("Material Barcode", materialBarcode)
                                intent.putExtra("Location Barcode", locationBarcode)

                            }


                        } else {

                            //send barcode data
                            if (barcodeCode != "") {

                                extras?.putString("Material Barcode", materialBarcode)
                                extras?.putString("Location Barcode", barcodeCode)

                                intent.putExtra("Material Barcode", materialBarcode)
                                intent.putExtra("Location Barcode", barcodeCode)

                            } else {

                                extras?.putString("Material Barcode", materialBarcode)
                                extras?.putString("Location Barcode", locationBarcode)

                                intent.putExtra("Material Barcode", materialBarcode)
                                intent.putExtra("Location Barcode", locationBarcode)

                            }

                        }

                        //start stokSorguActivity
                        it.startActivity(intent)
                        it.finish()
                        it.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
                    }

                }

            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)

    }

}