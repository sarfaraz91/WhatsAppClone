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
import com.google.firebase.auth.PhoneAuthProvider;

public class LoginAct extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "LoginActivity";
    private EditText txt_email;
    private EditText txt_pass;
    private Button btn_login;
    private Button btn_phone;
    private TextView tv_new_account;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Init();
    }

    private void Init() {
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        txt_email = findViewById(R.id.txt_email);
        txt_pass = findViewById(R.id.txt_pass);
        btn_login = findViewById(R.id.btn_login);
        btn_phone = findViewById(R.id.btn_phone);
        btn_phone.setOnClickListener(this);
        btn_login.setOnClickListener(this);
        tv_new_account = findViewById(R.id.tv_new_account);
        tv_new_account.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.btn_login:
                loginByEmail();
                break;
            case R.id.tv_new_account:
                startActivity(new Intent(this,RegisterAct.class));
                break;
            case R.id.btn_phone:
                LoginByPhone();
        }

    }

    public void loginByEmail(){

        String email = txt_email.getText().toString();
        String pass = txt_pass.getText().toString();

        mAuth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            Toast.makeText(LoginAct.this, "Login Successfully!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(LoginAct.this,MainActivity.class));
                            finish();
                            FirebaseUser user = mAuth.getCurrentUser();
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginAct.this, "Authentication failed."+task.getException(),
                                    Toast.LENGTH_SHORT).show();
                           // updateUI(null);
                        }

                        // ...
                    }
                });
    }


    private void LoginByPhone(){
        startActivity(new Intent(this,PhoneAct.class));
    }

}

