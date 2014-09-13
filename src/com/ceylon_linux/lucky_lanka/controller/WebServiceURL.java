/*
 * Intellectual properties of Supun Lakshan Wanigarathna Dissanayake
 * Copyright (c) 2014, Supun Lakshan Wanigarathna Dissanayake. All rights reserved.
 * Created on : May 9, 2014, 10:23:39 PM
 */

package com.ceylon_linux.lucky_lanka.controller;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * WebServiceURL - Holds web service URL(s)
 *
 * @author Supun Lakshan Wanigarathna Dissanayake
 * @mobile +94711290392
 * @email supunlakshan.xfinity@gmail.com
 */
abstract class WebServiceURL {

//	getProducts
//		insert_order
//	outstandingPayment
//		insert_session_stock_unload
//	setStockLoadConfirm
//		setStockUnloadConfirm

	private static final String webServiceURL = "http://gateway.ceylonlinux.com/CL_DISTRIBUTOR/android_service/";
//	private static final String webServiceURL = "http://222.165.133.139/CL_DISTRIBUTOR/android_service/";

	protected WebServiceURL() {
	}

	protected static final class CategoryURLPack {

		public static final HashMap<String, Object> getParameters(int positionId, int sessionId) {
			HashMap<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("position_id", positionId);
			parameters.put("session_id", sessionId);
			return parameters;
		}

		public static final HashMap<String, Object> getUnloadingParameters(int positionId, JSONArray unloadingJson, int sessionId) {
			HashMap<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("position_id", positionId);
			parameters.put("session_id", sessionId);
			parameters.put("data", unloadingJson);
			return parameters;
		}

		public static final HashMap<String, Object> getLoadingConfirmParameters(int positionId, int sessionId) {
			HashMap<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("position_id", positionId);
			parameters.put("session_id", sessionId);
			return parameters;
		}

		public static final HashMap<String, Object> getPOSMParameters(int positionId, int sessionId) {
			HashMap<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("position_id", positionId);
			parameters.put("session_id", sessionId);
			return parameters;
		}

		public static final HashMap<String, Object> getFreeIssueCalculationParameters(int positionId, int sessionId) {
			HashMap<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("position_id", positionId);
			parameters.put("session_id", sessionId);
			return parameters;
		}

		public static final String GET_ITEMS_AND_CATEGORIES = webServiceURL + "getProducts2";
		public static final String CONFIRM_UNLOADING = webServiceURL + "setStockUnloadConfirm";
		public static final String CONFIRM_LOADING = webServiceURL + "setStockLoadConfirm";
		public static final String POSM_DETAILS = webServiceURL + "getPOSMItems";
		public static final String FREE_ISSUE_CALCULATION_DETAILS = webServiceURL + "getAgainFreeItems";
	}

	protected static final class UserURLPack {

		public static final HashMap<String, Object> getParameters(String userName, String password) {
			HashMap<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("username", userName);
			parameters.put("password", password);
			return parameters;
		}

		public static final HashMap<String, Object> getAuthorizationParameters(int positionId) {
			HashMap<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("position_id", positionId);
			return parameters;
		}

		public static final String LOGIN = webServiceURL + "login";
		public static final String AUTHORIZATION = webServiceURL + "authorization";

	}

	protected static final class OutletURLPack {

		public static final HashMap<String, Object> getParameters(int positionId) {
			HashMap<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("position_id", positionId);
			return parameters;
		}

		public static final String GET_OUTLETS = webServiceURL + "getRouteAndOutlets";
		public static final String REGISTER_OUTLET = webServiceURL + "reg_outlet";
	}

	protected static final class OrderURLPack {

		public static final HashMap<String, Object> getParameters(JSONObject orderJson, int positionId, int sessionId) {
			HashMap<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("jsonString", orderJson);
			parameters.put("position_id", positionId);
			parameters.put("session_id", sessionId);
			return parameters;
		}

		public static final String INSERT_ORDER = webServiceURL + "insert_order";
	}

	protected static final class PaymentURLPack {
		public static final HashMap<String, Object> getParameters(JSONObject invoiceJson, int positionId, int sessionId) {
			HashMap<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("jsonString", invoiceJson);
			parameters.put("position_id", positionId);
			parameters.put("session_id", sessionId);
			return parameters;
		}

		public static final String PAYMENT_SYNC = webServiceURL + "outstandingPayment";
	}

	protected static final class BankURLPack {

		public static final HashMap<String, Object> getParameters(int positionId) {
			HashMap<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("position_id", positionId);
			return parameters;
		}

		public static final String GET_BANKS = webServiceURL + "getBanks";
	}
}
