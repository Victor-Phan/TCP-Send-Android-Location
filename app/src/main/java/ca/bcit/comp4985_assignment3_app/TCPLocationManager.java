package ca.bcit.comp4985_assignment3_app;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


public class TCPLocationManager implements LocationListener {
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public static final int updatesInMilliseconds = 3000;
    public static final int minDistance = 0;
    public Activity parentActivity = null;
    public Context mContext = null;
    public TCPClient client = null;
    public String ip;
    public int port;

    private LocationManager locationManager;

    //Permission is already requested..
    public TCPLocationManager(Activity activity, String ip, int port) throws Exception {
        this.parentActivity = activity;
        this.mContext = activity.getBaseContext();
        locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            throw new Exception("Permission not granted");
        }
        this.ip = ip;
        this.port = port;
    }

    // Do not need to check permissions since constructor checks for us.
    @SuppressLint("MissingPermission")
    public void startUpdates() {
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, updatesInMilliseconds, minDistance, TCPLocationManager.this);
    }

    @Override
    public void onLocationChanged(Location loc) {
        try {
            new SendUpdateToServer().execute(loc);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void checkLocationPermission(final Activity activity) {
        try {
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                        Manifest.permission.ACCESS_FINE_LOCATION)) {
                    createLocationRequestDialog(activity);

                } else {
                    // Permission is able to be requested
                    ActivityCompat.requestPermissions(activity,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            MY_PERMISSIONS_REQUEST_LOCATION);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void createLocationRequestDialog(final Activity activity) {
        //Alert the user for Location Permission request
        new AlertDialog.Builder(activity)
                .setTitle("Location Permission Required")
                .setMessage("This app required the Location permission, please accept to use location functionality")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Prompt the user once explanation has been shown
                        ActivityCompat.requestPermissions(activity,
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                MY_PERMISSIONS_REQUEST_LOCATION);
                    }
                })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Permission was not given
                        activity.finishAndRemoveTask();
                        //System.exit(1);
                    }
                })
                .create()
                .show();
    }

    class SendUpdateToServer extends AsyncTask<Location, Void, Location> {
        @Override
        protected Location doInBackground(Location... locs) {
            Location location = null;
            try {
                //Get location first
                location = locs[0];
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                //build string
                String locationString = "";
                locationString += latitude + ",";
                locationString += longitude;
                if (client == null) {
                    client = new TCPClient(ip, port);
                }
                client.sendData(locationString);
            } catch (Exception e) {
                // Remove updates
                locationManager.removeUpdates(TCPLocationManager.this);
                location = null;
            }
            return location;
        }

        protected void onPostExecute(final Location loc) {
            try {
                if (loc != null) {
                    double latitude = loc.getLatitude();
                    double longitude = loc.getLongitude();
                    Log.i("Location: ", "IN ON LOCATION CHANGE, lat=" + latitude + ", lon=" + longitude);
                    //append to ui
                } else {
                    throw new Exception("Connection Error");
                }
                final Location location = loc; // the final is important
                Thread t = new Thread(new Runnable() {
                    public void run() {
                        parentActivity.runOnUiThread(new Runnable() {
                            public void run() {
                                double latitude = location.getLatitude();
                                double longitude = location.getLongitude();
                                TextView tv = new TextView(mContext);
                                String locationString = "";
                                locationString += "lat=" + latitude + "\n";
                                locationString += "long=" + longitude + "\n";
                                tv.setText(locationString);
                                LinearLayout scrollView = parentActivity.findViewById(R.id.sentLocationList);
                                scrollView.addView(tv);

                            }
                        });
                    }
                });
                t.start();
            } catch (Exception e) {
                e.printStackTrace();
                new Thread() {
                    public void run() {
                        parentActivity.runOnUiThread(new Runnable() {
                            public void run() {
                                Toast toast = Toast.makeText(mContext, "Connection Error", Toast.LENGTH_LONG);
                                toast.show();
                            }
                        });
                    }
                }.start();
                parentActivity.finish();
            }
        }
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }
}
