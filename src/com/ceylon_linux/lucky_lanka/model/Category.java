/*
 * Intellectual properties of Supun Lakshan Wanigarathna Dissanayake
 * Copyright (c) 2014, Supun Lakshan Wanigarathna Dissanayake. All rights reserved.
 * Created on : Jun 10, 2014, 12:27:01 PM
 */
package com.ceylon_linux.lucky_lanka.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * @author Supun Lakshan Wanigarathna Dissanayake
 * @mobile +94711290392
 * @email supunlakshan.xfinity@gmail.com
 */
public class Category {
	private ArrayList<Item> items;
	private String categoryDescription;
	private int categoryId;

	public Category(int categoryId, String categoryDescription) {
		this.categoryDescription = categoryDescription;
		this.categoryId = categoryId;
	}

	public Category(int categoryId, String categoryDescription, ArrayList<Item> items) {
		this.items = items;
		this.categoryDescription = categoryDescription;
		this.categoryId = categoryId;
	}

	public static final Category parseCategory(JSONObject categoryJsonInstance) throws JSONException {
		if (categoryJsonInstance == null) {
			return null;
		}
		ArrayList<Item> items = new ArrayList<Item>();
		JSONArray itemCollection = categoryJsonInstance.getJSONArray("items");
		final int ITEM_COLLECTION_SIZE = itemCollection.length();
		for (int i = 0; i < ITEM_COLLECTION_SIZE; i++) {
			Item item = Item.parseItem(itemCollection.getJSONObject(i));
			if (item != null) {
				items.add(item);
			}
		}
		return (items.size() == 0) ? null : new Category(
				categoryJsonInstance.getInt("categoryId"),
				categoryJsonInstance.getString("categoryDescription"),
				items
		);
	}

	public ArrayList<Item> getItems() {
		return items;
	}

	public void setItems(ArrayList<Item> items) {
		this.items = items;
	}

	public String getCategoryDescription() {
		return categoryDescription;
	}

	public void setCategoryDescription(String categoryDescription) {
		this.categoryDescription = categoryDescription;
	}

	public int getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(int categoryId) {
		this.categoryId = categoryId;
	}
}
