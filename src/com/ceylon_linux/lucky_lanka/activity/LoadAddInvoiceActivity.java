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
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
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

	private Button btnNext;
	private TextView txtDate;
	private TextView txtTime;
	private TextView txtRoutine;
	private AutoCompleteTextView routeAuto;
	private AutoCompleteTextView outletAuto;
	private ArrayList<Route> routes;
	private Handler handler;
	private Timer timer;

	private Outlet outlet;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.load_add_invoice_page);
		initialize();
		routes = OutletController.loadRoutesFromDb(LoadAddInvoiceActivity.this);
		ArrayAdapter<Route> routeAdapter = new ArrayAdapter<Route>(LoadAddInvoiceActivity.this, android.R.layout.simple_dropdown_item_1line, routes) {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				View view = super.getView(position, convertView, parent);
				view.setBackgroundColor((position % 2 == 0) ? Color.parseColor("#E6E6E6") : Color.parseColor("#FFFFFF"));
				return view;
			}
		};
		routeAuto.setAdapter(routeAdapter);
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
						Log.i("running", "still running");
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
		routeAuto = (AutoCompleteTextView) findViewById(R.id.routeAuto);
		outletAuto = (AutoCompleteTextView) findViewById(R.id.outletAuto);
		btnNext.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				btnNextClicked(view);
			}
		});
		routeAuto.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
				routeAutoItemClicked(adapterView, view, position, id);
			}
		});
		outletAuto.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
				outletAutoItemClicked(adapterView, view, position, id);
			}
		});
	}
	// </editor-fold>

	private void routeAutoItemClicked(AdapterView<?> adapterView, View view, int position, long id) {
		Route route = (Route) adapterView.getAdapter().getItem(position);
		ArrayAdapter<Outlet> outletAdapter = new ArrayAdapter<Outlet>(LoadAddInvoiceActivity.this, android.R.layout.simple_list_item_1, route.getOutlets()) {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				View view = super.getView(position, convertView, parent);
				view.setBackgroundColor((position % 2 == 0) ? Color.parseColor("#E6E6E6") : Color.parseColor("#FFFFFF"));
				return view;
			}
		};
		outlet = null;
		outletAuto.clearListSelection();
		outletAuto.setText("");
		outletAuto.setAdapter(outletAdapter);
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
					if (routeAuto.getText().toString().isEmpty()) {
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
