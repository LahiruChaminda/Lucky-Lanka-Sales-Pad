/*
 * Intellectual properties of Supun Lakshan Wanigarathna Dissanayake
 * Copyright (c) 2014, Supun Lakshan Wanigarathna Dissanayake. All rights reserved.
 * Created on : Jun 13, 2014, 9:03:46 PM
 */

package com.ceylon_linux.lucky_lanka.util;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * @author Supun Lakshan Wanigarathna Dissanayake
 * @mobile +94711290392
 * @email supunlakshan.xfinity@gmail.com
 */
public class ProgressDialogGenerator {

	private ProgressDialogGenerator() {
	}

	public static ProgressDialog generateProgressDialog(Context context, String message, boolean cancelable, boolean cancelableOnBackgroundTouch) {
		ProgressDialog progressDialog = generateProgressDialog(context, message, cancelableOnBackgroundTouch);
		progressDialog.setCancelable(cancelable);
		return progressDialog;
	}

	public static ProgressDialog generateProgressDialog(Context context, String message, boolean cancelableOnBackgroundTouch) {
		ProgressDialog progressDialog = generateProgressDialog(context, message);
		progressDialog.setCanceledOnTouchOutside(cancelableOnBackgroundTouch);
		return progressDialog;
	}

	public static ProgressDialog generateProgressDialog(Context context, String message) {
		ProgressDialog progressDialog = new ProgressDialog(context);
		progressDialog.setMessage(message);
		return progressDialog;
	}

}
