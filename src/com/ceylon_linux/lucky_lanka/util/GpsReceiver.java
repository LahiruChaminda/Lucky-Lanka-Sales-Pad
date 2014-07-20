/*
 * Intellectual properties of Supun Lakshan Wanigarathna Dissanayake
 * Copyright (c) 2014, Supun Lakshan Wanigarathna Dissanayake. All rights reserved.
 * Created on : May 9, 2014, 10:10:54 PM
 */

package com.ceylon_linux.lucky_lanka.util;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;

import java.util.List;

/**
 * GpsReceiver - Receive and Provide GPS locations
 *
 * @author Supun Lakshan Wanigarathna Dissanayake
 * @mobile +94711290392
 * @email supunlakshan.xfinity@gmail.com
 */
public class GpsReceiver extends Service {


	private static final long MINIMUM_DISTANCE_CHANGE = 0;
	private static final long MINIMUM_TIME_DIFFERENCE = 0;
	protected static LocationManager locationManager;
	private volatile static Location lastKnownLocation;
	private volatile static GpsReceiver gpsReceiver;

	private GpsReceiver(Context applicationContext) {
		locationManager = (LocationManager) applicationContext.getSystemService(Context.LOCATION_SERVICE);

		float bestAccuracy = Float.MAX_VALUE;
		long bestTime = Long.MIN_VALUE;
		List<String> matchingProviders = locationManager.getAllProviders();
		for (String provider : matchingProviders) {
			Location location = locationManager.getLastKnownLocation(provider);
			locationManager.requestLocationUpdates(provider, MINIMUM_TIME_DIFFERENCE, MINIMUM_DISTANCE_CHANGE, LocationListenerImpl.getInstance(), Looper.getMainLooper());
			if (location != null) {
				float accuracy = location.getAccuracy();
				long time = location.getTime();
				if ((time > MINIMUM_TIME_DIFFERENCE && accuracy < bestAccuracy)) {
					lastKnownLocation = location;
					bestAccuracy = accuracy;
					bestTime = time;
				} else if (time < MINIMUM_TIME_DIFFERENCE && bestAccuracy == Float.MAX_VALUE && time > bestTime) {
					lastKnownLocation = location;
					bestTime = time;
				}
			}
		}
	}

	public synchronized static GpsReceiver getGpsReceiver(Context applicationContext) {
		if (gpsReceiver == null) {
			gpsReceiver = new GpsReceiver(applicationContext);
		}
		return gpsReceiver;
	}

	public synchronized Location getHighAccurateLocation() {
		lastKnownLocation = null;
		do {
			if (lastKnownLocation != null) {
				if (lastKnownLocation.getLatitude() == 0 && lastKnownLocation.getLongitude() == 0) {
					return lastKnownLocation = null;
				}
				long time = lastKnownLocation.getTime();
				long currentTimeMillis = System.currentTimeMillis();
				long timeDifference = Math.abs(time - currentTimeMillis);
				if (timeDifference > 30 * 60 * 1000) {
					return lastKnownLocation = null;
				}
			}
		} while (lastKnownLocation == null || (lastKnownLocation != null && (lastKnownLocation.getLatitude() == 0 || lastKnownLocation.getLongitude() == 0)));
		return lastKnownLocation;
	}

	public synchronized Location getLastKnownLocation() {
		if (lastKnownLocation != null) {
			if (lastKnownLocation.getLatitude() == 0 && lastKnownLocation.getLongitude() == 0) {
				return lastKnownLocation = null;
			}
			long time = lastKnownLocation.getTime();
			long currentTimeMillis = System.currentTimeMillis();
			long timeDifference = Math.abs(time - currentTimeMillis);
			if (timeDifference > 30 * 60 * 1000) {
				return lastKnownLocation = null;
			}
		}
		return lastKnownLocation;
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	private static class LocationListenerImpl implements LocationListener {
		private static LocationListenerImpl locationListener;

		private LocationListenerImpl() {
		}

		public static LocationListenerImpl getInstance() {
			return (locationListener == null) ? locationListener = new LocationListenerImpl() : locationListener;
		}

		@Override
		public void onLocationChanged(Location location) {
			float bestAccuracy = Float.MAX_VALUE;
			long bestTime = Long.MIN_VALUE;
			if (location != null) {
				float accuracy = location.getAccuracy();
				long time = location.getTime();
				if ((time > MINIMUM_TIME_DIFFERENCE && accuracy < bestAccuracy)) {
					lastKnownLocation = location;
					bestAccuracy = accuracy;
					bestTime = time;
				} else if (time < MINIMUM_TIME_DIFFERENCE && bestAccuracy == Float.MAX_VALUE && time > bestTime) {
					lastKnownLocation = location;
					bestTime = time;
				}
			}
			lastKnownLocation = location;
		}

		@Override
		public void onProviderDisabled(String provider) {
		}

		@Override
		public void onProviderEnabled(String provider) {
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	}
}
