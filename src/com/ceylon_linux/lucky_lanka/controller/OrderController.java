/*
 * Intellectual properties of Supun Lakshan Wanigarathna Dissanayake
 * Copyright (c) 2014, Supun Lakshan Wanigarathna Dissanayake. All rights reserved.
 * Created on : Jun 17, 2014, 4:35:16 PM
 */

package com.ceylon_linux.lucky_lanka.controller;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import com.ceylon_linux.lucky_lanka.db.DbHandler;
import com.ceylon_linux.lucky_lanka.db.SQLiteDatabaseHelper;
import com.ceylon_linux.lucky_lanka.model.Order;
import com.ceylon_linux.lucky_lanka.model.OrderDetail;
import com.ceylon_linux.lucky_lanka.model.Outlet;
import com.ceylon_linux.lucky_lanka.model.Payment;
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
public class OrderController extends AbstractController {

	public static final int ORDERS_SYNCED_SUCCESSFULLY = 0;
	public static final int UNABLE_TO_SYNC_ORDERS = 1;
	public static final int ORDERS_ALREADY_SYNCED = 2;

	private OrderController() {
	}

	public static boolean saveOrderToDb(Context context, Order order, boolean syncStatus) {
		SQLiteDatabaseHelper databaseHelper = SQLiteDatabaseHelper.getDatabaseInstance(context);
		SQLiteDatabase database = databaseHelper.getWritableDatabase();
		try {
			database.beginTransaction();
			String orderInsertSQL = "insert into tbl_order(outletId, routeId, positionId, invoiceTime, total, batteryLevel, longitude, latitude, syncStatus) values(?,?,?,?,?,?,?,?,?)";
			String orderDetailInsertSQL = "insert into tbl_order_detail(orderId, itemId, price, discount, quantity, freeQuantity, returnQuantity, replaceQuantity, sampleQuantity) values(?,?,?,?,?,?,?,?,?)";
			String paymentInsertSql = "insert into tbl_current_payment(orderId, paymentDate, amount, chequeDate, chequeNo, branchId) values (?,?,?,?,?,?)";
			double total = 0;
			for (OrderDetail orderDetail : order.getOrderDetails()) {
				total += orderDetail.getPrice() * orderDetail.getQuantity();
			}
			long orderId = DbHandler.performExecuteInsert(database, orderInsertSQL, new Object[]{
				order.getOutletId(),
				order.getRouteId(),
				order.getPositionId(),
				order.getInvoiceTime(),
				total,
				order.getBatteryLevel(),
				order.getLongitude(),
				order.getLatitude(),
				syncStatus ? 1 : 0
			});
			SQLiteStatement orderDetailInsertStatement = database.compileStatement(orderDetailInsertSQL);
			for (OrderDetail orderDetail : order.getOrderDetails()) {
				DbHandler.performExecuteInsert(orderDetailInsertStatement, new Object[]{
					orderId,
					orderDetail.getItemId(),
					orderDetail.getPrice(),
					0,
					orderDetail.getQuantity(),
					orderDetail.getFreeIssue(),
					orderDetail.getReturnQuantity(),
					orderDetail.getReplaceQuantity(),
					orderDetail.getSampleQuantity()
				});
			}
			SQLiteStatement paymentInsertStatement = database.compileStatement(paymentInsertSql);
			for (Payment payment : order.getPayments()) {
				DbHandler.performExecuteInsert(paymentInsertStatement, new Object[]{
					orderId,
					payment.getPaymentDate().getTime(),
					payment.getAmount(),
					(payment.getChequeDate() == null) ? 0 : payment.getChequeDate().getTime(),
					payment.getChequeNo() == null ? "" : payment.getChequeNo(),
					payment.getBranchCode()
				});
			}
			database.setTransactionSuccessful();
		} catch (SQLException ex) {
			ex.printStackTrace();
		} finally {
			database.endTransaction();
		}
		return true;
	}

	public static boolean syncOrder(Context context, JSONObject orderJson) throws IOException, JSONException {
		JSONObject responseJson = getJsonObject(OrderURLPack.INSERT_ORDER, OrderURLPack.getParameters(orderJson, UserController.getAuthorizedUser(context).getPositionId()), context);
		return (responseJson != null) && responseJson.getBoolean("result");
	}

