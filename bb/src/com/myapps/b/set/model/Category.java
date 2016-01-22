package com.myapps.b.set.model;

import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;

import com.myapps.b.set.data.BaseMarker;
import com.myapps.b.set.data.RequestData;

/**
 * class for  offerings
 * @author 324520
 *
 */
public class Category implements Parcelable 
{
	//BFS starts
	private String categoryName 			= "";
	private int categoryId 					= -1;	
	private int fileCount 					= 0;
	private int categoryType 				= 0;
	private String topCategoryName 			= "";
	private String superTopCategoryName 	= "";
	private boolean isSelected 				= false;
	private int position 					= -1;
	private String folderName 				= "";
	
	//private 
	private ArrayList<Category> lstCategory 		= new ArrayList<Category>();
	private ArrayList<Category> lstMainCategory 	= new ArrayList<Category>();
	private ArrayList<Category> lstSubCategory 		= new ArrayList<Category>();
	private ArrayList<Category> lstInnerCategory 	= new ArrayList<Category>();
	
	public void setFileCount(int fileCount) {
		this.fileCount 		= fileCount;
	}
	public int getFileCount() {
		return fileCount;
	}
	
	public void setCategoryId(int categoryId) {
		this.categoryId 	= categoryId;
	}
	public int getCategoryId() {
		return categoryId;
	}
	public void setCategoryName(String categoryName) {
		this.categoryName 	= categoryName;
	}
	public String getCategoryName() {
		return categoryName;
	}
	
	public void setListCategory(ArrayList<Category> lstCategory) {
		this.lstCategory 	= lstCategory;
	}
	public ArrayList<Category> getListCategory() {
		return lstCategory;
	}
	public void addListCategory(Category argCategory) {
		this.lstCategory.add(argCategory);
		
	}
	public int getCategoryType()
	{
		return categoryType;
	}
	public void setCategoryType(int categoryType)
	{
		this.categoryType 		= categoryType;
	}
	public String getTopCategoryName()
	{
		return topCategoryName;
	}
	public void setTopCategoryName(String topCategoryName)
	{
		this.topCategoryName 	= topCategoryName;
	}
	public String getSuperTopCategoryName()
	{
		return superTopCategoryName;
	}
	public void setSuperTopCategoryName(String superTopCategoryName)
	{
		this.superTopCategoryName = superTopCategoryName;
	}
	public ArrayList<Category> getLstSubCategory()
	{
		return lstSubCategory;
	}
	public void setLstSubCategory(ArrayList<Category> lstSubCategory)
	{
		this.lstSubCategory = lstSubCategory;
	}
	public ArrayList<Category> getLstInnerCategory()
	{
		return lstInnerCategory;
	}
	public void setLstInnerCategory(ArrayList<Category> lstInnerCategory)
	{
		this.lstInnerCategory = lstInnerCategory;
	}
	public ArrayList<Category> getLstMainCategory()
	{
		return lstMainCategory;
	}
	public void setLstMainCategory(ArrayList<Category> lstMainCategory)
	{
		this.lstMainCategory = lstMainCategory;
	}
	public void addSubListCategory(Category argCategory) {
		this.lstSubCategory.add(argCategory);
		
	}
	public void addMainListCategory(Category argCategory) {
		this.lstMainCategory.add(argCategory);
		
	}
	public void addInnerListCategory(Category argCategory) {
		this.lstInnerCategory.add(argCategory);
		
	}
	//bfs ends
	public boolean isSelected()
	{
		return isSelected;
	}
	public void setSelected(boolean isSelected)
	{
		this.isSelected 		= isSelected;
	}
	public int getPosition()
	{
		return position;
	}
	public void setPosition(int position)
	{
		this.position 			= position;
	}
	public String getFolderName()
	{
		return folderName;
	}
	public void setFolderName(String folderName)
	{
		this.folderName 		= folderName;
	}
	@Override
	public int describeContents()
	{		
		return 0;
	}
		
	@Override
	public void writeToParcel(Parcel parcel, int flag)
	{
		parcel.writeString(this.categoryName);
		parcel.writeString(this.topCategoryName);
		parcel.writeString(this.superTopCategoryName);
		parcel.writeString(this.folderName);
		parcel.writeString(this.categoryName);
		parcel.writeInt(this.fileCount);
		parcel.writeInt(this.categoryType);
		parcel.writeInt(this.categoryId);
		parcel.writeInt(this.position);				
	}
	
	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
		public Category createFromParcel(Parcel in) {
			return new Category();
		}

		public Category[] newArray(int size) {
			return new Category[size];
		}
	};

}
