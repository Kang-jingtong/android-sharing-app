package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        Thread myThread = new Thread(){
            public void run(){
                try{
                    sleep(1500);//开始界面持续1.5s
                    Intent it = new Intent(getApplicationContext(),MainActivity.class); //启动MainActivity
                    startActivity(it);
                    finish();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
        myThread.start();
    }
}