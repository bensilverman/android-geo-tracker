package com.webenomic.geotracker;

import android.app.Service;
import android.content.Context;
import android.content.Intent;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;

public class InvestigatorHelper extends Service {

	private static final int ONGOING_NOTIFICATION = 293285761; // Arbitrary id
	// for
	// notification
	private LocationManager locMan;
	private Location curLocation;
	private Boolean locationChanged;
	final String TAG = "InvestigatorHelper";
	private AsyncHttpClient client = new AsyncHttpClient();

	Handler handler = new Handler();

	LocationListener gpsListener;

	{
		gpsListener = new LocationListener() {
			private DataHelper dh;
			private Boolean changedOnce = false;
			private Integer count = 0;

			public void onLocationChanged(Location location) {

				if (curLocation == null) {
					curLocation = location;
					locationChanged = true;
				}

				if (curLocation.getLatitude() == location.getLatitude()
						&& curLocation.getLongitude() == location.getLongitude())
					locationChanged = false;
				else
					locationChanged = true;

				curLocation = location;

				if (locationChanged) {

					if (this.count.compareTo(0) == 0) {
						Log.d(TAG,
								"onLocationChanged "
										+ String.valueOf(location.getLatitude()));
						this.changedOnce = true;
						this.count++;
						this.dh = new DataHelper(getBaseContext());
						this.dh.insert(System.currentTimeMillis() / 1000,
								String.valueOf(curLocation.getLatitude()),
								String.valueOf(curLocation.getLongitude()),
								curLocation.getAccuracy(),
								curLocation.getProvider());
						this.dh.close();
					}


					locMan.removeUpdates(this);
				}
			}

			@Override
			public void onStatusChanged(String provider, int status, Bundle extras) {

			}

			public void onProviderDisabled(String provider) {
				this.dh = new DataHelper(getBaseContext());
				this.dh.insert(System.currentTimeMillis() / 1000, "0.0", "0.0",
						Float.parseFloat("0"), provider + " off");
				this.dh.close();
			}

			public void onProviderEnabled(String provider) {
				this.dh = new DataHelper(getBaseContext());
				this.dh.insert(System.currentTimeMillis() / 1000, "0.0", "0.0",
						Float.parseFloat("0"), provider + " on");
				this.dh.close();
			}
		};
	}

	@Override
	public void onCreate() {
		super.onCreate();
		locMan = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
	}

	@Override
	public void onStart(Intent i, int startId) {
		this.GpsFinder.run();
		this.stopSelf();
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	public Runnable GpsFinder = new Runnable() {

		private DataHelper dh;

		public void run() {
			if (locMan.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
				locMan.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
						0, gpsListener);
			} else if (locMan
					.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
				locMan.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
						0, 0, gpsListener);
				Log.d(TAG, "Starting gpsListening.");
			} else {
				//log device as gps/network off
				this.dh = new DataHelper(getBaseContext());
				this.dh.insert(System.currentTimeMillis() / 1000, "0.0", "0.0",
						Float.parseFloat("0"), "all off");
				this.dh.close();
				Log.d(TAG, "All off.");
			}

		}
	};

}
