package com.myapps.b.set;

import android.os.Parcel;
import android.os.Parcelable;

import com.myapps.b.set.data.RequestData;
import com.myapps.b.set.data.ResponseData;

public class BaseAppData implements Parcelable {
	private RequestData request;
	private ResponseData response;

	public RequestData getRequest() {
		return request;
	}

	public void setRequest(RequestData request) {
		this.request = request;
	}


	public ResponseData getResponse() {
		return response;
	}

	public void setResponse(ResponseData response) {
		this.response = response;
	}
	@Override
	public int describeContents() {		
		return 0;
	}
	
	public BaseAppData() {
		
	}

	public BaseAppData(Parcel in) {
		request 	= in.readParcelable(RequestData.class.getClassLoader());
		response 	= in.readParcelable(ResponseData.class.getClassLoader());
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeParcelable(request, flags);
		dest.writeParcelable(response, flags);
		
	}
	
	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
		public BaseAppData createFromParcel(Parcel in) {
			return new BaseAppData(in);
		}

		public BaseAppData[] newArray(int size) {
			return new BaseAppData[size];
		}
	};

}
