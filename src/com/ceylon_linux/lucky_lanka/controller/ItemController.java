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
import com.ceylon_linux.lucky_lanka.db.DbHandler;
import com.ceylon_linux.lucky_lanka.db.SQLiteDatabaseHelper;
import com.ceylon_linux.lucky_lanka.model.Category;
import com.ceylon_linux.lucky_lanka.model.Item;
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
public class ItemController extends AbstractController {

	private ItemController() {
	}

	public static void downloadItems(Context context, int positionId) throws IOException, JSONException {
		JSONArray categoryJson = getJsonArray(CategoryURLPack.GET_ITEMS_AND_CATEGORIES, CategoryURLPack.getParameters(positionId), context);
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

	private static void saveCategoriesToDb(ArrayList<Category> categories, Context context) {
		SQLiteDatabaseHelper databaseHelper = SQLiteDatabaseHelper.getDatabaseInstance(context);
		SQLiteDatabase database = databaseHelper.getWritableDatabase();
		try {
			database.beginTransaction();
			String categorySql = "replace into tbl_category(categoryId,categoryDescription) values (?,?)";
			String itemSql = "replace into tbl_item(itemId,categoryId,itemCode,itemDescription,wholeSalePrice,retailPrice,availableQuantity,loadedQuantity,sixPlusOneAvailability,minimumFreeIssueQuantity,freeIssueQuantity) values (?,?,?,?,?,?,?,?,?,?,?)";
			for (Category category : categories) {
				Object[] categoryParameters = {
					category.getCategoryId(),
					category.getCategoryDescription()
				};
				DbHandler.performExecuteInsert(database, categorySql, categoryParameters);
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
						item.getFreeIssueQuantity()
					};
					DbHandler.performExecuteInsert(database, itemSql, itemParameters);
				}
			}
			database.setTransactionSuccessful();
		} catch (SQLException ex) {
			Logger.getLogger(ItemController.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
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
		String itemSql = "select itemId,itemCode,itemDescription,availableQuantity,loadedQuantity,wholeSalePrice,retailPrice,sixPlusOneAvailability,minimumFreeIssueQuantity,freeIssueQuantity from tbl_item where categoryId=?";
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
					itemCursor.getInt(9)
				));
			}
			itemCursor.close();
			categories.add(new Category(categoryId, categoryDescription, items));
		}
		categoryCursor.close();
		databaseHelper.close();
		return categories;
	}

}
