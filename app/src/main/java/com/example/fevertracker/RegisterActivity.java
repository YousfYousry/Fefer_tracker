package com.example.fevertracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    public static final String TAG = "TAG";
    long maxid=1;
    TextView Title;
    TextInputLayout name, phone, address, passport, pass, confirmpass, email;
    TextInputEditText nameE, phoneE, addressE, passportE, passE, confirmpassE, emailE;
    DatabaseReference reff;
    Member member;
    Button reglog;
    Boolean register = true;
    FirebaseAuth fAuth;
    ProgressBar progressBar;
    FirebaseFirestore fStore;
    String userID;
    boolean fail = false;
    String AdminName = "",AdminPassword = "";
    int PERMISSION_ID = 44;
    public static final String SHARED_PREFS = "sharedPrefs";
    DocumentReference noteRef;


    public void Register(View view){
        if(checkPermissions()) {
            if (register) {
                final String name = nameE.getText().toString().trim();
                final String email = emailE.getText().toString().trim();
                final String phone = phoneE.getText().toString().trim();
                final String address = addressE.getText().toString().trim();
                final String passport = passportE.getText().toString().trim();
                String password = passE.getText().toString().trim();
                String ConfinrmPassword = confirmpassE.getText().toString().trim();

                if (TextUtils.isEmpty(name)) {
                    nameE.setError("Name is Required.");
                    return;
                }

                if (TextUtils.isEmpty(email)) {
                    emailE.setError("Email address is Required.");
                    return;
                }

                if (TextUtils.isEmpty(phone)) {
                    phoneE.setError("Phone is Required.");
                    return;
                }

                if (TextUtils.isEmpty(address)) {
                    addressE.setError("Address is Required.");
                    return;
                }

                if (TextUtils.isEmpty(passport)) {
                    passportE.setError("Passport number is Required.");
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    passE.setError("Password is Required.");
                    return;
                }

                if (password.length() < 6) {
                    passE.setError("Password Must be >= 6 Characters");
                    return;
                }

                if (password.compareTo(ConfinrmPassword) != 0) {
                    confirmpassE.setError("Password is not similar.");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                // register the user in firebase

                fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this, "User Created.", Toast.LENGTH_SHORT).show();
                            userID = fAuth.getCurrentUser().getUid();
                            DocumentReference documentReference = fStore.collection("users").document(userID);
                            Map<String, Object> user = new HashMap<>();

                            ArrayList<String> tracker = new ArrayList<>();

                            member = new Member();
                            member.setName(name);
                            member.setEmail(email);
                            member.setPhone(phone);
                            member.setAddress(address);
                            member.setPassport(passport);
                            member.setState("1");
                            member.setTracker(tracker);
                            reff.child(String.valueOf(maxid + 1)).setValue(member);
                            user.put("Id", Long.toString(maxid + 1));
                            saveData(Long.toString(maxid+1),"Id");

                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    fail = false;
                                    Log.d(TAG, "onSuccess: user Profile is created for " + userID);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "onFailure: " + e.toString());
                                    progressBar.setVisibility(View.GONE);
                                    fail = true;
                                }
                            });
                            if (!fail) {
                                startActivity(new Intent(getApplicationContext(), LocationActivity.class));
                                finish();
                            }
                        } else {
                            Toast.makeText(RegisterActivity.this, "Error ! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }


                });
            } else {

                final String email = emailE.getText().toString().trim();
                String password = passE.getText().toString().trim();


                if (TextUtils.isEmpty(email)) {
                    emailE.setError("Email is Required.");
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    passE.setError("Password is Required.");
                    return;
                }

                if (password.compareTo(AdminPassword) == 0 && email.compareTo(AdminName) == 0) {
                    startActivity(new Intent(getApplicationContext(), admin.class));
                    finish();
                    return;
                }

                if (password.length() < 6) {
                    passE.setError("Password Must be >= 6 Characters");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                // authenticate the user

                fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            saveId();
                        } else {
                            Toast.makeText(RegisterActivity.this, "Error ! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }

                    }
                });
            }
        }else{
            Toast.makeText(this,"Please accept permission first.",Toast.LENGTH_LONG).show();
        }
    }
    public void Login(View view){
        if(register) {
            Title.setText("Login");
            name.setVisibility(View.GONE);
            phone.setVisibility(View.GONE);
            address.setVisibility(View.GONE);
            passport.setVisibility(View.GONE);
            confirmpass.setVisibility(View.GONE);
            email.setHint("Email address");
            pass.setHint("Password");
            reglog.setText("Register");
            register = false;

}else{
            Title.setText("Registration");
            name.setVisibility(View.VISIBLE);
            phone.setVisibility(View.VISIBLE);
            address.setVisibility(View.VISIBLE);
            passport.setVisibility(View.VISIBLE);
            confirmpass.setVisibility(View.VISIBLE);
            email.setHint("Email address (Must remember)");
            pass.setHint("Password (Must remember)");
            reglog.setText("Login");
            register = true;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(!checkPermissions()){
            requestPermissions();
        }

        iniFunc();
        if(checkPermissions()) {
            if(!loadData("log").isEmpty()){
                startActivity(new Intent(getApplicationContext(), admin.class));
                finish();
            }
            if (fAuth.getCurrentUser() != null) {
                startActivity(new Intent(getApplicationContext(), LocationActivity.class));
                finish();
            }
        }
    }
    public String loadData(String name) {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        if (sharedPreferences == null) {
            return "";
        }
        return sharedPreferences.getString(name, "");
    }
    public void iniFunc(){
        Title = findViewById(R.id.RegistrationTitle);
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        phone = findViewById(R.id.phone);
        address = findViewById(R.id.address);
        passport = findViewById(R.id.passport);
        pass = findViewById(R.id.pass);
        confirmpass = findViewById(R.id.conPass);
        reglog = findViewById(R.id.button2);
        nameE = findViewById(R.id.username);
        emailE = findViewById(R.id.emailadress);
        phoneE = findViewById(R.id.phoneNum);
        addressE = findViewById(R.id.HomeAddress);
        passportE = findViewById(R.id.passport_number);
        passE = findViewById(R.id.password);
        confirmpassE = findViewById(R.id.confirmpassword);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        progressBar = findViewById(R.id.progressBar);
        reff= FirebaseDatabase.getInstance().getReference().child("Member");
        reff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                maxid=dataSnapshot.getChildrenCount();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        DatabaseReference reff = FirebaseDatabase.getInstance().getReference().child("adminInfo");
        reff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                AdminName = dataSnapshot.child("adminName").getValue().toString();
                AdminPassword = dataSnapshot.child("adminPass").getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }
    private void requestPermissions() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_ID
        );
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission accepted", Toast.LENGTH_SHORT).show();
                if (fAuth.getCurrentUser() != null) {
                    startActivity(new Intent(getApplicationContext(), LocationActivity.class));
                    finish();
                }
            }else{
                finish();
            }
        } else {
            finish();
        }
    }
    public void saveData(String data, String name) {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(name, data);
        editor.apply();
    }
    public void saveId(){
        String userID = fAuth.getCurrentUser().getUid();
        noteRef = fStore.collection("users").document(userID);
        noteRef.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                if (e != null) {
                    Toast.makeText(RegisterActivity.this, "Error while loading!", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, e.toString());
                    FirebaseAuth.getInstance().signOut();//logout
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                if (documentSnapshot.exists()) {
                    String Id = documentSnapshot.getString("Id");
                    saveData(Id, "Id");
                    Toast.makeText(RegisterActivity.this, "Logged in Successfully", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), LocationActivity.class));
                    finish();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}


