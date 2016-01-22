package com.myapps.b.set.parsers;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.xmlpull.v1.XmlPullParser;

import com.myapps.b.set.communication.XmlParserTags;
import com.myapps.b.set.data.BaseMarker;
import com.myapps.b.set.model.Document;
import com.myapps.b.set.model.ListItem;
import com.myapps.b.set.screens.DrawerHome;

public class GetListItemsParser extends BaseParser
{
	public static ListItem	listItemSet		= new ListItem();
	Document				tempDocument	= new Document();
	private static Document	objDocument		= new Document();
	String					startTag;
	String					endTag;

	boolean					isStart			= false;
	public boolean			status			= false;
	String					mainCatName		= "";

	public GetListItemsParser(InputStream inputStream, String argCat)
	{
		super(inputStream);
		mainCatName 		= argCat;
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
				startTag = pullParser.getName();

				if (startTag.equalsIgnoreCase(XmlParserTags.TAG_GET_LIST_ITEMS_RESPONSE))
				{
					objDocument = new Document();

				}
				else if (startTag.equalsIgnoreCase(XmlParserTags.TAG_GET_LIST_ITEMS_RESULT))
				{
					objDocument = new Document();
				}

				else if (startTag.equalsIgnoreCase(XmlParserTags.TAG_LIST_ITEMS))
				{
					objDocument = new Document();				
				}
				else if (startTag.equalsIgnoreCase(XmlParserTags.TAG_DATA))
				{
				}
				else if (startTag.equalsIgnoreCase(XmlParserTags.TAG_ROW))
				{

					String fileType = pullParser.getAttributeValue(null, "ows_DocIcon");
					String name_url = pullParser.getAttributeValue(null, "ows_Name_Url");

					if (fileType != null)
					{
						String assetname 	= pullParser.getAttributeValue(null, "ows_Asset_x0020_Name");
						String fileDes 		= pullParser.getAttributeValue(null, "ows_Description0");

						String linkFilename = pullParser.getAttributeValue(null, "ows_LinkFilename");
						String modifiedDate = pullParser.getAttributeValue(null, "ows_Last_x0020_Modified");
						String filePath 	= pullParser.getAttributeValue(null, "ows_FileRef");
						String fileSize 	= pullParser.getAttributeValue(null, "ows_FileSizeDisplay");
						String recommendedDocStatus = pullParser.getAttributeValue(null, "ows_MetaInfo");

						
						if (DrawerHome.oppurtunityTagValue.equalsIgnoreCase("Proposal Tracker"))
						{
							String oppurtunityname 	= pullParser.getAttributeValue(null, "ows_Opportunity_x0020_Name");
							String comments 		= pullParser.getAttributeValue(null, "ows_Comments");
							fileDes 				= comments;
							linkFilename 			= oppurtunityname;
						}
						else if (DrawerHome.oppurtunityTagValue.equalsIgnoreCase("Case Studies"))
						{

							String oppurtunityname 	= pullParser.getAttributeValue(null, "ows_Title");
							if (oppurtunityname == null || oppurtunityname.equals(""))
							{
								oppurtunityname 	= pullParser.getAttributeValue(null, "ows_Case_x0020_Study_x0020_Name");
							}
							assetname 				= oppurtunityname;
						}
						
						tempDocument 				= new Document();
						tempDocument.setAssetname(assetname);
						tempDocument.setDescription(fileDes);
						tempDocument.setFiletype(fileType);
						tempDocument.setLinkFilename(linkFilename);
						tempDocument.setModifiledDate(modifiedDate);
						tempDocument.setFilePath(filePath);
						tempDocument.setFileSize(fileSize);
						tempDocument.setRecommendedDocStatus(recommendedDocStatus);
						objDocument.addToArlstDocuments(tempDocument);
					}
					else if (name_url != null)
					{
						String linkFilename 		= pullParser.getAttributeValue(null, "ows_Name_Url");
						String fileDes 				= pullParser.getAttributeValue(null, "ows_Description");
						String filePath 			= pullParser.getAttributeValue(null, "ows_url");
						String modifiedDate 		= pullParser.getAttributeValue(null, "ows_Modified");

						tempDocument 				= new Document();
						tempDocument.setLinkFilename(linkFilename);
						tempDocument.setDescription(fileDes);
						tempDocument.setFilePath(filePath);
						tempDocument.setFiletype("web");
						tempDocument.setModifiledDate(modifiedDate);

						objDocument.addToArlstDocuments(tempDocument);
					}
					status = true;
				}

				else
				{
					isStart = true;
				}
				break;

			case XmlPullParser.END_TAG:
				isStart 	= false;
				endTag 		= pullParser.getName();

				if (endTag.equalsIgnoreCase(XmlParserTags.TAG_ROW))
				{

				}

				if (endTag.equalsIgnoreCase(XmlParserTags.TAG_LIST_ITEMS))
				{

				}
				break;
			case XmlPullParser.TEXT:
				if (isStart)
				{

				}
				break;

		}
	}

	@Override
	public void parseText()
	{
		String tagValue 		= pullParser.getText();

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

	public ArrayList<Document> getDocumentList()
	{

		ArrayList<Document> lstDocument 		= this.objDocument.getArlstDocuments();
		Collections.sort(lstDocument, new Comparator<Document>()
		{
			public int compare(Document o1, Document o2)
			{
				if (o1.getModifiledDate() == null || o2.getModifiledDate() == null) return 0;
				return o2.getModifiledDate().compareTo(o1.getModifiledDate());
			}
		});

		return lstDocument;
	}

}
