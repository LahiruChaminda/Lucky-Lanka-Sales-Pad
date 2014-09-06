/*
 * Intellectual properties of Supun Lakshan Wanigarathna Dissanayake
 * Copyright (c) 2014, Supun Lakshan Wanigarathna Dissanayake. All rights reserved.
 * Created on : Jul 07, 2014, 3:11 PM
 */

package com.ceylon_linux.lucky_lanka.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
public class PaymentActivity extends Activity {


	private final int PAYMENT_DONE = 3;
	private Order order;
	private Outlet outlet;
	private Button btnCashPayment;
	private Button btnChequePayment;
	private Button btnPrintInvoice;
	private EditText inputDiscount;
	private ListView listPayment;
	private TextView txtInvoiceTotal;
	private TextView txtTotallyPaid;
	private BaseAdapter adapter;


	private NumberFormat currencyFormat;
	private double invoiceTotal = 0;
	private boolean validOrder = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.payments_page);
		initialize();
		switch (outlet.getOutletType()) {
			case Outlet.SPECIAL_DISCOUNT_WITHOUT_FREE:
			case Outlet.SPECIAL_DISCOUNT_WITH_FREE:
				inputDiscount.setEnabled(true);
				break;
			default:
				inputDiscount.setEnabled(false);
				break;
		}
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
				String valueString;
				double discount = (valueString = inputDiscount.getText().toString()).isEmpty() ? 0 : Double.parseDouble(valueString);
				if (invoiceTotal - discount < sum) {
					txtTotallyPaid.setBackgroundColor(Color.parseColor("#FF8080"));
					validOrder = false;
				} else if (invoiceTotal - discount > sum) {
					txtTotallyPaid.setBackgroundColor(Color.parseColor("#FFFF80"));
					validOrder = true;
				} else {
					txtTotallyPaid.setBackgroundColor(Color.parseColor("#80FF80"));
					validOrder = true;
				}
				txtTotallyPaid.setText("Rs " + currencyFormat.format(sum + discount));
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
		inputDiscount.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				adapter.notifyDataSetChanged();
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
		if (validOrder) {
			String valueString;
			order.setDiscount((valueString = inputDiscount.getText().toString()).isEmpty() ? 0 : Double.parseDouble(valueString));
			Intent printPreviewActivity = new Intent(PaymentActivity.this, PrintPreviewActivity.class);
			printPreviewActivity.putExtra("order", order);
			printPreviewActivity.putExtra("outlet", outlet);
			startActivity(printPreviewActivity);
			finish();
		} else {
			AlertDialog.Builder alertBuilder = new AlertDialog.Builder(PaymentActivity.this);
			alertBuilder.setMessage("Please check payments!");
			alertBuilder.setTitle(R.string.app_name);
			alertBuilder.setPositiveButton("Ok", null);
			alertBuilder.show();
		}
	}


	@Override
	public void onBackPressed() {
		Intent posmSelectionActivity = new Intent(PaymentActivity.this, PosmSelectionActivity.class);
		posmSelectionActivity.putExtra("order", order);
		posmSelectionActivity.putExtra("outlet", outlet);
		startActivity(posmSelectionActivity);
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
