/*
 * Intellectual properties of Supun Lakshan Wanigarathna Dissanayake
 * Copyright (c) 2014, Supun Lakshan Wanigarathna Dissanayake. All rights reserved.
 * Created on : May 9, 2014, 10:08:12 PM
 */
package com.ceylon_linux.lucky_lanka.activity;

import android.app.Activity;
import android.os.Bundle;
import com.ceylon_linux.lucky_lanka.R;

/**
 * MainActivity - Entry point of the Lucky Lanka Sales Pad
 *
 * @author Supun Lakshan Wanigarathna Dissanayake
 * @mobile +94711290392
 * @email supunlakshan.xfinity@gmail.com
 */
public class MainActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		initialize();
	}

	// <editor-fold defaultstate="collapsed" desc="Initialize">
	private void initialize() {
	}
	// </editor-fold>

}
