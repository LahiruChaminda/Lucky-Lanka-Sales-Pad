/*
 * Intellectual properties of Supun Lakshan Wanigarathna Dissanayake
 * Copyright (c) 2014, Supun Lakshan Wanigarathna Dissanayake. All rights reserved.
 * Created on : Jun 10, 2014, 8:20:35 PM
 */

package com.ceylon_linux.lucky_lanka.model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * @author Supun Lakshan Wanigarathna Dissanayake
 * @mobile +94711290392
 * @email supunlakshan.xfinity@gmail.com
 */
public class Order implements Serializable {

	private long orderId;
	private int outletId;
	private String outletDescription;
	private int positionId;
	private int routeId;
	private long invoiceTime;
	private double longitude;
	private double latitude;
	private int batteryLevel;
	private double discount;
	private ArrayList<OrderDetail> orderDetails;
	private ArrayList<Payment> payments;
	private ArrayList<PosmDetail> posmDetails;

	public Order(int outletId, int positionId, int routeId, int batteryLevel, long invoiceTime, double longitude, double latitude, ArrayList<OrderDetail> orderDetails, ArrayList<PosmDetail> posmDetails) {
		this.setOutletId(outletId);
		this.setPositionId(positionId);
		this.setRouteId(routeId);
		this.setBatteryLevel(batteryLevel);
		this.setInvoiceTime(invoiceTime);
		this.setLongitude(longitude);
		this.setLatitude(latitude);
		this.setOrderDetails(orderDetails);
		this.posmDetails = posmDetails;
	}

	public Order(long orderId, int outletId, String outletDescription, int positionId, int routeId, long invoiceTime, double longitude, double latitude, int batteryLevel, ArrayList<OrderDetail> orderDetails, ArrayList<PosmDetail> posmDetails) {
		this.setOrderId(orderId);
		this.setOutletId(outletId);
		this.setOutletDescription(outletDescription);
		this.setPositionId(positionId);
		this.setRouteId(routeId);
		this.setInvoiceTime(invoiceTime);
		this.setLongitude(longitude);
		this.setLatitude(latitude);
		this.setBatteryLevel(batteryLevel);
		this.setOrderDetails(orderDetails);
		this.posmDetails = posmDetails;
	}

	public JSONObject getOrderAsJson() {
		HashMap<String, Object> orderJsonParams = new HashMap<String, Object>();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat();
		simpleDateFormat.applyPattern("yyyy-MM-dd");
		Date invoiceDate = new Date(invoiceTime);
		HashMap<String, Object> invoiceParams = new HashMap<String, Object>();
		invoiceParams.put("outletid", outletId);
		invoiceParams.put("orderId", orderId);
		invoiceParams.put("routeid", routeId);
		invoiceParams.put("discount", discount);
		invoiceParams.put("invtype", 0);
		invoiceParams.put("invDate", simpleDateFormat.format(invoiceDate));
		simpleDateFormat.applyPattern("HH:mm:ss");
		invoiceParams.put("invtime", simpleDateFormat.format(invoiceDate));
		invoiceParams.put("lon", String.valueOf(getLongitude()));
		invoiceParams.put("lat", String.valueOf(getLatitude()));
		invoiceParams.put("bat", batteryLevel);
		JSONArray orderDetailsJsonArray = new JSONArray();
		for (OrderDetail orderDetail : orderDetails) {
			orderDetailsJsonArray.put(orderDetail.getOrderDetailAsJson());
		}
		JSONArray paymentsJsonArray = new JSONArray();
		for (Payment payment : payments) {
			paymentsJsonArray.put(payment.getPaymentAsJson());
		}
		JSONArray posmJsonArray = new JSONArray();
		for (PosmDetail posmDetail : posmDetails) {
			posmJsonArray.put(posmDetail.getPosmDetailsAsJson());
		}
		orderJsonParams.put("invitems", orderDetailsJsonArray);
		orderJsonParams.put("Payment", paymentsJsonArray);
		orderJsonParams.put("posm", posmJsonArray);
		orderJsonParams.put("Invoice", new JSONObject(invoiceParams));
		return new JSONObject(orderJsonParams);
	}

	public boolean isCreditBill() {
		if (payments == null) {
			return true;
		}
		double paymentSum = 0;
		double invoiceSum = 0;
		for (Payment payment : payments) {
			paymentSum += payment.getAmount();
		}
		for (OrderDetail orderDetail : orderDetails) {
			invoiceSum += (orderDetail.getQuantity() * orderDetail.getPrice());
		}
		return invoiceSum - discount > paymentSum;
	}

	public double getDiscount() {
		return discount;
	}

	public void setDiscount(double discount) {
		this.discount = discount;
	}

	public long getOrderId() {
		return orderId;
	}

	public void setOrderId(long orderId) {
		this.orderId = orderId;
	}

	public int getOutletId() {
		return outletId;
	}

	public void setOutletId(int outletId) {
		this.outletId = outletId;
	}

	public String getOutletDescription() {
		return outletDescription;
	}

	public void setOutletDescription(String outletDescription) {
		this.outletDescription = outletDescription;
	}

	public int getPositionId() {
		return positionId;
	}

	public void setPositionId(int positionId) {
		this.positionId = positionId;
	}

	public int getRouteId() {
		return routeId;
	}

	public void setRouteId(int routeId) {
		this.routeId = routeId;
	}

	public long getInvoiceTime() {
		return invoiceTime;
	}

	public void setInvoiceTime(long invoiceTime) {
		this.invoiceTime = invoiceTime;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public int getBatteryLevel() {
		return batteryLevel;
	}

	public void setBatteryLevel(int batteryLevel) {
		this.batteryLevel = batteryLevel;
	}

	public ArrayList<OrderDetail> getOrderDetails() {
		return orderDetails;
	}

	public void setOrderDetails(ArrayList<OrderDetail> orderDetails) {
		this.orderDetails = orderDetails;
	}

	public ArrayList<Payment> getPayments() {
		return payments;
	}

	public void setPayments(ArrayList<Payment> payments) {
		this.payments = payments;
	}

	public ArrayList<PosmDetail> getPosmDetails() {
		return posmDetails;
	}

	public void setPosmDetails(ArrayList<PosmDetail> posmDetails) {
		this.posmDetails = posmDetails;
	}

	@Override
	public String toString() {
		return outletDescription;
	}

	@Override
	public boolean equals(Object o) {
		Order order = (Order) o;
		if (this == o) return true;
		if (o == null || o.getClass() != Order.class) return false;
		if (orderId != order.orderId) return false;
		return true;
	}

	@Override
	public int hashCode() {
		return (int) (orderId ^ (orderId >>> 32));
	}
}
