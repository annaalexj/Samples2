package com.myapps.b.set.viewholders;

import android.widget.LinearLayout;
import android.widget.TextView;

import com.myapps.b.set.model.Category;

public class ChildViewHolder
{
	public TextView txtChildHeader;
	public LinearLayout  lyChildHeader ;
	public int position = -1;
	private  Category objCategory ;
	public Category getObjCategory()
	{
		return objCategory;
	}
	public void setObjCategory(Category objCategory)
	{
		this.objCategory = objCategory;
	}
}
