package com.myapps.b.set.data;

import com.myapps.b.set.model.LoginCredentials;

import android.os.Parcel;
import android.os.Parcelable;


public class RequestData implements Parcelable {
	private int requestCode;
	private Object[] params;
	private String url;		
	private LoginCredentials loginCredentials;

	public int getRequestCode() {
		return requestCode;
	}
	
	public void setRequestCode(int requestCode) {
		this.requestCode 		= requestCode;
	}
	
	public Object[] getParams() {
		return params;
	}

	public void setParams(Object[] params) {
		this.params 			= params;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url 				= url;
	}
	
	public RequestData() {
		
	}
	
	public RequestData(Parcel in) {
		requestCode 			= in.readInt();
		params 					= in.readArray(Object.class.getClassLoader());
		url 					= in.readString();
	}
	
	@Override
	public int describeContents() {
		
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(requestCode);
		dest.writeArray(params);
		dest.writeString(url);
		
	}
	
	
	public LoginCredentials getLoginCredentials()
	{
		return loginCredentials;
	}

	public void setLoginCredentials(LoginCredentials tempLoginCredentials)
	{
		this.loginCredentials 						= tempLoginCredentials;
	}

	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
		public RequestData createFromParcel(Parcel in) {
			return new RequestData(in);
		}

		public RequestData[] newArray(int size) {
			return new RequestData[size];
		}
	};


}
