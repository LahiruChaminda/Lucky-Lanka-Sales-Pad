/*
 * Intellectual properties of Supun Lakshan Wanigarathna Dissanayake
 * Copyright (c) 2014, Supun Lakshan Wanigarathna Dissanayake. All rights reserved.
 * Created on : Jun 10, 2014, 9:42:22 AM
 */

package com.ceylon_linux.lucky_lanka.controller;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import com.ceylon_linux.lucky_lanka.db.DbHandler;
import com.ceylon_linux.lucky_lanka.db.SQLiteDatabaseHelper;
import com.ceylon_linux.lucky_lanka.model.Category;
import com.ceylon_linux.lucky_lanka.model.Item;
import com.ceylon_linux.lucky_lanka.model.PosmItem;
import com.ceylon_linux.lucky_lanka.model.UnloadingItem;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * @author Supun Lakshan Wanigarathna Dissanayake
 * @mobile +94711290392
 * @email supunlakshan.xfinity@gmail.com
 */
public class ItemController extends AbstractController {

	public static final byte UNLOADING_CONFIRMED = 0;
	public static final byte UNABLE_TO_CONFIRM_UNLOADING = 1;
	public static final byte LOADING_CONFIRMED = 2;
	public static final byte UNABLE_TO_CONFIRM_LOADING = 3;

	private ItemController() {
	}

	public static void downloadItems(Context context, int positionId, int sessionId) throws IOException, JSONException {
		JSONArray categoryJson = getJsonArray(CategoryURLPack.GET_ITEMS_AND_CATEGORIES, CategoryURLPack.getParameters(positionId, sessionId), context);
		ArrayList<Category> categories = new ArrayList<Category>();
		final int CATEGORY_LENGTH = categoryJson.length();
		for (int i = 0; i < CATEGORY_LENGTH; i++) {
			Category category = Category.parseCategory(categoryJson.getJSONObject(i));
			if (category != null) {
				categories.add(category);
			}
		}
		saveCategoriesToDb(categories, context);
	}

	public static void downloadPosmItems(Context context, int positionId, int sessionId) throws IOException, JSONException {
		JSONArray posmJson = getJsonArray(CategoryURLPack.POSM_DETAILS, CategoryURLPack.getPOSMParameters(positionId, sessionId), context);
		ArrayList<PosmItem> posmItems = new ArrayList<PosmItem>();
		for (int i = 0, POSM_LENGTH = posmJson.length(); i < POSM_LENGTH; i++) {
			PosmItem posmItem;
			if ((posmItem = PosmItem.parsePosmItem(posmJson.getJSONObject(i))) != null) {
				posmItems.add(posmItem);
			}
		}
		savePosmToDb(posmItems, context);
	}

	public static void downloadAndSaveFreeIssueCalculationData(Context context, int positionId, int sessionId) throws IOException, JSONException {
		JSONObject freeCalculationDataJson = getJsonObject(CategoryURLPack.FREE_ISSUE_CALCULATION_DETAILS, CategoryURLPack.getFreeIssueCalculationParameters(positionId, sessionId), context);
		SQLiteDatabaseHelper databaseInstance = SQLiteDatabaseHelper.getDatabaseInstance(context);
		SQLiteDatabase database = databaseInstance.getWritableDatabase();

		SQLiteStatement tbl_assort_item_issue_Statement = database.compileStatement("insert into tbl_assort_item_issue(tbl_assort_free_idassort_free, tbl_item_iditem, idassort_item_issue, afi_qty) values(?,?,?,?)");
		SQLiteStatement tbl_assort_free_Statement = database.compileStatement("insert into tbl_assort_free(idassort_free, af_date, af_time, af_status, af_type, af_qty, af_sixone_status, free_user_type) values(?,?,?,?,?,?,?,?)");
		SQLiteStatement tbl_assort_item_Statement = database.compileStatement("insert into tbl_assort_item(tbl_item_iditem, idassort_free, idassort_item) values(?,?,?)");

		JSONArray tbl_assort_item_issue = freeCalculationDataJson.getJSONArray("tbl_assort_item_issue");
		for (int i = 0, tbl_assort_item_issues_length = tbl_assort_item_issue.length(); i < tbl_assort_item_issues_length; i++) {
			JSONObject json_instance = tbl_assort_item_issue.getJSONObject(i);
			DbHandler.performExecuteInsert(tbl_assort_item_issue_Statement, new Object[]{
				json_instance.getInt("tbl_assort_free_idassort_free"),
				json_instance.getInt("tbl_item_iditem"),
				json_instance.getInt("idassort_item_issue"),
				json_instance.getInt("afi_qty")
			});
		}

		JSONArray tbl_assort_free = freeCalculationDataJson.getJSONArray("tbl_assort_free");
		for (int i = 0, tbl_assort_item_issues_length = tbl_assort_free.length(); i < tbl_assort_item_issues_length; i++) {
			JSONObject json_instance = tbl_assort_free.getJSONObject(i);
			DbHandler.performExecuteInsert(tbl_assort_free_Statement, new Object[]{
				json_instance.getInt("idassort_free"),
				json_instance.getString("af_date"),
				json_instance.getString("af_time"),
				json_instance.getInt("af_status"),
				json_instance.getString("af_type"),
				json_instance.getInt("af_qty"),
				json_instance.getInt("af_sixone_status"),
				json_instance.getInt("free_user_type")
			});
		}

		JSONArray tbl_assort_item = freeCalculationDataJson.getJSONArray("tbl_assort_item");
		for (int i = 0, tbl_assort_item_issues_length = tbl_assort_item.length(); i < tbl_assort_item_issues_length; i++) {
			JSONObject json_instance = tbl_assort_item.getJSONObject(i);
			DbHandler.performExecuteInsert(tbl_assort_item_Statement, new Object[]{
				json_instance.getInt("tbl_item_iditem"),
				json_instance.getInt("idassort_free"),
				json_instance.getInt("idassort_item")
			});
		}

		databaseInstance.close();
	}

