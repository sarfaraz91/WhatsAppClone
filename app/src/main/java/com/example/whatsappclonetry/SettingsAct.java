package com.example.whatsappclonetry;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

public class SettingsAct extends AppCompatActivity implements View.OnClickListener {

    private Button btn_update;
    private EditText txt_name;
    private EditText txt_status;
    private Toolbar main_page_toolbar;
    private ImageView btn_back;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentuser;
    private ImageView img_profile;
    public static final int PICK_IMAGE = 1;
    private StorageReference profileStorageReference;
    private String profile_image;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        main_page_toolbar = findViewById(R.id.main_page_toolbar);
        setSupportActionBar(main_page_toolbar);
        getSupportActionBar().setTitle("Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initViews();
        getSettings();
    }

    private void getSettings() {
        databaseReference.child("Users").child(currentuser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               if(!dataSnapshot.getValue().equals("")){
                   HashMap<String,String> hashMap = (HashMap<String, String>) dataSnapshot.getValue();
                   txt_name.setText(hashMap.get("name"));
                   txt_status.setText(hashMap.get("status"));
                   profile_image = hashMap.get("image");
                   Picasso.get()
                           .load(profile_image)
                           .placeholder(R.drawable.person)
                           .error(R.drawable.person)
                           .into(img_profile);
               }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void initViews(){
        profileStorageReference = FirebaseStorage.getInstance().getReference().child("Profile Image");
        img_profile = findViewById(R.id.img_profile);
        img_profile.setOnClickListener(this);
        firebaseAuth = FirebaseAuth.getInstance();
        currentuser = firebaseAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        txt_status = findViewById(R.id.txt_status);
        txt_name = findViewById(R.id.txt_name);
        btn_update = findViewById(R.id.btn_update);
        btn_update.setOnClickListener(this);
        btn_back = findViewById(R.id.btn_back);
        btn_back.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.btn_back:
                finish();
                break;
            case R.id.btn_update:
                update();
                break;
            case R.id.img_profile:
                openGallery();
                break;
        }
    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK  && null != data) {
            //TODO: action
            Uri selectedImage = data.getData();
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(this);

        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                img_profile.setImageURI(resultUri);
                StorageReference storageReference = profileStorageReference.child(currentuser.getUid()+".jpg");
                storageReference.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(SettingsAct.this, "Successfully Uploaded!", Toast.LENGTH_SHORT).show();

                            task.getResult().getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    profile_image = uri.toString();
                                    databaseReference.child("Users").child(currentuser.getUid()).child("image")
                                            .setValue(profile_image).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                Toast.makeText(SettingsAct.this, "Inserted on Database!", Toast.LENGTH_SHORT).show();
                                            }else{
                                                Toast.makeText(SettingsAct.this, "Database error "+task.getException(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            });

                        }else{
                            Toast.makeText(SettingsAct.this, "Error : "+task.getException(), Toast.LENGTH_SHORT).show();

                        }
                    }
                });
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    private void update() {

        String username = txt_name.getText().toString();
        String status = txt_status.getText().toString();

        if(TextUtils.isEmpty(username)){
            Toast.makeText(this, "username required", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(status)){
            Toast.makeText(this, "status required", Toast.LENGTH_SHORT).show();
        }else{

            HashMap<String,String> profileMap = new HashMap<>();
            profileMap.put("uid",currentuser.getUid());
            profileMap.put("name",username);
            profileMap.put("status",status);
            profileMap.put("image",profile_image);

            databaseReference.child("Users").child(currentuser.getUid()).
                    setValue(profileMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if(task.isSuccessful()){
                        startActivity(new Intent(SettingsAct.this,MainActivity.class));
                        finish();
                    }else{
                        Toast.makeText(SettingsAct.this, "error "+task.getResult(), Toast.LENGTH_SHORT).show();
                    }

                }
            });

        }

    }
}
