package com.myapps.b.set.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Expert
{
	private String expertName 			= "";
	private String employeeId 			= "";
	private String city 				= "";
	private String mobile 				= "";
	private String vnet 				= "";
	private String groupName 			= "";
	private String teamName 			= "";
	private String onsiteOrOffshore 	= "";
	private Date modifiledDate 		;;
	private ArrayList<Expert> arlstExperts = new ArrayList<Expert>();
	public String getExpertName()
	{
		return expertName;
	}
	public void setExpertName(String expertName)
	{
		this.expertName = expertName;
	}
	public String getEmployeeId()
	{
		return employeeId;
	}
	public void setEmployeeId(String employeeId)
	{
		this.employeeId = employeeId;
	}
	public String getCity()
	{
		return city;
	}
	public void setCity(String city)
	{
		this.city = city;
	}
	public String getMobile()
	{
		return mobile;
	}
	public void setMobile(String mobile)
	{
		this.mobile = mobile;
	}
	public String getVnet()
	{
		return vnet;
	}
	public void setVnet(String vnet)
	{
		this.vnet = vnet;
	}
	public String getGroupName()
	{
		return groupName;
	}
	public void setGroupName(String groupName)
	{
		this.groupName = groupName;
	}
	public String getTeamName()
	{
		return teamName;
	}
	public void setTeamName(String teamName)
	{
		this.teamName = teamName;
	}
	public String getOnsiteOrOffshore()
	{
		return onsiteOrOffshore;
	}
	public void setOnsiteOrOffshore(String onsiteOrOffshore)
	{
		this.onsiteOrOffshore = onsiteOrOffshore;
	}
	
	public ArrayList<Expert> getArlstExpert()
	{
		return arlstExperts;
	}

	public void setArlstExpert(ArrayList<Expert> arglstExperts)
	{
		this.arlstExperts = arglstExperts;
	}
	public void addToArlstExpert(Expert exp)
	{
		this.arlstExperts.add(exp);
	}
	
	public Date getModifiledDate()
	{
		return modifiledDate;
	}

	public void setModifiledDate(String argmodifiledDate)
	{
		//ows_Last_x0020_Modified 159;#2014-04-06 22:26:15 
		int dateIndex				= argmodifiledDate.indexOf("#");
		argmodifiledDate 			= argmodifiledDate.substring(dateIndex+1);
		SimpleDateFormat  format 	= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  

		try
		{
			this.modifiledDate 		= format.parse(argmodifiledDate);
		}
		catch (ParseException e)
		{			
			e.printStackTrace();
		}
	}

}
