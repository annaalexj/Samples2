package com.myapps.b.set.adapters;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.myapps.b.set.R;
import com.myapps.b.set.model.Category;

public class InnerListAdapter extends BaseAdapter
{

	private Activity				activity;
	private ArrayList<Category>		lstInnerCats	= new ArrayList<Category>();
	private static LayoutInflater	inflater		= null;
	String							dashboardType	= "";

	public InnerListAdapter(Activity argActivity, ArrayList<Category> argCatList)
	{
		activity 			= argActivity;
		lstInnerCats 		= argCatList;
		inflater 			= (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	private class ViewHolder
	{
		TextView	txtInnerCatName;
		ImageView	imgTick;
	}

	public int getCount()
	{
		return lstInnerCats.size();
	}

	public Object getItem(int index)
	{
		return lstInnerCats.get(index);
	}

	public long getItemId(int arg0)
	{
		return arg0;
	}

	public View getView(int position, View convertView, ViewGroup parent)
	{

		View row 				= null;
		ViewHolder holder 		= null;
		Category cat 			= (Category) getItem(position);
		if (convertView == null)
		{
			holder 					= new ViewHolder();
			row 					= inflater.inflate(R.layout.inner_list_item, null);
			holder.txtInnerCatName 	= (TextView) row.findViewById(R.id.txtInnerCatName);
			holder.imgTick 			= (ImageView) row.findViewById(R.id.imgTick);
			row.setTag(holder);
		}
		else
		{
			row 					= convertView;
			holder 					= (ViewHolder) convertView.getTag();
		}

		String docName 				= "";

		holder.txtInnerCatName.setText(cat.getCategoryName());

		if (cat.isSelected())
		{
			holder.imgTick.setImageResource(R.drawable.ic_action_accept);
		}
		else
		{
			holder.imgTick.setImageResource(0);
		}

		return row;
	}

}
