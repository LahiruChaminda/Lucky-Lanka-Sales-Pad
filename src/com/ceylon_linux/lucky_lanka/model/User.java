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
	private int routineId;
	private boolean validUser;

	private User(boolean validUser) {
		this.validUser = validUser;
	}

	public User(int positionId) {
		this.setPositionId(positionId);
		this.validUser = true;
	}

	public User(int positionId, String name, String address, String userName, Long loginTime, int routineId) {
		this.positionId = positionId;
		this.name = name;
		this.address = address;
		this.userName = userName;
		this.loginTime = loginTime;
		this.routineId = routineId;
		this.validUser = true;
	}

	public static User parseUser(JSONObject userJsonInstance) throws JSONException {
		if (userJsonInstance == null) {
			return null;
		} else if (!userJsonInstance.getBoolean("result")) {
			return new User(false);
		}
		return new User(
			userJsonInstance.getInt("position_id"),
			userJsonInstance.getString("name"),
			userJsonInstance.getString("postal_address"),
			userJsonInstance.getString("login_name"),
			new Date().getTime(),
			userJsonInstance.getInt("session_id")
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

	public int getRoutineId() {
		return routineId;
	}

	public void setRoutineId(int routineId) {
		this.routineId = routineId;
	}

	public boolean isValidUser() {
		return validUser;
	}
}
