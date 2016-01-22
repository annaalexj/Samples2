package com.myapps.b.set.model;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;

import com.myapps.b.set.screens.ZipListingActivity;

public class UnzippingModel 
{
	String	zipFileName 			= "";
	String	zipFolderName			="";
	String	location;
	String unzippedFolderName 		= "";
	String unzippedFolderPath 		= "";
	String parentFolderName 		= "";
	String rootFolderPath 			= "";
	String fileLocalPath 			= "";
	Activity appContext ;
	String zipRootFilePath;
	
	public UnzippingModel(Activity context , String tempFoledrPath, String tempFilePath, String tempFileName,String argRootFilePath)
	{
		appContext 			= context;
		zipRootFilePath 	= argRootFilePath;		
		rootFolderPath 		= tempFoledrPath;
		fileLocalPath 		= tempFilePath;
		zipFileName 		= tempFileName;
	}
	public void unzippingFile()
	{
		UnzippingTask unzipping 	= new UnzippingTask();
		unzipping.execute("");
	}
	public void Decompress(String argZipFileName, String argLocation)
	{
		zipFileName 		= argZipFileName;
		location 			= argLocation;
		directoryChecker("");
	}

	private void directoryChecker(String dir)
	{
		File file 			= new File(location + dir);
		if (!file.isDirectory())
		{
			file.mkdirs();
		}
	}

	public void unzip()
	{
		try
		{
			FileInputStream fin 			= new FileInputStream(zipFileName);
			ZipInputStream zin 				= new ZipInputStream(fin);
			ZipEntry ze 					= null;
			
			while ((ze = zin.getNextEntry()) != null)
			{			
				if (ze.isDirectory())
				{					
					directoryChecker(ze.getName());
				}
				else
				{					
					File targetFile 			= new File(location + ze.getName());
					File parent 				= targetFile.getParentFile();
					if (!parent.exists() && !parent.mkdirs())
					{
						throw new IllegalStateException("Couldn't create dir: " + parent);
					}
					unzippedFolderName 			= parent.getName();
					unzippedFolderPath 			= parent.getPath();
					FileOutputStream fout 		= new FileOutputStream(location + ze.getName());
					
					int size;
		            byte[] buffer 					= new byte[2048];
					BufferedOutputStream bufferOut 	= new BufferedOutputStream(fout, buffer.length);

	                while((size = zin.read(buffer, 0, buffer.length)) != -1) 
	                {
	                    bufferOut.write(buffer, 0, size);
	                }

	                bufferOut.flush();
	                bufferOut.close();
										
					targetFile.setReadable(true, false);									
					zin.closeEntry();
					fout.close();
					
				}
			}
			zin.close();
		}
		catch (Exception e)
		{
			
		}
	}

	private class UnzippingTask extends AsyncTask<String, Integer, String>
	{
		ProgressDialog	apkDownloadProgressDialog	= null;
		@Override
		protected void onPreExecute()
		{
			super.onPreExecute();
			apkDownloadProgressDialog 				= new ProgressDialog(appContext);
			apkDownloadProgressDialog.setTitle("Extracting..");
			apkDownloadProgressDialog.setMessage(zipFileName );
			apkDownloadProgressDialog.setCancelable(false);
			apkDownloadProgressDialog.setCanceledOnTouchOutside(false);
			apkDownloadProgressDialog.setIndeterminate(false);
			apkDownloadProgressDialog.setMax(100);
			apkDownloadProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

			apkDownloadProgressDialog.show();
		}
		@Override
		protected String doInBackground(String... params)
		{
			String s			= "";			
			rootFolderPath 		= rootFolderPath + File.separator;
			Decompress(fileLocalPath, rootFolderPath);
			unzip();	
			return s;
		}
		@Override
		protected void onPostExecute(String s)
		{
			apkDownloadProgressDialog.dismiss();
			if(!fileLocalPath.equalsIgnoreCase(zipRootFilePath))
			{
				File temp  		= new File(fileLocalPath);
				temp.delete();
			}
			listingOfFiles();
			
		}
	}
	public void listingOfFiles()
	{
		Intent i 		= new Intent(appContext,ZipListingActivity.class);
		i.putExtra("zipfolderPath", unzippedFolderPath);
		i.putExtra("filename", unzippedFolderName);
		
		{
			i.putExtra("backButtonText", "Back");
		}
		appContext.startActivity(i);		
	}
	private String getType(String name)
	{
		String extension 		= "";
		if(name.contains("."))
		{
			int index 			= name.lastIndexOf(".");		
			extension 			= name.substring(index+1);
			
		}
		return extension;
		
	}
	private String creatingParentDirectory(String tempFileName)
	{
		int dotIndex 			= tempFileName.lastIndexOf(".");
		String filename 		= tempFileName.substring(0, dotIndex);
		File parentFolder 		= new File(filename);		
		if( (!parentFolder.exists()) || (!parentFolder.isDirectory()))
		{
			parentFolder.mkdir();
		}
		return filename;
	}
}
