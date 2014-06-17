/*
 * Intellectual properties of Supun Lakshan Wanigarathna Dissanayake
 * Copyright (c) 2014, Supun Lakshan Wanigarathna Dissanayake. All rights reserved.
 * Created on : Jun 10, 2014, 7:26:46 PM
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
public class Outlet implements Serializable {

	public static final int NORMAL_OUTLET = 3;
	public static final int SIX_PLUS_ONE_OUTLET = 0;
	public static final int SUPER_MARKET = 1;
	private int outletId;
	private int routeId;
	private String outletName;
	private String outletAddress;
	private int outletType;
	private double outletDiscount;

	public Outlet(int outletId, int routeId, String outletName, String outletAddress, int outletType, double outletDiscount) {
		this.outletId = outletId;
		this.routeId = routeId;
		this.outletName = outletName;
		this.outletAddress = outletAddress;
		this.outletType = outletType;
		this.outletDiscount = outletDiscount;
	}

	public final static Outlet parseOutlet(JSONObject outletJsonInstance) throws JSONException {
		if (outletJsonInstance == null) {
			return null;
		}
		int outletType = 0;
		switch (outletJsonInstance.getInt("plus")) {
			case 0:
				outletType = Outlet.SIX_PLUS_ONE_OUTLET;
				break;
			case 1:
				if (outletJsonInstance.getInt("idoutlet_category") == Outlet.SUPER_MARKET) {
					outletType = Outlet.SUPER_MARKET;
				} else {
					outletType = Outlet.NORMAL_OUTLET;
				}
				break;
		}
		return new Outlet(
			outletJsonInstance.getInt("outletId"),
			outletJsonInstance.getInt("routeId"),
			outletJsonInstance.getString("outlet"),
			outletJsonInstance.getString("o_address"),
			outletType,
			outletJsonInstance.getDouble("oltdis")
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

	@Override
	public String toString() {
		return outletName;
	}
}
