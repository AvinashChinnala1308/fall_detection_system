package com.example.falldetection.Db;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DBCon {
    public static DatabaseReference getRef(){
       return FirebaseDatabase.getInstance().getReference("FallDetection");
    }


}
