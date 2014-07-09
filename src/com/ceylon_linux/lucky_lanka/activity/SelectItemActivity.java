/*
 * Intellectual properties of Supun Lakshan Wanigarathna Dissanayake
 * Copyright (c) 2014, Supun Lakshan Wanigarathna Dissanayake. All rights reserved.
 * Created on : Jun 10, 2014, 12:20:23 PM
 */

package com.ceylon_linux.lucky_lanka.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.ceylon_linux.lucky_lanka.R;
import com.ceylon_linux.lucky_lanka.controller.ItemController;
import com.ceylon_linux.lucky_lanka.controller.OrderController;
import com.ceylon_linux.lucky_lanka.controller.UserController;
import com.ceylon_linux.lucky_lanka.model.*;
import com.ceylon_linux.lucky_lanka.util.BatteryUtility;
import com.ceylon_linux.lucky_lanka.util.ProgressDialogGenerator;
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

	private ExpandableListView itemList;
	private Button finishButton;
	private Outlet outlet;
	private ArrayList<Category> categories;
	private ArrayList<OrderDetail> orderDetails;
	private volatile Item item;
	private volatile int groupPosition;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.select_items_page);
		initialize();
		outlet = (Outlet) getIntent().getExtras().get("outlet");
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
	}

	private boolean itemListOnChildClicked(ExpandableListView expandableListView, View view, final int groupPosition, int childPosition, long id) {
		this.item = categories.get(groupPosition).getItems().get(childPosition);
		Intent enterItemDetailsActivity = new Intent(SelectItemActivity.this, EnterItemDetailsActivity.class);
		enterItemDetailsActivity.putExtra("item", item);
		enterItemDetailsActivity.putExtra("outlet", outlet);
		startActivityForResult(enterItemDetailsActivity, RESULT_OK);
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
		final Order order = new Order(outlet.getOutletId(), UserController.getAuthorizedUser(SelectItemActivity.this).getPositionId(), outlet.getRouteId(), BatteryUtility.getBatteryLevel(SelectItemActivity.this), new Date().getTime(), 80, 6, orderDetails);
		new AsyncTask<Order, Void, Boolean>() {
			ProgressDialog progressDialog;

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				progressDialog = ProgressDialogGenerator.generateProgressDialog(SelectItemActivity.this, "Sync Order", false);
				progressDialog.show();
			}

			@Override
			protected Boolean doInBackground(Order... params) {
				Order order = params[0];
				try {
					return OrderController.syncOrder(SelectItemActivity.this, order.getOrderAsJson());
				} catch (IOException e) {
					e.printStackTrace();
				} catch (JSONException e) {
					e.printStackTrace();
				}
				return false;
			}

			@Override
			protected void onPostExecute(Boolean aBoolean) {
				if (progressDialog != null && progressDialog.isShowing()) {
					progressDialog.dismiss();
				}
				if (aBoolean) {
					Toast.makeText(SelectItemActivity.this, "Order Synced Successfully", Toast.LENGTH_LONG).show();
				} else {
					OrderController.saveOrderToDb(SelectItemActivity.this, order);
					Toast.makeText(SelectItemActivity.this, "Order placed in local database", Toast.LENGTH_LONG).show();
				}
				Intent paymentActivity = new Intent(SelectItemActivity.this, PaymentActivity.class);
				startActivity(paymentActivity);
				finish();
			}
		}.execute(order);
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
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			OrderDetail orderDetail = (OrderDetail) data.getSerializableExtra("orderDetail");
			if (!orderDetails.contains(orderDetail)) {
				orderDetails.add(orderDetail);
				item.setSelected(true);
				itemList.collapseGroup(groupPosition);
				itemList.expandGroup(groupPosition);
			}
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
