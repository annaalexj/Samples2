package com.myapps.b.set.fragments;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.NTCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Html;
import android.text.Spanned;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.artifex.mupdf.MuPDFActivity;
import com.myapps.b.set.R;
import com.myapps.b.set.ActivityListener;
import com.myapps.b.set.BaseAppData;
import com.myapps.b.set.BaseFragment;
import com.myapps.b.set.MySSLSocketFactory;
import com.myapps.b.set.adapters.DocumentsAdapter;
import com.myapps.b.set.adapters.ExpertListAdapter;
import com.myapps.b.set.adapters.InnerListAdapter;
import com.myapps.b.set.adapters.SlidingViewPagerAdapter;
import com.myapps.b.set.communication.HttpRequestConstants;
import com.myapps.b.set.data.RequestData;
import com.myapps.b.set.model.Category;
import com.myapps.b.set.model.Document;
import com.myapps.b.set.model.Expert;
import com.myapps.b.set.model.LoginCredentials;
import com.myapps.b.set.model.UnzippingModel;
import com.myapps.b.set.ntlm.NTLMSchemeFactory;
import com.myapps.b.set.parsers.ExpertLocatorParser;
import com.myapps.b.set.parsers.GetListItemsParser;
import com.myapps.b.set.parsers.SearchParser;
import com.myapps.b.set.parsers.SubCategoryParser;
import com.myapps.b.set.preferences.AppPreferences;
import com.myapps.b.set.screens.DocViewerActivity;
import com.myapps.b.set.screens.DrawerHome;
import com.myapps.b.set.screens.ZipListingActivity;
import com.myapps.b.set.utils.AppUtil;
import com.myapps.b.set.utils.Constants;
import com.myapps.b.set.viewholders.ExpertLayoutCell;

public class DashboardFragment extends BaseFragment implements ActivityListener, OnClickListener, OnItemClickListener, OnPageChangeListener
{
	private ViewGroup					parent;
	private int							containerId;
	private ArrayList<Document>			arlstDocuments					= new ArrayList<Document>();
	private ListView					lstDocuments					= null;
	LinearLayout						lyMarginLayout;
	LinearLayout						lyInnerCategory;
	private static final String			TAG								= DashboardFragment.class.getSimpleName();
	private String						listName						= "";
	private String						viewName						= "";
	private String						query							= "";
	private String						viewFields						= "";
	private String						rowLimit						= "";
	private String						queryOptions					= "";
	private String						webID							= "";
	private String						categoryName					= "";
	private String						subCategoryName					= "";
	private int							slideNumber						= 0;
	public static boolean				backActionStatus				= false;
	TextView							txtInnerCategoryHeader			= null;
	TextView							txtInnerCategoryList			= null;
	ImageView							imgInnerCategory				= null;
	ViewPager							dashboardViewPager;
	PagerAdapter						pagerAdapter;
	int									innerChildIndex					= 0;
	ArrayList<Category>					innerCatList					= null;
	// downloading
	ArrayList<Document>					listDocument					= null;
	EditText							searchEditText;
	TextView							headerTextView;
	TextView							txtTitle;
	TextView							txtDocWarning;
	TextView							emptyMessageTextView;

	ImageView							rightImageView					= null;
	ImageView							sliderImageView					= null;
	ImageView							searchCloseImageView;
	ImageView							refreshImageView;
	ImageView							navigatorImageView;
	ImageView							overlayImageView;

	ListView							navigatorList;
	ListView							list;

	Button								btnDocWarningOk;
	Button								btnDocWarningCancel;

	RelativeLayout						searchLayout;

	File[]								fileList						= null;
	File								sdcardPath						= null;
	OnItemClickListener					docListClickListener			= null;
	OnClickListener						dailogBtnClickListener			= null;
	OnClickListener						dailogCancelBtnClickListener	= null;

	Builder								docWarningDialog				= null;

	String								domainName						= "BFS";
	String								docName;
	String								docType;
	String								filePath;
	private String						offerId							= "";
	private int							offerDetailsStackId;
	String								OfferingId						= "";

	AlertDialog.Builder					errorDialog;

	String								subcategoryName					= "";
	String								folderName						= "";
	File								docFile							= null;
	Uri									fileUri							= null;
	Uri									localFileUri					= null;
	String								fileName;
	File								file;
	AlertDialog.Builder					appListingDialog;
	String								errorMessage					= "error";
	boolean								errorStatus						= false;

	AlertDialog.Builder					feedback1Dialog;
	AlertDialog.Builder					error1Dialog;
	AlertDialog.Builder					emptyFileDialog;

	boolean								serverError						= false;
	boolean								networkError					= false;

	long								len;
	String								localFilePath;
	String								extensionType;
	String								fileNameInMappingFile			= "";
	String								extension						= "";
	String								filenameWithoutExtension		= "";
	long								filesize						= 1;
	String								apkLocalFilePath;
	int									NUM_PAGES						= 0;
	int									currentPage						= 0;
	Handler								handler							= new Handler();
	Runnable							Update							= null;
	PopupWindow							innerListPopup;
	boolean								popupStatus						= false;
	// SearchView txtSearch ;
	EditText							txtSearch;
	LinearLayout						lySearch;
	LinearLayout						lyExpertLocator;
	HorizontalScrollView				lyExpertMain;
	HorizontalScrollView				lyExpertSub;
	LinearLayout						lyExpertLocatorMainContainer;
	LinearLayout						lyExpertLocatorSubContainer;
	LinearLayout						lyInnerCategoryHeader;
	HashMap<String, ArrayList<String>>	mapGroupsNTemas					= new HashMap<String, ArrayList<String>>();
	ArrayList<Expert>					lstExperts						= new ArrayList<Expert>();
	int									groupIndex						= 0;
	int									teamIndex						= 0;
	String								groupName						= "";
	String								teamName						= "";
	int									groupSize						= 0;
	TextView							txtTeamsLabel;
	TextView							txtExpertLabel;
	// int slideNumber = -1;
	String								appFilePath;
	AlertDialog.Builder					errorOccuredDialog;
	File								apkFile							= null;
	Uri									apkFileUri						= null;
	String								apkExtension					= "";
	String								apkFilenameWithoutExtension		= "";
	String								apkFilePath;

