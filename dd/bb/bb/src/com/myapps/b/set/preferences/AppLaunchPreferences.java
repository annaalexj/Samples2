package com.myapps.b.set.preferences;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

public class AppLaunchPreferences
{
	
	private static final String TAG 						= "AppLaunchPreferences";
	public static final String PREFERENCE_APP_FIRST_LAUNCH 	= "is_first_launch";
	
	public static boolean isAppFirstLaunch(SharedPreferences prefs)
	{
		return prefs.getBoolean(PREFERENCE_APP_FIRST_LAUNCH, true);
	}
	
	public static void setAppFirstLaunch(final Editor editor, boolean isAppFisrtLaunch)
	{
		Log.i(TAG, "Setting value of 'is app first launch' in preferences");
		editor.putBoolean(PREFERENCE_APP_FIRST_LAUNCH, isAppFisrtLaunch);
		if (!editor.commit()) 
		{
			Log.d(TAG, "AppLaunchPreferences: setAppFirstLaunch() commit failed");
		}
	}
}
