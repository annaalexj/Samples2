package com.myapps.b.set.screens;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.artifex.mupdf.MuPDFActivity;
import com.myapps.b.set.R;
import com.myapps.b.set.adapters.DocumentsAdapter;
import com.myapps.b.set.model.Document;
import com.myapps.b.set.model.DownloadingAPKModel;
import com.myapps.b.set.model.UnzippingModel;
import com.myapps.b.set.utils.AppUtil;
import com.myapps.b.set.utils.Constants;

public class ZipListingActivity extends Activity implements OnItemClickListener
{

	String				location;
	String				unzippedFolderName		= "";
	String				unzippedFolderPathNew	= "";
	String				parentFolderName		= "";
	String				rootFolderPath			= "";

	String				unzippedFolderPath, zipRootFilePath;

	ListView			lstDocuments;
	ArrayList<Document>	listDocuments			= null;
	AlertDialog.Builder	appListingDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		try
		{
			super.onCreate(savedInstanceState);
			if (getResources().getBoolean(R.bool.portrait_only))
			{
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			}
			else
			{
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			}

			setContentView(R.layout.zip_list);

			ActionBar actionBar 		= this.getActionBar();
			actionBar.hide();

			Intent i 					= getIntent();
			String filename 			= i.getStringExtra("filename");
			String backButtonText 		= i.getStringExtra("backButtonText");
			RelativeLayout firstRL 		= (RelativeLayout) findViewById(R.id.firstRL);

			firstRL.getLayoutParams().height = DrawerHome.actionbarHeight;
			ImageView imgBack 			= (ImageView) findViewById(R.id.imgBack);
			TextView headerTextView 	= (TextView) findViewById(R.id.headerTextView);
			TextView txtBack 			= (TextView) findViewById(R.id.txtBack);
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

			appListingDialog 		= new AlertDialog.Builder(this);
			lstDocuments 			= (ListView) findViewById(R.id.lstDocuments);
			listDocuments 			= new ArrayList<Document>();
			listDocuments.clear();
			unzippedFolderPathNew 	= getIntent().getStringExtra("zipfolderPath");
			zipRootFilePath 		= getIntent().getStringExtra("zipRootFilePath");
			File serviceStoreFolder = new File(unzippedFolderPathNew);
			
			if (serviceStoreFolder.exists())
			{
				String[] fileNames 	= serviceStoreFolder.list();
				for (String tempFileName : fileNames)
				{				
					Document temp 		= new Document();
					temp.setLinkFilename(tempFileName);
					String type 		= getType(tempFileName);

					if (type == null || type.equals(""))
					{
						type 			= "folder";
					}
					temp.setFiletype(type);
					listDocuments.add(temp);
				}
			}
			DocumentsAdapter adpDocument = new DocumentsAdapter(this, listDocuments, "zip");
			lstDocuments.setAdapter(adpDocument);
			lstDocuments.setOnItemClickListener(this);
		}
		catch (Exception e)
		{
			AppUtil.alertMsg(this, Constants.ALERT_TITLE_ERROR, Constants.RESPONSE_ERROR_OCCURRED);
			e.printStackTrace();
		}
	}

	private boolean checkingZipFile(String name)
	{
		int index 			= name.lastIndexOf(".");
		String extension 	= name.substring(index + 1);
		if (extension.equalsIgnoreCase("ZIP") || extension.equalsIgnoreCase("RAR"))
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	private String gettingParentDirectoryName(String tempFileName)
	{
		int dotIndex 		= tempFileName.lastIndexOf(".");
		String filename 	= tempFileName.substring(0, dotIndex);
		return filename;
	}

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

	@Override
	public void onItemClick(AdapterView<?> listview, View row, int index, long id)
	{
		if (listDocuments != null && listDocuments.size() > 0)
		{
			String tempFileName 			= listDocuments.get(index).getLinkFilename();
			if (checkingZipFile(tempFileName))
			{
				String zipFilePath 			= unzippedFolderPathNew + File.separator + tempFileName;
				// unzipping
				String parentDirectoryName 	= gettingParentDirectoryName(tempFileName);
				File directory 				= new File(unzippedFolderPathNew, parentDirectoryName);

				if (directory.exists())
				{					
					String filePathOnSdcard = directory.getPath();
					Intent i 				= new Intent(this, ZipListingActivity.class);
					i.putExtra("zipfolderPath", filePathOnSdcard);
					i.putExtra("zipRootFilePath", zipRootFilePath);
					i.putExtra("filename", tempFileName);
					{
						i.putExtra("backButtonText", "Back");
					}
					startActivity(i);
				}
				else
				{				
					// unzipping file
					String parentFolderPath 	= creatingParentDirectory(zipFilePath, tempFileName);
					UnzippingModel unzip 		= new UnzippingModel(this, parentFolderPath, zipFilePath, tempFileName, zipRootFilePath);
					unzip.unzippingFile();
				}
			}
			else if (!tempFileName.contains("."))
			{
				String zipFilePath 				= unzippedFolderPathNew + File.separator + tempFileName;
				File directory 					= new File(zipFilePath);
				if (directory.exists())
				{
					String filePathOnSdcard 	= directory.getPath();
					Intent i 					= new Intent(this, ZipListingActivity.class);
					i.putExtra("filename", tempFileName);
					{
						i.putExtra("backButtonText", "Back");
					}
					i.putExtra("zipfolderPath", filePathOnSdcard);
					i.putExtra("zipRootFilePath", zipRootFilePath);
					startActivity(i);
				}
			}
			else
			{
				String filePath 		= unzippedFolderPathNew + File.separator + tempFileName;
				String type 			= listDocuments.get(index).getFiletype();
				displayDocument(type, tempFileName, filePath);
			}
		}
	}

	public void DefaultDocOpening(String docType, String fileName, String tempfilePath)
	{

		boolean docAvailStatus 		= true;
		String extensionType 		= "";		
		File file 					= new File(tempfilePath);

		int current 				= 0;
		int total 					= 0;
		File appFolder 				= this.getFilesDir();
		File[] lstFiles11 			= appFolder.listFiles();
		FileInputStream fis;
		try
		{
			fis 					= new FileInputStream(file);
			FileOutputStream fos 	= this.openFileOutput("" + fileName, Context.MODE_WORLD_READABLE);
			byte data[] 			= new byte[1024];
			while ((current = fis.read(data)) != -1)
			{
				total 				+= current;
				fos.write(data, 0, current);
			}
			fos.close();
		}
		catch (Exception e3)
		{
			e3.printStackTrace();
		}

		File tempFilenew 			= new File(appFolder, "" + fileName);
		Uri localFileUri 			= null;
		if (tempFilenew.exists())
		{
			localFileUri 			= Uri.fromFile(tempFilenew);
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
			String temp 			= prefs.getString("tempFilename", "temp");
			Editor edt 				= prefs.edit();
			if (temp == null || temp.equals("") || temp.equalsIgnoreCase("temp") || temp.equalsIgnoreCase(fileName))
			{
				edt.remove("tempFilename");
				edt.commit();
			}
			else
			{
				File tempFileold 	= new File(appFolder, temp);
				if (tempFileold.exists()) tempFileold.delete();
				edt.remove("tempFilename");
				edt.commit();
			}
			edt.putString("tempFilename", fileName);
			edt.commit();
		}
		else
		{		
		}

		File[] lstFiles12 				= appFolder.listFiles();
		if (checkingDocumentOnSdcard(tempfilePath))
		{		
			if (docType.equalsIgnoreCase("PDF"))
			{
				Intent intent 			= new Intent(this, MuPDFActivity.class);
				intent.putExtra("filename", fileName);
				intent.putExtra("backButtonText", "Back");
				intent.setAction(Intent.ACTION_VIEW);
				intent.setData(localFileUri);
				startActivity(intent);
			}
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

				Intent docViewIntent 	= new Intent();
				docViewIntent.setAction(Intent.ACTION_VIEW);
				docViewIntent.setPackage(Constants.DOC_VIEWER_APP_PACKAGE);
				docViewIntent.setDataAndType(localFileUri, extensionType);
				docViewIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
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
						Intent docViewIntent1 	= new Intent();
						docViewIntent1.setAction(Intent.ACTION_VIEW);
						docViewIntent1.setDataAndType(localFileUri, extensionType);
						docViewIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
						docViewIntent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						startActivity(docViewIntent1);
					}
					catch (ActivityNotFoundException e1)
					{
						// show dialog to redirect to google play store
						AlertDialog.Builder appRedirectDialog 	= new AlertDialog.Builder(this);
						appRedirectDialog.setTitle("Alert");
						appRedirectDialog.setMessage("There is no applications can perform this action. Would you like to download " + Constants.DOC_VIEWER_APP_NAME
								+ " from Google Play store?");
						appRedirectDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener()
						{
							@Override
							public void onClick(DialogInterface dialog, int which)
							{
								final String appPackageName 	= Constants.DOC_VIEWER_APP_PACKAGE; // Can																// below
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
					catch (Exception e2)
					{

						AlertDialog.Builder appRedirectDialog1 		= new AlertDialog.Builder(this);
						appRedirectDialog1.setTitle("Alert");
						appRedirectDialog1.setMessage("There is no applications can perform this action. Would you like to download " + Constants.THIRD_PARTY_APP_NAME + " ?");
						final String docLocalPath 					= tempfilePath;
						appRedirectDialog1.setPositiveButton("Ok", new DialogInterface.OnClickListener()
						{
							@Override
							public void onClick(DialogInterface dialog, int which)
							{
								DownloadingAPKModel tempDownloadModel 	= new DownloadingAPKModel(ZipListingActivity.this, docLocalPath);
								tempDownloadModel.downloadDocumentViewer();
							}
						});
						appRedirectDialog1.setNegativeButton("Cancel", null);
						appRedirectDialog1.show();
					}
				}
			}
			else
			{
				if (docType.equalsIgnoreCase("MP4") || docType.equalsIgnoreCase("3GP"))
				{
					extensionType 		= "video/*";
				}
				else if (docType.equalsIgnoreCase("JPG") || docType.equalsIgnoreCase("PNG") || docType.equalsIgnoreCase("GIF"))
				{
					extensionType 		= "image/*";
				}
				else
				{
					appListingDialog 	= new AlertDialog.Builder(this);
					appListingDialog.setTitle("Error");
					appListingDialog.setMessage("There is no application can perform this action.");
					appListingDialog.setNeutralButton("Ok", null);
					appListingDialog.show();
					docAvailStatus 		= false;
				}
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
						appListingDialog.setTitle("Document Error");
						appListingDialog.setMessage("File cannot be opened");
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

	public boolean checkingDocumentOnSdcard(String filePath)
	{
		boolean status 		= false;
		String fileName 	= gettingFileName(filePath);
		File appFolder 		= this.getFilesDir();
		File file 			= new File(unzippedFolderPathNew, fileName);
		status 				= file.exists();
		return status;
	}

	public String gettingFileName(String url)
	{
		String fileName 	= "";
		int slashIndex 		= url.lastIndexOf("/");
		fileName 			= url.substring(slashIndex + 1);
		return fileName;
	}

	private String creatingParentDirectory(String tempFilePath, String tempFileName)
	{
		int dotIndex 		= tempFileName.lastIndexOf(".");
		String foldername 	= tempFileName.substring(0, dotIndex);
		File parentFolder 	= new File(unzippedFolderPathNew + File.separator + foldername);
		if ((!parentFolder.exists()) || (!parentFolder.isDirectory()))
		{
			parentFolder.mkdir();
		}
		return parentFolder.getPath();
	}

	public void displayDocument(String docType, String fileName, String tempfilePath)
	{

		File file 			= new File(tempfilePath);
		Uri localFileUri 	= Uri.fromFile(file);
		// show file
		if (docType.equalsIgnoreCase("MP4") || docType.equalsIgnoreCase("3GP") || docType.equalsIgnoreCase("JPG") || docType.equalsIgnoreCase("PNG")
				|| docType.equalsIgnoreCase("GIF") || docType.equalsIgnoreCase("web"))
		{
			boolean networkStatus 	= true;
			if (docType.equalsIgnoreCase("web"))
			{
				if (!isNetworkAvailable())
				{
					networkStatus 	= false;
					appListingDialog.setTitle(getString(R.string.offline_label));
					appListingDialog.setMessage(getString(R.string.offline_message));
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
				else
				{
					try
					{
						Intent browserIntent 	= new Intent(Intent.ACTION_VIEW, Uri.parse(tempfilePath));
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
								// finish();
							}
						});
						appListingDialog.show();
					}
				}
			}
			else
			{
				if (networkStatus)
				{
					Bundle documentBundle 	= new Bundle();
					documentBundle.putString("localFilePath", tempfilePath);
					documentBundle.putString("type", docType);
					documentBundle.putString("docName", fileName);
					documentBundle.putString("url", tempfilePath);
					Intent i 				= new Intent(this, DocViewerActivity.class);
					i.putExtra("filename", fileName);
					i.putExtra("backButtonText", "Back");					
					i.putExtra("viewer", documentBundle);
					startActivity(i);
				}
			}
		}
		else
		{
			DefaultDocOpening(docType, fileName, tempfilePath);
		}
	}

	public boolean isNetworkAvailable()
	{
		Context context 					= this;
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
}