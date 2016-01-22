package com.myapps.b.set.fragments;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;

import com.myapps.b.set.R;
import com.myapps.b.set.BaseFragment;
import com.myapps.b.set.adapters.DrawerListAdapter;
import com.myapps.b.set.model.Category;
import com.myapps.b.set.parsers.SubCategoryParser;
import com.myapps.b.set.preferences.AppPreferences;
import com.myapps.b.set.screens.LoginActivity;
import com.myapps.b.set.utils.AppUtil;
import com.myapps.b.set.utils.Constants;

public class DrawerFragment extends BaseFragment 
{

	ExpandableListView						xlstDrawer				= null;
	DrawerListAdapter						listAdapter;
	List<String>							listDataHeader;
	HashMap<String, List<String>>			listDataChild;
	View									view_Group;
	ArrayList<Category>						lstMainHeaders			= new ArrayList<Category>();
	HashMap<String, ArrayList<Category>>	childMap				= new HashMap<String, ArrayList<Category>>();
	
	String									fragmentTag				= "";
	ViewGroup								parent;
	int										containerId;
	DrawerLayout							drawerLayout;
	private int								lastExpandedPosition	= -1;
	private int								selectedGroupPosition	= -1;
	private int								selectedChildPosition	= -1;
	int										groupSize				= 0;
	boolean									searchClickStatus		= false;
	boolean									homeClickStatus			= false;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance)
	{
		parent 			= container;
		View view 		= inflater.inflate(R.layout.drawer_list, container, false);

		return view;
	}

	/**
	 * @author 324520
	 * @Function name : onActivityCreated
	 * @param
	 * @Description : initialize drawer UI , adding categories & subcategories to drawer
	 *                and to handle group & child click in drawer
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		try
		{
			super.onActivityCreated(savedInstanceState);

			containerId 				= ((ViewGroup) getView().getParent()).getId();
			drawerLayout 				= (DrawerLayout) getView().findViewById(R.id.drawer_layout);
			xlstDrawer 					= (ExpandableListView) getView().findViewById(R.id.xlstDrawer);
			
			//categories and subcateories are getting from xml file in asset folder
			Category objCategory 		= categoryListParsing();
			lstMainHeaders 				= objCategory.getLstMainCategory();
			ArrayList<Category> lstsub 	= objCategory.getLstSubCategory();
			final ArrayList<Category> lstInnerCategories 	= objCategory.getLstInnerCategory();
			childMap 					= new HashMap<String, ArrayList<Category>>();
			groupSize 					= lstMainHeaders.size();
			
			//assigning subcategories under each category name
			for (Category temp : lstMainHeaders)
			{
				ArrayList<Category> child 	= new ArrayList<Category>();
				String catName 				= temp.getCategoryName();
				String foldername 			= temp.getFolderName();
				int pos 					= 0;
				for (Category argCat : lstsub)
				{
					if (argCat.getSuperTopCategoryName().equalsIgnoreCase(catName))
					{
						argCat.setPosition(pos++);
						argCat.setFolderName(foldername);
						child.add(argCat);
					}
				}
				childMap.put(catName, child);
			}

			//setting to an expandable listview
			listAdapter 			= new DrawerListAdapter(getActivity(), lstMainHeaders, childMap, selectedGroupPosition);
			xlstDrawer.setAdapter(listAdapter);
			xlstDrawer.expandGroup(0);
			lastExpandedPosition 	= 0;
			xlstDrawer.setOnGroupClickListener(new OnGroupClickListener()
			{
				@Override
				public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id)
				{
					searchClickStatus 		= false;
					homeClickStatus 		= false;

					//if clicking home icon
					if (groupPosition == Constants.DASHBOARD)
					{
						// resetting all categories and sub categories
						if (selectedChildPosition != -1 && selectedGroupPosition != -1)
						{
							Category tempLastCat 		= (Category) xlstDrawer.getExpandableListAdapter().getGroup(selectedGroupPosition);
							childMap.get(tempLastCat.getCategoryName()).get(selectedChildPosition).setSelected(false);
							listAdapter.notifyDataSetChanged();
							selectedChildPosition 		= -1;
							selectedGroupPosition 		= -1;
							homeClickStatus 			= true;
						}

						//closing drawers
						DrawerLayout dLayout 					= (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);
						dLayout.closeDrawers();
						
						//calling dashboard screen
						FragmentManager fragmentManager 		= getFragmentManager();
						FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
						DashboardFragment dashboardFragment 	= new DashboardFragment();
						Bundle documentBundle 					= new Bundle();
						documentBundle.putInt("slideNumber", 0);
						documentBundle.putString("categoryName", "");
						documentBundle.putString("subcategoryName", "");
						dashboardFragment.setArguments(documentBundle);
						fragmentTransaction.replace(R.id.content_frame, dashboardFragment, "DashboardFragment");
						fragmentTransaction.commit();

					}

					//if clicks on search icon
					else if (groupPosition == (groupSize - 3))
					{
						if (selectedChildPosition != -1 && selectedGroupPosition != -1)
						{
							Category tempLastCat 		= (Category) xlstDrawer.getExpandableListAdapter().getGroup(selectedGroupPosition);
							childMap.get(tempLastCat.getCategoryName()).get(selectedChildPosition).setSelected(false);
							listAdapter.notifyDataSetChanged();
							selectedChildPosition 		= -1;
							selectedGroupPosition 		= -1;
						}
						searchClickStatus 				= true;
						DrawerLayout dLayout 			= (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);
						dLayout.closeDrawers();
						callingSearchUI();
					}
					
					//if clicks on feedback
					else if (groupPosition == (groupSize - 2))
					{
						if (selectedChildPosition != -1 && selectedGroupPosition != -1)
						{
							Category tempLastCat 		= (Category) xlstDrawer.getExpandableListAdapter().getGroup(selectedGroupPosition);
							childMap.get(tempLastCat.getCategoryName()).get(selectedChildPosition).setSelected(false);
							listAdapter.notifyDataSetChanged();
							selectedChildPosition 		= -1;
							selectedGroupPosition 		= -1;
						}
						DrawerLayout dLayout 			= (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);
						dLayout.closeDrawers();

						AlertDialog.Builder appListingDialog 		= new AlertDialog.Builder(getActivity());
						appListingDialog.setTitle("Your feedback is important to us!");
						appListingDialog.setMessage("Do let us know your feedback @ SETappsupport@c.com");
						appListingDialog.setNeutralButton("Ok", new DialogInterface.OnClickListener()
						{
							@Override
							public void onClick(DialogInterface dialog, int which)
							{
								FragmentManager fragmentManager 			= getFragmentManager();
								FragmentTransaction fragmentTransaction 	= fragmentManager.beginTransaction();
								DashboardFragment dashboardFragment 		= new DashboardFragment();
								Bundle documentBundle 						= new Bundle();
								documentBundle.putInt("slideNumber", 0);
								documentBundle.putString("categoryName", "");
								documentBundle.putString("subcategoryName", "");
								dashboardFragment.setArguments(documentBundle);
								fragmentTransaction.replace(R.id.content_frame, dashboardFragment, "DashboardFragment");
								fragmentTransaction.commit();
							}
						});
						appListingDialog.show();

					}
					
					//if clicks on Signout, removing all stored data from preference
					else if (groupPosition == (groupSize - 1))
					{
						if (selectedChildPosition != -1 && selectedGroupPosition != -1)
						{
							Category tempLastCat 		= (Category) xlstDrawer.getExpandableListAdapter().getGroup(selectedGroupPosition);
							childMap.get(tempLastCat.getCategoryName()).get(selectedChildPosition).setSelected(false);
							listAdapter.notifyDataSetChanged();
							selectedChildPosition 		= -1;
							selectedGroupPosition 		= -1;
						}
						DrawerLayout dLayout 			= (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);
						dLayout.closeDrawers();

						SharedPreferences prefs 		= PreferenceManager.getDefaultSharedPreferences(getActivity());
						Editor editor 					= prefs.edit();
						AppPreferences.saveUserPreferences(editor, "", "");
						AppPreferences.saveEncrytedUserPreferences(editor, "", "", "");
						Intent intentLogin 				= new Intent(getActivity(), LoginActivity.class);
						intentLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						intentLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
						intentLogin.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						startActivity(intentLogin);
						getActivity().finish();
					}

					return false;
				}
			});

			xlstDrawer.setOnGroupExpandListener(new OnGroupExpandListener()
			{
				@Override
				public void onGroupExpand(int groupPosition)
				{
					if (lastExpandedPosition != -1 && groupPosition != lastExpandedPosition)
					{
						xlstDrawer.collapseGroup(lastExpandedPosition);
					}
					lastExpandedPosition = groupPosition;

				}
			});

			xlstDrawer.setOnGroupCollapseListener(new OnGroupCollapseListener()
			{
				@Override
				public void onGroupCollapse(int groupPosition)
				{

				}
			});

			xlstDrawer.setOnChildClickListener(new OnChildClickListener()
			{
				@Override
				public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id)
				{
					//closing drawer
					DrawerLayout dLayout 				= (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);
					dLayout.closeDrawers();
					
					//refreshing expandale listview with newly selected items colors are changing
					if (!(selectedChildPosition == childPosition && selectedGroupPosition == groupPosition))
					{
						Category tempCat 				= (Category) xlstDrawer.getExpandableListAdapter().getGroup(groupPosition);
						childMap.get(tempCat.getCategoryName()).get(childPosition).setSelected(true);
						if (selectedChildPosition != -1 && selectedGroupPosition != -1)
						{
							if (!(selectedChildPosition == childPosition && selectedGroupPosition == groupPosition))
							{
								Category tempLastCat 	= (Category) xlstDrawer.getExpandableListAdapter().getGroup(selectedGroupPosition);
								childMap.get(tempLastCat.getCategoryName()).get(selectedChildPosition).setSelected(false);
							}
						}
						selectedChildPosition 			= childPosition;
						selectedGroupPosition 			= groupPosition;

						listAdapter.notifyDataSetChanged();

						Category objCat 				= (Category) xlstDrawer.getExpandableListAdapter().getChild(groupPosition, childPosition);
						String categoryname 			= objCat.getSuperTopCategoryName();
						String subategoryname 			= objCat.getCategoryName();
						String folderName 				= objCat.getFolderName();

						// fetching inner categories
						ArrayList<Category> innerlist = new ArrayList<Category>();
						for (Category temp : lstInnerCategories)
						{
							if (temp.getSuperTopCategoryName().equalsIgnoreCase(categoryname) && temp.getTopCategoryName().equalsIgnoreCase(subategoryname))
							{
								innerlist.add(temp);
							}
						}

						FragmentManager fragmentManager 		= getFragmentManager();
						FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

						DashboardFragment dashboardFragment 	= new DashboardFragment();
						Bundle documentBundle 					= new Bundle();
						documentBundle.putInt("slideNumber", 5);
						documentBundle.putString("categoryName", categoryname);
						documentBundle.putString("subcategoryName", subategoryname);
						documentBundle.putString("folderName", folderName);
						documentBundle.putParcelableArrayList("innerCategoryList", innerlist);
						dashboardFragment.setArguments(documentBundle);
						fragmentTransaction.replace(R.id.content_frame, dashboardFragment, "CategoryListingFragment");

						// if(fragmentManager.getBackStackEntryCount() == 0)
						fragmentTransaction.addToBackStack("CategoryListingFragment");
						fragmentTransaction.commit();

					}
					return false;
				}
			});

		}
		catch (Exception e)
		{
			AppUtil.alertMsg(getActivity(), Constants.ALERT_TITLE_ERROR, Constants.RESPONSE_ERROR_OCCURRED);
			e.printStackTrace();
		}

	}

	@Override
	public void onResume()
	{
		super.onResume();
	}

	/**
	 * @author 324520
	 * @Function name : categoryListParsing
	 * @param
	 * @Description : To get the category list from the sub_category_list.xml from asset folder
	 */
	private Category categoryListParsing()
	{
		InputStream categorydata 	= null;
		try
		{
			categorydata 			= getActivity().getAssets().open(Constants.CATEGORY_XML_PATH + Constants.CATEGORY_XML_FILE);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		SubCategoryParser parser 	= new SubCategoryParser(categorydata);
		return parser.getMainCategory();

	}

	/**
	 * @author 324520
	 * @Function name : callingSearchUI
	 * @param
	 * @Description : redirecting to search screen (Dashboard fragment)
	 *                when click on search item
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
		fragmentTransaction.addToBackStack(null);
		fragmentTransaction.commit();
	}
}
