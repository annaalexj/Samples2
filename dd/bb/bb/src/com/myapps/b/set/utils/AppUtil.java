package com.myapps.b.set.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

public class AppUtil {
	/**
	 * Function to display a simple alert
	 * @param context
	 * @param title
	 * @param message
	 */
	public static void alertMsg(Context context, String title, String message) {
		new AlertDialog.Builder(context)
		.setTitle(title)
		.setNeutralButton("Ok", new OnClickListener() {			
			@Override
			public void onClick(DialogInterface dialog, int which) {			
			}
		})
		.setMessage(message).show();
	}
	
}
