package com.jookovjook.base.Preferences;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.jookovjook.base.R;

public class MyPreferenceFragment extends PreferenceFragment {
    @Override
    public void onCreate(final Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref);
    }
}