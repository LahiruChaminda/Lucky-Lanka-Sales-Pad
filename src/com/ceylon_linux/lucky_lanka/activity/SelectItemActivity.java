/*
 * Intellectual properties of Supun Lakshan Wanigarathna Dissanayake
 * Copyright (c) 2014, Supun Lakshan Wanigarathna Dissanayake. All rights reserved.
 * Created on : Jun 10, 2014, 12:20:23 PM
 */

package com.ceylon_linux.lucky_lanka.activity;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.ceylon_linux.lucky_lanka.R;
import com.ceylon_linux.lucky_lanka.controller.ItemController;
import com.ceylon_linux.lucky_lanka.controller.OrderController;
import com.ceylon_linux.lucky_lanka.controller.UserController;
import com.ceylon_linux.lucky_lanka.model.*;
import com.ceylon_linux.lucky_lanka.util.ProgressDialogGenerator;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.select_items_page);
		initialize();
		outlet = (Outlet) getIntent().getExtras().get("outlet");
		try {
			categories = ItemController.loadItemsFromDb(this);
		} catch (IOException ex) {
			Logger.getLogger(SelectItemActivity.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
		} catch (JSONException ex) {
			Logger.getLogger(SelectItemActivity.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
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
			public boolean onChildClick(ExpandableListView expandableListView, View view, final int groupPosition, int childPosition, long id) {
				final Dialog dialog = new Dialog(SelectItemActivity.this);
				dialog.setCanceledOnTouchOutside(false);
				dialog.setTitle("Please Insert Quantity");
				dialog.setContentView(R.layout.quantity_insert_page);
				Button btnOk = (Button) dialog.findViewById(R.id.btnOk);
				TextView txtItemDescription = (TextView) dialog.findViewById(R.id.txtItemDescription);
				final EditText inputRequestedQuantity = (EditText) dialog.findViewById(R.id.inputRequestedQuantity);
				final Item item = categories.get(groupPosition).getItems().get(childPosition);
				final TextView txtFreeQuantity = (TextView) dialog.findViewById(R.id.txtFreeQuantity);
				final TextView txtDiscount = (TextView) dialog.findViewById(R.id.txtDiscount);
				Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel);
				txtItemDescription.setText(item.getItemDescription());
				Log.i("Six Item", Boolean.toString(item.isSixPlusOneAvailability()));
				Log.i("Six Outlet", Integer.toString(outlet.getOutletType()));
				inputRequestedQuantity.addTextChangedListener(new TextWatcher() {
					@Override
					public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
					}

					@Override
					public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
					}

					@Override
					public void afterTextChanged(Editable editable) {
						String requestedQuantityString = inputRequestedQuantity.getText().toString();
						int requestedQuantity = Integer.parseInt((requestedQuantityString.isEmpty()) ? "0" : requestedQuantityString);
						OrderDetail orderDetail = OrderDetail.getFreeIssueCalculatedOrderDetail(outlet, item, requestedQuantity);
						txtFreeQuantity.setText(Integer.toString(orderDetail.getFreeIssue()));
						txtDiscount.setText(Double.toString(outlet.getOutletDiscount()));
					}
				});
				btnOk.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						String requestedQuantityString = inputRequestedQuantity.getText().toString();
						int requestedQuantity = Integer.parseInt((requestedQuantityString.isEmpty()) ? "0" : requestedQuantityString);
						OrderDetail orderDetail = OrderDetail.getFreeIssueCalculatedOrderDetail(outlet, item, requestedQuantity);
						orderDetails.add(orderDetail);
						item.setSelected(true);
						itemList.collapseGroup(groupPosition);
						itemList.expandGroup(groupPosition);
						dialog.dismiss();
					}
				});
				btnCancel.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						dialog.dismiss();
					}
				});
				dialog.show();
				return true;
			}
		});
		finishButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				final Order order = new Order(outlet.getOutletId(), UserController.getAuthorizedUser(SelectItemActivity.this).getPositionId(), outlet.getRouteId(), new Date().getTime(), orderDetails);
				Log.i("json",order.getOrderAsJson().toString());
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
							Toast.makeText(SelectItemActivity.this, "Order Syncked Successfully", Toast.LENGTH_LONG).show();
						} else {
							Toast.makeText(SelectItemActivity.this, "Unable To Sync Order", Toast.LENGTH_LONG).show();
							OrderController.saveOrderToDb(SelectItemActivity.this, order);
						}
					}
				}.execute(order);
			}
		});
	}

		// <editor-fold defaultstate="collapsed" desc="Initialize">

	private void initialize() {
		itemList = (ExpandableListView) findViewById(R.id.itemList);
		finishButton = (Button) findViewById(R.id.finishButton);
		orderDetails = new ArrayList<OrderDetail>();
	}
	// </editor-fold>

	private ChildViewHolder updateView(ChildViewHolder childViewHolder, Item item) {
		for (OrderDetail orderDetail : orderDetails) {
			if (orderDetail.getItemId() == item.getItemId()) {
				childViewHolder.txtFreeIssue.setText(Integer.toString(orderDetail.getFreeIssue()));
				childViewHolder.txtQuantity.setText(Integer.toString(orderDetail.getQuantity()));
				return childViewHolder;
			}
		}
		childViewHolder.txtFreeIssue.setText("0");
		childViewHolder.txtQuantity.setText("0");
		return childViewHolder;
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
	}
}
