package com.example.falldetection;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.falldetection.Db.DBCon;
import com.example.falldetection.models.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class RegisterActivity extends AppCompatActivity {
    EditText etName;
    EditText etEmail;
    EditText etPhone;
    EditText etPassword;
    EditText kit_id;
    DatabaseReference databaseReference,databaseReference1,databaseReference2,databaseReference3,databaseReference4;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        databaseReference = DBCon.getRef().child("users");
        databaseReference1 = DBCon.getRef().child("guardians");
        databaseReference2 = DBCon.getRef().child("coordinates");
        databaseReference3 = DBCon.getRef().child("status");
        databaseReference4 = DBCon.getRef().child("tokens");
        etName=(EditText)findViewById(R.id.gname);
        etEmail=(EditText)findViewById(R.id.email);
        etPhone=(EditText)findViewById(R.id.phone);
        etPassword=(EditText)findViewById(R.id.password);
        kit_id=(EditText)findViewById(R.id.kit_id);

    }
    public void ureg(View view) {
        String name = etName.getText().toString();
        String email = etEmail.getText().toString();
        String phone = etPhone.getText().toString();
        String password = etPassword.getText().toString();
        final String kitId = kit_id.getText().toString();
        if (TextUtils.isEmpty(name) && TextUtils.isEmpty(name) && TextUtils.isEmpty(name) && TextUtils.isEmpty(name) && TextUtils.isEmpty(kitId)) {
            Toast.makeText(getApplicationContext(),"Please Fill All Fields",Toast.LENGTH_LONG).show();
        } else {
            final User user = new User();
            final String id=kitId;
            user.setName(name);
            user.setEmail(email);
            user.setPassword(password);
            user.setPhone(phone);
            user.setPhone_password(phone+"_"+password);
            user.setId(id);
            databaseReference1.child(kitId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    databaseReference2.child(kitId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            databaseReference3.child(kitId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    databaseReference4.child(kitId).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            databaseReference.child(id).setValue(user);
                                            Toast.makeText(getApplicationContext(),"Registration Success",Toast.LENGTH_LONG).show();
                                            Intent intent=new Intent(getApplicationContext(),MainActivity.class);
                                            startActivity(intent);
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });

                                }
                            });
                        }
                    });

                }
            });


        }
    }
}
