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
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.ceylon_linux.lucky_lanka.R;
import com.ceylon_linux.lucky_lanka.controller.ItemController;
import com.ceylon_linux.lucky_lanka.controller.OutletController;
import com.ceylon_linux.lucky_lanka.controller.UserController;
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
		new AsyncTask<Void, String, User>() {
			private ProgressDialog progressDialog;

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				progressDialog = new ProgressDialog(LoginActivity.this);
				progressDialog.setCanceledOnTouchOutside(false);
				progressDialog.setCancelable(false);
				progressDialog.setMessage("Download Data...");
				progressDialog.show();
			}

			@Override
			protected User doInBackground(Void... voids) {
				User user = null;
				try {
					publishProgress("Authenticating...");
					user = UserController.authenticate(LoginActivity.this, inputUserName.getText().toString().trim(), inputPassword.getText().toString().trim());
					if (user != null && user.isValidUser()) {
						UserController.setAuthorizedUser(LoginActivity.this, user);
						publishProgress("Authenticated");
						OutletController.downloadOutlets(LoginActivity.this, user.getPositionId());
						publishProgress("Outlets Downloaded Successfully");
						ItemController.downloadItems(LoginActivity.this, user.getPositionId());
						publishProgress("Items Downloaded Successfully");
					}
					return user;
				} catch (IOException e) {
					e.printStackTrace();
				} catch (JSONException e) {
					e.printStackTrace();
				}
				return null;
			}

			@Override
			protected void onProgressUpdate(String... values) {
				super.onProgressUpdate(values);
				Toast.makeText(LoginActivity.this, values[0], Toast.LENGTH_SHORT).show();
			}

			@Override
			protected void onPostExecute(User user) {
				super.onPostExecute(user);
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
					alertDialogBuilder.setMessage("Incorrect UserName Password Combination");
					alertDialogBuilder.setPositiveButton("Ok", null);
					alertDialogBuilder.show();
				}
			}
		}.execute();
	}
}
