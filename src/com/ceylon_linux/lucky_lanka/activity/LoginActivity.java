/*
 * Intellectual properties of Supun Lakshan Wanigarathna Dissanayake
 * Copyright (c) 2014, Supun Lakshan Wanigarathna Dissanayake. All rights reserved.
 * Created on : Jun 10, 2014, 11:04:59 AM
 */

package com.ceylon_linux.lucky_lanka.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
		new AsyncTask<Void, Integer, Boolean>() {
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
			protected Boolean doInBackground(Void... voids) {
				User user = null;
				try {
					user = UserController.authenticate(LoginActivity.this, inputUserName.getText().toString().trim(), inputPassword.getText().toString().trim());
					if (user != null) {
						UserController.setAuthorizedUser(LoginActivity.this, user);
						OutletController.downloadOutlets(LoginActivity.this, user.getPositionId());
						ItemController.downloadItems(LoginActivity.this, user.getPositionId());
					}

				} catch (IOException e) {
					e.printStackTrace();
				} catch (JSONException e) {
					e.printStackTrace();
				}
				return null;
			}

			@Override
			protected void onPostExecute(Boolean response) {
				super.onPostExecute(response);
				if (progressDialog != null && progressDialog.isShowing()) {
					progressDialog.dismiss();
				}
				Intent homeActivity = new Intent(LoginActivity.this, HomeActivity.class);
				startActivity(homeActivity);
				finish();
			}
		}.execute();
	}
}
