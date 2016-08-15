package com.example.deeptotv.freezelocate;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button button_normal,button_scan;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buttonClickListener();
    }

    public void buttonClickListener()
    {
        button_normal = (Button) findViewById(R.id.normal_button);
        button_scan = (Button) findViewById(R.id.scan_button);
        button_normal.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                Intent intent = new Intent(MainActivity.this,ActivityNormal.class);
                startActivity(intent);

            }

        });
        button_scan.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                Intent intent = new Intent(MainActivity.this,ActivityScan.class);
                startActivity(intent);

            }

        });
    }
}
