package ca.bcit.comp4985_assignment3_app;
/*------------------------------------------------------------------------------------------------------------------
-- SOURCE FILE: 	MainActivity.java - This is the main driver of the program. It loads up the initial screen
--                                      that prompts the user for the server IP and port.
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
--                  The MainActivity checks for location permissions and prompts the user.
--                  It also collects the IP and port of the server to pass to the next scene via an intent.
--
--------------------------------------------------------------------------------------------------------------------*/

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

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
-- INTERFACE:	void onCreate(Bundle savedInstanceState)
--
-- RETURNS:     void
--
-- NOTES:
--              This function create the scene and displays it to the user.
--              It will also check if location permission was provided.
--
-------------------------------------------------------------------------------------------------------------------*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Check location permissions
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            TCPLocationManager.checkLocationPermission(MainActivity.this);
        }

        Button btn_Connect = findViewById(R.id.connectButton);
        final EditText et_IPAddressEditText = findViewById(R.id.etIPAddress);
        final EditText et_PortNumber = findViewById(R.id.etPortNumber);
        final EditText et_ClientName = findViewById(R.id.etClientName);

        btn_Connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ip = et_IPAddressEditText.getText().toString();
                String port = et_PortNumber.getText().toString();
                String clientName = et_ClientName.getText().toString();
                //make intent and send to next activity
                Intent i = new Intent(getBaseContext(), SendLocationActivity.class);
                i.putExtra("ip", ip);
                i.putExtra("port", port);
                i.putExtra("clientName", clientName);
                startActivity(i);
            }
        });
    }


    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
