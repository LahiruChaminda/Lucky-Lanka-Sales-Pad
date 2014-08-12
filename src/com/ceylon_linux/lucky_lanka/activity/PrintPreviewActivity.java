/*
 * Intellectual properties of Supun Lakshan Wanigarathna Dissanayake
 * Copyright (c) 2014, Supun Lakshan Wanigarathna Dissanayake. All rights reserved.
 * Created on : Aug 07, 2014, 11:36:33 AM
 */
package com.ceylon_linux.lucky_lanka.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.ceylon_linux.lucky_lanka.R;

/**
 * @author Supun Lakshan Wanigarathna Dissanayake
 * @mobile +94711290392
 * @email supunlakshan.xfinity@gmail.com
 */
public class PrintPreviewActivity extends Activity {
	private TextView txtBillPreview;
	private Button btnOk;
	private Button btnCancel;
	private byte[] receivedOrder;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.invoice_preview_activity);
		initialize();
		receivedOrder = (byte[]) getIntent().getSerializableExtra("orderStream");
		txtBillPreview.setText(new String(receivedOrder));
	}

	private void initialize() {
		txtBillPreview = (TextView) findViewById(R.id.txtBillPreview);
		btnOk = (Button) findViewById(R.id.btnOk);
		btnCancel = (Button) findViewById(R.id.btnCancel);
		btnOk.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				btnOkClicked(v);
			}
		});
		btnCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				btnCancelClicked(v);
			}
		});
	}

	private void btnOkClicked(View view) {
		Intent data = new Intent();
		data.putExtra("response", true);
		setResult(RESULT_OK, data);
		finish();
	}

	private void btnCancelClicked(View view) {
		Intent data = new Intent();
		data.putExtra("response", false);
		setResult(RESULT_OK, data);
		finish();
	}
}