	private static void savePosmToDb(ArrayList<PosmItem> posmItems, Context context) {
		SQLiteDatabaseHelper databaseInstance = SQLiteDatabaseHelper.getDatabaseInstance(context);
		SQLiteDatabase database = databaseInstance.getWritableDatabase();
		SQLiteStatement statement = database.compileStatement("insert into tbl_posm_detail (posmDetailId,posmDescription,quantity) values (?,?,?)");
		try {
			for (PosmItem posmItem : posmItems) {
				DbHandler.performExecute(statement, new Object[]{
					posmItem.getPosmDetailId(),
					posmItem.getPosmDescription(),
					posmItem.getQuantity()
				});
			}
		} finally {
			databaseInstance.close();
		}
	}

	private static void saveCategoriesToDb(ArrayList<Category> categories, Context context) {
		SQLiteDatabaseHelper databaseHelper = SQLiteDatabaseHelper.getDatabaseInstance(context);
		SQLiteDatabase database = databaseHelper.getWritableDatabase();
		try {
			database.beginTransaction();
			SQLiteStatement categoryStatement = database.compileStatement("replace into tbl_category(categoryId,categoryDescription) values (?,?)");
			SQLiteStatement itemStatement = database.compileStatement("replace into tbl_item(itemId,categoryId,itemCode,itemDescription,wholeSalePrice,retailPrice,availableQuantity,loadedQuantity,sixPlusOneAvailability,minimumFreeIssueQuantity,freeIssueQuantity,itemShortName, freeIssueItemId) values (?,?,?,?,?,?,?,?,?,?,?,?,?)");
			for (Category category : categories) {
				Object[] categoryParameters = {
					category.getCategoryId(),
					category.getCategoryDescription()
				};
				DbHandler.performExecuteInsert(categoryStatement, categoryParameters);
				for (Item item : category.getItems()) {
					Object[] itemParameters = {
						item.getItemId(),
						category.getCategoryId(),
						item.getItemCode(),
						item.getItemDescription(),
						item.getWholeSalePrice(),
						item.getRetailSalePrice(),
						item.getAvailableQuantity(),
						item.getLoadedQuantity(),
						(item.isSixPlusOneAvailability()) ? 1 : 0,
						item.getMinimumFreeIssueQuantity(),
						item.getFreeIssueQuantity(),
						item.getItemShortName(),
						item.getFreeItemId()
					};
					DbHandler.performExecuteInsert(itemStatement, itemParameters);
				}
			}
			database.setTransactionSuccessful();
		} catch (SQLException ex) {
			ex.printStackTrace();
		} finally {
			database.endTransaction();
			databaseHelper.close();
		}
	}