	public static int syncUnSyncedOrders(Context context) throws IOException, JSONException {
		SQLiteDatabaseHelper databaseHelper = SQLiteDatabaseHelper.getDatabaseInstance(context);
		SQLiteDatabase database = databaseHelper.getWritableDatabase();
		try {
			String orderSelectSql = "select tbl_order.orderId, tbl_order.outletId, tbl_order.routeId, tbl_order.positionId, tbl_order.invoiceTime, tbl_order.total, tbl_order.batteryLevel, tbl_order.longitude, tbl_order.latitude, tbl_outlet.outletType from tbl_order inner join tbl_outlet on tbl_outlet.outletId=tbl_order.outletId where tbl_order.syncStatus=0";
			String orderDetailSelectSql = "select tbl_order_detail.itemId, tbl_order_detail.price, tbl_order_detail.discount, tbl_order_detail.quantity, tbl_order_detail.freeQuantity, tbl_item.itemDescription, tbl_order_detail.returnQuantity, tbl_order_detail.replaceQuantity, tbl_order_detail.sampleQuantity, tbl_item.itemShortName from tbl_order_detail inner join tbl_item on tbl_item.itemId=tbl_order_detail.itemId where orderId=?";
			String paymentSelectSql = "select paymentId, paymentDate, amount, chequeDate, chequeNo, branchId from tbl_current_payment where orderId=?";
			Cursor orderCursor = DbHandler.performRawQuery(database, orderSelectSql, null);
			ArrayList<Order> orders = new ArrayList<Order>();
			for (orderCursor.moveToFirst(); !orderCursor.isAfterLast(); orderCursor.moveToNext()) {
				long orderId = orderCursor.getLong(0);
				int outletId = orderCursor.getInt(1);
				int routeId = orderCursor.getInt(2);
				int positionId = orderCursor.getInt(3);
				long invoiceTime = orderCursor.getLong(4);
				double total = orderCursor.getDouble(5);
				int batteryLevel = orderCursor.getInt(6);
				double longitude = orderCursor.getDouble(7);
				double latitude = orderCursor.getDouble(8);
				int outletType = orderCursor.getInt(9);
				ArrayList<OrderDetail> orderDetails = new ArrayList<OrderDetail>();
				ArrayList<Payment> payments = new ArrayList<Payment>();

				Cursor orderDetailsCursor = DbHandler.performRawQuery(database, orderDetailSelectSql, new Object[]{orderId});
				for (orderDetailsCursor.moveToFirst(); !orderDetailsCursor.isAfterLast(); orderDetailsCursor.moveToNext()) {
					int itemId = orderDetailsCursor.getInt(0);
					double price = orderDetailsCursor.getDouble(1);
					//double discount = orderDetailsCursor.getDouble(2);
					int quantity = orderDetailsCursor.getInt(3);
					int freeQuantity = orderDetailsCursor.getInt(4);
					String itemDescription = orderDetailsCursor.getString(5);
					int returnQuantity = orderDetailsCursor.getInt(6);
					int replaceQuantity = orderDetailsCursor.getInt(7);
					int sampleQuantity = orderDetailsCursor.getInt(8);
					String itemShortName = orderDetailsCursor.getString(9);
					OrderDetail orderDetail;
					if (outletType == Outlet.SUPER_MARKET) {
						orderDetail = new OrderDetail(itemId, itemDescription, quantity, price, returnQuantity, replaceQuantity, sampleQuantity, itemShortName);
					} else {
						orderDetail = new OrderDetail(itemId, itemDescription, quantity, freeQuantity, price, returnQuantity, replaceQuantity, sampleQuantity, itemShortName);
					}
					orderDetails.add(orderDetail);
				}
				orderDetailsCursor.close();
				Order order = new Order(orderId, outletId, null, positionId, routeId, invoiceTime, longitude, latitude, batteryLevel, orderDetails);
				orders.add(order);

				Cursor paymentDetailsCursor = DbHandler.performRawQuery(database, paymentSelectSql, new Object[]{orderId});
				for (paymentDetailsCursor.moveToFirst(); !paymentDetailsCursor.isAfterLast(); paymentDetailsCursor.moveToNext()) {
					Payment payment = new Payment(
						paymentDetailsCursor.getInt(0),
						new Date(paymentDetailsCursor.getLong(1)),
						paymentDetailsCursor.getDouble(2),
						new Date(paymentDetailsCursor.getLong(3)),
						paymentDetailsCursor.getString(4),
						paymentDetailsCursor.getInt(5),
						paymentDetailsCursor.getInt(6) == 1
					);
					payments.add(payment);
				}
				order.setPayments(payments);
			}
			if (orders.size() == 0) {
				return ORDERS_ALREADY_SYNCED;
			}
			orderCursor.close();
			String updateQuery = "update tbl_order set syncStatus=1 where orderId=?";
			SQLiteStatement deleteStatement = database.compileStatement(updateQuery);
			for (Order order : orders) {
				boolean response = syncOrder(context, order.getOrderAsJson());
				if (response) {
					DbHandler.performExecuteUpdateDelete(deleteStatement, new Object[]{order.getOrderId()});
				} else {
					return UNABLE_TO_SYNC_ORDERS;
				}
			}
		} finally {
			databaseHelper.close();
		}
		return ORDERS_SYNCED_SUCCESSFULLY;
	}

