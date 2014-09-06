/*
 * Intellectual properties of Supun Lakshan Wanigarathna Dissanayake
 * Copyright (c) 2014, Supun Lakshan Wanigarathna Dissanayake. All rights reserved.
 * Created on : Jun 10, 2014, 8:21:21 PM
 */

package com.ceylon_linux.lucky_lanka.model;

import android.content.Context;
import com.ceylon_linux.lucky_lanka.controller.ItemController;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.HashMap;

/**
 * @author Supun Lakshan Wanigarathna Dissanayake
 * @mobile +94711290392
 * @email supunlakshan.xfinity@gmail.com
 */
public class OrderDetail implements Serializable {

	private int itemId;
	private String itemDescription;
	private String itemShortName;
	private int quantity;
	private int freeIssue;
	private double price;
	private int returnQuantity;
	private int replaceQuantity;
	private int sampleQuantity;

	private OrderDetail(Item item, int quantity, int freeIssue, int returnQuantity, int replaceQuantity, int sampleQuantity, String itemShortName) {
		this.itemId = item.getItemId();
		this.itemDescription = item.getItemDescription();
		this.quantity = quantity;
		this.freeIssue = freeIssue;
		this.price = item.getWholeSalePrice();
		this.returnQuantity = returnQuantity;
		this.replaceQuantity = replaceQuantity;
		this.sampleQuantity = sampleQuantity;
		this.itemShortName = itemShortName;
	}

	private OrderDetail(OrderDetail orderDetail, int quantity, int freeIssue, int returnQuantity, int replaceQuantity, int sampleQuantity, String itemShortName) {
		this.itemId = orderDetail.getItemId();
		this.itemDescription = orderDetail.getItemDescription();
		this.quantity = quantity;
		this.freeIssue = freeIssue;
		this.price = orderDetail.getPrice();
		this.returnQuantity = returnQuantity;
		this.replaceQuantity = replaceQuantity;
		this.sampleQuantity = sampleQuantity;
		this.itemShortName = itemShortName;
	}

	private OrderDetail(String itemDescription, int quantity, int freeIssue, int returnQuantity, int replaceQuantity, int sampleQuantity, String itemShortName, int itemId) {
		this.itemId = itemId;
		this.itemDescription = itemDescription;
		this.quantity = quantity;
		this.freeIssue = freeIssue;
		this.returnQuantity = returnQuantity;
		this.replaceQuantity = replaceQuantity;
		this.sampleQuantity = sampleQuantity;
		this.itemShortName = itemShortName;
	}

	private OrderDetail(Item item, int quantity, double discountPercentage, int returnQuantity, int replaceQuantity, int sampleQuantity, String itemShortName) {
		this.itemId = item.getItemId();
		this.itemDescription = item.getItemDescription();
		this.quantity = quantity;
		this.price = item.getRetailSalePrice() * (100 - discountPercentage) / 100;
		this.returnQuantity = returnQuantity;
		this.replaceQuantity = replaceQuantity;
		this.sampleQuantity = sampleQuantity;
		this.itemShortName = itemShortName;
	}

	private OrderDetail(OrderDetail orderDetail, int quantity, double discountPercentage, int returnQuantity, int replaceQuantity, int sampleQuantity, String itemShortName) {
		this.itemId = orderDetail.getItemId();
		this.itemDescription = orderDetail.getItemDescription();
		this.quantity = quantity;
		this.price = orderDetail.getPrice() * (100 - discountPercentage) / 100;
		this.returnQuantity = returnQuantity;
		this.replaceQuantity = replaceQuantity;
		this.sampleQuantity = sampleQuantity;
		this.itemShortName = itemShortName;
	}

	public OrderDetail(int itemId, String itemDescription, int quantity, int freeIssue, double price, int returnQuantity, int replaceQuantity, int sampleQuantity, String itemShortName) {
		this.itemId = itemId;
		this.itemDescription = itemDescription;
		this.quantity = quantity;
		this.freeIssue = freeIssue;
		this.price = price;
		this.returnQuantity = returnQuantity;
		this.replaceQuantity = replaceQuantity;
		this.sampleQuantity = sampleQuantity;
		this.itemShortName = itemShortName;
	}

	public OrderDetail(int itemId, String itemDescription, int quantity, double price, int returnQuantity, int replaceQuantity, int sampleQuantity, String itemShortName) {
		this.itemId = itemId;
		this.itemDescription = itemDescription;
		this.quantity = quantity;
		this.price = price;
		this.returnQuantity = returnQuantity;
		this.replaceQuantity = replaceQuantity;
		this.sampleQuantity = sampleQuantity;
		this.itemShortName = itemShortName;
	}

