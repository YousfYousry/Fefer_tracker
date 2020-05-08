package com.example.fevertracker;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class profileForAdminFragment extends Fragment implements PopupMenu.OnMenuItemClickListener {
    TextView name, email, phone, adress, passport, cancel, nameE, emailE, phoneE, adressE, passportE, StatusText, status, timeType, timeinterval;
    Button Edit, GPStracker;
    String Id, Time = "5 Second";
    EditText timeIntervalE;
    boolean init = false;
    boolean editMode = false;
    LinearLayout EditeMode, PreviewMode;
    Context context;
    int statuss = 1;
    boolean popUp = true;

    public void setContext(Context context) {
        this.context = context;
    }

    public void setId(String id) {
        Id = id;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.profile_fragment_for_admin, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (Id != null && !Id.isEmpty() && getView() != null) {
            init = true;
//            timeType = getView().findViewById(R.id.button5);
            timeinterval = getView().findViewById(R.id.timeinterval);
            timeIntervalE = getView().findViewById(R.id.timeIntervalE);
            StatusText = getView().findViewById(R.id.Status);
            status = getView().findViewById(R.id.StatusE);
            cancel = getView().findViewById(R.id.textView);
            Edit = getView().findViewById(R.id.button);
            name = getView().findViewById(R.id.Name);
            email = getView().findViewById(R.id.Email);
            phone = getView().findViewById(R.id.phone);
            adress = getView().findViewById(R.id.address);
            passport = getView().findViewById(R.id.passport);
            nameE = getView().findViewById(R.id.NameE);
            emailE = getView().findViewById(R.id.EmailE);
            phoneE = getView().findViewById(R.id.phoneE);
            adressE = getView().findViewById(R.id.addressE);
            passportE = getView().findViewById(R.id.passportE);
            EditeMode = getView().findViewById(R.id.EditMode);
            PreviewMode = getView().findViewById(R.id.PreviewMode);
            Edit.setVisibility(View.VISIBLE);
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Edit.setText("Edit");
                    EditeMode.setVisibility(View.GONE);
                    PreviewMode.setVisibility(View.VISIBLE);
                    editMode = false;
                }
            });
//            timeType.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    showPopUp2(v);
//                }
//            });
            status.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showPopUp(v);
                }
            });
            Edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!Id.isEmpty()) {
                        if (!editMode) {
                            Edit.setText("Save");
                            EditeMode.setVisibility(View.VISIBLE);
                            PreviewMode.setVisibility(View.GONE);
                            editMode = true;
                        } else {
                            DatabaseReference reff = FirebaseDatabase.getInstance().getReference().child("Member").child(Id);
                            reff.child("name").setValue(nameE.getText().toString());
                            reff.child("email").setValue(emailE.getText().toString());
                            reff.child("phone").setValue(phoneE.getText().toString());
                            reff.child("address").setValue(adressE.getText().toString());
                            reff.child("passport").setValue(passportE.getText().toString());
                            reff.child("state").setValue(Integer.toString(statuss));
                            DatabaseReference reff2 = FirebaseDatabase.getInstance().getReference().child("adminInfo");
                            reff2.child("TimeInterval").setValue(timeIntervalE.getText().toString());
                            Toast.makeText(context, "Data is saved successfully", Toast.LENGTH_LONG).show();
                            Edit.setText("Edit");
                            EditeMode.setVisibility(View.GONE);
                            PreviewMode.setVisibility(View.VISIBLE);
                            editMode = false;
                        }
                    } else {
                        Toast.makeText(getContext(), "Please get Id first", Toast.LENGTH_LONG).show();
                    }
                }

            });
            DatabaseReference reff = FirebaseDatabase.getInstance().getReference().child("Member").child(Id);
            reff.addValueEventListener(new

                                               ValueEventListener() {
                                                   @Override
                                                   public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                       if (dataSnapshot.child("name").getValue() != null) {
                                                           name.setText(dataSnapshot.child("name").getValue().toString());
                                                           email.setText(dataSnapshot.child("email").getValue().toString());
                                                           phone.setText(dataSnapshot.child("phone").getValue().toString());
                                                           adress.setText(dataSnapshot.child("address").getValue().toString());
                                                           passport.setText(dataSnapshot.child("passport").getValue().toString());
                                                           nameE.setText(dataSnapshot.child("name").getValue().toString());
                                                           emailE.setText(dataSnapshot.child("email").getValue().toString());
                                                           phoneE.setText(dataSnapshot.child("phone").getValue().toString());
                                                           adressE.setText(dataSnapshot.child("address").getValue().toString());
                                                           passportE.setText(dataSnapshot.child("passport").getValue().toString());
                                                           statuss = Integer.parseInt(dataSnapshot.child("state").getValue().toString());
                                                           setButtonStatus(statuss);
                                                       }
                                                   }

                                                   @Override
                                                   public void onCancelled(@NonNull DatabaseError databaseError) {
                                                   }
                                               });

            DatabaseReference reffAdmin = FirebaseDatabase.getInstance().getReference().child("adminInfo");
            reffAdmin.addValueEventListener(new

                                                    ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                            if (dataSnapshot.child("TimeInterval").getValue() != null) {
                                                                timeinterval.setText("Time interval for GPS: " + dataSnapshot.child("TimeInterval").getValue().toString() + " Seconds");
                                                                timeIntervalE.setText(dataSnapshot.child("TimeInterval").getValue().toString());
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                                        }
                                                    });
        } else {
            name = getView().findViewById(R.id.Name);
            email = getView().findViewById(R.id.Email);
            phone = getView().findViewById(R.id.phone);
            adress = getView().findViewById(R.id.address);
            passport = getView().findViewById(R.id.passport);
            name.setText("Scan Qr first");
            email.setText("Scan Qr first");
            phone.setText("Scan Qr first");
            adress.setText("Scan Qr first");
            passport.setText("Scan Qr first");
        }
    }

    public void showPopUp(View view) {
        popUp = true;
        PopupMenu popup = new PopupMenu(context, view);
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.menu_pop_up);
        popup.show();
    }

    public void showPopUp2(View view) {
        popUp = false;
        PopupMenu popup = new PopupMenu(context, view);
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.menu_time_pop_up);
        popup.show();
    }

    public void setButtonStatus(int statuss) {
        if (statuss == 1) {
            status.setText("Not infected");
            StatusText.setText("Not infected");
        } else if (statuss == 2) {
            status.setText("suspicious case");
            StatusText.setText("suspicious case");
        } else if (statuss == 3) {
            status.setText("Infected with corona");
            StatusText.setText("Infected with corona");
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (popUp) {
            switch (item.getItemId()) {
                case R.id.item1:
                    statuss = 1;
                    status.setText("Not infected");
                    return true;
                case R.id.item2:
                    statuss = 2;
                    status.setText("suspicious case");
                    return true;
                case R.id.item3:
                    statuss = 3;
                    status.setText("Infected with corona");
                    return true;
                default:
                    return false;
            }
        } else {
            switch (item.getItemId()) {
                case R.id.item1:
                    statuss = 1;
                    timeType.setText("Second");
                    return true;
                case R.id.item2:
                    statuss = 2;
                    timeType.setText("Minute");
                    return true;
                case R.id.item3:
                    statuss = 3;
                    timeType.setText("Hour");
                    return true;
                default:
                    return false;
            }
        }
    }
}
