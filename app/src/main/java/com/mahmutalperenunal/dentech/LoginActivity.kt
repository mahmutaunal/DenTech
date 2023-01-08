package com.mahmutalperenunal.dentech

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.mahmutalperenunal.dentech.api.RetrofitInstance
import com.mahmutalperenunal.dentech.databinding.ActivityLoginBinding
import com.mahmutalperenunal.dentech.model.AppVersion
import com.mahmutalperenunal.dentech.model.CustomLoadingDialog
import com.mahmutalperenunal.dentech.model.NetworkConnection
import com.mahmutalperenunal.dentech.model.auth.AuthToken
import com.mahmutalperenunal.dentech.model.auth.Login
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private lateinit var loadingDialog: CustomLoadingDialog

    private var passwordEdittext: EditText? = null

    private lateinit var sharedPreferencesAuthToken: SharedPreferences
    private lateinit var sharedPreferencesUsername: SharedPreferences

    private lateinit var editorAuthToken: SharedPreferences.Editor
    private lateinit var editorUsername: SharedPreferences.Editor

    private var versionName: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //animation to show until username and password are verified
        loadingDialog = CustomLoadingDialog(this)

        //set sharedPreference
        sharedPreferencesAuthToken = getSharedPreferences("authToken", MODE_PRIVATE)
        sharedPreferencesUsername = getSharedPreferences("Username", MODE_PRIVATE)

        //set editor data
        editorAuthToken = sharedPreferencesAuthToken.edit()
        editorUsername = sharedPreferencesUsername.edit()

        passwordEdittext = binding.loginPasswordEditText

        getAppVersionCodeAndName()

        checkConnection()

        //clear username edittext with end icon
        binding.loginUsernameEditTextLayout.setEndIconOnClickListener {
            binding.loginUsernameEditText.text!!.clear()
        }

        //homeActivity with username and password
        binding.loginLoginButton.setOnClickListener {

            //show progressDialog
            //loadingDialog.startLoadingDialog()

            //if username and password edittext is empty, set error
            when {
                binding.loginUsernameEditText.text.toString().isEmpty() -> {

                    loadingDialog.dismissDialog()

                    binding.loginUsernameEditText.error = resources.getString(R.string.not_left_blank_text)
                    Toast.makeText(applicationContext, R.string.enter_username_text, Toast.LENGTH_SHORT).show()

                }
                binding.loginPasswordEditText.text.toString().isEmpty() -> {

                    loadingDialog.dismissDialog()

                    binding.loginPasswordEditText.error = resources.getString(R.string.not_left_blank_text)
                    Toast.makeText(applicationContext, R.string.enter_password_text, Toast.LENGTH_SHORT).show()

                }

                //if username and password edittext not empty, login authentication request and start homeActivity
                else -> {

                    //checkAppVersion()

                    val intentHome = Intent(this, HomeActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                    startActivity(intentHome)
                    finish()

                }
            }

        }
    }


    //get application version code and name and display data
    @SuppressLint("SetTextI18n")
    private fun getAppVersionCodeAndName() {
        versionName = BuildConfig.VERSION_NAME
        binding.loginVersionTextView.text = "${resources.getString(R.string.version_text)} $versionName"
    }


    //check app version
    private fun checkAppVersion() {

        val retrofit = RetrofitInstance.apiVersionControl

        val call: Call<AppVersion> = retrofit.postVersionNumber(versionName)
        call.enqueue(object : Callback<AppVersion> {
            override fun onResponse(call: Call<AppVersion>, response: Response<AppVersion>) {

                if (response.isSuccessful) {

                    //loginProcess()

                } else {

                    AlertDialog.Builder(applicationContext, R.style.CustomAlertDialog)
                        .setTitle(R.string.update_text)
                        .setMessage(R.string.update_description_text)
                        .setIcon(R.drawable.without_internet)
                        .setPositiveButton(R.string.update_text) { dialog, _ ->
                            //updateApp()
                            dialog.dismiss()
                        }
                        .create()
                        .show()

                }

            }

            override fun onFailure(call: Call<AppVersion>, t: Throwable) {

                Toast.makeText(applicationContext, R.string.operation_failed_text, Toast.LENGTH_SHORT).show()

            }
        })

    }


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

                //checkAppVersion()

                //loginProcess()

            }

        }

    }

    //login process
    private fun loginProcess() {

        loadingDialog.startLoadingDialog()

        val intentHome = Intent(this, HomeActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)

        //get data
        val username = binding.loginUsernameEditText.text.toString().trim()
        val password = binding.loginPasswordEditText.text.toString().trim()

        val postLogin = Login(username, password, "password")

        val retrofit = RetrofitInstance.apiLogin

        val call: Call<AuthToken> = retrofit.postLogin(postLogin)
        call.enqueue(object : Callback<AuthToken> {
            override fun onResponse(call: Call<AuthToken>, response: Response<AuthToken>) {

                //Log.d("Token", response.body()!!.authToken)
                Log.d("Token", response.body().toString())
                Log.d("Response", response.message())
                Log.d("Response", response.code().toString())

                if (response.isSuccessful) {

                    //save token
                    val authToken = response.body()!!.authToken

                    editorAuthToken.putString("token", authToken)
                    editorAuthToken.apply()

                    if (authToken != null) {

                        editorUsername.putString("username", binding.loginUsernameEditText.text.toString().trim())
                        editorUsername.apply()

                        //start homeActivity
                        startActivity(intentHome)
                        finish()

                        loadingDialog.dismissDialog()

                        Toast.makeText(applicationContext, R.string.logged_text, Toast.LENGTH_SHORT).show()

                    } else {

                        Toast.makeText(applicationContext, R.string.operation_failed_text, Toast.LENGTH_SHORT).show()

                    }

                } else {

                    Toast.makeText(applicationContext, R.string.operation_failed_text, Toast.LENGTH_SHORT).show()

                }

            }

            override fun onFailure(call: Call<AuthToken>, t: Throwable) {

                loadingDialog.dismissDialog()

                //set error
                binding.loginUsernameEditTextLayout.error
                binding.loginPasswordEditTextLayout.error

                Log.e("Login Error", t.printStackTrace().toString())
                Toast.makeText(applicationContext, R.string.operation_failed_text, Toast.LENGTH_SHORT).show()

            }
        })

    }


    //exit application on back button pressed
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        val intentMainExit = Intent(Intent.ACTION_MAIN)
        intentMainExit.addCategory(Intent.CATEGORY_HOME)
        intentMainExit.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intentMainExit)
        finish()
    }
}