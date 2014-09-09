/*
 * Intellectual properties of Supun Lakshan Wanigarathna Dissanayake
 * Copyright (c) 2014, Supun Lakshan Wanigarathna Dissanayake. All rights reserved.
 * Created on : Aug 07, 2014, 11:36:33 AM
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
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.ceylon_linux.lucky_lanka.R;
import com.ceylon_linux.lucky_lanka.controller.OrderController;
import com.ceylon_linux.lucky_lanka.model.Order;
import com.ceylon_linux.lucky_lanka.model.OrderDetail;
import com.ceylon_linux.lucky_lanka.model.Outlet;
import com.ceylon_linux.lucky_lanka.model.Payment;
import org.json.JSONException;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.*;

/**
 * @author Supun Lakshan Wanigarathna Dissanayake
 * @mobile +94711290392
 * @email supunlakshan.xfinity@gmail.com
 */
public class PrintPreviewActivity extends Activity {
	private static BluetoothSocket bluetoothSocket;
	private final int PRINTER_LENGTH = 32;
	private final int REQUEST_CONNECT_DEVICE = 1;
	private final int REQUEST_ENABLE_BLUETOOTH = 2;
	private TextView txtBillPreview;
	private Button btnPrint;
	private Button btnCancel;
	private byte[] receivedOrder;
	private Order order;
	private Outlet outlet;
	private Handler handler = new Handler();
	private BluetoothAdapter bluetoothAdapter;
	private BluetoothDevice bluetoothDevice;
	private ProgressDialog bluetoothConnectProgressDialog;
	private UUID applicationUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.invoice_preview_activity);
		initialize();
		order = (Order) getIntent().getSerializableExtra("order");
		outlet = (Outlet) getIntent().getSerializableExtra("outlet");
		receivedOrder = getOrderCopyIntoByteStream(order);
		txtBillPreview.setText(new String(receivedOrder));
	}

	private void initialize() {
		txtBillPreview = (TextView) findViewById(R.id.txtBillPreview);
		btnPrint = (Button) findViewById(R.id.btnPrint);
		btnCancel = (Button) findViewById(R.id.btnCancel);
		btnPrint.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				btnPrintClicked(v);
			}
		});
		btnCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				btnCancelClicked(v);
			}
		});
	}

	private void btnPrintClicked(View view) {
		if (bluetoothSocket == null) {
			AlertDialog.Builder alertBuilder = new AlertDialog.Builder(PrintPreviewActivity.this);
			alertBuilder.setTitle(R.string.app_name);
			alertBuilder.setMessage("Printer is not connected");
			alertBuilder.setPositiveButton("Setup Printer", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					connectPrinter();
					return;
				}
			});
			alertBuilder.setNegativeButton("Proceed without printing", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					printInvoice();
				}
			});
			alertBuilder.show();
		} else {
			printInvoice();
		}
	}

	private void btnCancelClicked(View view) {
		Intent paymentActivity = new Intent(PrintPreviewActivity.this, PaymentActivity.class);
		paymentActivity.putExtra("order", order);
		paymentActivity.putExtra("outlet", outlet);
		startActivity(paymentActivity);
		finish();
	}

	private void printInvoice() {
		ExecutorService executor = Executors.newSingleThreadExecutor();
		Future<?> future = executor.submit(new Runnable() {
			private ProgressDialog progressDialog;
			private boolean syncStatus;

			public void run() {
				handler.post(new Runnable() {
					@Override
					public void run() {
						progressDialog = ProgressDialog.show(PrintPreviewActivity.this, null, "Processing...");
					}
				});
				try {
					syncStatus = OrderController.syncOrder(PrintPreviewActivity.this, order.getOrderAsJson());
					if (bluetoothSocket != null) {
						OutputStream outputStream = bluetoothSocket.getOutputStream();
						outputStream.write(getOrderIntoByteStream(order));
						if (order.isCreditBill()) {
							outputStream.write(getOrderCopyIntoByteStream(order));
						}
						outputStream.close();
						closeSocket(bluetoothSocket);
					}
				} catch (IOException e) {
					handler.post(new Runnable() {
						@Override
						public void run() {
							connectPrinter();
						}
					});
					e.printStackTrace();
					return;
				} catch (JSONException e) {
					e.printStackTrace();
				} finally {
					handler.post(new Runnable() {
						@Override
						public void run() {
							if (progressDialog != null && progressDialog.isShowing()) {
								progressDialog.dismiss();
							}
							OrderController.saveOrderToDb(PrintPreviewActivity.this, order, syncStatus);
							if (syncStatus) {
								Toast.makeText(PrintPreviewActivity.this, "Order Synced Successfully", Toast.LENGTH_LONG).show();
							} else {
								Toast.makeText(PrintPreviewActivity.this, "Order placed in local database", Toast.LENGTH_LONG).show();
							}
							Intent loadAddInvoiceActivity = new Intent(PrintPreviewActivity.this, LoadAddInvoiceActivity.class);
							startActivity(loadAddInvoiceActivity);
							finish();
						}
					});
				}
			}
		});
		try {
			future.get(1, TimeUnit.MINUTES);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} catch (TimeoutException e) {
			e.printStackTrace();
		}

		/*
		new Thread() {
			private ProgressDialog progressDialog;
			private boolean syncStatus;

			public void run() {
				handler.post(new Runnable() {
					@Override
					public void run() {
						progressDialog = ProgressDialogGenerator.generateProgressDialog(PrintPreviewActivity.this, "Processing...", false);
						progressDialog.show();
					}
				});
				try {
					syncStatus = OrderController.syncOrder(PrintPreviewActivity.this, order.getOrderAsJson());
					if (bluetoothSocket != null) {
						OutputStream outputStream = bluetoothSocket.getOutputStream();
						outputStream.write(getOrderIntoByteStream(order));
						if (order.isCreditBill()) {
							outputStream.write(getOrderCopyIntoByteStream(order));
						}
						outputStream.close();
						closeSocket(bluetoothSocket);
					}
				} catch (IOException e) {
					handler.post(new Runnable() {
						@Override
						public void run() {
							connectPrinter();
						}
					});
					e.printStackTrace();
					return;
				} catch (JSONException e) {
					e.printStackTrace();
				}
				handler.post(new Runnable() {
					@Override
					public void run() {
						if (progressDialog != null && progressDialog.isShowing()) {
							progressDialog.dismiss();
						}
						OrderController.saveOrderToDb(PrintPreviewActivity.this, order, syncStatus);
						if (syncStatus) {
							Toast.makeText(PrintPreviewActivity.this, "Order Synced Successfully", Toast.LENGTH_LONG).show();
						} else {
							Toast.makeText(PrintPreviewActivity.this, "Order placed in local database", Toast.LENGTH_LONG).show();
						}
						Intent loadAddInvoiceActivity = new Intent(PrintPreviewActivity.this, LoadAddInvoiceActivity.class);
						startActivity(loadAddInvoiceActivity);
						finish();
					}
				});
			}
		}.start();*/
	}

	private void connectPrinter() {
		bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (bluetoothAdapter == null) {
			Toast.makeText(PrintPreviewActivity.this, "Bluetooth Not Supported", Toast.LENGTH_LONG).show();
		} else {
			if (!bluetoothAdapter.isEnabled()) {
				Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
				startActivityForResult(enableBtIntent, REQUEST_ENABLE_BLUETOOTH);
			} else {
				Intent connectIntent = new Intent(PrintPreviewActivity.this, DeviceListActivity.class);
				startActivityForResult(connectIntent, REQUEST_CONNECT_DEVICE);
			}
		}
	}

	private void closeSocket(BluetoothSocket nOpenSocket) {
		try {
			nOpenSocket.close();
			bluetoothSocket = null;
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	private byte[] getOrderIntoByteStream(Order order) {
		SimpleDateFormat dateFormatter = new SimpleDateFormat();
		dateFormatter.applyPattern("dd MMM, yyyy");
		Date date = new Date();
		StringBuilder builder = new StringBuilder();
		builder.append(getPaddedString(getLeftAlignedString("Lucky Lanka Milk Processing PLC", PRINTER_LENGTH)));
		builder.append(getPaddedString(getLeftAlignedString("Bibulewela,Karagoda,Uyangoda.", PRINTER_LENGTH)));
		builder.append(getPaddedString(getLeftAlignedString("HotLine: 011-7215021", PRINTER_LENGTH)));
		builder.append(getPaddedString(getLeftAlignedString("---------------------------------------------", PRINTER_LENGTH)));
		builder.append(getPaddedString(getLeftAlignedString("Bill No : " + order.getOrderId(), PRINTER_LENGTH)));
		builder.append(getPaddedString(getLeftAlignedString("Outlet  : " + outlet.getOutletName(), PRINTER_LENGTH)));
		builder.append(getPaddedString(getLeftAlignedString("Date    : " + dateFormatter.format(date), PRINTER_LENGTH)));
		dateFormatter.applyPattern("hh:mm:ss aa");
		builder.append(getPaddedString(getLeftAlignedString("Time    : " + dateFormatter.format(date), PRINTER_LENGTH)));
		builder.append(getPaddedString(getLeftAlignedString("---------------------------------------------", PRINTER_LENGTH)));
		builder.append(getPaddedString(getLeftAlignedString("Qty   Item        Rate Value(Rs)", PRINTER_LENGTH)));
		builder.append(getPaddedString(getLeftAlignedString("---------------------------------------------", PRINTER_LENGTH)));
		double sum = 0;
		for (OrderDetail orderDetail : order.getOrderDetails()) {
			if (orderDetail.getQuantity() != 0) {
				sum += (orderDetail.getPrice() * orderDetail.getQuantity());
				builder.append(getPaddedString(getLeftAlignedString(String.valueOf(orderDetail.getQuantity()), 3) + "  " + getLeftAlignedString(orderDetail.getItemShortName(), 13) + getRightAlignedString(String.valueOf(orderDetail.getPrice()), 4) + " - " + getRightAlignedString(String.valueOf(orderDetail.getPrice() * orderDetail.getQuantity()), 6)));
			}
		}
		builder.append(getPaddedString(getLeftAlignedString("---------------------------------------------", PRINTER_LENGTH)));
		builder.append(getPaddedString(getLeftAlignedString("Gross Total", 21) + " Rs " + getRightAlignedString(String.valueOf(sum), 6)));
		builder.append(getPaddedString(getLeftAlignedString("Discount", 21) + " Rs " + getRightAlignedString(String.valueOf(order.getDiscount()), 6)));
		builder.append(getPaddedString(getLeftAlignedString("-------------------------------------------", PRINTER_LENGTH)));
		boolean returnAvailable = false;
		for (OrderDetail orderDetail : order.getOrderDetails()) {
			if (orderDetail.getReturnQuantity() != 0) {
				returnAvailable = true;
				break;
			}
		}
		if (returnAvailable) {
			builder.append(getPaddedString(getLeftAlignedString("Return & Replacement:", PRINTER_LENGTH)));
			for (OrderDetail orderDetail : order.getOrderDetails()) {
				if (orderDetail.getReturnQuantity() != 0) {
					builder.append(getPaddedString(getLeftAlignedString(String.valueOf(orderDetail.getReturnQuantity()), 3) + "  " + getLeftAlignedString(orderDetail.getItemShortName(), 13) + getRightAlignedString(String.valueOf(orderDetail.getPrice()), 4) + getRightAlignedString("                  ", 9)));
				}
			}
		}

		boolean freeIssueAvailability = false;
		for (OrderDetail orderDetail : order.getOrderDetails()) {
			if (orderDetail.getFreeIssue() != 0) {
				freeIssueAvailability = true;
				break;
			}
		}
		if (freeIssueAvailability) {
			builder.append(getPaddedString(getLeftAlignedString("Free Issues:", PRINTER_LENGTH)));
			for (OrderDetail orderDetail : order.getOrderDetails()) {
				if (orderDetail.getFreeIssue() != 0) {
					builder.append(getPaddedString(getLeftAlignedString(String.valueOf(orderDetail.getFreeIssue()), 3) + "  " + getLeftAlignedString(orderDetail.getItemShortName(), 13) + getRightAlignedString(String.valueOf(orderDetail.getPrice()), 4) + getRightAlignedString("                  ", 9)));
				}
			}
		}

		boolean sampleAvailability = false;
		for (OrderDetail orderDetail : order.getOrderDetails()) {
			if (orderDetail.getSampleQuantity() != 0) {
				sampleAvailability = true;
				break;
			}
		}
		if (sampleAvailability) {
			builder.append(getPaddedString(getLeftAlignedString("Sample:", PRINTER_LENGTH)));
			for (OrderDetail orderDetail : order.getOrderDetails()) {
				if (orderDetail.getSampleQuantity() != 0) {
					builder.append(getPaddedString(getLeftAlignedString(String.valueOf(orderDetail.getSampleQuantity()), 3) + "  " + getLeftAlignedString(orderDetail.getItemShortName(), 13) + getRightAlignedString(String.valueOf(orderDetail.getPrice()), 4) + getRightAlignedString("                  ", 9)));
				}
			}
		}

		if (returnAvailable || freeIssueAvailability || sampleAvailability) {
			builder.append(getPaddedString(getLeftAlignedString("--------------------------------------", PRINTER_LENGTH)));
		}
		builder.append(getPaddedString(getLeftAlignedString("Grand Total", 21) + " Rs " + getRightAlignedString(String.valueOf(sum - order.getDiscount()), 6)));
		double totallyPaid = 0;
		for (Payment payment : order.getPayments()) {
			totallyPaid += payment.getAmount();
		}
		builder.append(getPaddedString(getLeftAlignedString("Total Paid", 21) + " Rs " + getRightAlignedString(String.valueOf(totallyPaid), 6)));
		builder.append(getPaddedString(getLeftAlignedString("Balance", 21) + " Rs " + getRightAlignedString(String.valueOf(sum - order.getDiscount() - totallyPaid), 6)));
		builder.append(getPaddedString(getLeftAlignedString("-------------------------------------------------", PRINTER_LENGTH)));
		builder.append(getPaddedString(getLeftAlignedString("      " + "HAVE A LUCKY DAY !!!", PRINTER_LENGTH)));
		builder.append(getPaddedString(getLeftAlignedString("-------------------------------------------------", PRINTER_LENGTH)));
		builder.append("\n\n");
		return builder.toString().getBytes();
	}

	private byte[] getOrderCopyIntoByteStream(Order order) {
		SimpleDateFormat dateFormatter = new SimpleDateFormat();
		dateFormatter.applyPattern("dd MMM, yyyy");
		Date date = new Date();
		StringBuilder builder = new StringBuilder();
		builder.append(getPaddedString(getLeftAlignedString("----------Company Copy----------", PRINTER_LENGTH)));
		builder.append(getPaddedString(getLeftAlignedString("Lucky Lanka Milk Processing PLC", PRINTER_LENGTH)));
		builder.append(getPaddedString(getLeftAlignedString("Bibulewela,Karagoda,Uyangoda.", PRINTER_LENGTH)));
		builder.append(getPaddedString(getLeftAlignedString("HotLine: 011-7215021", PRINTER_LENGTH)));
		builder.append(getPaddedString(getLeftAlignedString("---------------------------------------------", PRINTER_LENGTH)));
		builder.append(getPaddedString(getLeftAlignedString("Bill No : " + order.getOrderId(), PRINTER_LENGTH)));
		builder.append(getPaddedString(getLeftAlignedString("Outlet  : " + outlet.getOutletName(), PRINTER_LENGTH)));
		builder.append(getPaddedString(getLeftAlignedString("Date    : " + dateFormatter.format(date), PRINTER_LENGTH)));
		dateFormatter.applyPattern("hh:mm:ss aa");
		builder.append(getPaddedString(getLeftAlignedString("Time    : " + dateFormatter.format(date), PRINTER_LENGTH)));
		builder.append(getPaddedString(getLeftAlignedString("---------------------------------------------", PRINTER_LENGTH)));
		builder.append(getPaddedString(getLeftAlignedString("Qty   Item        Rate Value(Rs)", PRINTER_LENGTH)));
		builder.append(getPaddedString(getLeftAlignedString("---------------------------------------------", PRINTER_LENGTH)));
		double sum = 0;
		for (OrderDetail orderDetail : order.getOrderDetails()) {
			if (orderDetail.getQuantity() != 0) {
				sum += (orderDetail.getPrice() * orderDetail.getQuantity());
				builder.append(getPaddedString(getLeftAlignedString(String.valueOf(orderDetail.getQuantity()), 3) + "  " + getLeftAlignedString(orderDetail.getItemShortName(), 13) + getRightAlignedString(String.valueOf(orderDetail.getPrice()), 4) + " - " + getRightAlignedString(String.valueOf(orderDetail.getPrice() * orderDetail.getQuantity()), 6)));
			}
		}
		builder.append(getPaddedString(getLeftAlignedString("---------------------------------------------", PRINTER_LENGTH)));
		builder.append(getPaddedString(getLeftAlignedString("Gross Total", 21) + " Rs " + getRightAlignedString(String.valueOf(sum), 6)));
		builder.append(getPaddedString(getLeftAlignedString("Discount", 21) + " Rs " + getRightAlignedString(String.valueOf(order.getDiscount()), 6)));
		builder.append(getPaddedString(getLeftAlignedString("-------------------------------------------", PRINTER_LENGTH)));
		boolean returnAvailable = false;
		for (OrderDetail orderDetail : order.getOrderDetails()) {
			if (orderDetail.getReturnQuantity() != 0) {
				returnAvailable = true;
				break;
			}
		}
		if (returnAvailable) {
			builder.append(getPaddedString(getLeftAlignedString("Return & Replacement:", PRINTER_LENGTH)));
			for (OrderDetail orderDetail : order.getOrderDetails()) {
				if (orderDetail.getReturnQuantity() != 0) {
					builder.append(getPaddedString(getLeftAlignedString(String.valueOf(orderDetail.getReturnQuantity()), 3) + "  " + getLeftAlignedString(orderDetail.getItemShortName(), 13) + getRightAlignedString(String.valueOf(orderDetail.getPrice()), 4) + getRightAlignedString("                  ", 9)));
				}
			}
		}

		boolean freeIssueAvailability = false;
		for (OrderDetail orderDetail : order.getOrderDetails()) {
			if (orderDetail.getFreeIssue() != 0) {
				freeIssueAvailability = true;
				break;
			}
		}
		if (freeIssueAvailability) {
			builder.append(getPaddedString(getLeftAlignedString("Free Issues:", PRINTER_LENGTH)));
			for (OrderDetail orderDetail : order.getOrderDetails()) {
				if (orderDetail.getFreeIssue() != 0) {
					builder.append(getPaddedString(getLeftAlignedString(String.valueOf(orderDetail.getFreeIssue()), 3) + "  " + getLeftAlignedString(orderDetail.getItemShortName(), 13) + getRightAlignedString(String.valueOf(orderDetail.getPrice()), 4) + getRightAlignedString("                  ", 9)));
				}
			}
		}

		boolean sampleAvailability = false;
		for (OrderDetail orderDetail : order.getOrderDetails()) {
			if (orderDetail.getSampleQuantity() != 0) {
				sampleAvailability = true;
				break;
			}
		}
		if (sampleAvailability) {
			builder.append(getPaddedString(getLeftAlignedString("Sample:", PRINTER_LENGTH)));
			for (OrderDetail orderDetail : order.getOrderDetails()) {
				if (orderDetail.getSampleQuantity() != 0) {
					builder.append(getPaddedString(getLeftAlignedString(String.valueOf(orderDetail.getSampleQuantity()), 3) + "  " + getLeftAlignedString(orderDetail.getItemShortName(), 13) + getRightAlignedString(String.valueOf(orderDetail.getPrice()), 4) + getRightAlignedString("                  ", 9)));
				}
			}
		}
		if (returnAvailable || freeIssueAvailability || sampleAvailability) {
			builder.append(getPaddedString(getLeftAlignedString("--------------------------------------", PRINTER_LENGTH)));
		}
		builder.append(getPaddedString(getLeftAlignedString("Grand Total", 21) + " Rs " + getRightAlignedString(String.valueOf(sum - order.getDiscount()), 6)));
		double totallyPaid = 0;
		for (Payment payment : order.getPayments()) {
			totallyPaid += payment.getAmount();
		}
		builder.append(getPaddedString(getLeftAlignedString("Total Paid", 21) + " Rs " + getRightAlignedString(String.valueOf(totallyPaid), 6)));
		builder.append(getPaddedString(getLeftAlignedString("Balance", 21) + " Rs " + getRightAlignedString(String.valueOf(sum - order.getDiscount() - totallyPaid), 6)));
		builder.append(getPaddedString(getLeftAlignedString("-------------------------------------------------", PRINTER_LENGTH)));
		builder.append(getPaddedString(getLeftAlignedString("Signature : ", PRINTER_LENGTH)));
		builder.append(getPaddedString(getLeftAlignedString("-------------------------------------------------", PRINTER_LENGTH)));
		builder.append(getPaddedString(getLeftAlignedString("      " + "HAVE A LUCKY DAY !!!", PRINTER_LENGTH)));
		builder.append(getPaddedString(getLeftAlignedString("-------------------------------------------------", PRINTER_LENGTH)));
		builder.append("\n\n");
		return builder.toString().getBytes();
	}

	private String getLeftAlignedString(String snippet, int length) {
		if (snippet.length() >= length) {
			return snippet.substring(0, length);
		} else {
			while (snippet.length() != length) {
				snippet = snippet.concat(" ");
			}
			return snippet;
		}
	}

	private String getRightAlignedString(String snippet, int length) {
		if (snippet.length() >= length) {
			return snippet.substring(0, length);
		} else {
			while (snippet.length() != length) {
				snippet = " " + snippet;
			}
			return snippet;
		}
	}

	private String getPaddedString(String snippet) {
		return snippet + "\n";
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
					new Thread() {
						@Override
						public void run() {
							try {
								bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(applicationUUID);
								bluetoothAdapter.cancelDiscovery();
								bluetoothSocket.connect();
								handler.post(new Runnable() {
									@Override
									public void run() {
										if (bluetoothConnectProgressDialog.isShowing()) {
											bluetoothConnectProgressDialog.dismiss();
										}
										Toast.makeText(PrintPreviewActivity.this, "Device Connected", Toast.LENGTH_LONG).show();
									}
								});
							} catch (IOException ex) {
								ex.printStackTrace();
								closeSocket(bluetoothSocket);
								return;
							}
						}
					}.start();
				}
				break;
			case REQUEST_ENABLE_BLUETOOTH:
				if (resultCode == Activity.RESULT_OK) {
					Intent connectIntent = new Intent(PrintPreviewActivity.this, DeviceListActivity.class);
					startActivityForResult(connectIntent, REQUEST_CONNECT_DEVICE);
				} else {
					Toast.makeText(PrintPreviewActivity.this, "Unable to Start Bluetooth", Toast.LENGTH_LONG).show();
				}
				break;
		}
	}

}
