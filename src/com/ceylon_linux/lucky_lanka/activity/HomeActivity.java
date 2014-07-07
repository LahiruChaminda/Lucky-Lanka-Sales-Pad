/*
 * Intellectual properties of Supun Lakshan Wanigarathna Dissanayake
 * Copyright (c) 2014, Supun Lakshan Wanigarathna Dissanayake. All rights reserved.
 * Created on : Jun 10, 2014, 11:22:52 AM
 */

package com.ceylon_linux.lucky_lanka.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

/**
 * @author Supun Lakshan Wanigarathna Dissanayake
 * @mobile +94711290392
 * @email supunlakshan.xfinity@gmail.com
 */
public class HomeActivity extends Activity implements Runnable {

	//-----------------------------------------------------------------------------------------
	private static final int REQUEST_CONNECT_DEVICE = 1;
	private static final int REQUEST_ENABLE_BT = 2;
	Button mPrint;
	BluetoothAdapter mBluetoothAdapter;
	BluetoothDevice mBluetoothDevice;
	private Button btnStart;
	private TextView txtName;
	private TextView txtAddress;
	private TextView txtUserName;
	private Button btnPayment;
	private Button btnSignOut;
	private Button btnConnectPrinter;
	private Button btnDisconnectPrinter;
	private UUID applicationUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	private ProgressDialog mBluetoothConnectProgressDialog;
	private BluetoothSocket mBluetoothSocket;
	//---------------------------------------------------------------------------------------------
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			mBluetoothConnectProgressDialog.dismiss();
			Toast.makeText(HomeActivity.this, "DeviceConnected", Toast.LENGTH_LONG).show();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home_page);
		initialize();
		User authorizedUser = UserController.getAuthorizedUser(this);
		txtName.setText(authorizedUser.getName());
		txtAddress.setText(authorizedUser.getAddress());
		txtUserName.setText(authorizedUser.getUserName());
		//-----------
		mPrint = (Button) findViewById(R.id.mPrint);
		mPrint.setOnClickListener(new View.OnClickListener() {
			public void onClick(View mView) {
				Thread t = new Thread() {
					public void run() {
						try {
							OutputStream os = mBluetoothSocket.getOutputStream();
							String BILL = "Invoice No: ABCDEF28060000005" + "    " + "04-08-2011\n";
							BILL = BILL + "-----------------------------------------";
							BILL = BILL + "\n\n";
							BILL = BILL + "Total Qty:" + "      " + "2.0\n";
							BILL = BILL + "Total Value:" + "     " + "17625.0\n";
							BILL = BILL + "-----------------------------------------\n";
							os.write(BILL.getBytes());
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				};
				t.start();
			}
		});
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
					ex.printStackTrace();
					return false;
				} catch (JSONException ex) {
					ex.printStackTrace();
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
/*

		try {
			if (mBluetoothSocket != null)
				mBluetoothSocket.close();
		} catch (Exception e) {
			Log.e("Tag", "Exe ", e);
		}
		setResult(RESULT_CANCELED);
		finish();*/
	}
	// </editor-fold>

	// <editor-fold defaultstate="collapsed" desc="Initialize">
	private void initialize() {
		btnStart = (Button) findViewById(R.id.btnStart);
		btnPayment = (Button) findViewById(R.id.btnPayment);
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
		btnPayment.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				btnPaymentClicked(view);
			}
		});
		btnConnectPrinter = (Button) findViewById(R.id.btnConnectPrinter);
		btnConnectPrinter.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				btnConnectPrinterClicked(view);
			}
		});
		btnDisconnectPrinter = (Button) findViewById(R.id.btnDisconnectPrinter);
		btnDisconnectPrinter.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				btnDisconnectPrinterClicked(view);
			}
		});
		btnSignOut.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				btnSignOutClicked(view);
			}
		});
	}

	private void btnConnectPrinterClicked(View view) {
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBluetoothAdapter == null) {
			Toast.makeText(HomeActivity.this, "Message1", Toast.LENGTH_LONG).show();
		} else {
			if (!mBluetoothAdapter.isEnabled()) {
				Intent enableBtIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);
				startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
			} else {
				ListPairedDevices();
				Intent connectIntent = new Intent(HomeActivity.this, DeviceListActivity.class);
				startActivityForResult(connectIntent, REQUEST_CONNECT_DEVICE);
			}
		}
	}

	private void btnDisconnectPrinterClicked(View view) {
		if (mBluetoothAdapter != null) {
			mBluetoothAdapter.disable();
		}
	}

	private void btnStartClicked(View view) {
		Intent loadAddInvoiceActivity = new Intent(HomeActivity.this, LoadAddInvoiceActivity.class);
		startActivity(loadAddInvoiceActivity);
		finish();
	}

	private void btnPaymentClicked(View view) {
		Intent paymentActivity = new Intent(HomeActivity.this, PaymentActivity.class);
		startActivity(paymentActivity);
		finish();
	}

	private void btnSignOutClicked(View view) {
		onBackPressed();
	}

	//---
	public void onActivityResult(int mRequestCode, int mResultCode, Intent mDataIntent) {
		super.onActivityResult(mRequestCode, mResultCode, mDataIntent);

		switch (mRequestCode) {
			case REQUEST_CONNECT_DEVICE:
				if (mResultCode == Activity.RESULT_OK) {
					Bundle mExtra = mDataIntent.getExtras();
					String mDeviceAddress = mExtra.getString("DeviceAddress");
					mBluetoothDevice = mBluetoothAdapter.getRemoteDevice(mDeviceAddress);
					mBluetoothConnectProgressDialog = ProgressDialog.show(this, "Connecting...", mBluetoothDevice.getName() + " : " + mBluetoothDevice.getAddress(), true, false);
					Thread mBlutoothConnectThread = new Thread(this);
					mBlutoothConnectThread.start();
				}
				break;

			case REQUEST_ENABLE_BT:
				if (mResultCode == Activity.RESULT_OK) {
					ListPairedDevices();
					Intent connectIntent = new Intent(HomeActivity.this, DeviceListActivity.class);
					startActivityForResult(connectIntent, REQUEST_CONNECT_DEVICE);
				} else {
					Toast.makeText(HomeActivity.this, "Message", Toast.LENGTH_LONG).show();
				}
				break;
		}
	}

	private void ListPairedDevices() {
		Set<BluetoothDevice> mPairedDevices = mBluetoothAdapter.getBondedDevices();
	}

	public void run() {
		try {
			mBluetoothSocket = mBluetoothDevice.createRfcommSocketToServiceRecord(applicationUUID);
			mBluetoothAdapter.cancelDiscovery();
			mBluetoothSocket.connect();
			mHandler.sendEmptyMessage(0);
		} catch (IOException ex) {
			ex.printStackTrace();
			closeSocket(mBluetoothSocket);
			return;
		}
	}

	private void closeSocket(BluetoothSocket nOpenSocket) {
		try {
			nOpenSocket.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		try {
			if (mBluetoothSocket != null)
				mBluetoothSocket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//--
}
