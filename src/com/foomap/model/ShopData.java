package com.foomap.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

//shop
public  class  ShopData implements Serializable{
	public int id;
	
	public String createUserId;
	public String name;
	public String iconUrl;
	public double latitude,longitude;
	public String address;
	public String feature;
	public double grade_avg,cost_avg;
	public int typeId;
	public List<String> picUrls;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getCreateUserId() {
		return createUserId;
	}
	public void setCreateUserId(String createUserId) {
		this.createUserId = createUserId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getIconUrl() {
		return iconUrl;
	}
	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getFeature() {
		return feature;
	}
	public void setFeature(String feature) {
		this.feature = feature;
	}
	public double getGrade_avg() {
		return grade_avg;
	}
	public void setGrade_avg(double grade_avg) {
		this.grade_avg = grade_avg;
	}
	public double getCost_avg() {
		return cost_avg;
	}
	public void setCost_avg(double cost_avg) {
		this.cost_avg = cost_avg;
	}
	public int getTypeId() {
		return typeId;
	}
	public void setTypeId(int typeId) {
		this.typeId = typeId;
	}
	public List<String> getPicUrls() {
		return picUrls;
	}
	public void setPicUrls(List<String> picUrls) {
		this.picUrls = picUrls;
	}
}