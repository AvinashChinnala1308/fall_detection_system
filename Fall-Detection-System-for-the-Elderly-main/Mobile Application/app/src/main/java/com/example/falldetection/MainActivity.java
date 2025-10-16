package com.example.falldetection;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.falldetection.Broad.FirebaseBackgroundService;
import com.example.falldetection.Db.DBCon;
import com.example.falldetection.models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;


public class MainActivity extends AppCompatActivity implements LocationListener {
    EditText etPhone;
    EditText etPasword;
    TextView newUser;
    Button buttonLogin;
    String phone, password;
    DatabaseReference databaseReference;
    SharedPreferences sharedPreferences;

    public static final String MyPREFERENCES = "MyPrefs";

    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 1;
    String permissions[] = {Manifest.permission.SEND_SMS};

    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 2;
    String permissions2[] = {Manifest.permission.ACCESS_FINE_LOCATION};

    private static final int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 3;
    String permissions3[] = {Manifest.permission.ACCESS_COARSE_LOCATION};

    private static final int MY_PERMISSIONS_REQUEST_ACCESS_CALL_PHONE = 4;
    String permissions4[] = {Manifest.permission.CALL_PHONE};


    public double latitude;
    public double longitude;
    public LocationManager locationManager;


    PendingIntent sentPI, deliveredPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        etPhone = (EditText) findViewById(R.id.phone);
        etPasword = (EditText) findViewById(R.id.password);
        newUser = (TextView) findViewById(R.id.new_user);
        buttonLogin = (Button) findViewById(R.id.login_btn);
        databaseReference = DBCon.getRef().child("users");
        sharedPreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        if (sharedPreferences.contains("kit_id")) {
            Intent intent = new Intent(this,UserHomeActivity.class);
            startActivity(intent);
        }
        sendSmsPermisson();

        Intent serviceIntent = new Intent(getApplicationContext(), FirebaseBackgroundService.class);
        getApplicationContext().startService(serviceIntent);

        newUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
            }
        });
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                phone = etPhone.getText().toString();
                password = etPasword.getText().toString();
                if (TextUtils.isEmpty(phone) && TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Please fill the Abovw Feilds", Toast.LENGTH_SHORT).show();
                } else {
                    databaseReference.orderByChild("phone_password").equalTo(phone + "_" + password).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                                    User customer = childSnapshot.getValue(User.class);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString("id", customer.getId());
                                    editor.putString("name", customer.getName());
                                    editor.putString("email", customer.getEmail());
                                    editor.putString("phone", customer.getPhone());
                                    editor.putString("role", "user");
                                    editor.putString("kit_id", customer.getId());
                                    editor.commit();
                                    Intent serviceIntent = new Intent(getApplicationContext(), MyForeGroudService.class);
                                    serviceIntent.putExtra("kit_id",customer.getId());
                                    serviceIntent.putExtra("name",customer.getName());
                                    ContextCompat.startForegroundService(getApplicationContext(), serviceIntent);
                                    Intent intent = new Intent(getApplicationContext(), UserHomeActivity.class);
                                    startActivity(intent);
                                }


                            } else {
                                Toast.makeText(getApplicationContext(), "Invalid Login Details", Toast.LENGTH_SHORT).show();
                                etPasword.setText("");
                                etPhone.setText("");

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }
            }
        });

    }


    void sendSmsPermisson() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, permissions, MY_PERMISSIONS_REQUEST_SEND_SMS);
            }
        }
        callPhone();
    }

    void accessLocation() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, permissions2, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            } else {
                locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, (LocationListener) MainActivity.this);

            }
        }
        accessCoarseLocation();
    }
    void accessCoarseLocation() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, permissions3, MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
            }
        }
    }
    void callPhone() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, permissions4, MY_PERMISSIONS_REQUEST_ACCESS_CALL_PHONE);
            }
        }
        accessLocation();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_SEND_SMS: {
                for (int i = 0; i < permissions.length; i++) {
                    String permission = permissions[i];
                    if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        boolean showRationale = ActivityCompat.shouldShowRequestPermissionRationale(this, permission);
                        if (showRationale) {
                            Toast.makeText(getApplicationContext(), "Request Rejected Once You can Ask Again", Toast.LENGTH_LONG).show();

                        } else {
                            Toast.makeText(getApplicationContext(), "Never Ask Again", Toast.LENGTH_LONG).show();
                        }
                    }
                    accessLocation();
                }
            }
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                for (int i = 0; i < permissions.length; i++) {
                    String permission = permissions[i];
                    if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        boolean showRationale = ActivityCompat.shouldShowRequestPermissionRationale(this, permission);
                        if (showRationale) {
                            Toast.makeText(getApplicationContext(), "Request Rejected Once You can Ask Again", Toast.LENGTH_LONG).show();

                        } else {
                            Toast.makeText(getApplicationContext(), "Never Ask Again", Toast.LENGTH_LONG).show();
                        }

                    }
                }
            }

            case MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION: {
                for (int i = 0; i < permissions.length; i++) {
                    String permission = permissions[i];
                    if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        boolean showRationale = ActivityCompat.shouldShowRequestPermissionRationale(this, permission);
                        if (showRationale) {
                            Toast.makeText(getApplicationContext(), "Request Rejected Once You can Ask Again", Toast.LENGTH_LONG).show();

                        } else {
                            Toast.makeText(getApplicationContext(), "Never Ask Again", Toast.LENGTH_LONG).show();
                        }

                    }
                }
            }
            case MY_PERMISSIONS_REQUEST_ACCESS_CALL_PHONE: {
                for (int i = 0; i < permissions.length; i++) {
                    String permission = permissions[i];
                    if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        boolean showRationale = ActivityCompat.shouldShowRequestPermissionRationale(this, permission);
                        if (showRationale) {
                            Toast.makeText(getApplicationContext(), "Request Rejected Once You can Ask Again", Toast.LENGTH_LONG).show();

                        } else {
                            Toast.makeText(getApplicationContext(), "Never Ask Again", Toast.LENGTH_LONG).show();
                        }
                        sendSmsPermisson();
                    }
                }
            }

        }
    }


    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }
}





