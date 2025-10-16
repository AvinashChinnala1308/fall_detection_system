package com.example.falldetection;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.falldetection.Db.DBCon;
import com.example.falldetection.models.CoOrdinates;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;


public class AddCoOrdinates extends Fragment {

    EditText xp,xn,yp,yn,zp,zn;
    Button addc;
    DatabaseReference databaseReference;
    SharedPreferences sharedPreferences;
    public static final String MyPREFERENCES = "MyPrefs" ;
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_co_ordinates, container, false);
        sharedPreferences = getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        databaseReference= DBCon.getRef().child("coordinates");
        xp = (EditText)view.findViewById(R.id.xp);
        xn = (EditText)view.findViewById(R.id.xn);
        yp = (EditText)view.findViewById(R.id.yp);
        yn = (EditText)view.findViewById(R.id.yn);
        zp = (EditText)view.findViewById(R.id.zp);
        zn = (EditText)view.findViewById(R.id.zn);
        addc = (Button)view.findViewById(R.id.addc);

        databaseReference.child(sharedPreferences.getString("kitId","")).addListenerForSingleValueEvent((new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    CoOrdinates coOrdinates = new CoOrdinates();
                    xp.setText(coOrdinates.getXp());
                    xn.setText(coOrdinates.getXn());
                    yp.setText(coOrdinates.getYp());
                    yn.setText(coOrdinates.getYn());
                    zp.setText(coOrdinates.getZp());
                    zn.setText(coOrdinates.getZn());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        }));


        addc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CoOrdinates coOrdinates = new CoOrdinates();
                coOrdinates.setId(sharedPreferences.getString("id",""));
                coOrdinates.setXp(xp.getText().toString());
                coOrdinates.setXn(xn.getText().toString());
                coOrdinates.setYp(yp.getText().toString());
                coOrdinates.setYn(yn.getText().toString());
                coOrdinates.setZp(zp.getText().toString());
                coOrdinates.setZn(zn.getText().toString());
                databaseReference.child(coOrdinates.getId()).setValue(coOrdinates).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getContext(),"Co Ordinates Updated",Toast.LENGTH_LONG).show();
                        FragmentManager fm=getActivity().getSupportFragmentManager();
                        fm.beginTransaction().replace(R.id.user_fragment_container,new WelcomeUser()).commit();
                    }
                });
            }
        });


        return view;
    }

}
