package com.example.falldetection;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.falldetection.Db.DBCon;
import com.example.falldetection.models.Guardian;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class ViewGuardian extends Fragment {
    DatabaseReference databaseReference;
    SharedPreferences sharedPreferences;
    public static final String MyPREFERENCES = "MyPrefs" ;
    ListView guardians_list;
    List<Guardian> guardianList;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_view_guardian, container, false);
        sharedPreferences = getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        databaseReference= DBCon.getRef().child("guardians").child(sharedPreferences.getString("kit_id",""));
        guardians_list = (ListView)view.findViewById(R.id.guardians_list);
        databaseReference.orderByChild("uphone").equalTo(sharedPreferences.getString("phone","")).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                guardianList = new ArrayList<Guardian>();
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    Guardian guardian = childSnapshot.getValue(Guardian.class);
                    guardianList.add(guardian);
                }
                CustomAdoptor customAdoptor = new CustomAdoptor();
                guardians_list.setAdapter(customAdoptor);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return view;
    }
    class CustomAdoptor extends BaseAdapter {

        @Override
        public int getCount() {
            return guardianList.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {
            view = getLayoutInflater().inflate(R.layout.guardians_list,null);
            final TextView gname=(TextView)view.findViewById(R.id.gname);
            final TextView gphone=(TextView)view.findViewById(R.id.gphone);
            final TextView gemail=(TextView)view.findViewById(R.id.gemail);
            final TextView grelation=(TextView)view.findViewById(R.id.grelation);

            gname.setText(guardianList.get(i).getName());
            gphone.setText(guardianList.get(i).getPhone());
            gemail.setText(guardianList.get(i).getEmail());
            grelation.setText(guardianList.get(i).getRelation());
            return view;
        }
    }
}
