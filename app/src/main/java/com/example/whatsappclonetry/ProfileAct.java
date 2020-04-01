package com.example.whatsappclonetry;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.whatsappclonetry.Model.User;
import com.example.whatsappclonetry.Utils.Global;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileAct extends AppCompatActivity implements View.OnClickListener {

    private CircleImageView profile_image;
    private TextView txt_name;
    private TextView txt_status;
    private Button btn_request;
    private User user;
    private FirebaseAuth auth;
    private FirebaseUser currentUser;
    private DatabaseReference chatReqRef;
    private DatabaseReference contactRef;
    private String RequestId;
    private String chat_request;
    private LinearLayout container_check_req;
    private Button btn_cancel;
    private Button btn_accept;
    private boolean isAccept = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        initViews();
        User user = Global.user;
        txt_name.setText(user.getName());
        txt_status.setText(user.getStatus());
        Picasso.get()
                .load(user.getImage())
                .placeholder(R.drawable.person)
                .error(R.drawable.person)
                .into(profile_image);
        RequestId = user.getUid();
        chat_request = "new";
        container_check_req.setVisibility(View.GONE);
        if (currentUser.getUid().equals(RequestId)) {
            btn_request.setVisibility(View.GONE);
        } else {
            btn_request.setVisibility(View.VISIBLE);
        }

        checkRequest();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_request:
                if (btn_request.getText().equals("Send Request")) {
                    SendChatRequest();
                } else if (btn_request.getText().equals("Cancel Request")) {
                    CancelRequest();
                } else if (btn_request.getText().equals("Remove this Contact")) {
                    RemoveRequest();
                }
                break;
            case R.id.btn_cancel:
                CancelRequest();
                break;
            case R.id.btn_accept:
                isAccept = true;
                AcceptRequest();
                break;
        }
    }

    private void RemoveRequest() {
        contactRef.child(currentUser.getUid()).child(RequestId)
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    contactRef.child(RequestId).child(currentUser.getUid())
                            .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                container_check_req.setVisibility(View.GONE);
                                btn_request.setVisibility(View.VISIBLE);
                                btn_request.setText("Send Request");
                            } else {
                                Toast.makeText(ProfileAct.this, "Error " + task.getException(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(ProfileAct.this, "Error " + task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void AcceptRequest() {
        contactRef.child(currentUser.getUid()).child(RequestId).setValue("Saved")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            contactRef.child(RequestId).child(currentUser.getUid()).setValue("Saved")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                CancelRequest();
                                            } else {
                                                Toast.makeText(ProfileAct.this, "ERROR " + task.getException(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        } else {
                            Toast.makeText(ProfileAct.this, "ERROR " + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void checkRequest() {

        chatReqRef.child(currentUser.getUid()).child(RequestId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    HashMap<String, String> hashMap = (HashMap<String, String>) dataSnapshot.getValue();
                    if (hashMap.get("request_type").equals("sent")) {
                        btn_request.setText("Cancel Request");
                    } else if (hashMap.get("request_type").equals("recieved")) {
                        container_check_req.setVisibility(View.VISIBLE);
                        btn_request.setVisibility(View.GONE);
                    }
                } else {
                    contactRef.child(currentUser.getUid()).child(RequestId).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            if (dataSnapshot.exists()) {
                                btn_request.setText("Remove this Contact");
                            }else{
                                contactRef.child(RequestId).child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                        if (dataSnapshot.exists()) {
                                            btn_request.setText("Remove this Contact");
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void initViews() {
        btn_cancel = findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(this);
        btn_accept = findViewById(R.id.btn_accept);
        btn_accept.setOnClickListener(this);
        container_check_req = findViewById(R.id.container_check_req);
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        chatReqRef = FirebaseDatabase.getInstance().getReference().child("Chat Requests");
        profile_image = findViewById(R.id.profile_image);
        txt_name = findViewById(R.id.txt_name);
        txt_status = findViewById(R.id.txt_status);
        btn_request = findViewById(R.id.btn_request);
        btn_request.setOnClickListener(this);
        contactRef = FirebaseDatabase.getInstance().getReference().child("Contacts");
    }


    private void CancelRequest() {
        chatReqRef.child(currentUser.getUid()).child(RequestId)
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    chatReqRef.child(RequestId).child(currentUser.getUid())
                            .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                container_check_req.setVisibility(View.GONE);
                                btn_request.setVisibility(View.VISIBLE);
                                if (isAccept) {
                                    btn_request.setText("Remove this Contact");
                                } else {
                                    btn_request.setText("Send Request");
                                }
                            } else {
                                Toast.makeText(ProfileAct.this, "Error on Recieve Cancel " + task.getException(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(ProfileAct.this, "Error on Send Cancel" + task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void SendChatRequest() {
        chat_request = "sent";
        chatReqRef.child(currentUser.getUid()).child(RequestId).child("request_type")
                .setValue(chat_request).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    chat_request = "recieved";
                    chatReqRef.child(RequestId).child(currentUser.getUid()).child("request_type")
                            .setValue(chat_request).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                btn_request.setText("Cancel Request");
                            } else {
                                Toast.makeText(ProfileAct.this, "Error on Recieve " + task.getException(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(ProfileAct.this, "Error on Send " + task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
