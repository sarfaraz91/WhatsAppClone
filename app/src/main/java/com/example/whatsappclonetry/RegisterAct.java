package com.example.whatsappclonetry;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;


public class RegisterAct extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "RegisterActivity";
    private EditText txt_name;
    private EditText txt_mobile;
    private EditText txt_email;
    private EditText txt_pass;
    private Button btn_register;
    private TextView tv_already_have_account;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Init();
    }


    private void Init() {
        mAuth = FirebaseAuth.getInstance();
        txt_email = findViewById(R.id.txt_email);
        txt_pass = findViewById(R.id.txt_pass);
        btn_register = findViewById(R.id.btn_register);
        btn_register.setOnClickListener(this);
        tv_already_have_account = findViewById(R.id.tv_already_have_account);
        tv_already_have_account.setOnClickListener(this);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Users");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_register:
                creteAccount();
                break;
            case R.id.tv_already_have_account:
                finish();
                break;
        }
    }

    public void creteAccount() {

        String email = txt_email.getText().toString();
        String pass = txt_pass.getText().toString();

        mAuth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(RegisterAct.this, "Register Successfull", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(RegisterAct.this, LoginAct.class));
                            String uid = mAuth.getCurrentUser().getUid();
                            myRef.child(uid).setValue("");
                            finish();
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegisterAct.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }

                        // ...
                    }
                });
    }


}
