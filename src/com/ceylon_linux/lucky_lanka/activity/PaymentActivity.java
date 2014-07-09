/*
 * Intellectual properties of Supun Lakshan Wanigarathna Dissanayake
 * Copyright (c) 2014, Supun Lakshan Wanigarathna Dissanayake. All rights reserved.
 * Created on : Jul 07, 2014, 3:11 PM
 */
package com.ceylon_linux.lucky_lanka.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import com.ceylon_linux.lucky_lanka.R;
import com.ceylon_linux.lucky_lanka.model.Payment;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

/**
 * @author Supun Lakshan Wanigarathna Dissanayake
 * @mobile +94711290392
 * @email supunlakshan.xfinity@gmail.com
 */
public class PaymentActivity extends Activity implements Runnable {
	private static final int REQUEST_CONNECT_DEVICE = 1;
	private static final int REQUEST_ENABLE_BT = 2;
	private Button btnCashPayment;
	private Button btnChequePayment;
	private Button btnPrintInvoice;
	private ListView listPayment;
	private ArrayList<Payment> payments;
	private BaseAdapter adapter;
	private Handler mHandler;
	private BluetoothAdapter mBluetoothAdapter;
	private BluetoothDevice mBluetoothDevice;
	private Button btnConnectPrinter;
	private Button btnDisconnectPrinter;
	private UUID applicationUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	private ProgressDialog mBluetoothConnectProgressDialog;
	private BluetoothSocket mBluetoothSocket;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.payments_page);
		initialize();
	}

	private void initialize() {
		btnCashPayment = (Button) findViewById(R.id.btnCashPayment);
		btnChequePayment = (Button) findViewById(R.id.btnChequePayment);
		btnPrintInvoice = (Button) findViewById(R.id.btnPrintInvoice);
		listPayment = (ListView) findViewById(R.id.listPayment);
		payments = new ArrayList<Payment>();
		adapter = new BaseAdapter() {
			@Override
			public int getCount() {
				return payments.size();
			}

			@Override
			public Payment getItem(int position) {
				return payments.get(position);
			}

			@Override
			public long getItemId(int position) {
				return position;
			}

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				return null;
			}
		};
		listPayment.setAdapter(adapter);

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

		btnCashPayment.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				btnCashPaymentClicked(view);
			}
		});
		btnChequePayment.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				btnChequePaymentClicked(view);
			}
		});
		btnPrintInvoice.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				btnPrintInvoiceClicked(view);
			}
		});
	}

	private void btnCashPaymentClicked(View view) {
		Intent cashPaymentActivity = new Intent(PaymentActivity.this, CashPaymentActivity.class);
		startActivityForResult(cashPaymentActivity, 0);
	}

	private void btnChequePaymentClicked(View view) {
		Intent chequePaymentActivity = new Intent(PaymentActivity.this, ChequePaymentActivity.class);
		startActivityForResult(chequePaymentActivity, 0);
	}

	private void btnPrintInvoiceClicked(View view) {
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

	private void btnConnectPrinterClicked(View view) {
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBluetoothAdapter == null) {
			Toast.makeText(PaymentActivity.this, "Message1", Toast.LENGTH_LONG).show();
		} else {
			if (!mBluetoothAdapter.isEnabled()) {
				Intent enableBtIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);
				startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
			} else {
				ListPairedDevices();
				Intent connectIntent = new Intent(PaymentActivity.this, DeviceListActivity.class);
				startActivityForResult(connectIntent, REQUEST_CONNECT_DEVICE);
			}
		}
	}

	private void btnDisconnectPrinterClicked(View view) {
		if (mBluetoothAdapter != null) {
			mBluetoothAdapter.disable();
		}
	}

	private void ListPairedDevices() {
		Set<BluetoothDevice> mPairedDevices = mBluetoothAdapter.getBondedDevices();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			Payment payment = (Payment) data.getSerializableExtra("payment");
			payments.add(payment);
			adapter.notifyDataSetChanged();
			listPayment.setAdapter(adapter);
		}
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

/*
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
	}*/
}
