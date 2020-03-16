package ca.bcit.comp4985_assignment3_app;
/*------------------------------------------------------------------------------------------------------------------
-- SOURCE FILE: 	SendLocationActivity.java - Contains the activity that shows each sent location to the server.
--
-- PROGRAM: 		SendLocationUpdates
--
-- FUNCTIONS:       onCreate(Bundle savedInstanceState)
--                  onStop()
--                  onDestroy()
--
--
-- DATE: 			March 16, 2020
--
-- REVISIONS:
--
-- DESIGNER: 		Victor Phan
--
-- PROGRAMMER: 		Victor Phan
--
-- NOTES:
--                  Connects the server and starts periodically sending location updates.
--
--------------------------------------------------------------------------------------------------------------------*/

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SendLocationActivity extends AppCompatActivity {

    public TCPLocationManager manager = null;


    /*-----------------------------------------------------------------------------------------------------------------
-- Function:	onCreate
--
-- DATE:		March 16, 2020
--
-- REVISIONS:
--
-- DESIGNER: 	Victor Phan
--
-- PROGRAMMER: 	Victor Phan
--
-- Interface:	onCreate(Bundle savedInstanceState)
--
-- NOTES:
--              Grabs the Server ip and port via an intent.
--              Starts sending location updates to the server.
--
-------------------------------------------------------------------------------------------------------------------*/
    @SuppressLint("ServiceCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_location);
        //https://developer.android.com/studio/run/emulator-networking
        // For debugging purposes, use ip address 10.0.2.2
        try {
            Intent intent = getIntent();
            String ip = intent.getStringExtra("ip");
            String port = intent.getStringExtra("port");

            TextView tv_IPAddress = findViewById(R.id.ipAddress);
            TextView tv_PortNumber = findViewById(R.id.portNumber);

            tv_IPAddress.setText(ip);
            tv_PortNumber.setText(port);

            manager = new TCPLocationManager(SendLocationActivity.this, ip, Integer.parseInt(port));
            manager.startUpdates();
        } catch (Exception e) {
            e.printStackTrace();
            finish();
        }
    }

    /**
     * Default unused.
     */
    @Override
    protected void onStop() {
        super.onStop();
    }

    /**
     * Default unused.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
