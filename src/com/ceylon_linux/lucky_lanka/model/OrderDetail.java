/*
 * Intellectual properties of Supun Lakshan Wanigarathna Dissanayake
 * Copyright (c) 2014, Supun Lakshan Wanigarathna Dissanayake. All rights reserved.
 * Created on : Jun 10, 2014, 8:21:21 PM
 */

package com.ceylon_linux.lucky_lanka.model;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * @author Supun Lakshan Wanigarathna Dissanayake
 * @mobile +94711290392
 * @email supunlakshan.xfinity@gmail.com
 */
public class OrderDetail {

	private int itemId;
	private String itemDescription;
	private int quantity;
	private int freeIssue;
	private double price;
	private int returnQuantity;
	private int replaceQuantity;
	private int sampleQuantity;

	private OrderDetail(Item item, int quantity, int freeIssue, int returnQuantity, int replaceQuantity, int sampleQuantity) {
		this.itemId = item.getItemId();
		this.itemDescription = item.getItemDescription();
		this.quantity = quantity;
		this.freeIssue = freeIssue;
		this.price = item.getWholeSalePrice();
		this.returnQuantity = returnQuantity;
		this.replaceQuantity = replaceQuantity;
		this.sampleQuantity = sampleQuantity;
	}

	private OrderDetail(Item item, int quantity, double discountPercentage, int returnQuantity, int replaceQuantity, int sampleQuantity) {
		this.itemId = item.getItemId();
		this.itemDescription = item.getItemDescription();
		this.quantity = quantity;
		this.price = item.getRetailSalePrice() * (100 - discountPercentage) / 100;
		this.returnQuantity = returnQuantity;
		this.replaceQuantity = replaceQuantity;
		this.sampleQuantity = sampleQuantity;
	}

	public OrderDetail(int itemId, String itemDescription, int quantity, int freeIssue, double price, int returnQuantity, int replaceQuantity, int sampleQuantity) {
		this.itemId = itemId;
		this.itemDescription = itemDescription;
		this.quantity = quantity;
		this.freeIssue = freeIssue;
		this.price = price;
		this.returnQuantity = returnQuantity;
		this.replaceQuantity = replaceQuantity;
		this.sampleQuantity = sampleQuantity;
	}

	public OrderDetail(int itemId, String itemDescription, int quantity, double price, int returnQuantity, int replaceQuantity, int sampleQuantity) {
		this.itemId = itemId;
		this.itemDescription = itemDescription;
		this.quantity = quantity;
		this.price = price;
		this.returnQuantity = returnQuantity;
		this.replaceQuantity = replaceQuantity;
		this.sampleQuantity = sampleQuantity;
	}

	public static final OrderDetail getFreeIssueCalculatedOrderDetail(Outlet outlet, Item item, int quantity, int returnQuantity, int replaceQuantity, int sampleQuantity) {
		int freeIssue = 0;
		double discountPercentage = 0;
		int minimumFreeIssueQuantity = item.getMinimumFreeIssueQuantity();
		int freeIssueRatio = item.getFreeIssueQuantity();
		switch (outlet.getOutletType()) {
			case Outlet.NORMAL_OUTLET:
				if (quantity >= minimumFreeIssueQuantity) {
					freeIssue = ((int) (quantity / minimumFreeIssueQuantity)) * freeIssueRatio;
				}
				break;
			case Outlet.SIX_PLUS_ONE_OUTLET:
				if (quantity >= 216 && item.isSixPlusOneAvailability()) {
					freeIssue = ((int) (quantity / 216)) * 36;
				} else if (quantity >= minimumFreeIssueQuantity) {
					freeIssue = ((int) (quantity / minimumFreeIssueQuantity)) * freeIssueRatio;
				}
				break;
			case Outlet.SUPER_MARKET:
				discountPercentage = outlet.getOutletDiscount();
				break;
		}

		if (outlet.getOutletType() == Outlet.SUPER_MARKET) {
			return new OrderDetail(item, quantity, discountPercentage, returnQuantity, replaceQuantity, sampleQuantity);
		} else {
			return new OrderDetail(item, quantity, freeIssue, returnQuantity, replaceQuantity, sampleQuantity);
		}
	}

	public int getItemId() {
		return itemId;
	}

	public void setItemId(int itemId) {
		this.itemId = itemId;
	}

	public String getItemDescription() {
		return itemDescription;
	}

	public void setItemDescription(String itemDescription) {
		this.itemDescription = itemDescription;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public int getFreeIssue() {
		return freeIssue;
	}

	public void setFreeIssue(int freeIssue) {
		this.freeIssue = freeIssue;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public int getReturnQuantity() {
		return returnQuantity;
	}

	public void setReturnQuantity(int returnQuantity) {
		this.returnQuantity = returnQuantity;
	}

	public int getReplaceQuantity() {
		return replaceQuantity;
	}

	public void setReplaceQuantity(int replaceQuantity) {
		this.replaceQuantity = replaceQuantity;
	}

	public int getSampleQuantity() {
		return sampleQuantity;
	}

	public void setSampleQuantity(int sampleQuantity) {
		this.sampleQuantity = sampleQuantity;
	}

	public JSONObject getOrderDetailAsJson() {
		HashMap<String, Object> orderDetailsParams = new HashMap<String, Object>();
		orderDetailsParams.put("id_item", itemId);
		orderDetailsParams.put("qty", quantity);
		orderDetailsParams.put("free", freeIssue);
		orderDetailsParams.put("price", price);
		return new JSONObject(orderDetailsParams);
	}

	@Override
	public String toString() {
		return itemDescription;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		OrderDetail that = (OrderDetail) o;

		if (itemId != that.itemId) return false;

		return true;
	}

	@Override
	public int hashCode() {
		return itemId;
	}
}
