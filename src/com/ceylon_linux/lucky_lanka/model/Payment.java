/*
 * Intellectual properties of Supun Lakshan Wanigarathna Dissanayake
 * Copyright (c) 2014, Supun Lakshan Wanigarathna Dissanayake. All rights reserved.
 * Created on : Jul 01, 2014, 10:45 AM
 */

package com.ceylon_linux.lucky_lanka.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * @author Supun Lakshan Wanigarathna Dissanayake
 * @mobile +94711290392
 * @email supunlakshan.xfinity@gmail.com
 */
public class Payment implements Serializable {

	public static final byte ACCEPTED_PAYMENT = 0;
	public static final byte REJECTED_PAYMENT = 1;
	public static final byte PENDING_PAYMENT = 2;
	public static final byte AGED_PAYMENT = 3;
	public static final byte FRESH_PAYMENT = 4;

	private int paymentId;
	private Date paymentDate;
	private double amount;
	private Date chequeDate;
	private String chequeNo;
	private String bank;
	private int branchCode;
	private byte paymentStatus;

	public Payment(double amount, byte paymentStatus) {
		this.amount = amount;
		this.paymentDate = new Date();
		this.paymentStatus = paymentStatus;
	}

	public Payment(double amount, Date chequeDate, String chequeNo, String bank, int branchCode, byte paymentStatus) {
		this.amount = amount;
		this.chequeDate = chequeDate;
		this.chequeNo = chequeNo;
		this.paymentDate = new Date();
		this.bank = bank;
		this.branchCode = branchCode;
		this.paymentStatus = paymentStatus;
	}

	public Payment(int paymentId, Date paymentDate, double amount, byte paymentStatus) {
		this.paymentId = paymentId;
		this.paymentDate = paymentDate;
		this.amount = amount;
		this.paymentStatus = paymentStatus;
	}

	public Payment(int paymentId, Date paymentDate, double amount, Date chequeDate, String chequeNo, String bank, byte paymentStatus) {
		this.paymentId = paymentId;
		this.paymentDate = paymentDate;
		this.amount = amount;
		this.chequeDate = chequeDate;
		this.chequeNo = chequeNo;
		this.bank = bank;
		this.paymentStatus = paymentStatus;
	}

	public Payment(int paymentId, Date paymentDate, double amount, Date chequeDate, String chequeNo, int branchCode, byte paymentStatus) {
		this.paymentId = paymentId;
		this.paymentDate = paymentDate;
		this.amount = amount;
		this.chequeDate = chequeDate;
		this.chequeNo = chequeNo;
		this.branchCode = branchCode;
		this.paymentStatus = paymentStatus;
	}

	public static Payment parseChequePayment(JSONObject jsonInstance) throws JSONException, ParseException {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		return new Payment(
			jsonInstance.getInt("idpayment"),
			simpleDateFormat.parse(jsonInstance.getString("pm_date")),
			jsonInstance.getDouble("pm_amount"),
			simpleDateFormat.parse(jsonInstance.getString("pc_date")),
			jsonInstance.getString("pc_no"),
			jsonInstance.getString("b_name"),
			ACCEPTED_PAYMENT
		);
	}

	public static Payment parseCashPayment(JSONObject jsonInstance) throws JSONException, ParseException {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		return new Payment(
			jsonInstance.getInt("idpayment"),
			simpleDateFormat.parse(jsonInstance.getString("pm_date")),
			jsonInstance.getDouble("pm_amount"),
			ACCEPTED_PAYMENT
		);
	}

	public long getPaymentId() {
		return paymentId;
	}

	public void setPaymentId(int paymentId) {
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

	public String getBank() {
		return bank;
	}

	public void setBank(String bank) {
		this.bank = bank;
	}

	public byte getPaymentStatus() {
		return paymentStatus;
	}

	public void setPaymentStatus(byte paymentStatus) {
		this.paymentStatus = paymentStatus;
	}

	public int getBranchCode() {
		return branchCode;
	}

	public void setBranchCode(int branchCode) {
		this.branchCode = branchCode;
	}

	public JSONObject getPaymentAsJson() {
		HashMap<String, Object> parameters = new HashMap<String, Object>();
		boolean isChequePayment = chequeNo != null && !chequeNo.isEmpty();
		SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
		parameters.put("creditpayment", 0);
		parameters.put("cashpayment", isChequePayment ? 0 : amount);
		parameters.put("cheque_date", isChequePayment ? dateFormatter.format(chequeDate) : "");
		parameters.put("chequeNo", isChequePayment ? chequeNo : "");
		parameters.put("bank", isChequePayment ? branchCode : 0);
		parameters.put("realizeddate", isChequePayment ? dateFormatter.format(chequeDate) : "");
		parameters.put("chequepayment", isChequePayment ? amount : 0);
		parameters.put("type", isChequePayment ? "Cheque" : "Cash");
		return new JSONObject(parameters);
	}
}
