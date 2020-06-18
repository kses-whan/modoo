package com.icure.kses.modoo.activity;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceScreen;

import com.icure.kses.modoo.R;

public class ModooSettingsActivity extends AppCompatActivity {

    public static final String PREF_AUTO_LOGIN = "PREF_AUTO_LOGIN";
    public static final String USER_PREFERENCE = "USER_PREFERENCE";
    public static final String PREF_PRICES = "PREF_PRICES";
    public static final String PREF_LOGOUT = "PREF_LOGOUT";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.settings_main);

    }

    public static class PrefFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener {

        ListPreference pricePreference;

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

            setPreferencesFromResource(R.xml.userpreferences, null);
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
            PreferenceScreen screen = getPreferenceScreen();

            //가격세팅
            pricePreference = (ListPreference)screen.findPreference(PREF_PRICES);
            int index = pricePreference.findIndexOfValue(prefs.getString(PREF_PRICES, "0"));
            pricePreference.setSummary(pricePreference.getEntries()[index]);
            pricePreference.setOnPreferenceChangeListener(this);
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {

            if(preference == pricePreference) {
                int index = pricePreference.findIndexOfValue(newValue.toString());
                pricePreference.setSummary(pricePreference.getEntries()[index]);
            }

            return true;
        }
    }
}
