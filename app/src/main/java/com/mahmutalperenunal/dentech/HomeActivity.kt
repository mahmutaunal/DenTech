package com.mahmutalperenunal.dentech

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import com.mahmutalperenunal.dentech.databinding.ActivityHomeBinding
import com.mahmutalperenunal.dentech.model.CustomLoadingDialog
import com.mahmutalperenunal.dentech.modules.sayimraporlama.SayimMainActivity
import com.mahmutalperenunal.dentech.modules.stoksorgulama.StokSorguActivity
import java.util.*
import kotlin.concurrent.timerTask

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    private lateinit var loadingDialog: CustomLoadingDialog

    private var doubleBackToExitPressedOnce = false

    private lateinit var sharedPreferencesBarcodeCode: SharedPreferences
    private lateinit var sharedPreferencesAuthToken: SharedPreferences
    private lateinit var sharedPreferencesUsername: SharedPreferences

    private lateinit var editorBarcodeCode: SharedPreferences.Editor
    private lateinit var editorAuthToken: SharedPreferences.Editor

    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle

    private var username: String = ""


    @SuppressLint("RtlHardcoded", "UseCompatLoadingForDrawables")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //set progressDialog
        loadingDialog = CustomLoadingDialog(this)

        //set sharedPreference
        sharedPreferencesBarcodeCode = getSharedPreferences("Barcode Code", MODE_PRIVATE)
        sharedPreferencesAuthToken = getSharedPreferences("Auth Token", MODE_PRIVATE)
        sharedPreferencesUsername = getSharedPreferences("Username", MODE_PRIVATE)

        //set editor
        editorBarcodeCode = sharedPreferencesBarcodeCode.edit()
        editorAuthToken = sharedPreferencesAuthToken.edit()

        //clear editor data
        editorBarcodeCode.clear()
        editorBarcodeCode.commit()

        //set navigation drawer
        actionBarDrawerToggle = ActionBarDrawerToggle(this, binding.homeDrawerLayout, R.string.nav_open, R.string.nav_close)
        binding.homeDrawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

        //set up action bar
        binding.homeToolbar.title = resources.getString(R.string.app_name)
        setSupportActionBar(binding.homeToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.homeToolbar.navigationIcon = resources.getDrawable(R.drawable.menu_2)

        getAppVersionCodeAndName()

        //get and set username data
        username = sharedPreferencesUsername.getString("username", null).toString()
        binding.homeUsernameTextView.text = username

        //open navigation drawer item
        binding.homeNavigationView.setNavigationItemSelectedListener {

            when (it.itemId) {

                //sayim raporlama
                R.id.home_nav_menu_sayimRaporlama -> {

                    val intent = Intent(applicationContext, SayimMainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                    startActivity(intent)

                }

                //stok sorgulama
                R.id.home_nav_menu_stokSorgulama -> {

                    val intent = Intent(applicationContext, StokSorguActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                    startActivity(intent)

                }

                //settings
                R.id.home_nav_menu_settings -> {

                    val intent = Intent(applicationContext, SettingsActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                    startActivity(intent)
                }

                //logout
                R.id.home_nav_menu_logout -> {

                    AlertDialog.Builder(this, R.style.CustomAlertDialog)
                        .setTitle(R.string.logout_text)
                        .setMessage(R.string.logout_description_text)
                        .setIcon(R.drawable.logout)
                        .setPositiveButton(R.string.logout_text) {
                                dialog, _ ->
                            logout()
                            dialog.dismiss()
                        }
                        .setNegativeButton(R.string.cancel_text) {
                            dialog, _ ->
                            dialog.dismiss()
                        }
                        .create()
                        .show()

                }

            }

            true

        }

    }


    //set menu item
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            true
        } else super.onOptionsItemSelected(item)
    }


    //get application version code and name and display data
    @SuppressLint("SetTextI18n")
    private fun getAppVersionCodeAndName() {
        val versionName = BuildConfig.VERSION_NAME
        binding.homeVersionTextView.text = "${resources.getString(R.string.version_text)} $versionName"
    }


    //logout process
    private fun logout() {

        loadingDialog.startLoadingDialog()

        //clear token data
        editorAuthToken.clear()
        editorAuthToken.apply()

        val intentLogin = Intent(applicationContext, LoginActivity::class.java)

        //start homeActivity
        val startActivityTimer = timerTask {
            startActivity(intentLogin)
        }

        //set timer
        val timer = Timer()
        timer.schedule(startActivityTimer, 1000)
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)

        /*val retrofit = RetrofitInstance.apiLogout

        val call: Call<Logout> = retrofit.postLogout()
        call.enqueue(object : Callback<Logout> {
            override fun onResponse(call: Call<Logout>, response: Response<Logout>) {

                Log.d("Logout Info", response.body()!!.detail)

                //clear token data
                editorAuthToken.clear()
                editorAuthToken.apply()

                val intentLogin = Intent(applicationContext, LoginActivity::class.java)

                //start homeActivity
                val startActivityTimer = timerTask {
                    startActivity(intentLogin)
                }

                //set timer
                val timer = Timer()
                timer.schedule(startActivityTimer, 3000)
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)

                Toast.makeText(applicationContext, R.string.logged_out_text, Toast.LENGTH_SHORT).show()

            }

            override fun onFailure(call: Call<Logout>, t: Throwable) {

                if (progressDialog.isShowing) progressDialog.dismiss()

                Log.e("Logout Error", t.printStackTrace().toString())

                Toast.makeText(applicationContext, R.string.operation_failed_text, Toast.LENGTH_SHORT).show()

            }
        })*/

    }


    //exit application with double click
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {

        if (doubleBackToExitPressedOnce) {
            val intentMainExit = Intent(Intent.ACTION_MAIN)
            intentMainExit.addCategory(Intent.CATEGORY_HOME)
            intentMainExit.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intentMainExit)
            finish()
            return
        }

        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this, R.string.press_back_again_text, Toast.LENGTH_SHORT).show()

        Handler(Looper.getMainLooper()).postDelayed({
            doubleBackToExitPressedOnce = false
        }, 3000)

    }
}