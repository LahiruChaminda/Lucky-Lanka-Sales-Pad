/*
 * Intellectual properties of Supun Lakshan Wanigarathna Dissanayake
 * Copyright (c) 2014, Supun Lakshan Wanigarathna Dissanayake. All rights reserved.
 * Created on : May 9, 2014, 10:23:39 PM
 */

package com.ceylon_linux.lucky_lanka.controller;

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

	private static final String webServiceURL = "http://gateway.ceylonlinux.com/CL_DISTRIBUTOR/android_service/";

	protected WebServiceURL() {
	}

	protected static final class CategoryURLPack {

		public static final HashMap<String, Object> getParameters(int positionId) {
			HashMap<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("position_id", positionId);
			return parameters;
		}

		public static final String GET_ITEMS_AND_CATEGORIES = webServiceURL + "getProducts";
	}

	protected static final class UserURLPack {

		public static final HashMap<String, Object> getParameters(String userName, String password) {
			HashMap<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("username", userName);
			parameters.put("password", password);
			return parameters;
		}

		public static final String LOGIN = webServiceURL + "login";

	}

	protected static final class OutletURLPack {

		public static final HashMap<String, Object> getParameters(int positionId) {
			HashMap<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("position_id", positionId);
			return parameters;
		}

		public static final String GET_OUTLETS = webServiceURL + "getRouteAndOutlets";
	}

	protected static final class OrderURLPack {

		public static final HashMap<String, Object> getParameters(JSONObject orderJson, int positionId) {
			HashMap<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("jsonString", orderJson);
			parameters.put("position_id", positionId);
			return parameters;
		}

		public static final String INSERT_ORDER = webServiceURL + "insert_order";
	}

	protected static final class PaymentURLPack {
		public static final HashMap<String, Object> getParameters(JSONObject invoiceJson, int positionId) {
			HashMap<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("jsonString", invoiceJson);
			parameters.put("position_id", positionId);
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
