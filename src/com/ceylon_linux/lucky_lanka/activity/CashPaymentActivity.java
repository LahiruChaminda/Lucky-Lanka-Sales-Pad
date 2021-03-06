/*
 * Intellectual properties of Supun Lakshan Wanigarathna Dissanayake
 * Copyright (c) 2014, Supun Lakshan Wanigarathna Dissanayake. All rights reserved.
 * Created on : Jul 09, 2014, 2:18 PM
 */
package com.ceylon_linux.lucky_lanka.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.ceylon_linux.lucky_lanka.R;
import com.ceylon_linux.lucky_lanka.model.Payment;

/**
 * @author Supun Lakshan Wanigarathna Dissanayake
 * @mobile +94711290392
 * @email supunlakshan.xfinity@gmail.com
 */
public class CashPaymentActivity extends Activity {

	private EditText inputAmount;
	private Button btnOk;
	private Button btnCancel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cash_data_input_dialog_page);
		initialize();
	}

	private void initialize() {
		inputAmount = (EditText) findViewById(R.id.inputAmount);
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

	private void btnCancelClicked(View view) {
		Intent intent = new Intent();
		setResult(RESULT_CANCELED, intent);
		finish();
	}

	private void btnOkClicked(View view) {
		String amountString = inputAmount.getText().toString();
		double amount = amountString.isEmpty() ? 0 : Double.parseDouble(amountString);
		if (amount <= 0) {
			return;
		}
		Intent intent = new Intent();
		Payment payment = new Payment(amount, Payment.FRESH_PAYMENT);
		intent.putExtra("payment", payment);
		setResult(RESULT_OK, intent);
		finish();
	}
}
