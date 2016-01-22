package com.myapps.b.set.preferences;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

public class AppPreferences {
	
	private static final String TAG 						= "AppPreferences";
	/* logged in user related preferences */
	public static final String PREFERENCE_NAME 				= "name";
	public static final String PREFERENCE_USER_NAME 		= "user_name";
	public static final String PREFERENCE_USER_PASSWORD 	= "user_password";
	public static final String PREFERENCE_ENC_USER_NAME 	= "enc_user_name";
	public static final String PREFERENCE_ENC_USER_PASSWORD = "enc_user_password";
	public static final String PREFERENCE_PREVIOUS_USERNAME = "previous_user_name";
	
	public static void saveEncrytedUserPreferences(final Editor editor, String name, String username,
			String password) {
		Log.i(TAG, "Encrypting the username and password before saving");
		Log.i(TAG, "Setting user info in preferences");
		editor.putString(PREFERENCE_NAME, name);
		editor.putString(PREFERENCE_ENC_USER_NAME, username);
		editor.putString(PREFERENCE_ENC_USER_PASSWORD, password);
		if (!editor.commit()) {
			Log.d(TAG, "storeUser preferences commit failed");
		}
	}
	
	public static void saveUserPreferences(final Editor editor, String username,
			String password) {
		Log.i(TAG, "Setting user info in preferences");
		editor.putString(PREFERENCE_USER_NAME, username);
		editor.putString(PREFERENCE_USER_PASSWORD, password);
		if (!editor.commit()) {
			Log.d(TAG, "storeUser preferences commit failed");
		}
	}
	
	public static String getName(SharedPreferences prefs) {
		return prefs.getString(PREFERENCE_NAME, null);
	}

	public static String getEncUserName(SharedPreferences prefs) {
		return prefs.getString(PREFERENCE_ENC_USER_NAME, null);
	}

	public static String getEncUserPassword(SharedPreferences prefs) {
		return prefs.getString(PREFERENCE_ENC_USER_PASSWORD, null);
	}
	
	public static String getUserName(SharedPreferences prefs) {
		return prefs.getString(PREFERENCE_USER_NAME, "");
	}

	public static String getUserPassword(SharedPreferences prefs) {
		return prefs.getString(PREFERENCE_USER_PASSWORD, null);
	}
	
	public static void savePreviousUsername(final Editor editor, String username) {
		editor.putString(PREFERENCE_PREVIOUS_USERNAME, username);
		if (!editor.commit()) {
			Log.d(TAG, "previousUsername commit failed");
		}
	}
	
	public static String getPreviousUsername(SharedPreferences prefs) {
		return prefs.getString(PREFERENCE_PREVIOUS_USERNAME, null);
	}
}
