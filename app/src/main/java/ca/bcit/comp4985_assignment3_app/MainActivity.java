package ca.bcit.comp4985_assignment3_app;

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

        btn_Connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ip = et_IPAddressEditText.getText().toString();
                String port = et_PortNumber.getText().toString();
                //make intent and send to next activity
                Intent i = new Intent(getBaseContext(), SendLocationActivity.class);
                i.putExtra("ip", ip);
                i.putExtra("port", port);
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
