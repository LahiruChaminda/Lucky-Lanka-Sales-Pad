/*
 * Intellectual properties of Supun Lakshan Wanigarathna Dissanayake
 * Copyright (c) 2014, Supun Lakshan Wanigarathna Dissanayake. All rights reserved.
 * Created on : Jun 13, 2014, 3:46:01 PM
 */

package com.ceylon_linux.lucky_lanka.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author Supun Lakshan Wanigarathna Dissanayake
 * @mobile +94711290392
 * @email supunlakshan.xfinity@gmail.com
 */
public class Route implements Serializable {

	private int routeId;
	private String routeName;
	private ArrayList<Outlet> outlets;

	public Route(int routeId, String routeName) {
		this.routeId = routeId;
		this.routeName = routeName;
	}

	public Route(int routeId, String routeName, ArrayList<Outlet> outlets) {
		this.setRouteId(routeId);
		this.setRouteName(routeName);
		this.setOutlets(outlets);
	}

	public static final Route parseRoute(JSONObject routeJsonInstance) throws JSONException {
		ArrayList<Outlet> outlets = new ArrayList<Outlet>();
		JSONArray outletCollection = routeJsonInstance.getJSONArray("outlets");
		final int OUTLET_COUNT = outletCollection.length();
		for (int i = 0; i < OUTLET_COUNT; i++) {
			outlets.add(Outlet.parseOutlet(outletCollection.getJSONObject(i)));
		}
		return new Route(
			routeJsonInstance.getInt("areaId"),
			routeJsonInstance.getString("area"),
			outlets
		);
	}

	public int getRouteId() {
		return routeId;
	}

	public void setRouteId(int routeId) {
		this.routeId = routeId;
	}

	public String getRouteName() {
		return routeName;
	}

	public void setRouteName(String routeName) {
		this.routeName = routeName;
	}

	public ArrayList<Outlet> getOutlets() {
		return outlets;
	}

	public void setOutlets(ArrayList<Outlet> outlets) {
		this.outlets = outlets;
	}

	@Override
	public String toString() {
		return routeName;
	}
}
