/*
 * Intellectual properties of Supun Lakshan Wanigarathna Dissanayake
 * Copyright (c) 2014, Supun Lakshan Wanigarathna Dissanayake. All rights reserved.
 * Created on : May 9, 2014, 10:08:12 PM
 */

package com.ceylon_linux.lucky_lanka.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import com.ceylon_linux.lucky_lanka.R;

/**
 * SplashActivity - Entry point of the Lucky Lanka Sales Pad
 *
 * @author Supun Lakshan Wanigarathna Dissanayake
 * @mobile +94711290392
 * @email supunlakshan.xfinity@gmail.com
 */
public class SplashActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash_screen);
		initialize();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		finish();
		System.exit(0);
	}

	// <editor-fold defaultstate="collapsed" desc="Initialize">
	private void initialize() {
		new Thread() {
			@Override
			public void run() {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				Intent loginActivity = new Intent(SplashActivity.this, LoginActivity.class);
				startActivity(loginActivity);
				finish();
			}
		}.start();
	}
	// </editor-fold>

}
