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
import com.myapps.b.set.utils.Constants;

public class SearchParser extends BaseParser
{
	public static ListItem	listItemSet		= new ListItem();
	Document				tempDocument	= new Document();
	private static Document	objDocument		= new Document();
	String					startTag;
	String					endTag;

	boolean					isStart			= false;
	public boolean			status			= false;

	public SearchParser(InputStream inputStream)
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
				startTag = pullParser.getName();

				if (startTag.equalsIgnoreCase(XmlParserTags.TAG_SEARCH_QUERYEX_RESPONSE))
				{
					objDocument = new Document();

				}
				else if (startTag.equalsIgnoreCase(XmlParserTags.TAG_SEARCH_QUERYEX_RESULT))
				{
					objDocument = new Document();
				}

				else if (startTag.equalsIgnoreCase(XmlParserTags.TAG_SEARCH_RESULT))
				{
					objDocument = new Document();
				}

				else if (startTag.equalsIgnoreCase(XmlParserTags.TAG_SEARCH_RELEVANT_RESULTS))
				{
					status 			= true;
					tempDocument 	= new Document();
				}

				else
				{
					isStart 		= true;
				}
				break;

			case XmlPullParser.END_TAG:
				isStart 	= false;
				endTag 		= pullParser.getName();

				if (endTag.equalsIgnoreCase(XmlParserTags.TAG_SEARCH_RELEVANT_RESULTS))
				{
					tempDocument.setFiletype(Constants.SEARCH_LABEL);
					objDocument.addToArlstDocuments(tempDocument);
				}

				break;
			case XmlPullParser.TEXT:
				if (isStart)
				{
					parseText();
				}
				break;

		}
	}

	@Override
	public void parseText()
	{
		String tagValue = pullParser.getText();
		if (startTag.equalsIgnoreCase(XmlParserTags.TAG_TITLE))
		{
			tempDocument.setLinkFilename(tagValue);
		}
		else if (startTag.equalsIgnoreCase(XmlParserTags.TAG_SEARCH_HIGHLIGHT_SUMMARY))
		{
			tempDocument.setDescription(tagValue);
		}
		else if (startTag.equalsIgnoreCase(XmlParserTags.TAG_PATH))
		{
			tempDocument.setFilePath(tagValue);
		}
		else if (startTag.equalsIgnoreCase(XmlParserTags.TAG_SEARCH_DATE))
		{
			tempDocument.setSearchModifiledDate(tagValue);
		}
		else if (startTag.equalsIgnoreCase(XmlParserTags.TAG_SIZE))
		{
			tempDocument.setFileSize(tagValue);
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

	public ArrayList<Document> getDocumentList()
	{

		ArrayList<Document> lstDocument = this.objDocument.getArlstDocuments();
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
