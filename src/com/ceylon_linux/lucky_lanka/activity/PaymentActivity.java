/*
 * Intellectual properties of Supun Lakshan Wanigarathna Dissanayake
 * Copyright (c) 2014, Supun Lakshan Wanigarathna Dissanayake. All rights reserved.
 * Created on : Jul 07, 2014, 3:11 PM
 */
package com.ceylon_linux.lucky_lanka.activity;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import com.ceylon_linux.lucky_lanka.R;
import com.ceylon_linux.lucky_lanka.model.Payment;

import java.util.ArrayList;

/**
 * @author Supun Lakshan Wanigarathna Dissanayake
 * @mobile +94711290392
 * @email supunlakshan.xfinity@gmail.com
 */
public class PaymentActivity extends Activity {
	private Button btnCashPayment;
	private Button btnChequePayment;
	private Button btnPrintInvoice;
	private ListView listPayment;
	private ArrayList<Payment> payments;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.payments_page);
		initialize();
	}

	private void initialize() {
		btnCashPayment = (Button) findViewById(R.id.btnCashPayment);
		btnChequePayment = (Button) findViewById(R.id.btnChequePayment);
		btnPrintInvoice = (Button) findViewById(R.id.btnPrintInvoice);
		listPayment = (ListView) findViewById(R.id.listPayment);
		payments = new ArrayList<Payment>();

		btnCashPayment.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				btnCashPaymentClicked(view);
			}
		});
		btnChequePayment.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				btnChequePaymentClicked(view);
			}
		});
		btnPrintInvoice.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				btnPrintInvoiceClicked(view);
			}
		});
	}

	private void btnCashPaymentClicked(View view) {
		final Dialog dialog = new Dialog(PaymentActivity.this);
		dialog.setContentView(R.layout.cash_data_input_dialog_page);
		final EditText inputAmount = (EditText) dialog.findViewById(R.id.inputAmount);
		final Button btnOk = (Button) dialog.findViewById(R.id.btnOk);
		final Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel);
		btnOk.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

			}
		});
		btnCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		dialog.show();
	}

	private void btnChequePaymentClicked(View view) {
		final Dialog dialog = new Dialog(PaymentActivity.this);
		dialog.setContentView(R.layout.cheque_data_input_dialog_page);
		final EditText inputAmount = (EditText) dialog.findViewById(R.id.inputAmount);
		final Button btnOk = (Button) dialog.findViewById(R.id.btnOk);
		final Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel);
		btnOk.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

			}
		});
		btnCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		dialog.show();
	}

	private void btnPrintInvoiceClicked(View view) {

	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}
}
