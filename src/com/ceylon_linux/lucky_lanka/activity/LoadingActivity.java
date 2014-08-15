/*
 * Intellectual properties of Supun Lakshan Wanigarathna Dissanayake
 * Copyright (c) 2014, Supun Lakshan Wanigarathna Dissanayake. All rights reserved.
 * Created on : Aug 13, 2014, 11:43:24 AM
 */
package com.ceylon_linux.lucky_lanka.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import com.ceylon_linux.lucky_lanka.R;
import com.ceylon_linux.lucky_lanka.controller.ItemController;
import com.ceylon_linux.lucky_lanka.controller.UserController;
import com.ceylon_linux.lucky_lanka.model.Category;
import com.ceylon_linux.lucky_lanka.model.Item;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

/**
 * @author Supun Lakshan Wanigarathna Dissanayake
 * @mobile +94711290392
 * @email supunlakshan.xfinity@gmail.com
 */
public class LoadingActivity extends Activity {
	private ArrayList<Item> availableStock = new ArrayList<Item>();
	private BaseAdapter adapter;
	private ListView listView;
	private Button btnLoading;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.loading_page);
		initialize();
		try {
			ArrayList<Category> categories = ItemController.loadItemsFromDb(this);
			for (Category category : categories) {
				for (Item item : category.getItems()) {
					if (item.getAvailableQuantity() != 0) {
						availableStock.add(item);
					}
				}
			}
			adapter = new BaseAdapter() {
				@Override
				public int getCount() {
					return availableStock.size();
				}

				@Override
				public Item getItem(int position) {
					return availableStock.get(position);
				}

				@Override
				public long getItemId(int position) {
					return position;
				}

				@Override
				public View getView(int position, View convertView, ViewGroup parent) {
					ViewHolder viewHolder;
					if (convertView == null) {
						LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
						convertView = layoutInflater.inflate(R.layout.loaded_item, null);
						convertView.setTag(viewHolder = new ViewHolder());
						viewHolder.txtItemDescription = (TextView) convertView.findViewById(R.id.txtItemDescription);
						viewHolder.txtQuantity = (TextView) convertView.findViewById(R.id.txtQuantity);
					} else {
						viewHolder = (ViewHolder) convertView.getTag();
					}
					viewHolder.txtItemDescription.setText(getItem(position).getItemDescription());
					viewHolder.txtQuantity.setText(getItem(position).getLoadedQuantity() + "");
					return convertView;
				}
			};
			listView.setAdapter(adapter);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void initialize() {
		Boolean loading = (Boolean) getIntent().getSerializableExtra("loading");
		listView = (ListView) findViewById(R.id.listView);
		btnLoading = (Button) findViewById(R.id.btnLoading);
		btnLoading.setEnabled(!UserController.confirmLoading(LoadingActivity.this));
		btnLoading.setVisibility(loading ? View.VISIBLE : View.INVISIBLE);
		btnLoading.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				btnLoadingClicked(v);
			}
		});
	}

	private void btnLoadingClicked(View view) {
		boolean response = UserController.confirmLoading(LoadingActivity.this);
		if (response) {
			Intent homeActivity = new Intent(LoadingActivity.this, HomeActivity.class);
			startActivity(homeActivity);
			finish();
		}
	}

	private static class ViewHolder {
		TextView txtItemDescription;
		TextView txtQuantity;
	}
}
