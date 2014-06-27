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
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.ceylon_linux.lucky_lanka.R;
import com.ceylon_linux.lucky_lanka.controller.OrderController;
import com.ceylon_linux.lucky_lanka.controller.UserController;
import com.ceylon_linux.lucky_lanka.model.User;
import org.json.JSONException;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Supun Lakshan Wanigarathna Dissanayake
 * @mobile +94711290392
 * @email supunlakshan.xfinity@gmail.com
 */
public class HomeActivity extends Activity {

	private Button btnStart;
	private TextView txtName;
	private TextView txtAddress;
	private TextView txtUserName;
	private Button btnSignOut;

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
		new AsyncTask<Void, Void, Boolean>() {
			ProgressDialog progressDialog;

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				progressDialog = new ProgressDialog(HomeActivity.this);
				progressDialog.setCanceledOnTouchOutside(false);
				progressDialog.setCancelable(false);
				progressDialog.setMessage("Syncing Orders");
				progressDialog.show();
			}

			@Override
			protected Boolean doInBackground(Void... params) {
				try {
					return OrderController.syncUnSyncedOrders(HomeActivity.this);
				} catch (IOException ex) {
					Logger.getLogger(HomeActivity.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
					return false;
				} catch (JSONException ex) {
					Logger.getLogger(HomeActivity.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
					return false;
				}
			}

			@Override
			protected void onPostExecute(Boolean response) {
				if (progressDialog != null && progressDialog.isShowing()) {
					progressDialog.dismiss();
				}
				if (response) {
					Toast.makeText(HomeActivity.this, "Orders Synced Successfully", Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(HomeActivity.this, "Unable to Sync Orders", Toast.LENGTH_LONG).show();
				}
			}
		}.execute();
		return true;
	}

	@Override
	public void onBackPressed() {
		AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
		builder.setTitle(R.string.app_name);
		builder.setMessage("You are about to sign out from sales pad\nIf you continue your un-synced data will be lost");
		builder.setPositiveButton("Sign out", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				UserController.clearAuthentication(HomeActivity.this);
				Intent loginActivity = new Intent(HomeActivity.this, LoginActivity.class);
				startActivity(loginActivity);
				finish();
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
	}
	// </editor-fold>

	private void btnStartClicked(View view) {
		Intent loadAddInvoiceActivity = new Intent(HomeActivity.this, LoadAddInvoiceActivity.class);
		startActivity(loadAddInvoiceActivity);
		finish();
	}

	private void btnSignOutClicked(View view) {
		onBackPressed();
	}
}
