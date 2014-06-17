/*
 * Intellectual properties of Supun Lakshan Wanigarathna Dissanayake
 * Copyright (c) 2014, Supun Lakshan Wanigarathna Dissanayake. All rights reserved.
 * Created on : Jun 13, 2014, 2:34:21 PM
 */

package com.ceylon_linux.lucky_lanka.controller;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import com.ceylon_linux.lucky_lanka.db.DbHandler;
import com.ceylon_linux.lucky_lanka.db.SQLiteDatabaseHelper;
import com.ceylon_linux.lucky_lanka.model.Outlet;
import com.ceylon_linux.lucky_lanka.model.Route;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Supun Lakshan Wanigarathna Dissanayake
 * @mobile +94711290392
 * @email supunlakshan.xfinity@gmail.com
 */
public class OutletController extends AbstractController {

	private OutletController() {
	}

	public static void downloadOutlets(Context context, int positionId) throws IOException, JSONException {
		JSONArray routeJson = getJsonArray(OutletURLPack.GET_OUTLETS, OutletURLPack.getParameters(positionId), context);
		ArrayList<Route> routes = new ArrayList<Route>();
		final int ROUTE_LENGTH = routeJson.length();
		for (int i = 0; i < ROUTE_LENGTH; i++) {
			routes.add(Route.parseRoute(routeJson.getJSONObject(i)));
		}
		saveOutletsToDb(routes, context);
	}

	private static void saveOutletsToDb(ArrayList<Route> routes, Context context) {
		SQLiteDatabaseHelper databaseHelper = SQLiteDatabaseHelper.getDatabaseInstance(context);
		SQLiteDatabase database = databaseHelper.getWritableDatabase();
		String routeSql = "replace into tbl_route(routeId, routeName) values (?,?)";
		String outletSql = "replace into tbl_outlet(outletId, routeId, outletName, outletAddress, outletType, outletDiscount) values (?,?,?,?,?,?)";
		try {
			database.beginTransaction();
			for (Route route : routes) {
				DbHandler.performExecuteInsert(database, routeSql, new Object[]{
					route.getRouteId(),
					route.getRouteName()
				});
				for (Outlet outlet : route.getOutlets()) {
					DbHandler.performExecuteInsert(database, outletSql, new Object[]{
						outlet.getOutletId(),
						outlet.getRouteId(),
						outlet.getOutletName(),
						outlet.getOutletAddress(),
						outlet.getOutletType(),
						outlet.getOutletDiscount()
					});
				}
			}
			database.setTransactionSuccessful();
		} catch (SQLException ex) {
			Logger.getLogger(Outlet.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
		} finally {
			database.endTransaction();
			databaseHelper.close();
		}
	}

	public static ArrayList<Route> loadRoutesFromDb(Context context) {
		SQLiteDatabaseHelper databaseHelper = SQLiteDatabaseHelper.getDatabaseInstance(context);
		SQLiteDatabase database = databaseHelper.getWritableDatabase();
		String routeQuery = "select routeId, routeName from tbl_route";
		String outletSql = "select outletId, routeId, outletName, outletAddress, outletType, outletDiscount from tbl_outlet where routeId=?";
		Cursor routeCursor = DbHandler.performRawQuery(database, routeQuery, null);
		ArrayList<Route> routes = new ArrayList<Route>();
		for (routeCursor.moveToFirst(); !routeCursor.isAfterLast(); routeCursor.moveToNext()) {
			ArrayList<Outlet> outlets = new ArrayList<Outlet>();
			int routeId = routeCursor.getInt(0);
			String routeName = routeCursor.getString(1);
			Cursor outletCursor = DbHandler.performRawQuery(database, outletSql, new Object[]{routeId});
			for (outletCursor.moveToFirst(); !outletCursor.isAfterLast(); outletCursor.moveToNext()) {
				outlets.add(new Outlet(
					outletCursor.getInt(0),
					outletCursor.getInt(1),
					outletCursor.getString(2),
					outletCursor.getString(3),
					outletCursor.getInt(4),
					outletCursor.getDouble(5)
				));
			}
			outletCursor.close();
			routes.add(new Route(routeId, routeName, outlets));
		}
		routeCursor.close();
		databaseHelper.close();
		return routes;
	}
	/*

	public static ArrayList<Route> loadRoutesFromDb(Context context) {
		SQLiteDatabaseHelper databaseHelper = SQLiteDatabaseHelper.getDatabaseInstance(context);
		SQLiteDatabase database = databaseHelper.getWritableDatabase();
		String routeQuery = "select routeId, routeName from tbl_route";
		Cursor routeCursor = DbHandler.performRawQuery(database, routeQuery, null);
		ArrayList<Route> routes = new ArrayList<Route>();
		for (routeCursor.moveToFirst(); !routeCursor.isAfterLast(); routeCursor.moveToNext()) {
			int routeId = routeCursor.getInt(0);
			String routeName = routeCursor.getString(1);
			routes.add(new Route(routeId, routeName));
		}
		routeCursor.close();
		databaseHelper.close();
		return routes;
	}

	public static ArrayList<Outlet> loadOutletsFromDb(Context context) {
		SQLiteDatabaseHelper databaseHelper = SQLiteDatabaseHelper.getDatabaseInstance(context);
		SQLiteDatabase database = databaseHelper.getWritableDatabase();
		String outletSql = "select outletId, routeId, outletName, outletAddress, outletType, outletDiscount from tbl_outlet";
		ArrayList<Outlet> outlets = new ArrayList<Outlet>();
		Cursor outletCursor = DbHandler.performRawQuery(database, outletSql, null);
		for (outletCursor.moveToFirst(); !outletCursor.isAfterLast(); outletCursor.moveToNext()) {
			outlets.add(new Outlet(
				outletCursor.getInt(0),
				outletCursor.getInt(1),
				outletCursor.getString(2),
				outletCursor.getString(3),
				outletCursor.getInt(4),
				outletCursor.getDouble(5)
			));
		}
		outletCursor.close();
		databaseHelper.close();
		return outlets;
	}
	*/
}