	public static ArrayList<Category> loadItemsFromDb(Context context) throws IOException, JSONException {
		SQLiteDatabaseHelper databaseHelper = SQLiteDatabaseHelper.getDatabaseInstance(context);
		SQLiteDatabase database = databaseHelper.getWritableDatabase();
		ArrayList<Category> categories = new ArrayList<Category>();
		String categorySql = "select categoryId,categoryDescription from tbl_category";
		String itemSql = "select itemId,itemCode,itemDescription,availableQuantity,loadedQuantity,wholeSalePrice,retailPrice,sixPlusOneAvailability,minimumFreeIssueQuantity,freeIssueQuantity,itemShortName, freeIssueItemId from tbl_item where categoryId=? group by itemId";
		Cursor categoryCursor = DbHandler.performRawQuery(database, categorySql, null);
		for (categoryCursor.moveToFirst(); !categoryCursor.isAfterLast(); categoryCursor.moveToNext()) {
			int categoryId = categoryCursor.getInt(0);
			String categoryDescription = categoryCursor.getString(1);
			ArrayList<Item> items = new ArrayList<Item>();
			Cursor itemCursor = DbHandler.performRawQuery(database, itemSql, new Object[]{categoryId});
			for (itemCursor.moveToFirst(); !itemCursor.isAfterLast(); itemCursor.moveToNext()) {
				items.add(new Item(
					itemCursor.getInt(0),
					itemCursor.getString(1),
					itemCursor.getString(2),
					itemCursor.getInt(3),
					itemCursor.getInt(4),
					itemCursor.getDouble(5),
					itemCursor.getDouble(6),
					(itemCursor.getInt(7) == 1),
					itemCursor.getInt(8),
					itemCursor.getInt(9),
					itemCursor.getString(10),
					itemCursor.getInt(11)
				));
			}
			itemCursor.close();
			categories.add(new Category(categoryId, categoryDescription, items));
		}
		categoryCursor.close();
		databaseHelper.close();
		return categories;
	}

	public static ArrayList<UnloadingItem> getUnLoadingStock(Context context) throws IOException, JSONException {
		SQLiteDatabaseHelper databaseHelper = SQLiteDatabaseHelper.getDatabaseInstance(context);
		SQLiteDatabase database = databaseHelper.getWritableDatabase();
		String itemSql = "select itemId,availableQuantity,wholeSalePrice from tbl_item where availableQuantity>0";
		ArrayList<UnloadingItem> unloadingItems = new ArrayList<UnloadingItem>();
		Cursor itemCursor = DbHandler.performRawQuery(database, itemSql, null);
		for (itemCursor.moveToFirst(); !itemCursor.isAfterLast(); itemCursor.moveToNext()) {
			unloadingItems.add(new UnloadingItem(
				itemCursor.getInt(0),
				itemCursor.getInt(1),
				itemCursor.getDouble(2)
			));
		}
		itemCursor.close();
		databaseHelper.close();
		return unloadingItems;
	}

	public static int syncUnloading(Context context, JSONArray unloadingStock, int positionId, int sessionId) throws IOException, JSONException {
		System.out.println(unloadingStock);
		JSONObject responseJson = getJsonObject(CategoryURLPack.CONFIRM_UNLOADING, CategoryURLPack.getUnloadingParameters(positionId, unloadingStock, sessionId), context);
		return responseJson != null && responseJson.getInt("result") == 1 ? UNLOADING_CONFIRMED : UNABLE_TO_CONFIRM_UNLOADING;
	}

	public static int confirmLoading(Context context, int positionId, int sessionId) throws IOException, JSONException {
		JSONObject jsonResponse = getJsonObject(CategoryURLPack.CONFIRM_LOADING, CategoryURLPack.getLoadingConfirmParameters(positionId, sessionId), context);
		if (jsonResponse != null && jsonResponse.getInt("result") == 1) {
			return LOADING_CONFIRMED;
		} else {
			return UNABLE_TO_CONFIRM_LOADING;
		}
	}

