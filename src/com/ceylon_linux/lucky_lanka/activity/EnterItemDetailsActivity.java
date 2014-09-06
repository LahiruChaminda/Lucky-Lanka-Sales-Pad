/*
 * Intellectual properties of Supun Lakshan Wanigarathna Dissanayake
 * Copyright (c) 2014, Supun Lakshan Wanigarathna Dissanayake. All rights reserved.
 * Created on : Jul 09, 2014, 3:27 PM
 */
package com.ceylon_linux.lucky_lanka.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.ceylon_linux.lucky_lanka.R;
import com.ceylon_linux.lucky_lanka.model.Item;
import com.ceylon_linux.lucky_lanka.model.OrderDetail;
import com.ceylon_linux.lucky_lanka.model.Outlet;

/**
 * @author Supun Lakshan Wanigarathna Dissanayake
 * @mobile +94711290392
 * @email supunlakshan.xfinity@gmail.com
 */
public class EnterItemDetailsActivity extends Activity {

	private Item item;
	private Outlet outlet;
	private OrderDetail orderDetail;
	private TextView txtItemDescription;
	private EditText inputRequestedQuantity;
	private EditText inputReturnQuantity;
	private EditText inputReplaceQuantity;
	private EditText inputSampleQuantity;
	private TextView txtFreeQuantity;
	private TextView txtDiscount;
	private Button btnOk;
	private Button btnCancel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.quantity_insert_page);
		initialize();
	}

	private void initialize() {
		Intent intent = getIntent();
		item = (Item) intent.getSerializableExtra("item");
		outlet = (Outlet) intent.getSerializableExtra("outlet");
		if (intent.hasExtra("orderDetail")) {
			orderDetail = (OrderDetail) intent.getSerializableExtra("orderDetail");
		}
		btnOk = (Button) findViewById(R.id.btnOk);
		txtItemDescription = (TextView) findViewById(R.id.txtItemDescription);
		inputRequestedQuantity = (EditText) findViewById(R.id.inputRequestedQuantity);
		inputReturnQuantity = (EditText) findViewById(R.id.inputReturnQuantity);
		inputReplaceQuantity = (EditText) findViewById(R.id.inputReplaceQuantity);
		inputSampleQuantity = (EditText) findViewById(R.id.inputSampleQuantity);
		txtFreeQuantity = (TextView) findViewById(R.id.txtFreeQuantity);
		txtDiscount = (TextView) findViewById(R.id.txtDiscount);
		btnCancel = (Button) findViewById(R.id.btnCancel);
		txtItemDescription.setText(item.getItemDescription());
		if (orderDetail != null) {
			inputRequestedQuantity.setText(String.valueOf(orderDetail.getQuantity()));
			inputReturnQuantity.setText(String.valueOf(orderDetail.getReturnQuantity()));
			inputReplaceQuantity.setText(String.valueOf(orderDetail.getReplaceQuantity()));
			inputSampleQuantity.setText(String.valueOf(orderDetail.getSampleQuantity()));
			txtFreeQuantity.setText(String.valueOf(orderDetail.getFreeIssue()));
		}
		boolean quantityAvailable;
		inputRequestedQuantity.setEnabled(quantityAvailable = item.getAvailableQuantity() != 0);
		inputReplaceQuantity.setEnabled(quantityAvailable);
		inputSampleQuantity.setEnabled(quantityAvailable);
		inputRequestedQuantity.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
			}

			@Override
			public void afterTextChanged(Editable editable) {
				String requestedQuantityString = inputRequestedQuantity.getText().toString();
				String returnQuantityString = inputReturnQuantity.getText().toString();
				String replaceQuantityString = inputReplaceQuantity.getText().toString();
				String sampleQuantityString = inputSampleQuantity.getText().toString();
				int requestedQuantity = requestedQuantityString.isEmpty() ? 0 : Integer.parseInt(requestedQuantityString);
				int returnQuantity = returnQuantityString.isEmpty() ? 0 : Integer.parseInt(returnQuantityString);
				int replaceQuantity = replaceQuantityString.isEmpty() ? 0 : Integer.parseInt(replaceQuantityString);
				int sampleQuantity = sampleQuantityString.isEmpty() ? 0 : Integer.parseInt(sampleQuantityString);
				OrderDetail orderDetail = OrderDetail.getOrderDetail(outlet, item, requestedQuantity, returnQuantity, replaceQuantity, sampleQuantity, EnterItemDetailsActivity.this);
				txtFreeQuantity.setText(Integer.toString(orderDetail.getFreeIssue()));
				txtDiscount.setText(Double.toString(outlet.getOutletDiscount()));
			}
		});
		btnOk.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				btnOkClicked(view);
			}
		});
		btnCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				btnCancelClicked(view);
			}
		});
		inputReturnQuantity.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				inputReplaceQuantity.setText(inputReturnQuantity.getText());
			}
		});
	}

	private void btnOkClicked(View view) {
		String requestedQuantityString = inputRequestedQuantity.getText().toString();
		String returnQuantityString = inputReturnQuantity.getText().toString();
		String replaceQuantityString = inputReplaceQuantity.getText().toString();
		String sampleQuantityString = inputSampleQuantity.getText().toString();
		int requestedQuantity = requestedQuantityString.isEmpty() ? 0 : Integer.parseInt(requestedQuantityString);
		int returnQuantity = returnQuantityString.isEmpty() ? 0 : Integer.parseInt(returnQuantityString);
		int replaceQuantity = replaceQuantityString.isEmpty() ? 0 : Integer.parseInt(replaceQuantityString);
		int sampleQuantity = sampleQuantityString.isEmpty() ? 0 : Integer.parseInt(sampleQuantityString);
		OrderDetail orderDetail = OrderDetail.getOrderDetail(outlet, item, requestedQuantity, returnQuantity, replaceQuantity, sampleQuantity, EnterItemDetailsActivity.this);
		orderDetail.setFreeIssue(0);
		Intent intent = new Intent();
		intent.putExtra("orderDetail", orderDetail);
		setResult(RESULT_OK, intent);
		finish();
	}

	private void btnCancelClicked(View view) {
		Intent intent = new Intent();
		setResult(RESULT_CANCELED, intent);
		finish();
	}
}
