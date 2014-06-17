/*
 * Intellectual properties of Supun Lakshan Wanigarathna Dissanayake
 * Copyright (c) 2014, Supun Lakshan Wanigarathna Dissanayake. All rights reserved.
 * Created on : May 9, 2014, 10:23:39 PM
 */

package com.ceylon_linux.lucky_lanka.controller;

import java.util.HashMap;

/**
 * WebServiceURL - Holds web service URL(s)
 *
 * @author Supun Lakshan Wanigarathna Dissanayake
 * @mobile +94711290392
 * @email supunlakshan.xfinity@gmail.com
 */
abstract class WebServiceURL {

	private static final String webServiceURL = "http://192.168.1.60/kukku_serv/android_service/";
	//private static final String webServiceURL = "http://220.247.234.226/CL_DISTRIBUTOR/native/";

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
}
