package com.myapps.b.set.model;

public class LoginCredentials
{
	private String username 			= "";
	private String password 			= "";
	private String localHost 			= "";
	private String domain 				= "";
	public String getUsername()
	{
		return username;
	}
	public void setUsername(String username)
	{
		this.username = username;
	}
	public String getPassword()
	{
		return password;
	}
	public void setPassword(String password)
	{
		this.password = password;
	}
	public String getLocalHost()
	{
		return localHost;
	}
	public void setLocalHost(String localHost)
	{
		this.localHost = localHost;
	}
	public String getDomain()
	{
		return domain;
	}
	public void setDomain(String domain)
	{
		this.domain = domain;
	}
}
