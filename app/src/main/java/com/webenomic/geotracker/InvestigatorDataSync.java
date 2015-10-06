package com.webenomic.geotracker;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.BatteryManager;
import android.os.IBinder;
import android.provider.Settings;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import org.apache.http.Header;
import android.util.Log;

public class InvestigatorDataSync extends Service {

	public DataHelper dh;

	//TODO/FYI: add additional logic for https/SSL post, uses http post
	String url_add_geo = "http://your.posturl.com/post/";
	private AsyncHttpClient client = new AsyncHttpClient();

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		this.dh = new DataHelper(this);
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
		this.DataSender.run();
		this.stopSelf();
	}
	
	@Override
	public void onDestroy() {
		this.dh.close();
	}

	public Runnable DataSender = new Runnable() {
		public void run() {
			sendGeo();
		}
	};

	private void sendGeo() {

		final Cursor c;
		try {
			c = dh.getCursor();
			if (c != null) {
				
				// Battery 
				android.content.IntentFilter ifilter = new android.content.IntentFilter(Intent.ACTION_BATTERY_CHANGED);
				Intent batteryStatus = getApplicationContext().registerReceiver(null, ifilter);
				int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
				int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
				float batteryPct = level / (float)scale;
				String battery = String.valueOf(batteryPct);
				
				SharedPreferences login_app_preferences = getApplicationContext()
						.getSharedPreferences("WEBENOMIC_DATA", 0);

				//a sample post var of some authenticating info from application events, e.g. user login
				String userId = login_app_preferences.getString("userId", "");

				//logging device id in case the aforementioned userid logs in from multiple devices
				String deviceId = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);

				while (c.moveToNext()) {
					RequestParams params = new RequestParams();
					params.put("userid", userId);
					params.put("deviceid", deviceId);
					params.put("lat", c.getString(c.getColumnIndex("lat")));
					params.put("lon", c.getString(c.getColumnIndex("lon")));
					params.put("created", String.valueOf(c.getLong(c
							.getColumnIndex("created"))));
					params.put("accuracy", String.valueOf(c.getLong(c
							.getColumnIndex("accuracy"))));
					params.put("provider",
							c.getString(c.getColumnIndex("provider")));
					params.put("battery", battery);
					client.post(getApplicationContext(),
							url_add_geo, params,
							new TextHttpResponseHandler() {

								public void onSuccess(int arg0, Header[] arg1, String response) {
									try {
										//passes back failed if post vars don't meet criteria
										if (!response.contains("failed")) {
											DataHelper dhLocal = new DataHelper(getApplicationContext());
											dhLocal.deleteRow(Integer
													.parseInt(response));
											dhLocal.close();
										} else {
											Log.d("sendGeo", "Failed");
										}
									} catch (Exception e) {
										e.printStackTrace();
									}
								}

								@Override
								public void onFailure(int arg0, Header[] arg1,
													  String arg2, Throwable arg3) {
									Log.d("sendGeo", "Request failed");

								}

							});
				}
			}
			c.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
