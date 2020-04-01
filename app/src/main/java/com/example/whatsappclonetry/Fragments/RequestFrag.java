package com.example.whatsappclonetry.Fragments;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.whatsappclonetry.Adapter.RequestsAdap;
import com.example.whatsappclonetry.Model.User;
import com.example.whatsappclonetry.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class RequestFrag extends Fragment implements RequestsAdap.OnAcceptClicked, RequestsAdap.OnCancelClicked {


    public RequestFrag() {
        // Required empty public constructor
    }

    RecyclerView rv_coontacts;
    DatabaseReference chatReqRef;
    DatabaseReference usersRef;
    FirebaseAuth auth;
    FirebaseUser currentUser;
    RequestsAdap requestsAdap;
    ArrayList<User> userArrayList;
    ArrayList<String> ids;
    DatabaseReference contactRef;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_request, container, false);

        initViews(view);

        getChatReqs();

        return view;
    }

    private void getChatReqs() {

        chatReqRef.child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){
                    for (DataSnapshot zoneSnapshot: dataSnapshot.getChildren()) {
                        if(zoneSnapshot.child("request_type").getValue(String.class).equals("recieved")){
                            ids.add((zoneSnapshot.getKey()));
                        }
                    }
                    passids(ids);
                }else{
                    Toast.makeText(getActivity(), "No record found!!!", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void passids(final ArrayList<String> ids) {
        userArrayList.clear();
        for(int i=0; i<ids.size(); i++){
            final int finalI = i;
            usersRef.child(ids.get(i)).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        User user = dataSnapshot.getValue(User.class);
                        userArrayList.add(user);
                        requestsAdap.notifyDataSetChanged();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }

    }

    private void initViews(View view) {
        ids = new ArrayList<>();
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        rv_coontacts = view.findViewById(R.id.rv_coontacts);
        chatReqRef = FirebaseDatabase.getInstance().getReference().child("Chat Requests");
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        userArrayList = new ArrayList<>();
        requestsAdap = new RequestsAdap(getContext(),userArrayList);
        requestsAdap.setOnClickForAccept(this);
        requestsAdap.setOnClickForCancel(this);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rv_coontacts.setLayoutManager(mLayoutManager);
        rv_coontacts.setAdapter(requestsAdap);
        contactRef = FirebaseDatabase.getInstance().getReference().child("Contacts");

    }

    private void AcceptRequest(final String RequestId) {
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
                                                CancelRequest(RequestId);
                                            } else {
                                                Toast.makeText(getActivity(), "ERROR " + task.getException(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        } else {
                            Toast.makeText(getActivity(), "ERROR " + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void CancelRequest(final String RequestId) {
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
                                Toast.makeText(getActivity(), "Request Accepted " + task.getException(), Toast.LENGTH_SHORT).show();
                                getChatReqs();
                            } else {
                                Toast.makeText(getActivity(), "Error on Recieve Cancel " + task.getException(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(getActivity(), "Error on Send Cancel" + task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        userArrayList.clear();
    }


    @Override
    public void onAcceptClick(int position, User user) {
        AcceptRequest(user.getUid());
    }

    @Override
    public void onCancelClick(int position, User user) {
        CancelRequest(user.getUid());

    }
}
