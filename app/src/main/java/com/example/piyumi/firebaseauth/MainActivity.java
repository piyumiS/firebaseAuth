package com.example.piyumi.firebaseauth;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
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

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnregister;
    private EditText editTextUname;
    private EditText editTextName;
    private EditText editTextpassword;
    private EditText editTextconfirm_password;
    private EditText editTextemail;
    private TextView textViewlogin;


    private ProgressDialog progressDialog;

    //defining firebase object
    private FirebaseAuth firebaseAuth;
    //defining db
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initializing Firebase object
        firebaseAuth = FirebaseAuth.getInstance();


        if(firebaseAuth.getCurrentUser() !=null)
        {
            //start profile activity  here
            finish();
            startActivity(new Intent(getApplicationContext(),ProfileActivity.class));
        }



        progressDialog = new ProgressDialog(this);

        //get the firebase database reference
        databaseReference = FirebaseDatabase.getInstance().getReference().child("User_Info");


        btnregister = (Button) findViewById(R.id.btnregister);


        editTextUname = (EditText) findViewById(R.id.editTextUname);
        editTextName = (EditText) findViewById(R.id.editTextName);
        editTextemail = (EditText) findViewById(R.id.editTextemail);
        editTextpassword = (EditText) findViewById(R.id.editTextpassword);
        editTextconfirm_password = (EditText) findViewById(R.id.editTextConfirmpw);

        textViewlogin = (TextView) findViewById(R.id.textViewlogin);

        //attaching listner to button
        btnregister.setOnClickListener(this);
        textViewlogin.setOnClickListener(this);
    }
    private void registerUser(){
        String name = editTextName.getText().toString().trim();
        String uname = editTextUname.getText().toString().trim();
        String email = editTextemail.getText().toString().trim();
        String password = editTextpassword.getText().toString().trim();
        String confirm_pw = editTextconfirm_password.getText().toString().trim();

        final UserDetails newUser = new UserDetails(name,uname,email,password,confirm_pw);


        if (TextUtils.isEmpty(newUser.getName())){
            //password is empty
            Toast.makeText(this, "Please enter Name", Toast.LENGTH_SHORT).show();
            //stopping the function execution further
            return;

        }
        if (TextUtils.isEmpty(newUser.getUname())){
            //password is empty
            Toast.makeText(this, "Please enter User Name", Toast.LENGTH_SHORT).show();
            //stopping the function execution further
            return;

        }

        if(TextUtils.isEmpty(newUser.getEmail())){
            //email is empty
            Toast.makeText(this, "Please enter email",Toast.LENGTH_SHORT).show();
            //stopping the function execution further
            return;


        }

        if (TextUtils.isEmpty(newUser.getPassword())){
            //password is empty
            Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
            //stopping the function execution further
            return;

        }

        if (TextUtils.isEmpty(newUser.getComfirm_pw())){
            //confirm password is empty
            Toast.makeText(this, "Please confirm password", Toast.LENGTH_SHORT).show();
            //stopping the function execution further
            return;

        }
        if (!newUser.getPassword().equals(newUser.getComfirm_pw())){
            //passwords mismatch
            Toast.makeText(this, "Passwords does not match", Toast.LENGTH_SHORT).show();
            //stopping the function execution further
            return;

        }

        //if validations are ok a progress bar will be shown
        progressDialog.setMessage("Registering User...");
        progressDialog.show();

        //creating a new user
        firebaseAuth.createUserWithEmailAndPassword(newUser.getEmail(),newUser.getPassword())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            //user is successfully registered and logged in

                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            DatabaseReference userDB = databaseReference.child(user.getUid());
                            userDB.child("name").setValue(newUser.getName());
                            userDB.child("username").setValue(newUser.getUname());
                            userDB.child("email").setValue(newUser.getEmail());



                            finish();

                            startActivity(new Intent(getApplicationContext(),ProfileActivity.class));
                        }

                        else
                        {
                            Toast.makeText(MainActivity.this,"Registration Failed! Please try again.",Toast.LENGTH_SHORT).show();
                        }

                    }
                });


    }


    @Override
    public void onClick(View view) {
        if (view == btnregister)
        {
            registerUser();
        }
        if (view== textViewlogin)
        {
            //start login activity
            startActivity(new Intent(this, LoginActivity.class));
        }

    }
}
