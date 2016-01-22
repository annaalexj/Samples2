package com.myapps.b.set.screens;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.myapps.b.set.R;
import com.myapps.b.set.ActivityListener;
import com.myapps.b.set.BaseActivity;
import com.myapps.b.set.BaseAppData;
import com.myapps.b.set.fragments.DashboardFragment;
import com.myapps.b.set.fragments.DrawerFragment;
import com.myapps.b.set.utils.AppUtil;
import com.myapps.b.set.utils.Constants;

/**
 * @author 324520
 * @Function name : DrawerHome
 * @param
 * @Description : Home page of app. It displays dashboard screen. It is calling 2 fragments,
 * one is for dashboard screen  and other is for drawer.
 */
public class DrawerHome extends BaseActivity implements ActivityListener
{
	private ActionBarDrawerToggle	drawerToggle;
	boolean							shouldGoInvisible		= false;

	DrawerLayout					drawerLayout;
	DashboardFragment				dashboardFragment;
	DrawerFragment					drawerFragment;

	public static String			oppurtunityTagValue		= "";
	LinearLayout					leftDrawer;

	private static final String		TAG						= DrawerHome.class.getSimpleName();
	private static final String		DASHBOARD_FRAGMENT_TAG	= "Dashboard";
	public static int				actionbarHeight			= 0;
	public static String			ACTIONBAR_TITLE			= "Dashboard";

	/**
	 * @author 324520
	 * @Function name : onCreate
	 * @param
	 * @Description : Creating the HomeActivity
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		try
		{
			super.onCreate(savedInstanceState);
			setContentView(R.layout.bfs_main);
			initDrawer(savedInstanceState);
		}
		catch (Exception e)
		{
			AppUtil.alertMsg(DrawerHome.this, Constants.ALERT_TITLE_ERROR, Constants.RESPONSE_ERROR_OCCURRED);
			e.printStackTrace();
		}
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState)
	{
		super.onPostCreate(savedInstanceState);
		drawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig)
	{
		super.onConfigurationChanged(newConfig);
		drawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		if (drawerToggle.onOptionsItemSelected(item))
		{
			return true;
		}

		switch (item.getItemId())
		{
			case R.id.action_search:
				DrawerHome.ACTIONBAR_TITLE 		= Constants.SEARCH_LABEL;
				this.getActionBar().setTitle(Constants.SEARCH_LABEL);
				callingSearchUI();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater inflater 	= getMenuInflater();
		inflater.inflate(R.menu.actionbar_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu)
	{
		boolean drawerOpen 		= shouldGoInvisible;
		hideMenuItems(menu, !drawerOpen);

		return super.onPrepareOptionsMenu(menu);
	}

	/**
	 * @author 324520
	 * @Function name : initDrawer
	 * @param
	 * @Description : Initialize the UI and Drawer
	 */

	private void initDrawer(Bundle savedInstanceState)
	{
		ActionBar actionBar 		= this.getSupportActionBar();

		drawerLayout 				= (DrawerLayout) findViewById(R.id.drawer_layout);
		leftDrawer 					= (LinearLayout) findViewById(R.id.left_drawer);
		drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
		actionBar.setTitle(Constants.DRAWER_CLOSED_TITLE);
		
		drawerToggle 				= new ActionBarDrawerToggle(this, drawerLayout, R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close)
		{
			float	mPreviousOffset	= 0f;
			public void onDrawerClosed(View view)
			{
				super.onDrawerClosed(view);
				shouldGoInvisible 	= false;
				invalidateOptionsMenu();
				getSupportActionBar().setTitle(ACTIONBAR_TITLE);
			}
			public void onDrawerOpened(View drawerView)
			{
				super.onDrawerOpened(drawerView);
				shouldGoInvisible 	= true;
				invalidateOptionsMenu();
				getSupportActionBar().setTitle(Constants.DRAWER_OPENED_TITLE);
			}

			@Override
			public void onDrawerSlide(View arg0, float slideOffset)
			{
				super.onDrawerSlide(arg0, slideOffset);
				if (slideOffset > mPreviousOffset && !shouldGoInvisible)
				{
					shouldGoInvisible 		= true;
					invalidateOptionsMenu();
				}
				else if (mPreviousOffset > slideOffset && slideOffset < 0.5f && shouldGoInvisible)
				{
					shouldGoInvisible 		= false;
					invalidateOptionsMenu();
				}
				mPreviousOffset 			= slideOffset;
			}

			@Override
			public void onDrawerStateChanged(int arg0)
			{
			}
		};
		drawerLayout.setDrawerListener(drawerToggle);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setHomeButtonEnabled(true);
		FragmentManager fragmentManager 			= getFragmentManager();
		FragmentTransaction fragmentTransaction 	= fragmentManager.beginTransaction();
		dashboardFragment 							= new DashboardFragment();
		drawerFragment 								= new DrawerFragment();
		Bundle documentBundle 						= new Bundle();
		documentBundle.putInt("slideNumber", Constants.DASHBOARD);
		documentBundle.putString("categoryName", "");
		documentBundle.putString("subcategoryName", "");
		dashboardFragment.setArguments(documentBundle);

		if (savedInstanceState == null)
		{

			fragmentTransaction.add(R.id.left_drawer, drawerFragment, "DrawerFragment");
			fragmentTransaction.add(R.id.content_frame, dashboardFragment, "DashboardFragment");
			// fragmentTransaction.addToBackStack(null);
			fragmentTransaction.commit();
		}
		TypedValue tv = new TypedValue();
		if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
		{
			actionbarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
		}
	}

	@Override
	public void onFinish(BaseAppData data)
	{
	}

	@Override
	public void onCancel(BaseAppData data)
	{
	}

	private void hideMenuItems(Menu menu, boolean visible)
	{
		for (int i = 0; i < menu.size(); i++)
		{
			menu.getItem(i).setVisible(visible);
		}
	}

	@Override
	public void onBackPressed()
	{
		if (drawerLayout.isDrawerOpen(GravityCompat.START))
		{
			drawerLayout.closeDrawers();
		}
		else
		{
			FragmentManager fm = getFragmentManager();
			if (fm.getBackStackEntryCount() > 0)
			{
				fm.popBackStackImmediate("DashboardFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
				fm.popBackStack();
			}
			else
			{
				super.onBackPressed();
			}
		}
	}

	/**
	 * @author 324520
	 * @Function name : callingSearchUI
	 * @param
	 * @Description : calling search functionality
	 */
	public void callingSearchUI()
	{
		Bundle documentBundle 					= new Bundle();
		documentBundle.putInt("slideNumber", Constants.SEARCH);
		FragmentManager fragmentManager 		= this.getFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		DashboardFragment searchFragment 		= new DashboardFragment();
		searchFragment.setArguments(documentBundle);
		fragmentTransaction.replace(R.id.content_frame, searchFragment, "SearchFragment");
		// if(fragmentManager.getBackStackEntryCount() == 0)
		fragmentTransaction.addToBackStack(null);
		fragmentTransaction.commit();

	}
}
