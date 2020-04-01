package com.example.whatsappclonetry;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.whatsappclonetry.Adapter.GroupChatAdap;
import com.example.whatsappclonetry.Model.GroupChat;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class GroupChatAct extends AppCompatActivity implements View.OnClickListener{

    EditText txt_chat;
    Button btn_send;
    private DatabaseReference databaseReference;
    private FirebaseAuth auth;
    private FirebaseUser currentUser;
    private ListView listview;
    private GroupChatAdap groupChatAdap;
    private ArrayList<GroupChat> groupChatArrayList;

    private String name;
    private String message;
    private String groupname;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        initViews();
        getName();
        Intent intent = getIntent();

        if(intent != null){
            groupname = intent.getStringExtra("groupname");
            Toast.makeText(this, "name "+groupname, Toast.LENGTH_SHORT).show();
        }
        getAllChats();
    }

    public void initViews(){
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        txt_chat = findViewById(R.id.txt_chat);
        btn_send = findViewById(R.id.btn_send);
        listview = findViewById(R.id.listview);
        btn_send.setOnClickListener(this);
        groupChatArrayList = new ArrayList<>();
        groupChatAdap = new GroupChatAdap(GroupChatAct.this,groupChatArrayList);
        listview.setAdapter(groupChatAdap);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_send:
                sendChat();
                break;
        }
    }

    private void getName(){
        databaseReference.child("Users").child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               HashMap<String,String> hashMap = (HashMap<String, String>) dataSnapshot.getValue();
               name = hashMap.get("name");
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void sendChat() {

        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
        
        message = txt_chat.getText().toString();
        HashMap<String,String> chatMessage = new HashMap<>();
        chatMessage.put("name",name);
        chatMessage.put("message",message);
        chatMessage.put("date",currentDate);
        chatMessage.put("time",currentTime);
        
        databaseReference.child("Groups").child(groupname).push().setValue(chatMessage).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(GroupChatAct.this, "Message Sent!", Toast.LENGTH_SHORT).show();if(task.isSuccessful()){
                    
                }
            }
        });
    }

    private void getAllChats(){
        databaseReference.child("Groups").child(groupname).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                groupChatArrayList.clear();
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    String key = ds.getKey();
                    GroupChat groupChat = ds.getValue(GroupChat.class);
                    groupChatArrayList.add(groupChat);
                }
                groupChatAdap.notifyDataSetChanged();
                listview.post(new Runnable(){
                    public void run() {
                        listview.setSelection(listview.getCount() - 1);
                    }});
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
