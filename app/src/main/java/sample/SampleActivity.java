package sample;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;

import com.webenomic.geotracker.DataHelper;
import com.webenomic.geotracker.HeartBeat;
import com.webenomic.geotracker.InvestigatorDataSync;
import com.webenomic.geotracker.InvestigatorHelper;

import webenomic.android.geotracker.R;

public class SampleActivity extends Activity {

    //millisecond interval to run GPS geolocation, every 30 seconds
    int geo_locate_interval = 300000;

    //millisecond interval to post data to remote URL, every 90 seconds
    int send_data_interval  = 900000;

    //millisecond interval to check device for heartbeat (on state), every 30 seconds
    int heartbeat_interval  = 300000;

    private DataHelper dh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        //UI goes here, blank in this sample
        setContentView(R.layout.home);

        // Set alarm for getting geo
        Intent iGeoService = new Intent(this, InvestigatorHelper.class);
        PendingIntent piGeoService = PendingIntent.getService(this, 0, iGeoService, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager GeoAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        GeoAlarmManager.cancel(piGeoService);
        GeoAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), geo_locate_interval, piGeoService);

        // Set alarm for sending geo data
        Intent iSendDataService = new Intent(this, InvestigatorDataSync.class);
        PendingIntent piSendDataService = PendingIntent.getService(this, 0, iSendDataService, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager DataAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        DataAlarmManager.cancel(piSendDataService);
        DataAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), send_data_interval, piSendDataService);

        // Set Heartbeat
        Intent iHeartBeatService = new Intent(this, HeartBeat.class);
        PendingIntent piHeartBeatService = PendingIntent.getService(this, 0, iHeartBeatService, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager HeartBeatAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        HeartBeatAlarmManager.cancel(piHeartBeatService);
        HeartBeatAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), heartbeat_interval, piHeartBeatService);


    }
}
