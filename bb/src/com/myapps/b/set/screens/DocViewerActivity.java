package com.myapps.b.set.screens;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.auth.BasicScheme;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.myapps.b.set.R;
import com.myapps.b.set.ActivityListener;
import com.myapps.b.set.BaseAppData;
import com.myapps.b.set.preferences.AppPreferences;
import com.myapps.b.set.utils.AppUtil;
import com.myapps.b.set.utils.Constants;

public class DocViewerActivity extends Activity implements ActivityListener, OnClickListener, OnItemClickListener, OnPageChangeListener
{

	private static final String	TAG	= DocViewerActivity.class.getSimpleName();
	LinearLayout				lyVideoDisplay;
	LinearLayout				lyImageDisplay;
	LinearLayout				lyDocWebview;

	ImageView					imgDocImage;
	WebView						webviewDocumentViewer;
	VideoView					video_view;
	ProgressBar					progress;

	String						localFilePath;
	String						docType;
	String						fileName;
	String						url;

	@Override
	public void onCreate(Bundle savedInstance)
	{
		try
		{
			super.onCreate(savedInstance);
			if (getResources().getBoolean(R.bool.portrait_only))
			{
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			}
			else
			{
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			}

			setContentView(R.layout.doc_viewer);

			lyVideoDisplay 			= (LinearLayout) findViewById(R.id.lyVideoDisplay);
			lyImageDisplay 			= (LinearLayout) findViewById(R.id.lyImageDisplay);
			lyDocWebview 			= (LinearLayout) findViewById(R.id.lyDocWebview);
			webviewDocumentViewer 	= (WebView) findViewById(R.id.webviewDocumentViewer);
			imgDocImage 			= (ImageView) findViewById(R.id.imgDocImage);
			video_view 				= (VideoView) findViewById(R.id.video_view);
			progress 				= (ProgressBar) findViewById(R.id.progress);

			lyVideoDisplay.setVisibility(View.GONE);
			lyImageDisplay.setVisibility(View.GONE);
			lyDocWebview.setVisibility(View.GONE);

			Bundle documentBundle 	= getIntent().getBundleExtra("viewer");
			localFilePath 			= documentBundle.getString("localFilePath");
			docType 				= documentBundle.getString("type");
			fileName 				= documentBundle.getString("docName");
			url 					= documentBundle.getString("url");
			ActionBar actionBar 	= this.getActionBar();
			actionBar.hide();

			Intent i 				= getIntent();
			String filename 		= i.getStringExtra("filename");
			String backButtonText 	= i.getStringExtra("backButtonText");
			RelativeLayout firstRL 	= (RelativeLayout) findViewById(R.id.firstRL);
			firstRL.getLayoutParams().height 	= DrawerHome.actionbarHeight;
			ImageView imgBack 		= (ImageView) findViewById(R.id.imgBack);
			TextView headerTextView = (TextView) findViewById(R.id.headerTextView);
			TextView txtBack 		= (TextView) findViewById(R.id.txtBack);
			headerTextView.setText(filename);

			txtBack.setText(backButtonText);

			imgBack.setOnClickListener(new OnClickListener()
			{

				@Override
				public void onClick(View arg0)
				{
					onBackPressed();

				}

			});
			if (docType.equalsIgnoreCase("JPG") || docType.equalsIgnoreCase("PNG") || docType.equalsIgnoreCase("GIF"))
			{
				setImageView();
			}
			else if (docType.equalsIgnoreCase("MP4") || docType.equalsIgnoreCase("3GP"))
			{
				setVideoView();
			}
			else if (docType.equalsIgnoreCase("web"))
			{
				headerTextView.setText(url);
				setWebView();
			}

		}
		catch (Exception e)
		{
			AppUtil.alertMsg(this, Constants.ALERT_TITLE_ERROR, Constants.RESPONSE_ERROR_OCCURRED);
			e.printStackTrace();
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

	@Override
	public void onPageSelected(int arg0)
	{

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
	{

	}

	@Override
	public void onClick(View arg0)
	{

	}

	@Override
	public void onFinish(BaseAppData data)
	{

	}

	@Override
	public void onCancel(BaseAppData data)
	{

	}

	private void setVideoView()
	{

		lyVideoDisplay.setVisibility(View.VISIBLE);
		lyImageDisplay.setVisibility(View.GONE);
		lyDocWebview.setVisibility(View.GONE);

		VideoView videoView 		= (VideoView) findViewById(R.id.video_view);
		ProgressBar progress 		= (ProgressBar) findViewById(R.id.progress);
		lyVideoDisplay.setVisibility(View.VISIBLE);
		if (localFilePath == "" || localFilePath == null)
		{
			Toast.makeText(this, "Sorry, this file can not be displayed!", Toast.LENGTH_LONG).show();
		}
		else
		{
			Uri uri 						= Uri.parse(localFilePath);
			videoView.setVideoURI(uri);
			MediaController mediaController = new MediaController(this);
			mediaController.setPadding(0, 0, 0, 80);
			videoView.setMediaController(mediaController);
			videoView.start();
			progress.setVisibility(View.GONE);
		}

	}

	private void setImageView()
	{
		lyVideoDisplay.setVisibility(View.GONE);
		lyImageDisplay.setVisibility(View.VISIBLE);
		lyDocWebview.setVisibility(View.GONE);

		ImageView imgDocImage 		= (ImageView) findViewById(R.id.imgDocImage);
		lyImageDisplay.setVisibility(View.VISIBLE);
		Uri uri 					= Uri.parse(localFilePath);
		imgDocImage.setImageURI(uri);

	}

	private void setWebView()
	{
		lyVideoDisplay.setVisibility(View.GONE);
		lyImageDisplay.setVisibility(View.GONE);
		lyDocWebview.setVisibility(View.VISIBLE);

		//shwoing through web view
		/*SharedPreferences prefs 		= PreferenceManager.getDefaultSharedPreferences(this);
		String username 				= AppPreferences.getUserName(prefs);
		String password 				= AppPreferences.getUserPassword(prefs);
		String usernameWithDomain 		= Constants.DOMAIN + "\\" + username;

		webviewDocumentViewer.setWebViewClient(new ProxyAuthWebViewClient(usernameWithDomain, password));
		webviewDocumentViewer.getSettings().setJavaScriptEnabled(true);
		webviewDocumentViewer.getSettings().setBuiltInZoomControls(true);
		webviewDocumentViewer.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_INSET);
		webviewDocumentViewer.getSettings().setPluginState(PluginState.ON);
		webviewDocumentViewer.getSettings().setAllowFileAccess(true);
		webviewDocumentViewer.loadUrl("https://bfs-set.c.com/default.aspx");		
		webviewDocumentViewer.setLayerType(View.LAYER_TYPE_SOFTWARE, null);*/
		
		/*String doc1 = "<iframe src='";
		String doc2 = "https://mail.c.com";
		String doc3 = "' width='100%' height='100%' style='border: none;'></iframe>";
		String doc 	= doc1 + doc2 + doc3;*/

		//showing through system browser
		try
		{
			Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
			startActivity(browserIntent);
		}
		catch (Exception e)
		{
			finish();
		}
	}

	private class ProxyAuthWebViewClient extends WebViewClient
	{

		String	proxyUserName;
		String	proxyPassword;

		public ProxyAuthWebViewClient(String proxyUserName, String proxyPassword)
		{
			this.proxyUserName = proxyUserName;
			this.proxyPassword = proxyPassword;
		}

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url)
		{
			loadUrl(view, url, proxyUserName, proxyPassword);

			return true;
		}

		@Override
		public void onPageFinished(WebView view, String url)
		{
			super.onPageFinished(view, url);

		}

		public void loadUrl(WebView view, String url, String proxyUserName, String proxyPassword)
		{
			UsernamePasswordCredentials creds 	= new UsernamePasswordCredentials(proxyUserName, proxyPassword);
			Header credHeader 					= BasicScheme.authenticate(creds, "UTF-8", true);
			Map<String, String> header 			= new HashMap<String, String>();
			header.put(credHeader.getName(), credHeader.getValue());
			view.loadUrl(url, header);
			// view.loadUrl(url);

		}
	}

}
