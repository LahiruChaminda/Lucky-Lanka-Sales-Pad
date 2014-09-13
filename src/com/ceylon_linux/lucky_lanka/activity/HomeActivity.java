/*
 * Intellectual properties of Supun Lakshan Wanigarathna Dissanayake
 * Copyright (c) 2014, Supun Lakshan Wanigarathna Dissanayake. All rights reserved.
 * Created on : Jun 10, 2014, 11:22:52 AM
 */

package com.ceylon_linux.lucky_lanka.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.ceylon_linux.lucky_lanka.R;
import com.ceylon_linux.lucky_lanka.controller.ItemController;
import com.ceylon_linux.lucky_lanka.controller.OrderController;
import com.ceylon_linux.lucky_lanka.controller.OutletController;
import com.ceylon_linux.lucky_lanka.controller.UserController;
import com.ceylon_linux.lucky_lanka.model.UnloadingItem;
import com.ceylon_linux.lucky_lanka.model.User;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

/**
 * @author Supun Lakshan Wanigarathna Dissanayake
 * @mobile +94711290392
 * @email supunlakshan.xfinity@gmail.com
 */
public class HomeActivity extends Activity {

	private TextView txtName;
	private TextView txtAddress;
	private TextView txtUserName;
	private Button btnSignOut;
	private Button btnStart;
	private Button btnOutletAdd;
	private Button btnAvailableStock;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home_page);
		initialize();
		User authorizedUser = UserController.getAuthorizedUser(this);
		txtName.setText(authorizedUser.getName());
		txtAddress.setText(authorizedUser.getAddress());
		txtUserName.setText(authorizedUser.getUserName());
	}

	@Override
	public void onBackPressed() {
		AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
		builder.setTitle(R.string.app_name);
		builder.setMessage("You are about to sign out and confirm unloading");
		builder.setPositiveButton("Sign Out", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				new Thread() {
					private ProgressDialog progressDialog;
					private String message;
					private Handler handler = new Handler();

					private void publishProgress(final String message) {
						handler.post(new Runnable() {
							@Override
							public void run() {
								Toast.makeText(HomeActivity.this, message, Toast.LENGTH_LONG).show();
							}
						});
					}

					@Override
					public void run() {
						handler.post(new Runnable() {
							@Override
							public void run() {
								progressDialog = ProgressDialog.show(HomeActivity.this, null, "Signing Out");
							}
						});
						try {
							ArrayList<UnloadingItem> unLoadingStock = ItemController.getUnLoadingStock(HomeActivity.this);
							JSONArray unLoadingStockJson = new JSONArray();
							for (UnloadingItem unloadingItem : unLoadingStock) {
								unLoadingStockJson.put(unloadingItem.getUnLoadingItemAsJson());
							}
							int response;
							response = ItemController.syncUnloading(HomeActivity.this, unLoadingStockJson, UserController.getAuthorizedUser(HomeActivity.this).getPositionId(), UserController.getAuthorizedUser(HomeActivity.this).getRoutineId());
							if (response == ItemController.UNABLE_TO_CONFIRM_UNLOADING) {
								message = "Unable to confirm unloading";
								return;
							}
							publishProgress("Unloading confirmed");
							response = OutletController.syncOutstandingPayments(HomeActivity.this);
							publishProgress("Outstanding Payments synced");
							if (response == OutletController.UNABLE_TO_SYNC_OUTSTANDING_PAYMENTS) {
								message = "Unable to sync outstanding payments";
								return;
							}
							UserController.confirmUnloading(HomeActivity.this);
							response = OrderController.syncUnSyncedOrders(HomeActivity.this);
							if (response == OrderController.UNABLE_TO_SYNC_ORDERS) {
								message = "Unable to sync invoices";
								return;
							}
							publishProgress("Invoices Uploaded");

						} catch (IOException ex) {
							ex.printStackTrace();
						} catch (JSONException ex) {
							ex.printStackTrace();
						} finally {
							handler.post(new Runnable() {
								@Override
								public void run() {
									if (progressDialog != null && progressDialog.isShowing()) {
										progressDialog.dismiss();
									}
									if (message != null) {
										Toast.makeText(HomeActivity.this, message, Toast.LENGTH_LONG).show();
										UserController.clearAuthentication(HomeActivity.this);
										Intent loginActivity = new Intent(HomeActivity.this, LoginActivity.class);
										startActivity(loginActivity);
										finish();
									}
								}
							});
						}
					}
				}.start();
			}
		});
		builder.setNegativeButton("Cancel", null);
		builder.show();
	}

	// <editor-fold defaultstate="collapsed" desc="Initialize">
	private void initialize() {
		btnStart = (Button) findViewById(R.id.btnStart);
		btnSignOut = (Button) findViewById(R.id.btnSignOut);
		btnOutletAdd = (Button) findViewById(R.id.btnOutletAdd);
		txtName = (TextView) findViewById(R.id.txtName);
		txtAddress = (TextView) findViewById(R.id.txtAddress);
		btnAvailableStock = (Button) findViewById(R.id.btnAvailableStock);
		txtUserName = (TextView) findViewById(R.id.txtUserName);
		btnStart.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				btnStartClicked(view);
			}
		});
		btnOutletAdd.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				btnOutletAddClicked(v);
			}
		});
		btnSignOut.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				btnSignOutClicked(view);
			}
		});
		btnAvailableStock.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				btnAvailableStockClicked(v);
			}
		});
	}
	// </editor-fold>

	private void btnOutletAddClicked(View view) {
		Intent addOutletActivity = new Intent(HomeActivity.this, AddOutletActivity.class);
		startActivity(addOutletActivity);
		finish();
	}

	private void btnAvailableStockClicked(View view) {
		if (UserController.isLoadingConfirmed(HomeActivity.this)) {
			Intent loadingActivity = new Intent(HomeActivity.this, LoadingActivity.class);
			loadingActivity.putExtra("option", LoadingActivity.VIEW_CURRENT_STOCK);
			startActivity(loadingActivity);
			finish();
		} else {
			AlertDialog.Builder alertBuilder = new AlertDialog.Builder(HomeActivity.this);
			alertBuilder.setMessage("Loading isn't conformed yet. Please confirm it.");
			alertBuilder.setTitle(R.string.app_name);
			alertBuilder.setPositiveButton("Confirm Loading", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Intent loadingActivity = new Intent(HomeActivity.this, LoadingActivity.class);
					loadingActivity.putExtra("option", LoadingActivity.CONFIRM_LOADING);
					startActivity(loadingActivity);
					finish();
				}
			});
			alertBuilder.show();
		}
	}

	private void btnStartClicked(View view) {
		if (UserController.isLoadingConfirmed(HomeActivity.this)) {
			Intent loadAddInvoiceActivity = new Intent(HomeActivity.this, LoadAddInvoiceActivity.class);
			startActivity(loadAddInvoiceActivity);
			finish();
		} else {
			Intent loadingActivity = new Intent(HomeActivity.this, LoadingActivity.class);
			loadingActivity.putExtra("option", LoadingActivity.CONFIRM_LOADING);
			startActivity(loadingActivity);
			finish();
		}
	}

	private void btnSignOutClicked(View view) {
		onBackPressed();
	}
}
