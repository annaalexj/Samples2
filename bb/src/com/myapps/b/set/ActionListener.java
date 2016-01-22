package com.myapps.b.set;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import android.view.MotionEvent;

public interface ActionListener {
	public static Map<Integer, ActivityListener> activities = new ConcurrentHashMap<Integer, ActivityListener>();
	public void addListener(Integer index, ActivityListener listener);
	public void addListenerWithRepetition(Integer index, ActivityListener listener);
	public void removeListener(ActivityListener listener);
	public void execute(BaseAppData data);
	public void showProgress(String title, String msg);
	public void dismissProgress();
}
