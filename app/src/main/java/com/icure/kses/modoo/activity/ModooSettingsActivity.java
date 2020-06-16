package com.icure.kses.modoo.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;

import com.icure.kses.modoo.R;

public class ModooSettingsActivity extends AppCompatActivity {

    public static final String PREF_AUTO_UPDATE = "PREF_AUTO_UPDATE";
    public static final String USER_PREFERENCE = "USER_PREFERENCE";
    public static final String PREF_PRICES = "PREF_PRICES";
    public static final String PREF_UPDATE_FREQ = "PREF_UPDATE_FREQ";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.settings_main);
    }

    public static class PrefFragment extends PreferenceFragmentCompat{

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.userpreferences, null);
        }
    }
}
