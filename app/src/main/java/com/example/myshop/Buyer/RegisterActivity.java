package com.example.myshop.Buyer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.myshop.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private Button CreateAccountButton;
    private EditText InputName,  InputPassword, InputPhoneNumber;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        CreateAccountButton = (Button) findViewById(R.id.register_btn);
        InputName = (EditText) findViewById(R.id.register_username_input);
        InputPassword = (EditText) findViewById(R.id.register_password_input);
        InputPhoneNumber = (EditText) findViewById(R.id.register_phone_number_input);
        loadingBar = new ProgressDialog(this);

        CreateAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateAccount();
            }
        });
    }

    private void CreateAccount()
    {
        String name = InputName.getText().toString();
        String password = InputPassword.getText().toString();
        String phone = InputPhoneNumber.getText().toString();

        if(TextUtils.isEmpty(name))
        {
            Toast.makeText(this, "Pleasse write your name...", Toast.LENGTH_SHORT).show();
        }
       else  if(TextUtils.isEmpty(password))
        {
            Toast.makeText(this, "Pleasse write your password...", Toast.LENGTH_SHORT).show();
        }
       else if(TextUtils.isEmpty(phone))
        {
            Toast.makeText(this, "Pleasse write your phone number...", Toast.LENGTH_SHORT).show();
        }
       else
        {
            loadingBar.setTitle("Create Account");
            loadingBar.setMessage("Please Wait,While we check your credentials.");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            ValidatephoneNumber(name,password,phone);
        }
    }

    private void ValidatephoneNumber(final String name, final String password, final String phone)
    {
          final DatabaseReference RootRef;
          RootRef = FirebaseDatabase.getInstance().getReference();
          RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
              @Override
              public void onDataChange(@NonNull DataSnapshot dataSnapshot)
              {
                   if(!(dataSnapshot.child("Users").child(phone).exists()))
                   {
                       HashMap<String, Object> userdataMap = new HashMap<>();
                       userdataMap.put("name" , name);
                       userdataMap.put("password" , password);
                       userdataMap.put("phone" , phone);

                       RootRef.child("Users").child(phone).updateChildren(userdataMap)
                               .addOnCompleteListener(new OnCompleteListener<Void>() {
                                   @Override
                                   public void onComplete(@NonNull Task<Void> task)
                                           {
                                       if(task.isSuccessful())
                                       {
                                           Toast.makeText(RegisterActivity.this, "Congratulations created", Toast.LENGTH_SHORT).show();
                                           loadingBar.dismiss();

                                           Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                           startActivity(intent);
                                       }
                                       else
                                       {
                                           loadingBar.dismiss();
                                           Toast.makeText(RegisterActivity.this, "Network eorror", Toast.LENGTH_SHORT).show();
                                       }
                                   }
                               });
                   }
                   else
                   {
                       Toast.makeText(RegisterActivity.this, "This "+ phone + " already exists.", Toast.LENGTH_SHORT).show();
                       loadingBar.dismiss();
                       Toast.makeText(RegisterActivity.this, "Please Try again!!", Toast.LENGTH_SHORT).show();

                       Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                       startActivity(intent);
                   }
              }

              @Override
              public void onCancelled(@NonNull DatabaseError databaseError) {

              }
          });
    }
}