	public static synchronized int getFreeIssue(int itemId, int quantity, boolean sixPlusOneOutlet, Context context) {
		SQLiteDatabaseHelper databaseInstance = SQLiteDatabaseHelper.getDatabaseInstance(context);
		SQLiteDatabase database = databaseInstance.getWritableDatabase();
		String query;
		if (sixPlusOneOutlet) {
			query = "select freeIssueQuantity * cast( ? / minimumFreeIssueQuantity as int) as freeIssue from tbl_item where itemId=? and minimumFreeIssueQuantity<=? order by minimumFreeIssueQuantity desc limit 1";
		} else {
			query = "select freeIssueQuantity * cast( ? / minimumFreeIssueQuantity as int) as freeIssue from tbl_item where itemId=? and minimumFreeIssueQuantity<=? and sixPlusOneAvailability=0 order by minimumFreeIssueQuantity desc limit 1";
		}
		Cursor cursor = DbHandler.performRawQuery(database, query, new Object[]{quantity, itemId, quantity});
		for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
			return cursor.getInt(0);
		}
		return 0;
	}

	public static ArrayList<PosmItem> getPosmItems(Context context) {
		SQLiteDatabaseHelper databaseHelper = SQLiteDatabaseHelper.getDatabaseInstance(context);
		SQLiteDatabase database = databaseHelper.getWritableDatabase();
		String posmSql = "select posmDetailId,posmDescription,quantity from tbl_posm_detail";
		ArrayList<PosmItem> posmItems = new ArrayList<PosmItem>();
		Cursor posmCursor = DbHandler.performRawQuery(database, posmSql, null);
		for (posmCursor.moveToFirst(); !posmCursor.isAfterLast(); posmCursor.moveToNext()) {
			posmItems.add(new PosmItem(
				posmCursor.getInt(0),
				posmCursor.getString(1),
				posmCursor.getInt(2)
			));
		}
		posmCursor.close();
		databaseHelper.close();
		return posmItems;
	}

	public static HashMap<Integer, Integer> getAssociativeFreeIssue(Context context, String itemIds, HashMap<Integer, Integer> data) {
		String sql = "SELECT tai.idassort_free, tai.tbl_item_iditem, taf.af_qty FROM (select distinct stai.idassort_free from tbl_assort_item as stai inner join tbl_assort_free staf ON stai.idassort_free = staf.idassort_free where stai.tbl_item_iditem in (?) nd staf.af_status = 1 and staf.af_sixone_status = 0 and staf.af_type = 'assort') tmp inner join tbl_assort_free taf ON tmp.idassort_free = taf.idassort_free inner join tbl_assort_item tai ON taf.idassort_free = tai.idassort_free order by tai.idassort_free";
		SQLiteDatabaseHelper databaseInstance = SQLiteDatabaseHelper.getDatabaseInstance(context);
		SQLiteDatabase database = databaseInstance.getWritableDatabase();
		Cursor cursor = DbHandler.performRawQuery(database, sql, new Object[]{itemIds});
		HashSet<Assort> assortSet = new HashSet<Assort>();
		ArrayList<Assort> assortArrayList = null;
		for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
			Assort assort = new Assort();
			assort.idassort_free = cursor.getInt(0);
			assort.af_qty = cursor.getInt(2);
			assortArrayList = new ArrayList<Assort>(assortSet);
			assortSet.add(assort);
			Assort oldAssort = assortArrayList.get(assortArrayList.indexOf(assort));
			oldAssort.tbl_item_iditem.add(cursor.getInt(1));
		}
		cursor.close();
		ArrayList<Free> freeList = new ArrayList();
		ArrayList<AssortIssue> assortIssues = new ArrayList();
		if (assortArrayList != null) {
			for (Assort assort : assortArrayList) {
				boolean exists = true;
				double thisTurnTotal = 0;
				for (Integer integer : assort.tbl_item_iditem) {
					if (data.containsKey(integer)) {
						thisTurnTotal += data.get(integer);
					} else {
						exists = false;
						break;
					}
				}
				if (exists && thisTurnTotal >= assort.af_qty) {
					freeList.add(new Free(assort.idassort_free, (int) Math.floor(thisTurnTotal / assort.af_qty)));
				}
			}
			for (Free free : freeList) {
				Cursor cursor1 = DbHandler.performRawQuery(database, "SELECT tbl_item_iditem, afi_qty * ? as afi_qty FROM tbl_assort_item_issue tafi where tafi.idassort_item_issue = ?", new Object[]{free.multiply, free.idassort_free});
				for (cursor1.moveToFirst(); !cursor1.isAfterLast(); cursor1.moveToNext()) {
					assortIssues.add(new AssortIssue(cursor1.getInt(0), cursor1.getInt(1)));
				}
				cursor1.close();
			}
		}
		HashMap<Integer, Integer> result = new HashMap<Integer, Integer>();
		for (AssortIssue assortIssue : assortIssues) {
			result.put(assortIssue.tbl_item_iditem, assortIssue.afi_qty);
		}
		return result;
	}

	private static class AssortIssue {
		int tbl_item_iditem;
		int afi_qty;

		private AssortIssue(int tbl_item_iditem, int afi_qty) {
			this.tbl_item_iditem = tbl_item_iditem;
			this.afi_qty = afi_qty;
		}
	}

	private static class Free {
		int idassort_free;
		int multiply;

		private Free(int idassort_free, int multiply) {
			this.idassort_free = idassort_free;
			this.multiply = multiply;
		}
	}

	private static class Assort {
		int idassort_free;
		ArrayList<Integer> tbl_item_iditem;
		int af_qty;

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			Assort assort = (Assort) o;
			if (idassort_free != assort.idassort_free) return false;
			return true;
		}

		@Override
		public int hashCode() {
			return idassort_free;
		}
	}
}
