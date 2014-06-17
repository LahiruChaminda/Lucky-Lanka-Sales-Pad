/*
 * Intellectual properties of Supun Lakshan Wanigarathna Dissanayake
 * Copyright (c) 2014, Supun Lakshan Wanigarathna Dissanayake. All rights reserved.
 * Created on : Jun 7, 2014, 11:06:25 AM
 */

package com.ceylon_linux.lucky_lanka.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Item - Description of Item
 *
 * @author Supun Lakshan Wanigarathna Dissanayake
 * @mobile +94711290392
 * @email supunlakshan.xfinity@gmail.com
 */
public class Item implements Serializable {

	private int itemId;
	private String itemCode;
	private String itemDescription;
	private int availableQuantity;
	private int loadedQuantity;
	private double wholeSalePrice;
	private double retailSalePrice;
	private boolean sixPlusOneAvailability;
	private int minimumFreeIssueQuantity;
	private int freeIssueQuantity;
	private boolean selected;

	public Item(int itemId, String itemCode, String itemDescription, int availableQuantity, int loadedQuantity, double wholeSalePrice, double retailSalePrice, boolean sixPlusOneAvailability, int minimumFreeIssueQuantity, int freeIssueQuantity) {
		this.itemId = itemId;
		this.itemCode = itemCode;
		this.itemDescription = itemDescription;
		this.availableQuantity = availableQuantity;
		this.loadedQuantity = loadedQuantity;
		this.wholeSalePrice = wholeSalePrice;
		this.retailSalePrice = retailSalePrice;
		this.sixPlusOneAvailability = sixPlusOneAvailability;
		this.minimumFreeIssueQuantity = minimumFreeIssueQuantity;
		this.freeIssueQuantity = freeIssueQuantity;
	}

	public static final Item parseItem(JSONObject itemJsonInstance) throws JSONException {
		if (itemJsonInstance == null) {
			return null;
		}
		return new Item(
			itemJsonInstance.getInt("iditem"),//int itemId
			itemJsonInstance.getString("itemCode"),//int itemCode
			itemJsonInstance.getString("itemName"),//itemDescription
			itemJsonInstance.getInt("sst_qty"),//avilableQuantity
			itemJsonInstance.getInt("sst_qty"),//loadedQuantity
			itemJsonInstance.getDouble("ip_whole_price"),//wholeSalePrice
			itemJsonInstance.getDouble("ip_price_visible"),//retailSalePrice
			itemJsonInstance.getBoolean("af_sixone_status"), //sixPlusOneAvailability
			itemJsonInstance.getInt("minim"),//minimumFreeIssueQuantity
			itemJsonInstance.getInt("freeq")//freeIssueQuantity
		);
	}

	public int getItemId() {
		return itemId;
	}

	public void setItemId(int itemId) {
		this.itemId = itemId;
	}

	public String getItemCode() {
		return itemCode;
	}

	public void setItemCode(String itemCode) {
		this.itemCode = itemCode;
	}

	public String getItemDescription() {
		return itemDescription;
	}

	public void setItemDescription(String itemDescription) {
		this.itemDescription = itemDescription;
	}

	public int getAvailableQuantity() {
		return availableQuantity;
	}

	public void setAvailableQuantity(int availableQuantity) {
		this.availableQuantity = availableQuantity;
	}

	public int getLoadedQuantity() {
		return loadedQuantity;
	}

	public void setLoadedQuantity(int loadedQuantity) {
		this.loadedQuantity = loadedQuantity;
	}

	public double getWholeSalePrice() {
		return wholeSalePrice;
	}

	public void setWholeSalePrice(double wholeSalePrice) {
		this.wholeSalePrice = wholeSalePrice;
	}

	public double getRetailSalePrice() {
		return retailSalePrice;
	}

	public void setRetailSalePrice(double retailSalePrice) {
		this.retailSalePrice = retailSalePrice;
	}

	public boolean isSixPlusOneAvailability() {
		return sixPlusOneAvailability;
	}

	public void setSixPlusOneAvailability(boolean sixPlusOneAvailability) {
		this.sixPlusOneAvailability = sixPlusOneAvailability;
	}

	public int getMinimumFreeIssueQuantity() {
		return minimumFreeIssueQuantity;
	}

	public void setMinimumFreeIssueQuantity(int minimumFreeIssueQuantity) {
		this.minimumFreeIssueQuantity = minimumFreeIssueQuantity;
	}

	public int getFreeIssueQuantity() {
		return freeIssueQuantity;
	}

	public void setFreeIssueQuantity(int freeIssueQuantity) {
		this.freeIssueQuantity = freeIssueQuantity;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	@Override
	public String toString() {
		return itemDescription;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		Item item = (Item) o;
		return (itemId == item.itemId);
	}

	@Override
	public int hashCode() {
		return itemId;
	}
}
