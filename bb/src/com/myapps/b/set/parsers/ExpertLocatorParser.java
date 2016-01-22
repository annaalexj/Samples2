package com.myapps.b.set.parsers;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.xmlpull.v1.XmlPullParser;

import com.myapps.b.set.communication.XmlParserTags;
import com.myapps.b.set.data.BaseMarker;
import com.myapps.b.set.model.Expert;
import com.myapps.b.set.model.ListItem;

public class ExpertLocatorParser extends BaseParser
{
	public static ListItem	listItemSet	= new ListItem();
	Expert					tempExpert	= new Expert();
	private static Expert	objExpert	= new Expert();
	String					startTag;
	String					endTag;

	boolean					isStart		= false;
	public boolean			status		= false;

	public ExpertLocatorParser(InputStream inputStream)
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

				if (startTag.equalsIgnoreCase(XmlParserTags.TAG_GET_LIST_ITEMS_RESPONSE))
				{
					objExpert = new Expert();

				}
				else if (startTag.equalsIgnoreCase(XmlParserTags.TAG_GET_LIST_ITEMS_RESULT))
				{
					objExpert = new Expert();
				}

				else if (startTag.equalsIgnoreCase(XmlParserTags.TAG_LIST_ITEMS))
				{
					objExpert = new Expert();
					// status = true;
				}
				else if (startTag.equalsIgnoreCase(XmlParserTags.TAG_DATA))
				{
				}
				else if (startTag.equalsIgnoreCase(XmlParserTags.TAG_ROW))
				{

					String expertname 		= pullParser.getAttributeValue(null, "ows_Title");

					if (expertname != null)
					{

						String team 			= pullParser.getAttributeValue(null, "ows_Team");

						String group 			= pullParser.getAttributeValue(null, "ows_Group");
						String modifiedDate 	= pullParser.getAttributeValue(null, "ows_Modified");
						String onsiteOrOffshore = pullParser.getAttributeValue(null, "ows_Onsite_x002f_Offshore");
						String level 			= pullParser.getAttributeValue(null, "ows__Level");

						String mobile 			= pullParser.getAttributeValue(null, "ows_Mobile_x0020_Number");
						String vnet 			= pullParser.getAttributeValue(null, "ows_Vnet");
						String emp_id 			= pullParser.getAttributeValue(null, "ows_Employee_x0020_Id");
						String city 			= pullParser.getAttributeValue(null, "ows_City");

						tempExpert 				= new Expert();
						tempExpert.setExpertName(expertname);
						tempExpert.setCity(city);
						tempExpert.setEmployeeId(emp_id);
						tempExpert.setGroupName(group);
						tempExpert.setMobile(mobile);
						tempExpert.setTeamName(team);
						tempExpert.setOnsiteOrOffshore(onsiteOrOffshore);
						objExpert.addToArlstExpert(tempExpert);
					}

					status 			= true;
				}

				else
				{
					isStart 		= true;
				}
				break;

			case XmlPullParser.END_TAG:
				isStart 			= false;
				endTag 				= pullParser.getName();

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
		String tagValue		 = pullParser.getText();

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

	public ArrayList<Expert> getExpertList()
	{

		ArrayList<Expert> lstExperts = this.objExpert.getArlstExpert();
		Collections.sort(lstExperts, new Comparator<Expert>()
		{
			public int compare(Expert o1, Expert o2)
			{
				if (o1.getExpertName() == null || o2.getExpertName() == null) return 0;

				return o1.getExpertName().compareTo(o2.getExpertName());
			}
		});

		return lstExperts;
	}

}
