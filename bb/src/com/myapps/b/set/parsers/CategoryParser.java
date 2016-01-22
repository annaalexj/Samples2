package com.myapps.b.set.parsers;

import java.io.InputStream;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;

import com.myapps.b.set.communication.XmlParserTags;
import com.myapps.b.set.data.BaseMarker;
import com.myapps.b.set.model.Category;

public class CategoryParser extends BaseParser
{
	public static Category category 		= new Category();
	Category tempCategory 					= new Category();
	String startTag;
	String endTag;
	static String categoryType;
	boolean isStart							= false;
	private static int step 				= 0;
	public CategoryParser(InputStream inputStream)
	{
		super(inputStream);		
	}

	@Override
	public void doProcessTagByTag(int eventType) throws Exception
	{
		switch(eventType)
		{
			case XmlPullParser.START_DOCUMENT:
				break; 
			case XmlPullParser.END_DOCUMENT : 							
				break;
			case XmlPullParser.START_TAG:
				startTag 				= pullParser.getName();		
				if(startTag.equalsIgnoreCase(XmlParserTags.TAG_CATEGORYS))
				{
					category 			= new Category();		
					category.getListCategory().clear();				
				}
				else if(startTag.equalsIgnoreCase(XmlParserTags.TAG_MAIN_CATEGORY))
				{
					tempCategory 		= new Category();
				}

				else
				{
					isStart 			= true;
				}
				break;
				
			case XmlPullParser.END_TAG :				
				isStart 				= false;
				endTag 					= pullParser.getName();
				if(endTag.equalsIgnoreCase(XmlParserTags.TAG_MAIN_CATEGORY))
				{
					category.addListCategory(tempCategory);					
				}
				break;
			case XmlPullParser.TEXT :				
				if(isStart)
					parseText();
				break;		
		}	
	}

	@Override
	public void parseText()
	{
			String tagValue 			= pullParser.getText();
			if(startTag.equalsIgnoreCase(XmlParserTags.TAG_CATEGORYID))				
			{				
				tempCategory.setCategoryId(Integer.parseInt(tagValue));				
			}
			else if(startTag.equalsIgnoreCase(XmlParserTags.TAG_CATEGORYNAME))				
			{
				String tempValue 		= tagValue;
				tempCategory.setCategoryName(tempValue.replaceAll("&amp;", "&"));
			}			
			else if(startTag.equalsIgnoreCase(XmlParserTags.TAG_COUNT))				
			{
				tempCategory.setFileCount(Integer.parseInt(tagValue));
			}
			
	}

	@Override
	public BaseMarker getData()
	{	
		return null;
	}

	@Override
	public void endOfDoc() {

	}

	public ArrayList<Category> getCategoryList()
	{
		return category.getListCategory();
	}
}
