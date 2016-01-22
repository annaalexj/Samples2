package com.myapps.b.set.parsers;

import java.io.InputStream;

import org.xmlpull.v1.XmlPullParser;

import com.myapps.b.set.communication.HttpRequestConstants;
import com.myapps.b.set.communication.XmlParserTags;
import com.myapps.b.set.data.BaseMarker;

public class LoginListParser extends BaseParser
{

	String startTag;
	String endTag;

	boolean isStart						= false;
	private static boolean loginStatus 	= false;
	public LoginListParser(InputStream inputStream)
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
				startTag 		= pullParser.getName();	
				isStart			= true;
				break;
				
			case XmlPullParser.END_TAG :				
				isStart 		= false;
				endTag 			= pullParser.getName();
				
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
			String tagValue 	= pullParser.getText();
			if(startTag.equalsIgnoreCase(XmlParserTags.TAG_HTML_PARA))				
			{				
				if(tagValue.equalsIgnoreCase(HttpRequestConstants.PARM_HTML_PARA))
				{
					setLoginStatus(true);				
				}
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

	public boolean isLoginStatus()
	{
		return loginStatus;
	}

	public void setLoginStatus(boolean loginStatus)
	{
		this.loginStatus = loginStatus;
	}
}
