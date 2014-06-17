/*
 * Intellectual properties of Supun Lakshan Wanigarathna Dissanayake
 * Copyright (c) 2014, Supun Lakshan Wanigarathna Dissanayake. All rights reserved.
 * Created on : May 13, 2014, 8:36:18 AM
 */

package com.ceylon_linux.lucky_lanka.controller;

import android.content.Context;
import android.content.SharedPreferences;
import com.ceylon_linux.lucky_lanka.model.User;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;

/**
 * UserController - Description of UserController
 *
 * @author Supun Lakshan Wanigarathna Dissanayake
 * @mobile +94711290392
 * @email supunlakshan.xfinity@gmail.com
 */
public class UserController extends AbstractController {

	private UserController() {
	}

	public static User getAuthorizedUser(Context context) {
		SharedPreferences userData = context.getSharedPreferences("userData", Context.MODE_PRIVATE);
		Integer positionId;
		String userName;
		String name;
		String address;
		long loginTime;
		if ((loginTime = userData.getLong("loginTime", -1)) == -1) {
			return null;
		}
		Date lastLoginDate = new Date(loginTime);
		Date currentDate = new Date();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("y-M-d");
		if (!simpleDateFormat.format(lastLoginDate).equalsIgnoreCase(simpleDateFormat.format(currentDate))) {
			return null;
		}
		if ((positionId = userData.getInt("userId", -1)) == -1) {
			return null;
		}
		if ((userName = userData.getString("userName", "")).isEmpty()) {
			return null;
		}
		if ((name = userData.getString("name", "")).isEmpty()) {
			return null;
		}
		if ((address = userData.getString("address", "")).isEmpty()) {
			return null;
		}
		return new User(positionId, name, address, userName, loginTime);
	}

	public static boolean setAuthorizedUser(Context context, User user) {
		SharedPreferences userData = context.getSharedPreferences("userData", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = userData.edit();
		editor.putInt("userId", user.getPositionId());
		editor.putString("userName", user.getUserName());
		editor.putString("name", user.getName());
		editor.putString("address", user.getAddress());
		editor.putLong("loginTime", user.getLoginTime());
		return editor.commit();
	}

	private static String getMD5HashVal(String strToBeEncrypted) throws UnsupportedEncodingException, NoSuchAlgorithmException {
		Formatter formatter = new Formatter();
		try {
			String encryptedString;
			byte[] bytesToBeEncrypted;
			bytesToBeEncrypted = strToBeEncrypted.getBytes("UTF-8");
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] theDigest = md.digest(bytesToBeEncrypted);
			for (byte b : theDigest) {
				formatter.format("%02x", b);
			}
			encryptedString = formatter.toString().toLowerCase();
			return encryptedString;
		} finally {
			formatter.close();
		}
	}

	public static boolean clearAuthentication(Context context) {
		SharedPreferences userData = context.getSharedPreferences("userData", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = userData.edit();
		editor.putInt("userId", -1);
		editor.putString("userName", "");
		editor.putString("name", "");
		editor.putString("address", "");
		editor.putLong("loginTime", -1);
		return editor.commit();
	}

	public static User authenticate(Context context, String userName, String password) throws IOException, JSONException {
		JSONObject userJson = getJsonObject(UserURLPack.LOGIN, UserURLPack.getParameters(userName, password), context);
		return User.parseUser(userJson);
	}
}
