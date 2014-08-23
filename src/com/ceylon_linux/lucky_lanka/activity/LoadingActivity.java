/*
 * Intellectual properties of Supun Lakshan Wanigarathna Dissanayake
 * Copyright (c) 2014, Supun Lakshan Wanigarathna Dissanayake. All rights reserved.
 * Created on : Aug 13, 2014, 11:43:24 AM
 */
package com.ceylon_linux.lucky_lanka.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
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

	public static final byte CONFIRM_LOADING = 0;
	public static final byte VIEW_CURRENT_STOCK = 1;

	private ArrayList<Item> availableStock = new ArrayList<Item>();
	private BaseAdapter adapter;
	private ListView listView;
	private Button btnLoadingConfirm;
	private Button btnOption;

	private byte option;

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
						viewHolder.txtAvailableQuantity = (TextView) convertView.findViewById(R.id.txtAvailableQuantity);
					} else {
						viewHolder = (ViewHolder) convertView.getTag();
					}
					viewHolder.txtItemDescription.setText(getItem(position).getItemDescription());
					viewHolder.txtQuantity.setText(getItem(position).getLoadedQuantity() + "");
					viewHolder.txtAvailableQuantity.setText(getItem(position).getAvailableQuantity() + "");
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

	@Override
	public void onBackPressed() {
		Intent homeActivity = new Intent(LoadingActivity.this, HomeActivity.class);
		startActivity(homeActivity);
		finish();
	}

	private void initialize() {
		option = (Byte) getIntent().getSerializableExtra("option");
		listView = (ListView) findViewById(R.id.listView);
		btnLoadingConfirm = (Button) findViewById(R.id.btnLoadingConfirm);
		btnLoadingConfirm.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				btnLoadingConfirmClicked(v);
			}
		});
		btnOption = (Button) findViewById(R.id.btnOption);
		btnOption.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				btnOptionClicked(v);
			}
		});
		btnLoadingConfirm.setVisibility((option == VIEW_CURRENT_STOCK) ? View.INVISIBLE : View.VISIBLE);
		btnOption.setText((option == VIEW_CURRENT_STOCK) ? "Ok" : "Cancel");
	}

	private void btnOptionClicked(View view) {
		switch (option) {
			case CONFIRM_LOADING:
				//should act as cancel button
				UserController.clearAuthentication(LoadingActivity.this);
				finish();
				System.exit(0);
				break;
			case VIEW_CURRENT_STOCK:
				//should act as ok button
				onBackPressed();
				break;
		}
	}

	private void btnLoadingConfirmClicked(View view) {
		new Thread() {
			private Handler handler = new Handler();
			private int confirmLoadingResponse;

			@Override
			public void run() {
				try {
					confirmLoadingResponse = ItemController.confirmLoading(LoadingActivity.this, UserController.getAuthorizedUser(LoadingActivity.this).getPositionId());
					handler.post(new Runnable() {
						@Override
						public void run() {
							if (confirmLoadingResponse == ItemController.LOADING_CONFIRMED) {
								boolean response = UserController.confirmLoading(LoadingActivity.this);
								if (response) {
									Intent homeActivity = new Intent(LoadingActivity.this, HomeActivity.class);
									startActivity(homeActivity);
									finish();
								}
								Toast.makeText(LoadingActivity.this, "Loading Confirmed", Toast.LENGTH_LONG).show();
							} else {
								Toast.makeText(LoadingActivity.this, "Unable to Confirm Loading", Toast.LENGTH_LONG).show();
							}
						}
					});
				} catch (IOException e) {
					e.printStackTrace();
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}.start();
	}

	private static class ViewHolder {
		TextView txtItemDescription;
		TextView txtQuantity;
		TextView txtAvailableQuantity;
	}
}
