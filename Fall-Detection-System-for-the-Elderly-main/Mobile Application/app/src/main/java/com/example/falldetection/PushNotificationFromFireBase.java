package com.example.falldetection;

import android.app.PendingIntent;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class PushNotificationFromFireBase extends FirebaseMessagingService implements LocationListener {
    String title,body;
    public double latitude;
    public double longitude;
    public LocationManager locationManager;
    PendingIntent sentPI, deliveredPI;
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        if(remoteMessage.getNotification() != null) {
             title = remoteMessage.getNotification().getTitle();
             body = remoteMessage.getNotification().getBody();
             displayNotifications();

            Log.d("notificationnnnnnn",title+body);
          //  getLocation();

        }
    }
    void displayNotifications() {
        NotificationCompat.Builder mBuilder=new NotificationCompat.Builder(this,UserHomeActivity.CHANNEL_ID)
                .setSmallIcon(R.drawable.common_full_open_on_phone)
                .setContentTitle(title)
                .setContentText(body)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(1,mBuilder.build());
    }

    void getLocation() {
        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, this);
        }
        catch(SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(Location location) {

        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage("9666665154", null, "Current Location:" + location.getLatitude() +" "+ location.getLongitude(), sentPI, deliveredPI);
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
}
