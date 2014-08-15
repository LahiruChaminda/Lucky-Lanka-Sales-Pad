/*
 * Intellectual properties of Supun Lakshan Wanigarathna Dissanayake
 * Copyright (c) 2014, Supun Lakshan Wanigarathna Dissanayake. All rights reserved.
 * Created on : Jul 07, 2014, 3:11 PM
 */

package com.ceylon_linux.lucky_lanka.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.ceylon_linux.lucky_lanka.R;
import com.ceylon_linux.lucky_lanka.model.Order;
import com.ceylon_linux.lucky_lanka.model.OrderDetail;
import com.ceylon_linux.lucky_lanka.model.Outlet;
import com.ceylon_linux.lucky_lanka.model.Payment;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * @author Supun Lakshan Wanigarathna Dissanayake
 * @mobile +94711290392
 * @email supunlakshan.xfinity@gmail.com
 */
public class OutstandingPaymentActivity extends Activity {

	private final int PAYMENT_DONE = 3;
	private Order order;
	private Outlet outlet;
	private Button btnCashPayment;
	private Button btnChequePayment;
	private Button btnSavePayment;
	private ListView listPayment;
	private TextView txtInvoiceTotal;
	private TextView txtTotallyPaid;
	private BaseAdapter adapter;
	private NumberFormat currencyFormat;
	private double invoiceTotal = 0;
	private boolean validOrder;

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
		btnSavePayment = (Button) findViewById(R.id.btnSavePayment);
		listPayment = (ListView) findViewById(R.id.listPayment);
		txtInvoiceTotal = (TextView) findViewById(R.id.txtInvoiceTotal);
		txtTotallyPaid = (TextView) findViewById(R.id.txtTotalPaid);
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
				if (invoiceTotal < sum) {
					txtTotallyPaid.setBackgroundColor(Color.parseColor("#FF8080"));
					validOrder = false;
				} else if (invoiceTotal > sum) {
					txtTotallyPaid.setBackgroundColor(Color.parseColor("#FFFF80"));
					validOrder = true;
				} else {
					txtTotallyPaid.setBackgroundColor(Color.parseColor("#80FF80"));
					validOrder = true;
				}
				txtTotallyPaid.setText("Rs " + currencyFormat.format(sum));
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
		btnSavePayment.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				btnSavePaymentClicked(view);
			}
		});
	}

	private void btnSavePaymentClicked(View view) {
		throw new UnsupportedOperationException("Plz implement this");
	}

	private void btnCashPaymentClicked(View view) {
		Intent cashPaymentActivity = new Intent(OutstandingPaymentActivity.this, CashPaymentActivity.class);
		startActivityForResult(cashPaymentActivity, PAYMENT_DONE);
	}

	private void btnChequePaymentClicked(View view) {
		Intent chequePaymentActivity = new Intent(OutstandingPaymentActivity.this, ChequePaymentActivity.class);
		startActivityForResult(chequePaymentActivity, PAYMENT_DONE);
	}

	@Override
	public void onBackPressed() {
		Intent selectItemActivity = new Intent(OutstandingPaymentActivity.this, SelectItemActivity.class);
		selectItemActivity.putExtra("order", order);
		selectItemActivity.putExtra("outlet", outlet);
		startActivity(selectItemActivity);
		finish();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
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

	public static class PaymentViewHolder {

		TextView txtPaidValue;
		TextView txtPaymentMethod;
		TextView txtChequeNo;
		TextView txtBankingDate;
		ImageButton imageButton;
	}
}
