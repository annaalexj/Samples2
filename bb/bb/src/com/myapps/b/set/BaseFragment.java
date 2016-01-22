package com.myapps.b.set;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.myapps.b.set.R;
import com.myapps.b.set.utils.AppUtil;
import com.myapps.b.set.utils.Constants;

public class BaseFragment extends Fragment implements ActionListener{

	private static final String TAG 									= BaseFragment.class.getSimpleName();
	private static boolean APP_ACTIVE 									= false;
	Dialog progress;

	protected static ArrayList<String> activitiesNameList 				= new ArrayList<String>();
	protected static HashMap<String, Activity> activitiesStackMap 		= new HashMap<String, Activity>();

	public static final String FINISH_ALL_ACTIVITIES_ACTIVITY_ACTION 	= "FINISH_ALL_ACTIVITIES_ACTIVITY_ACTION";

	ViewGroup parent ; 
	int containerId;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance)
	{		
		parent 								= container;		
		View view 							= inflater.inflate(R.layout.bfs_main,container, false);
		return  view;
	}
	

	@Override
	public void onActivityCreated (Bundle savedInstanceState) 
	{
	    super.onActivityCreated(savedInstanceState);
	    containerId 						= ((ViewGroup) getView().getParent()).getId();
	}
	

	/**
	 * Function for checking whether network is available or not
	 * 
	 * @return
	 */
	public boolean isNetworkAvailable() {
		Context context 					= getActivity();
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
			// showProgress();
			Log.i(TAG, "Calling execute function of Base Executor");
			BaseAppExecutor executor = new BaseAppExecutor(data);
			executor.execute();
		} else {

			dismissProgress();
			AppUtil.alertMsg(getActivity(), Constants.ALERT_TITLE_ERROR,   getString(R.string.no_network));
		}

	}


	@Override
	public void showProgress(String title, String msg)
	{
		if(title.equalsIgnoreCase(null) || title.equalsIgnoreCase(""))
			title 			= Constants.MESSEGE_LOADING;
		if(msg.equalsIgnoreCase(null) || msg.equalsIgnoreCase(""))
			msg 			= Constants.MESSEGE_PLEASE_WAIT;
		progress 			= ProgressDialog.show(getActivity(),title ,msg );
	}

	@Override
	public void dismissProgress() {

		if(progress != null)
		{
			if (progress.isShowing())
				progress.dismiss();
		}
	}
}
