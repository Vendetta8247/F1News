package ua.com.vendetta8247.f1news;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;

public class SettingsActivity extends PreferenceActivity
{

    public static Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_general);



    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        finish();
        activity.recreate();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}