	/*public static boolean syncUnSyncedInvoices(Context context) throws IOException, JSONException {
		SQLiteDatabaseHelper databaseHelper = SQLiteDatabaseHelper.getDatabaseInstance(context);
		SQLiteDatabase database = databaseHelper.getWritableDatabase();
		try {
			String invoiceSelectSql = "select tbl_order.orderId, tbl_order.outletId, tbl_order.routeId, tbl_order.positionId, tbl_order.invoiceTime, tbl_order.total, tbl_order.batteryLevel, tbl_order.longitude, tbl_order.latitude, tbl_outlet.outletType from tbl_order inner join tbl_outlet on tbl_outlet.outletId=tbl_order.outletId where tbl_order.syncStatus=0";
			String paymentSQL = "select paymentId, paymentDate, amount, chequeDate, chequeNo, branchId from tbl_payment where invoiceId=?";
			Cursor orderCursor = DbHandler.performRawQuery(database, invoiceSelectSql, null);
			ArrayList<Order> orders = new ArrayList<Order>();
			for (orderCursor.moveToFirst(); !orderCursor.isAfterLast(); orderCursor.moveToNext()) {
				long orderId = orderCursor.getLong(0);
				int outletId = orderCursor.getInt(1);
				int routeId = orderCursor.getInt(2);
				int positionId = orderCursor.getInt(3);
				long invoiceTime = orderCursor.getLong(4);
				double total = orderCursor.getDouble(5);
				int batteryLevel = orderCursor.getInt(6);
				double longitude = orderCursor.getDouble(7);
				double latitude = orderCursor.getDouble(8);
				int outletType = orderCursor.getInt(9);
				ArrayList<OrderDetail> orderDetails = new ArrayList<OrderDetail>();
				ArrayList<Payment> payments = new ArrayList<Payment>();

				Cursor orderDetailsCursor = DbHandler.performRawQuery(database, orderDetailSelectSql, new Object[]{orderId});
				for (orderDetailsCursor.moveToFirst(); !orderDetailsCursor.isAfterLast(); orderDetailsCursor.moveToNext()) {
					int itemId = orderDetailsCursor.getInt(0);
					double price = orderDetailsCursor.getDouble(1);
					//double discount = orderDetailsCursor.getDouble(2);
					int quantity = orderDetailsCursor.getInt(3);
					int freeQuantity = orderDetailsCursor.getInt(4);
					String itemDescription = orderDetailsCursor.getString(5);
					int returnQuantity = orderDetailsCursor.getInt(6);
					int replaceQuantity = orderDetailsCursor.getInt(7);
					int sampleQuantity = orderDetailsCursor.getInt(8);
					String itemShortName = orderDetailsCursor.getString(9);
					OrderDetail orderDetail;
					if (outletType == Outlet.SUPER_MARKET) {
						orderDetail = new OrderDetail(itemId, itemDescription, quantity, price, returnQuantity, replaceQuantity, sampleQuantity, itemShortName);
					} else {
						orderDetail = new OrderDetail(itemId, itemDescription, quantity, freeQuantity, price, returnQuantity, replaceQuantity, sampleQuantity, itemShortName);
					}
					orderDetails.add(orderDetail);
				}
				orderDetailsCursor.close();

				Order order = new Order(outletId, positionId, routeId, batteryLevel, invoiceTime, longitude, latitude, orderDetails);
				orders.add(order);

				Cursor paymentDetailsCursor = DbHandler.performRawQuery(database, paymentSelectSql, new Object[]{orderId});
				for (paymentDetailsCursor.moveToFirst(); !paymentDetailsCursor.isAfterLast(); paymentDetailsCursor.moveToNext()) {
					Payment payment = new Payment(
						paymentDetailsCursor.getInt(0),
						new Date(paymentDetailsCursor.getLong(1)),
						paymentDetailsCursor.getDouble(2),
						new Date(paymentDetailsCursor.getLong(3)),
						paymentDetailsCursor.getString(4),
						paymentDetailsCursor.getInt(5),
						paymentDetailsCursor.getInt(6) == 1
					);
					payments.add(payment);
				}
				order.setPayments(payments);
			}
			orderCursor.close();
			String updateQuery = "update tbl_order set syncStatus=1 where orderId=?";
			SQLiteStatement deleteStatement = database.compileStatement(updateQuery);
			for (Order order : orders) {
				boolean response = syncOrder(context, order.getOrderAsJson());
				if (response) {
					DbHandler.performExecuteUpdateDelete(deleteStatement, new Object[]{order.getOrderId()});
				} else {
					return false;
				}
			}
		} finally {
			databaseHelper.close();
		}
		return true;
	}*/
}
