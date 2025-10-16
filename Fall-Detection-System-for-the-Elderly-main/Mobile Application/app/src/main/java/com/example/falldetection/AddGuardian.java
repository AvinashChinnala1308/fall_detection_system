package com.example.falldetection;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

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
import com.example.falldetection.models.Guardian;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;


public class AddGuardian extends Fragment {

    EditText gname,gphone,gemail,grelation;
    Button addg;
    DatabaseReference databaseReference;
    SharedPreferences sharedPreferences;
    public static final String MyPREFERENCES = "MyPrefs" ;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view=  inflater.inflate(R.layout.fragment_add_guardian, container, false);
        sharedPreferences = getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        databaseReference= DBCon.getRef().child("guardians");

        gname = (EditText)view.findViewById(R.id.gname);
        gphone = (EditText)view.findViewById(R.id.gphone);
        gemail = (EditText)view.findViewById(R.id.gemail);
        grelation = (EditText)view.findViewById(R.id.grelation);
        addg = (Button)view.findViewById(R.id.addg);
        addg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Guardian guardian= new Guardian();
                guardian.setId(databaseReference.push().getKey());
                guardian.setName(gname.getText().toString());
                guardian.setPhone(gphone.getText().toString());
                guardian.setEmail(gemail.getText().toString());
                guardian.setRelation(grelation.getText().toString());
                guardian.setUphone(sharedPreferences.getString("phone",""));
                if (TextUtils.isEmpty(guardian.getName()) && TextUtils.isEmpty(guardian.getEmail()) && TextUtils.isEmpty(guardian.getPhone()) && TextUtils.isEmpty(guardian.getRelation())) {
                    Toast.makeText(getContext(),"Please Fill All Fields",Toast.LENGTH_LONG).show();
                } else {
                    databaseReference.child(sharedPreferences.getString("kit_id","")).child(guardian.getId()).setValue(guardian).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getContext(),"Guardian Details Added Successfully",Toast.LENGTH_LONG).show();
                            FragmentManager fm=getActivity().getSupportFragmentManager();
                            fm.beginTransaction().replace(R.id.user_fragment_container,new WelcomeUser()).commit();
                        }
                    });
                }
            }
        });
        return view;
    }

}
