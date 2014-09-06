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
import java.text.NumberFormat;
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
		int positionId;
		int routineId;
		String userName;
		String name;
		String address;
		long loginTime;
		long lastOrderId;
		if ((loginTime = userData.getLong("loginTime", -1)) == -1) {
			return null;
		}
		Date lastLoginDate = new Date(loginTime);
		Date currentDate = new Date();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		if (!simpleDateFormat.format(lastLoginDate).equalsIgnoreCase(simpleDateFormat.format(currentDate))) {
			return null;
		}
		if ((positionId = userData.getInt("userId", -1)) == -1) {
			return null;
		}
		if ((routineId = userData.getInt("routineId", -1)) == -1) {
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
		lastOrderId = userData.getLong("orderId", getInvoiceId(context));
		return new User(positionId, name, address, userName, loginTime, routineId, lastOrderId);
	}

	public static boolean setAuthorizedUser(Context context, User user) {
		SharedPreferences userData = context.getSharedPreferences("userData", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = userData.edit();
		editor.putInt("userId", user.getPositionId());
		editor.putInt("routineId", user.getRoutineId());
		editor.putString("userName", user.getUserName());
		editor.putString("name", user.getName());
		editor.putString("address", user.getAddress());
		editor.putLong("loginTime", user.getLoginTime());
		editor.putBoolean("loading", false);
		editor.putBoolean("unloading", false);
		editor.putLong("orderId", user.getLastOrderId());
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
		editor.putInt("routineId", -1);
		editor.putString("name", "");
		editor.putString("address", "");
		editor.putLong("loginTime", -1);
		return editor.commit();
	}

	public static boolean confirmLoading(Context context) {
		SharedPreferences userData = context.getSharedPreferences("userData", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = userData.edit();
		editor.putBoolean("loading", true);
		return editor.commit();
	}

	public static boolean confirmUnloading(Context context) {
		SharedPreferences userData = context.getSharedPreferences("userData", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = userData.edit();
		editor.putBoolean("unloading", true);
		return editor.commit();
	}

	public static User authenticate(Context context, String userName, String password) throws IOException, JSONException {
		JSONObject userJson = getJsonObject(UserURLPack.LOGIN, UserURLPack.getParameters(userName, password), context);
		return User.parseUser(userJson);
	}

	public static boolean checkStockAvailability(Context context, int positionId) throws IOException, JSONException {
		JSONObject stockJson = getJsonObject(UserURLPack.AUTHORIZATION, UserURLPack.getAuthorizationParameters(positionId), context);
		return stockJson != null && stockJson.getBoolean("result");
	}

	public static String getPastAuthorizedUserName(Context context) {
		SharedPreferences userData = context.getSharedPreferences("userData", Context.MODE_PRIVATE);
		String userName;
		if ((userName = userData.getString("userName", "")).isEmpty()) {
			return null;
		}
		return userName;
	}

	public static boolean isLoadingConfirmed(Context context) {
		SharedPreferences userData = context.getSharedPreferences("userData", Context.MODE_PRIVATE);
		long loginTime;
		if ((loginTime = userData.getLong("loginTime", -1)) == -1) {
			return false;
		}
		Date lastLoginDate = new Date(loginTime);
		Date currentDate = new Date();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		if (!simpleDateFormat.format(lastLoginDate).equalsIgnoreCase(simpleDateFormat.format(currentDate))) {
			return false;
		}
		return userData.getBoolean("loading", false);
	}

	public static boolean isUnloadingConfirmed(Context context) {
		SharedPreferences userData = context.getSharedPreferences("userData", Context.MODE_PRIVATE);
		long loginTime;
		if ((loginTime = userData.getLong("loginTime", -1)) == -1) {
			return false;
		}
		Date lastLoginDate = new Date(loginTime);
		Date currentDate = new Date();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		if (!simpleDateFormat.format(lastLoginDate).equalsIgnoreCase(simpleDateFormat.format(currentDate))) {
			return false;
		}
		return userData.getBoolean("unloading", false);
	}

	public static long getInvoiceId(Context context) {
		SharedPreferences userData = context.getSharedPreferences("userData", Context.MODE_PRIVATE);
		long nextInvoiceId = userData.getLong("orderId", 0) + 1;
		if (nextInvoiceId == 1) {
			NumberFormat numberFormat = NumberFormat.getInstance();
			numberFormat.setMaximumIntegerDigits(7);
			numberFormat.setGroupingUsed(false);
			numberFormat.setMinimumIntegerDigits(7);
			nextInvoiceId = Long.parseLong(userData.getInt("userId", 0) + "" + numberFormat.format(nextInvoiceId));
			SharedPreferences.Editor editor = userData.edit();
			editor.putLong("orderId", nextInvoiceId);
			editor.commit();
			return nextInvoiceId;
		} else {
			return nextInvoiceId;
		}
	}

	public static boolean increaseLatestOrderId(Context context, long invoiceId) {
		SharedPreferences userData = context.getSharedPreferences("userData", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = userData.edit();
		editor.putLong("lastOrderId", invoiceId);
		return editor.commit();
	}

	public static void decreaseLatestOrderId(Context context) {
		SharedPreferences userData = context.getSharedPreferences("userData", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = userData.edit();
		editor.putLong("lastOrderId", userData.getLong("lastOrderId", 0) - 1);
		editor.commit();
	}
}
