package com.example.demo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

//import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static android.location.LocationManager.NETWORK_PROVIDER;

//import android.content.Context;
//import android.content.Intent;
//import android.provider.Settings;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, SensorEventListener {
    Date date = new Date();
    FileWriter outputfile;
    CSVWriter writer;
    File journeyfile;
    boolean flag;
    GoogleMap gMap;
    LatLng myPos, tempLatlng;
    private SensorManager sensorManager;
    Sensor accelerometer;
    private TextView gValues;
    LocationListener locationListener;
    LocationManager locationManager;
    ArrayList<Double> zvals;
    static Location location;
    Marker marker1;
    CameraUpdate cameraUpdate;
    ToggleButton tracking_btn;
   private FusedLocationProviderClient fusedLocationClient;
  //  LocationRequest mLocationRequest;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        gValues = findViewById(R.id.actualvalue);
        tracking_btn = findViewById(R.id.button2);
        tracking_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tracking_btn.isChecked()) {
                    createfile();
                    initializewriters();
                    flag = true;
                } else {
                    if (flag) {
                        try {
                            writer.close();
                            Toast.makeText(getBaseContext(), "Data saved", Toast.LENGTH_LONG).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
        zvals = new ArrayList<>();
        sensorManager.registerListener(MainActivity.this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.INTERNET
                }, 10);
            }
            onRestart();
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
       if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1, new android.location.LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                        int line_colour = calculateAverage();
                        set_to_zero();
                        myPos = new LatLng(location.getLatitude(), location.getLongitude());
                        String temp = "Location Changed to = " + +myPos.latitude + " " + myPos.longitude;
                        // gMap();
                        marker1.remove();
                        marker1 = gMap.addMarker(new MarkerOptions().position(myPos).title(temp));
                        Polyline line = gMap.addPolyline(new PolylineOptions()
                                .add(tempLatlng, myPos).width(15f).color(line_colour));
                        CameraPosition target = CameraPosition.builder().target(myPos).tilt(65).zoom(17).bearing(180).build();
                        //  gMap.moveCamera(CameraUpdateFactory.newCameraPosition(target));
                        cameraUpdate = CameraUpdateFactory.newCameraPosition(target);
                        gMap.animateCamera(cameraUpdate);

                        tempLatlng = new LatLng(myPos.latitude, myPos.longitude);




                 /*   myPos = new LatLng(location.getLatitude(), location.getLongitude());
                    String temp = "Location Changed to = " + +myPos.latitude + " " + myPos.longitude;
                    // gMap();
                    marker1.remove();
                    marker1 = gMap.addMarker(new MarkerOptions().position(myPos).title(temp));
                    Polyline line = gMap.addPolyline(new PolylineOptions()
                            .add(tempLatlng, myPos).width(15f).color(Color.RED));

                    CameraPosition target = CameraPosition.builder().target(myPos).tilt(65).zoom(17).bearing(180).build();
                    // gMap.moveCamera(CameraUpdateFactory.newCameraPosition(target));
                    cameraUpdate = CameraUpdateFactory.newCameraPosition(target);
                    gMap.animateCamera(cameraUpdate);
                    tempLatlng = new LatLng(myPos.latitude, myPos.longitude);
                    */


                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            });
        }
        /*
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,0, new android.location.LocationListener() {
                @Override
                public void onLocationChanged(Location location) {

                        int line_colour = calculateAverage();
                        set_to_zero();
                        myPos = new LatLng(location.getLatitude(), location.getLongitude());
                        String temp = "Location Changed to = " + +myPos.latitude + " " + myPos.longitude;
                        // gMap();
                        marker1.remove();
                        marker1 = gMap.addMarker(new MarkerOptions().position(myPos).title(temp));
                        Polyline line = gMap.addPolyline(new PolylineOptions()
                                .add(tempLatlng, myPos).width(15f).color(line_colour));
                        CameraPosition target = CameraPosition.builder().target(myPos).tilt(65).zoom(17).bearing(180).build();
                        //  gMap.moveCamera(CameraUpdateFactory.newCameraPosition(target));
                        cameraUpdate = CameraUpdateFactory.newCameraPosition(target);
                        gMap.animateCamera(cameraUpdate);

                        tempLatlng = new LatLng(myPos.latitude, myPos.longitude);

                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            });
        }*/
        // location = new locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                requestPermissions(new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.INTERNET
                }, 10);
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                //return;
            }
        } else {
            /*locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, (android.location.LocationListener) locationListener);
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if(location==null)
            {
                Toast.makeText(getApplicationContext(),"network not acquired",Toast.LENGTH_LONG).show();
            }*/
            configureLocation();

        }
         Criteria criteria = new Criteria();
        String bestprovider = String.valueOf(locationManager.getBestProvider(criteria,true)).toString();
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        location = new Location(NETWORK_PROVIDER);
        try {
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
             fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                 @Override
                 public void onSuccess(Location location1) {
                     if(location!=null)
                     {
                         location =location1;
                     }
                 }

             });
        }catch (NullPointerException e)
        {
            locationManager.requestLocationUpdates(bestprovider,1000,0, (LocationListener) MainActivity.this);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,1000,1,locationListener);
        }
