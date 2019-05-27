package com.example.yeticlicker

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat

class Prefs : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.prefs, rootKey)
    }
}