package com.example.yeticlicker

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager

class OptionsActivity : AppCompatActivity() {
    private val SP: String = "PRIVATE STORAGE"
    private val BG: String = "BG"

    private var statePref: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.PreferenceScreen)
        setContentView(R.layout.activity_options)

        supportFragmentManager.beginTransaction().add(
            R.id.options,
            Prefs()
        ).commit()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(
            R.anim.back_in_new_activity,
            R.anim.back_out_new_activity
        )

        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        statePref = prefs.getBoolean("switch_preference", false)

        val sp: SharedPreferences.Editor? = getSharedPreferences(SP, Context.MODE_PRIVATE).edit()
        sp?.putBoolean(BG, statePref)
        sp?.commit()
    }
}
