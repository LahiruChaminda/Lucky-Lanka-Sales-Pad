/*
 * Intellectual properties of Supun Lakshan Wanigarathna Dissanayake
 * Copyright (c) 2014, Supun Lakshan Wanigarathna Dissanayake. All rights reserved.
 * Created on : Jul 01, 2014, 10:45 AM
 */
package com.ceylon_linux.lucky_lanka.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Supun Lakshan Wanigarathna Dissanayake
 * @mobile +94711290392
 * @email supunlakshan.xfinity@gmail.com
 */
public class Payment {
	private long paymentId;
	private Date paymentDate;
	private double amount;
	private Date chequeDate;
	private String chequeNo;
	private boolean synced;

	public Payment(long paymentId, Date paymentDate, double amount, boolean synced) {
		this.paymentId = paymentId;
		this.paymentDate = paymentDate;
		this.amount = amount;
	}

	public Payment(long paymentId, Date paymentDate, double amount, Date chequeDate, String chequeNo, boolean synced) {
		this.paymentId = paymentId;
		this.paymentDate = paymentDate;
		this.amount = amount;
		this.chequeDate = chequeDate;
		this.chequeNo = chequeNo;
	}

	public static Payment parseChequePayment(JSONObject jsonInstance) throws JSONException, ParseException {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		return new Payment(
			jsonInstance.getLong("idpayment"),
			simpleDateFormat.parse(jsonInstance.getString("pm_date")),
			jsonInstance.getDouble("pm_amount"),
			simpleDateFormat.parse(jsonInstance.getString("pc_date")),
			jsonInstance.getString("pc_no"),
			true
		);
	}

	public static Payment parseCashPayment(JSONObject jsonInstance) throws JSONException, ParseException {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		return new Payment(
			jsonInstance.getLong("idpayment"),
			simpleDateFormat.parse(jsonInstance.getString("pm_date")),
			jsonInstance.getDouble("pm_amount"),
			true
		);
	}

	public long getPaymentId() {
		return paymentId;
	}

	public void setPaymentId(long paymentId) {
		this.paymentId = paymentId;
	}

	public Date getPaymentDate() {
		return paymentDate;
	}

	public void setPaymentDate(Date paymentDate) {
		this.paymentDate = paymentDate;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public Date getChequeDate() {
		return chequeDate;
	}

	public void setChequeDate(Date chequeDate) {
		this.chequeDate = chequeDate;
	}

	public String getChequeNo() {
		return chequeNo;
	}

	public void setChequeNo(String chequeNo) {
		this.chequeNo = chequeNo;
	}

	public boolean isSynced() {
		return synced;
	}

	public void setSynced(boolean synced) {
		this.synced = synced;
	}
}
