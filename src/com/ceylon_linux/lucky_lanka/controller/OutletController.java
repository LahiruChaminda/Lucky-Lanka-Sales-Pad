/*
 * Intellectual properties of Supun Lakshan Wanigarathna Dissanayake
 * Copyright (c) 2014, Supun Lakshan Wanigarathna Dissanayake. All rights reserved.
 * Created on : Jun 13, 2014, 2:34:21 PM
 */

package com.ceylon_linux.lucky_lanka.controller;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;
import com.ceylon_linux.lucky_lanka.db.DbHandler;
import com.ceylon_linux.lucky_lanka.db.SQLiteDatabaseHelper;
import com.ceylon_linux.lucky_lanka.model.Invoice;
import com.ceylon_linux.lucky_lanka.model.Outlet;
import com.ceylon_linux.lucky_lanka.model.Payment;
import com.ceylon_linux.lucky_lanka.model.Route;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

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
		String invoiceDeleteSql = "delete from tbl_invoice";

		SQLiteStatement routeInsertSqlStatement = database.compileStatement("replace into tbl_route(routeId, routeName) values (?,?)");
		SQLiteStatement outletInsertSqlStatement = database.compileStatement("replace into tbl_outlet(outletId, routeId, outletName, outletAddress, outletType, outletDiscount) values (?,?,?,?,?,?)");
		SQLiteStatement invoiceInsertSqlStatement = database.compileStatement("insert into tbl_invoice( invoiceId, outletId, invoiceDate, amount) values(?,?,?,?)");
		SQLiteStatement paymentInsertSqlStatement = database.compileStatement("insert or ignore into tbl_payment(invoiceId, paymentDate, amount, chequeDate, chequeNo, status) values(?,?,?,?,?,?)");
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
					for (Invoice invoice : outlet.getInvoices(context)) {
						DbHandler.performExecuteInsert(invoiceInsertSqlStatement, new Object[]{
							invoice.getInvoiceId(),
							outlet.getOutletId(),
							invoice.getDate().getTime(),
							invoice.getAmount()
						});
						for (Payment payment : invoice.getPayments()) {
							DbHandler.performExecuteInsert(paymentInsertSqlStatement, new Object[]{
								invoice.getInvoiceId(),
								(payment.getPaymentDate() != null) ? payment.getPaymentDate().getTime() : 0,
								payment.getAmount(),
								(payment.getChequeDate() != null) ? payment.getChequeDate().getTime() : 0,
								payment.getChequeNo(),
								payment.isSynced() ? 1 : 0
							});
						}
					}
				}
			}
			database.setTransactionSuccessful();
		} catch (Exception ex) {
			Log.e("ADDED", ex.getMessage(), ex);
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

	public static ArrayList<Invoice> loadInvoicesFromDb(Context context, int outletId) {
		SQLiteDatabaseHelper databaseHelper = SQLiteDatabaseHelper.getDatabaseInstance(context);
		SQLiteDatabase database = databaseHelper.getWritableDatabase();
		String invoiceSql = "select invoiceId, invoiceDate, amount from tbl_invoice where outletId=?";
		String paymentsSql = "select paymentId, paymentDate, amount, chequeDate, chequeNo, status, bank from tbl_payment where invoiceId=?";
		ArrayList<Invoice> invoices = new ArrayList<Invoice>();
		Cursor invoiceCursor = DbHandler.performRawQuery(database, invoiceSql, new Object[]{outletId});
		for (invoiceCursor.moveToFirst(); !invoiceCursor.isAfterLast(); invoiceCursor.moveToNext()) {
			int invoiceId = invoiceCursor.getInt(0);
			long date = invoiceCursor.getLong(1);
			double amount = invoiceCursor.getDouble(2);
			ArrayList<Payment> payments = new ArrayList<Payment>();
			Cursor paymentCursor = DbHandler.performRawQuery(database, paymentsSql, new Object[]{invoiceId});
			for (paymentCursor.moveToFirst(); !paymentCursor.isAfterLast(); paymentCursor.moveToNext()) {
				payments.add(new Payment(
					paymentCursor.getInt(0),
					new Date(paymentCursor.getLong(1)),
					paymentCursor.getDouble(2),
					new Date(paymentCursor.getLong(3)),
					paymentCursor.getString(4),
					paymentCursor.getString(6),
					paymentCursor.getInt(5) == 0
				));
			}
			invoices.add(new Invoice(invoiceId, date, amount, payments));
		}
		invoiceCursor.close();
		databaseHelper.close();
		return invoices;
	}

	public static ArrayList<Outlet> loadOutletsFromDb(Context context) {
		SQLiteDatabaseHelper databaseHelper = SQLiteDatabaseHelper.getDatabaseInstance(context);
		SQLiteDatabase database = databaseHelper.getWritableDatabase();
		String outletSql = "select outletId, routeId, outletName, outletAddress, outletType, outletDiscount from tbl_outlet where routeId=?";
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

	public static boolean syncOutstandingPayments(Context context, Invoice invoice) throws IOException, JSONException {
		JSONObject responseJson = getJsonObject(PaymentURLPack.PAYMENT_SYNC, PaymentURLPack.getParameters(invoice.getInvoiceAsJson(), UserController.getAuthorizedUser(context).getPositionId()), context);
		return (responseJson != null) && responseJson.getBoolean("result");
	}

	public static void saveOutstandingPayments(Context context, Invoice invoice) {
		SQLiteDatabaseHelper databaseHelper = SQLiteDatabaseHelper.getDatabaseInstance(context);
		SQLiteDatabase database = databaseHelper.getWritableDatabase();
		String paymentInsertSql = "insert into tbl_payment(invoiceId, paymentDate,amount, chequeDate, chequeNo, bank, status) values (?,?,?,?,?,?,0);";
		SQLiteStatement paymentInsertStatement = database.compileStatement(paymentInsertSql);
		for (Payment payment : invoice.getPayments()) {
			DbHandler.performExecuteInsert(paymentInsertStatement, new Object[]{
				invoice.getInvoiceId(),
				(payment.getPaymentDate() != null) ? payment.getPaymentDate().getTime() : 0,
				payment.getAmount(),
				(payment.getChequeDate() != null) ? payment.getChequeDate().getTime() : 0,
				payment.getChequeNo()
			});
		}
		database.close();
	}
}
