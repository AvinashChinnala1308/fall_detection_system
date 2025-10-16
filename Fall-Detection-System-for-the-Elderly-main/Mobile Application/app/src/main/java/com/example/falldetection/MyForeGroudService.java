package com.example.falldetection;

import android.Manifest;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.falldetection.Db.DBCon;
import com.example.falldetection.models.Guardian;
import com.example.falldetection.models.KitStatus;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class MyForeGroudService extends Service {
    DatabaseReference databaseReference, databaseReference2;
    String kit_id, name;
    PendingIntent sentPI, deliveredPI;
    LocationManager mLocationManager;
    FusedLocationProviderClient client;
    Iterator<Guardian> guardianIterator;

    @Override
    public void onCreate() {
        super.onCreate();
        client = LocationServices.getFusedLocationProviderClient(this);

    }


    String getCurrentLocation() {

        try {
            LocationListener mLocationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    String latitude = Double.toString(location.getLatitude());
                    String longitude = Double.toHexString(location.getLongitude());
                    Geocoder gcd = new Geocoder(getBaseContext(),
                            Locale.getDefault());
                    List<Address> addresses;
                    try {
                        addresses = gcd.getFromLocation(location.getLatitude(),
                                location.getLongitude(), 1);
                        if (addresses.size() > 0) {
                            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                            String locality = addresses.get(0).getLocality();
                            String subLocality = addresses.get(0).getSubLocality();
                            String state = addresses.get(0).getAdminArea();
                            String country = addresses.get(0).getCountryName();
                            String postalCode = addresses.get(0).getPostalCode();
                            String knownName = addresses.get(0).getFeatureName();
                            String currentLocation = null;
                            if (subLocality != null) {

                                currentLocation = locality + "," + subLocality;
                            } else {

                                currentLocation = locality;
                            }
                            currentLocation = currentLocation + address;
                            Log.d("address", address);
                            Log.d("locality", locality);
                            Log.d("address", address);
                            Log.d("knownName", knownName);

                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

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
            };


        } catch (Exception e) {

        }

        return null;
    }

    @Override
    public int onStartCommand(final Intent intent, final int flags, int startId) {
        kit_id = intent.getStringExtra("kit_id");
        name = intent.getStringExtra("name");
        databaseReference = DBCon.getRef().child("status").child(kit_id);
        databaseReference2 = DBCon.getRef().child("guardians").child(kit_id);


        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this, App.CHANNEL_ID)
                .setContentTitle("Fall Detection System")
                .setContentText("Your Kit ID is " + kit_id)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(1, notification);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                KitStatus kitStatus = dataSnapshot.getValue(KitStatus.class);
                if (kitStatus != null) {
                    if (kitStatus.getStatus().equalsIgnoreCase("Danger")) {
                        databaseReference2.addValueEventListener(new ValueEventListener() {
                            List<Guardian> guardianList = new ArrayList<Guardian>();

                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot dataSnapshot2 : dataSnapshot.getChildren()) {
                                    Guardian guardian = dataSnapshot2.getValue(Guardian.class);
                                    guardianList.add(guardian);
                                }
                                guardianIterator = guardianList.iterator();

                                client.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                                    @Override
                                    public void onSuccess(Location location) {
                                        String latitude = Double.toString(location.getLatitude());
                                        String longitude = Double.toString(location.getLongitude());
                                        Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());

                                        List<Address> addresses;
                                        try {
                                            addresses = gcd.getFromLocation(location.getLatitude(),
                                                    location.getLongitude(), 1);
                                            if (addresses.size() > 0) {
                                                String address = addresses.get(0).getAddressLine(0);
                                                String message = name.toUpperCase() + " is in Emergency at " + address + ". for live location follow";
                                                String message2 = "https://www.google.com/maps/@" + latitude + "," + longitude + ",20z";

                                                while (guardianIterator.hasNext()) {
                                                  Log.d("pppp",message);
                                                    Guardian guardian2 = guardianIterator.next();
                                                    SmsManager sms = SmsManager.getDefault();
                                                    sms.sendTextMessage(guardian2.getPhone(), null, message, sentPI, deliveredPI);
                                                    sms.sendTextMessage(guardian2.getPhone(), null, message2, sentPI, deliveredPI);
                                                }
                                                guardianIterator = guardianList.iterator();
                                                while (guardianIterator.hasNext()) {

                                                    Guardian guardian2 = guardianIterator.next();
                                                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                                                    callIntent.setData(Uri.parse("tel:" + guardian2.getPhone()));
                                                    Log.d("aaa",guardian2.getPhone());
                                                    callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    if (checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                                        return;
                                                    }
                                                    startActivity(callIntent);
                                                    Thread.sleep(50000);

                                                }

                                            }

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                } else {
                    Intent notificationIntent = new Intent(getApplicationContext(), UserHomeActivity.class);
                    PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),
                            0, notificationIntent, 0);
                    Notification notification = new NotificationCompat.Builder(getApplicationContext(), App.CHANNEL_ID)
                            .setContentTitle("Fall Detection System")
                            .setContentText("Your Kit Status Not Available")
                            .setSmallIcon(R.drawable.ic_launcher_background)
                            .setContentIntent(pendingIntent)
                            .build();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        //do heavy work on a background thread
        //stopSelf();

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
