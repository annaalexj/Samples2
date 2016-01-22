package com.myapps.b.set.receivers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.preference.PreferenceManager;

import com.myapps.b.set.fragments.DashboardFragment;
import com.myapps.b.set.utils.Constants;


/**
 * @author 324520
 * @Class name : AppInstalledReceiver
 * @Description : This receiver listens doc viewer app installation process,
 * and after completing installation, it will open the document using this doc viewer
 */
public class AppInstalledReceiver extends BroadcastReceiver
{

	Uri					docLocalFileUri;
	Context				activity;
	AlertDialog.Builder	appListingDialog;
	String				fileName, extensionType, docType, readerPackageName, filePath;

	
	
	@Override
	public void onReceive(Context context, Intent intent)
	{
		activity 						= context;
		String installedPackageName 	= intent.getData().toString();
		readerPackageName 				= installedPackageName.replace("package:", "");
		if (readerPackageName.equalsIgnoreCase(Constants.APK_FILE_PACKAGE))
		{
			appListingDialog 			= new AlertDialog.Builder(context);
			filePath 					= DashboardFragment.docPath;
			docType 					= gettingFileExtension(filePath);
			File docfile 				= new File(filePath);
			displayDocument(docfile);
		}

	}

	public boolean checkingDocumentOnDevice()
	{
		boolean status 				= false;
		File file 					= new File(filePath);
		status 						= file.exists();
		return status;
	}

	public String gettingLocalFilePath(String argfileName)
	{
		String filePathOnSdcard 			= "";
		File appFolder 						= activity.getFilesDir();
		File file 							= new File(filePath);
		if (file.exists())
		{
			filePathOnSdcard 				= file.getPath();
		}
		return filePathOnSdcard;
	}

	public String gettingFileName(String url)
	{
		String fileName 					= "";
		int slashIndex 						= url.lastIndexOf("/");
		fileName 							= url.substring(slashIndex + 1);
		return fileName;
	}

	public String gettingFileExtension(String url)
	{
		String extension 					= "";
		int slashIndex 						= url.lastIndexOf(".");
		extension 							= url.substring(slashIndex + 1);
		return extension;
	}

	public void DefaultDocOpening()
	{

		boolean docAvailStatus 				= true;

		if (checkingDocumentOnDevice())
		{
			if (docType.equalsIgnoreCase("PPTX") || docType.equalsIgnoreCase("PPT") || docType.equalsIgnoreCase("POT") || docType.equalsIgnoreCase("POTX"))
			{
				extensionType 				= "application/vnd.ms-powerpoint";
			}
			else if (docType.equalsIgnoreCase("DOCX") || docType.equalsIgnoreCase("DOC"))
			{
				extensionType 				= "application/msword";
			}
			else if (docType.equalsIgnoreCase("XLSX") || docType.equalsIgnoreCase("XLS"))
			{
				extensionType 				= "application/vnd.ms-excel";
			}
			else
			{
				appListingDialog.show();
				docAvailStatus 				= false;
			}
			if (docAvailStatus)
			{

				Intent docViewIntent 		= new Intent();
				docViewIntent.setAction(Intent.ACTION_VIEW);
				docViewIntent.setDataAndType(docLocalFileUri, extensionType);
				docViewIntent.setPackage(Constants.APK_FILE_PACKAGE);
				docViewIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
				docViewIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				try
				{
					activity.startActivity(docViewIntent);
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

	public void displayDocument(File file)
	{

		File appFolder 			= activity.getFilesDir();
		File[] lstFiles11 		= appFolder.listFiles();
		int current 			= 0;
		int total 				= 0;
		FileInputStream fis;
		try
		{
			fis 					= new FileInputStream(file);
			FileOutputStream fos 	= activity.openFileOutput("" + fileName, Context.MODE_WORLD_READABLE);
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
			docLocalFileUri 		= Uri.fromFile(tempFilenew);

			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
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

		File[] lstFiles12 		= appFolder.listFiles();
		{
			DefaultDocOpening();
		}
	}

}