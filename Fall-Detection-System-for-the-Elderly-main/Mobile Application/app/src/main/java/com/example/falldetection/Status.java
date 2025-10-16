package com.example.falldetection;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.falldetection.Db.DBCon;
import com.example.falldetection.models.KitStatus;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;


public class Status extends Fragment {
    DatabaseReference databaseReference;
    SharedPreferences sharedPreferences;
    public static final String MyPREFERENCES = "MyPrefs" ;
    TextView status;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_status, container, false);
        status = (TextView)view.findViewById(R.id.status);
        sharedPreferences = getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        databaseReference= DBCon.getRef().child("status");
        databaseReference.child(sharedPreferences.getString("kit_id","")).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    KitStatus kitStatus= dataSnapshot.getValue(KitStatus.class);
                    status.setText(kitStatus.getStatus());
                    if (kitStatus.getStatus().equalsIgnoreCase("Danger")){
                        status.setTextColor(Color.RED);
                    } else {
                        status.setTextColor(Color.GREEN);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return view;
    }

}
