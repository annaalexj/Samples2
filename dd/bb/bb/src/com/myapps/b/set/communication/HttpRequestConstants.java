package com.myapps.b.set.communication;

import java.lang.reflect.Field;

import android.os.Build;

public class HttpRequestConstants {
	
	/**
	 * BFS STARTS
	 */
	public static final String REQUEST_XML_START_TAG = 	"<?xml version='1.0' encoding='utf-8'?>" +
			"<soap:Envelope xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' xmlns:xsd='http://www.w3.org/2001/XMLSchema' xmlns:soap='http://schemas.xmlsoap.org/soap/envelope/'><soap:Body>";	        
	public static final String REQUEST_XML_END_TAG = "</soap:Body></soap:Envelope>";
	
	public static final String REQUEST_SEARCH_QUERY_START_TAG = "<queryXml><![CDATA[<QueryPacket xmlns='urn:Microsoft.Search.Query' Revision='1000'>"+
     "<Query domain='QDomain'>" +
     "<SupportedFormats>" +
     "<Format>urn:Microsoft.Search.Response.Document.Document</Format>" +
     "</SupportedFormats>" +
     "<Range><Count>50</Count></Range>" +
     "<Context><QueryText language='en-US' type='STRING'>";
	
	public static final String REQUEST_SEARCH_QUERY_TEXT_START_TAG ="IsDocument:true SCOPE:\"All Sites\" ";
	public static final String REQUEST_SEARCH_QUERY_TEXT_END_TAG =" SITE:\"https://bfs-set.c.com\"";
	public static final String REQUEST_SEARCH_QUERY_TEXT = "anz.zip";
	public static final String REQUEST_SEARCH_QUERY_END_TAG = "</QueryText></Context>" +
			 "</Query>" +
			 "</QueryPacket>]]></queryXml>";
	
	public static final int REQ_LOGIN = 500;
	public static final int REQ_SHAREPOINT_SERVER_ACCESS = 501;
	public static final int REQ_LOGIN_MODE = 502;
	public static final int REQ_GET_LIST = 503;
	public static final int REQ_LOGIN_LIST = 504;
	public static final int REQ_GETLISTITEMS = 505;
	
	//HOME
	public static final int REQ_ADMIN_SET_DOCUMENTS = 506;
	public static final int REQ_ALL_SET_RECOMMANDED = 507;
	public static final int REQ_NEWSLETTER = 508;
	public static final int REQ_EXPERT_LOCATOR = 509;
	public static final int REQ_MOBILE_APP = 510;
	public static final int REQ_DASHBOARD = 511;
	
	//Drawer
	public static final int REQ_GET_SUB_CATEGORIES = 512;
	public static final int REQ_GET_SUB_CATEGORIES1 = 513;
	
	//Search
	public static final int REQ_SEARCH_QUERY = 514;
	//mobile user log
	public static final int REQ_MOBILE_USER_LOG = 515;
	
	//Admin SET Documents
	//All SET Recommanded
	//Newsletter
	// Expert_Locator
	//Mobile App
	
	
	
	public static final String PARM_LOGIN_ACTION = "Login";
	public static final String PARM_GET_LIST_ACTION = "GetList";
	public static final String PARM_LOGIN_MODE = "Mode";
	
	public static final String PARM_USER = "username";
	public static final String PARM_PWD = "password";
	public static final String PARM_LIST_NAME = "listName";
	public static final String PARM_HTML_PARA = "Lists"; 
	
	//GetListitems params
	public static final String PARM_GETLISTITEMS_ACTION = "GetListItems"; 
	public static final String PARM_VIEW_NAME = "viewName"; 
	public static final String PARM_QUERY = "query"; 
	public static final String PARM_VIEW_FIELDS = "viewFields"; 
	public static final String PARM_ROW_LIMIT = "rowLimit"; 
	public static final String PARM_QUERY_OPTIONS = "queryOptions"; 
	public static final String PARM_WEB_ID = "webID"; 
	
	//SEARCH
	public static final String PARM_QUERY_EX = "QueryEx"; 
	
	
	public static final int ROW_LIMIT = 1000; 
	

	
	
	//HOME
	public static final String ACTION_ADMIN_SET_DOCUMENTS = "Admin SET Documents";
	public static final String ACTION_ALL_SET_RECOMMANDED = "All SET Recommanded";
	public static final String ACTION_NEWSLETTER = "Newsletter";
	public static final String ACTION_EXPERT_LOCATOR = "Expert_Locator";
	public static final String ACTION_MOBILE_APP = "Mobile App";
	
	
	public static final String PARM_ADD_USER_LOG_ACTION = "AddUserLog";
	public static final String PARM_USER_ID = "Userid";
	
	/**
	 * BFS ENDS
	 */
	
	
	private static String getDeviceOsName() {

		String oSName = "";

		Field[] fields = Build.VERSION_CODES.class.getFields();
		for (Field field : fields) {
			String fieldName = field.getName();
			int fieldValue = -1;

			try {
				fieldValue = field.getInt(new Object());
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (NullPointerException e) {
				e.printStackTrace();
			}

			if (fieldValue == Build.VERSION.SDK_INT) {
				oSName =fieldName;
			}
		}
		return oSName;
	}

}

