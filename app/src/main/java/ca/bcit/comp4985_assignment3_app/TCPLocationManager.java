package ca.bcit.comp4985_assignment3_app;
/*------------------------------------------------------------------------------------------------------------------
-- SOURCE FILE: 	TCPLocationManager.java - Creates a TCP Connection and sends the the user's location
--                                            to the server every 3 seconds.
--
-- PROGRAM: 		SendLocationUpdates
--
-- FUNCTIONS:       startUpdates(Bundle savedInstanceState)
--                  onLocationChanged(Location loc)
--                  onDestroy()
--                  checkLocationPermission(final Activity activity)
--                  createLocationRequestDialog(final Activity activity)
--                  SendUpdateToServer
--                  onProviderDisabled(String provider)
--                  onProviderEnabled(String provider)
--                  onStatusChanged(String provider, int status, Bundle extras)
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
--                  This class contains methods to get the user's location periodically then
--                  send it over to the server in a formated string
--                  ie: lat,long
--                  ie: 10.22,-123.446
--
--------------------------------------------------------------------------------------------------------------------*/

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
    public String clientName = "unknown";
    public String ip;
    public int port;
    private LocationManager locationManager;

    /*-----------------------------------------------------------------------------------------------------------------
-- Constructor:	TCPLocationManager
--
-- DATE:		March 16, 2020
--
-- REVISIONS:
--
-- DESIGNER: 	Victor Phan
--
-- PROGRAMMER: 	Victor Phan
--
-- Interface:	TCPLocationManager(Activity activity, String ip, int port)
--                                  activity - The activity creating this object
--                                  ip - valid ip address of the server
--                                  port - valid port on the server
--
-- NOTES:
--              Checks if location permission was provided.
--              Saves all parameters to object instance.
--              Throws Exception if location permissions is not granted.
--
-------------------------------------------------------------------------------------------------------------------*/
    public TCPLocationManager(Activity activity, String ip, int port, String clientName) throws Exception {
        this.parentActivity = activity;
        this.mContext = activity.getBaseContext();
        locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            throw new Exception("Permission not granted");
        }
        this.ip = ip;
        this.port = port;
        this.clientName = clientName;
    }

    /*-----------------------------------------------------------------------------------------------------------------
-- Function:	startUpdates
--
-- DATE:		March 16, 2020
--
-- REVISIONS:
--
-- DESIGNER: 	Victor Phan
--
-- PROGRAMMER: 	Victor Phan
--
-- INTERFACE:	void startUpdates()
--
-- RETURNS:     void
--
-- NOTES:
--              Do not need to check permissions because the constructor already checks for it.
--              This method will start the onLocationChanged callback loop.
--
-------------------------------------------------------------------------------------------------------------------*/
    @SuppressLint("MissingPermission")
    public void startUpdates() {
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, updatesInMilliseconds, minDistance, TCPLocationManager.this);
    }

    /*-----------------------------------------------------------------------------------------------------------------
-- Function:	onLocationChanged
--
-- DATE:		March 16, 2020
--
-- REVISIONS:
--
-- DESIGNER: 	Victor Phan
--
-- PROGRAMMER: 	Victor Phan
--
-- INTERFACE:	void onLocationChanged(Location loc)
--                      loc - contains the current location
--
-- RETURNS:     void
--
-- NOTES:
--              This function will be called every updatesInMilliseconds milliseconds after the startUpdates has ran.
--
-------------------------------------------------------------------------------------------------------------------*/
    @Override
    public void onLocationChanged(Location loc) {
        try {
            new SendUpdateToServer().execute(loc);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /*-----------------------------------------------------------------------------------------------------------------
-- Function:	checkLocationPermission
--
-- DATE:		March 16, 2020
--
-- REVISIONS:
--
-- DESIGNER: 	Victor Phan
--
-- PROGRAMMER: 	Victor Phan
--
-- INTERFACE:	void  checkLocationPermission(final Activity activity)
--                      activity - activity that calls this function
--
-- RETURNS:     void
--
-- NOTES:
--              This function will check if location permissions are granted to the app.
--              If they are not it will create a dialog requesting permissions to be granted.
--
-------------------------------------------------------------------------------------------------------------------*/
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

    /*-----------------------------------------------------------------------------------------------------------------
-- Function:	createLocationRequestDialog
--
-- DATE:		March 16, 2020
--
-- REVISIONS:
--
-- DESIGNER: 	Victor Phan
--
-- PROGRAMMER: 	Victor Phan
--
-- INTERFACE:	void  createLocationRequestDialog(final Activity activity)
--                      activity - activity that calls this function
--
-- RETURNS:     void
--
-- NOTES:
--                    Creates a dialog to request permissions from the user.
--
-------------------------------------------------------------------------------------------------------------------*/
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
                locationString += longitude + ",";
                locationString += clientName;
                if (client == null) {
                    client = new TCPClient(ip, port);
                }
                client.sendData(locationString);
            } catch (Exception e) {
                // Remove updates
                locationManager.removeUpdates(TCPLocationManager.this);
                location = null;
                if(client != null) {
                    client.disconnect();
                }
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

    /**
     * Unused.
     * @param provider
     */
    @Override
    public void onProviderDisabled(String provider) {
    }

    /**
     * Unused.
     * @param provider
     */
    @Override
    public void onProviderEnabled(String provider) {
    }

    /**
     * Unused.
     * @param provider
     * @param status
     * @param extras
     */
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }
}
