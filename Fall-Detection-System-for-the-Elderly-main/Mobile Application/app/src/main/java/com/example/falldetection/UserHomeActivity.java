package com.example.falldetection;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import com.example.falldetection.Db.DBCon;
import com.example.falldetection.models.AppTocken;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;

public class UserHomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private AppBarConfiguration mAppBarConfiguration;
    SharedPreferences sharedPreferences;
    public static final String MyPREFERENCES = "MyPrefs" ;
    DatabaseReference databaseReference;
    String kit_id ;
    AppTocken appTocken;

    // 1. Notificatoin Channel
    // 2. Notification Builder
    // 3. Notification Manager
    public static final String CHANNEL_ID= "SmartPlant";
    private static final String CHANNEL_NAME = "SmartPlant";
    private static final String CHANNEL_DESC = "SmartPlant Description";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);
        sharedPreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        FragmentManager fm=getSupportFragmentManager();
        fm.beginTransaction().replace(R.id.user_fragment_container,new WelcomeUser()).commit();
        NavigationView navigationView = findViewById(R.id.nav_view);

        navigationView.setNavigationItemSelectedListener(this);
        databaseReference= DBCon.getRef().child("tokens");
        kit_id = sharedPreferences.getString("kit_id","");


        databaseReference.child(kit_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                 appTocken = dataSnapshot.getValue(AppTocken.class);

                    FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                        @Override
                        public void onComplete(@NonNull Task<InstanceIdResult> task) {
                            String token = task.getResult().getToken();
                            if (appTocken==null){
                            AppTocken appTocken1 = new AppTocken();
                            appTocken1.setId(kit_id);
                            appTocken1.setToken(token);
                            databaseReference.child(kit_id).setValue(appTocken1);
                            } else {
                                if (!appTocken.getToken().equalsIgnoreCase(token)) {
                                    AppTocken appTocken1 = new AppTocken();
                                    appTocken1.setId(kit_id);
                                    appTocken1.setToken(token);
                                    databaseReference.child(kit_id).setValue(appTocken1);
                                }
                            }
                        }
                    });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        if(Build.VERSION.SDK_INT >Build.VERSION_CODES.O) {
            NotificationChannel channel= new NotificationChannel(CHANNEL_ID,CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(CHANNEL_DESC);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.user_home, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        FragmentManager fm=getSupportFragmentManager();

        if (id == R.id.uhome) {
            fm.beginTransaction().replace(R.id.user_fragment_container,new WelcomeUser()).commit();
        }  else if (id == R.id.addg) {
            fm.beginTransaction().replace(R.id.user_fragment_container,new AddGuardian()).commit();
        } else if (id == R.id.viewg) {
            fm.beginTransaction().replace(R.id.user_fragment_container,new ViewGuardian()).commit();

        } else if (id == R.id.status) {
            fm.beginTransaction().replace(R.id.user_fragment_container,new Status()).commit();
        }else if (id == R.id.position) {
            fm.beginTransaction().replace(R.id.user_fragment_container,new AddCoOrdinates()).commit();
        }else if (id == R.id.logout) {

            Intent serviceIntent = new Intent(this, MyForeGroudService.class);
            stopService(serviceIntent);

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.commit();
            Intent intent=new Intent(getApplicationContext(),MainActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
