package com.mahmutalperenunal.dentech.modules.sayimraporlama

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.mahmutalperenunal.dentech.R
import com.mahmutalperenunal.dentech.databinding.ActivitySayimMainBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener

class SayimMainActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySayimMainBinding

    private lateinit var sharedPreferencesCount: SharedPreferences
    private lateinit var editorCount: SharedPreferences.Editor


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySayimMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //setup toolbar
        binding.sayimMainToolbar.title = resources.getString(R.string.sayimRapor_text)
        setSupportActionBar(binding.sayimMainToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        //set sharedPreferences data
        sharedPreferencesCount = getSharedPreferences("Count", MODE_PRIVATE)
        editorCount = sharedPreferencesCount.edit()

        editorCount.clear()
        editorCount.apply()

        //set sayimAddFragment bundle
        val bundle = Bundle()
        bundle.putString("Sayim No", "")
        bundle.putString("Material Barcode", "")
        bundle.putString("Lot Batch No", "")
        bundle.putString("Configuration No", "")
        bundle.putString("Serial No", "")
        bundle.putString("Location Barcode", "")
        bundle.putString("Amount", "")
        bundle.putString("Type", "")
        bundle.putBoolean("Material Barcode Confirm Button Clicked", false)

        val sayimAddFragment = SayimAddFragment()

        //set sayimAddFragment arguments
        sayimAddFragment.arguments = bundle

        //start sayimAddFragment
        val fragmentManager: FragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.sayim_constraintLayout, sayimAddFragment, "Sayim Add")
        fragmentTransaction.commit()

        //open fragment with selected tab
        binding.sayimMainTabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {

            override fun onTabSelected(tab: TabLayout.Tab?) {

                when (tab!!.position) {

                    1 -> {

                        //set search view visibility
                        if (findViewById<SearchView>(R.id.searchId) != null) {
                            findViewById<SearchView>(R.id.searchId).visibility = View.GONE
                        }

                        //open sayimListFragment
                        val sayimListFragment = SayimListFragment()
                        val fragmentListManager: FragmentManager = supportFragmentManager
                        val fragmentListTransaction: FragmentTransaction = fragmentListManager.beginTransaction()
                        fragmentListTransaction.add(R.id.sayim_constraintLayout, sayimListFragment, "Sayim List")
                        fragmentListTransaction.commit()

                    }

                    0 -> {

                        //set search view visibility
                        if (findViewById<SearchView>(R.id.searchId) != null) {
                            findViewById<SearchView>(R.id.searchId).visibility = View.GONE
                        }

                        //set sayimAddFragment bundle
                        val bundle = Bundle()
                        bundle.putString("Sayim No", "")
                        bundle.putString("Material Barcode", "")
                        bundle.putString("Lot Batch No", "")
                        bundle.putString("Configuration No", "")
                        bundle.putString("Serial No", "")
                        bundle.putString("Location Barcode", "")
                        bundle.putString("Amount", "")
                        bundle.putString("Type", "")
                        bundle.putBoolean("Material Barcode Confirm Button Clicked", false)

                        val sayimAddFragment = SayimAddFragment()

                        //set sayimAddFragment argument
                        sayimAddFragment.arguments = bundle

                        //open sayimAddFragment
                        val fragmentManager: FragmentManager = supportFragmentManager
                        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
                        fragmentTransaction.add(R.id.sayim_constraintLayout, sayimAddFragment, "Sayim Add")
                        fragmentTransaction.commit()

                    }

                }

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) { }

            override fun onTabReselected(tab: TabLayout.Tab?) { }

        })

        //back
        binding.sayimMainToolbar.setNavigationOnClickListener {
            onBackPressed()
        }

    }

}