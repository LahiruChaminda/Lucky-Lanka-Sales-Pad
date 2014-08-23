/*
 * Intellectual properties of Supun Lakshan Wanigarathna Dissanayake
 * Copyright (c) 2014, Supun Lakshan Wanigarathna Dissanayake. All rights reserved.
 * Created on : Aug 21, 2014, 3:34:28 PM
 */
package com.ceylon_linux.lucky_lanka.model;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * @author Supun Lakshan Wanigarathna Dissanayake
 * @mobile +94711290392
 * @email supunlakshan.xfinity@gmail.com
 */
public class UnloadingItem {
	private int itemId;
	private int quantity;
	private double price;

	public UnloadingItem(int itemId, int quantity, double price) {
		this.itemId = itemId;
		this.quantity = quantity;
		this.price = price;
	}

	public JSONObject getUnLoadingItemAsJson() {
		HashMap<String, Object> unloadingParams = new HashMap<String, Object>();
		unloadingParams.put("itemId", itemId);
		unloadingParams.put("qty", quantity);
		unloadingParams.put("price", price);
		return new JSONObject(unloadingParams);
	}
}
