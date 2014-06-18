/*
 * Intellectual properties of Supun Lakshan Wanigarathna Dissanayake
 * Copyright (c) 2014, Supun Lakshan Wanigarathna Dissanayake. All rights reserved.
 * Created on : Jun 13, 2014, 11:54:31 AM
 */

package com.ceylon_linux.lucky_lanka.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Supun Lakshan Wanigarathna Dissanayake
 * @mobile +94711290392
 * @email supunlakshan.xfinity@gmail.com
 */
public class User implements Serializable {

	private int positionId;
	private String name;
	private String address;
	private String userName;
	private Long loginTime;

	public User(int positionId) {
		this.setPositionId(positionId);
	}

	public User(int positionId, String name, String address, String userName, Long loginTime) {
		this.setPositionId(positionId);
		this.setName(name);
		this.setAddress(address);
		this.setUserName(userName);
		this.setLoginTime(loginTime);
	}

	public static User parseUser(JSONObject userJsonInstance) throws JSONException {
		if (!userJsonInstance.getBoolean("result")) {
			return null;
		}
		return new User(
			userJsonInstance.getInt("position_id"),
			userJsonInstance.getString("name"),
			userJsonInstance.getString("postal_address"),
			userJsonInstance.getString("login_name"),
			new Date().getTime()
		);
	}

	public int getPositionId() {
		return positionId;
	}

	public void setPositionId(int positionId) {
		this.positionId = positionId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Long getLoginTime() {
		return loginTime;
	}

	public void setLoginTime(Long loginTime) {
		this.loginTime = loginTime;
	}
}
