/*
 * Intellectual properties of Supun Lakshan Wanigarathna Dissanayake
 * Copyright (c) 2014, Supun Lakshan Wanigarathna Dissanayake. All rights reserved.
 * Created on : Jun 19, 2014, 4:23:10 PM
 */

package com.ceylon_linux.lucky_lanka.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.ceylon_linux.lucky_lanka.R;
import com.ceylon_linux.lucky_lanka.controller.OutletController;
import com.ceylon_linux.lucky_lanka.model.Invoice;
import com.ceylon_linux.lucky_lanka.model.Outlet;

import java.util.ArrayList;

/**
 * @author Supun Lakshan Wanigarathna Dissanayake
 * @mobile +94711290392
 * @email supunlakshan.xfinity@gmail.com
 */
public class PaymentActivity extends Activity {

	private AutoCompleteTextView outletAuto;
	private ExpandableListView invoiceList;
	private ArrayList<Outlet> outlets;
	private Outlet selectedOutlet;
	private Button btnOk;
	private Button btnCancel;
	private BaseExpandableListAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pending_invoices_page);
		initialize();

		outlets = OutletController.loadOutletsFromDb(PaymentActivity.this);
		outletAuto.setAdapter(new ArrayAdapter<Outlet>(this, android.R.layout.simple_dropdown_item_1line, outlets));

		outletAuto.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
				outletAutoOnItemClicked(adapterView, view, i, l);
			}
		});

		adapter = new BaseExpandableListAdapter() {
			@Override
			public int getGroupCount() {
				return selectedOutlet.getInvoices(PaymentActivity.this).size();
			}

			@Override
			public int getChildrenCount(int groupPosition) {
				return 1;
			}

			@Override
			public Invoice getGroup(int groupPosition) {
				return selectedOutlet.getInvoices(PaymentActivity.this).get(groupPosition);
			}

			@Override
			public Object getChild(int groupPosition, int childPosition) {
				return null;
			}

			@Override
			public long getGroupId(int groupPosition) {
				return groupPosition;
			}

			@Override
			public long getChildId(int groupPosition, int childPosition) {
				return childPosition;
			}

			@Override
			public boolean hasStableIds() {
				return false;
			}

			@Override
			public View getGroupView(int groupPosition, boolean b, View convertView, ViewGroup viewGroup) {
				CategoryViewHolder categoryViewHolder;
				if (convertView == null) {
					LayoutInflater inflater = (LayoutInflater) getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
					convertView = inflater.inflate(R.layout.invoice_item, viewGroup, false);
					categoryViewHolder = new CategoryViewHolder();
					categoryViewHolder.txtInvoiceNo = (TextView) convertView.findViewById(R.id.txtInvoiceNo);
					categoryViewHolder.txtTotal = (TextView) convertView.findViewById(R.id.txtTotal);
					categoryViewHolder.txtDate = (TextView) convertView.findViewById(R.id.txtDate);
					convertView.setTag(categoryViewHolder);
				} else {
					categoryViewHolder = (CategoryViewHolder) convertView.getTag();
				}
				Invoice invoice = getGroup(groupPosition);
				return convertView;
			}

			@Override
			public View getChildView(final int groupPosition, int childPosition, boolean b, View convertView, ViewGroup viewGroup) {
				final Invoice invoice = getGroup(groupPosition);
				LayoutInflater inflater = (LayoutInflater) getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(R.layout.invoice_payment_details_page, viewGroup, false);
				LinearLayout donePaymentList = (LinearLayout) convertView.findViewById(R.id.donePaymentList);
				LinearLayout justMadePaymentList = (LinearLayout) convertView.findViewById(R.id.madePayments);
				TextView txtPendingAmount = (TextView) convertView.findViewById(R.id.txtPendingAmount);
				boolean colorize = true;
				Button btnCash = (Button) convertView.findViewById(R.id.btnCash);
				btnCash.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						final Dialog dialog = new Dialog(PaymentActivity.this);
						dialog.setTitle("Cash Payment");
						dialog.setContentView(R.layout.cash_data_input_dialog_page);
						Button btnOk = (Button) dialog.findViewById(R.id.btnOk);
						Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel);
						final EditText inputAmount = (EditText) dialog.findViewById(R.id.inputAmount);
						btnOk.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View view) {
								invoiceList.collapseGroup(groupPosition);
								invoiceList.expandGroup(groupPosition);
								dialog.dismiss();
							}
						});
						btnCancel.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View view) {
								dialog.dismiss();
							}
						});
						dialog.show();
					}
				});
				Button btnCheque = (Button) convertView.findViewById(R.id.btnCheque);
				btnCheque.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						final Dialog dialog = new Dialog(PaymentActivity.this);
						dialog.setTitle("Cheque Payment");
						dialog.setContentView(R.layout.cheque_data_input_dialog_page);
						final Spinner bankCombo = (Spinner) dialog.findViewById(R.id.bankCombo);
						//ArrayAdapter<Bank> adapter = new ArrayAdapter<Bank>(PaymentActivity.this, android.R.layout.simple_spinner_item, BankController.getBanks());
						//adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
						//bankCombo.setAdapter(adapter);
						Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel);
						Button btnOk = (Button) dialog.findViewById(R.id.btnOk);
						final EditText inputAmount = (EditText) dialog.findViewById(R.id.inputAmount);
						final EditText inputChequeNo = (EditText) dialog.findViewById(R.id.inputChequeNo);

						btnOk.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View view) {

								invoiceList.collapseGroup(groupPosition);
								invoiceList.expandGroup(groupPosition);
								dialog.dismiss();
							}
						});
						btnCancel.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View view) {
								dialog.dismiss();
							}
						});
						dialog.show();
					}
				});
				return convertView;
			}

			@Override
			public boolean isChildSelectable(int groupPosition, int childPosition) {
				return false;
			}
		};

	}

	private void outletAutoOnItemClicked(AdapterView<?> adapterView, View view, int position, long id) {
		selectedOutlet = (Outlet) adapterView.getAdapter().getItem(position);
		adapter.notifyDataSetChanged();
		invoiceList.setAdapter(adapter);
	}

	@Override
	public void onBackPressed() {
		Intent homeActivity = new Intent(this, HomeActivity.class);
		startActivity(homeActivity);
		finish();
	}

	// <editor-fold defaultstate="collapsed" desc="Initialize">
	private void initialize() {
		outletAuto = (AutoCompleteTextView) findViewById(R.id.outletAuto);
		invoiceList = (ExpandableListView) findViewById(R.id.invoiceList);
		btnOk = (Button) findViewById(R.id.btnOk);
		btnCancel = (Button) findViewById(R.id.btnCancel);
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
	}
	// </editor-fold>

	private void btnCancelClicked(View view) {
		Intent homeActivity = new Intent(PaymentActivity.this, HomeActivity.class);
		startActivity(homeActivity);
		finish();
		Toast.makeText(this, "Payments Saved Successfully", Toast.LENGTH_LONG).show();
	}

	private void btnOkClicked(View view) {
		boolean response = true;
		if (response) {
			Intent homeActivity = new Intent(PaymentActivity.this, HomeActivity.class);
			startActivity(homeActivity);
			finish();
			Toast.makeText(this, "Payments Saved Successfully", Toast.LENGTH_LONG).show();
		} else {
			AlertDialog.Builder dialog = new AlertDialog.Builder(PaymentActivity.this);
			dialog.setTitle(R.string.app_name);
			dialog.setMessage("Unable to place Payments");
			dialog.setPositiveButton("Ok", null);
			dialog.show();
		}
	}

	private static class CategoryViewHolder {

		TextView txtInvoiceNo;
		TextView txtTotal;
		TextView txtDate;
	}
}
