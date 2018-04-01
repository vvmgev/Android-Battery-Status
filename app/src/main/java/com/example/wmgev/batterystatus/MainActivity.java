package com.example.wmgev.batterystatus;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;



import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button start, stop;
    private TextView textViewId;
    private String dataFileName = "data.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        start = (Button) findViewById(R.id.buttonStart);
        stop = (Button) findViewById(R.id.buttonStop);
        textViewId = (TextView) findViewById(R.id.textViewId);

        start.setOnClickListener(this);
        stop.setOnClickListener(this);


        File file = new File(getApplicationContext().getFilesDir(), this.dataFileName);
        if(file.exists()) {
            textViewId.setText(this.GetId());
        } else {
            this.SaveId(this.generateID(9, 0));
        }


    }

    private String generateID(Integer max, Integer min) {
        Random rand = new Random();
        String randomNum1 = String.valueOf(rand.nextInt((max - min) + 1) + min);
        String randomNum2 = String.valueOf(rand.nextInt((max - min) + 1) + min);
        String randomNum3 = String.valueOf(rand.nextInt((max - min) + 1) + min);
        String randomNum4 = String.valueOf(rand.nextInt((max - min) + 1) + min);
        return randomNum1 + randomNum2 + randomNum3 +randomNum4;
    }

    private void SaveId(String id) {
        String fileContents = id;
        FileOutputStream outputStream;

        try {
            outputStream = openFileOutput(this.dataFileName, Context.MODE_PRIVATE);
            outputStream.write(fileContents.getBytes());
            outputStream.close();
            Log.d("Tag", "SaveId: ");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String GetId( ) {
        StringBuffer datax = new StringBuffer("");
        try {
            FileInputStream fIn = openFileInput ( this.dataFileName ) ;
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

    @Override
    public void onClick(View view) {
        if(view == start) {
            startService(new Intent(this, BatteryService.class));
        }else {
            stopService(new Intent(this, BatteryService.class));
        }
    }
}