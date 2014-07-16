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
import android.widget.*;
import com.ceylon_linux.lucky_lanka.R;
import com.ceylon_linux.lucky_lanka.controller.BankController;
import com.ceylon_linux.lucky_lanka.model.Bank;
import com.ceylon_linux.lucky_lanka.model.Payment;

import java.util.ArrayList;
import java.util.Date;

/**
 * @author Supun Lakshan Wanigarathna Dissanayake
 * @mobile +94711290392
 * @email supunlakshan.xfinity@gmail.com
 */
public class ChequePaymentActivity extends Activity {
	private final ArrayList<Bank.BankBranch> BANK_BRANCHES = new ArrayList<Bank.BankBranch>();
	private EditText inputAmount;
	private EditText inputChequeNo;
	private DatePicker datePicker;
	private Spinner bankCombo;
	private Spinner branchCombo;
	private Button btnOk;
	private Button btnCancel;
	private ArrayAdapter<Bank.BankBranch> bankBranchArrayAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cheque_data_input_dialog_page);
		initialize();
	}

	private void initialize() {
		inputAmount = (EditText) findViewById(R.id.inputAmount);
		inputChequeNo = (EditText) findViewById(R.id.inputChequeNo);
		datePicker = (DatePicker) findViewById(R.id.datePicker);
		bankCombo = (Spinner) findViewById(R.id.bankCombo);
		branchCombo = (Spinner) findViewById(R.id.branchCombo);
		bankCombo.setAdapter(new ArrayAdapter<Bank>(ChequePaymentActivity.this, R.layout.spinner_layout, BankController.getBanks(ChequePaymentActivity.this)));
		bankCombo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				bankComboItemClicked(parent, view, position, id);
			}
		});
		bankBranchArrayAdapter = new ArrayAdapter<Bank.BankBranch>(ChequePaymentActivity.this, R.layout.spinner_layout, BANK_BRANCHES);
		branchCombo.setAdapter(bankBranchArrayAdapter);
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

	private void bankComboItemClicked(AdapterView<?> parent, View view, int position, long id) {
		Bank bank = (Bank) parent.getAdapter().getItem(position);
		BANK_BRANCHES.clear();
		BANK_BRANCHES.addAll(bank.getBankBranches());
		bankBranchArrayAdapter.notifyDataSetChanged();
		branchCombo.setAdapter(bankBranchArrayAdapter);
	}

	private void btnOkClicked(View view) {
		String amountString = inputAmount.getText().toString();
		double amount = amountString.isEmpty() ? 0 : Double.parseDouble(amountString);
		if (amount <= 0) {
			return;
		}
		Intent intent = new Intent();
		Payment payment = new Payment(amount, new Date(datePicker.getCalendarView().getDate()), inputChequeNo.getText().toString(), bankCombo.getSelectedItem().toString(), ((Bank.BankBranch) branchCombo.getSelectedItem()).getBranchId());
		intent.putExtra("payment", payment);
		setResult(RESULT_OK, intent);
		finish();
	}

	private void btnCancelClicked(View view) {
		Intent intent = new Intent();
		setResult(RESULT_CANCELED, intent);
		finish();
	}
}
