package com.myapps.b.set.communication;

import java.io.UnsupportedEncodingException;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;

import android.util.Log;

import com.myapps.b.set.BaseAppExecutor;
import com.myapps.b.set.utils.Constants;


/**
 * Class HttpRequestMaker used to create webservice request.
 * @author 324520
 *
 */
public class HttpRequestMaker
{
	private static final String TAG 			= BaseAppExecutor.class.getSimpleName();

	public static HttpRequestBase getRequest(int code, Object... objects)
	{
		HttpPost request 						= new HttpPost(Constants.BASE_DATA_URL);
		request.setHeader("Content-Type", "text/xml; charset=utf-8");

		switch (code)
		{

			/** Request for hitting BFS Server **/
			case HttpRequestConstants.REQ_SHAREPOINT_SERVER_ACCESS:

				HttpGet requestSharepoint 					= new HttpGet(Constants.SHAREPOINT_SERVER);
				return requestSharepoint;
				
			/** Request for hitting BASE URL FOR LOGIN **/
			case HttpRequestConstants.REQ_LOGIN_LIST:
			
				HttpGet requestLoginList 					= new HttpGet(Constants.BASE_DATA_URL);
				return requestLoginList;
				
			/** Request for LOGIN Authentication **/
			case HttpRequestConstants.REQ_LOGIN:
				HttpPost loginRequest 			= new HttpPost(Constants.BASE_LOGIN_URL);
				loginRequest.setHeader("Content-Type", "text/xml; charset=utf-8");
				String requestLoginXmlContent 	= "<"
												+ HttpRequestConstants.PARM_LOGIN_ACTION + " xmlns='"
												+ Constants.SOAP_ACTION_BASE_URL + "'>" + "<"
												+ HttpRequestConstants.PARM_USER + ">"+ (String) objects[0] + "</"+ HttpRequestConstants.PARM_USER + ">" + "<"	
												+ HttpRequestConstants.PARM_PWD + ">" + (String) objects[1] + "</" + HttpRequestConstants.PARM_PWD + ">" +"</"
												+ HttpRequestConstants.PARM_LOGIN_ACTION + ">";
							
				Log.i(TAG, "XML REQ: " + requestLoginXmlContent);
				String requestXml 				= HttpRequestConstants.REQUEST_XML_START_TAG
												+ requestLoginXmlContent
												+ HttpRequestConstants.REQUEST_XML_END_TAG;

				StringEntity reqEntity 			= null;
				try
				{
					reqEntity 					= new StringEntity(requestXml);
				}
				catch (UnsupportedEncodingException e1)
				{
					e1.printStackTrace();
				}
				loginRequest.setHeader("SOAPAction", Constants.SOAP_ACTION_BASE_URL+ HttpRequestConstants.PARM_LOGIN_ACTION);
				loginRequest.setEntity(reqEntity);
				return loginRequest;
	
			/** Request for LOGIN Authentication **/
			case HttpRequestConstants.REQ_LOGIN_MODE:
				HttpPost loginModeRequest 		= new HttpPost(Constants.BASE_LOGIN_URL);
				loginModeRequest.setHeader("Content-Type", "text/xml; charset=utf-8");
				String requestLoginMODEXmlContent 	= "<"
												+ HttpRequestConstants.PARM_LOGIN_MODE + " xmlns='"
												+ Constants.SOAP_ACTION_BASE_URL + "'/>" ;
							
				Log.i(TAG, "XML REQ: " + requestLoginMODEXmlContent);
				String requestModeXml 			= HttpRequestConstants.REQUEST_XML_START_TAG
												+ requestLoginMODEXmlContent
												+ HttpRequestConstants.REQUEST_XML_END_TAG;

				StringEntity reqModeEntity 		= null;
				try
				{
					reqModeEntity 				= new StringEntity(requestModeXml);
				}
				catch (UnsupportedEncodingException e1)
				{
					e1.printStackTrace();
				}
				loginModeRequest.setHeader("SOAPAction", Constants.SOAP_ACTION_BASE_URL+ HttpRequestConstants.PARM_LOGIN_MODE);
				loginModeRequest.setEntity(reqModeEntity);
				return loginModeRequest;
	

			/** Request for Listing documents **/
			case HttpRequestConstants.REQ_GET_LIST:			
				String requestGetListXmlContent = "<"
												+ HttpRequestConstants.PARM_GET_LIST_ACTION + " xmlns='"
												+ Constants.SOAP_ACTION_BASE_URL + "'>" + "<"												
												+ HttpRequestConstants.PARM_LIST_NAME + ">"+ (String) objects[0] + "</"+ HttpRequestConstants.PARM_LIST_NAME + ">"+"</" 
												+ HttpRequestConstants.PARM_GET_LIST_ACTION + ">";
							
				Log.i(TAG, "XML REQ: " + requestGetListXmlContent);
				String requestGetListXml 		= HttpRequestConstants.REQUEST_XML_START_TAG
												+ requestGetListXmlContent
												+ HttpRequestConstants.REQUEST_XML_END_TAG;

				StringEntity reqGetListEntity 	= null;
				try
				{
					reqGetListEntity 			= new StringEntity(requestGetListXml);
				}
				catch (UnsupportedEncodingException e1)
				{
					e1.printStackTrace();
				}
				request.setHeader("SOAPAction", Constants.SOAP_ACTION_BASE_URL+ HttpRequestConstants.PARM_GET_LIST_ACTION);
				request.setEntity(reqGetListEntity);
				return request;
				
				/** Request for Listing documents **/
			case HttpRequestConstants.REQ_GETLISTITEMS:			
				String requestGetListItemsXmlContent = "<"
												+ HttpRequestConstants.PARM_GETLISTITEMS_ACTION + " xmlns='"
												+ Constants.SOAP_ACTION_BASE_URL + "'>" + "<"												
												+ HttpRequestConstants.PARM_LIST_NAME + ">"+ (String) objects[0] + "</"+ HttpRequestConstants.PARM_LIST_NAME + ">"+"<" 
												//+ HttpRequestConstants.PARM_VIEW_NAME + ">"+ (String) objects[1] + "</"+ HttpRequestConstants.PARM_VIEW_NAME + ">"+"<" 
												+ HttpRequestConstants.PARM_QUERY + ">"+ (String) objects[2] + "</"+ HttpRequestConstants.PARM_QUERY + ">"+"<" 
												//+ HttpRequestConstants.PARM_VIEW_FIELDS + ">"+ (String) objects[3] + "</"+ HttpRequestConstants.PARM_VIEW_FIELDS + ">"+"<" 
												+ HttpRequestConstants.PARM_ROW_LIMIT + ">"+ (String) objects[4] + "</"+ HttpRequestConstants.PARM_ROW_LIMIT + ">"+"<" 
												+ HttpRequestConstants.PARM_QUERY_OPTIONS + ">"+ (String) objects[5] + "</"+ HttpRequestConstants.PARM_QUERY_OPTIONS + ">"
												//+"<" 
												//+ HttpRequestConstants.PARM_WEB_ID + ">"+ (String) objects[6] + "</"+ HttpRequestConstants.PARM_WEB_ID + ">"
												+"</" 
												+ HttpRequestConstants.PARM_GETLISTITEMS_ACTION + ">";
							
				
				String requestGetListItemsXml 		= HttpRequestConstants.REQUEST_XML_START_TAG
												+ requestGetListItemsXmlContent
												+ HttpRequestConstants.REQUEST_XML_END_TAG;
				Log.i(TAG, "XML REQ: " + requestGetListItemsXml);
				StringEntity reqGetListItemsEntity 	= null;
				try
				{
					reqGetListItemsEntity 			= new StringEntity(requestGetListItemsXml);
				}
				catch (UnsupportedEncodingException e1)
				{
					e1.printStackTrace();
				}
				request.setHeader("SOAPAction", Constants.SOAP_ACTION_BASE_URL+ HttpRequestConstants.PARM_GETLISTITEMS_ACTION);
				request.setEntity(reqGetListItemsEntity);
				return request;
				
			case HttpRequestConstants.REQ_DASHBOARD:			
				String requestHomeXmlContent = "<"
												+ HttpRequestConstants.PARM_GETLISTITEMS_ACTION + " xmlns='"
												+ Constants.SOAP_ACTION_BASE_URL + "'>" + "<"												
												+ HttpRequestConstants.PARM_LIST_NAME + ">"+ (String) objects[0] + "</"+ HttpRequestConstants.PARM_LIST_NAME + ">"
												+"</" 
												+ HttpRequestConstants.PARM_GETLISTITEMS_ACTION + ">";
	
				String requestHomeXml		= HttpRequestConstants.REQUEST_XML_START_TAG
												+ requestHomeXmlContent
												+ HttpRequestConstants.REQUEST_XML_END_TAG;
				Log.i(TAG, "XML REQ: " + requestHomeXml);
				StringEntity reqHomeEntity 	= null;
				try
				{
					reqHomeEntity 			= new StringEntity(requestHomeXml);
				}
				catch (UnsupportedEncodingException e1)
				{
					e1.printStackTrace();
				}
				request.setHeader("SOAPAction", Constants.SOAP_ACTION_BASE_URL+ HttpRequestConstants.PARM_GETLISTITEMS_ACTION);
				request.setEntity(reqHomeEntity);
				return request;
				
			case HttpRequestConstants.REQ_MOBILE_APP:			
				String requestMobileAppXmlContent = "<"
												+ HttpRequestConstants.PARM_GETLISTITEMS_ACTION + " xmlns='"
												+ Constants.SOAP_ACTION_BASE_URL + "'>" + "<"												
												+ HttpRequestConstants.PARM_LIST_NAME + ">"+ (String) objects[0] + "</"+ HttpRequestConstants.PARM_LIST_NAME + ">"+"<" 
												+ HttpRequestConstants.PARM_ROW_LIMIT + ">"+ (String) objects[1] + "</"+ HttpRequestConstants.PARM_ROW_LIMIT + ">"+"<" 
												+ HttpRequestConstants.PARM_QUERY_OPTIONS + ">"+ (String) objects[2] + "</"+ HttpRequestConstants.PARM_QUERY_OPTIONS + ">"
												+"</" 
												+ HttpRequestConstants.PARM_GETLISTITEMS_ACTION + ">";
			
				String requestMobileAppXml		= HttpRequestConstants.REQUEST_XML_START_TAG
												+ requestMobileAppXmlContent
												+ HttpRequestConstants.REQUEST_XML_END_TAG;
				Log.i(TAG, "XML REQ: " + requestMobileAppXml);
				StringEntity reqMobileAppEntity 	= null;
				try
				{
					reqMobileAppEntity 			= new StringEntity(requestMobileAppXml);
				}
				catch (UnsupportedEncodingException e1)
				{
					e1.printStackTrace();
				}
				request.setHeader("SOAPAction", Constants.SOAP_ACTION_BASE_URL+ HttpRequestConstants.PARM_GETLISTITEMS_ACTION);
				request.setEntity(reqMobileAppEntity);
				return request;
			case HttpRequestConstants.REQ_GET_SUB_CATEGORIES:			
				String requestSubXmlContent = "<"
												+ HttpRequestConstants.PARM_GETLISTITEMS_ACTION + " xmlns='"
												+ Constants.SOAP_ACTION_BASE_URL + "'>" + "<"												
												+ HttpRequestConstants.PARM_LIST_NAME + ">"+ (String) objects[0] + "</"+ HttpRequestConstants.PARM_LIST_NAME + ">"+"<" 
												+ HttpRequestConstants.PARM_QUERY + ">"+ (String) objects[1] + "</"+ HttpRequestConstants.PARM_QUERY + ">"+"<" 
												+ HttpRequestConstants.PARM_ROW_LIMIT + ">"+ (String) objects[2] + "</"+ HttpRequestConstants.PARM_ROW_LIMIT + ">"+"<" 
												+ HttpRequestConstants.PARM_QUERY_OPTIONS + ">"+ (String) objects[3] + "</"+ HttpRequestConstants.PARM_QUERY_OPTIONS + ">"
												+"</" 
												+ HttpRequestConstants.PARM_GETLISTITEMS_ACTION + ">";
	
				String requestSubXml		= HttpRequestConstants.REQUEST_XML_START_TAG
												+ requestSubXmlContent
												+ HttpRequestConstants.REQUEST_XML_END_TAG;
				Log.i(TAG, "XML REQ: " + requestSubXml);
				StringEntity reqSubEntity 	= null;
				try
				{
					reqSubEntity 			= new StringEntity(requestSubXml);
				}
				catch (UnsupportedEncodingException e1)
				{
					e1.printStackTrace();
				}
				request.setHeader("SOAPAction", Constants.SOAP_ACTION_BASE_URL+ HttpRequestConstants.PARM_GETLISTITEMS_ACTION);
				request.setEntity(reqSubEntity);
				return request;
					
				
			case HttpRequestConstants.REQ_GET_SUB_CATEGORIES1:
				
				String s = "<soapenv:Envelope xmlns:soapenv='http://schemas.xmlsoap.org/soap/envelope/' xmlns:soap='http://schemas.microsoft.com/sharepoint/soap/'><soapenv:Header/><soapenv:Body><soap:GetListItems><soap:listName>Case Studies</soap:listName><soap:query><Query><Where><And><Eq><FieldRef Name='Asset_x0020_Type'/><Value Type='Text'>Domain</Value></Eq><Eq><FieldRef Name='Sub_x0020_Vertical'/><Value Type='Text'>Asset and Wealth Management</Value></Eq></And></Where></Query></soap:query><soap:rowLimit>1000</soap:rowLimit><!--Optional:--><soap:queryOptions><QueryOptions><ViewAttributes Scope='RecursiveAll'/></QueryOptions></soap:queryOptions></soap:GetListItems></soapenv:Body></soapenv:Envelope>";
				String requestSubXmlContent1 = "<"
						+ HttpRequestConstants.PARM_GETLISTITEMS_ACTION + " xmlns='"
						+ Constants.SOAP_ACTION_BASE_URL + "'>" + "<"												
						+ HttpRequestConstants.PARM_LIST_NAME + ">"+ (String) objects[0] + "</"+ HttpRequestConstants.PARM_LIST_NAME + ">"+"<" 
						+ HttpRequestConstants.PARM_QUERY + ">"+ (String) objects[1] + "</"+ HttpRequestConstants.PARM_QUERY + ">"+"<" 
						+ HttpRequestConstants.PARM_ROW_LIMIT + ">"+ (String) objects[2] + "</"+ HttpRequestConstants.PARM_ROW_LIMIT + ">"+"<" 
						+ HttpRequestConstants.PARM_QUERY_OPTIONS + ">"+ (String) objects[3] + "</"+ HttpRequestConstants.PARM_QUERY_OPTIONS + ">"
						+"</" 
						+ HttpRequestConstants.PARM_GETLISTITEMS_ACTION + ">";

					String requestSubXml1		= HttpRequestConstants.REQUEST_XML_START_TAG
											+ requestSubXmlContent1
											+ HttpRequestConstants.REQUEST_XML_END_TAG;
					Log.i(TAG, "XML REQ: " + requestSubXml1);
					StringEntity reqSubEntity1 	= null;
					try
					{
					reqSubEntity1 			= new StringEntity(requestSubXml1);
					}
					catch (UnsupportedEncodingException e1)
					{
					e1.printStackTrace();
					}
					request.setHeader("SOAPAction", Constants.SOAP_ACTION_BASE_URL+ HttpRequestConstants.PARM_GETLISTITEMS_ACTION);
					request.setEntity(reqSubEntity1);
					return request;
					/** Request for Listing documents **/
			case HttpRequestConstants.REQ_SEARCH_QUERY:			
				String requestSearchXmlContent = "<"
												+ HttpRequestConstants.PARM_QUERY_EX + " xmlns='"
												+ Constants.SEARCH_SOAP_ACTION_BASE_URL + "'>" 
												+ HttpRequestConstants.REQUEST_SEARCH_QUERY_START_TAG
												+ HttpRequestConstants.REQUEST_SEARCH_QUERY_TEXT_START_TAG
												+ (String) objects[0] 
												+ HttpRequestConstants.REQUEST_SEARCH_QUERY_TEXT_END_TAG
												+ HttpRequestConstants.REQUEST_SEARCH_QUERY_END_TAG
												+"</" 
												+ HttpRequestConstants.PARM_QUERY_EX + ">";
							
				String requestSearchXml 		= HttpRequestConstants.REQUEST_XML_START_TAG
												+ requestSearchXmlContent
												+ HttpRequestConstants.REQUEST_XML_END_TAG;

	
				String xml3= "<?xml version='1.0' encoding='utf-8'?><soap:Envelope xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' xmlns:xsd='http://www.w3.org/2001/XMLSchema' xmlns:soap='http://schemas.xmlsoap.org/soap/envelope/'><soap:Body><QueryEx xmlns='http://microsoft.com/webservices/OfficeServer/QueryService'><queryXml><![CDATA[<QueryPacket xmlns='urn:Microsoft.Search.Query' Revision='1000'><Query domain='QDomain'><SupportedFormats><Format>urn:Microsoft.Search.Response.Document.Document</Format></SupportedFormats><Range><Count>10</Count></Range><Context><QueryText language='en-US' type='STRING'>IsDocument:true SCOPE:\"All Sites\" ans.zip SITE:\"https://bfs-set.c.com\"</QueryText></Context></Query></QueryPacket>]]></queryXml></QueryEx></soap:Body></soap:Envelope>";
				StringEntity reqSearchEntity 	= null;
				try
				{
					reqSearchEntity 			= new StringEntity(requestSearchXml);
				}
				catch (UnsupportedEncodingException e1)
				{
					e1.printStackTrace();
				}
				Log.i(TAG, "XML REQ: " + requestSearchXml);
							
				HttpPost requestSearch						= new HttpPost(Constants.BASE_SEARCH_URL);				
				requestSearch.setHeader("Content-Type", "text/xml; charset=utf-8");								
				requestSearch.setHeader("SOAPAction", Constants.SEARCH_SOAP_ACTION_BASE_URL+ "/"+HttpRequestConstants.PARM_QUERY_EX);
				requestSearch.setEntity(reqSearchEntity);
				return requestSearch;
			 

				/** Request for Listing documents **/
			case HttpRequestConstants.REQ_MOBILE_USER_LOG:			
					String requestUserLogXmlContent = "<"
													+ HttpRequestConstants.PARM_ADD_USER_LOG_ACTION + " xmlns='"
													+ Constants.SOAP_ACTION_ADD_USER_BASE_URL + "'>" + "<"												
													+ HttpRequestConstants.PARM_USER_ID + ">"+ (String) objects[0] + "</"+ HttpRequestConstants.PARM_USER_ID + ">"+ "<"	
													+ HttpRequestConstants.PARM_LOGIN_MODE + ">"+ (String) objects[1] + "</"+ HttpRequestConstants.PARM_LOGIN_MODE + ">"
													+"</" 
													+ HttpRequestConstants.PARM_ADD_USER_LOG_ACTION + ">";
								
					Log.i(TAG, "XML REQ: " + requestUserLogXmlContent);
					String requestUserLogXml 		= HttpRequestConstants.REQUEST_XML_START_TAG
													+ requestUserLogXmlContent
													+ HttpRequestConstants.REQUEST_XML_END_TAG;

					StringEntity reqUserLogEntity 	= null;
					try
					{
						reqUserLogEntity 			= new StringEntity(requestUserLogXml);
					}
					catch (UnsupportedEncodingException e1)
					{
						e1.printStackTrace();
					}
					
					HttpPost requestAddUser						= new HttpPost(Constants.BASE_MOBILE_USER_LOG_URL);
					requestAddUser.setHeader("Content-Type", "text/xml; charset=utf-8");
					requestAddUser.setHeader("SOAPAction", Constants.SOAP_ACTION_ADD_USER_BASE_URL+ HttpRequestConstants.PARM_ADD_USER_LOG_ACTION);
					requestAddUser.setEntity(reqUserLogEntity);
					return requestAddUser;
													
			default:
				return null;
		}

	}
}
