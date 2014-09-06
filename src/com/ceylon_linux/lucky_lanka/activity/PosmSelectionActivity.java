/*
 * Intellectual properties of Supun Lakshan Wanigarathna Dissanayake
 * Copyright (c) 2014, Supun Lakshan Wanigarathna Dissanayake. All rights reserved.
 * Created on : Aug 27, 2014, 1:44:20 PM
 */
package com.ceylon_linux.lucky_lanka.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.ceylon_linux.lucky_lanka.R;
import com.ceylon_linux.lucky_lanka.controller.ItemController;
import com.ceylon_linux.lucky_lanka.model.Order;
import com.ceylon_linux.lucky_lanka.model.Outlet;
import com.ceylon_linux.lucky_lanka.model.PosmDetail;
import com.ceylon_linux.lucky_lanka.model.PosmItem;

import java.util.ArrayList;

/**
 * @author Supun Lakshan Wanigarathna Dissanayake
 * @mobile +94711290392
 * @email supunlakshan.xfinity@gmail.com
 */
public class PosmSelectionActivity extends Activity {

	private final byte POSM_ITEM_INSERT = 0;

	private ListView listView;
	private Button btnBack;
	private Button btnNext;
	private BaseAdapter adapter;
	private Order order;
	private Outlet outlet;
	private ArrayList<PosmDetail> posmDetails;
	private ArrayList<PosmItem> posmItems;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.posm_selection_page);
		initialize();
	}

	private void initialize() {
		listView = (ListView) findViewById(R.id.listView);
		btnBack = (Button) findViewById(R.id.btnBack);
		btnBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
		btnNext = (Button) findViewById(R.id.btnNext);
		btnNext.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				btnNextClicked(v);
			}
		});
		order = (Order) getIntent().getSerializableExtra("order");
		outlet = (Outlet) getIntent().getSerializableExtra("outlet");
		posmItems = ItemController.getPosmItems(PosmSelectionActivity.this);
		posmDetails = order.getPosmDetails();
		adapter = new BaseAdapter() {

			@Override
			public int getCount() {
				return posmItems.size();
			}

			@Override
			public PosmItem getItem(int position) {
				return posmItems.get(position);
			}

			@Override
			public long getItemId(int position) {
				return position;
			}

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				ViewHolder viewHolder;
				if (convertView == null) {
					viewHolder = new ViewHolder();
					LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
					convertView = inflater.inflate(R.layout.posm_item, null);
					viewHolder.txtPosmDescription = (TextView) convertView.findViewById(R.id.txtPosmDescription);
					viewHolder.txtQuantity = (TextView) convertView.findViewById(R.id.txtQuantity);
					convertView.setTag(viewHolder);
				} else {
					viewHolder = (ViewHolder) convertView.getTag();
				}
				PosmItem posmItem = getItem(position);
				viewHolder.txtPosmDescription.setText(posmItem.getPosmDescription());
				viewHolder.txtQuantity.setText(posmItem.getQuantity() + "");
				for (PosmDetail posmDetail : posmDetails) {
					if (posmDetail.getPosmDetailId() == posmItem.getPosmDetailId()) {
						viewHolder.txtQuantity.setText(posmDetail.getQuantity() + "");
						break;
					}
				}
				return convertView;
			}
		};
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				listViewItemClicked(parent, view, position, id);
			}
		});
	}

	private void listViewItemClicked(AdapterView<?> parent, View view, int position, long id) {
		PosmItem posmItem = (PosmItem) parent.getAdapter().getItem(position);
		TextView txtQuantity = (TextView) view.findViewById(R.id.txtQuantity);
		Intent enterPosmDetailActivity = new Intent(PosmSelectionActivity.this, EnterPosmDetailActivity.class);
		enterPosmDetailActivity.putExtra("posm", posmItem);
		String requestedQuantityString = txtQuantity.getText().toString();
		enterPosmDetailActivity.putExtra("quantity", requestedQuantityString.isEmpty() ? 0 : Integer.parseInt(requestedQuantityString));
		startActivityForResult(enterPosmDetailActivity, POSM_ITEM_INSERT);
	}

	@Override
	public void onBackPressed() {
		Intent selectItemActivity = new Intent(PosmSelectionActivity.this, SelectItemActivity.class);
		selectItemActivity.putExtra("order", order);
		selectItemActivity.putExtra("outlet", outlet);
		startActivity(selectItemActivity);
		finish();
	}

	private void btnNextClicked(View view) {
		Intent paymentActivity = new Intent(PosmSelectionActivity.this, PaymentActivity.class);
		paymentActivity.putExtra("order", order);
		paymentActivity.putExtra("outlet", outlet);
		startActivity(paymentActivity);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			case POSM_ITEM_INSERT:
				if (resultCode == RESULT_OK) {
					PosmDetail posmDetail = (PosmDetail) data.getSerializableExtra("posm");
					posmDetails.add(posmDetail);
				}
				adapter.notifyDataSetChanged();
				break;
		}
	}

	private class ViewHolder {
		private TextView txtPosmDescription;
		private TextView txtQuantity;
	}
}
