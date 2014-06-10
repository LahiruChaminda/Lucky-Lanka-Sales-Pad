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
	private boolean freeIssueAvailability;

	public Item(int itemId, String itemDescription, int availableQuantity, int loadedQuantity, double retailSalePrice, double wholeSalePrice, boolean freeIssueAvailability) {
		this.setItemId(itemId);
		this.setItemDescription(itemDescription);
		this.setAvailableQuantity(availableQuantity);
		this.setLoadedQuantity(loadedQuantity);
		this.setRetailSalePrice(retailSalePrice);
		this.setWholeSalePrice(wholeSalePrice);
		this.setFreeIssueAvailability(freeIssueAvailability);
	}

	public Item(int itemId, String itemCode, String itemDescription, int loadedQuantity, int availableQuantity, double wholeSalePrice, double retailSalePrice, boolean freeIssueAvailability) {
		this.setItemId(itemId);
		this.setItemCode(itemCode);
		this.setItemDescription(itemDescription);
		this.setLoadedQuantity(loadedQuantity);
		this.setAvailableQuantity(availableQuantity);
		this.setWholeSalePrice(wholeSalePrice);
		this.setRetailSalePrice(retailSalePrice);
		this.setFreeIssueAvailability(freeIssueAvailability);
	}

	public static final Item parseItem(JSONObject itemJsonInstance) throws JSONException {
		if (itemJsonInstance == null) {
			return null;
		}
		return new Item(
				itemJsonInstance.getInt("itemId"),
				itemJsonInstance.getString("itemCode"),
				itemJsonInstance.getString("itemDescription"),
				itemJsonInstance.getInt("availableQuantity"),
				itemJsonInstance.getInt("loadedQuantity"),
				itemJsonInstance.getDouble("wholeSalePrice"),
				itemJsonInstance.getDouble("retailSalePrice"),
				itemJsonInstance.getBoolean("freeIssueAvailability")
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

	public boolean isFreeIssueAvailability() {
		return freeIssueAvailability;
	}

	public void setFreeIssueAvailability(boolean freeIssueAvailability) {
		this.freeIssueAvailability = freeIssueAvailability;
	}
}
