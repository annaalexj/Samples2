package com.myapps.b.set.viewholders;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.myapps.b.set.R;

public class ExpertLayoutCell extends LinearLayout {
	TextView txtExpertName;	
	LinearLayout lyExpertCell;

	ImageView image;
	public ExpertLayoutCell(Context context) 
	{
		super(context);
		init();
	}
	
	public ExpertLayoutCell(Context context, int defStyle)
	{
		super(context);
		init();
	}
	
	public ExpertLayoutCell(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ExpertLayoutCell(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }
	
	
	public TextView getExpertNameTextView() {
		return txtExpertName;
	}
	
	public LinearLayout getExpertCellLayout() {
		return lyExpertCell;
	}
	
	public void init()
	{
		this.setOrientation(LinearLayout.VERTICAL); 
		LayoutInflater  mInflater 	= (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mInflater.inflate(R.layout.expert_locator_cell, this, true);
		txtExpertName 				= ( TextView) findViewById(R.id.txtExpertName);
		lyExpertCell 				= ( LinearLayout) findViewById(R.id.lyExpertCell);
	}
	
	public void setTextValue(String text)
	{
		txtExpertName.setText(text);
	}
	
	public void makeCellAsSelected()
	{
		lyExpertCell.setBackgroundResource(R.drawable.bg_expert_cell_selected);
	}
	public void makeCellAsUnselected()
	{
		lyExpertCell.setBackgroundResource(R.drawable.bg_expert_cell);
	}
}
