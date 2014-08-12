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
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.ceylon_linux.lucky_lanka.R;
import com.ceylon_linux.lucky_lanka.controller.OrderController;
import com.ceylon_linux.lucky_lanka.model.Order;
import com.ceylon_linux.lucky_lanka.model.OrderDetail;
import com.ceylon_linux.lucky_lanka.model.Outlet;
import com.ceylon_linux.lucky_lanka.model.Payment;
import com.ceylon_linux.lucky_lanka.util.ProgressDialogGenerator;
import org.json.JSONException;

import java.io.IOException;
import java.io.OutputStream;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

/**
 * @author Supun Lakshan Wanigarathna Dissanayake
 * @mobile +94711290392
 * @email supunlakshan.xfinity@gmail.com
 */
public class PaymentActivity extends Activity {

	private static BluetoothSocket bluetoothSocket;
	private static OutputStream outputStream;
	private final int REQUEST_CONNECT_DEVICE = 1;
	private final int REQUEST_ENABLE_BLUETOOTH = 2;
	private final int PAYMENT_DONE = 3;
	private final int INVOICE_PREVIEW = 4;
	private final int PRINTER_LENGTH = 32;
	private BluetoothAdapter bluetoothAdapter;
	private BluetoothDevice bluetoothDevice;
	private Order order;
	private Outlet outlet;
	private Button btnCashPayment;
	private Button btnChequePayment;
	private Button btnPrintInvoice;
	private EditText inputDiscount;
	private ListView listPayment;
	private TextView txtInvoiceTotal;
	private boolean immediatePrint;
	private TextView txtTotallyPaid;
	private BaseAdapter adapter;
	private UUID applicationUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	private ProgressDialog bluetoothConnectProgressDialog;
	private NumberFormat currencyFormat;
	private Handler handler = new Handler();
	private double invoiceTotal = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.payments_page);
		initialize();
	}

	private void initialize() {
		Intent intent = getIntent();
		order = (Order) intent.getSerializableExtra("order");
		outlet = (Outlet) intent.getSerializableExtra("outlet");
		final SimpleDateFormat dateFormatter = new SimpleDateFormat("yy-MM-dd");
		btnCashPayment = (Button) findViewById(R.id.btnCashPayment);
		btnChequePayment = (Button) findViewById(R.id.btnChequePayment);
		btnPrintInvoice = (Button) findViewById(R.id.btnPrintInvoice);
		listPayment = (ListView) findViewById(R.id.listPayment);
		txtInvoiceTotal = (TextView) findViewById(R.id.txtInvoiceTotal);
		txtTotallyPaid = (TextView) findViewById(R.id.txtTotalPaid);
		inputDiscount = (EditText) findViewById(R.id.inputDiscount);
		order.setPayments(new ArrayList<Payment>());
		currencyFormat = NumberFormat.getInstance();
		currencyFormat.setGroupingUsed(true);
		currencyFormat.setMaximumFractionDigits(2);
		currencyFormat.setMinimumFractionDigits(2);
		for (OrderDetail orderDetail : order.getOrderDetails()) {
			invoiceTotal += orderDetail.getPrice() * orderDetail.getQuantity();
		}
		txtInvoiceTotal.setText("Rs " + currencyFormat.format(invoiceTotal));
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
						order.getPayments().remove(payment);
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
				for (Payment payment : order.getPayments()) {
					sum += payment.getAmount();
				}
				txtTotallyPaid.setText("Rs " + currencyFormat.format(sum));
				String valueString;
				double discount = (valueString = inputDiscount.getText().toString()).isEmpty() ? 0 : Double.parseDouble(valueString);
				if (invoiceTotal - discount < sum) {
					txtTotallyPaid.setBackgroundColor(Color.parseColor("#FF4B40"));
				} else if (invoiceTotal - discount > sum) {
					txtTotallyPaid.setBackgroundColor(Color.parseColor("#FFFBFF61"));
				} else {
					txtTotallyPaid.setBackgroundColor(Color.parseColor("#FF45C009"));
				}
			}
		};
		listPayment.setAdapter(adapter);
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
		if (immediatePrint) {
			printInvoice();
			immediatePrint = false;
		} else {
			Intent printPreviewActivity = new Intent(PaymentActivity.this, PrintPreviewActivity.class);
			String valueString;
			order.setDiscount((valueString = inputDiscount.getText().toString()).isEmpty() ? 0 : Double.parseDouble(valueString));
			printPreviewActivity.putExtra("orderStream", getOrderCopyIntoByteStream(order));
			startActivityForResult(printPreviewActivity, INVOICE_PREVIEW);
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
						progressDialog = ProgressDialogGenerator.generateProgressDialog(PaymentActivity.this, "Processing...", false);
						progressDialog.show();
					}
				});
				try {
					syncStatus = OrderController.syncOrder(PaymentActivity.this, order.getOrderAsJson());
					if (bluetoothSocket != null) {
						outputStream = bluetoothSocket.getOutputStream();
						outputStream.write(getOrderIntoByteStream(order));
						if (order.isCreditBill()) {
							outputStream.write(getOrderCopyIntoByteStream(order));
						}
						outputStream.flush();
					}
				} catch (IOException e) {
					e.printStackTrace();
				} catch (JSONException e) {
					e.printStackTrace();
				}
				handler.post(new Runnable() {
					@Override
					public void run() {
						if (progressDialog != null && progressDialog.isShowing()) {
							progressDialog.dismiss();
							OrderController.saveOrderToDb(PaymentActivity.this, order);
						}
						if (syncStatus) {
							Toast.makeText(PaymentActivity.this, "Order Synced Successfully", Toast.LENGTH_LONG).show();
						} else {
							Toast.makeText(PaymentActivity.this, "Order placed in local database", Toast.LENGTH_LONG).show();
						}
						Intent loadAddInvoiceActivity = new Intent(PaymentActivity.this, LoadAddInvoiceActivity.class);
						startActivity(loadAddInvoiceActivity);
						finish();
					}
				});
			}
		}.start();
	}

	private void connectPrinter() {
		bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (bluetoothAdapter == null) {
			Toast.makeText(PaymentActivity.this, "Bluetooth Not Supported", Toast.LENGTH_LONG).show();
		} else {
			if (!bluetoothAdapter.isEnabled()) {
				Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
				startActivityForResult(enableBtIntent, REQUEST_ENABLE_BLUETOOTH);
			} else {
				Intent connectIntent = new Intent(PaymentActivity.this, DeviceListActivity.class);
				startActivityForResult(connectIntent, REQUEST_CONNECT_DEVICE);
			}
		}
	}

	@Override
	public void onBackPressed() {
		Intent selectItemActivity = new Intent(PaymentActivity.this, SelectItemActivity.class);
		selectItemActivity.putExtra("order", order);
		selectItemActivity.putExtra("outlet", outlet);
		startActivity(selectItemActivity);
		finish();
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
										Toast.makeText(PaymentActivity.this, "Device Connected", Toast.LENGTH_LONG).show();
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
			case INVOICE_PREVIEW:
				if (resultCode == RESULT_OK) {
					Boolean response = (Boolean) data.getSerializableExtra("response");
					if (response) {
						immediatePrint = true;
						final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(PaymentActivity.this);
						alertBuilder.setTitle("Lucky Lanka Sales Pad");
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
						if (bluetoothSocket == null) {
							alertBuilder.show();
						} else {
							printInvoice();
						}
					}
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
		SimpleDateFormat dateFormatter = new SimpleDateFormat();
		dateFormatter.applyPattern("dd MMM, yyyy");
		Date date = new Date();
		StringBuilder builder = new StringBuilder();
		builder.append(getPaddedString(getLeftAlignedString("Lucky Lanka Milk Processing PLC", PRINTER_LENGTH)));
		builder.append(getPaddedString(getLeftAlignedString("Bibulewela,Karagoda,Uyangoda.", PRINTER_LENGTH)));
		builder.append(getPaddedString(getLeftAlignedString("HotLine: 011-7215021", PRINTER_LENGTH)));
		builder.append(getPaddedString(getLeftAlignedString("-------------------------------------------", PRINTER_LENGTH)));
		builder.append(getPaddedString(getLeftAlignedString("Outlet : " + outlet.getOutletName(), PRINTER_LENGTH)));
		builder.append(getPaddedString(getLeftAlignedString("Date   : " + dateFormatter.format(date), PRINTER_LENGTH)));
		dateFormatter.applyPattern("hh:mm:ss aa");
		builder.append(getPaddedString(getLeftAlignedString("Time   : " + dateFormatter.format(date), PRINTER_LENGTH)));
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
		builder.append(getPaddedString(getLeftAlignedString("-------------------------------------------", PRINTER_LENGTH)));
		double returnValue = 0;
		boolean returnReplacementExist = false;
		for (OrderDetail orderDetail : order.getOrderDetails()) {
			if (orderDetail.getReturnQuantity() != 0) {
				returnValue += (orderDetail.getPrice() * orderDetail.getReturnQuantity());
				returnReplacementExist = true;
			}
		}
		if (returnValue != 0) {
			builder.append(getPaddedString(getLeftAlignedString("Return:", PRINTER_LENGTH)));
			for (OrderDetail orderDetail : order.getOrderDetails()) {
				if (orderDetail.getReturnQuantity() != 0) {
					builder.append(getPaddedString(getLeftAlignedString(String.valueOf(orderDetail.getReturnQuantity()), 3) + "  " + getLeftAlignedString(orderDetail.getItemShortName(), 13) + getRightAlignedString(String.valueOf(orderDetail.getPrice()), 4) + getRightAlignedString("                  ", 9)));
				}
			}
		}
		double replacementValue = 0;
		for (OrderDetail orderDetail : order.getOrderDetails()) {
			if (orderDetail.getReplaceQuantity() != 0) {
				replacementValue += (orderDetail.getPrice() * orderDetail.getReplaceQuantity());
				returnReplacementExist = true;
			}
		}
		if (replacementValue != 0) {
			builder.append(getPaddedString(getLeftAlignedString("Replacement:", PRINTER_LENGTH)));
			for (OrderDetail orderDetail : order.getOrderDetails()) {
				if (orderDetail.getReplaceQuantity() != 0) {
					builder.append(getPaddedString(getLeftAlignedString(String.valueOf(orderDetail.getReplaceQuantity()), 3) + "  " + getLeftAlignedString(orderDetail.getItemShortName(), 13) + getRightAlignedString(String.valueOf(orderDetail.getPrice()), 4) + getRightAlignedString("                  ", 9)));
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
		builder.append(getPaddedString(getLeftAlignedString("--------------------------------------", PRINTER_LENGTH)));
		if (returnReplacementExist) {
			builder.append(getPaddedString(getLeftAlignedString("Return & Replacement", 21) + " Rs " + getRightAlignedString(String.valueOf(replacementValue - returnValue), 6)));
		}
		builder.append(getPaddedString(getLeftAlignedString("Discount", 21) + " Rs " + getRightAlignedString(String.valueOf(order.getDiscount()), 6)));
		builder.append(getPaddedString(getLeftAlignedString("-------------------------------------", PRINTER_LENGTH)));
		builder.append(getPaddedString(getLeftAlignedString("Grand Total", 21) + " Rs " + getRightAlignedString(String.valueOf(sum + replacementValue - returnValue - order.getDiscount()), 6)));
		builder.append(getPaddedString(getLeftAlignedString("-------------------------------------------------", PRINTER_LENGTH)));
		builder.append(getPaddedString(getLeftAlignedString("      " + "HAVE A LUCKY DAY !!!", PRINTER_LENGTH)));
		builder.append(getPaddedString(getLeftAlignedString("------------------------------------------------", PRINTER_LENGTH)));
		builder.append("\n\n");
		return builder.toString().getBytes();
	}

	private byte[] getOrderCopyIntoByteStream(Order order) {
		SimpleDateFormat dateFormatter = new SimpleDateFormat();
		dateFormatter.applyPattern("dd MMM, yyyy");
		Date date = new Date();
		StringBuilder builder = new StringBuilder();
		builder.append(getPaddedString(getLeftAlignedString("Lucky Lanka Milk Processing PLC", PRINTER_LENGTH)));
		builder.append(getPaddedString(getLeftAlignedString("Bibulewela,Karagoda,Uyangoda.", PRINTER_LENGTH)));
		builder.append(getPaddedString(getLeftAlignedString("HotLine: 011-7215021", PRINTER_LENGTH)));
		builder.append(getPaddedString(getLeftAlignedString("-------------------------------------------", PRINTER_LENGTH)));
		builder.append(getPaddedString(getLeftAlignedString("Outlet : " + outlet.getOutletName(), PRINTER_LENGTH)));
		builder.append(getPaddedString(getLeftAlignedString("Date   : " + dateFormatter.format(date), PRINTER_LENGTH)));
		dateFormatter.applyPattern("hh:mm:ss aa");
		builder.append(getPaddedString(getLeftAlignedString("Time   : " + dateFormatter.format(date), PRINTER_LENGTH)));
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
		builder.append(getPaddedString(getLeftAlignedString("-------------------------------------------", PRINTER_LENGTH)));
		double returnValue = 0;
		boolean returnReplacementExist = false;
		for (OrderDetail orderDetail : order.getOrderDetails()) {
			if (orderDetail.getReturnQuantity() != 0) {
				returnValue += (orderDetail.getPrice() * orderDetail.getReturnQuantity());
				returnReplacementExist = true;
			}
		}
		if (returnValue != 0) {
			builder.append(getPaddedString(getLeftAlignedString("Return:", PRINTER_LENGTH)));
			for (OrderDetail orderDetail : order.getOrderDetails()) {
				if (orderDetail.getReturnQuantity() != 0) {
					builder.append(getPaddedString(getLeftAlignedString(String.valueOf(orderDetail.getReturnQuantity()), 3) + "  " + getLeftAlignedString(orderDetail.getItemShortName(), 13) + getRightAlignedString(String.valueOf(orderDetail.getPrice()), 4) + getRightAlignedString("                  ", 9)));
				}
			}
		}
		double replacementValue = 0;
		for (OrderDetail orderDetail : order.getOrderDetails()) {
			if (orderDetail.getReplaceQuantity() != 0) {
				replacementValue += (orderDetail.getPrice() * orderDetail.getReplaceQuantity());
				returnReplacementExist = true;
			}
		}
		if (replacementValue != 0) {
			builder.append(getPaddedString(getLeftAlignedString("Replacement:", PRINTER_LENGTH)));
			for (OrderDetail orderDetail : order.getOrderDetails()) {
				if (orderDetail.getReplaceQuantity() != 0) {
					builder.append(getPaddedString(getLeftAlignedString(String.valueOf(orderDetail.getReplaceQuantity()), 3) + "  " + getLeftAlignedString(orderDetail.getItemShortName(), 13) + getRightAlignedString(String.valueOf(orderDetail.getPrice()), 4) + getRightAlignedString("                  ", 9)));
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
		builder.append(getPaddedString(getLeftAlignedString("--------------------------------------", PRINTER_LENGTH)));
		if (returnReplacementExist) {
			builder.append(getPaddedString(getLeftAlignedString("Return & Replacement", 21) + " Rs " + getRightAlignedString(String.valueOf(replacementValue - returnValue), 6)));
		}
		builder.append(getPaddedString(getLeftAlignedString("Discount", 21) + " Rs " + getRightAlignedString(String.valueOf(order.getDiscount()), 6)));
		builder.append(getPaddedString(getLeftAlignedString("-------------------------------------", PRINTER_LENGTH)));
		builder.append(getPaddedString(getLeftAlignedString("Grand Total", 21) + " Rs " + getRightAlignedString(String.valueOf(sum + replacementValue - returnValue - order.getDiscount()), 6)));
		double totallyPaid = 0;
		for (Payment payment : order.getPayments()) {
			totallyPaid += payment.getAmount();
		}
		builder.append(getPaddedString(getLeftAlignedString("Total Paid", 21) + " Rs " + getRightAlignedString(String.valueOf(totallyPaid), 6)));
		builder.append(getPaddedString(getLeftAlignedString("Balance", 21) + " Rs " + getRightAlignedString(String.valueOf(sum + replacementValue - returnValue - order.getDiscount() - totallyPaid), 6)));
		builder.append(getPaddedString(getLeftAlignedString("-------------------------------------------------", PRINTER_LENGTH)));
		builder.append(getPaddedString(getLeftAlignedString("Signature : ", PRINTER_LENGTH)));
		builder.append(getPaddedString(getLeftAlignedString("-------------------------------------------------", PRINTER_LENGTH)));
		builder.append(getPaddedString(getLeftAlignedString("      " + "HAVE A LUCKY DAY !!!", PRINTER_LENGTH)));
		builder.append(getPaddedString(getLeftAlignedString("------------------------------------------------", PRINTER_LENGTH)));
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

	public static class PaymentViewHolder {

		TextView txtPaidValue;
		TextView txtPaymentMethod;
		TextView txtChequeNo;
		TextView txtBankingDate;
		ImageButton imageButton;
	}
}
