/**
 * SplashActivity for displaying the splash screen at the beginning
 */
package com.myapps.b.set.screens;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;

import com.myapps.b.set.R;
import com.myapps.b.set.preferences.AppPreferences;
import com.myapps.b.set.utils.AppUtil;
import com.myapps.b.set.utils.Constants;

/** 
 * @author 324520
 * @Classname SplashActivity
 * @Description  Called when the activity is first created, it checks whether user is already logged in
 * then redirect to corrersponding screen like Home or Login
 */

public class SplashActivity extends Activity
{

	private int	splashTime	= 0;
	Handler		handler		= new Handler();

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		try
		{
			super.onCreate(savedInstanceState);
			if (getResources().getBoolean(R.bool.portrait_only))
			{
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			}
			else
			{
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			}
			requestWindowFeature(Window.FEATURE_NO_TITLE);

			redirectToLoginActivity();
			finish();
			Runnable splashRunnable 	= new Runnable()
			{
				public void run()
				{
					redirectToLoginActivity();
					finish();
				}
			};

		}
		catch (Exception e)
		{
			AppUtil.alertMsg(this, Constants.ALERT_TITLE_ERROR, Constants.RESPONSE_ERROR_OCCURRED);
			e.printStackTrace();
		}
	}

	/**
	 * On clicking splash screen will be taken to the login page
	 * 
	 * @param v
	 */
	public void onClick(View v)
	{
		redirectToLoginActivity();
	}

	/**
	 * Redirecting to login page
	 */
	private void redirectToLoginActivity()
	{
		SharedPreferences prefs 		= PreferenceManager.getDefaultSharedPreferences(SplashActivity.this);
		if (AppPreferences.getUserName(prefs) == null || AppPreferences.getUserName(prefs).equals(""))
		{
			Intent intent 				= new Intent(this, LoginActivity.class);
			startActivity(intent);
			finish();
		}
		else
		{
			Intent intent 				= new Intent(this, DrawerHome.class);
			startActivity(intent);
			finish();
		}
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
	}
}
