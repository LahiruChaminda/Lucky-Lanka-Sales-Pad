/*
 * Intellectual properties of Supun Lakshan Wanigarathna Dissanayake
 * Copyright (c) 2014, Supun Lakshan Wanigarathna Dissanayake. All rights reserved.
 * Created on : Jul 01, 2014, 10:25 AM
 */
package com.ceylon_linux.lucky_lanka.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * @author Supun Lakshan Wanigarathna Dissanayake
 * @mobile +94711290392
 * @email supunlakshan.xfinity@gmail.com
 */
public class Invoice implements Serializable {
	private int invoiceId;
	private Date date;
	private double amount;
	private ArrayList<Payment> payments;

	public Invoice(int invoiceId, long date, double amount, ArrayList<Payment> payments) {
		this.invoiceId = invoiceId;
		this.date = new Date(date);
		this.amount = amount;
		this.payments = payments;
	}

	public Invoice(int invoiceId, String dateTime, double amount, ArrayList<Payment> payments) throws ParseException {
		this.invoiceId = invoiceId;
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		this.date = simpleDateFormat.parse(dateTime);
		this.amount = amount;
		this.payments = payments;
	}

	public static Invoice parseInvoice(JSONObject jsonInstance) throws JSONException, ParseException {
		System.out.println("invoice" + jsonInstance);
		JSONArray cashArray = jsonInstance.getJSONArray("cash");
		JSONArray chequeArray = jsonInstance.getJSONArray("cheque");
		ArrayList<Payment> payments = new ArrayList<Payment>();
		for (int i = 0; i < cashArray.length(); i++) {
			payments.add(Payment.parseCashPayment(cashArray.getJSONObject(i)));
		}
		for (int i = 0; i < chequeArray.length(); i++) {
			payments.add(Payment.parseChequePayment(chequeArray.getJSONObject(i)));
		}
		return new Invoice(
			jsonInstance.getInt("idinvoice"),
			jsonInstance.getString("i_date") + " " + jsonInstance.getString("i_time"),
			jsonInstance.getDouble("amount"),
			payments
		);
	}

	public long getInvoiceId() {
		return invoiceId;
	}

	public void setInvoiceId(int invoiceId) {
		this.invoiceId = invoiceId;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public ArrayList<Payment> getPayments() {
		return (payments == null) ? payments = new ArrayList<Payment>() : payments;
	}

	public void setPayments(ArrayList<Payment> payments) {
		this.payments = payments;
	}

	public double getPaidValue() {
		if (payments == null || payments.size() == 0) {
			return 0;
		} else {
			double paidValue = 0;
			for (Payment payment : payments) {
				paidValue += payment.getAmount();
			}
			return paidValue;
		}
	}

	public double getBalanceValue() {
		if (payments == null || payments.size() == 0) {
			return amount;
		} else {
			double paidValue = 0;
			for (Payment payment : payments) {
				paidValue += payment.getAmount();
			}
			return amount - paidValue;
		}
	}

	public JSONObject getInvoiceAsJson() {
		HashMap<String, Object> jsonParams = new HashMap<String, Object>();
		jsonParams.put("invoiceId", invoiceId);
		JSONArray paymentsJsonArray = new JSONArray();
		for (Payment payment : payments) {
			paymentsJsonArray.put(payment.getPaymentAsJson());
		}
		jsonParams.put("payments", paymentsJsonArray);
		return new JSONObject(jsonParams);
	}
}
