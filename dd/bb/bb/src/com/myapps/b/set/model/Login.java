package com.myapps.b.set.model;

import com.myapps.b.set.data.BaseMarker;

public class Login implements BaseMarker {
	
	private boolean isAuthenticated = false;;	
	private String name 			= "";
	private String errorMessage 	= "";
	public Login() {
		
	}

	public void setAuthenticated(boolean isAuthenticated) {
		this.isAuthenticated = isAuthenticated;
	}


	public boolean isAuthenticated() {
		return isAuthenticated;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
	
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getErrorMessage() {
		return errorMessage;
	}


}
