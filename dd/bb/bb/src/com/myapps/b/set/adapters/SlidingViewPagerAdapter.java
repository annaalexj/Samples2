package com.myapps.b.set.adapters;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.myapps.b.set.R;
import com.myapps.b.set.fragments.DashboardFragment;
import com.myapps.b.set.utils.Constants;

public class SlidingViewPagerAdapter extends PagerAdapter
{
	// Declare Variables
	Context			context;
	String[]		rank;
	String[]		country;
	String[]		population;
	int[]			slides;
	LayoutInflater	inflater;

	public SlidingViewPagerAdapter(Context context, int[] argSlides)
	{
		this.context 		= context;
		slides 				= argSlides;
	}

	@Override
	public int getCount()
	{
		return slides.length;
	}

	@Override
	public boolean isViewFromObject(View view, Object object)
	{
		return view == ((RelativeLayout) object);
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position)
	{
		final int slideNumber 		= position + 1;
		inflater 					= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View slide 					= inflater.inflate(R.layout.viewpager_custom_item, container, false);
		ImageView imgSlide 			= (ImageView) slide.findViewById(R.id.slider);
		imgSlide.setBackgroundResource(slides[position]);

		imgSlide.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if (slideNumber == Constants.WHATS_NEW)
				{

				}
				else if (slideNumber == Constants.EXPERT_LOCATOR)
				{

				}
				else if (slideNumber == Constants.SET_RECOMMENDED)
				{

				}
				else if (slideNumber == Constants.NEWSLETTER)
				{

				}

				Bundle documentBundle 						= new Bundle();
				documentBundle.putInt("slideNumber", slideNumber);
				FragmentManager fragmentManager 			= ((Activity) context).getFragmentManager();
				FragmentTransaction fragmentTransaction 	= fragmentManager.beginTransaction();
				DashboardFragment documentListingFragment 	= new DashboardFragment();
				documentListingFragment.setArguments(documentBundle);
				fragmentTransaction.replace(R.id.content_frame, documentListingFragment, "DocumentListingFragment");
				// if(fragmentManager.getBackStackEntryCount() == 0)
				fragmentTransaction.addToBackStack("DocumentListingFragment");
				fragmentTransaction.commit();

			}
		});
		// Add viewpager_item.xml to ViewPager
		((ViewPager) container).addView(slide);
		return slide;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object)
	{
		((ViewPager) container).removeView((RelativeLayout) object);
	}

}