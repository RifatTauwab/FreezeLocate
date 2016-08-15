package com.example.deeptotv.freezelocate;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.content.Intent;
import android.widget.Toast;


import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.sql.*;

public class ActivityScan extends AppCompatActivity {

    Button scan_button,send_database_button;
    EditText get_code_edittext;
    TextView latTxt,longTxt;
    GPSTracker gps;
    Connection conn;
    String barCode,latTextView,longTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_scan);
        scan_button = (Button) findViewById(R.id.scan_button);
        send_database_button = (Button) findViewById(R.id.send_button);
        get_code_edittext = (EditText) findViewById(R.id.edittext_barcode);
        latTxt = (TextView) findViewById(R.id.loc_lat);
        longTxt = (TextView) findViewById(R.id.loc_long);

        scan_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.scan_button) {
                    IntentIntegrator scanIntegrator = new IntentIntegrator(ActivityScan.this);
                    gps = new GPSTracker(ActivityScan.this);
                    scanIntegrator.initiateScan();
                }
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanningResult != null) {
            String scanContent = scanningResult.getContents();
            get_code_edittext.setText(scanContent);
            if (gps.canGetLocation()) {

                double latitude = gps.getLatitude();
                double longitude = gps.getLongitude();
                latTxt.setText(String.valueOf(latitude));
                longTxt.setText(String.valueOf(longitude));
            }
            else
            {
                gps.showSettingsAlert();
            }
        }
        else{
            Toast toast = Toast.makeText(getApplicationContext(),
                    "No scan data received!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }
    public void onClick(View view)
    {

        barCode = get_code_edittext.getText().toString();
        latTextView = latTxt.getText().toString();
        longTextView = longTxt.getText().toString();
        class ConnectThread implements Runnable
        {
            @Override
            public void run() {

                connect();
            }
        }
        Thread t = new Thread(new ConnectThread());
        //t.setDaemon(true);
        t.start();


    }

    public void connect()
    {

        try
        {
            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection("jdbc:postgresql://172.16.253.10:5432/adempiere", "adempiere", "adempierews");
            Statement statement1 = conn.createStatement();
            String sql = "INSERT INTO Freezer_Location (barcode,latitude,longitude)" +
                    "VALUES ('"+barCode+"', '"+latTextView+"', '"+longTextView+"')";
            statement1.executeUpdate(sql);

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("not connected");
        }

    }
}