	public static String				docPath							= "";
	String								apkFileName;
	File								downloadingAPKfile;
	boolean								innerListStatus					= false;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance)
	{
		parent 			= container;
		View view 		= inflater.inflate(R.layout.home_list, container, false);
		return view;
	}

	/**
	 * @author 324520
	 * @Function name : onActivityCreated
	 * @param
	 * @Description : initialize home UI , adding pageviewr for slider and
	 *              calling webservice for getting listitems
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		try
		{
			super.onActivityCreated(savedInstanceState);
			appListingDialog 				= new AlertDialog.Builder(getActivity());
			containerId 					= ((ViewGroup) getView().getParent()).getId();
			lstDocuments 					= (ListView) getView().findViewById(R.id.lstDocuments);
			dashboardViewPager 				= (ViewPager) getView().findViewById(R.id.dashboardViewPager);
			lyMarginLayout 					= (LinearLayout) getView().findViewById(R.id.lyMarginLayout);
			lyInnerCategory 				= (LinearLayout) getView().findViewById(R.id.lyInnerCategory);
			txtInnerCategoryHeader 			= (TextView) getView().findViewById(R.id.txtInnerCategoryHeader);
			txtInnerCategoryList 			= (TextView) getView().findViewById(R.id.txtInnerCategoryList);
			txtInnerCategoryHeader 			= (TextView) getView().findViewById(R.id.txtInnerCategoryHeader);
			imgInnerCategory 				= (ImageView) getView().findViewById(R.id.imgInnerCategory);
			
			txtTeamsLabel 					= (TextView) getView().findViewById(R.id.txtTeamsLabel);
			txtExpertLabel 					= (TextView) getView().findViewById(R.id.txtExpertLabel);
			
			txtSearch 						= (EditText) getView().findViewById(R.id.txtSearch);
			lySearch 						= (LinearLayout) getView().findViewById(R.id.lySearch);

			lyExpertLocator 				= (LinearLayout) getView().findViewById(R.id.lyExpertLocator);			
			lyExpertMain 					= (HorizontalScrollView) getView().findViewById(R.id.lyExpertMain);
			lyExpertSub 					= (HorizontalScrollView) getView().findViewById(R.id.lyExpertSub);
			lyExpertLocatorMainContainer 	= (LinearLayout) getView().findViewById(R.id.lyExpertLocatorMainContainer);
			lyExpertLocatorSubContainer 	= (LinearLayout) getView().findViewById(R.id.lyExpertLocatorSubContainer);
			lyInnerCategoryHeader 			= (LinearLayout) getView().findViewById(R.id.lyInnerCategoryHeader);

			lyMarginLayout.setVisibility(View.VISIBLE);
			lyInnerCategory.setVisibility(View.GONE);
			lySearch.setVisibility(View.GONE);
			lyExpertLocator.setVisibility(View.GONE);
			
			listDocument 					= new ArrayList<Document>();
			initDocDownloadingViews();
			lstDocuments.setOnItemClickListener(this);
			Bundle docBundle 				= getArguments();
			slideNumber 					= docBundle.getInt("slideNumber");

			try
			{
				categoryName 				= docBundle.getString("categoryName");
				subcategoryName 			= docBundle.getString("subcategoryName");
				folderName 					= docBundle.getString("folderName");
				innerCatList 				= docBundle.getParcelableArrayList("innerCategoryList");
				if (innerCatList != null && innerCatList.size() > 0)
				{
					innerListStatus 		= true;
				}
				else
				{
					innerListStatus 		= false;
				}
			}
			catch (Exception e)
			{
				categoryName 				= "";
				subcategoryName 			= "";
				folderName 					= "";
			}

			//listening click event for innercategory arrows
			getView().setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					if (innerListStatus) 
						imgInnerCategory.setImageResource(R.drawable.ic_action_expand_3);
					else
						imgInnerCategory.setImageResource(R.drawable.ic_action_expand);
					popupStatus 			= false;

				}
			});

			//listening touch event for innercategory arrows
			getView().setOnTouchListener(new OnTouchListener()
			{
				@Override
				public boolean onTouch(View v, MotionEvent event)
				{
					if (innerListStatus) 
						imgInnerCategory.setImageResource(R.drawable.ic_action_expand_3);
					else
						imgInnerCategory.setImageResource(R.drawable.ic_action_expand);
					popupStatus 			= false;
					return false;
				}

			});

			//if its dashboard
			if (slideNumber == 0)
			{
				lyInnerCategory.setVisibility(View.GONE);
				lySearch.setVisibility(View.GONE);
				dashboardViewPager.setVisibility(View.VISIBLE);
				lyMarginLayout.setVisibility(View.VISIBLE);
				lyExpertLocator.setVisibility(View.GONE);
				
				DrawerHome.ACTIONBAR_TITLE 		= Constants.DRAWER_CLOSED_TITLE;
				getActivity().getActionBar().setTitle(Constants.DRAWER_CLOSED_TITLE);
				int[] slides 					= { R.drawable.whats_new, R.drawable.expert_locator, R.drawable.set, R.drawable.news };
				dashboardViewPager.setPageMargin(0);
				pagerAdapter = new SlidingViewPagerAdapter(getActivity(), slides);
				dashboardViewPager.setAdapter(pagerAdapter);
				dashboardViewPager.setOnPageChangeListener(this);

				//for sliding automatically
				Timer swipeTimer 				= new Timer();
				NUM_PAGES 						= slides.length;
				Update = new Runnable()
				{
					public void run()
					{
						if (currentPage == NUM_PAGES)
						{
							currentPage 		= 0;
						}
						dashboardViewPager.setCurrentItem(currentPage++, true);
					}
				};
				swipeTimer 						= new Timer();
				swipeTimer.schedule(new TimerTask()
				{
					@Override
					public void run()
					{
						handler.post(Update);
					}
				}, 100, 4000);

				//calling webservice for document list
				callGetHomeListItemsService(HttpRequestConstants.ACTION_MOBILE_APP);
			}
			
			// search
			else if (slideNumber == Constants.SEARCH)
			{				
				lyInnerCategory.setVisibility(View.GONE);
				lySearch.setVisibility(View.VISIBLE);
				dashboardViewPager.setVisibility(View.GONE);
				lyMarginLayout.setVisibility(View.GONE);
				lyExpertLocator.setVisibility(View.GONE);
	
				//custom search view
				txtSearch.setOnEditorActionListener(new OnEditorActionListener()
				{
					public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
					{
						if (actionId == EditorInfo.IME_ACTION_SEARCH)
						{
							String searchquery 				= txtSearch.getText().toString();					
							if (searchquery != null && !searchquery.equals(""))
							{
								callSearchQueryService(searchquery);
								InputMethodManager imm 		= (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
								imm.hideSoftInputFromWindow(txtSearch.getWindowToken(), 0);
								txtSearch.clearFocus();
							}
							else
							{
								AppUtil.alertMsg(getActivity(), "Alert", "Enter search term");
							}
							return true;
						}
						return false;
					}
				});

				//for clearing text when clicking on delete button
				txtSearch.setOnTouchListener(new OnTouchListener()
				{
					@Override
					public boolean onTouch(View v, MotionEvent event)
					{
						final int DRAWABLE_LEFT 	= 0;
						final int DRAWABLE_TOP 		= 1;
						final int DRAWABLE_RIGHT 	= 2;
						final int DRAWABLE_BOTTOM 	= 3;
						if (event.getAction() == MotionEvent.ACTION_UP)
						{
							if (event.getRawX() >= (txtSearch.getRight() - txtSearch.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width()))
							{
								txtSearch.setText("");
								return true;
							}
						}
						return false;
					}
				});

				DrawerHome.ACTIONBAR_TITLE 			= Constants.SEARCH_LABEL;
				getActivity().getActionBar().setTitle(Constants.SEARCH_LABEL);
			}
			else
			{
				backActionStatus 					= true;
				dashboardViewPager.setVisibility(View.GONE);
				lySearch.setVisibility(View.GONE);
				lyExpertLocator.setVisibility(View.GONE);
				lyInnerCategory.setVisibility(View.GONE);
				switch (slideNumber)
				{
					case Constants.WHATS_NEW:
						DrawerHome.ACTIONBAR_TITLE 	= Constants.LABEL_WHATS_NEW;
						getActivity().getActionBar().setTitle(Constants.LABEL_WHATS_NEW);
						callGetHomeListItemsService(HttpRequestConstants.ACTION_ADMIN_SET_DOCUMENTS);
						break;
					case Constants.EXPERT_LOCATOR:
						callGetHomeListItemsService(HttpRequestConstants.ACTION_EXPERT_LOCATOR);
						break;
					case Constants.SET_RECOMMENDED:
						DrawerHome.ACTIONBAR_TITLE 	= Constants.LABEL_SET_REC;
						getActivity().getActionBar().setTitle(Constants.LABEL_SET_REC);
						callGetHomeListItemsService(HttpRequestConstants.ACTION_ALL_SET_RECOMMANDED);
						break;
					case Constants.NEWSLETTER:
						DrawerHome.ACTIONBAR_TITLE 	= Constants.LABEL_NEWSLETTER;
						getActivity().getActionBar().setTitle(Constants.LABEL_NEWSLETTER);
						callGetHomeListItemsService(HttpRequestConstants.ACTION_NEWSLETTER);
						break;
					case Constants.DRAWER_CATEGORY:
						lyMarginLayout.setVisibility(View.GONE);
						dashboardViewPager.setVisibility(View.GONE);
						lyInnerCategory.setVisibility(View.VISIBLE);
						String innerCatName 		= "";
						
						//if third level category list available,
						//setting title and preparing third level category list
						if (innerCatList != null && innerCatList.size() > 0)
						{
							innerChildIndex 		= 0;

							innerCatName 			= innerCatList.get(innerChildIndex).getCategoryName();
							String cateNames 		= categoryName + " > " + subcategoryName + " > " + innerCatName;

							int listSize 			= innerCatList.size();
							for (int i = 0; i < listSize; i++)
							{
								innerCatList.get(i).setSelected(false);
							}
							innerCatList.get(0).setSelected(true);

							txtInnerCategoryList.setText(cateNames);
							txtInnerCategoryHeader.setText(innerCatName);

							View innerListView 		= getActivity().getLayoutInflater().inflate(R.layout.inner_list, null);
							innerListPopup 			= new PopupWindow(innerListView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,true);

							ListView lstInnerCategories 		= (ListView) innerListView.findViewById(R.id.lstInnerCategories);
							InnerListAdapter innerlistAdp 		= new InnerListAdapter(getActivity(), innerCatList);
							lstInnerCategories.setAdapter(innerlistAdp);
							innerListPopup.setContentView(innerListView);
							innerListPopup.setBackgroundDrawable(new BitmapDrawable());
							innerListPopup.setOutsideTouchable(true);

							lstInnerCategories.setOnItemClickListener(new OnItemClickListener()
							{
								@Override
								public void onItemClick(AdapterView<?> arg0, View arg1, int index, long arg3)
								{
									handlingInnerCategoryItems(innerCatList.get(index));
									int listSize 	= innerCatList.size();
									for (int i = 0; i < listSize; i++)
									{
										innerCatList.get(i).setSelected(false);
									}

									innerCatList.get(index).setSelected(true);
									innerListPopup.dismiss();
									imgInnerCategory.setImageResource(R.drawable.ic_action_expand_3);
									((InnerListAdapter) arg0.getAdapter()).notifyDataSetChanged();
								}
							});

							lstInnerCategories.setSelection(0);
							imgInnerCategory.setImageResource(R.drawable.ic_action_expand_3);
							imgInnerCategory.setVisibility(View.VISIBLE);
							
							//calling webservice for listing documents for third level category
							imgInnerCategory.setOnClickListener(new OnClickListener()
							{
								@Override
								public void onClick(View v)
								{
									popupStatus 		= !popupStatus;
									if (innerListPopup != null && popupStatus == true)
									{
										innerListPopup.showAsDropDown(imgInnerCategory);
										imgInnerCategory.setImageResource(R.drawable.ic_action_collapse_3);
									}
									else
									{
										imgInnerCategory.setImageResource(R.drawable.ic_action_expand_3);
									}

								}
							});

						}
						else
						{
							String cateNames 		= categoryName + " > " + subcategoryName;
							txtInnerCategoryList.setText(cateNames);
							txtInnerCategoryHeader.setText("No categories");
							imgInnerCategory.setImageResource(R.drawable.ic_action_expand);
							lyInnerCategoryHeader.setVisibility(View.GONE);
						}
						if (!(folderName.equals(""))) 
							callGetlistitemsService(folderName, subcategoryName, innerCatName);

						else
							callGetlistitemsService(categoryName, subcategoryName, innerCatName);
						break;
					default:
						break;
				}

			}
		}
		catch (Exception e)
		{
			AppUtil.alertMsg(getActivity(), Constants.ALERT_TITLE_ERROR, Constants.RESPONSE_ERROR_OCCURRED);
			e.printStackTrace();
		}

	}

	/**
	 * @author 324520
	 * @Function name : callGetHomeListItemsService
	 * @param
	 * @Description : webservice for fetching documents
	 */

	private void callGetHomeListItemsService(String requestParam)
	{
		try
		{
			if (requestParam.equalsIgnoreCase(HttpRequestConstants.ACTION_EXPERT_LOCATOR))
			{
				showProgress("Fetching experts", "");
			}
			else
			{
				showProgress("Fetching documents", "");
			}
			SharedPreferences prefs 				= PreferenceManager.getDefaultSharedPreferences(getActivity());
			String username 						= AppPreferences.getUserName(prefs);
			String password 						= AppPreferences.getUserPassword(prefs);

			BaseAppData loginData 					= new BaseAppData();
			RequestData request 					= new RequestData();

			LoginCredentials tempLoginCredentials 	= new LoginCredentials();
			tempLoginCredentials.setUsername(username);
			tempLoginCredentials.setPassword(password);
			tempLoginCredentials.setDomain(Constants.DOMAIN);
			tempLoginCredentials.setLocalHost("");

			// For using callGetlistitems Service
			request.setUrl(Constants.BASE_DATA_URL);
			listName 								= requestParam;

			if (requestParam.equalsIgnoreCase(HttpRequestConstants.ACTION_MOBILE_APP) || requestParam.equalsIgnoreCase(listName))
			{
				addListener(HttpRequestConstants.REQ_MOBILE_APP, this);
				request.setRequestCode(HttpRequestConstants.REQ_MOBILE_APP);
				rowLimit 				= String.valueOf(HttpRequestConstants.ROW_LIMIT);
				queryOptions 			= "<QueryOptions><ViewAttributes Scope=\"RecursiveAll\"/></QueryOptions>";
				String[] params 		= { listName, rowLimit, queryOptions };
				request.setParams(params);
			}
			else
			{
				addListener(HttpRequestConstants.REQ_DASHBOARD, this);
				request.setRequestCode(HttpRequestConstants.REQ_DASHBOARD);
				String[] params 		= { listName };
				request.setParams(params);
			}
			request.setLoginCredentials(tempLoginCredentials);
			loginData.setRequest(request);
			execute(loginData);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * @author 324520
	 * @Function name : callSearchQueryService
	 * @param
	 * @Description : webservice for search service is calling
	 */

	private void callSearchQueryService(String requestParam)
	{
		try
		{
			showProgress("Searching", "");

			SharedPreferences prefs 		= PreferenceManager.getDefaultSharedPreferences(getActivity());
			String username 				= AppPreferences.getUserName(prefs);
			String password 				= AppPreferences.getUserPassword(prefs);

			BaseAppData loginData 			= new BaseAppData();
			RequestData request 			= new RequestData();

			LoginCredentials tempLoginCredentials = new LoginCredentials();
			tempLoginCredentials.setUsername(username);
			tempLoginCredentials.setPassword(password);
			tempLoginCredentials.setDomain(Constants.DOMAIN);
			tempLoginCredentials.setLocalHost("");

			// For using callSearchQueryService 
			request.setUrl(Constants.BASE_SEARCH_URL);
			listName 			= requestParam;
			addListener(HttpRequestConstants.REQ_SEARCH_QUERY, this);
			request.setRequestCode(HttpRequestConstants.REQ_SEARCH_QUERY);
			String[] params 	= { listName };
			request.setParams(params);
			request.setLoginCredentials(tempLoginCredentials);
			loginData.setRequest(request);
			execute(loginData);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void onClick(View arg0)
	{
	}

	@Override
	public void onFinish(BaseAppData data)
	{
		try
		{
			if (data.getResponse().getResponseData() == null)
			{
				this.dismissProgress();
			}
			
			//for drawer category's documents 
			if (data.getRequest().getRequestCode() == HttpRequestConstants.REQ_GETLISTITEMS)
			{
				try
				{
					if (data.getResponse().isSuccess())
					{
						if (!data.getResponse().getResponseData().equals(null))
						{
							String[] reqParams 				= (String[]) data.getRequest().getParams();
							
							/** Parsing the response data
							 * setting documnets to adapter and listing it thorugh listview */

							listDocument.clear();
							DrawerHome.oppurtunityTagValue 	= reqParams[0];
							GetListItemsParser parser 		= new GetListItemsParser(new ByteArrayInputStream(data.getResponse().getResponseData().getBytes()), reqParams[0]);
							listDocument 					= parser.getDocumentList();
							DocumentsAdapter adpDocument 	= new DocumentsAdapter(getActivity(), listDocument, "category_" + reqParams[0]);
							lstDocuments.setAdapter(adpDocument);
							DrawerHome.ACTIONBAR_TITLE 		= Constants.LABEL_DOCUMENTS_LIST;
							getActivity().getActionBar().setTitle(Constants.LABEL_DOCUMENTS_LIST);
							this.dismissProgress();
							if (listDocument == null || listDocument.size() == 0)
							{
								this.dismissProgress();
								AppUtil.alertMsg(getActivity(), Constants.ALERT_TITLE_ALERT, Constants.MESSEGE_NO_DATA);
							}
							else
							{
								this.dismissProgress();
							}
						}
						else
						{
							this.dismissProgress();
							AppUtil.alertMsg(getActivity(), Constants.ALERT_TITLE_ERROR, data.getResponse().getResponseMessage());
						}
					}
					else
					{
						this.dismissProgress();
						AppUtil.alertMsg(getActivity(), Constants.ALERT_TITLE_ERROR, data.getResponse().getResponseMessage());
					}

				}
				catch (Exception e)
				{
					this.dismissProgress();
					AppUtil.alertMsg(getActivity(), Constants.ALERT_TITLE_ERROR, Constants.RESPONSE_ERROR_OCCURRED);
					e.printStackTrace();
				}
			}
			
			//for listing documents for dashboard and slides
			else if (data.getRequest().getRequestCode() == HttpRequestConstants.REQ_DASHBOARD || data.getRequest().getRequestCode() == HttpRequestConstants.REQ_MOBILE_APP)
			{
				try
				{
					if (data.getResponse().isSuccess())
					{
						if (!data.getResponse().getResponseData().equals(null))
						{
							String[] reqParams 					= (String[]) data.getRequest().getParams();
							
							//for expert locator
							if (reqParams[0].equalsIgnoreCase(HttpRequestConstants.ACTION_EXPERT_LOCATOR))
							{

								/** Parsing the response data
								 * setting expert list to adapter and listing it through listview */

								lstExperts.clear();
								ExpertLocatorParser parser 		= new ExpertLocatorParser(new ByteArrayInputStream(data.getResponse().getResponseData().getBytes()));
								lstExperts 						= parser.getExpertList();
								mapGroupsNTemas 				= settingExpertLocatorData(lstExperts);							
								this.dismissProgress();
								if (lstExperts == null || lstExperts.size() == 0)
								{
									AppUtil.alertMsg(getActivity(), Constants.ALERT_TITLE_ALERT, Constants.MESSEGE_NO_DATA);
								}
								else
								{
									initExpertUI(reqParams[0]);
								}
							}
							
							//for other slides
							else
							{
								/** Parsing the response data
								 * setting documnets to adapter and listing it thorugh listview */

								listDocument.clear();
								DrawerHome.oppurtunityTagValue 		= reqParams[0];
								GetListItemsParser parser 			= new GetListItemsParser(new ByteArrayInputStream(data.getResponse().getResponseData().getBytes()), reqParams[0]);
								listDocument 						= parser.getDocumentList();
								DocumentsAdapter adpDocument 		= new DocumentsAdapter(getActivity(), listDocument, reqParams[0]);
								lstDocuments.setAdapter(adpDocument);
								this.dismissProgress();
								if (listDocument == null || listDocument.size() == 0)
								{
									AppUtil.alertMsg(getActivity(), Constants.ALERT_TITLE_ALERT, Constants.MESSEGE_NO_DATA);
								}
							}
						}
						else
						{
							this.dismissProgress();
							AppUtil.alertMsg(getActivity(), Constants.ALERT_TITLE_ERROR, data.getResponse().getResponseMessage());
						}
					}
					else
					{
						this.dismissProgress();
						AppUtil.alertMsg(getActivity(), Constants.ALERT_TITLE_ERROR, data.getResponse().getResponseMessage());
					}

				}
				catch (Exception e)
				{
					this.dismissProgress();
					AppUtil.alertMsg(getActivity(), Constants.ALERT_TITLE_ERROR, Constants.RESPONSE_ERROR_OCCURRED);
					e.printStackTrace();
				}
			}
			
			//for search response
			else if (data.getRequest().getRequestCode() == HttpRequestConstants.REQ_SEARCH_QUERY)
			{
				try
				{
					if (data.getResponse().isSuccess())
					{
						if (!data.getResponse().getResponseData().equals(null))
						{							
							String[] reqParams 			= (String[]) data.getRequest().getParams();
							
							/** Parsing the response data
							 * setting documnets to adapter and listing it thorugh listview */

							listDocument.clear();
							SearchParser parser 		= new SearchParser(new ByteArrayInputStream(data.getResponse().getResponseData().getBytes()));
							listDocument 				= parser.getDocumentList();
							DocumentsAdapter adpDocument = new DocumentsAdapter(getActivity(), listDocument, Constants.SEARCH_LABEL);
							lstDocuments.setAdapter(adpDocument);
							DrawerHome.ACTIONBAR_TITLE 	= Constants.SEARCH_LABEL;
							getActivity().getActionBar().setTitle(Constants.SEARCH_LABEL);
							this.dismissProgress();
							if (listDocument == null || listDocument.size() == 0)
							{
								AppUtil.alertMsg(getActivity(), Constants.SEARCH_NO_DATA_TITLE, Constants.SEARCH_NO_DATA_MESSAGE);
							}
						}
						else
						{
							this.dismissProgress();
							AppUtil.alertMsg(getActivity(), Constants.ALERT_TITLE_ERROR, data.getResponse().getResponseMessage());
						}
					}
					else
					{
						this.dismissProgress();
						AppUtil.alertMsg(getActivity(), Constants.ALERT_TITLE_ERROR, data.getResponse().getResponseMessage());
					}
				}
				catch (Exception e)
				{
					this.dismissProgress();
					AppUtil.alertMsg(getActivity(), Constants.ALERT_TITLE_ERROR, Constants.RESPONSE_ERROR_OCCURRED);
					e.printStackTrace();
				}
			}
			else
			{
				this.dismissProgress();
				AppUtil.alertMsg(getActivity(), Constants.ALERT_TITLE_ERROR, data.getResponse().getResponseMessage());
			}

		}
		catch (Exception e)
		{
			this.dismissProgress();
			AppUtil.alertMsg(getActivity(), Constants.ALERT_TITLE_ERROR, Constants.RESPONSE_ERROR_OCCURRED);
			e.printStackTrace();
		}
	}

	@Override
	public void onCancel(BaseAppData data)
	{

	}
	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
	}
	@Override
	public void onDetach()
	{
		super.onDetach();
	}
	/**
	 * @author 324520
	 * @Function name : handlingInnerCategoryItems
	 * @param
	 * @Description : calling webservice for callGetlistitemsService (for third category docs) is calling
	 */
	private void handlingInnerCategoryItems(Category innerCat)
	{
		String innerCatName 		= innerCat.getCategoryName();
		String cateNames 			= categoryName + " > " + subcategoryName + " > " + innerCatName;
		txtInnerCategoryList.setText(cateNames);
		txtInnerCategoryHeader.setText(innerCatName);

		if (!(folderName.equals(""))) 
			callGetlistitemsService(folderName, subcategoryName, innerCatName);		
		else
			callGetlistitemsService(categoryName, subcategoryName, innerCatName);
	}

	/**
	 * @author 324520
	 * @Function name : callGetInnerCategoryListItemsService
	 * @param
	 * @Description : webservice for callGetlistitemsService (for subcategory docs and
	 *              innercategory docs) is calling
	 */

	private void callGetlistitemsService(String catName, String subCatName, String innerCatName)
	{
		try
		{
			showProgress("Fetching documents", "");

			SharedPreferences prefs 				= PreferenceManager.getDefaultSharedPreferences(getActivity());
			String username 						= AppPreferences.getUserName(prefs);
			String password 						= AppPreferences.getUserPassword(prefs);

			BaseAppData loginData 					= new BaseAppData();
			RequestData request 					= new RequestData();

			LoginCredentials tempLoginCredentials 	= new LoginCredentials();

			tempLoginCredentials.setUsername(username);
			tempLoginCredentials.setPassword(password);
			tempLoginCredentials.setDomain(Constants.DOMAIN);
			tempLoginCredentials.setLocalHost("");

			// For using callGetlistitems Service
			addListener(HttpRequestConstants.REQ_GETLISTITEMS, this);
			request.setUrl(Constants.BASE_DATA_URL);
			request.setRequestCode(HttpRequestConstants.REQ_GETLISTITEMS);

			innerCatName 	= innerCatName.replace("/", " or ");
			innerCatName 	= innerCatName.replace("  or  ", " or ");
			innerCatName 	= innerCatName.replace("&", " and ");
			innerCatName 	= innerCatName.replace("  and  ", " and ");
			catName 		= catName.replace("/", " or ");
			catName 		= catName.replace("  or  ", " or ");
			catName 		= catName.replace("&", " and ");
			catName 		= catName.replace("  and  ", " and ");
			subCatName 		= subCatName.replace("/", " or ");
			subCatName 		= subCatName.replace("  or  ", " or ");
			subCatName 		= subCatName.replace("&", " and ");
			subCatName 		= subCatName.replace("  and  ", " and ");

			categoryName 	= catName;
			subCategoryName = subCatName;
			listName 		= categoryName;
			viewName 		= "";

			//setting query according to category name
			if (innerCatName.equals(""))
			{
				query 		= "<Query><Where><Eq><FieldRef Name='Asset_x0020_Type'/><Value Type='Text'>" + subCategoryName + "</Value></Eq></Where></Query>";
			}
			else
			{
				if (catName.equalsIgnoreCase("SGO"))
				{
					query 	= "<Query><Where><And><Eq><FieldRef Name='Asset_x0020_Type'/><Value Type='Text'>" + subCategoryName
							+ "</Value></Eq><Eq><FieldRef Name='Asset_x0020_Sub_x0020_Type'/><Value Type='Text'>" + innerCatName + "</Value></Eq></And></Where></Query>";
				}
				else if (catName.equalsIgnoreCase("Case Studies"))
				{
					query 	= "<Query><Where><And><Eq><FieldRef Name='Asset_x0020_Type'/><Value Type='Text'>" + subCategoryName
							+ "</Value></Eq><Eq><FieldRef Name='Sub_x0020_Vertical'/><Value Type='Text'>" + innerCatName + "</Value></Eq></And></Where></Query>";
				}
			}

			if (catName.equalsIgnoreCase("AVM"))
			{
				if (subCategoryName.equalsIgnoreCase("AVM CoE Content"))
				{
					listName 		= "AVM Links";
					query 			= "<Query><Where><Eq><FieldRef Name='Folder'/><Value Type='Text'>" + innerCatName + "</Value></Eq></Where></Query>";
				}
				else
				{
					listName 		= subCategoryName;
					innerCatName 	= "Application Maintenance";
					query 			= "<Query><Where><Eq><FieldRef Name='Asset_x0020_Type'/><Value Type='Text'>" + innerCatName + "</Value></Eq></Where></Query>";
				}
			}

			if (subCategoryName.equalsIgnoreCase("SET Recommended"))
			{					
				query 				= "";
				listName 			= HttpRequestConstants.ACTION_ALL_SET_RECOMMANDED;	
			}

			if (subCategoryName.equalsIgnoreCase("Useful Links"))
			{
				query 				= "";
				listName 			= "Sitelinks";
			}

			viewFields 				= "";
			rowLimit 				= String.valueOf(HttpRequestConstants.ROW_LIMIT);
			queryOptions 			= "<QueryOptions><ViewAttributes Scope=\"RecursiveAll\"/></QueryOptions>";
			webID 					= "";

			String[] params 		= { listName, viewName, query, viewFields, rowLimit, queryOptions, webID };
			request.setParams(params);
			request.setLoginCredentials(tempLoginCredentials);
			loginData.setRequest(request);
			execute(loginData);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	
	/**
	 * @author 324520
	 * @Function name : onItemClick
	 * @param
	 * @Description : onclicking a document, if network available dowcument will be downloaded and displayed.
	 * once a document is downloaded, next time, it wil be automatically displayed.
	 * App will hold only 5 documents at a time
	 */
	@Override
	public void onItemClick(AdapterView<?> listview, View row, int index, long id)
	{
		if (listDocument != null && listDocument.size() > 0)
		{
				TextView txtFileName 	= (TextView) row.findViewById(R.id.txtFileName);
				docName 				= txtFileName.getText().toString();
				Document doc 			= listDocument.get(index);
				String type 			= doc.getFiletype();
				String assetName 		= doc.getAssetname();
				String linkFileName 	= doc.getLinkFilename();
				docType 				= doc.getFiletype();
				filePath 				= doc.getFilePath(docType);
				String fileSizeInString = doc.getFileSize();
				
				//Convert the bytes to MB
				if (fileSizeInString != null && !fileSizeInString.equals(""))
				{
					long fileSizeInBytes 	= Long.parseLong(fileSizeInString);
					// Convert the bytes to Kilobytes (1 KB = 1024 Bytes)
					long fileSizeInKB 		= fileSizeInBytes / 1024;
					// Convert the KB to MegaBytes (1 MB = 1024 KBytes)
					long fileSizeInMB 		= fileSizeInKB / 1024;
					filesize 				= fileSizeInMB;
				}

				if (type != null && !type.equals(""))
				{
					if (type.equalsIgnoreCase("DOCX") || type.equalsIgnoreCase("DOC") || type.equalsIgnoreCase("POT") || type.equalsIgnoreCase("POTX")
							|| type.equalsIgnoreCase("PPTX") || type.equalsIgnoreCase("PPT") || type.equalsIgnoreCase("XLSX") || type.equalsIgnoreCase("XLS")
							|| type.equalsIgnoreCase("MP4") || type.equalsIgnoreCase("3GP") || type.equalsIgnoreCase("JPG") || type.equalsIgnoreCase("GIF")
							|| type.equalsIgnoreCase("PNG") || type.equalsIgnoreCase("PDF") || type.equalsIgnoreCase("YOUTUBE") || type.equalsIgnoreCase(Constants.SEARCH_LABEL)
							|| type.equalsIgnoreCase("ZIP"))
					{
							if (type.equalsIgnoreCase(Constants.SEARCH_LABEL))
							{
								String ext 	= gettingFileExtension(filePath);
								docType 	= ext;
							}
							downloadOrDisplayDocument();						
					}
					
					//IF FILE is a webpage, showing it in browser
					else if (type.equalsIgnoreCase("web"))
					{
						
						if (!isNetworkAvailable())
						{
							AppUtil.alertMsg(getActivity(), getString(R.string.offline_label), getString(R.string.offline_message));
						}
						else
						{
							try
							{
								Intent browserIntent 	= new Intent(Intent.ACTION_VIEW, Uri.parse(filePath));
								startActivity(browserIntent);
							}
							catch (Exception e)
							{
								appListingDialog.setTitle("Error");
								appListingDialog.setMessage("Error Occured");
								appListingDialog.setNeutralButton("Ok", new DialogInterface.OnClickListener()
								{
									@Override
									public void onClick(DialogInterface dialog, int which)
									{
									}
								});
								appListingDialog.show();
							}
						}		
					}
					else
					{
						errorDialog.show();
					}
				}
				else
				{
					errorDialog.show();
				}
		}
	}

	/**
	 * @author 324520
	 * @Function name : initDocDownloadingViews
	 * @param
	 * @Description : 	Initializing alert messages
	 */
	private void initDocDownloadingViews()
	{
		errorDialog 		= new AlertDialog.Builder(getActivity());
		errorDialog.setTitle("Can't read this type of document in this device");
		errorDialog.setMessage("Log into web application to view the document");
		errorDialog.setNeutralButton("Ok", null);

		docWarningDialog 	= new AlertDialog.Builder(getActivity());
		docWarningDialog.setTitle("File Size");

		docWarningDialog.setPositiveButton("Download", new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				if (isNetworkAvailable())
				{
					// downloading file
					DownloadFile downloadFile = new DownloadFile();
					downloadFile.execute(filePath);
				}
				else
				{
					appListingDialog.setTitle(getString(R.string.offline_label));
					appListingDialog.setMessage(getString(R.string.offline_message));
					appListingDialog.setNeutralButton("Ok", new DialogInterface.OnClickListener()
					{
						@Override
						public void onClick(DialogInterface dialog, int which)
						{
						}
					});
					appListingDialog.show();
				}
			}
		});
		docWarningDialog.setNegativeButton("Cancel", null);

	}
	
	/**
	 * @author 324520
	 * @Function name : downloadOrDisplayDocument
	 * @param
	 * @Description : 	downloading file
	 */
	public void downloadOrDisplayDocument()
	{
		try
		{
			if (docType.equalsIgnoreCase(Constants.SEARCH_LABEL))
			{
				String ext 		= gettingFileExtension(filePath);
				docType 		= ext;
			}
			// creating a file object with the  file path
			docFile 			= new File(filePath);
			fileUri 			= Uri.fromFile(docFile);

			// alert dialog
			appListingDialog 	= new AlertDialog.Builder(getActivity());
			appListingDialog.setTitle("Complete action using");
			appListingDialog.setMessage("No applications can perform this action");
			appListingDialog.setNeutralButton("Ok", new DialogInterface.OnClickListener()
			{
				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					
				}
			});

			error1Dialog 		= new AlertDialog.Builder(getActivity());
			error1Dialog.setTitle("Error");
			error1Dialog.setMessage("Unable to download document. Please try again");
			error1Dialog.setNeutralButton("Ok", null);

			feedback1Dialog 	= new AlertDialog.Builder(getActivity());
			feedback1Dialog.setTitle("Error");
			feedback1Dialog.setMessage("Unable to download document. Please try again");
			feedback1Dialog.setNegativeButton("Cancel", null);

			fileName 					= gettingFileName(filePath);
			extension 					= gettingFileExtension(fileName);
			filenameWithoutExtension 	= gettingEncodedFileName(fileName);
			try
			{
				File appFolder 		= getActivity().getFilesDir();
				file 				= new File(appFolder, fileName);
				/**
				 * checks internet connection if connection exists, download
				 * new file, otherwise show already downloaded file
				 */
				if (!file.exists())
				{
					if (filesize > 1)
					{
						docWarningDialog.setMessage("File size you are trying to download is " + filesize + " MB. Do you wish to download?");
						docWarningDialog.show();
					}
					else
					{
						if (isNetworkAvailable())
						{
							// downloading file
							DownloadFile downloadFile = new DownloadFile();
							downloadFile.execute(filePath);
						}
						else
						{
							appListingDialog.setTitle(getString(R.string.offline_label));
							appListingDialog.setMessage(getString(R.string.offline_message));
							appListingDialog.setNeutralButton("Ok", new DialogInterface.OnClickListener()
							{
								@Override
								public void onClick(DialogInterface dialog, int which)
								{
								}
							});
							appListingDialog.show();
						}
					}
				}
				else
				{
					displayFile();
				}			
			}
			catch (Exception e)
			{
				e.printStackTrace();
				appListingDialog.setTitle("Error");
				appListingDialog.setMessage("Unable to download document. Please try again");
				appListingDialog.setNeutralButton("Ok", new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
					}
				});
				appListingDialog.show();
			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
			Toast.makeText(getActivity(), "Error occured", Toast.LENGTH_SHORT);
		}
	}

	/**
	 * @author 324520
	 * @Function name : gettingFileName
	 * @param
	 * @Description : 	getting FileName
	 */
	public String gettingFileName(String url)
	{
		String fileName 		= "";
		int slashIndex 			= url.lastIndexOf("/");
		fileName 				= url.substring(slashIndex + 1);
		return fileName;
	}

	/**
	 * @author 324520
	 * @Function name : displayFile
	 * @Description : 	display File
	 */
	public void displayFile()
	{
		boolean fileStatus 		= false;
		if (file.exists())
		{
			long fileSize 		= file.length();
			if (!(fileSize > 0))
			{
				// if file is empty, delete it
				boolean delete 	= file.delete();
				fileStatus 		= false;
			}
			else
			{
				fileStatus 		= true;
			}
		}
		if (!fileStatus)
		{
			// no network to download
			String no_network 	= getResources().getString(R.string.no_network);
			emptyFileDialog 	= new AlertDialog.Builder(getActivity());
			emptyFileDialog.setTitle("Error");
			emptyFileDialog.setMessage(no_network);
			emptyFileDialog.setNeutralButton("Ok", null);
			emptyFileDialog.show();
		}
		else
		{
			displayDocument(file);
		}
	}

	/**
	 * @author 324520
	 * @Class name : DownloadFile
	 * @Description : 	Async task for downloading file from server.
	 * A progress dialog will be displayed to show the progress in percentage
	 */
	private class DownloadFile extends AsyncTask<String, Integer, String>
	{
		ProgressDialog	mProgressDialog		= null;

		@Override
		protected String doInBackground(String... sUrl)
		{
			try
			{
				downloadFileFromUrl(getActivity());
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			return null;
		}

		public void downloadFileFromUrl(Activity act)
		{
			HttpClient httpclient 			= getNewHttpClient();
			try
			{
				String filePathUrl 			= filePath;
				if (filePathUrl != null && (!filePathUrl.equalsIgnoreCase("")))
				{
					int slashIndex 			= filePathUrl.lastIndexOf("/");
					String urlDomain 		= filePathUrl.substring(0, slashIndex + 1);
					String fileName1 		= filePathUrl.substring(slashIndex + 1);

					//getting internal folder within the app to save files
					File appFolder 			= getActivity().getFilesDir();
					boolean success 		= false;
					if (!appFolder.exists())
					{
						success 			= appFolder.mkdir();
						if (!success)
						{
						}
						else
						{
							file 			= new File(appFolder, fileName1);
						}
					}
					else
					{
						file 				= new File(appFolder, fileName1);
					}

					if (file.exists())
					{
						file.delete();

					}
					String type 			= getType(fileName1);
					if (type.equalsIgnoreCase("ZIP") || type.equalsIgnoreCase("RAR"))
					{
						String unzipfoldername 	= gettingParentDirectoryName(fileName1);
						File folder 			= new File(appFolder, unzipfoldername);
						if (folder.exists())
						{
							deleteDirectory(folder);	
						}
					}

					file.createNewFile();
					localFilePath 			= file.getPath();

					SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
					String userName 		= AppPreferences.getUserName(prefs);
					String password 		= AppPreferences.getUserPassword(prefs);
					Spanned docspannedUrl 	= Html.fromHtml(urlDomain);
					String docUrlFormatted 	= docspannedUrl.toString().replace(" ", "%20");
					String encodeFileName 	= fileName1;
					try
					{
						encodeFileName 		= URLEncoder.encode(fileName1, "UTF-8");
						encodeFileName 		= encodeFileName.toString().replace("+", "%20");
					}
					catch (UnsupportedEncodingException e)
					{
						e.printStackTrace();
						encodeFileName 		= fileName1;
					}
					String urlEncoded 		= docUrlFormatted + encodeFileName;

					HttpGet downloadDocGet 	= new HttpGet(urlEncoded);

					((DefaultHttpClient) httpclient).getAuthSchemes().register("ntlm", new NTLMSchemeFactory());
					((DefaultHttpClient) httpclient).getCredentialsProvider().setCredentials(new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT),
							new NTCredentials(userName, password, "", Constants.DOMAIN));

					HttpResponse response 	= httpclient.execute(downloadDocGet);
					HttpEntity entity 		= response.getEntity();
					fileNameInMappingFile 	= encodeFileName;
					if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
					{
						if (entity != null)
						{
							InputStream is 	= entity.getContent();
							len 			= entity.getContentLength();
							int current 	= 0;
							int total 		= 0;

							FileOutputStream fos 	= getActivity().openFileOutput(fileName, Context.MODE_WORLD_READABLE);
							byte data[] 			= new byte[1024];
							while ((current = is.read(data)) != -1)
							{
								total 		+= current;

								float p1 	= ((float) total / (float) len);
								p1 			= p1 * 100;
								int p 		= (int) p1;
								publishProgress(p);
								fos.write(data, 0, current);
							}

							fos.close();

							long fileSize 	= file.length();
							if (!(fileSize > 0))
							{
								boolean delete 	= file.delete();
								errorStatus 	= true;
							}
							else if (fileSize < len)
							{
								boolean delete 	= file.delete();
								errorStatus 	= true;
							}

							else
							{
								//file downloading complete	
							}
						}
					}
					else
					{
						boolean delete 	= file.delete();
						serverError 	= true;
						if (response.getStatusLine().getStatusCode() == 404)
						{
							errorStatus 	= true;
							errorMessage 	= "File not found on server. \nHTTP error code : " + response.getStatusLine().getStatusCode();
						}
						else if (response.getStatusLine().getStatusCode() == 400)
						{
							errorStatus 	= true;
							errorMessage 	= "Bad Request. \nHTTP error code : " + response.getStatusLine().getStatusCode();
						}
						else if (response.getStatusLine().getStatusCode() == 403)
						{
							errorStatus 	= true;
							errorMessage 	= "Access is denied. \nHTTP error code : " + response.getStatusLine().getStatusCode();
						}
						else if (response.getStatusLine().getStatusCode() == 500)
						{
							errorStatus 	= true;
							errorMessage 	= "Internal Server Error. \nHTTP error code : " + response.getStatusLine().getStatusCode();
						}
						else if (response.getStatusLine().getStatusCode() == 503)
						{
							errorStatus 	= true;
							errorMessage 	= "Service unavailable. \nHTTP error code : " + response.getStatusLine().getStatusCode();
						}
						else
						{
							errorStatus 	= true;
							errorMessage 	= response.getStatusLine().getReasonPhrase() + " \nHTTP error code : " + response.getStatusLine().getStatusCode();
						}
					}
				}
			}

			catch (UnknownHostException e)
			{
				errorStatus 		= true;
				errorMessage 		= "Exception - UnknownHostException :\n" + e.getMessage();
				e.printStackTrace();
				serverError 		= true;
			}
			catch (ConnectTimeoutException e)
			{
				errorStatus 		= true;
				networkError 		= true;
				errorMessage 		= "Exception - ConnectTimeoutException :\n" + e.getMessage();
				e.printStackTrace();
			}
			catch (ClientProtocolException e)
			{
				errorStatus 		= true;
				serverError 		= true;
				errorMessage 		= "Exception - ClientProtocolException :\n" + e.getMessage();
				e.printStackTrace();
			}
			catch (IOException e)
			{
				networkError 		= true;
				errorStatus 		= true;
				errorMessage 		= "Exception - IOException :\n" + e.getMessage();
				e.printStackTrace();
			}
			catch (IllegalStateException e)
			{
				errorStatus 		= true;
				serverError 		= true;
				errorMessage 		= "Exception - IllegalStateException :\n" + e.getMessage();
				e.printStackTrace();
			}

			catch (Exception e)
			{
				errorStatus = true;
				if (e.getMessage().equalsIgnoreCase("No peer certificate"))
				{
					networkError 	= true;
				}
				else
				{
					serverError 	= true;
				}
				errorMessage 		= "Exception :\n" + e.getMessage();
				e.printStackTrace();
			}

			finally
			{
				long fileSize 		= file.length();
				if (!(fileSize > 0))
				{
					boolean delete 	= file.delete();
					errorStatus 	= true;
				}

				else if (fileSize < len)
				{
					boolean delete 	= file.delete();
					errorStatus 	= true;
				}
				else
				{
					//file downloading complete
				}

				httpclient.getConnectionManager().shutdown();
			}
		}

		@Override
		protected void onPreExecute()
		{
			super.onPreExecute();

			mProgressDialog = new ProgressDialog(getActivity());
			mProgressDialog.setTitle("Downloading file");
			mProgressDialog.setMessage("" + fileName);
			mProgressDialog.setCancelable(false);
			mProgressDialog.setCanceledOnTouchOutside(false);
			mProgressDialog.setIndeterminate(false);
			mProgressDialog.setMax(100);
			mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

			mProgressDialog.show();
		}

		@Override
		protected void onProgressUpdate(Integer... progress)
		{
			super.onProgressUpdate(progress);
			mProgressDialog.setProgress(progress[0]);
		}

		@Override
		protected void onPostExecute(String s)
		{
			mProgressDialog.dismiss();
			if (errorStatus)
			{
				appListingDialog.setTitle("Error");
				appListingDialog.setNeutralButton("Ok", new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
					}
				});

				appListingDialog.setMessage("Unable to download document. Please try again");
				appListingDialog.show();
			}
			else
			{
				checkingAndDeletingOlderFiles();
				displayDocument(file);
			}
			super.onPostExecute(s);
		}

	}

	/**
	 * @author 324520
	 * @Function name : displayDocument
	 * @Description : 	display document
	 */
	public void displayDocument(File file)
	{
		localFilePath 		= gettingLocalFilePath(fileName);
		localFileUri 		= Uri.fromFile(file);
		// if file type is image, video, or web then shows file
		if (docType.equalsIgnoreCase("MP4") || docType.equalsIgnoreCase("3GP") || docType.equalsIgnoreCase("JPG") || docType.equalsIgnoreCase("PNG")
				|| docType.equalsIgnoreCase("GIF") || docType.equalsIgnoreCase("web"))
		{
			boolean networkStatus = true;
			// if file type is web then shows file in system browser
			if (docType.equalsIgnoreCase("web"))
			{
				if (!isNetworkAvailable())
				{
					networkStatus 		= false;
					appListingDialog.setTitle(getString(R.string.offline_label));
					appListingDialog.setMessage(getString(R.string.offline_message));

					appListingDialog.setNeutralButton("Ok", new DialogInterface.OnClickListener()
					{
						@Override
						public void onClick(DialogInterface dialog, int which)
						{							
						}
					});
					appListingDialog.show();
				}
				else
				{
					try
					{
						Intent browserIntent 	= new Intent(Intent.ACTION_VIEW, Uri.parse(filePath));
						startActivity(browserIntent);
					}
					catch (Exception e)
					{
						appListingDialog.setTitle("Error");
						appListingDialog.setMessage("Error Occured");
						appListingDialog.setNeutralButton("Ok", new DialogInterface.OnClickListener()
						{
							@Override
							public void onClick(DialogInterface dialog, int which)
							{
							}
						});

						appListingDialog.show();
					}

				}
			}
			// if file type is image or video,  then calls to next activity, and display the file there
			else
			{
				if (networkStatus)
				{
					Bundle documentBundle 	= new Bundle();
					documentBundle.putString("localFilePath", localFilePath);
					documentBundle.putString("type", docType);
					documentBundle.putString("docName", fileName);
					documentBundle.putString("url", filePath);

					Intent i 				= new Intent(getActivity(), DocViewerActivity.class);

					i.putExtra("filename", fileName);
					if (slideNumber == Constants.SEARCH)
					{
						i.putExtra("backButtonText", "Search");
					}
					else if (slideNumber == Constants.DASHBOARD && !(getResources().getBoolean(R.bool.portrait_only)))
					{
						i.putExtra("backButtonText", Constants.DRAWER_CLOSED_TITLE);
					}
					else if (slideNumber == Constants.WHATS_NEW && !(getResources().getBoolean(R.bool.portrait_only)))
					{
						i.putExtra("backButtonText", Constants.LABEL_WHATS_NEW);
					}
					else if (slideNumber == Constants.EXPERT_LOCATOR && !(getResources().getBoolean(R.bool.portrait_only)))
					{
						i.putExtra("backButtonText", Constants.LABEL_EXPERT_LOCATOR);
					}
					else if (slideNumber == Constants.SET_RECOMMENDED && !(getResources().getBoolean(R.bool.portrait_only)))
					{
						i.putExtra("backButtonText", Constants.LABEL_SET_REC);
					}
					else if (slideNumber == Constants.NEWSLETTER && !(getResources().getBoolean(R.bool.portrait_only)))
					{
						i.putExtra("backButtonText", Constants.LABEL_NEWSLETTER);
					}
					else
					{
						i.putExtra("backButtonText", "Back");
					}
					i.putExtra("viewer", documentBundle);
					startActivity(i);					
				}
			}
		}

		else
		{
			DefaultDocOpening();
		}
	}
	
	
	/**
	 * @author 324520
	 * @Function name : DefaultDocOpening
	 * @Description : display file if it is pdf, or office files
	 */
	public void DefaultDocOpening()
	{
		boolean docAvailStatus 		= true;

		if (checkingDocumentOnSdcard())
		{
			//pdf files are showing using MUPDF library
			if (docType.equalsIgnoreCase("PDF"))
			{
				Intent intent 		= new Intent(getActivity(), MuPDFActivity.class);
				intent.putExtra("filename", fileName);
				if (slideNumber == Constants.SEARCH)
				{
					intent.putExtra("backButtonText", "Search");
				}
				else if (slideNumber == Constants.DASHBOARD && !(getResources().getBoolean(R.bool.portrait_only)))
				{
					intent.putExtra("backButtonText", Constants.DRAWER_CLOSED_TITLE);
				}
				else if (slideNumber == Constants.WHATS_NEW && !(getResources().getBoolean(R.bool.portrait_only)))
				{
					intent.putExtra("backButtonText", Constants.LABEL_WHATS_NEW);
				}
				else if (slideNumber == Constants.EXPERT_LOCATOR && !(getResources().getBoolean(R.bool.portrait_only)))
				{
					intent.putExtra("backButtonText", Constants.LABEL_EXPERT_LOCATOR);
				}
				else if (slideNumber == Constants.SET_RECOMMENDED && !(getResources().getBoolean(R.bool.portrait_only)))
				{
					intent.putExtra("backButtonText", Constants.LABEL_SET_REC);
				}
				else if (slideNumber == Constants.NEWSLETTER && !(getResources().getBoolean(R.bool.portrait_only)))
				{
					intent.putExtra("backButtonText", Constants.LABEL_NEWSLETTER);
				}
				else
				{
					intent.putExtra("backButtonText", "Back");
				}
				intent.setAction(Intent.ACTION_VIEW);
				intent.setData(localFileUri);
				startActivity(intent);
			}
			
			//if files are microsoft files
			else if ((docType.equalsIgnoreCase("PPTX") || docType.equalsIgnoreCase("PPT") || docType.equalsIgnoreCase("POT") || docType.equalsIgnoreCase("POTX")
					|| docType.equalsIgnoreCase("DOCX") || docType.equalsIgnoreCase("DOC") || docType.equalsIgnoreCase("XLSX") || docType.equalsIgnoreCase("XLS")))
			{

				if (docType.equalsIgnoreCase("PPTX") || docType.equalsIgnoreCase("PPT") || docType.equalsIgnoreCase("POT") || docType.equalsIgnoreCase("POTX"))
				{
					extensionType 		= "application/vnd.ms-powerpoint";
				}
				else if (docType.equalsIgnoreCase("DOCX") || docType.equalsIgnoreCase("DOC"))
				{
					extensionType 		= "application/msword";
				}
				else if (docType.equalsIgnoreCase("XLSX") || docType.equalsIgnoreCase("XLS"))
				{
					extensionType 		= "application/vnd.ms-excel";
				}

				//try to open document using one default third party app
				Intent docViewIntent 	= new Intent();
				docViewIntent.setAction(Intent.ACTION_VIEW);
				docViewIntent.setPackage(Constants.DOC_VIEWER_APP_PACKAGE);
				docViewIntent.setDataAndType(localFileUri, extensionType);
				docViewIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				try
				{
					startActivity(docViewIntent);
				}
				catch (ActivityNotFoundException e)
				{
					e.printStackTrace();
					try
					{
						//if that app is not installed in device,
						//then tries to open document using any document viewer which is installed in device
						Intent docViewIntent1 	= new Intent();
						docViewIntent1.setAction(Intent.ACTION_VIEW);
						docViewIntent1.setDataAndType(localFileUri, extensionType);
						docViewIntent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						startActivity(docViewIntent1);
					}
					catch (ActivityNotFoundException e1)
					{

						//if no document viewer is not installed in device, then tries to install 
						//a document viewer using Google Paly Store app
						boolean googlePlayStatus 					= isAppInstalled("com.android.vending");
						if (googlePlayStatus)
						{
							// show dialog to redirect to google play store
							AlertDialog.Builder appRedirectDialog 	= new AlertDialog.Builder(getActivity());
							appRedirectDialog.setTitle("Alert");
							appRedirectDialog.setMessage("There is no application can perform this action. Would you like to download " + Constants.DOC_VIEWER_APP_NAME
									+ " from Google Play store?");
							appRedirectDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener()
							{
								@Override
								public void onClick(DialogInterface dialog, int which)
								{
									final String appPackageName 	= Constants.DOC_VIEWER_APP_PACKAGE; 
									try
									{
										startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
									}
									catch (android.content.ActivityNotFoundException noAppEx)
									{		
										noAppEx.printStackTrace();
									}
								}
							});
							appRedirectDialog.setNegativeButton("Cancel", null);
							appRedirectDialog.show();
						}
						//if Google Play store app is not installed in device,
						//the BFS app will redirect to a link , which download and install a document viewer
						else
						{
							AlertDialog.Builder appRedirectDialog1 		= new AlertDialog.Builder(getActivity());
							appRedirectDialog1.setTitle("Alert");
							appRedirectDialog1.setMessage("There is no application can perform this action. Would you like to download " + Constants.THIRD_PARTY_APP_NAME + " ?");
							appRedirectDialog1.setPositiveButton("Ok", new DialogInterface.OnClickListener()
							{
								@Override
								public void onClick(DialogInterface dialog, int which)
								{
									downloadDocumentViewer();
								}
							});
							appRedirectDialog1.setNegativeButton("Cancel", null);
							appRedirectDialog1.show();
						}
					}
					catch (Exception e2)
					{

						AlertDialog.Builder appRedirectDialog1 			= new AlertDialog.Builder(getActivity());
						appRedirectDialog1.setTitle("Alert");
						appRedirectDialog1.setMessage("There is no application can perform this action. Would you like to download " + Constants.THIRD_PARTY_APP_NAME + " ?");
						appRedirectDialog1.setPositiveButton("Ok", new DialogInterface.OnClickListener()
						{
							@Override
							public void onClick(DialogInterface dialog, int which)
							{
								downloadDocumentViewer();
							}
						});
						appRedirectDialog1.setNegativeButton("Cancel", null);
						appRedirectDialog1.show();
					}
				}
			}
			
			//if file is zip file, then unzip the file to a folder which is having the same file name
			//this folder is stored in app's internal folder
			else if (docType.equalsIgnoreCase("ZIP") || docType.equalsIgnoreCase("RAR"))
			{
				// unzippimg
				String parentDirectoryName 		= gettingParentDirectoryName(fileName);

				File appFolder 					= getActivity().getFilesDir();
				File directory 					= new File(appFolder, parentDirectoryName);

				//if already unzipped, then redirect to another activity, which lists zip contents
				//if zip contents contains again a zip file, then the zip file will also be unzipped
				if (directory.exists())
				{
					String filePathOnSdcard 	= directory.getPath();
					Intent i 					= new Intent(getActivity(), ZipListingActivity.class);
					i.putExtra("zipfolderPath", filePathOnSdcard);
					i.putExtra("zipRootFilePath", localFilePath);
					i.putExtra("filename", fileName);
					
					if (slideNumber == Constants.SEARCH)
					{
						i.putExtra("backButtonText", "Search");
					}
					else if (slideNumber == Constants.DASHBOARD && !(getResources().getBoolean(R.bool.portrait_only)))
					{
						i.putExtra("backButtonText", Constants.DRAWER_CLOSED_TITLE);
					}
					else if (slideNumber == Constants.WHATS_NEW && !(getResources().getBoolean(R.bool.portrait_only)))
					{
						i.putExtra("backButtonText", Constants.LABEL_WHATS_NEW);
					}
					else if (slideNumber == Constants.EXPERT_LOCATOR && !(getResources().getBoolean(R.bool.portrait_only)))
					{
						i.putExtra("backButtonText", Constants.LABEL_EXPERT_LOCATOR);
					}
					else if (slideNumber == Constants.SET_RECOMMENDED && !(getResources().getBoolean(R.bool.portrait_only)))
					{
						i.putExtra("backButtonText", Constants.LABEL_SET_REC);
					}
					else if (slideNumber == Constants.NEWSLETTER && !(getResources().getBoolean(R.bool.portrait_only)))
					{
						i.putExtra("backButtonText", Constants.LABEL_NEWSLETTER);
					}
					else
					{
						i.putExtra("backButtonText", "Back");
					}
					startActivity(i);
				}
				else
				{
					// unzipping file functionality
					String parentFolderPath 		= creatingParentDirectory(localFilePath);
					UnzippingModel unzip 			= new UnzippingModel(getActivity(), parentFolderPath, localFilePath, fileName, localFilePath);
					unzip.unzippingFile();
				}

			}
			else
			{if (docType.equalsIgnoreCase("MP4") || docType.equalsIgnoreCase("3GP"))
				{
					extensionType 		= "video/*";
				}
				else if (docType.equalsIgnoreCase("JPG") || docType.equalsIgnoreCase("PNG") || docType.equalsIgnoreCase("GIF"))
				{
					extensionType 		= "image/*";
				}
				else if (docType.equalsIgnoreCase("PDF"))
				{
					extensionType 		= "application/pdf";
				}
				else
				{

					appListingDialog 	= new AlertDialog.Builder(getActivity());
					appListingDialog.setTitle("Error");
					appListingDialog.setMessage("There is no application can perform this action.");
					appListingDialog.setNeutralButton("Ok", null);

					appListingDialog.show();
					appListingDialog.show();
					docAvailStatus 		= false;
				}
			    // file is opening using default applications
				if (docAvailStatus)
				{
					Intent docViewIntent 	= new Intent();
					docViewIntent.setAction(Intent.ACTION_VIEW);
					docViewIntent.setDataAndType(localFileUri, extensionType);
					docViewIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					try
					{
						startActivity(docViewIntent);
					}
					catch (ActivityNotFoundException e)
					{
						appListingDialog.show();					
					}
					catch (Exception e)
					{
						appListingDialog.setTitle("Document Error");
						appListingDialog.setMessage("File cannot be opened");
						appListingDialog.show();
					}
				}
			}
		}
	}

	/**
	 * @author 324520
	 * @Function name : gettingLocalFilePath
	 * @Description : getting Local File Path from the file name
	 */
	public String gettingLocalFilePath(String argfileName)
	{
		String filePathOnSdcard 		= "";
		File appFolder 					= getActivity().getFilesDir();
		File file 						= new File(appFolder, fileName);

		if (file.exists())
		{
			filePathOnSdcard 			= file.getPath();
		}
		return filePathOnSdcard;
	}
	
	/**
	 * @author 324520
	 * @Function name : checkingDocumentOnSdcard
	 * @Description : checking Document is already exist On the app
	 */
	public boolean checkingDocumentOnSdcard()
	{
		boolean status 			= false;
		fileName 				= gettingFileName(filePath);
		File appFolder 			= getActivity().getFilesDir();
		File file 				= new File(appFolder, fileName);
		status 					= file.exists();
		return status;
	}
	
	/**
	 * @author 324520
	 * @Function name : gettingFileExtension
	 * @Description : getting File Extension
	 */
	public String gettingFileExtension(String url)
	{
		String extension 		= "";
		int slashIndex 			= url.lastIndexOf(".");
		extension 				= url.substring(slashIndex + 1);
		return extension;
	}
	
	/**
	 * @author 324520
	 * @Function name : gettingEncodedFileName
	 * @Description : getting Encoded FileName
	 */
	public String gettingEncodedFileName(String filefullname)
	{
		String fileName 		= "";
		if (filefullname.contains("."))
		{
			int slashIndex 		= filefullname.lastIndexOf(".");
			fileName 			= filefullname.substring(0, slashIndex);
		}
		else
		{
			fileName 			= filefullname;
		}
		String encodeFileName 	= "";
		try
		{
			encodeFileName 		= URLEncoder.encode(fileName, "UTF-8");
			encodeFileName 		= encodeFileName.toString().replace("+", "%20");
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
			encodeFileName 		= fileName;
		}
		return encodeFileName;
	}

	/**
	 * @author 324520
	 * @Function name : getNewHttpClient
	 * @Description : get HttpClient object which handles ssl handshake error, timeout etc
	 */
	public HttpClient getNewHttpClient()
	{
		try
		{
			KeyStore trustStore 		= KeyStore.getInstance(KeyStore.getDefaultType());
			trustStore.load(null, null);

			SSLSocketFactory sf 		= new MySSLSocketFactory(trustStore);
			sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

			HttpParams params 			= new BasicHttpParams();
			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
			int timeoutConnection 		= 30000;
			HttpConnectionParams.setConnectionTimeout(params, timeoutConnection);
			int timeoutSocket 			= 45000;
			HttpConnectionParams.setSoTimeout(params, timeoutSocket);
			SchemeRegistry registry 	= new SchemeRegistry();
			registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
			registry.register(new Scheme("https", sf, 443));

			ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

			return new DefaultHttpClient(ccm, params);
		}
		catch (Exception e)
		{
			return new DefaultHttpClient();
		}
	}

	/**
	 * @author 324520
	 * @Function name : getType
	 * @Description : get file type from file name
	 */
	private String getType(String name)
	{
		String extension 	= "";
		if (name.contains("."))
		{
			int index 		= name.lastIndexOf(".");
			extension 		= name.substring(index + 1);
		}
		return extension;
	}
	/**
	 * @author 324520
	 * @Function name : checkingAndDeletingOlderFiles
	 * @Description : app stores only 5 documents. This function checks the number of documents in app folder
	 * and delete the files from older to newer if till document number reaches 5.
	 * If deleting file is a zip file, it will delete zip folder also
	 * 
	 * For showing microsoft documents using external app,
	 * this document will be moved to app internal folder root directory.
	 * So that file will also be deletd. Its name is already saved in preference
	 */
	public void checkingAndDeletingOlderFiles()
	{

		File appFolder 			= getActivity().getFilesDir();
		File[] lstFiles11 		= appFolder.listFiles();
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
		String temp 			= prefs.getString("tempFilename", "temp");
		Editor edt 				= prefs.edit();
		if (temp == null || temp.equals("") || temp.equalsIgnoreCase("temp"))
		{

		}
		else
		{
			File tempFileold 	= new File(appFolder, temp);
			if (tempFileold.exists()) tempFileold.delete();
			edt.remove("tempFilename");
			edt.commit();
		}
		File[] lstFiles12 		= appFolder.listFiles();
		if (appFolder.exists())
		{
			File[] lstFiles 						= appFolder.listFiles();
			ArrayList<File> lstFolderFiles 			= new ArrayList<File>();
			for (File file : lstFiles)
			{
				if (!file.isDirectory()) lstFolderFiles.add(file);
			}
			Collections.sort(lstFolderFiles, new Comparator<File>()
			{
				public int compare(File o1, File o2)
				{
					long result 			= o2.lastModified() - o1.lastModified();
					if (result > 0)
					{
						return 1;
					}
					else if (result < 0)
					{
						return -1;
					}
					else
					{
						return 0;
					}
				}
			});
			int length 					= lstFolderFiles.size();
			if (length > 5)
			{
				for (int i = length; i > 5; i--)
				{
					File f 				= lstFolderFiles.get(i - 1);
					String type 		= getType(f.getName());
					if (type.equalsIgnoreCase("ZIP") || type.equalsIgnoreCase("RAR"))
					{
						// deleting folder
						String unzipfoldername 		= gettingParentDirectoryName(f.getName());
						File folder 				= new File(appFolder, unzipfoldername);
						if (folder.exists()) deleteDirectory(folder);
					}
					lstFolderFiles.get(i - 1).delete();
				}
			}
		}
	}

	@Override
	public void onPageScrollStateChanged(int arg0)
	{

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2)
	{

	}

	/**
	 * @author 324520
	 * @Function name : onPageSelected
	 * @Description : for automatically sliding banners.
	 * If number reaches to last one, then it will set to 0. 
	 */
	@Override
	public void onPageSelected(int index)
	{
		currentPage 		= index++;
		if (currentPage == NUM_PAGES) 
			currentPage 	= 0;
	}

	@Override
	public void onPause()
	{
		super.onPause();
		
		//dismissing third level category list when pausing the screen
		if (innerListPopup != null) 
			innerListPopup.dismiss();
		if (innerListStatus) 
			imgInnerCategory.setImageResource(R.drawable.ic_action_expand_3);
		else
			imgInnerCategory.setImageResource(R.drawable.ic_action_expand);
	}

	/**
	 * @author 324520
	 * @Function name : categoryListParsing
	 * @Description : To get the category list from the folder : assets/www/sub_category_list.xml
	 */
	
	private Category categoryListParsing()
	{
		InputStream i 		= null;
		try
		{
			i 				= getActivity().getAssets().open(Constants.CATEGORY_XML_PATH + Constants.CATEGORY_XML_FILE);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		SubCategoryParser parser 		= new SubCategoryParser(i);
		return parser.getMainCategory();

	}

	@Override
	public void onResume()
	{
		switch (slideNumber)
		{
			case Constants.DASHBOARD:
				lyInnerCategory.setVisibility(View.GONE);
				lySearch.setVisibility(View.GONE);
				dashboardViewPager.setVisibility(View.VISIBLE);
				lyMarginLayout.setVisibility(View.VISIBLE);
				lyExpertLocator.setVisibility(View.GONE);
				DrawerHome.ACTIONBAR_TITLE 		= Constants.DRAWER_CLOSED_TITLE;
				getActivity().getActionBar().setTitle(Constants.DRAWER_CLOSED_TITLE);
				break;

			case Constants.WHATS_NEW:
				DrawerHome.ACTIONBAR_TITLE 		= Constants.LABEL_WHATS_NEW;
				getActivity().getActionBar().setTitle(Constants.LABEL_WHATS_NEW);
				dashboardViewPager.setVisibility(View.GONE);
				lySearch.setVisibility(View.GONE);
				lyExpertLocator.setVisibility(View.GONE);
				lyInnerCategory.setVisibility(View.GONE);
				break;
				
			case Constants.EXPERT_LOCATOR:
				lyExpertLocator.setVisibility(View.VISIBLE);
				lySearch.setVisibility(View.GONE);
				dashboardViewPager.setVisibility(View.GONE);
				lyMarginLayout.setVisibility(View.GONE);
				lyInnerCategory.setVisibility(View.GONE);
				DrawerHome.ACTIONBAR_TITLE 		= Constants.LABEL_EXPERT_LOCATOR;
				getActivity().getActionBar().setTitle(Constants.LABEL_EXPERT_LOCATOR);
				break;
				
			case Constants.SET_RECOMMENDED:
				DrawerHome.ACTIONBAR_TITLE 		= Constants.LABEL_SET_REC;
				getActivity().getActionBar().setTitle(Constants.LABEL_SET_REC);
				dashboardViewPager.setVisibility(View.GONE);
				lySearch.setVisibility(View.GONE);
				lyExpertLocator.setVisibility(View.GONE);
				lyInnerCategory.setVisibility(View.GONE);
				break;
				
			case Constants.NEWSLETTER:
				DrawerHome.ACTIONBAR_TITLE 		= Constants.LABEL_NEWSLETTER;
				getActivity().getActionBar().setTitle(Constants.LABEL_NEWSLETTER);
				dashboardViewPager.setVisibility(View.GONE);
				lySearch.setVisibility(View.GONE);
				lyExpertLocator.setVisibility(View.GONE);
				lyInnerCategory.setVisibility(View.GONE);
				break;
				
			case Constants.DRAWER_CATEGORY:
				getActivity().getActionBar().setTitle(Constants.LABEL_DOCUMENTS_LIST);
				DrawerHome.ACTIONBAR_TITLE 		= Constants.LABEL_DOCUMENTS_LIST;
				lyExpertLocator.setVisibility(View.GONE);
				lyMarginLayout.setVisibility(View.GONE);
				dashboardViewPager.setVisibility(View.GONE);
				lyInnerCategory.setVisibility(View.VISIBLE);
				break;
				
			case Constants.SEARCH:
				lyInnerCategory.setVisibility(View.GONE);
				lySearch.setVisibility(View.VISIBLE);
				dashboardViewPager.setVisibility(View.GONE);
				lyMarginLayout.setVisibility(View.GONE);
				lyExpertLocator.setVisibility(View.GONE);
				DrawerHome.ACTIONBAR_TITLE 		= Constants.SEARCH_LABEL;
				getActivity().getActionBar().setTitle(Constants.SEARCH_LABEL);
				break;
		}
		super.onResume();
	}

	/**
	 * @author 324520
	 * @Function name : settingExpertLocatorData
	 * @Description : getting gropus and teams
	 */
	public HashMap<String, ArrayList<String>> settingExpertLocatorData(ArrayList<Expert> argExpertList)
	{
		// getting groups
		ArrayList<String> groupList 		= new ArrayList<String>();
		for (Expert temp : argExpertList)
		{
			String groupName 				= temp.getGroupName();
			if (!(groupList.contains(groupName)))
			{
				groupList.add(groupName);
			}
		}
		Collections.sort(groupList);
		
		// getting teams
		HashMap<String, ArrayList<String>> mapTeams 	= new HashMap<String, ArrayList<String>>();
		for (String tempGroupName : groupList)
		{
			ArrayList<String> tempTeamList 				= new ArrayList<String>();
			for (Expert temp : argExpertList)
			{
				String groupName 						= temp.getGroupName();
				if (groupName.equalsIgnoreCase(tempGroupName))
				{
					String teamName 					= temp.getTeamName();
					if (!(tempTeamList.contains(teamName)))
					{
						tempTeamList.add(teamName);
					}
				}
			}
			Collections.sort(tempTeamList);
			mapTeams.put(tempGroupName, tempTeamList);
		}
		return mapTeams;
	}
	
	/**
	 * @author 324520
	 * @Function name : initExpertUI
	 * @Description : initializing ExpertLocator UI
	 * Assigning each group with its team on the onclick
	 */
	private void initExpertUI(final String requestParam)
	{
		lyExpertLocator.setVisibility(View.VISIBLE);
		lySearch.setVisibility(View.GONE);
		dashboardViewPager.setVisibility(View.GONE);
		lyMarginLayout.setVisibility(View.GONE);
		lyInnerCategory.setVisibility(View.GONE);
		DrawerHome.ACTIONBAR_TITLE 			= Constants.LABEL_EXPERT_LOCATOR;
		getActivity().getActionBar().setTitle(Constants.LABEL_EXPERT_LOCATOR);
		txtTeamsLabel.setVisibility(View.VISIBLE);
		
		groupSize 							= mapGroupsNTemas.size();		
		Set<String> groupnameSet 			= mapGroupsNTemas.keySet();
		ArrayList<String> groupnamelist 	= new ArrayList<String>(groupnameSet);
		Collections.sort(groupnamelist);

		int index 							= 0;
		for (String tempGroupName : groupnamelist)
		{
			ExpertLayoutCell expertCellLayout 		= new ExpertLayoutCell(getActivity());
			expertCellLayout.setTextValue(tempGroupName);

			final int finalIndex 					= index;
			final String finalgroupName 			= tempGroupName;
			expertCellLayout.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					txtTeamsLabel.setVisibility(View.VISIBLE);
					groupIndex 						= finalIndex;
					groupName 						= finalgroupName;
					settingSelectedColorToGroupCell(finalIndex);
					settingTeams(groupIndex, groupName, requestParam);
				}
			});

			lyExpertLocatorMainContainer.addView(expertCellLayout);

			if (index == 0)
			{
				groupIndex 				= 0;
				groupName 				= tempGroupName;
				teamName 				= mapGroupsNTemas.get(groupName).get(0);
			}
			index++;

		}

		final ExpertLayoutCell expertCellLayout 		= new ExpertLayoutCell(getActivity());
		expertCellLayout.setTextValue("All");
		expertCellLayout.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				settingSelectedColorToGroupCell(groupSize);
				txtTeamsLabel.setVisibility(View.GONE);
				lyExpertLocatorSubContainer.removeAllViews();
				ExpertListAdapter adpExpert 			= new ExpertListAdapter(getActivity(), lstExperts, requestParam);
				lstDocuments.setAdapter(adpExpert);
				txtExpertLabel.setText("All");
			}
		});
		lyExpertLocatorMainContainer.addView(expertCellLayout);

		settingSelectedColorToGroupCell(groupIndex);
		settingSelectedColorToTeamCell(0);
		settingTeams(groupIndex, groupName, requestParam);
		settingExpertLocatorList(0, teamName, groupName, requestParam);
		txtExpertLabel.setText(groupName + " - " + teamName);
	}

	/**
	 * @author 324520
	 * @Function name : settingTeams
	 * @Description : setting Teams and its click
	 */
	public void settingTeams(int groupindex, final String groupName, final String requestParam)
	{
		lyExpertLocatorSubContainer.removeAllViews();
		txtTeamsLabel.setVisibility(View.VISIBLE);
		ArrayList<String> teamList 		= mapGroupsNTemas.get(groupName);
		final int teamSize 				= teamList.size();

		int index 						= 0;
		for (String tempteamName : teamList)
		{
			ExpertLayoutCell expertCellLayout 		= new ExpertLayoutCell(getActivity());
			expertCellLayout.setTextValue(tempteamName);

			final int finalIndex 					= index;
			final String finalteamName 				= tempteamName;
			expertCellLayout.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					settingSelectedColorToTeamCell(finalIndex);
					teamIndex 				= finalIndex;
					teamName 				= finalteamName;
					settingExpertLocatorList(teamIndex, teamName, groupName, requestParam);
				}
			});

			lyExpertLocatorSubContainer.addView(expertCellLayout);

			if (index == 0)
			{
				teamName 					= tempteamName;
			}
			index++;
		}

		ExpertLayoutCell expertCellLayout 		= new ExpertLayoutCell(getActivity());
		expertCellLayout.setTextValue("All");
		expertCellLayout.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				settingSelectedColorToTeamCell(teamSize);
				teamName 						= "All";
				settingExpertLocatorList(-1, teamName, groupName, requestParam);
			}
		});
		lyExpertLocatorSubContainer.addView(expertCellLayout);
		settingSelectedColorToTeamCell(0);
		settingExpertLocatorList(0, teamName, groupName, requestParam);
	}

	/**
	 * @author 324520
	 * @Function name : settingExpertLocatorList
	 * @Description : setting expert list to list view corresponding selected group and team
	 * Default group,team is 0,0 
	 */
	public void settingExpertLocatorList(int teamIndex, String teamName, String groupName, String requestParam)
	{
		ArrayList<Expert> teamwiseExpertList 		= new ArrayList<Expert>();
		teamwiseExpertList.clear();
		for (Expert temp : lstExperts)
		{
			if (temp.getGroupName().equalsIgnoreCase(groupName) && teamName.equalsIgnoreCase("All"))
			{
				teamwiseExpertList.add(temp);
			}
			else if (temp.getGroupName().equalsIgnoreCase(groupName) && temp.getTeamName().equalsIgnoreCase(teamName)) teamwiseExpertList.add(temp);
		}
		txtExpertLabel.setText(groupName + " - " + teamName);

		if (teamwiseExpertList != null && teamwiseExpertList.size() > 0)
		{
			ExpertListAdapter adpExpert 			= new ExpertListAdapter(getActivity(), teamwiseExpertList, requestParam);
			lstDocuments.setAdapter(adpExpert);
		}
		else
		{
			AlertDialog.Builder errorDialog 		= new AlertDialog.Builder(getActivity());
			errorDialog.setTitle("No Contacts");
			errorDialog.setMessage("for the selected group and team");
			errorDialog.setNeutralButton("Ok", null);
			errorDialog.show();
		}
	}

	/**
	 * @author 324520
	 * @Function name : settingSelectedColorToGroupCell
	 * @Description : change color of selected group
	 */
	public void settingSelectedColorToGroupCell(int selectedPosition)
	{
		int groupCellSize 						= lyExpertLocatorMainContainer.getChildCount();
		for (int i = 0; i < groupCellSize; i++)
		{
			ExpertLayoutCell expertCellLayout 	= (ExpertLayoutCell) lyExpertLocatorMainContainer.getChildAt(i);
			if (i == selectedPosition)
				expertCellLayout.makeCellAsSelected();
			else
				expertCellLayout.makeCellAsUnselected();
		}
	}

	/**
	 * @author 324520
	 * @Function name : settingSelectedColorToTeamCell
	 * @Description : change color of selected team
	 */
	public void settingSelectedColorToTeamCell(int selectedPosition)
	{
		int teamCellSize 						= lyExpertLocatorSubContainer.getChildCount();
		for (int i = 0; i < teamCellSize; i++)
		{
			ExpertLayoutCell expertCellLayout 	= (ExpertLayoutCell) lyExpertLocatorSubContainer.getChildAt(i);
			if (i == selectedPosition) 
				expertCellLayout.makeCellAsSelected();
			else
				expertCellLayout.makeCellAsUnselected();
		}
	}

	/**
	 * @author 324520
	 * @Function name : downloadDocumentViewer
	 * @Description : downloading DocumentViewer from a website
	 */
	public void downloadDocumentViewer()
	{
		try
		{
			if (isNetworkAvailable())
			{
				// "http://www.kingsoftstore.com/download/android_office_reader.apk";
				appFilePath 				= Constants.APK_FILE_PATH;
				initializingAlertMessages();
				downloadOrDisplayAPK();
			}
			else
			{
				AppUtil.alertMsg(getActivity(), getString(R.string.offline_label), getString(R.string.offline_message));
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			AppUtil.alertMsg(getActivity(), "Error", "Error occured");
		}
	}

	public void initializingAlertMessages()
	{
		errorOccuredDialog 				= new AlertDialog.Builder(getActivity());
		errorOccuredDialog.setTitle("Error");
		errorOccuredDialog.setMessage("Error Occured. Can't download file!");
		errorOccuredDialog.setNeutralButton("Ok", null);
	}

	/**
	 * @author 324520
	 * @Function name : downloadOrDisplayAPK
	 * @Description : downloading doc viewer apk to sdcard
	 */
	public void downloadOrDisplayAPK()
	{
		try
		{
			// creating a file object with the remote file path
			apkFile 					= new File(appFilePath);
			apkFileUri 					= Uri.fromFile(apkFile);
			apkFileName 				= gettingFileName(appFilePath);
			apkExtension 				= gettingFileExtension(apkFileName);
			apkFilenameWithoutExtension = gettingEncodedFileName(apkFileName);

			try
			{
				boolean isSDPresent 		= android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
				if (isSDPresent)
				{
					downloadingAPKfile 		= new File(Environment.getExternalStorageDirectory() + File.separator + Constants.SDCARD_FOLDER + File.separator + apkFileName);
					apkLocalFilePath 		= downloadingAPKfile.getPath();
					if (!downloadingAPKfile.exists())
					{
						if (isNetworkAvailable())
						{
							DownloadAPKFile downloadAPKFile 		= new DownloadAPKFile();
							downloadAPKFile.execute(appFilePath);
						}
						else
						{
							appListingDialog.setTitle(getString(R.string.offline_label));
							appListingDialog.setMessage(getString(R.string.offline_message));
							appListingDialog.setNeutralButton("Ok", new DialogInterface.OnClickListener()
							{
								@Override
								public void onClick(DialogInterface dialog, int which)
								{
								}
							});
							appListingDialog.show();
						}
					}
					else
					{
						displaySelectedDocument();
					}
				}
				else
				{
					appListingDialog.setTitle("Error");
					appListingDialog.setMessage("SD Card not present. Can't download file!");
					appListingDialog.show();
				}
			}
			catch (Exception e)
			{
				errorOccuredDialog.show();
				getActivity().finish();
			}
		}
		catch (Exception e)
		{
			errorOccuredDialog.show();
			getActivity().finish();
		}
	}

	/**
	 * @author 324520
	 * @Function name : DownloadAPKFile
	 * @Description : Async task for downloading spk file
	 */
	private class DownloadAPKFile extends AsyncTask<String, Integer, String>
	{
		ProgressDialog	apkDownloadProgressDialog		= null;

		@Override
		protected String doInBackground(String... sUrl)
		{
			try
			{
				downloadAPKFromUrl(getActivity());
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			return null;
		}

		public void downloadAPKFromUrl(Activity act)
		{
			HttpClient httpclient 			= getNewHttpClient();
			try
			{
				String filePathUrl 			= appFilePath;
				if (filePathUrl != null && (!filePathUrl.equalsIgnoreCase("")))
				{
					int slashIndex 			= filePathUrl.lastIndexOf("/");
					String urlDomain 		= filePathUrl.substring(0, slashIndex + 1);
					String fileName1 		= filePathUrl.substring(slashIndex + 1);
					File file 				= null;
					File serviceStoreFolder = new File(Environment.getExternalStorageDirectory() + File.separator + Constants.SDCARD_FOLDER);
					boolean success 		= false;
					if (!serviceStoreFolder.exists())
					{
						success 			= serviceStoreFolder.mkdir();
						if (!success)
						{
						}
						else
						{
							file 			= new File(Environment.getExternalStorageDirectory() + File.separator + Constants.SDCARD_FOLDER + File.separator + fileName1);
						}
					}
					else
					{
						file 				= new File(Environment.getExternalStorageDirectory() + File.separator + Constants.SDCARD_FOLDER + File.separator + fileName1);
					}

					if (file.exists())
					{
						file.delete();
					}

					file.createNewFile();
					apkLocalFilePath 		= file.getPath();

					SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
					String userName 		= AppPreferences.getUserName(prefs);
					String password 		= AppPreferences.getUserPassword(prefs);
					Spanned docspannedUrl 	= Html.fromHtml(urlDomain);
					String docUrlFormatted 	= docspannedUrl.toString().replace(" ", "%20");
					String encodeFileName 	= fileName1;
					try
					{
						encodeFileName 		= URLEncoder.encode(fileName1, "UTF-8");
						encodeFileName 		= encodeFileName.toString().replace("+", "%20");
					}
					catch (UnsupportedEncodingException e)
					{
						e.printStackTrace();
						encodeFileName 		= fileName1;
					}
					String urlEncoded 		= docUrlFormatted + encodeFileName;
					HttpGet downloadDocGet 	= new HttpGet(urlEncoded);

					downloadDocGet.addHeader("Accept", "*/*");
					downloadDocGet.addHeader("Authorization", "Basic " + new String(Base64.encode(("" + "\\" + userName + ":" + password).getBytes(), Base64.NO_WRAP)));
					HttpResponse response 	= httpclient.execute(downloadDocGet);
					HttpEntity entity 		= response.getEntity();
					fileNameInMappingFile 	= encodeFileName;
					if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
					{
						if (entity != null)
						{					
							InputStream is 				= entity.getContent();
							len 						= entity.getContentLength();
							int current 				= 0;
							int total 					= 0;
							FileOutputStream fos 		= new FileOutputStream(file);

							byte data[] 				= new byte[1024];
							while ((current = is.read(data)) != -1)
							{
								total 			+= current;

								float p1 		= ((float) total / (float) len);
								p1 				= p1 * 100;
								int p 			= (int) p1;
								publishProgress(p);
								fos.write(data, 0, current);
							}

							fos.close();

							long fileSize 			= file.length();
							if (!(fileSize > 0))
							{
								boolean delete 		= file.delete();
								errorStatus 		= true;
							}
							else if (fileSize < len)
							{
								boolean delete 		= file.delete();
								errorStatus 		= true;
							}

							else
							{
								//file downloading complete
							}
						}
					}
					else
					{
						boolean delete 		= file.delete();
						errorStatus 		= true;

					}
				}

			}
			catch (Exception e)
			{
				errorStatus 				= true;
				e.printStackTrace();
			}

			finally
			{
				long fileSize 				= downloadingAPKfile.length();
				if (!(fileSize > 0))
				{
					boolean delete 			= downloadingAPKfile.delete();
					errorStatus 			= true;
				}
				else if (fileSize < len)
				{
					boolean delete 			= downloadingAPKfile.delete();
					errorStatus 			= true;
				}
				else
				{
					//file downloading complete
				}
				httpclient.getConnectionManager().shutdown();
			}
		}

		@Override
		protected void onPreExecute()
		{
			super.onPreExecute();

			apkDownloadProgressDialog = new ProgressDialog(getActivity());
			apkDownloadProgressDialog.setTitle("Downloading..");
			apkDownloadProgressDialog.setMessage("" + apkFileName);
			apkDownloadProgressDialog.setCancelable(false);
			apkDownloadProgressDialog.setCanceledOnTouchOutside(false);
			apkDownloadProgressDialog.setIndeterminate(false);
			apkDownloadProgressDialog.setMax(100);
			apkDownloadProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

			apkDownloadProgressDialog.show();
		}

		@Override
		protected void onProgressUpdate(Integer... progress)
		{
			super.onProgressUpdate(progress);
			apkDownloadProgressDialog.setProgress(progress[0]);

		}

		@Override
		protected void onPostExecute(String s)
		{
			apkDownloadProgressDialog.dismiss();
			if (errorStatus)
			{
				appListingDialog.setTitle("Error");
				appListingDialog.setNeutralButton("Ok", new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						
					}
				});

				appListingDialog.setMessage("Error Occured");
				appListingDialog.show();
			}
			else
			{
				
				AppInstallingTask task 			= new AppInstallingTask();
				task.execute("hai");
			}
			super.onPostExecute(s);
		}

	}

	public void displaySelectedDocument()
	{
		boolean fileStatus 						= false;
		if (downloadingAPKfile.exists())
		{
			long fileSize 						= downloadingAPKfile.length();
			if (!(fileSize > 0))
			{
				// if file is empty, delete it
				boolean delete 					= downloadingAPKfile.delete();
				fileStatus = false;
			}
			else
			{
				fileStatus = true;
			}
		}
		if (!fileStatus)
		{
			errorOccuredDialog.show();
		}
		else
		{
			// displayDocument(file);
			try
			{
				AppInstallingTask task 			= new AppInstallingTask();
				task.execute("hai");
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	private class AppInstallingTask extends AsyncTask<String, Integer, String>
	{

		@Override
		protected void onPreExecute()
		{
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... params)
		{
			String s 			= "";
			installingApp();
			return s;
		}

		@Override
		protected void onPostExecute(String s)
		{
			DashboardFragment.docPath 			= localFilePath;
		}
	}
	/**
	 * @author 324520
	 * @Function name : installingApp
	 * @Description : installing app when downloading apk file completes
	 * after installing, this new app will automatically opening the document we already selected 
	 */
	public void installingApp()
	{
		boolean isNonPlayAppAllowed;
		try
		{
			//redirecting to settings page
			
			/*
			 * isNonPlayAppAllowed =
			 * Settings.Secure.getInt(getContentResolver(),
			 * Settings.Secure.INSTALL_NON_MARKET_APPS) == 1; if
			 * (!isNonPlayAppAllowed) { AlertDialog.Builder
			 * enableUnkNownSourcesAlert = new AlertDialog.Builder(this);
			 * enableUnkNownSourcesAlert.setMessage("Enable Unknown sources");
			 * enableUnkNownSourcesAlert.setTitle(
			 * "Enable Unknown sources from SETTINGS > SECURITY inorder to install the app."
			 * ); enableUnkNownSourcesAlert.setPositiveButton("Ok", new
			 * OnClickListener() {
			 * 
			 * @Override public void onClick(DialogInterface dialog, int which)
			 * { startActivity(new
			 * Intent(android.provider.Settings.ACTION_SECURITY_SETTINGS)); }
			 * }); enableUnkNownSourcesAlert.setNegativeButton("Cancel", null);
			 * enableUnkNownSourcesAlert.show(); }
			 * 
			 * 
			 * else
			 */
			{
				Intent intent 			= new Intent(Intent.ACTION_VIEW);
				intent.setDataAndType(Uri.fromFile(new File(apkLocalFilePath)), "application/vnd.android.package-archive");
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * @author 324520
	 * @Function name : creatingParentDirectory
	 * @Description : creating Parent Directory for the zip file
	 */

	private String creatingParentDirectory(String tempFilePath)
	{
		int dotIndex 				= tempFilePath.lastIndexOf(".");
		String folderpath 			= tempFilePath.substring(0, dotIndex);
		File parentFolder 			= new File(folderpath);
		if ((!parentFolder.exists()) || (!parentFolder.isDirectory()))
		{
			parentFolder.mkdir();
		}
		return folderpath;
	}

	/**
	 * @author 324520
	 * @Function name	 : gettingParentDirectoryName
	 * @Description 	 : getting Parent Directory Name
	 */
	private String gettingParentDirectoryName(String tempFileName)
	{
		int dotIndex 			= tempFileName.lastIndexOf(".");
		String filename 		= tempFileName.substring(0, dotIndex);
		return filename;
	}

	/**
	 * @author 324520
	 * @Function name 	: deleteDirectory
	 * @Description 	: deleting a folder and its contents
	 */	
	public static boolean deleteDirectory(File path)
	{
		if (path.exists())
		{
			File[] files 			= path.listFiles();
			if (files == null)
			{
				return true;
			}
			for (int i = 0; i < files.length; i++)
			{
				if (files[i].isDirectory())
				{
					deleteDirectory(files[i]);
				}
				else
				{
					files[i].delete();
				}
			}
		}
		return (path.delete());
	}

	/**
	 * @author 324520
	 * @Function name : isAppInstalled
	 * @Description : checking document viewer is Installed
	 */
	private boolean isAppInstalled(String packageName)
	{
		PackageManager pm 		= getActivity().getPackageManager();
		boolean installed 		= false;
		try
		{
			pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
			installed 			= true;
		}
		catch (PackageManager.NameNotFoundException e)
		{
			installed 			= false;
		}
		return installed;
	}
}