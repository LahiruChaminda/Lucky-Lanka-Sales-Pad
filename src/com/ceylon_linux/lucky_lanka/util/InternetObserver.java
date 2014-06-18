/*
 * Intellectual properties of Supun Lakshan Wanigarathna Dissanayake
 * Copyright (c) 2014, Supun Lakshan Wanigarathna Dissanayake. All rights reserved.
 * Created on : May 9, 2014, 10:12:19 PM
 */

package com.ceylon_linux.lucky_lanka.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * InternetObserver - Check weather user can connect to Internet
 *
 * @author Supun Lakshan Wanigarathna Dissanayake
 * @mobile +94711290392
 * @email supunlakshan.xfinity@gmail.com
 */
public class InternetObserver {

	private static InternetObserver instance;
	private final ConnectivityManager connectivityManager;

	private InternetObserver(Context context) {
		connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	}

	public static synchronized boolean isConnectedToInternet(Context context) {
		if (instance == null) {
			instance = new InternetObserver(context);
		}
		NetworkInfo networkInfo = instance.connectivityManager.getActiveNetworkInfo();
		return networkInfo != null && networkInfo.isConnected();
	}
}
