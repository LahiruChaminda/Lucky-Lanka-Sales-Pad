/*
 * Intellectual properties of Supun Lakshan Wanigarathna Dissanayake
 * Copyright (c) 2014, Supun Lakshan Wanigarathna Dissanayake. All rights reserved.
 * Created on : Jun 11, 2014, 4:53 PM
 */

package com.ceylon_linux.lucky_lanka.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import com.ceylon_linux.lucky_lanka.R;
import com.ceylon_linux.lucky_lanka.controller.OutletController;
import com.ceylon_linux.lucky_lanka.controller.UserController;
import com.ceylon_linux.lucky_lanka.model.Invoice;
import com.ceylon_linux.lucky_lanka.model.Outlet;
import com.ceylon_linux.lucky_lanka.model.Route;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author Supun Lakshan Wanigarathna Dissanayake
 * @mobile +94711290392
 * @email supunlakshan.xfinity@gmail.com
 */
public class LoadAddInvoiceActivity extends Activity {

	private final ArrayList<Outlet> outlets = new ArrayList<Outlet>();
	private final int REQUEST_OUTSTANDING_PAYMENTS = 0;
	private Button btnNext;
	private TextView txtDate;
	private TextView txtTime;
	private TextView txtRoutine;
	private Spinner routeAuto;
	private Spinner outletAuto;
	private ArrayList<Route> routes;
	private Handler handler;
	private Timer timer;
	private ArrayAdapter<Outlet> outletAdapter;
	private Outlet outlet;
	private LinearLayout pendingDetailsTable;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.load_add_invoice_page);
		initialize();
		txtRoutine.setText(Integer.toString(UserController.getAuthorizedUser(LoadAddInvoiceActivity.this).getRoutineId()));
		routes = OutletController.loadRoutesFromDb(LoadAddInvoiceActivity.this);

		ArrayAdapter<Route> routeAdapter = new ArrayAdapter<Route>(LoadAddInvoiceActivity.this, R.layout.spinner_layout, routes);
		routeAdapter.setDropDownViewResource(R.layout.spinner_dropdown_layout);
		routeAuto.setAdapter(routeAdapter);

		outletAdapter = new ArrayAdapter<Outlet>(LoadAddInvoiceActivity.this, R.layout.spinner_layout, outlets);
		outletAdapter.setDropDownViewResource(R.layout.spinner_dropdown_layout);

		handler = new Handler();
		final SimpleDateFormat simpleDateFormat = new SimpleDateFormat();
		timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				handler.post(new Runnable() {
					@Override
					public void run() {
						Date date = new Date();
						simpleDateFormat.applyPattern("EEEE, dd MMMM, yyyy");
						txtDate.setText(simpleDateFormat.format(date));
						simpleDateFormat.applyPattern("hh:mm aa");
						txtTime.setText(simpleDateFormat.format(date));
					}
				});
			}
		}, new Date(), 60000);


	}

	// <editor-fold defaultstate="collapsed" desc="Initialize">
	private void initialize() {
		btnNext = (Button) findViewById(R.id.btnNext);
		txtDate = (TextView) findViewById(R.id.txtDate);
		txtTime = (TextView) findViewById(R.id.txtTime);
		txtRoutine = (TextView) findViewById(R.id.txtRoutine);
		routeAuto = (Spinner) findViewById(R.id.routeAuto);
		outletAuto = (Spinner) findViewById(R.id.outletAuto);
		pendingDetailsTable = (LinearLayout) findViewById(R.id.pendingDetailsTable);
		btnNext.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				btnNextClicked(view);
			}
		});
		routeAuto.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				routeAutoItemClicked(parent, view, position, id);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});
		outletAuto.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				outletAutoItemClicked(parent, view, position, id);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});
	}
	// </editor-fold>


	@Override
	public void onBackPressed() {
		Intent homeActivity = new Intent(LoadAddInvoiceActivity.this, HomeActivity.class);
		startActivity(homeActivity);
		finish();
	}

	private void routeAutoItemClicked(AdapterView<?> adapterView, View view, int position, long id) {
		Route route = (Route) adapterView.getAdapter().getItem(position);
		outlets.clear();
		outlets.addAll(route.getOutlets());
		outletAdapter.notifyDataSetChanged();
		outletAuto.setAdapter(outletAdapter);
		outlet = null;
		pendingDetailsTable.removeAllViews();
		outletAuto.requestFocus();
	}

	private void outletAutoItemClicked(AdapterView<?> adapterView, View view, int position, long id) {
		outlet = (Outlet) adapterView.getAdapter().getItem(position);
		String outletType;
		switch (outlet.getOutletType()) {
			case Outlet.PHARMACY:
			case Outlet.RETAIL_OUTLET:
			case Outlet.CANTEEN:
			case Outlet.BAKER:
			case Outlet.HOTEL:
			case Outlet.WELFARE_SHOPS:
			case Outlet.OTHER:
			case Outlet.STORES:
				outletType = "Normal Outlet";
				break;
			case Outlet.WHOLESALE_OUTLET:
				outletType = "6+1 Outlet";
				break;
			case Outlet.SUPER_MARKET:
				outletType = "Super Market";
				break;
			case Outlet.SPECIAL_DISCOUNT_WITH_FREE:
				outletType = "Discount with free";
				break;
			case Outlet.SPECIAL_DISCOUNT_WITHOUT_FREE:
				outletType = "Discount without free";
				break;
			default:
				outletType = "Unknown outlet type";
				throw new IllegalArgumentException("Unknown outlet type");
		}
		Toast.makeText(LoadAddInvoiceActivity.this, outletType, Toast.LENGTH_SHORT).show();
		pendingDetailsTable.removeAllViews();
		ArrayList<Invoice> invoices = outlet.getInvoices(LoadAddInvoiceActivity.this);
		LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		SimpleDateFormat dateFormat = new SimpleDateFormat("yy-MM-dd");
		for (final Invoice invoice : invoices) {
			View pendingDetailsItemView = layoutInflater.inflate(R.layout.pending_details_item, null);
			TextView txtInvoiceNo = (TextView) pendingDetailsItemView.findViewById(R.id.txtInvoiceNo);
			TextView txtDate = (TextView) pendingDetailsItemView.findViewById(R.id.txtDate);
			TextView txtAmount = (TextView) pendingDetailsItemView.findViewById(R.id.txtAmount);
			TextView txtPaid = (TextView) pendingDetailsItemView.findViewById(R.id.txtPaid);
			TextView txtBalance = (TextView) pendingDetailsItemView.findViewById(R.id.txtBalance);

			txtInvoiceNo.setText(String.valueOf(invoice.getInvoiceId()));
			txtDate.setText(dateFormat.format(invoice.getDate()));
			txtAmount.setText(String.valueOf(invoice.getAmount()));
			txtPaid.setText(String.valueOf(invoice.getPaidValue()));
			txtBalance.setText(String.valueOf(invoice.getBalanceValue()));
			/*pendingDetailsItemView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent outstandingPaymentActivity = new Intent(LoadAddInvoiceActivity.this, OutstandingPaymentActivity.class);
					outstandingPaymentActivity.putExtra("invoice", invoice);
					startActivityForResult(outstandingPaymentActivity, REQUEST_OUTSTANDING_PAYMENTS);
				}
			});*/
			pendingDetailsTable.addView(pendingDetailsItemView);
		}
	}

	private void btnNextClicked(View view) {
		if (outlet == null) {
			AlertDialog.Builder alert = new AlertDialog.Builder(LoadAddInvoiceActivity.this);
			alert.setTitle(R.string.app_name);
			alert.setMessage("Please select an outlet");
			alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialogInterface, int i) {
					if (routeAuto.getSelectedItem() == null) {
						routeAuto.requestFocus();
					} else {
						outletAuto.requestFocus();
					}
				}
			});
			alert.show();
			return;
		}
		Intent selectItemsActivity = new Intent(LoadAddInvoiceActivity.this, SelectItemActivity.class);
		selectItemsActivity.putExtra("outlet", outlet);
		startActivity(selectItemsActivity);
		timer.cancel();
		finish();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			case REQUEST_OUTSTANDING_PAYMENTS:
				if (resultCode == RESULT_OK) {
					//
				}
				break;
		}
	}
}
