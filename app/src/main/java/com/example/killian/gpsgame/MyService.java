package com.example.killian.gpsgame;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

public class MyService extends Service {
    public MyService() {
    }
    int goalsReachedToday = 0;
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            int message = intent.getIntExtra("counter",0);
            goalsReachedToday += message;
            Log.d("receiver", "Goals reached: " + goalsReachedToday);
        }
    };

    public int onStartCommand(Intent intent, int flags, int startId) {
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("Service counter"));
        Toast.makeText(this, "GAME STARTED",Toast.LENGTH_LONG).show();
        return START_STICKY;
    }

    public void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        Toast.makeText(this, "Game over!", Toast.LENGTH_LONG).show();
        super.onDestroy();

    }
}
