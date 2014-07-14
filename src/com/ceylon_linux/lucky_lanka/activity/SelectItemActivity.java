/*
 * Intellectual properties of Supun Lakshan Wanigarathna Dissanayake
 * Copyright (c) 2014, Supun Lakshan Wanigarathna Dissanayake. All rights reserved.
 * Created on : Jun 10, 2014, 12:20:23 PM
 */

package com.ceylon_linux.lucky_lanka.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.ceylon_linux.lucky_lanka.R;
import com.ceylon_linux.lucky_lanka.controller.ItemController;
import com.ceylon_linux.lucky_lanka.controller.UserController;
import com.ceylon_linux.lucky_lanka.model.*;
import com.ceylon_linux.lucky_lanka.util.BatteryUtility;
import com.ceylon_linux.lucky_lanka.util.GpsReceiver;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

/**
 * @author Supun Lakshan Wanigarathna Dissanayake
 * @mobile +94711290392
 * @email supunlakshan.xfinity@gmail.com
 */
public class SelectItemActivity extends Activity {

	private final int ENTER_ITEM_DETAILS = 0;
	private ExpandableListView itemList;
	private Button finishButton;
	private Outlet outlet;
	private ArrayList<Category> categories;
	private ArrayList<OrderDetail> orderDetails;
	private volatile Item item;
	private volatile int groupPosition;
	private Location location;
	private GpsReceiver gpsReceiver;
	private Thread GPS_RECEIVER;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.select_items_page);
		initialize();
		Intent intent = getIntent();
		if (intent.hasExtra("order")) {
			Order order = (Order) intent.getSerializableExtra("order");
			orderDetails = order.getOrderDetails();
		}
		outlet = (Outlet) intent.getExtras().get("outlet");
		try {
			categories = ItemController.loadItemsFromDb(this);
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (JSONException ex) {
			ex.printStackTrace();
		}

		itemList.setAdapter(new BaseExpandableListAdapter() {
			@Override
			public int getGroupCount() {
				return categories.size();
			}

			@Override
			public int getChildrenCount(int groupPosition) {
				return categories.get(groupPosition).getItems().size();
			}

			@Override
			public Category getGroup(int groupPosition) {
				return categories.get(groupPosition);
			}

			@Override
			public Item getChild(int groupPosition, int childPosition) {
				return categories.get(groupPosition).getItems().get(childPosition);
			}

			@Override
			public long getGroupId(int groupPosition) {
				return groupPosition;
			}

			@Override
			public long getChildId(int groupPosition, int childPosition) {
				return childPosition;
			}

			@Override
			public boolean hasStableIds() {
				return false;
			}

			@Override
			public View getGroupView(int groupPosition, boolean b, View view, ViewGroup viewGroup) {
				GroupViewHolder groupViewHolder;
				if (view == null) {
					LayoutInflater layoutInflater = (LayoutInflater) SelectItemActivity.this.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
					view = layoutInflater.inflate(R.layout.category_item_view, null);
					groupViewHolder = new GroupViewHolder();
					groupViewHolder.txtCategory = (TextView) view.findViewById(R.id.txtCategory);
					view.setTag(groupViewHolder);
				} else {
					groupViewHolder = (GroupViewHolder) view.getTag();
				}
				groupViewHolder.txtCategory.setText(getGroup(groupPosition).getCategoryDescription());
				return view;
			}

			@Override
			public View getChildView(int groupPosition, int childPosition, boolean b, View view, ViewGroup viewGroup) {
				ChildViewHolder childViewHolder;
				if (view == null) {
					LayoutInflater layoutInflater = (LayoutInflater) SelectItemActivity.this.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
					view = layoutInflater.inflate(R.layout.category_sub_item, null);
					childViewHolder = new ChildViewHolder();
					childViewHolder.txtItemDescription = (TextView) view.findViewById(R.id.txtItemDescription);
					childViewHolder.txtEachDiscount = (TextView) view.findViewById(R.id.txtEachDiscount);
					childViewHolder.txtFreeIssue = (TextView) view.findViewById(R.id.txtFreeIssue);
					childViewHolder.txtEachDiscount = (TextView) view.findViewById(R.id.txtEachDiscount);
					childViewHolder.checkBox = (CheckBox) view.findViewById(R.id.checkBox);
					childViewHolder.txtQuantity = (TextView) view.findViewById(R.id.txtQuantity);
					childViewHolder.txtReturnQuantity = (TextView) view.findViewById(R.id.txtReturnQuantity);
					childViewHolder.txtReplaceQuantity = (TextView) view.findViewById(R.id.txtReplaceQuantity);
					childViewHolder.txtSampleQuantity = (TextView) view.findViewById(R.id.txtSampleQuantity);
					view.setTag(childViewHolder);
				} else {
					childViewHolder = (ChildViewHolder) view.getTag();
				}
				Item item = getChild(groupPosition, childPosition);
				childViewHolder.txtItemDescription.setText(item.getItemDescription());
				childViewHolder.txtEachDiscount.setText(Double.toString(outlet.getOutletDiscount()));
				childViewHolder.checkBox.setChecked(item.isSelected());
				view.setBackgroundColor((childPosition % 2 == 0) ? Color.parseColor("#E6E6E6") : Color.parseColor("#FFFFFF"));
				updateView(childViewHolder, item);
				return view;
			}

			@Override
			public boolean isChildSelectable(int groupPosition, int childPosition) {
				return true;
			}
		});
		itemList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
			@Override
			public boolean onChildClick(ExpandableListView expandableListView, View view, int groupPosition, int childPosition, long id) {
				return itemListOnChildClicked(expandableListView, view, groupPosition, childPosition, id);
			}
		});
		finishButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				finishButtonClicked(view);
			}
		});
		finishButton.setEnabled(false);
		gpsReceiver = GpsReceiver.getGpsReceiver(SelectItemActivity.this);
		GPS_RECEIVER = new Thread() {
			private Handler handler = new Handler();

			@Override
			public void run() {
				do {
					location = gpsReceiver.getLastKnownLocation();
				} while (location == null);
				handler.post(new Runnable() {
					@Override
					public void run() {
						Toast.makeText(SelectItemActivity.this, "GPS Location Received", Toast.LENGTH_LONG).show();
						finishButton.setEnabled(true);
					}
				});
			}
		};
		GPS_RECEIVER.start();
	}

	private boolean itemListOnChildClicked(ExpandableListView expandableListView, View view, final int groupPosition, int childPosition, long id) {
		this.item = categories.get(groupPosition).getItems().get(childPosition);
		Intent enterItemDetailsActivity = new Intent(SelectItemActivity.this, EnterItemDetailsActivity.class);
		enterItemDetailsActivity.putExtra("item", item);
		enterItemDetailsActivity.putExtra("outlet", outlet);
		startActivityForResult(enterItemDetailsActivity, ENTER_ITEM_DETAILS);
		this.groupPosition = groupPosition;
		return true;
	}

	// <editor-fold defaultstate="collapsed" desc="Initialize">
	private void initialize() {
		itemList = (ExpandableListView) findViewById(R.id.itemList);
		finishButton = (Button) findViewById(R.id.finishButton);
		orderDetails = new ArrayList<OrderDetail>();
	}
	// </editor-fold>

	private void finishButtonClicked(View view) {
		if (orderDetails.size() == 0) {
			final AlertDialog.Builder alert = new AlertDialog.Builder(SelectItemActivity.this);
			alert.setTitle(R.string.app_name);
			alert.setMessage("Please select at least one item");
			alert.setPositiveButton("Ok", null);
			alert.show();
			return;
		}
		if ((location = gpsReceiver.getLastKnownLocation()) == null) {
			Thread.State state = GPS_RECEIVER.getState();
			if (state == Thread.State.TERMINATED) {
				finishButton.setEnabled(false);
				GPS_RECEIVER.start();
			}
			Toast.makeText(SelectItemActivity.this, "Please wait for GPS Location", Toast.LENGTH_LONG).show();
			return;
		}
		Order order = new Order(
			outlet.getOutletId(),
			UserController.getAuthorizedUser(SelectItemActivity.this).getPositionId(),
			outlet.getRouteId(),
			BatteryUtility.getBatteryLevel(SelectItemActivity.this),
			new Date().getTime(),
			location.getLongitude(),
			location.getLatitude(),
			orderDetails
		);
		Intent paymentActivity = new Intent(SelectItemActivity.this, PaymentActivity.class);
		paymentActivity.putExtra("order", order);
		paymentActivity.putExtra("outlet", outlet);
		startActivity(paymentActivity);
		finish();
	}

	@Override
	public void onBackPressed() {
		Intent loadAddInvoiceActivity = new Intent(SelectItemActivity.this, LoadAddInvoiceActivity.class);
		startActivity(loadAddInvoiceActivity);
		finish();
	}

	private ChildViewHolder updateView(ChildViewHolder childViewHolder, Item item) {
		for (OrderDetail orderDetail : orderDetails) {
			if (orderDetail.getItemId() == item.getItemId()) {
				childViewHolder.txtFreeIssue.setText(Integer.toString(orderDetail.getFreeIssue()));
				childViewHolder.txtQuantity.setText(Integer.toString(orderDetail.getQuantity()));
				childViewHolder.txtReturnQuantity.setText(Integer.toString(orderDetail.getReturnQuantity()));
				childViewHolder.txtReplaceQuantity.setText(Integer.toString(orderDetail.getReplaceQuantity()));
				childViewHolder.txtSampleQuantity.setText(Integer.toString(orderDetail.getSampleQuantity()));
				return childViewHolder;
			}
		}
		childViewHolder.txtFreeIssue.setText("0");
		childViewHolder.txtQuantity.setText("0");
		childViewHolder.txtReturnQuantity.setText("0");
		childViewHolder.txtReplaceQuantity.setText("0");
		childViewHolder.txtSampleQuantity.setText("0");
		return childViewHolder;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == ENTER_ITEM_DETAILS && resultCode == RESULT_OK) {
			OrderDetail orderDetail = (OrderDetail) data.getSerializableExtra("orderDetail");
			int index;
			if ((index = orderDetails.indexOf(orderDetail)) != -1) {
				orderDetails.set(index, orderDetail);
			} else {
				orderDetails.add(orderDetail);
				item.setSelected(true);
			}
			itemList.collapseGroup(groupPosition);
			itemList.expandGroup(groupPosition);
		}
	}

	private static class GroupViewHolder {

		TextView txtCategory;
	}

	private static class ChildViewHolder {

		TextView txtItemDescription;
		CheckBox checkBox;
		TextView txtQuantity;
		TextView txtFreeIssue;
		TextView txtEachDiscount;
		TextView txtReturnQuantity;
		TextView txtReplaceQuantity;
		TextView txtSampleQuantity;
	}
}
