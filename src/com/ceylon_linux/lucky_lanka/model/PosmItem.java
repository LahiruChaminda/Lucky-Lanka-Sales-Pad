/*
 * Intellectual properties of Supun Lakshan Wanigarathna Dissanayake
 * Copyright (c) 2014, Supun Lakshan Wanigarathna Dissanayake. All rights reserved.
 * Created on : Aug 28, 2014, 9:23:16 AM
 */
package com.ceylon_linux.lucky_lanka.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * @author Supun Lakshan Wanigarathna Dissanayake
 * @mobile +94711290392
 * @email supunlakshan.xfinity@gmail.com
 */
public class PosmItem implements Serializable {
	private int posmDetailId;
	private String posmDescription;
	private int quantity;

	public PosmItem(int posmDetailId, String posmDescription, int quantity) {
		this.posmDetailId = posmDetailId;
		this.posmDescription = posmDescription;
		this.quantity = quantity;
	}

	public static PosmItem parsePosmItem(JSONObject posmJsonInstance) throws JSONException {
		if (posmJsonInstance == null) {
			return null;
		}
		return new PosmItem(
			posmJsonInstance.getInt("id_posm_item"),
			posmJsonInstance.getString("item_name"),
			0
		);
	}

	public int getPosmDetailId() {
		return posmDetailId;
	}

	public void setPosmDetailId(int posmDetailId) {
		this.posmDetailId = posmDetailId;
	}

	public String getPosmDescription() {
		return posmDescription;
	}

	public void setPosmDescription(String posmDescription) {
		this.posmDescription = posmDescription;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
}

