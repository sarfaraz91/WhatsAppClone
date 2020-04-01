package com.example.whatsappclonetry;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.whatsappclonetry.Model.User;
import com.example.whatsappclonetry.Utils.Global;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class FriendsAct extends AppCompatActivity {

    private RecyclerView recycler_view;
    private DatabaseReference databaseReference;
    private FirebaseAuth auth;
    private FirebaseUser currentUser;
    private Toolbar main_page_toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        main_page_toolbar = findViewById(R.id.main_page_toolbar);
        setSupportActionBar(main_page_toolbar);
        getSupportActionBar().setTitle("Find Friends");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initViews();

        FirebaseRecyclerOptions<User> options =
                new FirebaseRecyclerOptions.Builder<User>()
                        .setQuery(databaseReference, User.class)
                        .build();

        FirebaseRecyclerAdapter<User,FriendsViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<User, FriendsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FriendsViewHolder holder, int position, @NonNull final User model) {
                holder.txt_name.setText(model.getName());
                holder.txt_status.setText(model.getStatus());
                Picasso.get()
                        .load(model.getImage())
                        .placeholder(R.drawable.person)
                        .error(R.drawable.person)
                        .into(holder.profile_image);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(FriendsAct.this, "hellow", Toast.LENGTH_SHORT).show();
                        Global.user = model;
                        startActivity(new Intent(FriendsAct.this,ProfileAct.class));
                    }
                });

            }

            @NonNull
            @Override
            public FriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(FriendsAct.this).inflate(R.layout.friends_items,null,false);
                return new FriendsViewHolder(view);
            }
        };
        recycler_view.setAdapter(firebaseRecyclerAdapter);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recycler_view.setLayoutManager(mLayoutManager);
        firebaseRecyclerAdapter.startListening();
    }

    private void initViews() {
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        recycler_view = findViewById(R.id.recycler_view);
    }

   class FriendsViewHolder extends RecyclerView.ViewHolder {

        ImageView profile_image;
        TextView txt_name;
        TextView txt_status;

       public FriendsViewHolder(@NonNull View itemView) {
           super(itemView);
           profile_image = itemView.findViewById(R.id.profile_image);
           txt_name = itemView.findViewById(R.id.txt_name);
           txt_status = itemView.findViewById(R.id.txt_status);

       }
   }

}
