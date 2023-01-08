package com.mahmutalperenunal.dentech

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import com.mahmutalperenunal.dentech.databinding.ActivitySplashBinding

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    private lateinit var sharedPreferencesTheme: SharedPreferences

    private var theme: Int? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //set system bar color
        window.navigationBarColor = (ContextCompat.getColor(this, R.color.splash_screen_color))
        window.statusBarColor = (ContextCompat.getColor(this, R.color.splash_screen_color))

        //set sharedPreferences
        sharedPreferencesTheme = getSharedPreferences("appTheme", MODE_PRIVATE)

        checkTheme()

        //open login activity with timer
        binding.apply {
            splashLogoImageView.alpha = 0f
            splashLogoImageView.animate().setDuration(1500).alpha(1f).withEndAction{
                val intent = Intent(this@SplashActivity, LoginActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                startActivity(intent)
                finish()
            }
        }
    }


    //control app theme
    private fun checkTheme() {

        theme = sharedPreferencesTheme.getInt("theme", 0)

        val appTheme = when (theme) {
            -1 -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM //-1
            2 -> AppCompatDelegate.MODE_NIGHT_YES //2
            else -> AppCompatDelegate.MODE_NIGHT_NO //1
        }
        Log.d("App Theme", "theme:$appTheme")
        AppCompatDelegate.setDefaultNightMode(appTheme)

    }
}