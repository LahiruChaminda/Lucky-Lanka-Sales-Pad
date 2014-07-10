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
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.ceylon_linux.lucky_lanka.R;
import com.ceylon_linux.lucky_lanka.controller.OrderController;
import com.ceylon_linux.lucky_lanka.model.Order;
import com.ceylon_linux.lucky_lanka.model.Payment;
import com.ceylon_linux.lucky_lanka.util.ProgressDialogGenerator;
import org.json.JSONException;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.UUID;

/**
 * @author Supun Lakshan Wanigarathna Dissanayake
 * @mobile +94711290392
 * @email supunlakshan.xfinity@gmail.com
 */
public class PaymentActivity extends Activity {
	private final int REQUEST_CONNECT_DEVICE = 1;
	private final int REQUEST_ENABLE_BLUETOOTH = 2;
	private final int PAYMENT_DONE = 3;
	BluetoothAdapter mBluetoothAdapter;
	BluetoothDevice mBluetoothDevice;
	private Order order;
	private Button btnCashPayment;
	private Button btnChequePayment;
	private Button btnPrintInvoice;
	private ListView listPayment;
	private BaseAdapter adapter;
	private Button btnConnectPrinter;
	private Button btnDisconnectPrinter;
	private UUID applicationUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	private ProgressDialog mBluetoothConnectProgressDialog;
	private BluetoothSocket mBluetoothSocket;
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			mBluetoothConnectProgressDialog.dismiss();
			Toast.makeText(PaymentActivity.this, "Device Connected", Toast.LENGTH_LONG).show();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.payments_page);
		initialize();
	}

	private void initialize() {
		order = (Order) getIntent().getSerializableExtra("order");
		final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd MMMM, yyyy");
		btnCashPayment = (Button) findViewById(R.id.btnCashPayment);
		btnChequePayment = (Button) findViewById(R.id.btnChequePayment);
		btnPrintInvoice = (Button) findViewById(R.id.btnPrintInvoice);
		listPayment = (ListView) findViewById(R.id.listPayment);
		order.setPayments(new ArrayList<Payment>());
		adapter = new BaseAdapter() {
			@Override
			public int getCount() {
				return order.getPayments().size();
			}

			@Override
			public Payment getItem(int position) {
				return order.getPayments().get(position);
			}

			@Override
			public long getItemId(int position) {
				return position;
			}

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				PaymentViewHolder paymentViewHolder;
				if (convertView == null) {
					LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
					convertView = layoutInflater.inflate(R.layout.payment_detail_item, null);
					paymentViewHolder = new PaymentViewHolder();
					paymentViewHolder.txtPaidValue = (TextView) convertView.findViewById(R.id.txtPaidValue);
					paymentViewHolder.txtPaidDate = (TextView) convertView.findViewById(R.id.txtPaidDate);
					paymentViewHolder.txtPaymentMethod = (TextView) convertView.findViewById(R.id.txtPaymentMethod);
					paymentViewHolder.txtChequeNo = (TextView) convertView.findViewById(R.id.txtChequeNo);
					paymentViewHolder.txtBankingDate = (TextView) convertView.findViewById(R.id.txtBankingDate);
					convertView.setTag(paymentViewHolder);
				} else {
					paymentViewHolder = (PaymentViewHolder) convertView.getTag();
				}
				Payment payment = getItem(position);
				boolean isChequePayment = payment.getChequeNo() != null && !payment.getChequeNo().isEmpty();
				paymentViewHolder.txtPaidValue.setText(Double.toString(payment.getAmount()));
				paymentViewHolder.txtPaidDate.setText(dateFormatter.format(payment.getPaymentDate()));
				paymentViewHolder.txtPaymentMethod.setText((isChequePayment) ? "CHEQUE" : "CASH");
				paymentViewHolder.txtChequeNo.setText((isChequePayment) ? payment.getChequeNo() : "");
				paymentViewHolder.txtBankingDate.setText((isChequePayment) ? dateFormatter.format(payment.getChequeDate()) : "");
				return convertView;
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
		startActivityForResult(cashPaymentActivity, PAYMENT_DONE);
	}

	private void btnChequePaymentClicked(View view) {
		Intent chequePaymentActivity = new Intent(PaymentActivity.this, ChequePaymentActivity.class);
		startActivityForResult(chequePaymentActivity, PAYMENT_DONE);
	}

	private void btnPrintInvoiceClicked(View view) {
		new AsyncTask<Void, String, Boolean>() {
			ProgressDialog progressDialog;

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				progressDialog = ProgressDialogGenerator.generateProgressDialog(PaymentActivity.this, "Processing...", false);
				progressDialog.show();
			}

			@Override
			protected Boolean doInBackground(Void... params) {
				try {
					boolean syncStatus = OrderController.syncOrder(PaymentActivity.this, order.getOrderAsJson());
					OutputStream os = mBluetoothSocket.getOutputStream();
					StringBuilder builder = new StringBuilder();
					builder.append("   Lucky Lanka Invoice                          \n");
					builder.append("   ---------------------------------------------\n");
					builder.append("   Outlet : Piyani Stores                       \n");
					builder.append("   Date   : 10 July, 2014                       \n");
					builder.append("   Time   : 14:40 pm                            \n");
					builder.append("   ---------------------------------------------\n");
					builder.append("   Vanilla Milk 100ml           120 x 10 Rs 1200\n");
					builder.append("   Chocolate Milk 100ml         120 x 10 Rs 1200\n");
					builder.append("   ---------------------------------------------\n");
					builder.append("   Total                                 Rs 2400\n");
					builder.append("   ---------------------------------------------\n");
					builder.append("   ABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRS\n\n\n\n");
					os.write(builder.toString().getBytes());
					os.close();
					return syncStatus;
				} catch (IOException e) {
					e.printStackTrace();
				} catch (JSONException e) {
					e.printStackTrace();
				}
				return false;
			}

			@Override
			protected void onProgressUpdate(String... values) {
				Toast.makeText(PaymentActivity.this, values[0], Toast.LENGTH_LONG).show();
			}

			@Override
			protected void onPostExecute(Boolean aBoolean) {
				if (progressDialog != null && progressDialog.isShowing()) {
					progressDialog.dismiss();
				}
				if (aBoolean) {
					Toast.makeText(PaymentActivity.this, "Order Synced Successfully", Toast.LENGTH_LONG).show();
				} else {
					OrderController.saveOrderToDb(PaymentActivity.this, order);
					Toast.makeText(PaymentActivity.this, "Order placed in local database", Toast.LENGTH_LONG).show();
				}
			}
		}.execute();
		new Thread() {
			public void run() {
				try {
					//
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();
	}

	private void btnConnectPrinterClicked(View view) {
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBluetoothAdapter == null) {
			Toast.makeText(PaymentActivity.this, "Bluetooth Not Supported", Toast.LENGTH_LONG).show();
		} else {
			if (!mBluetoothAdapter.isEnabled()) {
				Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
				startActivityForResult(enableBtIntent, REQUEST_ENABLE_BLUETOOTH);
			} else {
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

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			case REQUEST_CONNECT_DEVICE:
				if (resultCode == Activity.RESULT_OK) {
					Bundle mExtra = data.getExtras();
					String mDeviceAddress = mExtra.getString("DeviceAddress");
					mBluetoothDevice = mBluetoothAdapter.getRemoteDevice(mDeviceAddress);
					mBluetoothConnectProgressDialog = ProgressDialog.show(this, "Connecting...", mBluetoothDevice.getName() + " : " + mBluetoothDevice.getAddress(), true, false);
					new Thread(new Runnable() {
						@Override
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
					}).start();
				}
				break;
			case REQUEST_ENABLE_BLUETOOTH:
				if (resultCode == Activity.RESULT_OK) {
					Intent connectIntent = new Intent(PaymentActivity.this, DeviceListActivity.class);
					startActivityForResult(connectIntent, REQUEST_CONNECT_DEVICE);
				} else {
					Toast.makeText(PaymentActivity.this, "Unable to Start Bluetooth", Toast.LENGTH_LONG).show();
				}
				break;
			case PAYMENT_DONE:
				if (resultCode == RESULT_OK) {
					Payment payment = (Payment) data.getSerializableExtra("payment");
					order.getPayments().add(payment);
					adapter.notifyDataSetChanged();
					listPayment.setAdapter(adapter);
				}
				break;
		}
	}

	private void closeSocket(BluetoothSocket nOpenSocket) {
		try {
			nOpenSocket.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	private byte[] getOrderIntoByteStream(Order order) {

		return null;
	}

	private String getAlignedString(String snippet, int length) {
		if (snippet.length() > length) {
			return snippet.substring(0, length);
		}
		for (int i = 0, SNIPPET_LENGTH = (length - snippet.length()); i < SNIPPET_LENGTH; i++) {
			snippet.concat(" ");
		}
		return snippet;
	}

	private String getPaddedString(String snippet) {
		return "  " + snippet + "  \n";
	}

	public static class PaymentViewHolder {
		TextView txtPaidValue;
		TextView txtPaidDate;
		TextView txtPaymentMethod;
		TextView txtChequeNo;
		TextView txtBankingDate;
	}
}
