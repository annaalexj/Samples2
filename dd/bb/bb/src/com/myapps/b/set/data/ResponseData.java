package com.myapps.b.set.data;

import android.os.Parcel;
import android.os.Parcelable;


public class ResponseData implements Parcelable {
	private boolean isSuccess;
	private int requestcode;
	private String responseMessage;
	private String responseData;

	public boolean isSuccess() {
		return isSuccess;
	}
	
	public void setSuccess(boolean isSuccess) {
		this.isSuccess 			= isSuccess;
	}

	public int getRequestcode() {
		return requestcode;
	}

	public void setRequestcode(int requestcode) {
		this.requestcode 		= requestcode;
	}

	public String getResponseMessage() {
		return responseMessage;
	}

	public void setResponseMessage(String responseMessage) {
		this.responseMessage 	= responseMessage;
	}

	public String getResponseData() {
		return responseData;
	}

	public void setResponseData(String responseData) {
		this.responseData 		= responseData;
	}
	
	public ResponseData() {
		
	}
	
	public ResponseData(Parcel in) {
		isSuccess 				= (in.readByte() == 1);
		requestcode 			= in.readInt();
		responseMessage 		= in.readString();
		responseData 			= in.readString();
		
	}
	
	@Override
	public int describeContents() {		
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeByte((byte) (isSuccess ? 1 : 0));
		dest.writeInt(requestcode);
		dest.writeString(responseMessage);
		dest.writeString(responseData);
		
	}
	
	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
		public ResponseData createFromParcel(Parcel in) {
			return new ResponseData(in);
		}

		public ResponseData[] newArray(int size) {
			return new ResponseData[size];
		}
	};


}
