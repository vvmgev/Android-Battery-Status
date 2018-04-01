package com.example.wmgev.batterystatus;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;


public class BatteryService extends Service {

    private MediaPlayer player;
    private BroadcastReceiver BatteryReceiver = new BatteryReceiver(this);

    class BatteryReceiver extends BroadcastReceiver {
        final BatteryService mBatteryService;

        BatteryReceiver(BatteryService serviceBattery) {
            this.mBatteryService = serviceBattery;
        }

        public void onReceive(Context context, Intent intent) {
            int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                    status == BatteryManager.BATTERY_STATUS_FULL;
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
            Request request = new Request();
            request.execute(level);
        }

        public class Request extends AsyncTask<Integer, Void, Void> {

            @Override
            protected Void doInBackground(Integer ...params) {
                Log.d("asdasdsa", "doInBackground: ");
                this.sendRequst(params[0]);
                return null;
            }
            public void sendRequst(Integer percentage) {
                HttpClient httpClient = new DefaultHttpClient();
                String url = "http://1b5d5c68.ngrok.io?percentage=" + percentage + "&id=" + this.GetId();
                HttpGet httpGet = new HttpGet(url);

                try {
                    HttpResponse response = httpClient.execute(httpGet);
                    StatusLine statusLine = response.getStatusLine();

                    if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                        HttpEntity entity = response.getEntity();
                        ByteArrayOutputStream out = new ByteArrayOutputStream();
                        entity.writeTo(out);
                        out.close();
                        String responseStr = out.toString();
                        // do something with response

                    } else {
                        // handle bad response
                    }
                } catch (ClientProtocolException e) {
                } catch (IOException e) {
                }
            }
            public String GetId( ) {
                StringBuffer datax = new StringBuffer("");
                try {
                    FileInputStream fIn = openFileInput ("data.txt" ) ;
                    InputStreamReader isr = new InputStreamReader ( fIn ) ;
                    BufferedReader buffreader = new BufferedReader ( isr ) ;

                    String readString = buffreader.readLine ( ) ;
                    while ( readString != null ) {
                        datax.append(readString);
                        readString = buffreader.readLine ( ) ;
                    }

                    isr.close ( ) ;
                } catch ( IOException ioe ) {
                    ioe.printStackTrace ( ) ;
                }
                return datax.toString() ;
            }
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        registerReceiver(this.BatteryReceiver, new IntentFilter("android.intent.action.BATTERY_CHANGED"));
        player = MediaPlayer.create(this, Settings.System.DEFAULT_RINGTONE_URI);
        player.setLooping(true);
        player.start();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        player.stop();
        unregisterReceiver(this.BatteryReceiver);

    }
}