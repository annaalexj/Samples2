package com.myapps.b.set;

import android.app.Application;
import android.content.Context;

public class BaseApplication extends Application
{
	static Context AppContext;
	
	@Override
	public void onCreate()
	{
		super.onCreate();
		AppContext = getApplicationContext();
	}
	public static Context getAppContext()
	{
		return AppContext;
	}
}
