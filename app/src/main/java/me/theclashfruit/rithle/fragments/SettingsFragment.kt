package me.theclashfruit.rithle.fragments

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.CheckBoxPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import me.theclashfruit.rithle.R

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        val licensePref = findPreference<Preference>("licensesItem")
        val darkModePRef = findPreference<CheckBoxPreference>("darkMode")

        licensePref!!.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            R.raw.licenses.toString()

            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Licenses")
                .setMessage(requireContext().resources.openRawResource(R.raw.licenses).readBytes().decodeToString())
                .setPositiveButton("Ok") { _, _ -> }
                .show()

            return@OnPreferenceClickListener true
        }

        darkModePRef!!.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { p, v ->
            AppCompatDelegate.setDefaultNightMode(
                if(v as Boolean) AppCompatDelegate.MODE_NIGHT_YES
                else AppCompatDelegate.MODE_NIGHT_NO
            )

            return@OnPreferenceChangeListener true
        }
    }
}