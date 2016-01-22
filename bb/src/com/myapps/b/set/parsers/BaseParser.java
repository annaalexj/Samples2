package com.myapps.b.set.parsers;

import java.io.InputStream;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.myapps.b.set.data.BaseMarker;

public abstract class BaseParser {

	protected XmlPullParser pullParser;
	private InputStream resStream;

	public BaseParser(InputStream inputStream) {
		this.resStream 						= inputStream;
		parser();
	}

	private void parser() {
		try {
			XmlPullParserFactory factory 	= XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);
			pullParser 						= factory.newPullParser();
			pullParser.setInput(resStream, "UTF-8");
			int eventType 					= pullParser.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				doProcessTagByTag(eventType);
				eventType 					= pullParser.next();
			}
			endOfDoc();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public abstract void doProcessTagByTag(int eventType) throws Exception;

	public abstract void parseText();
	
	public abstract void endOfDoc();

	public abstract BaseMarker getData();

}

