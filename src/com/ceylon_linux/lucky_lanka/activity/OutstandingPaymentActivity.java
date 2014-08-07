/*
 * Intellectual properties of Supun Lakshan Wanigarathna Dissanayake
 * Copyright (c) 2014, Supun Lakshan Wanigarathna Dissanayake. All rights reserved.
 * Created on : Jul 07, 2014, 3:11 PM
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
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.ceylon_linux.lucky_lanka.R;
import com.ceylon_linux.lucky_lanka.controller.OutletController;
import com.ceylon_linux.lucky_lanka.model.Invoice;
import com.ceylon_linux.lucky_lanka.model.Outlet;
import com.ceylon_linux.lucky_lanka.model.Payment;
import com.ceylon_linux.lucky_lanka.util.ProgressDialogGenerator;

import java.io.IOException;
import java.io.OutputStream;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * @author Supun Lakshan Wanigarathna Dissanayake
 * @mobile +94711290392
 * @email supunlakshan.xfinity@gmail.com
 */
public class OutstandingPaymentActivity extends Activity {

	private final int REQUEST_CONNECT_DEVICE = 1;
	private final int REQUEST_ENABLE_BLUETOOTH = 2;
	private final int PAYMENT_DONE = 3;
	private BluetoothAdapter bluetoothAdapter;
	private BluetoothDevice bluetoothDevice;
	private Invoice invoice;
	private Outlet outlet;
	private Button btnCashPayment;
	private Button btnChequePayment;
	private Button btnPrintInvoice;
	private ListView listPayment;
	private TextView txtInvoiceTotal;
	private TextView txtTotallyPaid;
	private BaseAdapter adapter;
	private Button btnConnectPrinter;
	private UUID applicationUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	private ProgressDialog bluetoothConnectProgressDialog;
	private BluetoothSocket bluetoothSocket;
	private NumberFormat currencyFormat;
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			bluetoothConnectProgressDialog.dismiss();
			Toast.makeText(OutstandingPaymentActivity.this, "Device Connected", Toast.LENGTH_LONG).show();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.payments_page);
		initialize();
	}

	private void initialize() {
		Intent intent = getIntent();
		invoice = (Invoice) intent.getSerializableExtra("invoice");
		outlet = (Outlet) intent.getSerializableExtra("outlet");
		final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd MMMM, yyyy");
		btnCashPayment = (Button) findViewById(R.id.btnCashPayment);
		btnChequePayment = (Button) findViewById(R.id.btnChequePayment);
		btnPrintInvoice = (Button) findViewById(R.id.btnPrintInvoice);
		listPayment = (ListView) findViewById(R.id.listPayment);
		txtInvoiceTotal = (TextView) findViewById(R.id.txtInvoiceTotal);
		txtTotallyPaid = (TextView) findViewById(R.id.txtTotalPaid);
		currencyFormat = NumberFormat.getInstance();
		currencyFormat.setGroupingUsed(true);
		currencyFormat.setMaximumFractionDigits(2);
		currencyFormat.setMinimumFractionDigits(2);
		double invoiceTotal = 0;
		txtInvoiceTotal.setText("Rs " + currencyFormat.format(invoiceTotal));
		adapter = new BaseAdapter() {

			@Override
			public int getCount() {
				return invoice.getPayments().size();
			}

			@Override
			public Payment getItem(int position) {
				return invoice.getPayments().get(position);
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
					paymentViewHolder.txtPaymentMethod = (TextView) convertView.findViewById(R.id.txtPaymentMethod);
					paymentViewHolder.txtChequeNo = (TextView) convertView.findViewById(R.id.txtChequeNo);
					paymentViewHolder.txtBankingDate = (TextView) convertView.findViewById(R.id.txtBankingDate);
					paymentViewHolder.imageButton = (ImageButton) convertView.findViewById(R.id.imageButton);
					convertView.setTag(paymentViewHolder);
				} else {
					paymentViewHolder = (PaymentViewHolder) convertView.getTag();
				}
				final Payment payment = getItem(position);
				boolean isChequePayment = payment.getChequeNo() != null && !payment.getChequeNo().isEmpty();
				paymentViewHolder.txtPaidValue.setText("Rs " + currencyFormat.format(payment.getAmount()));
				paymentViewHolder.txtPaymentMethod.setText((isChequePayment) ? "CHEQUE" : "CASH");
				paymentViewHolder.txtChequeNo.setText((isChequePayment) ? payment.getChequeNo() : "");
				paymentViewHolder.txtBankingDate.setText((isChequePayment) ? dateFormatter.format(payment.getChequeDate()) : "");
				paymentViewHolder.imageButton.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						invoice.getPayments().remove(payment);
						adapter.notifyDataSetChanged();
						listPayment.setAdapter(adapter);
					}
				});
				return convertView;
			}

			@Override
			public void notifyDataSetChanged() {
				super.notifyDataSetChanged();
				double sum = 0;
				for (Payment payment : invoice.getPayments()) {
					sum += payment.getAmount();
				}
				txtTotallyPaid.setText("Rs " + currencyFormat.format(sum));
			}
		};
		listPayment.setAdapter(adapter);

		btnConnectPrinter = (Button) findViewById(R.id.btnConnectPrinter);
		btnConnectPrinter.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				btnConnectPrinterClicked(view);
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
		Intent cashPaymentActivity = new Intent(OutstandingPaymentActivity.this, CashPaymentActivity.class);
		startActivityForResult(cashPaymentActivity, PAYMENT_DONE);
	}

	private void btnChequePaymentClicked(View view) {
		Intent chequePaymentActivity = new Intent(OutstandingPaymentActivity.this, ChequePaymentActivity.class);
		startActivityForResult(chequePaymentActivity, PAYMENT_DONE);
	}

	private void btnPrintInvoiceClicked(View view) {
		final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(OutstandingPaymentActivity.this);
		alertBuilder.setTitle("Lucky Lanka Sales Pad");
		alertBuilder.setMessage("Printer is not connected");
		alertBuilder.setPositiveButton("Setup Printer", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				btnConnectPrinter.performClick();
				return;
			}
		});
		alertBuilder.setNegativeButton("Proceed without printing", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				printInvoice();
			}
		});
		if (bluetoothSocket == null) {
			alertBuilder.show();
		} else {
			printInvoice();
		}
	}

	private void printInvoice() {
		new Thread() {
			private ProgressDialog progressDialog;
			private boolean syncStatus;

			public void run() {
				handler.post(new Runnable() {
					@Override
					public void run() {
						progressDialog = ProgressDialogGenerator.generateProgressDialog(OutstandingPaymentActivity.this, "Processing...", false);
						progressDialog.show();
					}
				});
				try {
					syncStatus = OutletController.syncOutstandingPayments(OutstandingPaymentActivity.this, invoice);
					if (bluetoothSocket != null) {
						OutputStream os = bluetoothSocket.getOutputStream();
						os.write(getInvoiceIntoByteStream(invoice));
						os.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
				handler.post(new Runnable() {
					@Override
					public void run() {
						if (progressDialog != null && progressDialog.isShowing()) {
							progressDialog.dismiss();
						}
						if (syncStatus) {
							Toast.makeText(OutstandingPaymentActivity.this, "Payments Synced Successfully", Toast.LENGTH_LONG).show();
						} else {
							OutletController.saveOutstandingPayments(OutstandingPaymentActivity.this, invoice);
							Toast.makeText(OutstandingPaymentActivity.this, "Payments placed in local database", Toast.LENGTH_LONG).show();
						}
						setResult(RESULT_OK);
						finish();
					}
				});
			}
		}.start();
	}

	private void btnConnectPrinterClicked(View view) {
		bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (bluetoothAdapter == null) {
			Toast.makeText(OutstandingPaymentActivity.this, "Bluetooth Not Supported", Toast.LENGTH_LONG).show();
		} else {
			if (!bluetoothAdapter.isEnabled()) {
				Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
				startActivityForResult(enableBtIntent, REQUEST_ENABLE_BLUETOOTH);
			} else {
				Intent connectIntent = new Intent(OutstandingPaymentActivity.this, DeviceListActivity.class);
				startActivityForResult(connectIntent, REQUEST_CONNECT_DEVICE);
			}
		}
	}

	private void btnDisconnectPrinterClicked(View view) {
		if (bluetoothAdapter != null) {
			bluetoothAdapter.disable();
		}
	}

	@Override
	public void onBackPressed() {
		Intent homeActivity = new Intent(OutstandingPaymentActivity.this, HomeActivity.class);
		startActivity(homeActivity);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			case REQUEST_CONNECT_DEVICE:
				if (resultCode == Activity.RESULT_OK) {
					Bundle mExtra = data.getExtras();
					String mDeviceAddress = mExtra.getString("DeviceAddress");
					bluetoothDevice = bluetoothAdapter.getRemoteDevice(mDeviceAddress);
					bluetoothConnectProgressDialog = ProgressDialog.show(this, "Connecting...", bluetoothDevice.getName() + " : " + bluetoothDevice.getAddress(), true, false);
					new Thread(new Runnable() {
						@Override
						public void run() {
							try {
								bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(applicationUUID);
								bluetoothAdapter.cancelDiscovery();
								bluetoothSocket.connect();
								handler.sendEmptyMessage(0);
							} catch (IOException ex) {
								ex.printStackTrace();
								closeSocket(bluetoothSocket);
								return;
							}
						}
					}).start();
				}
				break;
			case REQUEST_ENABLE_BLUETOOTH:
				if (resultCode == Activity.RESULT_OK) {
					Intent connectIntent = new Intent(OutstandingPaymentActivity.this, DeviceListActivity.class);
					startActivityForResult(connectIntent, REQUEST_CONNECT_DEVICE);
				} else {
					Toast.makeText(OutstandingPaymentActivity.this, "Unable to Start Bluetooth", Toast.LENGTH_LONG).show();
				}
				break;
			case PAYMENT_DONE:
				if (resultCode == RESULT_OK) {
					Payment payment = (Payment) data.getSerializableExtra("payment");
					invoice.getPayments().add(payment);
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

	private byte[] getInvoiceIntoByteStream(Invoice invoice) {
		SimpleDateFormat dateFormatter = new SimpleDateFormat();
		dateFormatter.applyPattern("dd MMM, yyyy");
		Date date = new Date();
		StringBuilder builder = new StringBuilder();
		builder.append(getPaddedString(getAlignedString("Lucky Lanka Outstanding Payments", 44)));
		builder.append(getPaddedString(getAlignedString("---------------------------------------------", 44)));
		builder.append(getPaddedString(getAlignedString("Outlet : " + outlet.getOutletName(), 44)));
		builder.append(getPaddedString(getAlignedString("Date   : " + dateFormatter.format(date), 44)));
		dateFormatter.applyPattern("hh:mm:ss aa");
		builder.append(getPaddedString(getAlignedString("Time   : " + dateFormatter.format(date), 44)));
		builder.append(getPaddedString(getAlignedString("---------------------------------------------", 44)));
		double sum = 0;
		for (Payment payment : invoice.getPayments()) {
			sum += payment.getAmount();
		}
		builder.append(getPaddedString(getAlignedString("---------------------------------------------", 44)));
		builder.append(getPaddedString(getAlignedString("Total                             ", 34) + "Rs " + sum));
		builder.append(getPaddedString(getAlignedString("---------------------------------------------", 44)));
		builder.append("\n\n\n");
		return builder.toString().getBytes();
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
		TextView txtPaymentMethod;
		TextView txtChequeNo;
		TextView txtBankingDate;
		ImageButton imageButton;
	}
}
