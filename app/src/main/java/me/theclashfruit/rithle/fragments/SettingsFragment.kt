package me.theclashfruit.rithle.fragments

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import me.theclashfruit.rithle.R

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }
}