	public static final OrderDetail getOrderDetail(Outlet outlet, Item item, int quantity, int returnQuantity, int replaceQuantity, int sampleQuantity, Context context) {
		int freeIssue;
		double discountPercentage;
		switch (outlet.getOutletType()) {
			case Outlet.PHARMACY:
			case Outlet.RETAIL_OUTLET:
			case Outlet.CANTEEN:
			case Outlet.BAKER:
			case Outlet.HOTEL:
			case Outlet.WELFARE_SHOPS:
			case Outlet.OTHER:
			case Outlet.STORES:
				freeIssue = (item.getFreeItemId() == item.getItemId()) ? ItemController.getFreeIssue(item.getItemId(), quantity, false, context) : 0;
				return new OrderDetail(item, quantity, freeIssue, returnQuantity, replaceQuantity, sampleQuantity, item.getItemShortName());
			case Outlet.WHOLESALE_OUTLET:
				freeIssue = (item.getFreeItemId() == item.getItemId()) ? ItemController.getFreeIssue(item.getItemId(), quantity, true, context) : 0;
				return new OrderDetail(item, quantity, freeIssue, returnQuantity, replaceQuantity, sampleQuantity, item.getItemShortName());
			case Outlet.SUPER_MARKET:
			case Outlet.SPECIAL_DISCOUNT_WITHOUT_FREE:
				discountPercentage = outlet.getOutletDiscount();
				return new OrderDetail(item, quantity, discountPercentage, returnQuantity, replaceQuantity, sampleQuantity, item.getItemShortName());
			default:
				throw new IllegalArgumentException("Unknown outlet type");
		}
	}

	public static final OrderDetail getFreeIssueDetail(Outlet outlet, Item item, int quantity, Context context) {
		int freeIssue;
		double discountPercentage;
		switch (outlet.getOutletType()) {
			case Outlet.PHARMACY:
			case Outlet.RETAIL_OUTLET:
			case Outlet.CANTEEN:
			case Outlet.BAKER:
			case Outlet.HOTEL:
			case Outlet.WELFARE_SHOPS:
			case Outlet.OTHER:
			case Outlet.STORES:
				freeIssue = ItemController.getFreeIssue(item.getItemId(), quantity, false, context);
				return new OrderDetail(item, quantity, freeIssue, 0, 0, 0, item.getItemShortName());
			case Outlet.WHOLESALE_OUTLET:
				freeIssue = ItemController.getFreeIssue(item.getItemId(), quantity, true, context);
				return new OrderDetail(item, quantity, freeIssue, 0, 0, 0, item.getItemShortName());
			case Outlet.SUPER_MARKET:
			case Outlet.SPECIAL_DISCOUNT_WITHOUT_FREE:
				discountPercentage = outlet.getOutletDiscount();
				return new OrderDetail(item, quantity, discountPercentage, 0, 0, 0, item.getItemShortName());
			default:
				throw new IllegalArgumentException("Unknown outlet type");
		}
	}

	public static final OrderDetail getFreeIssueDetail(Outlet outlet, OrderDetail orderDetail, int quantity, Context context) {
		int freeIssue;
		double discountPercentage;
		switch (outlet.getOutletType()) {
			case Outlet.PHARMACY:
			case Outlet.RETAIL_OUTLET:
			case Outlet.CANTEEN:
			case Outlet.BAKER:
			case Outlet.HOTEL:
			case Outlet.WELFARE_SHOPS:
			case Outlet.OTHER:
			case Outlet.STORES:
				freeIssue = ItemController.getFreeIssue(orderDetail.getItemId(), quantity, false, context);
				return new OrderDetail(orderDetail, quantity, freeIssue, 0, 0, 0, orderDetail.getItemShortName());
			case Outlet.WHOLESALE_OUTLET:
				freeIssue = ItemController.getFreeIssue(orderDetail.getItemId(), quantity, true, context);
				return new OrderDetail(orderDetail, quantity, freeIssue, 0, 0, 0, orderDetail.getItemShortName());
			case Outlet.SUPER_MARKET:
			case Outlet.SPECIAL_DISCOUNT_WITHOUT_FREE:
				discountPercentage = outlet.getOutletDiscount();
				return new OrderDetail(orderDetail, quantity, discountPercentage, 0, 0, 0, orderDetail.getItemShortName());
			default:
				throw new IllegalArgumentException("Unknown outlet type");
		}
	}

	public String getItemShortName() {
		return itemShortName;
	}

	public void setItemShortName(String itemShortName) {
		this.itemShortName = itemShortName;
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
		orderDetailsParams.put("return", returnQuantity);
		orderDetailsParams.put("replace", replaceQuantity);
		orderDetailsParams.put("sample", sampleQuantity);
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
		if (o == null || o.getClass() != OrderDetail.class) return false;
		OrderDetail that = (OrderDetail) o;
		if (itemId != that.itemId) return false;
		return true;
	}

	@Override
	public int hashCode() {
		return itemId;
	}
}
