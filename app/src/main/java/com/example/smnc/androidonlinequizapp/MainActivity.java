package com.example.smnc.androidonlinequizapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.smnc.androidonlinequizapp.BroadcastReceiver.AlarmReceiver;
import com.example.smnc.androidonlinequizapp.Common.Common;
import com.example.smnc.androidonlinequizapp.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    MaterialEditText edtNewUser,edtNewPassword,edtNewEmail; //for sign up
    MaterialEditText edtUser,edtPassword; //for sign in

    Button btnSignUp,btnSignIn;

    FirebaseDatabase database;
    DatabaseReference users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        registerAlarm();

        //Firebase
        database=FirebaseDatabase.getInstance();
        users=database.getReference("Users");

        edtUser=(MaterialEditText)findViewById(R.id.edtUser);
        edtPassword=(MaterialEditText)findViewById(R.id.edtPassword);

        btnSignIn=(Button)findViewById(R.id.btn_sign_in);
        btnSignUp=(Button)findViewById(R.id.btn_sign_up);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSignUpDialog();
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn(edtUser.getText().toString(),edtPassword.getText().toString());
            }
        });
    }

    private void registerAlarm() {
        Calendar calendar=Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY,20); //Saat akşam 5
        calendar.set(Calendar.MINUTE,0); //Dakika 10
        calendar.set(Calendar.SECOND,0);

        Intent intent = new Intent(MainActivity.this, AlarmReceiver.class);
        PendingIntent pendingIntent=PendingIntent.getBroadcast(MainActivity.this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am=(AlarmManager)this.getSystemService(this.ALARM_SERVICE);
        am.setRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),AlarmManager.INTERVAL_DAY,pendingIntent);
    }

    private void signIn(final String user, final String pwd) {
        users.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(user).exists())
                {
                    if(!user.isEmpty())
                    {
                        User login=dataSnapshot.child(user).getValue(User.class);
                        if(login.getPassword().equals(pwd))
                        {
                            Intent homeActivity=new Intent(MainActivity.this,Home.class);
                            Common.currentUser=login;
                            startActivity(homeActivity);
                            finish();
                        }
                        else
                        {
                            Toast.makeText(MainActivity.this,"Wrong password",Toast.LENGTH_SHORT).show();
                        }
                    }
                    else
                    {
                        Toast.makeText(MainActivity.this,"Please enter your name",Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(MainActivity.this,"User is not exist",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void showSignUpDialog() {
        AlertDialog.Builder alertDialog=new AlertDialog.Builder(MainActivity.this);
        alertDialog.setTitle("Sign Up");
        alertDialog.setMessage("Please fill full information");

        LayoutInflater inflater=this.getLayoutInflater();
        View sign_up_layout=inflater.inflate(R.layout.sign_up_layout,null);

        edtNewUser=(MaterialEditText)sign_up_layout.findViewById(R.id.edtNewUserNme);
        edtNewEmail=(MaterialEditText)sign_up_layout.findViewById(R.id.edtNewEmail);
        edtNewPassword=(MaterialEditText)sign_up_layout.findViewById(R.id.edtNewPassword);

        alertDialog.setView(sign_up_layout);
        alertDialog.setIcon(R.drawable.ic_account_circle_black_24dp);

        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                final User user=new User(edtNewUser.getText().toString(),edtNewPassword.getText().toString(),edtNewEmail.getText().toString());

                if(edtNewPassword.getText().toString().isEmpty() || edtNewUser.getText().toString().isEmpty() || edtNewEmail.getText().toString().isEmpty())
                {
                    Toast.makeText(MainActivity.this,"Please full fill the blanks",Toast.LENGTH_SHORT).show();
                    dialogInterface.dismiss();
                }
                else
                {
                    users.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.child(user.getUserName()).exists())
                            {
                                Toast.makeText(MainActivity.this,"User already exist",Toast.LENGTH_SHORT).show();

                            }
                            else
                            {
                                users.child(user.getUserName()).setValue(user);
                                Toast.makeText(MainActivity.this,"User registration success",Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    dialogInterface.dismiss();
                }

            }
        });
        alertDialog.show();
    }
}
