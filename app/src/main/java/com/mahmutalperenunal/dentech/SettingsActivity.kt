package com.mahmutalperenunal.dentech

import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.mahmutalperenunal.dentech.databinding.SettingsActivityBinding
import java.util.*

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: SettingsActivityBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SettingsActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //setup toolbar
        binding.settingsToolbar.title = resources.getString(R.string.settings_text)
        setSupportActionBar(binding.settingsToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        //back
        binding.settingsToolbar.setNavigationOnClickListener { onBackPressed() }

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        }
    }

    class SettingsFragment : PreferenceFragmentCompat() {

        private lateinit var sharedPreferencesTheme: SharedPreferences
        private lateinit var editorTheme: SharedPreferences.Editor

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)

            //set sharedPreferences
            sharedPreferencesTheme = requireActivity().getSharedPreferences("appTheme", MODE_PRIVATE)
            editorTheme = sharedPreferencesTheme.edit()

            val theme = Objects.requireNonNull(findPreference<ListPreference>("theme"))
            val textSize = Objects.requireNonNull(findPreference<ListPreference>("text_size"))

            theme?.summaryProvider = ListPreference.SimpleSummaryProvider.getInstance()
            textSize?.summaryProvider = ListPreference.SimpleSummaryProvider.getInstance()

            theme?.onPreferenceChangeListener =
                Preference.OnPreferenceChangeListener { _: Preference?, newValue: Any? ->
                    onThemeUpdated(
                        (newValue as String?)!!
                    )
                }
        }

        //change app theme
        private fun onThemeUpdated(newValue: String): Boolean {

            when (newValue) {

                //light
                "1" -> {

                    //change theme
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

                    //save theme
                    editorTheme.putInt("theme", 1)
                    editorTheme.apply()
                }

                //dark
                "2" -> {

                    //change theme
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

                    //save theme
                    editorTheme.putInt("theme", 2)
                    editorTheme.apply()

                }

                //system theme
                "0" -> {

                    //change theme
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)

                    //save theme
                    editorTheme.putInt("theme", -1)
                    editorTheme.apply()

                }

            }

            return true

        }

    }

}