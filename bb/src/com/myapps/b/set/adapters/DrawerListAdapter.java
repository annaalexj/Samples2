package com.myapps.b.set.adapters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.myapps.b.set.R;
import com.myapps.b.set.model.Category;
import com.myapps.b.set.viewholders.ChildViewHolder;
import com.myapps.b.set.viewholders.GroupViewHolder;

public class DrawerListAdapter extends BaseExpandableListAdapter
{

	private Context							context;
	private List<Category>					lstGroup				= new ArrayList<Category>();
	HashMap<String, ArrayList<Category>>	childMap				= new HashMap<String, ArrayList<Category>>();
	int										selectedGroupPosition	= -2;

	public DrawerListAdapter(Context context, ArrayList<Category> argLstGroup, HashMap<String, ArrayList<Category>> argChildMap, int argSelectedGroupPosition)
	{
		this.context 				= context;
		this.lstGroup 				= argLstGroup;
		this.childMap 				= argChildMap;
		this.selectedGroupPosition 	= argSelectedGroupPosition;
	}

	@Override
	public Object getChild(int groupPosition, int childPosititon)
	{
		String key 							= lstGroup.get(groupPosition).getCategoryName();
		ArrayList<Category> tempChildList 	= childMap.get(key);
		Category tempCategory 				= tempChildList.get(childPosititon);

		return tempCategory;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition)
	{
		return childPosition;
	}

	@Override
	public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent)
	{

		View row 					= null;
		ChildViewHolder holder 		= null;
		Category tempCat 			= (Category) getChild(groupPosition, childPosition);
		if (convertView == null)
		{
			holder 							= new ChildViewHolder();
			LayoutInflater infalInflater 	= (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row 							= infalInflater.inflate(R.layout.drawer_xlist_item, null);
			holder.lyChildHeader 			= (LinearLayout) row.findViewById(R.id.lyChildHeader);
			holder.txtChildHeader 			= (TextView) row.findViewById(R.id.txtChildHeader);
			holder.setObjCategory(tempCat);
			row.setTag(holder);
		}
		else
		{
			row 							= convertView;
			holder 							= (ChildViewHolder) row.getTag();
		}

		int selectedBgColor 		= context.getResources().getColor(R.color.xlist_child_bg_sel);
		int selectedTextColor 		= context.getResources().getColor(R.color.xlist_child_text_sel);
		int unselectedBgColor 		= context.getResources().getColor(R.color.xlist_child_bg_unsel);
		int unselectedTextColor 	= context.getResources().getColor(R.color.xlist_child_text_unsel);

		if (tempCat.isSelected())
		{
			holder.lyChildHeader.setBackgroundColor(selectedBgColor);
			holder.txtChildHeader.setTextColor(selectedTextColor);
		}
		else
		{
			holder.lyChildHeader.setBackgroundColor(unselectedBgColor);
			holder.txtChildHeader.setTextColor(unselectedTextColor);
		}

		holder.txtChildHeader.setText(tempCat.getCategoryName());

		return row;
	}

	@Override
	public int getChildrenCount(int groupPosition)
	{
		String key 		= lstGroup.get(groupPosition).getCategoryName();
		return childMap.get(key).size();
	}

	@Override
	public Object getGroup(int groupPosition)
	{
		return this.lstGroup.get(groupPosition);
	}

	@Override
	public int getGroupCount()
	{
		return this.lstGroup.size();
	}

	@Override
	public long getGroupId(int groupPosition)
	{
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent)
	{
		View row 					= null;
		GroupViewHolder holder 		= null;
		Category tempCat 			= (Category) getGroup(groupPosition);
		if (convertView == null)
		{
			holder 							= new GroupViewHolder();
			LayoutInflater infalInflater 	= (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row 							= infalInflater.inflate(R.layout.drawer_xlist_group, null);
			holder.txtGroupHeader 			= (TextView) row.findViewById(R.id.txtGroupHeader);
			holder.lyGroupHeader 			= (LinearLayout) row.findViewById(R.id.lyGroupHeader);
			row.setTag(holder);
		}
		else
		{
			row 			= convertView;
			holder 			= (GroupViewHolder) row.getTag();
		}

		int selectedBgColor 			= context.getResources().getColor(R.color.xlist_group_bg_sel);
		int selectedTextColor 			= context.getResources().getColor(R.color.xlist_group_text_sel);
		int unselectedBgColor 			= context.getResources().getColor(R.color.xlist_group_bg_unsel);
		int unselectedTextColor 		= context.getResources().getColor(R.color.xlist_group_text_unsel);
		{
			
			if (isExpanded)
			{
				holder.lyGroupHeader.setBackgroundColor(selectedBgColor);
				holder.txtGroupHeader.setTextColor(selectedTextColor);
				holder.txtGroupHeader.setTypeface(null, Typeface.BOLD);
			}
			else
			{
				holder.lyGroupHeader.setBackgroundColor(unselectedBgColor);
				holder.txtGroupHeader.setTextColor(unselectedTextColor);
				holder.txtGroupHeader.setTypeface(null, Typeface.NORMAL);
			}
		}
		holder.txtGroupHeader.setText(tempCat.getCategoryName());
		return row;
	}

	@Override
	public boolean hasStableIds()
	{
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition)
	{
		return true;
	}

	public void chaningColors()
	{

	}
}