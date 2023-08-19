package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import Etity.User;

import static Etity.User.Gender.BOY;
import static Etity.User.Gender.GIRL;

public class ShowUserInfoActivity extends AppCompatActivity {
    private TextView username2;
    private TextView gender2;
    private TextView birth2;
    private TextView whatUp2;
    private Button mainScreen;
    private Button userInfo;
    private Button backToScreen;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_user_info);
        username2 = (TextView) findViewById(R.id.user_name2);
        gender2 = (TextView) findViewById(R.id.user_gender2);
        birth2 = (TextView) findViewById(R.id.birthday2);
        whatUp2 = (TextView) findViewById(R.id.what_up2);
        mainScreen = (Button) findViewById(R.id.main_screen);
        backToScreen = (Button) findViewById(R.id.back_to_screen);
        backToScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Intent intent = getIntent();
        User u=null;
        if(intent!=null){
            u = (User) intent.getSerializableExtra("username");
            username2.setText(u.getUser_name());
            if(u.getSex()==BOY){
                gender2.setText("BOY");
            }
            else if(u.getSex()==GIRL){
                gender2.setText("GIRL");
            }
            else {
                gender2.setText("SECRET");
            }
            birth2.setText(u.getBirth());
            whatUp2.setText(u.getWhat_up());
        }
        mainScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        userInfo = (Button) findViewById(R.id.user_info);
        userInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShowUserInfoActivity.this, UserInformationActivity.class);
                startActivity(intent);
            }
        });

    }
}