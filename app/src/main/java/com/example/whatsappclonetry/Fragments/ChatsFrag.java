package com.example.whatsappclonetry.Fragments;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.whatsappclonetry.Adapter.ContactsAdap;
import com.example.whatsappclonetry.ChatAct;
import com.example.whatsappclonetry.Model.User;
import com.example.whatsappclonetry.R;
import com.example.whatsappclonetry.Utils.Global;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatsFrag extends Fragment implements ContactsAdap.OnContactCliked {


    public ChatsFrag() {
        // Required empty public constructor
    }


    private RecyclerView recycler_view;
    private DatabaseReference contactsRef, usersRef;
    private FirebaseAuth auth;
    private FirebaseUser currentUser;
    ArrayList<User> contacts;
    ContactsAdap contactsAdap;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contacts, container, false);
        recycler_view = view.findViewById(R.id.recycler_view);
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        contactsRef = FirebaseDatabase.getInstance().getReference().child("Contacts").child(currentUser.getUid());
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        contacts = new ArrayList<>();
        contactsAdap = new ContactsAdap(getActivity(),contacts);
        contactsAdap.setOnClickContact(this);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recycler_view.setLayoutManager(mLayoutManager);
        recycler_view.setAdapter(contactsAdap);
        getContacts();
        return view;

    }

    private void getContacts() {
        final ArrayList<String> ids = new ArrayList<>();
        contactsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Iterator iterator = (Iterator) dataSnapshot.getChildren().iterator();
                    while (iterator.hasNext()) {
                        ids.add(((DataSnapshot)iterator.next()).getKey());
                    }
                }
                passIds(ids);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void passIds(ArrayList<String> ids) {
        contacts.clear();
        for(int i=0; i<ids.size(); i++){
            usersRef.child(ids.get(i)).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        User user = dataSnapshot.getValue(User.class);
                        contacts.add(user);
                    }
                    contactsAdap.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }

    }

    @Override
    public void onStart() {
        super.onStart();
        contacts.clear();
    }

    @Override
    public void onClick(int position, User user) {
        Global.chatContact = user;
        startActivity(new Intent(getActivity(), ChatAct.class));
    }
}
