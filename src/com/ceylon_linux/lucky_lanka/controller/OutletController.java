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
import android.database.sqlite.SQLiteStatement;
import com.ceylon_linux.lucky_lanka.db.DbHandler;
import com.ceylon_linux.lucky_lanka.db.SQLiteDatabaseHelper;
import com.ceylon_linux.lucky_lanka.model.Invoice;
import com.ceylon_linux.lucky_lanka.model.Outlet;
import com.ceylon_linux.lucky_lanka.model.Payment;
import com.ceylon_linux.lucky_lanka.model.Route;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.text.SimpleDateFormat;
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
		String invoiceDeleteSql = "delete from tbl_invoice";
		String invoiceInsertSql = "insert into tbl_invoice( invoiceId, outletId, invoiceDate, amount) values(?,?,?,?)";
		String paymentInsertSql = "insert into tbl_payment(invoiceId, paymentDate, amount, chequeDate, chequeNo, status) values(?,?,?,?,?,?)";

		SQLiteStatement routeInsertSqlStatement = database.compileStatement(routeSql);
		SQLiteStatement outletInsertSqlStatement = database.compileStatement(outletSql);
		SQLiteStatement invoiceInsertSqlStatement = database.compileStatement(invoiceInsertSql);
		SQLiteStatement paymentInsertSqlStatement = database.compileStatement(paymentInsertSql);
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		try {
			database.beginTransaction();
			DbHandler.performExecute(database, invoiceDeleteSql, null);
			for (Route route : routes) {
				DbHandler.performExecuteInsert(routeInsertSqlStatement, new Object[]{
					route.getRouteId(),
					route.getRouteName()
				});
				for (Outlet outlet : route.getOutlets()) {
					DbHandler.performExecuteInsert(outletInsertSqlStatement, new Object[]{
						outlet.getOutletId(),
						outlet.getRouteId(),
						outlet.getOutletName(),
						outlet.getOutletAddress(),
						outlet.getOutletType(),
						outlet.getOutletDiscount()
					});
					for (Invoice invoice : outlet.getInvoices()) {
						DbHandler.performExecuteInsert(invoiceInsertSqlStatement, new Object[]{
							invoice.getInvoiceId(),
							outlet.getOutletId(),
							simpleDateFormat.format(invoice.getDate()),
							invoice.getAmount()
						});
						for (Payment payment : invoice.getPayments()) {
							DbHandler.performExecuteInsert(paymentInsertSqlStatement, new Object[]{
								invoice.getInvoiceId(),
								simpleDateFormat.format(payment.getPaymentDate()),
								payment.getAmount(),
								simpleDateFormat.format(payment.getChequeDate()),
								payment.getChequeNo(),
								payment.isSynced() ? 1 : 0
							});
						}
					}
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

	public static boolean addPayment(long invoiceId, Context context, Payment... payments) {
		return false;
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
}
