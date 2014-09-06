/*
 * Intellectual properties of Supun Lakshan Wanigarathna Dissanayake
 * Copyright (c) 2014, Supun Lakshan Wanigarathna Dissanayake. All rights reserved.
 * Created on : Jun 10, 2014, 7:26:46 PM
 */

package com.ceylon_linux.lucky_lanka.model;

import android.content.Context;
import com.ceylon_linux.lucky_lanka.controller.OutletController;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;

/**
 * @author Supun Lakshan Wanigarathna Dissanayake
 * @mobile +94711290392
 * @email supunlakshan.xfinity@gmail.com
 */
public class Outlet implements Serializable {

	public static final int SUPER_MARKET = 1;
	public static final int PHARMACY = 2;
	public static final int RETAIL_OUTLET = 3;
	public static final int CANTEEN = 4;
	public static final int BAKER = 5;
	public static final int HOTEL = 6;
	public static final int WELFARE_SHOPS = 7;
	public static final int WHOLESALE_OUTLET = 8;
	public static final int OTHER = 9;
	public static final int STORES = 10;
	public static final int SPECIAL_DISCOUNT_WITH_FREE = 11;
	public static final int SPECIAL_DISCOUNT_WITHOUT_FREE = 12;

	private int outletId;
	private int routeId;
	private String outletName;
	private String outletAddress;
	private int outletType;
	private double outletDiscount;
	private ArrayList<Invoice> invoices;

	public Outlet(int outletId, int routeId, String outletName, String outletAddress, int outletType, double outletDiscount) {
		this.outletId = outletId;
		this.routeId = routeId;
		this.outletName = outletName;
		this.outletAddress = outletAddress;
		this.outletType = outletType;
		this.outletDiscount = outletDiscount;
	}

	private Outlet(int outletId, int routeId, String outletName, String outletAddress, int outletType, double outletDiscount, ArrayList<Invoice> invoices) {
		this.outletId = outletId;
		this.routeId = routeId;
		this.outletName = outletName;
		this.outletAddress = outletAddress;
		this.outletType = outletType;
		this.outletDiscount = outletDiscount;
		this.invoices = invoices;
	}

	public final static Outlet parseOutlet(JSONObject outletJsonInstance) throws JSONException {
		if (outletJsonInstance == null) {
			return null;
		}
		ArrayList<Invoice> invoices = new ArrayList<Invoice>();
		JSONArray invoicesJson = outletJsonInstance.getJSONArray("invs");
		for (int i = 0; i < invoicesJson.length(); i++) {
			try {
				invoices.add(Invoice.parseInvoice(invoicesJson.getJSONObject(i)));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return new Outlet(
			outletJsonInstance.getInt("outletId"),
			outletJsonInstance.getInt("routeId"),
			outletJsonInstance.getString("outlet"),
			outletJsonInstance.getString("o_address"),
			outletJsonInstance.getInt("idoutlet_category"),
			outletJsonInstance.getDouble("dis_pre"),
			invoices
		);
	}

	public int getOutletId() {
		return outletId;
	}

	public void setOutletId(int outletId) {
		this.outletId = outletId;
	}

	public String getOutletName() {
		return outletName;
	}

	public void setOutletName(String outletName) {
		this.outletName = outletName;
	}

	public String getOutletAddress() {
		return outletAddress;
	}

	public void setOutletAddress(String outletAddress) {
		this.outletAddress = outletAddress;
	}

	public int getOutletType() {
		return outletType;
	}

	public void setOutletType(int outletType) {
		this.outletType = outletType;
	}

	public double getOutletDiscount() {
		return outletDiscount;
	}

	public void setOutletDiscount(double outletDiscount) {
		this.outletDiscount = outletDiscount;
	}

	public int getRouteId() {
		return routeId;
	}

	public void setRouteId(int routeId) {
		this.routeId = routeId;
	}

	public ArrayList<Invoice> getInvoices(Context context) {
		return invoices = OutletController.loadInvoicesFromDb(context, this.outletId);
	}

	public void setInvoices(ArrayList<Invoice> invoices) {
		this.invoices = invoices;
	}

	@Override
	public String toString() {
		return outletName;
	}
}
