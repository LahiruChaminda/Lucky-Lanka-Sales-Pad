/*
 * Intellectual properties of Supun Lakshan Wanigarathna Dissanayake
 * Copyright (c) 2014, Supun Lakshan Wanigarathna Dissanayake. All rights reserved.
 * Created on : Sep 10, 2014, 8:30 AM
 */
package com.ceylon_linux.lucky_lanka.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.ceylon_linux.lucky_lanka.R;
import com.ceylon_linux.lucky_lanka.controller.OutletController;
import com.ceylon_linux.lucky_lanka.util.BatteryUtility;
import com.ceylon_linux.lucky_lanka.util.GpsReceiver;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

/**
 * @author Supun Lakshan Wanigarathna Dissanayake
 * @mobile +94711290392
 * @email supunlakshan.xfinity@gmail.com
 */
public class AddOutletActivity extends Activity {

	private final byte IMAGE_CAPTURE = 0;
	private final File IMAGE_FILE = new File(Environment.getExternalStorageDirectory() + "/lucky_lanka_temp/image.jpg");
	private ImageView imageView;
	private EditText inputOutletName;
	private EditText inputOutletAddress;
	private Button btnAddOutlet;
	private GpsReceiver gpsReceiver;
	private Thread GPS_RECEIVER;
	private volatile Location location;
	private ProgressDialog progressDialog;
	private Handler handler = new Handler();
	private boolean imageCaptured;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.outlet_image_capture);
		imageView = (ImageView) findViewById(R.id.imageView);
		imageView.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				return imageViewLongClicked(v);
			}
		});
		inputOutletName = (EditText) findViewById(R.id.inputOutletName);
		inputOutletAddress = (EditText) findViewById(R.id.inputOutletAddress);
		btnAddOutlet = (Button) findViewById(R.id.btnAddOutlet);
		btnAddOutlet.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				btnOutletAddClicked(v);
			}
		});
		gpsReceiver = GpsReceiver.getGpsReceiver(AddOutletActivity.this);
		GPS_RECEIVER = new Thread() {
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
						Toast.makeText(AddOutletActivity.this, "GPS Location Received", Toast.LENGTH_LONG).show();
					}
				});
			}
		};
		GPS_RECEIVER.start();
	}

	private void btnOutletAddClicked(final View view) {
		new Thread() {
			private boolean response;

			@Override
			public void run() {
				synchronized (AddOutletActivity.this) {
					if (location == null) {
						handler.post(new Runnable() {
							@Override
							public void run() {
								progressDialog = ProgressDialog.show(AddOutletActivity.this, null, "Waiting for GPS...");
							}
						});
						return;
					} else {
						if (imageCaptured) {
							try {
								handler.post(new Runnable() {
									@Override
									public void run() {
										progressDialog = ProgressDialog.show(AddOutletActivity.this, null, "Uploading Data...");
									}
								});
								HashMap<String, Object> parameters = new HashMap<String, Object>();
								parameters.put("companyname", inputOutletName.getText().toString());
								parameters.put("address", inputOutletAddress.getText().toString());
								parameters.put("outlet_cat_id", 8);
								parameters.put("routeid", 5);
								parameters.put("contactperson", "Thamira Madhushanka");
								parameters.put("DOB", "2014-01-31");
								parameters.put("email", "thamira@ceylonlinux.com");
								parameters.put("tel", "074420270");
								parameters.put("lon", location.getLongitude());
								parameters.put("lat", location.getLatitude());
								parameters.put("bat", BatteryUtility.getBatteryLevel(AddOutletActivity.this));
								response = OutletController.registerNewOutlet(AddOutletActivity.this, IMAGE_FILE, new JSONObject(parameters));
								if (response) {
									Intent homeActivity = new Intent(AddOutletActivity.this, HomeActivity.class);
									startActivity(homeActivity);
									finish();
								} else {
									AlertDialog.Builder alertBuilder = new AlertDialog.Builder(AddOutletActivity.this);
									alertBuilder.setTitle(R.string.app_name);
									alertBuilder.setMessage("Unable to register outlet");
									alertBuilder.setPositiveButton("OK", null);
									alertBuilder.show();
								}
							} catch (IOException e) {
								e.printStackTrace();
							} catch (JSONException e) {
								e.printStackTrace();
							} finally {
								handler.post(new Runnable() {
									@Override
									public void run() {
										if (progressDialog != null && progressDialog.isShowing()) {
											progressDialog.dismiss();
										}
										Toast.makeText(AddOutletActivity.this, "Outlet Registered", Toast.LENGTH_LONG).show();
									}
								});
							}
						} else {
							AlertDialog.Builder alertBuilder = new AlertDialog.Builder(AddOutletActivity.this);
							alertBuilder.setTitle(R.string.app_name);
							alertBuilder.setMessage("You need to capture image");
							alertBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									imageViewLongClicked(view);
								}
							});
							alertBuilder.setNegativeButton("Cancel", null);
							alertBuilder.show();
						}
					}
				}
			}
		}.start();
	}

	private boolean imageViewLongClicked(View view) {
		Intent imageCaptureActivity = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		createDirectoryIfNeeded();
		try {
			IMAGE_FILE.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Uri uri = Uri.fromFile(IMAGE_FILE);
		imageCaptureActivity.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		imageCaptureActivity.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
		startActivityForResult(imageCaptureActivity, IMAGE_CAPTURE);
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == IMAGE_CAPTURE && resultCode == RESULT_OK) {
			imageCaptured = true;
			imageView.setImageDrawable(null);
			Bitmap bitmap = BitmapFactory.decodeFile(IMAGE_FILE.getAbsolutePath());
			imageView.setImageBitmap(bitmap);
		}
	}

	private void createDirectoryIfNeeded() {
		File direct = new File(Environment.getExternalStorageDirectory() + "/lucky_lanka_temp");
		if (!direct.exists() && direct.mkdir()) {
			Log.i(getClass().getName(), "Directory Created");
		}
	}
}
