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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.my_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.sync:
				new Thread() {
					private ProgressDialog progressDialog;
					private Handler handler = new Handler();
					private int response;

					@Override
					public void run() {
						handler.post(new Runnable() {
							@Override
							public void run() {
								progressDialog = ProgressDialog.show(HomeActivity.this, null, "Syncing Orders");
							}
						});
						try {
							ArrayList<UnloadingItem> unLoadingStock = ItemController.getUnLoadingStock(HomeActivity.this);
							JSONArray unLoadingStockJson = new JSONArray();
							for (UnloadingItem unloadingItem : unLoadingStock) {
								unLoadingStockJson.put(unloadingItem.getUnLoadingItemAsJson());
							}
							response = ItemController.syncUnloading(HomeActivity.this, unLoadingStockJson, UserController.getAuthorizedUser(HomeActivity.this).getPositionId());
							if (response == ItemController.UNLOADING_CONFIRMED) {
								publishProgress("Unloading Confirmed");
								UserController.confirmUnloading(HomeActivity.this);
								OutletController.syncOutstandingPayments(HomeActivity.this);
								response = OrderController.syncUnSyncedOrders(HomeActivity.this);
							}
						} catch (IOException ex) {
							ex.printStackTrace();
							response = OrderController.UNABLE_TO_SYNC_ORDERS;
						} catch (JSONException ex) {
							ex.printStackTrace();
							response = OrderController.UNABLE_TO_SYNC_ORDERS;
						}
						handler.post(new Runnable() {
							@Override
							public void run() {
								if (progressDialog != null && progressDialog.isShowing()) {
									progressDialog.dismiss();
								}
								switch (response) {
									case OrderController.UNABLE_TO_SYNC_ORDERS:
										Toast.makeText(HomeActivity.this, "Unable to Sync Orders", Toast.LENGTH_LONG).show();
										break;
									case OrderController.ORDERS_ALREADY_SYNCED:
										Toast.makeText(HomeActivity.this, "Already Synced", Toast.LENGTH_LONG).show();
										break;
									case OrderController.ORDERS_SYNCED_SUCCESSFULLY:
										Toast.makeText(HomeActivity.this, "Orders Synced Successfully", Toast.LENGTH_LONG).show();
										break;
								}
							}
						});
					}

					private void publishProgress(final String progress) {
						handler.post(new Runnable() {
							@Override
							public void run() {
								Toast.makeText(HomeActivity.this, progress, Toast.LENGTH_LONG).show();
							}
						});
					}
				}.start();
				return true;
			case R.id.stock:
				//
				return true;
			default:
				return false;
		}
	}

	@Override
	public void onBackPressed() {
		AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
		builder.setTitle(R.string.app_name);
		builder.setMessage("You are about to sign out from sales pad\nIf you continue your un-synced data will be lost");
		builder.setPositiveButton("Sign out", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				new Thread() {
					private ProgressDialog progressDialog;
					private int response;
					private Handler handler = new Handler();

					@Override
					public void run() {
						handler.post(new Runnable() {
							@Override
							public void run() {
								progressDialog = ProgressDialog.show(HomeActivity.this, null, "Syncing Orders");
							}
						});
						try {
							ArrayList<UnloadingItem> unLoadingStock = ItemController.getUnLoadingStock(HomeActivity.this);
							JSONArray unLoadingStockJson = new JSONArray();
							for (UnloadingItem unloadingItem : unLoadingStock) {
								unLoadingStockJson.put(unloadingItem.getUnLoadingItemAsJson());
							}
							response = ItemController.syncUnloading(HomeActivity.this, unLoadingStockJson, UserController.getAuthorizedUser(HomeActivity.this).getPositionId());
							if (response == ItemController.UNLOADING_CONFIRMED) {
								UserController.confirmUnloading(HomeActivity.this);
								response = OrderController.syncUnSyncedOrders(HomeActivity.this);
							}
						} catch (IOException ex) {
							ex.printStackTrace();
							response = OrderController.UNABLE_TO_SYNC_ORDERS;
						} catch (JSONException ex) {
							ex.printStackTrace();
							response = OrderController.UNABLE_TO_SYNC_ORDERS;
						}
						handler.post(new Runnable() {
							@Override
							public void run() {
								if (progressDialog != null && progressDialog.isShowing()) {
									progressDialog.dismiss();
								}
								switch (response) {
									case OrderController.UNABLE_TO_SYNC_ORDERS:
										Toast.makeText(HomeActivity.this, "Unable to Sync Orders/UnProductive Calls", Toast.LENGTH_LONG).show();
										return;
									case OrderController.ORDERS_ALREADY_SYNCED:
										Toast.makeText(HomeActivity.this, "Already Synced", Toast.LENGTH_LONG).show();
										break;
									case OrderController.ORDERS_SYNCED_SUCCESSFULLY:
										Toast.makeText(HomeActivity.this, "Orders Synced Successfully", Toast.LENGTH_LONG).show();
										break;
								}
								UserController.clearAuthentication(HomeActivity.this);
								Intent loginActivity = new Intent(HomeActivity.this, LoginActivity.class);
								startActivity(loginActivity);
								finish();
							}
						});
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
