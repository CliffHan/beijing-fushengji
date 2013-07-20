package com.cliff.beijing;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;

import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.cliff.beijing.game.Constants;

import de.greenrobot.event.EventBus;

public class SettingsActivity extends SherlockPreferenceActivity implements OnSharedPreferenceChangeListener{

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		if ((key.equals(PreferenceManager.KEY_AUTO_CHANGE_TAB)) 
			||(key.equals(PreferenceManager.KEY_PLAY_SOUND))){
			EventBus.getDefault().post(Constants.UPDATE_SETTINGS);
        }
		
	}
	
	@SuppressWarnings("deprecation")
	@Override
	protected void onResume() {
	    super.onResume();
	    getPreferenceScreen().getSharedPreferences()
	            .registerOnSharedPreferenceChangeListener(this);
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onPause() {
	    super.onPause();
	    getPreferenceScreen().getSharedPreferences()
	            .unregisterOnSharedPreferenceChangeListener(this);
	}	

}
