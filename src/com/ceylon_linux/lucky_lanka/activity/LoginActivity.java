/*
 * Intellectual properties of Supun Lakshan Wanigarathna Dissanayake
 * Copyright (c) 2014, Supun Lakshan Wanigarathna Dissanayake. All rights reserved.
 * Created on : Jun 10, 2014, 11:04:59 AM
 */

package com.ceylon_linux.lucky_lanka.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.ceylon_linux.lucky_lanka.R;
import com.ceylon_linux.lucky_lanka.controller.BankController;
import com.ceylon_linux.lucky_lanka.controller.ItemController;
import com.ceylon_linux.lucky_lanka.controller.OutletController;
import com.ceylon_linux.lucky_lanka.controller.UserController;
import com.ceylon_linux.lucky_lanka.db.SQLiteDatabaseHelper;
import com.ceylon_linux.lucky_lanka.model.User;
import org.json.JSONException;

import java.io.IOException;

/**
 * @author Supun Lakshan Wanigarathna Dissanayake
 * @mobile +94711290392
 * @email supunlakshan.xfinity@gmail.com
 */
public class LoginActivity extends Activity {

	private EditText inputUserName;
	private EditText inputPassword;
	private Button btnLogin;
	private Button btnExit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_page);
		initialize();
		String userName;
		if ((userName = UserController.getPastAuthorizedUserName(LoginActivity.this)) != null) {
			inputUserName.setText(userName);
		}
	}

	// <editor-fold defaultstate="collapsed" desc="Initialize">
	private void initialize() {
		inputUserName = (EditText) findViewById(R.id.inputUserName);
		inputPassword = (EditText) findViewById(R.id.inputPassword);
		btnLogin = (Button) findViewById(R.id.btnLogin);
		btnExit = (Button) findViewById(R.id.btnExit);
		btnLogin.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				btnLoginClicked(view);
			}
		});
		btnExit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				btnExitClicked(view);
			}
		});
	}
	// </editor-fold>

	private void btnExitClicked(View view) {
		finish();
		System.exit(0);
	}

	private void btnLoginClicked(View view) {
		new Thread() {
			private ProgressDialog progressDialog;
			private Handler handler = new Handler();
			private User user;

			@Override
			public void run() {
				handler.post(new Runnable() {
					@Override
					public void run() {
						progressDialog = ProgressDialog.show(LoginActivity.this, null, "Download Data...");
					}
				});

				try {
					publishProgress("Authenticating...");
					user = UserController.authenticate(LoginActivity.this, inputUserName.getText().toString().trim(), inputPassword.getText().toString().trim());
					if (user != null && user.isValidUser()) {
						int positionId = user.getPositionId();
						int routineId = user.getRoutineId();
						SQLiteDatabaseHelper.dropDatabase(LoginActivity.this);
						UserController.setAuthorizedUser(LoginActivity.this, user);
						publishProgress("Authenticated");
						BankController.downloadBanks(LoginActivity.this, positionId);
						publishProgress("Banks Downloaded Successfully");
						OutletController.downloadOutlets(LoginActivity.this, positionId);
						publishProgress("Outlets Downloaded Successfully");
						ItemController.downloadItems(LoginActivity.this, positionId, routineId);
						publishProgress("Items Downloaded Successfully");
						ItemController.downloadPosmItems(LoginActivity.this, positionId, routineId);
						publishProgress("POSM details Downloaded Successfully");
						ItemController.downloadAndSaveFreeIssueCalculationData(LoginActivity.this, positionId, routineId);
					}
				} catch (IOException e) {
					e.printStackTrace();
					user = null;
					UserController.clearAuthentication(LoginActivity.this);
				} catch (JSONException e) {
					e.printStackTrace();
					user = null;
					UserController.clearAuthentication(LoginActivity.this);
				}

				handler.post(new Runnable() {
					@Override
					public void run() {
						if (progressDialog != null && progressDialog.isShowing()) {
							progressDialog.dismiss();
						}
						if (user == null) {
							AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(LoginActivity.this);
							alertDialogBuilder.setTitle(R.string.app_name);
							alertDialogBuilder.setMessage("No Active Internet Connection Found");
							alertDialogBuilder.setPositiveButton("Ok", null);
							alertDialogBuilder.show();
						} else if (user.isValidUser()) {
							Intent homeActivity = new Intent(LoginActivity.this, HomeActivity.class);
							startActivity(homeActivity);
							finish();
						} else {
							AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(LoginActivity.this);
							alertDialogBuilder.setTitle(R.string.app_name);
							alertDialogBuilder.setMessage("Incorrect UserName Password Combination or Loading Unavailable");
							alertDialogBuilder.setPositiveButton("Ok", null);
							alertDialogBuilder.show();
						}
					}
				});
			}

			private void publishProgress(final String message) {
				handler.post(new Runnable() {
					@Override
					public void run() {
						Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
					}
				});
			}
		}.start();
	}
}