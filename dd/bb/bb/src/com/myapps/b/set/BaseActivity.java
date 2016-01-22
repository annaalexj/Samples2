package com.myapps.b.set;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Window;

import com.myapps.b.set.R;
import com.myapps.b.set.preferences.AppPreferences;
import com.myapps.b.set.utils.AppUtil;
import com.myapps.b.set.utils.Constants;

public class BaseActivity extends ActionBarActivity implements ActionListener {

	private static final String TAG 									= BaseActivity.class.getSimpleName();
	private static boolean APP_ACTIVE 									= false;
	Dialog progress;
	
	protected static ArrayList<String> activitiesNameList 				= new ArrayList<String>();
	protected static HashMap<String, Activity> activitiesStackMap 		= new HashMap<String, Activity>();

	public static final String FINISH_ALL_ACTIVITIES_ACTIVITY_ACTION 	= "FINISH_ALL_ACTIVITIES_ACTIVITY_ACTION";
	private BaseActivityReceiver baseActivityReceiver 					= new BaseActivityReceiver();
	public static final IntentFilter INTENT_FILTER 						= createIntentFilter();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if(getResources().getBoolean(R.bool.portrait_only)){
	        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	    }
		else
		{
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		}

		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.login);
		setProgressBarIndeterminateVisibility(false);
		
	}

	@Override
	public void addListener(Integer index, ActivityListener listener) {
		if (activities.containsValue(listener)) {
			removeListener(listener);
		}
		activities.put(index, listener);

	}

	@Override
	public void addListenerWithRepetition(Integer index, ActivityListener listener) {
		activities.put(index, listener);
	}

	@Override
	public void removeListener(ActivityListener listener) {
		Vector<Integer> rm 			= new Vector<Integer>();
		for (Integer index : activities.keySet()) {
			if (activities.get(index).equals(listener)) {
				rm.add(index);
			}
		}
		for (Integer pos : rm) {
			activities.remove(pos);
		}

	}

	@Override
	public void execute(BaseAppData data) {
		if (isNetworkAvailable()) {
			BaseAppExecutor executor 			= new BaseAppExecutor(data);
			executor.execute();
		} else {

			dismissProgress();
			AppUtil.alertMsg(this, Constants.ALERT_TITLE_ERROR,   getString(R.string.no_network));			
		}

	}

	/**
	 * Function for checking whether network is available or not
	 * 
	 * @return
	 */
	public boolean isNetworkAvailable() {
		Context context 					= getApplicationContext();
		ConnectivityManager connectivity 	= (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity == null) {
			return false;
		} else {
			NetworkInfo[] info 				= connectivity.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		return false;
	}

	@Override
	public void showProgress(String title, String msg)
	{
		if(title.equalsIgnoreCase(null) || title.equalsIgnoreCase(""))
			title 			= Constants.MESSEGE_LOADING;
		if(msg.equalsIgnoreCase(null) || msg.equalsIgnoreCase(""))
			msg 			= Constants.MESSEGE_PLEASE_WAIT;
		progress 			= ProgressDialog.show(this,title ,msg );
	}

	@Override
	public void dismissProgress() {

		if(progress != null)
		{
			if (progress.isShowing())
				progress.dismiss();
		}
	}

	private static IntentFilter createIntentFilter() {
		IntentFilter filter 		= new IntentFilter();
		filter.addAction(FINISH_ALL_ACTIVITIES_ACTIVITY_ACTION);
		return filter;
	}

	protected void registerBaseActivityReceiver() {
		registerReceiver(baseActivityReceiver, INTENT_FILTER);
	}

	protected void unRegisterBaseActivityReceiver() {
		try
		{
			unregisterReceiver(baseActivityReceiver);
		}
		catch(IllegalArgumentException e)
		{
			e.printStackTrace();
			finish();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			finish();
		}
	}

	public class BaseActivityReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(FINISH_ALL_ACTIVITIES_ACTIVITY_ACTION)) {
				finish();
			}
		}
	}

	protected void closeAllActivities() {
		sendBroadcast(new Intent(FINISH_ALL_ACTIVITIES_ACTIVITY_ACTION));
	}

	public void setAppActiveStatus(boolean status)
	{
		this.APP_ACTIVE 		= status;
	}

	public boolean getAppActiveStatus()
	{
		return this.APP_ACTIVE;
	}

	@Override
	protected void onPause()
	{
		// app is in background
		boolean appBackgroundstatus 		= isApplicationBroughtToBackground() ;
		if(appBackgroundstatus)
		{
			//logout
			setAppActiveStatus(false);					
			SharedPreferences prefs 		= PreferenceManager.getDefaultSharedPreferences(this);
			String userName 				= AppPreferences.getUserName(prefs);
			if(!userName.equals(""))
			{
				Log.i("App is in background ", "App is in background ");			
			}			
		}
		super.onPause();
	}

	@Override
	protected void onResume()
	{
		// app is active
		boolean appBackgroundstatus 		= isApplicationBroughtToBackground() ;
		if(!appBackgroundstatus)
		{
			//login
			if(!getAppActiveStatus()) {
				setAppActiveStatus(true);
				SharedPreferences prefs 	= PreferenceManager.getDefaultSharedPreferences(this);
				String userName 			= AppPreferences.getUserName(prefs);
				if(!userName.equals(""))
				{
					Log.i("App is active : login", "App is active : login");				
				}
			}
		}
		super.onResume();
	}

	protected boolean isApplicationBroughtToBackground() {

		ActivityManager am 				= (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> tasks 	= am.getRunningTasks(1);
		if (!tasks.isEmpty())
		{
			ComponentName topActivity 	= tasks.get(0).topActivity;
			if (!topActivity.getPackageName().equals(this.getPackageName()))
			{
				return true;
			}
		}

		return false;
	}
	public boolean isTablet(Context context) 
	{  
		TelephonyManager manager 	= (TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
		if(manager.getPhoneType() == TelephonyManager.PHONE_TYPE_NONE)
		{
		   // return "Tablet";
			return true;
		}
		else
		{
		    //return "Mobile";
			return false;
		}
	
	}

}
