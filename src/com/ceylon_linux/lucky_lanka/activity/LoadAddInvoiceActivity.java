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
import android.view.View;
import android.widget.*;
import com.ceylon_linux.lucky_lanka.R;
import com.ceylon_linux.lucky_lanka.controller.OutletController;
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.load_add_invoice_page);
		initialize();
		routes = OutletController.loadRoutesFromDb(LoadAddInvoiceActivity.this);
		ArrayAdapter<Route> routeAdapter = new ArrayAdapter<Route>(LoadAddInvoiceActivity.this, android.R.layout.simple_spinner_item, routes);
		routeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		routeAuto.setAdapter(routeAdapter);

		outletAdapter = new ArrayAdapter<Outlet>(LoadAddInvoiceActivity.this, android.R.layout.simple_spinner_item, outlets);
		outletAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

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
						simpleDateFormat.applyPattern("hh:mm:ss aa");
						txtTime.setText(simpleDateFormat.format(date));
					}
				});
			}
		}, new Date(), 1000);


	}

	// <editor-fold defaultstate="collapsed" desc="Initialize">
	private void initialize() {
		btnNext = (Button) findViewById(R.id.btnNext);
		txtDate = (TextView) findViewById(R.id.txtDate);
		txtTime = (TextView) findViewById(R.id.txtTime);
		txtRoutine = (TextView) findViewById(R.id.txtRoutine);
		routeAuto = (Spinner) findViewById(R.id.routeAuto);
		outletAuto = (Spinner) findViewById(R.id.outletAuto);
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
		outletAuto.requestFocus();
	}

	private void outletAutoItemClicked(AdapterView<?> adapterView, View view, int position, long id) {
		outlet = (Outlet) adapterView.getAdapter().getItem(position);
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
}
