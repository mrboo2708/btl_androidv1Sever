package com.example.btl_androidv1sever;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.btl_androidv1sever.Common.Common;
import com.example.btl_androidv1sever.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

public class SignIn extends AppCompatActivity {

    EditText editPhone,editPassword;
    Button btnSignIn;
    FirebaseDatabase db;
    DatabaseReference user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        editPassword = (MaterialEditText)findViewById(R.id.textPassword);
        editPhone = (MaterialEditText)findViewById(R.id.textPhone);
        btnSignIn = (Button) findViewById(R.id.btnSignInInside);

        db = FirebaseDatabase.getInstance();
        user = db.getReference("User");
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signInUser(editPhone.getText().toString(),editPassword.getText().toString());

            }
        });

    }

    private void signInUser(String phone, String password) {
        ProgressDialog mDiaLog = new ProgressDialog(SignIn.this);
        mDiaLog.setMessage("Please waitting...");
        mDiaLog.show();
        final String SignInPhone = phone;
        final String SignInPassword = password;
        user.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(SignInPhone).exists()){
                    mDiaLog.dismiss();
                    User user = snapshot.child(SignInPhone).getValue(User.class);
                    user.setPhone(SignInPhone);
                    if(Boolean.parseBoolean(user.getIsWork())){
                        if(user.getPassword().equals(SignInPassword)){
                            Intent home = new Intent(SignIn.this,Activity_Homepage.class);
                            Common.currentUser = user;
                            startActivity(home);
                            finish();
                        }
                        else{
                            Toast.makeText(SignIn.this,"Login failed ! Password wrong",Toast.LENGTH_SHORT).show();
                        }
                    }
                    else{
                        Toast.makeText(SignIn.this,"Login failed ! Wrong account",Toast.LENGTH_SHORT).show();

                    }

                }
                else{
                    mDiaLog.dismiss();
                    Toast.makeText(SignIn.this,"Login failed ! Account not exits",Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}