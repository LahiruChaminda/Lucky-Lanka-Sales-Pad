/*
 * Intellectual properties of Supun Lakshan Wanigarathna Dissanayake
 * Copyright (c) 2014, Supun Lakshan Wanigarathna Dissanayake. All rights reserved.
 * Created on : Aug 28, 2014, 8:55:32 AM
 */
package com.ceylon_linux.lucky_lanka.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.ceylon_linux.lucky_lanka.R;
import com.ceylon_linux.lucky_lanka.model.PosmDetail;
import com.ceylon_linux.lucky_lanka.model.PosmItem;

/**
 * @author Supun Lakshan Wanigarathna Dissanayake
 * @mobile +94711290392
 * @email supunlakshan.xfinity@gmail.com
 */
public class EnterPosmDetailActivity extends Activity {

	private PosmItem posmItem;
	private EditText inputRequestedQuantity;
	private Button btnOk;
	private Button btnCancel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.posm_insert_page);
		initialize();
	}

	private void initialize() {
		posmItem = (PosmItem) getIntent().getSerializableExtra("posm");
		inputRequestedQuantity = (EditText) findViewById(R.id.inputRequestedQuantity);
		int requestedQuantity;
		if ((requestedQuantity = getIntent().getIntExtra("quantity", 0)) != 0) {
			inputRequestedQuantity.setText(String.valueOf(requestedQuantity));
		}
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
		String inputString = inputRequestedQuantity.getText().toString();
		PosmDetail posmDetail = new PosmDetail(posmItem.getPosmDetailId(), posmItem.getPosmDescription(), inputString.isEmpty() ? 0 : Integer.parseInt(inputString));
		Intent data = new Intent();
		data.putExtra("posm", posmDetail);
		setResult(RESULT_OK, data);
		finish();
	}

	private void btnCancelClicked(View view) {
		setResult(RESULT_CANCELED);
		finish();
	}
}
