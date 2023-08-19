package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import Etity.User;

import static Etity.User.Gender.BOY;
import static Etity.User.Gender.GIRL;

public class UserInformationActivity extends AppCompatActivity {

    private static final String DBDRIVER = "com.mysql.jdbc.Driver";
    private static final String DBURL = "jdbc:mysql://cdb-1hqd6ecg.cd.tencentcdb.com:10189/Final_app";
    private static final String DBUSER = "root";
    private static final String DBPASSWORD = "KJT123acms71260";

    private Button modify;
    private TextView username2;
    private TextView gender2;
    private TextView birth2;
    private TextView what_up2;
    private User u;
    private Button backToMainscreen;
    private Button logout;
    SharedPreferences sp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_information);
        sp=this.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        username2 = (TextView) findViewById(R.id.user_name2);
        gender2 = (TextView) findViewById(R.id.user_gender2);
        birth2 = (TextView) findViewById(R.id.birthday2);
        what_up2 = (TextView) findViewById(R.id.what_up2);
        logout=(Button) findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor=sp.edit();
//                editor.putString("USERNAME","");
//                editor.putString("PASSWORD","");
//                editor.putBoolean("CHECK",false);
                editor.putBoolean("AUTOLOGIN",false);
                editor.apply();
                finish();
                Intent i = new Intent(UserInformationActivity.this,MainActivity.class);
                startActivity(i);
            }
        });
        backToMainscreen = (Button) findViewById(R.id.back_to_mainscreen);
        backToMainscreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        modify = (Button) findViewById(R.id.modify);
        modify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserInformationActivity.this, ModifyUserInfoActivity.class);
                startActivity(intent);
            }
        });
        Threadprint thread = new Threadprint();
        thread.start();
        try {
            thread.join();
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
            what_up2.setText(u.getWhat_up());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    class Threadprint extends Thread{
        MainActivity m = new MainActivity();
        public void run(){
            //加载驱动
            try {
                Class.forName(DBDRIVER);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            //建立连接
            Connection conn = null;
            try {
                conn = (Connection) DriverManager.getConnection(DBURL,DBUSER,DBPASSWORD);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            PreparedStatement ps = null;
            try {
                ps = conn.prepareStatement("SELECT username,sex,birth,what_up from User where username = ? ");
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                ps.setString(1,m.username);//这里的username和数据库里的username进行比较
            } catch (SQLException e) {
                e.printStackTrace();
            }
            ResultSet rs = null;
            try {
                rs = ps.executeQuery();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if(rs.next()){
                    u = new User();
                    u.setUser_name(rs.getString("username"));
                    if(rs.getString("sex").equals("BOY")){
                        u.setSex(BOY);
                    }
                    else if(rs.getString("sex").equals("GIRL")){
                        u.setSex(GIRL);
                    }
                    if(rs.getString("birth")!=null){
                        u.setBirth(rs.getString("birth"));
                    }
                    else{
                        u.setBirth(" ");
                    }
                    if(rs.getString("what_up")!=null){
                        u.setWhat_up(rs.getString("what_up"));
                    }

                }
                else{
                    System.out.println("login fails");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            if(rs!=null){
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if(ps != null){
                try {
                    ps.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if(conn!=null){
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}