package com.myapps.b.set.adapters;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.myapps.b.set.R;
import com.myapps.b.set.model.Expert;

public class ExpertListAdapter extends BaseAdapter
{

	private Activity				activity;
	private ArrayList<Expert>		lstExperts		= new ArrayList<Expert>();
	private static LayoutInflater	inflater		= null;
	String							dashboardType	= "";
	AlertDialog.Builder				errorDialog;

	public ExpertListAdapter(Activity argActivity, ArrayList<Expert> argExpertList, String argdashboardType)
	{
		dashboardType 		= argdashboardType;
		activity 			= argActivity;
		lstExperts 			= argExpertList;
		inflater 			= (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	private class ViewHolder
	{
		TextView	txtFileName;
		TextView	txtDescription;
		ImageView	imgCall;
		TextView	txtId;
		TextView	txtMobile;

	}

	public int getCount()
	{
		return lstExperts.size();
	}

	public Object getItem(int index)
	{
		return lstExperts.get(index);
	}

	public long getItemId(int arg0)
	{
		return arg0;
	}

	public View getView(int position, View convertView, ViewGroup parent)
	{

		View row 					= null;
		ViewHolder holder 			= null;
		Expert expert 				= (Expert) getItem(position);
		final String mobileNumer 	= expert.getMobile();
		if (convertView == null)
		{
			holder 					= new ViewHolder();
			row 					= inflater.inflate(R.layout.expert_list_cell, null);
			holder.txtFileName 		= (TextView) row.findViewById(R.id.txtExpertName);
			holder.txtDescription 	= (TextView) row.findViewById(R.id.txtDescription);
			holder.txtId 			= (TextView) row.findViewById(R.id.txtId);
			holder.imgCall 			= (ImageView) row.findViewById(R.id.imgCall);
			holder.txtMobile 		= (TextView) row.findViewById(R.id.txtMobile);

			row.setTag(holder);
		}
		else
		{
			row 			= convertView;
			holder 			= (ViewHolder) convertView.getTag();
		}

		holder.txtFileName.setText(expert.getExpertName());
		holder.txtDescription.setText(expert.getOnsiteOrOffshore());
		holder.txtId.setText(expert.getEmployeeId());
		if (mobileNumer == null || mobileNumer.equals(""))
		{
			holder.imgCall.setVisibility(View.GONE);
			holder.txtMobile.setVisibility(View.GONE);
		}
		else
		{
			holder.imgCall.setVisibility(View.VISIBLE);
			holder.txtMobile.setVisibility(View.VISIBLE);
			holder.txtMobile.setText(mobileNumer);
		}

		errorDialog 			= new AlertDialog.Builder(activity);
		errorDialog.setTitle("Error");
		errorDialog.setMessage("Phone number is empty");
		errorDialog.setNeutralButton("Ok", null);

		holder.imgCall.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if (mobileNumer != null && !mobileNumer.equals(""))
				{
					call(mobileNumer);
				}
				else
				{
					errorDialog.setMessage("Phone number is empty");
					errorDialog.show();
				}
			}
		});

		return row;
	}

	private void call(final String phone)
	{

		AlertDialog.Builder callDialog 		= new AlertDialog.Builder(activity);

		callDialog.setMessage(phone);
		callDialog.setPositiveButton("Call", new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{

				boolean simstatus 			= checkingSIMCard();
				if (simstatus)
				{
					try
					{
						Intent callIntent 	= new Intent(Intent.ACTION_CALL);
						callIntent.setData(Uri.parse("tel:" + phone));
						activity.startActivity(callIntent);
					}
					catch (ActivityNotFoundException e)
					{
						e.printStackTrace();
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}
				else
				{
					errorDialog.setMessage("No SIM Card installed");
					errorDialog.show();
				}
			}
		});

		callDialog.setNegativeButton("Cancel", null);
		callDialog.show();

	}

	private boolean checkingSIMCard()
	{
		boolean SimStatus 				= true;
		TelephonyManager telMgr 		= (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);
		int simState 					= telMgr.getSimState();
		switch (simState)
		{
			case TelephonyManager.SIM_STATE_ABSENT:
				SimStatus 		= false;
				break;
			case TelephonyManager.SIM_STATE_NETWORK_LOCKED:
				SimStatus 		= false;
				break;
			case TelephonyManager.SIM_STATE_PIN_REQUIRED:
				SimStatus 		= false;
				break;
			case TelephonyManager.SIM_STATE_PUK_REQUIRED:
				SimStatus 		= false;
				break;
			case TelephonyManager.SIM_STATE_READY:
				SimStatus 		= true;
				break;
			case TelephonyManager.SIM_STATE_UNKNOWN:
				SimStatus 		= false;
				break;
		}
		return SimStatus;
	}
}
