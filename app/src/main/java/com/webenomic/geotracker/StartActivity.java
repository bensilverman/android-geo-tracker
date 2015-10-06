package com.webenomic.geotracker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class StartActivity extends Activity {

	private DataHelper dh;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final Intent iLocationService = new Intent(this, InvestigatorHelper.class);
		this.startService(iLocationService);

	}
}