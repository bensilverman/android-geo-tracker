package com.webenomic.geotracker;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class HeartBeat extends Service {
	final String TAG = "HeartBeat";

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onStart(Intent i, int startId) {
		this.beat.run();
		this.stopSelf();
	}

	public Runnable beat = new Runnable() {
		private DataHelper dh;
		public void run() {
			this.dh = new DataHelper(getBaseContext());
			this.dh.insert(System.currentTimeMillis() / 1000, "0.0", "0.0",
					Float.parseFloat("0"), "hb");
			this.dh.close();
		}
	};
}
