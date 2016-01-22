package com.myapps.b.set.parsers;

import java.io.InputStream;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;

import com.myapps.b.set.communication.XmlParserTags;
import com.myapps.b.set.data.BaseMarker;
import com.myapps.b.set.model.Category;
import com.myapps.b.set.utils.Constants;

public class SubCategoryParser extends BaseParser
{
	public static Category	category				= new Category();
	Category				tempCategory			= new Category();
	Category				tempMainCategory		= new Category();
	String					startTag;
	String					endTag;
	static String			categoryType;
	boolean					isStart					= false;

	boolean					isSubCatStart			= false;
	boolean					isInnerCatStart			= false;

	private static int		step					= 0;
	int						type					= 0;
	String					topCategoryName			= "";
	String					superTopCategoryName	= "";

	public SubCategoryParser(InputStream inputStream)
	{
		super(inputStream);
	}

	@Override
	public void doProcessTagByTag(int eventType) throws Exception
	{
		switch (eventType)
		{
			case XmlPullParser.START_DOCUMENT:
				break;
			case XmlPullParser.END_DOCUMENT:
				break;
			case XmlPullParser.START_TAG:
				startTag 				= pullParser.getName();
				if (startTag.equalsIgnoreCase(XmlParserTags.TAG_CATEGORYS))
				{
					category 			= new Category();
					category.getListCategory().clear();
					tempCategory 		= new Category();
					tempCategory.setCategoryName(Constants.DRAWER_HOME);
					tempCategory.setCategoryType(Constants.MAIN_CATEGORY);

					category.addMainListCategory(tempCategory);

				}
				else if (startTag.equalsIgnoreCase(XmlParserTags.TAG_MAIN_CATEGORY))
				{

					tempCategory 		= new Category();
					type 				= Constants.MAIN_CATEGORY;
					isSubCatStart 		= false;
					isInnerCatStart 	= false;
				}
				else if (startTag.equalsIgnoreCase(XmlParserTags.TAG_SUB_CATEGORY))
				{

					if (type == Constants.MAIN_CATEGORY)
					{					
						category.addMainListCategory(tempCategory);
					}
					isSubCatStart 		= true;
					isInnerCatStart 	= false;
					tempCategory 		= new Category();
					type 				= Constants.SUB_CATEGORY;

				}
				else if (startTag.equalsIgnoreCase(XmlParserTags.TAG_INNER_CATEGORY))
				{
					if (type == Constants.SUB_CATEGORY)
					{
						category.addSubListCategory(tempCategory);
					}

					isInnerCatStart 	= true;
					tempCategory 		= new Category();
					type 				= Constants.INNER_CATEGORY;
				}

				else
				{
					isStart 			= true;
				}
				break;

			case XmlPullParser.END_TAG:
				isStart 				= false;
				endTag 					= pullParser.getName();

				if (endTag.equalsIgnoreCase(XmlParserTags.TAG_INNER_CATEGORY))
				{
					category.addInnerListCategory(tempCategory);
				}
				else if (endTag.equalsIgnoreCase(XmlParserTags.TAG_SUB_CATEGORY))
				{
					if (type == Constants.SUB_CATEGORY) category.addSubListCategory(tempCategory);
				}
				else if (endTag.equalsIgnoreCase(XmlParserTags.TAG_MAIN_CATEGORY))
				{
					if (type == Constants.MAIN_CATEGORY) category.addMainListCategory(tempCategory);
				}
				else if (endTag.equalsIgnoreCase(XmlParserTags.TAG_CATEGORYS))
				{
					tempCategory 	= new Category();
					tempCategory.setCategoryName(Constants.SEARCH_LABEL);
					tempCategory.setCategoryType(Constants.MAIN_CATEGORY);
					category.addMainListCategory(tempCategory);

					tempCategory 	= new Category();
					tempCategory.setCategoryName(Constants.FEEDBACK_LABEL);
					tempCategory.setCategoryType(Constants.MAIN_CATEGORY);
					category.addMainListCategory(tempCategory);

					tempCategory 	= new Category();
					tempCategory.setCategoryName(Constants.SIGN_OUT);
					tempCategory.setCategoryType(Constants.MAIN_CATEGORY);
					category.addMainListCategory(tempCategory);
				}

				break;
			case XmlPullParser.TEXT:
				if (isStart) parseText();
				break;
		}
	}

	@Override
	public void parseText()
	{
		String tagValue 		= pullParser.getText();
		if (startTag.equalsIgnoreCase(XmlParserTags.TAG_CATEGORYID))
		{
			tempCategory.setCategoryId(Integer.parseInt(tagValue));
		}
		else if (startTag.equalsIgnoreCase(XmlParserTags.TAG_CATEGORYNAME))
		{			
			String tempValue 			= tagValue;
			String formattedtempValue 	= tempValue.replaceAll("&amp;", "&");
			tempCategory.setCategoryName(formattedtempValue);
			tempCategory.setCategoryType(type);
			if (type == 0)
			{
				superTopCategoryName 	= formattedtempValue;
			}
			if (type == 1)
			{
				tempCategory.setSuperTopCategoryName(superTopCategoryName);
				topCategoryName 		= formattedtempValue;
			}
			if (type == 2)
			{
				tempCategory.setSuperTopCategoryName(superTopCategoryName);
				tempCategory.setTopCategoryName(topCategoryName);
			}
		}
		else if (startTag.equalsIgnoreCase(XmlParserTags.TAG_COUNT))
		{
			tempCategory.setFileCount(Integer.parseInt(tagValue));
		}
		else if (startTag.equalsIgnoreCase(XmlParserTags.TAG_FOLDER_NAME))
		{
			tempCategory.setFolderName(tagValue);

		}

	}

	@Override
	public BaseMarker getData()
	{
		return null;
	}

	@Override
	public void endOfDoc()
	{

	}

	public ArrayList<Category> getCategoryList()
	{
		return category.getListCategory();
	}

	public Category getMainCategory()
	{
		return category;
	}

}
