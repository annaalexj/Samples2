package com.myapps.b.set;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.security.KeyStore;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.StatusLine;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.NTCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.xmlpull.v1.XmlPullParser;

import android.os.AsyncTask;
import android.util.Log;

import com.myapps.b.set.communication.HttpRequestMaker;
import com.myapps.b.set.data.ResponseData;
import com.myapps.b.set.model.LoginCredentials;
import com.myapps.b.set.ntlm.NTLMSchemeFactory;
import com.myapps.b.set.utils.Constants;

public class BaseAppExecutor extends AsyncTask<Void, Void, BaseAppData> {
	private static final String TAG 		= BaseAppExecutor.class.getSimpleName();
	private BaseAppData data;
	private HttpClient httpclient 			= getNewHttpClient();
	private HttpRequestBase request;
	XmlPullParser xpp;

	/**
	 * Function to get the HttpClient for executing the request
	 * @return
	 */
	
	public HttpClient getNewHttpClient() {
		try {
			KeyStore trustStore 			= KeyStore.getInstance(KeyStore.getDefaultType());
			trustStore.load(null, null);

			SSLSocketFactory sf 			= new MySSLSocketFactory(trustStore);
			sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

			HttpParams params 				= new BasicHttpParams();
			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
			int timeoutConnection 			= 10000;
			HttpConnectionParams.setConnectionTimeout(params, timeoutConnection);
			int timeoutSocket 				= 20000;
			HttpConnectionParams.setSoTimeout(params, timeoutSocket);
			SchemeRegistry registry 		= new SchemeRegistry();
			registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
			registry.register(new Scheme("https", sf, 443));

			ClientConnectionManager ccm 	= new ThreadSafeClientConnManager(params, registry);

			return new DefaultHttpClient(ccm, params);
		} catch (Exception e) {
			return new DefaultHttpClient();
		}
	}

	
	public BaseAppExecutor(BaseAppData data) {
		this.data = data;
	}

	/**
	 * Function to convert Input Stream to String
	 * @param in
	 * @return
	 * @throws IOException
	 */
	private String streamToString(InputStream in) throws IOException {
		String out 			= new String();
		BufferedReader br 	= new BufferedReader(new InputStreamReader(in));
		for(String line = br.readLine(); line != null; line = br.readLine())
					out 	+= line;
		return out;

	}

	/**
	 * Function to form an error data for displaying the user
	 * @param e
	 * @param reqCode
	 * @return
	 */
	private ResponseData formErrorData(String e, final int reqCode) {
		ResponseData resData 		= new ResponseData();
		resData.setRequestcode(reqCode);
		resData.setSuccess(false);
		resData.setResponseData(null);
		resData.setResponseMessage(e);
		return resData;

	}

	/**
	 * Function to notify the listeners for calling the 
	 * corresponding activity from which the web 
	 * service request is called.
	 * @param resultData
	 */
	private void notifyListeners(BaseAppData resultData) {		
		try
		{
		for(Integer key : ActionListener.activities.keySet()) {
			if(resultData.getRequest().getRequestCode() == key) {
				ActionListener.activities.get(key).onFinish(resultData);
			}
		}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			
		}
	}

	/**
	 * Function where the web service request is processed.
	 */
	@Override
	protected BaseAppData doInBackground(Void... values)
	{
		request 							= HttpRequestMaker.getRequest(data.getRequest().getRequestCode(), data.getRequest().getParams());
		LoginCredentials loginCredentials 	= data.getRequest().getLoginCredentials();
		//Got Request from Http Request Maker		
		ResponseData resData 				= new ResponseData();

		try
		{			
			((DefaultHttpClient) httpclient).getAuthSchemes().register("ntlm", new NTLMSchemeFactory());
            ((DefaultHttpClient) httpclient).getCredentialsProvider().setCredentials(                  
                    new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT),                   
                    new NTCredentials(loginCredentials.getUsername(), loginCredentials.getPassword(),loginCredentials.getLocalHost(),loginCredentials.getDomain())
            );

			HttpResponse response 		= httpclient.execute(request);
			StatusLine statusLine 		= response.getStatusLine();
			System.out.println("response code : " +statusLine.getStatusCode() );
			if(statusLine.getStatusCode() == HttpStatus.SC_OK) 
			{
				Log.i(TAG, "Http Status Line is OK or " + statusLine.getStatusCode());
				
				HttpEntity entity 		= response.getEntity();
				InputStream is 			= entity.getContent();
				String content 			= streamToString(is);			
				
				try 
				{
					resData.setResponseData(content);
					resData.setSuccess(true);
					resData.setRequestcode(data.getRequest().getRequestCode() );
					data.setResponse(resData);
					return data;
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}

			} 
			else
			{
			
				response.getEntity().getContent().close();
				if(statusLine.getStatusCode() == HttpStatus.SC_UNAUTHORIZED)
				{
					throw new IOException(Constants.RESPONSE_UNAUTHORIZED);
				}
				else
					throw new IOException(statusLine.getReasonPhrase());
			}


		}
		catch (IllegalArgumentException e) {
			resData = formErrorData(Constants.ALERT_NETWORK_ERROR, data.getRequest().getRequestCode());
			e.printStackTrace();
			data.setResponse(resData);
			return data;
		}
		catch(UnknownHostException e ) {
			resData = formErrorData(Constants.ALERT_NETWORK_ERROR, data.getRequest().getRequestCode());
			//e.printStackTrace();
			data.setResponse(resData);
			return data;
		}
		catch(ConnectTimeoutException e ) {
			resData = formErrorData(Constants.RESPONSE_CONNECTION_TIMEOUT, data.getRequest().getRequestCode());
			e.printStackTrace();
			data.setResponse(resData);
			return data;
		}
		catch (ClientProtocolException e) {
			resData = formErrorData(Constants.ALERT_NETWORK_ERROR, data.getRequest().getRequestCode());
			e.printStackTrace();
			data.setResponse(resData);
			return data;
		} catch (IOException e) {
			resData = formErrorData(Constants.ALERT_NETWORK_ERROR, data.getRequest().getRequestCode());
			e.printStackTrace();
			data.setResponse(resData);
			return data;
		}
		return null;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}

	@Override
	protected void onPostExecute(BaseAppData result) {
		super.onPostExecute(result);
		notifyListeners(result);
	}

	@Override
	protected void onProgressUpdate(Void... values) {
		super.onProgressUpdate(values);
	}
	
	
}
