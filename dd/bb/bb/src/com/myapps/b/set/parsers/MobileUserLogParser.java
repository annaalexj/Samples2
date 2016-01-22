package com.myapps.b.set.parsers;

import java.io.InputStream;

import org.xmlpull.v1.XmlPullParser;

import com.myapps.b.set.communication.XmlParserTags;
import com.myapps.b.set.data.BaseMarker;

public class MobileUserLogParser extends BaseParser
{

	String					startTag;
	String					endTag;

	boolean					isStart		= false;
	private static boolean	loginStatus	= false;

	public MobileUserLogParser(InputStream inputStream)
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
				startTag 	= pullParser.getName();

				if (startTag.equalsIgnoreCase(XmlParserTags.TAG_MOBILE_USER_LOG_RESPONSE))
				{
					setLoginStatus(true);
					
				}

				isStart 	= true;
				break;

			case XmlPullParser.END_TAG:
				isStart 	= false;
				endTag 		= pullParser.getName();

				break;
			case XmlPullParser.TEXT:
				if (isStart) parseText();
				break;
		}
	}

	@Override
	public void parseText()
	{

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

	public boolean isLoginStatus()
	{
		return loginStatus;
	}

	public void setLoginStatus(boolean loginStatus)
	{
		this.loginStatus = loginStatus;
	}
}
