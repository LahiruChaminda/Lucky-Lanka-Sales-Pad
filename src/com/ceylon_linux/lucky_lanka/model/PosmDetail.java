/*
 * Intellectual properties of Supun Lakshan Wanigarathna Dissanayake
 * Copyright (c) 2014, Supun Lakshan Wanigarathna Dissanayake. All rights reserved.
 * Created on : Aug 27, 2014, 1:57:47 PM
 */
package com.ceylon_linux.lucky_lanka.model;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.HashMap;

/**
 * @author Supun Lakshan Wanigarathna Dissanayake
 * @mobile +94711290392
 * @email supunlakshan.xfinity@gmail.com
 */
public class PosmDetail implements Serializable {
	private int posmOrderDetailId;
	private int posmDetailId;
	private String posmDescription;
	private int quantity;

	public PosmDetail(int posmDetailId, String posmDescription, int quantity) {
		this.quantity = quantity;
		this.posmDescription = posmDescription;
		this.posmDetailId = posmDetailId;
	}

	public PosmDetail(int posmOrderDetailId, int posmDetailId, String posmDescription, int quantity) {
		this.posmOrderDetailId = posmOrderDetailId;
		this.posmDetailId = posmDetailId;
		this.posmDescription = posmDescription;
		this.quantity = quantity;
	}

	public int getPosmOrderDetailId() {
		return posmOrderDetailId;
	}

	public void setPosmOrderDetailId(int posmOrderDetailId) {
		this.posmOrderDetailId = posmOrderDetailId;
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

	public JSONObject getPosmDetailsAsJson() {
		HashMap<String, Object> jsonParams = new HashMap<String, Object>();
		jsonParams.put("id_posm_item", posmDetailId);
		jsonParams.put("posm_qty", quantity);
		return new JSONObject(jsonParams);
	}
}
