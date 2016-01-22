package com.myapps.b.set.parsers;

import java.io.InputStream;

import org.xmlpull.v1.XmlPullParser;

import com.myapps.b.set.communication.HttpResponseTags;
import com.myapps.b.set.data.BaseMarker;
import com.myapps.b.set.model.Ldap;

public class LdapParser extends BaseParser
{
	private String mstartTagName;
	private boolean isStart;
	private Ldap model;
	
	public LdapParser(InputStream inputStream) 
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
			String localName 			= pullParser.getName();
			if (localName.equals(HttpResponseTags.RESPONSE_LDAP_AUTHENTICATION)) 
			{
				model 					= new Ldap();
			}
			mstartTagName 				= localName;
			isStart 					= true;
			break;
		case XmlPullParser.END_TAG:
			localName 					= pullParser.getName();
			if (localName.equals(HttpResponseTags.RESPONSE_LDAP_AUTHENTICATION)) 
			{
				//model = null;
			}
			isStart 					= false;
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
		if (mstartTagName.equals(HttpResponseTags.RESPONSE_ISAUTHENTICATED)) 
		{
			String isAuthenticated = pullParser.getText();
			if (isAuthenticated != null) 
			{
				if(isAuthenticated.equals("true"))
					model.setAuthenticated(true);
			}
		}
		else if (mstartTagName.equals(HttpResponseTags.RESPONSE_ERRORMESSAGE)) 
		{
			model.setErrorMessage(pullParser.getText());
		} 
		else if (mstartTagName.equals(HttpResponseTags.RESPONSE_LDAP_NAME))
		{
			model.setName(pullParser.getText());
		}		
	}

	@Override
	public BaseMarker getData() 
	{
		return model;
	}

	@Override
	public void endOfDoc() {
		
		
	}

}