// important location object


    }


    @SuppressLint("MissingPermission")
    private void configureLocation() {
        //locationManager.requestLocationUpdates("gps", 1000, 0, locationListener);
        //   locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (android.location.LocationListener) locationListener);

        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1, (android.location.LocationListener) locationListener);
        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        Toast.makeText(getApplicationContext(), "net acquired", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 10:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    configureLocation();

                }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        gMap = googleMap;

        //   String temp = "on mapreadylat,lng = " + location.getLatitude() + " " + location.getLongitude();
        //   Toast.makeText(getApplicationContext(), temp, Toast.LENGTH_SHORT).show();
        // CameraPosition target = CameraPosition.builder().target(myPos).tilt(65).zoom(17).bearing(180).build();
        // gMap.moveCamera(CameraUpdateFactory.newCameraPosition(target));
        zvals = new ArrayList<>();
        myPos = new LatLng(location.getLatitude(), location.getLongitude());
        tempLatlng = new LatLng(myPos.latitude, myPos.longitude);
        gMap.getUiSettings().setMapToolbarEnabled(false); // disables the toolbar that appears on clicking the marker
        marker1 = gMap.addMarker(new MarkerOptions()
                .position(myPos)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                .title("" + location.getLatitude() + "," + location.getLongitude())
                .alpha(1f));
        CameraPosition target = CameraPosition.builder().target(myPos).tilt(65).zoom(17).bearing(180).build();
        gMap.moveCamera(CameraUpdateFactory.newCameraPosition(target));

    }

double sumzacc=0;
    int no_of_vals=0;
    @Override
    public void onSensorChanged(SensorEvent event) {
        //zvals.add((double)event.values[2]);
        String gval = "" + event.values[2];
        gValues.setText(gval);
        //acc.calculateIRI(zvals);
        if (flag) {
            //initializewriters();
            writeDataLineByLine(myPos.latitude, myPos.longitude, event.values[2]);
            zvals.add((double)Math.abs(event.values[2]));
            sumzacc+=(double)Math.abs(event.values[2]);
            no_of_vals++;
        }
    }
    public int calculateAverage()
    {
        int quality_level;
        Color color = null;
        double avg = sumzacc/no_of_vals;
        if(avg<=1)
        {
            quality_level=1;
            return Color.GREEN;

        }
        else if(avg>1&& avg<=2)
        {
            quality_level=2;
            return Color.BLUE;
        }
        else
        {
           quality_level=3;
            return Color.RED;
        }
      //  return quality_level;
    }
     public void set_to_zero()
     {
         sumzacc=0;
         no_of_vals=0;
         zvals.clear();
     }

    private void initializewriters() {
        // create FileWriter object with file as parameter
        try {
            outputfile = new FileWriter(journeyfile);
            // create CSVWriter object filewriter object as parameter
            writer = new CSVWriter(outputfile);

            // adding header to csv
            String[] header = {"Lat", "Lng", "Zacc", "Timestamp"};
            writer.writeNext(header);

        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    public void writeDataLineByLine(double lat, double lng, double zacc) {
        Calendar now = Calendar.getInstance();
        int hour = now.get(Calendar.HOUR_OF_DAY);
        int minute = now.get(Calendar.MINUTE);
        int second = now.get(Calendar.SECOND);
        String timestamp = hour + ":" + minute + ":" + second;

        // first create file object for file placed at location
        // specified by filepath
        // File file = new File(filePath);
        // create FileWriter object with file as parameter
        //    outputfile = new FileWriter(journeyfile);

        // create CSVWriter object filewriter object as parameter
        //    writer = new CSVWriter(outputfile);


        // add data to csv
        String[] mydata = {lat + "", lng + "", zacc + "" + timestamp};

        writer.writeNext(mydata);


    }

    public String createfile() {
        File sdCard = Environment.getExternalStorageDirectory();
        File dir = new File(sdCard.getAbsolutePath() + "/project");
        Boolean dirsMade = dir.mkdir();
        //System.out.println(dirsMade);
        Log.v("Accel", dirsMade.toString());

        journeyfile = new File(dir, "journey " + date + ".csv");
        try {
            FileOutputStream f = new FileOutputStream(journeyfile, true);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        //File file = new File();
        return journeyfile.getAbsolutePath();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


}

