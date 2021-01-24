package com.example.myshop.Buyer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myshop.Admin.AdminHomeActivity;
import com.example.myshop.Model.Users;
import com.example.myshop.Prevalent.Prevalent;
import com.example.myshop.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rey.material.widget.CheckBox;

import io.paperdb.Paper;


public class LoginActivity extends AppCompatActivity
{
     private EditText InputPhoneNumber, InputPassword;
     private Button LoginButton;
     private ProgressDialog loadingBar;
     private TextView AdminLink, NotAdminLink, ForgetPasswordLink;

     private String parentDbName = "Users";
     private CheckBox chkBoxRememberMe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        LoginButton = (Button) findViewById(R.id.main_login_btn);
        InputPassword = (EditText) findViewById(R.id.login_password_input);
        InputPhoneNumber = (EditText) findViewById(R.id.login_phone_number_input);
        AdminLink = (TextView) findViewById(R.id.admin_panel_link);
        NotAdminLink = (TextView) findViewById(R.id.not_admin_panel_link);
        ForgetPasswordLink = (TextView) findViewById(R.id.forget_password_link);
        loadingBar = new ProgressDialog(this);

        chkBoxRememberMe = (CheckBox) findViewById(R.id.remember_me_chkb);
        Paper.init(this);

        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
              LoginUser();
            }
        });

        ForgetPasswordLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ResetPasswordActivity.class);
                intent.putExtra("check","login");
                startActivity(intent);
            }
        });

        AdminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                LoginButton.setText("Login Admin");
                AdminLink.setVisibility(View.INVISIBLE);
                NotAdminLink.setVisibility(View.VISIBLE);
                parentDbName = "Admins";
            }
        });
        NotAdminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                LoginButton.setText("Login");
                AdminLink.setVisibility(View.VISIBLE);
                NotAdminLink.setVisibility(View.INVISIBLE);
                parentDbName = "Users";

            }
        });
    }

    private void LoginUser()
    {
        String phone = InputPhoneNumber.getText().toString();
        String password = InputPassword.getText().toString();
          if(TextUtils.isEmpty(phone))
    {
        Toast.makeText(this, "Pleasse write your phone no...", Toast.LENGTH_SHORT).show();
    }
          else if(TextUtils.isEmpty(password))
    {
        Toast.makeText(this, "Pleasse write your password...", Toast.LENGTH_SHORT).show();
    }
          else
          {
              loadingBar.setTitle("Login Account");
              loadingBar.setMessage("Please Wait,While we check your credentials.");
              loadingBar.setCanceledOnTouchOutside(false);
              loadingBar.show();


              AllowAccessToAccount(phone, password);
          }
    }

    private void AllowAccessToAccount(final String phone, final String password)
    {
        if(chkBoxRememberMe.isChecked())
        {
            Paper.book().write(Prevalent.UserPasswordKey,password);
            Paper.book().write(Prevalent.UserPhoneKey,phone);
        }

        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if(snapshot.child(parentDbName).child(phone).exists())
                {
                  Users usersData = snapshot.child(parentDbName).child(phone).getValue(Users.class);

                  if(usersData.getPhone().equals(phone))
                  {
                      if(usersData.getPassword().equals(password))
                      {
                          if(parentDbName.equals("Admins"))
                          {
                              Toast.makeText(LoginActivity.this, "Welcome Admin", Toast.LENGTH_SHORT).show();
                              loadingBar.dismiss();

                              Intent intent = new Intent(LoginActivity.this, AdminHomeActivity.class);
                              startActivity(intent);
                          }
                          else if(parentDbName.equals("Users"))
                          {
                              Toast.makeText(LoginActivity.this, "Logged in Successfull", Toast.LENGTH_SHORT).show();
                              loadingBar.dismiss();

                              Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                              Prevalent.currentOnlineUser = usersData;
                              startActivity(intent);
                          }
                      }
                      else
                      {
                          loadingBar.dismiss();
                          Toast.makeText(LoginActivity.this, "Password is Incorrect", Toast.LENGTH_SHORT).show();

                      }

                  }

                }
                else
                {
                    Toast.makeText(LoginActivity.this, "Account with this " + phone + "number does not exists", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}