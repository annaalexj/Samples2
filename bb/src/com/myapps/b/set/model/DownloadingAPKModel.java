package com.myapps.b.set.model;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.KeyStore;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
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
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.Spanned;
import android.util.Base64;

import com.myapps.b.set.R;
import com.c.bfs.set.fragments.DashboardFragment;
import com.myapps.b.set.MySSLSocketFactory;
import com.myapps.b.set.preferences.AppPreferences;
import com.myapps.b.set.utils.AppUtil;
import com.myapps.b.set.utils.Constants;

public class DownloadingAPKModel
{
	String					appFilePath;
	AlertDialog.Builder		errorOccuredDialog;
	File					apkFile						= null;
	Uri						apkFileUri					= null;
	String					apkExtension				= "";
	String					apkFilenameWithoutExtension	= "";
	String					apkFilePath;
	String					apkLocalFilePath;
	public static String	docPath						= "";
	String					apkFileName;
	File					downloadingAPKfile;
	Activity				context;
	AlertDialog.Builder		appListingDialog;
	String					fileNameInMappingFile		= "";
	long					len;
	boolean					errorStatus					= false;
	String					docLocalPath				= "";

	public DownloadingAPKModel(Activity argContext, String argfilePath)
	{
		context 			= argContext;
		docLocalPath 		= argfilePath;
		appListingDialog 	= new AlertDialog.Builder(context);
		appListingDialog.setTitle("Error");
		appListingDialog.setMessage("Unable to download document. Please try again");
		appListingDialog.setNeutralButton("Ok", new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				// finish();
			}
		});
		appListingDialog.show();
	}

	public void downloadDocumentViewer()
	{
		try
		{
			if (isNetworkAvailable())
			{
				// "http://www.kingsoftstore.com/download/android_office_reader.apk";
				appFilePath 		= Constants.APK_FILE_PATH;
				initializingAlertMessages();
				downloadOrDisplayAPK();
			}
			else
			{
				AppUtil.alertMsg(context, "Error", context.getString(R.string.no_network));
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			AppUtil.alertMsg(context, "Error", "Error occured");
		}
	}

	public void initializingAlertMessages()
	{

		errorOccuredDialog 			= new AlertDialog.Builder(context);
		errorOccuredDialog.setTitle("Error");
		errorOccuredDialog.setMessage("Error Occured. Can't download file!");
		errorOccuredDialog.setNeutralButton("Ok", null);
	}

	public boolean isNetworkAvailable()
	{
		Context context1 					= context;
		ConnectivityManager connectivity 	= (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity == null)
		{
			return false;
		}
		else
		{
			NetworkInfo[] info 				= connectivity.getAllNetworkInfo();
			if (info != null)
			{
				for (int i = 0; i < info.length; i++)
				{
					if (info[i].getState() == NetworkInfo.State.CONNECTED)
					{
						return true;
					}
				}
			}
		}
		return false;
	}

	public String gettingFileName(String url)
	{
		String fileName 			= "";
		int slashIndex 				= url.lastIndexOf("/");
		fileName 					= url.substring(slashIndex + 1);
		
		return fileName;
	}

	public String gettingFileExtension(String url)
	{
		String extension 			= "";
		int slashIndex 				= url.lastIndexOf(".");
		extension 					= url.substring(slashIndex + 1);

		return extension;
	}

	public String gettingEncodedFileName(String filefullname)
	{
		String fileName 			= "";
		if (filefullname.contains("."))
		{
			int slashIndex 			= filefullname.lastIndexOf(".");
			fileName 				= filefullname.substring(0, slashIndex);
		}
		else
		{
			fileName 				= filefullname;
		}
	
		String encodeFileName 		= "";
		try
		{
			encodeFileName 			= URLEncoder.encode(fileName, "UTF-8");
			encodeFileName 			= encodeFileName.toString().replace("+", "%20");
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
			encodeFileName 			= fileName;
		}

		return encodeFileName;
	}

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
				boolean isSDPresent 	= android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
				if (isSDPresent)
				{
					downloadingAPKfile 	= new File(Environment.getExternalStorageDirectory() + File.separator + Constants.SDCARD_FOLDER + File.separator + apkFileName);
					apkLocalFilePath 	= downloadingAPKfile.getPath();
					if (!downloadingAPKfile.exists())
					{
						if (isNetworkAvailable())
						{
							DownloadAPKFile downloadAPKFile 	= new DownloadAPKFile();
							downloadAPKFile.execute(appFilePath);
						}
						else
						{
							appListingDialog.setTitle("Error");
							appListingDialog.setMessage("Unable to download app. Please try again");
							appListingDialog.setNeutralButton("Ok", new DialogInterface.OnClickListener()
							{
								@Override
								public void onClick(DialogInterface dialog, int which)
								{
									// finish();
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
				context.finish();
			}
		}
		catch (Exception e)
		{
			errorOccuredDialog.show();
			context.finish();
		}
	}

	private class DownloadAPKFile extends AsyncTask<String, Integer, String>
	{

		ProgressDialog	apkDownloadProgressDialog	= null;

		@Override
		protected String doInBackground(String... sUrl)
		{
			try
			{
				downloadAPKFromUrl(context);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			return null;
		}

		public void downloadAPKFromUrl(Activity act)
		{
			HttpClient httpclient 		= getNewHttpClient();
			try
			{
				String filePathUrl 		= appFilePath;
				if (filePathUrl != null && (!filePathUrl.equalsIgnoreCase("")))
				{
					int slashIndex 				= filePathUrl.lastIndexOf("/");
					String urlDomain 			= filePathUrl.substring(0, slashIndex + 1);
					String fileName1 			= filePathUrl.substring(slashIndex + 1);
					File file 					= null;
					File serviceStoreFolder 	= new File(Environment.getExternalStorageDirectory() + File.separator + Constants.SDCARD_FOLDER);
					boolean success 			= false;
					if (!serviceStoreFolder.exists())
					{
						success 				= serviceStoreFolder.mkdir();
						if (!success)
						{

						}
						else
						{
							file 				= new File(Environment.getExternalStorageDirectory() + File.separator + Constants.SDCARD_FOLDER + File.separator + fileName1);
						}
					}
					else
					{
						file 					= new File(Environment.getExternalStorageDirectory() + File.separator + Constants.SDCARD_FOLDER + File.separator + fileName1);
					}

					if (file.exists())
					{
						file.delete();
					}

					file.createNewFile();
					apkLocalFilePath 			= file.getPath();
					SharedPreferences prefs 	= PreferenceManager.getDefaultSharedPreferences(context);
					String userName 			= AppPreferences.getUserName(prefs);
					String password 			= AppPreferences.getUserPassword(prefs);
					Spanned docspannedUrl 		= Html.fromHtml(urlDomain);
					String docUrlFormatted 		= docspannedUrl.toString().replace(" ", "%20");
					String encodeFileName 		= fileName1;
					try
					{
						encodeFileName 			= URLEncoder.encode(fileName1, "UTF-8");
						encodeFileName 			= encodeFileName.toString().replace("+", "%20");
					}
					catch (UnsupportedEncodingException e)
					{
						e.printStackTrace();
						encodeFileName 			= fileName1;
					}
					String urlEncoded 			= docUrlFormatted + encodeFileName;
					HttpGet downloadDocGet 		= new HttpGet(urlEncoded);

					downloadDocGet.addHeader("Accept", "*/*");
					downloadDocGet.addHeader("Authorization", "Basic " + new String(Base64.encode(("" + "\\" + userName + ":" + password).getBytes(), Base64.NO_WRAP)));

					HttpResponse response 		= httpclient.execute(downloadDocGet);
					HttpEntity entity 			= response.getEntity();
	
					fileNameInMappingFile 		= encodeFileName;
					if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
					{
						if (entity != null)
						{
						
							InputStream is 			= entity.getContent();
							len 					= entity.getContentLength();
							int current 			= 0;
							int total 				= 0;
							FileOutputStream fos 	= new FileOutputStream(file);

							byte data[] 			= new byte[1024];
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

			apkDownloadProgressDialog = new ProgressDialog(context);
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
						// finish();
					}
				});

				appListingDialog.setMessage("Error Occured");
				appListingDialog.show();
			}
			else
			{
				
				AppInstallingTask task = new AppInstallingTask();
				task.execute("hai");
			}
			
			super.onPostExecute(s);
		}

	}

	public void displaySelectedDocument()
	{
		boolean fileStatus 				= false;
		if (downloadingAPKfile.exists())
		{
			long fileSize 				= downloadingAPKfile.length();
			if (!(fileSize > 0))
			{
				// if file is empty, delete it
				boolean delete 			= downloadingAPKfile.delete();
				fileStatus 				= false;
			}
			else
			{
				fileStatus 			= true;
			}
		}
		if (!fileStatus)
		{
			errorOccuredDialog.show();
		}
		else
		{			
			try
			{
				AppInstallingTask task 		= new AppInstallingTask();
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
			String s 		= "";
			installingApp();
			return s;
		}

		@Override
		protected void onPostExecute(String s)
		{
			DashboardFragment.docPath = docLocalPath;
		}
	}

	public void installingApp()
	{
		boolean isNonPlayAppAllowed;
		try
		{
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
				context.startActivity(intent);
			}
		}
		catch (Exception e)
		{		
			e.printStackTrace();
		}
	}

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
}
