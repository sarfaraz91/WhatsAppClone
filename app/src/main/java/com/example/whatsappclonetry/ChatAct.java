package com.example.whatsappclonetry;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.whatsappclonetry.Adapter.MessageAdap;
import com.example.whatsappclonetry.Model.Message;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatAct extends AppCompatActivity implements View.OnClickListener {

    EditText txt_chat;
    Button btn_send;
    private DatabaseReference databaseReference;
    private FirebaseAuth auth;
    private FirebaseUser currentUser;
    private ListView listview;
    private ArrayList<Message> messageArrayList;
    private MessageAdap messageAdap;

    private Toolbar main_page_toolbar;
    private TextView lbl_name;
    private TextView lbl_last_scene;
    private CircleImageView profile_image;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat2);
        initViews();

        user = Global.chatContact;
        lbl_name.setText(user.getName());

        Picasso.get()
                .load(user.getImage())
                .placeholder(R.drawable.person)
                .error(R.drawable.person)
                .into(profile_image);

        getMessages();

    }

    private void getMessages() {
        databaseReference.child("Messages").child(currentUser.getUid()).child(user.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            messageArrayList.clear();
                            for(DataSnapshot ds : dataSnapshot.getChildren()) {
                                String key = ds.getKey();
                                Message message = ds.getValue(Message.class);
                                messageArrayList.add(message);
                            }
                            messageAdap.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void initViews() {
        txt_chat = findViewById(R.id.txt_chat);
        main_page_toolbar = findViewById(R.id.main_page_toolbar);
        setSupportActionBar(main_page_toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        View view = LayoutInflater.from(this).inflate(R.layout.custom_chat_bar, null);
        lbl_name = view.findViewById(R.id.lbl_name);
        lbl_last_scene = view.findViewById(R.id.lbl_last_scene);
        profile_image = view.findViewById(R.id.profile_image);

        getSupportActionBar().setCustomView(view);

        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        btn_send = findViewById(R.id.btn_send);
        btn_send.setOnClickListener(this);
        listview = findViewById(R.id.listview);

        messageArrayList = new ArrayList<>();
        messageAdap = new MessageAdap(this,messageArrayList);

        listview.setAdapter(messageAdap);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_send:
                sendMessage();
                break;
        }
    }

    private void sendMessage() {
        String message = txt_chat.getText().toString();
        final String pushId = databaseReference.push().getKey();

        final Map messageBody = new HashMap();
        messageBody.put("message",message);
        messageBody.put("type","message");
        messageBody.put("from",currentUser.getUid());

        databaseReference.child("Messages").child(currentUser.getUid()).child(user.getUid()).child(pushId)
        .setValue(messageBody).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if(task.isSuccessful()){
                    databaseReference.child("Messages").child(user.getUid()).child(currentUser.getUid()).child(pushId)
                            .setValue(messageBody).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(ChatAct.this, "message sent!", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(ChatAct.this, "message failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else{
                    Toast.makeText(ChatAct.this, "message failed", Toast.LENGTH_SHORT).show();

                }
            }
        });

    }


}
