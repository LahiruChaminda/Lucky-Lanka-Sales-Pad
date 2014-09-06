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
import java.io.Serializable;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * @author Supun Lakshan Wanigarathna Dissanayake
 * @mobile +94711290392
 * @email supunlakshan.xfinity@gmail.com
 */
public class SelectItemActivity extends Activity implements Serializable {

	private transient final int ENTER_ITEM_DETAILS = 0;
	private transient ExpandableListView itemList;
	private transient MyExpandableListAdapter expandableListAdapter;
	private transient Button finishButton;
	private transient Outlet outlet;
	private transient ArrayList<Category> categories;
	private transient ArrayList<OrderDetail> orderDetails;
	private transient ArrayList<OrderDetail> freeIssues;
	private transient Location location;
	private transient ArrayList<Item> availableStock = new ArrayList<Item>();
	private transient ArrayList<Item> unAvailableStock = new ArrayList<Item>();
	private transient GpsReceiver gpsReceiver;
	private transient Thread GPS_RECEIVER;
	private transient ProgressDialog progressDialog;
	private transient TextView txtInvoiceAmount;
	private transient NumberFormat currencyFormat;
	private transient Order order;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.select_items_page);
		initialize();
		Intent intent = getIntent();
		if (intent.hasExtra("order")) {
			order = (Order) intent.getSerializableExtra("order");
			orderDetails = order.getOrderDetails();
			double invoiceTotal = 0;
			for (OrderDetail instance : orderDetails) {
				invoiceTotal += instance.getPrice() * instance.getQuantity();
			}
			txtInvoiceAmount.setText("Rs " + currencyFormat.format(invoiceTotal));
		}
		outlet = (Outlet) intent.getExtras().get("outlet");
		try {
			categories = ItemController.loadItemsFromDb(this);
			for (Category category : categories) {
				for (Item item : category.getItems()) {
					if (item.getAvailableQuantity() != 0) {
						availableStock.add(item);
					} else {
						unAvailableStock.add(item);
					}
				}
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (JSONException ex) {
			ex.printStackTrace();
		}
		expandableListAdapter = new MyExpandableListAdapter();
		itemList.setAdapter(expandableListAdapter);
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
		gpsReceiver = GpsReceiver.getGpsReceiver(SelectItemActivity.this);
		GPS_RECEIVER = new Thread() {
			private Handler handler = new Handler();

			@Override
			public void run() {
				do {
					location = gpsReceiver.getLastKnownLocation();
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				} while (location == null);
				handler.post(new Runnable() {
					@Override
					public void run() {
						if (progressDialog != null && progressDialog.isShowing()) {
							progressDialog.dismiss();
						}
						Toast.makeText(SelectItemActivity.this, "GPS Location Received", Toast.LENGTH_LONG).show();
					}
				});
			}
		};
		GPS_RECEIVER.start();
	}

	private boolean itemListOnChildClicked(ExpandableListView expandableListView, View view, final int groupPosition, int childPosition, long id) {
		Item item = (groupPosition == 0) ? availableStock.get(childPosition) : unAvailableStock.get(childPosition);
		Intent enterItemDetailsActivity = new Intent(SelectItemActivity.this, EnterItemDetailsActivity.class);
		enterItemDetailsActivity.putExtra("item", item);
		enterItemDetailsActivity.putExtra("outlet", outlet);
		for (OrderDetail orderDetail : orderDetails) {
			if (orderDetail.getItemId() == item.getItemId()) {
				enterItemDetailsActivity.putExtra("orderDetail", orderDetail);
				break;
			}
		}
		startActivityForResult(enterItemDetailsActivity, ENTER_ITEM_DETAILS);
		return true;
	}

	// <editor-fold defaultstate="collapsed" desc="Initialize">
	private void initialize() {
		itemList = (ExpandableListView) findViewById(R.id.itemList);
		finishButton = (Button) findViewById(R.id.finishButton);
		txtInvoiceAmount = (TextView) findViewById(R.id.txtInvoiceAmount);
		orderDetails = new ArrayList<OrderDetail>() {
			@Override
			public boolean add(OrderDetail object) {
				int index;
				if ((index = super.indexOf(object)) != -1) {
					if (object.getQuantity() != 0) {
						super.set(index, object);
					} else {
						super.remove(index);
					}
					return true;
				}
				return super.add(object);
			}
		};

		freeIssues = new ArrayList<OrderDetail>();
		currencyFormat = NumberFormat.getInstance();
		currencyFormat.setGroupingUsed(true);
		currencyFormat.setMaximumFractionDigits(2);
		currencyFormat.setMinimumFractionDigits(2);
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
			progressDialog = ProgressDialog.show(SelectItemActivity.this, null, "Waiting For GPS...", false);
			return;
		}
		if (order == null) {
			order = new Order(
				outlet.getOutletId(),
				UserController.getAuthorizedUser(SelectItemActivity.this).getPositionId(),
				outlet.getRouteId(),
				BatteryUtility.getBatteryLevel(SelectItemActivity.this),
				new Date().getTime(),
				location.getLongitude(),
				location.getLatitude(),
				orderDetails,
				new ArrayList<PosmDetail>()
			);
		} else {
			order.setOrderDetails(orderDetails);
		}
		order.setOrderId(UserController.getInvoiceId(SelectItemActivity.this));
		Intent posmSelectionActivity = new Intent(SelectItemActivity.this, PosmSelectionActivity.class);
		posmSelectionActivity.putExtra("order", order);
		posmSelectionActivity.putExtra("outlet", outlet);
		startActivity(posmSelectionActivity);
		finish();
	}

	@Override
	public void onBackPressed() {
		Intent loadAddInvoiceActivity = new Intent(SelectItemActivity.this, LoadAddInvoiceActivity.class);
		startActivity(loadAddInvoiceActivity);
		finish();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == ENTER_ITEM_DETAILS && resultCode == RESULT_OK) {
			OrderDetail orderDetail = (OrderDetail) data.getSerializableExtra("orderDetail");
			orderDetail.setFreeIssue(0);
			orderDetails.add(orderDetail);
			calculateFreeIssues();
			expandableListAdapter.notifyDataSetChanged();
			double invoiceTotal = 0;
			for (OrderDetail instance : orderDetails) {
				invoiceTotal += instance.getPrice() * instance.getQuantity();
			}
			txtInvoiceAmount.setText("Rs " + currencyFormat.format(invoiceTotal));
		}
	}

	private void calculateFreeIssues() {
		HashMap<Integer, OrderDetail> freeIssuesMap = new HashMap<Integer, OrderDetail>() {
			@Override
			public OrderDetail put(Integer key, OrderDetail value) {
				if (super.containsKey(key)) {
					OrderDetail orderDetail = super.get(key);
					orderDetail.setFreeIssue(orderDetail.getFreeIssue() + value.getFreeIssue());
					value = orderDetail;
				}
				return super.put(key, value);
			}
		};
		for (OrderDetail instance : orderDetails) {
			OrderDetail freeIssueDetail = OrderDetail.getFreeIssueDetail(outlet, instance, instance.getQuantity(), SelectItemActivity.this);
			freeIssuesMap.put(instance.getItemId(), freeIssueDetail);
		}
		freeIssues.clear();
		freeIssues.addAll(freeIssuesMap.values());
	}

	private static class GroupViewHolder {

		TextView txtCategory;
	}

	private static class ChildViewHolder {

		TextView txtItemDescription;
		TextView txtQuantity;
		TextView txtFreeIssue;
		TextView txtEachDiscount;
		TextView txtReturnQuantity;
		TextView txtReplaceQuantity;
		TextView txtSampleQuantity;
	}

	private class MyExpandableListAdapter extends BaseExpandableListAdapter {
		private final ArrayList<OrderDetail> combinedOrderDetails = new ArrayList<OrderDetail>() {
			@Override
			public boolean add(OrderDetail orderDetail) {
				int index;
				if ((index = super.indexOf(orderDetail)) != -1) {
					OrderDetail obsoleteDetail = super.get(index);
					obsoleteDetail.setFreeIssue(obsoleteDetail.getFreeIssue() + orderDetail.getFreeIssue());
					super.set(index, obsoleteDetail);
					return true;
				}
				return super.add(orderDetail);
			}
		};

		@Override
		public int getGroupCount() {
			return 1;
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			return (groupPosition == 0) ? availableStock.size() : unAvailableStock.size();
		}

		@Override
		public ArrayList<Item> getGroup(int groupPosition) {
			return (groupPosition == 0) ? availableStock : unAvailableStock;
		}

		@Override
		public Item getChild(int groupPosition, int childPosition) {
			return (groupPosition == 0) ? availableStock.get(childPosition) : unAvailableStock.get(childPosition);
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
			groupViewHolder.txtCategory.setText((groupPosition == 0) ? "Loaded Items" : "UnAvailable Items");
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
			view.setBackgroundColor((childPosition % 2 == 0) ? Color.parseColor("#E6E6E6") : Color.parseColor("#FFFFFF"));
			updateView(childViewHolder, item, view);
			return view;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}

		@Override
		public void notifyDataSetChanged() {
			synchronized (SelectItemActivity.this) {
				combinedOrderDetails.clear();
				combinedOrderDetails.addAll(orderDetails);
				for (OrderDetail orderDetail : freeIssues) {
					combinedOrderDetails.add(orderDetail);
				}
				super.notifyDataSetChanged();
			}
		}

		private ChildViewHolder updateView(ChildViewHolder childViewHolder, Item item, View view) {
			for (OrderDetail orderDetail : combinedOrderDetails) {
				if (orderDetail.getItemId() == item.getItemId()) {
					childViewHolder.txtFreeIssue.setText(Integer.toString(orderDetail.getFreeIssue()));
					childViewHolder.txtQuantity.setText(Integer.toString(orderDetail.getQuantity()));
					childViewHolder.txtReturnQuantity.setText(Integer.toString(orderDetail.getReturnQuantity()));
					childViewHolder.txtReplaceQuantity.setText(Integer.toString(orderDetail.getReplaceQuantity()));
					childViewHolder.txtSampleQuantity.setText(Integer.toString(orderDetail.getSampleQuantity()));
					view.setBackgroundColor(Color.parseColor("#FFEFACFF"));
					return childViewHolder;
				}
			}
			childViewHolder.txtFreeIssue.setText("0");
			childViewHolder.txtQuantity.setText("0");
			view.setBackgroundColor(Color.TRANSPARENT);
			childViewHolder.txtReturnQuantity.setText("0");
			childViewHolder.txtReplaceQuantity.setText("0");
			childViewHolder.txtSampleQuantity.setText("0");
			return childViewHolder;
		}
	}
}
