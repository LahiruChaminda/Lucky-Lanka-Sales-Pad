/*
 * Intellectual properties of Supun Lakshan Wanigarathna Dissanayake
 * Copyright (c) 2014, Supun Lakshan Wanigarathna Dissanayake. All rights reserved.
 * Created on : Jun 17, 2014, 4:35:16 PM
 */

package com.ceylon_linux.lucky_lanka.controller;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import com.ceylon_linux.lucky_lanka.db.DbHandler;
import com.ceylon_linux.lucky_lanka.db.SQLiteDatabaseHelper;
import com.ceylon_linux.lucky_lanka.model.Order;
import com.ceylon_linux.lucky_lanka.model.OrderDetail;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Supun Lakshan Wanigarathna Dissanayake
 * @mobile +94711290392
 * @email supunlakshan.xfinity@gmail.com
 */
public class OrderController extends AbstractController {

	private OrderController() {
	}

	public static boolean saveOrderToDb(Context context, Order order) {
		SQLiteDatabaseHelper databaseHelper = SQLiteDatabaseHelper.getDatabaseInstance(context);
		SQLiteDatabase database = databaseHelper.getWritableDatabase();
		try {
			database.beginTransaction();
			String orderInsertSQL = "insert into tbl_order(outletId, routeId, positionId, invoiceTime, total, batteryLevel, longitude, latitude) values(?,?,?,?,?,?,?,?)";
			String orderDetailInsertSQL = "insert into tbl_order(orderId, itemId, price, discount, quantity, freeQuantity) values(?,?,?,?,?,?)";
			long orderId = DbHandler.performExecuteInsert(database, orderInsertSQL, new Object[]{
				order.getOutletId(),
				order.getRouteId(),
				order.getPositionId(),
				order.getInvoiceTime(),
				0,
				100,
				80,
				6
			});
			SQLiteStatement orderDetailInsertStatement = database.compileStatement(orderDetailInsertSQL);
			for (OrderDetail orderDetail : order.getOrderDetails()) {
				DbHandler.performExecuteInsert(orderDetailInsertStatement, new Object[]{
					orderId,
					orderDetail.getItemId(),
					orderDetail.getPrice(),
					0,
					orderDetail.getQuantity(),
					orderDetail.getFreeIssue()
				});
			}
			database.setTransactionSuccessful();
		} catch (SQLException ex) {
			Logger.getLogger(OrderController.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
		} finally {
			database.endTransaction();
		}
		return true;
	}

	public static boolean syncOrder(Context context, JSONObject orderJson) throws IOException, JSONException {
		JSONObject responseJson = getJsonObject(OrderURLPack.INSERT_ORDER, OrderURLPack.getParameters(orderJson, UserController.getAuthorizedUser(context).getPositionId()), context);
		return responseJson.getBoolean("result");
	}

}
