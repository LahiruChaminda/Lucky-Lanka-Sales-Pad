/*
 * Intellectual properties of Supun Lakshan Wanigarathna Dissanayake
 * Copyright (c) 2014, Supun Lakshan Wanigarathna Dissanayake. All rights reserved.
 * Created on : Jun 10, 2014, 8:20:35 PM
 */

package com.ceylon_linux.lucky_lanka.model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * @author Supun Lakshan Wanigarathna Dissanayake
 * @mobile +94711290392
 * @email supunlakshan.xfinity@gmail.com
 */
public class Order {

	private int outletId;
	private String outletDescription;
	private int positionId;
	private int routeId;
	private long invoiceTime;
	private ArrayList<OrderDetail> orderDetails;

	public Order(int outletId, int positionId, int routeId, long invoiceTime, ArrayList<OrderDetail> orderDetails, String outletDescription) {
		this.setOutletId(outletId);
		this.setPositionId(positionId);
		this.setRouteId(routeId);
		this.setInvoiceTime(invoiceTime);
		this.setOrderDetails(orderDetails);
		this.setOutletDescription(outletDescription);
	}

	public Order(int outletId, int positionId, int routeId, long invoiceTime, ArrayList<OrderDetail> orderDetails) {
		this.setOutletId(outletId);
		this.setPositionId(positionId);
		this.setRouteId(routeId);
		this.setInvoiceTime(invoiceTime);
		this.setOrderDetails(orderDetails);
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

	public ArrayList<OrderDetail> getOrderDetails() {
		return orderDetails;
	}

	public void setOrderDetails(ArrayList<OrderDetail> orderDetails) {
		this.orderDetails = orderDetails;
	}

	@Override
	public String toString() {
		return outletDescription;
	}

	public JSONObject getOrderAsJson() {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat();
		simpleDateFormat.applyPattern("yyyy-MM-dd");
		Date invoiceDate = new Date(invoiceTime);
		HashMap<String, Object> orderParams = new HashMap<String, Object>();
		orderParams.put("outletId", outletId);
		orderParams.put("routeId", routeId);
		orderParams.put("date", simpleDateFormat.format(invoiceDate));
		simpleDateFormat.applyPattern("HH:mm:ss");
		orderParams.put("time", simpleDateFormat.format(invoiceDate));
		JSONArray orderDetailsJsonArray = new JSONArray();
		for (OrderDetail orderDetail : orderDetails) {
			orderDetailsJsonArray.put(orderDetail.getOrderDetailAsJson());
		}
		orderParams.put("items", orderDetailsJsonArray);
		return new JSONObject(orderParams);
	}
}
