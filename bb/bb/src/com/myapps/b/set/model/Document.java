package com.myapps.b.set.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.myapps.b.set.utils.Constants;

public class Document
{
	private String assetname 			= "";
	private String linkFilename 		= "";
	private String description 			= "";
	private String filetype 			= "";
	private String filenamewithouttype 	= "";
	private Date modifiledDate 		;
	private String filePath 			= "";
	private String fileName 			= "";
	private String fileSize 			= "";
	private boolean recommendedDocStatus 		= false;

	private ArrayList<Document> arlstDocuments 	= new ArrayList<Document>();
	
	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description 			= description;
	}

	public String getFiletype()
	{
		return filetype;
	}

	public void setFiletype(String filetype)
	{
		this.filetype 				= filetype;
	}

	public ArrayList<Document> getArlstDocuments()
	{
		return arlstDocuments;
	}

	public void setArlstDocuments(ArrayList<Document> arlstDocuments)
	{
		this.arlstDocuments 		= arlstDocuments;
	}
	public void addToArlstDocuments(Document argDocument)
	{
		this.arlstDocuments.add(argDocument);
	}
	
	public String gettingFileName(String filefullname) 
	{
		String fileName 		= "";
		if(filefullname.contains("."))
		{
			int slashIndex 		= filefullname.lastIndexOf(".");
			fileName 			= filefullname.substring(0,slashIndex);
		} 
		else
		{
			fileName 			= filefullname;
		}
		return fileName;
	}

	public String getFilenamewithouttype()
	{
		String filename 			= gettingFileName(linkFilename);
		this.filenamewithouttype 	= filename;
		return filenamewithouttype;
	}

	public void setFilenamewithouttype(String filenamewithouttype)
	{
		this.filenamewithouttype 	= filenamewithouttype;	
	}

	public String getAssetname()
	{
		return assetname;
	}

	public void setAssetname(String assetname)
	{
		this.assetname 				= assetname;
	}

	public String getLinkFilename()
	{
		return linkFilename;
	}

	public void setLinkFilename(String linkFilename)
	{
		this.linkFilename 			= linkFilename;
	}

	public Date getModifiledDate()
	{
		return modifiledDate;
	}

	public void setModifiledDate(String argmodifiledDate)
	{
		//ows_Last_x0020_Modified 159;#2014-04-06 22:26:15= 
		int dateIndex				= argmodifiledDate.indexOf("#");
		argmodifiledDate 			= argmodifiledDate.substring(dateIndex+1);
		SimpleDateFormat  format 	= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
		try
		{
			this.modifiledDate 		= format.parse(argmodifiledDate);
		}
		catch (ParseException e)
		{		
			e.printStackTrace();
		}
	}

	public void setSearchModifiledDate(String argmodifiledDate)
	{
		//2014-04-06 22:26:15
		//2015-01-16T09:03:20+05:30
		//yyyy-MM-ddTHH:mm:ss+
		int dateIndex				= argmodifiledDate.indexOf("#");
		argmodifiledDate 			= argmodifiledDate.substring(dateIndex+1);
		SimpleDateFormat  format 	= new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");  
		try
		{
			this.modifiledDate 		= format.parse(argmodifiledDate);
		}
		catch (ParseException e)
		{		
			e.printStackTrace();
		}
	}
	
	
	public String getFilePath(String type)
	{
		if(type.equalsIgnoreCase(Constants.SEARCH_LABEL))
			return filePath;
		else if(type.equalsIgnoreCase("web"))
			return filePath;
		else
			return gettingFileUrl(filePath);
	}

	public void setFilePath(String filePath)
	{
		this.filePath = filePath;
	}
	
	private String gettingFileUrl(String path)
	{
		String url 		= "";
		int dateIndex	= path.indexOf("#");		
		path 			= path.substring(dateIndex+1);
		url 			= Constants.DOCUMENT_SERVER + path;
		return url;
	}

	public String getFileName()
	{
		return fileName;
	}

	public void setFileName(String fileName)
	{
		this.fileName = fileName;
	}

	public String getFileSize()
	{
		return fileSize;
	}

	public void setFileSize(String fileSize)
	{
		this.fileSize = fileSize;
	}

	public boolean getRecommendedDocStatus()
	{		
		return recommendedDocStatus;
	}

	public void setRecommendedDocStatus(String argRecommendedDocStatus)
	{
		String setRecommendedValue 			= "0";
		try
		{
			if(argRecommendedDocStatus!=null&& !argRecommendedDocStatus.equals(""))
			{
				String[] matchresult 			= argRecommendedDocStatus.split("\n");
				for(String temp : matchresult)
				{
					if(temp.contains("Tag:"))
					{
						int index 				= temp.indexOf("|");
						setRecommendedValue 	= temp.substring(index+1);
					}
				}
				
				if (setRecommendedValue != null && !setRecommendedValue.equals(""))
				{
					try
					{
						String formatedValue 		= setRecommendedValue.trim();
						if(formatedValue.contains("."))
						{						
							int index 				= formatedValue.indexOf(".");
							formatedValue 			= formatedValue.substring(0,index);
						}
						int recVale 				= Integer.parseInt(formatedValue);
						if (recVale == 1)
						{
							this.recommendedDocStatus = true;
						}
						else
						{
							this.recommendedDocStatus = false;
						}
					}
					catch (Exception e)
					{
						this.recommendedDocStatus = false;
						e.printStackTrace();
					}
				}
				else
				{
					this.recommendedDocStatus = false;
				}
			}
			else
			{
				this.recommendedDocStatus = false;
			}
		}
		catch(Exception e)
		{			
			e.printStackTrace();
			this.recommendedDocStatus = false;
		}
	}
		
}
