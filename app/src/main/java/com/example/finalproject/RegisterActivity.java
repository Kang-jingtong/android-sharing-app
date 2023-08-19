package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import Etity.User;

public class RegisterActivity extends AppCompatActivity {

    private static final String DBDRIVER = "com.mysql.jdbc.Driver";
    private static final String DBURL = "jdbc:mysql://cdb-1hqd6ecg.cd.tencentcdb.com:10189/Final_app";
    private static final String DBUSER = "root";
    private static final String DBPASSWORD = "KJT123acms71260";

    private EditText name;
    private EditText pwd;
    private EditText confirmPwd;
    private Button submit;
    private Button backToLogin;
    private ArrayList<User> list= new ArrayList<User>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        name = (EditText) findViewById(R.id.name);
        pwd = (EditText) findViewById(R.id.password);
        confirmPwd=(EditText) findViewById(R.id.confirmpassword);
        submit = (Button) findViewById(R.id.submit);
        backToLogin=(Button) findViewById(R.id.back_to_login);
        backToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TestNetWork()==true){
                    registUser();
                }
                else{
                    Toast.makeText(RegisterActivity.this,"Please check your internet connection",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

        private void registUser() {
            final String username = name.getText().toString();
            final String password = pwd.getText().toString(); //把name和password取出来转化为string格式
            if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
                Toast.makeText(RegisterActivity.this,"Username and Password can't be empty",Toast.LENGTH_SHORT).show();
                return;
            }
            User user = new User();
            user.setUser_name(username);
            user.setUser_pwd(password);

            //进行2获得此时将要注册的username判断是不是重复
            Thread2 thread2 = new Thread2(username);
            thread2.start();
            try {
                thread2.join();

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (list.size() > 0) {
                Toast.makeText(RegisterActivity.this,"This username is already exist",Toast.LENGTH_SHORT).show();
            } else {
                if(!user.getUser_pwd().equals(confirmPwd.getText().toString())){
                    Toast.makeText(RegisterActivity.this,"The password you enter doesn't match",Toast.LENGTH_SHORT).show();
                    return;
                }else{
                    Thread1 thread1 = new Thread1(user);
                    thread1.start();
                    try {
                        thread1.join();//Wait for this thread to die 主线程等待子线程的终止
                        Toast.makeText(RegisterActivity.this,"register successful",Toast.LENGTH_SHORT).show();
                        finish();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        Toast.makeText(RegisterActivity.this,"register fail",Toast.LENGTH_SHORT).show();
                    }
                }
            }
            list.clear();
        }

        class Thread1 extends Thread{
            User  u;
            Thread1(User a){
                this.u = a;
            }
            //注册新用户将新用户存入数据库
            public void run(){

                try {
                    Class.forName(DBDRIVER);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                Connection conn = null;
                try {
                    conn = (Connection) DriverManager.getConnection(DBURL,DBUSER,DBPASSWORD);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                PreparedStatement ps = null;
                try {
                    ps = conn.prepareStatement("INSERT INTO User (username,password) VALUES (?,?)");
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                try {
                    ps.setString(1,u.getUser_name());
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                try {
                    ps.setString(2,u.getUser_pwd());
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                try {
                    ps.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
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

        class Thread2 extends Thread{
            String username;
            Thread2(String n){
                this.username = n;
            }
            //检查是不是有人有重复姓名
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
                    ps = conn.prepareStatement("SELECT * FROM User WHERE username = ? ");
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                try {
                    ps.setString(1,username);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                ResultSet rs = null;
                try {
                    rs = ps.executeQuery();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                //找到与数据库里相同的name的user并把它存在数据库里
                while(true){
                    try {
                        if (!rs.next()) break;
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    User user = new User();
                    try {
                        user.setUser_name(rs.getString("username"));
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    try {
                        user.setUser_pwd(rs.getString("password"));
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    list.add(user);
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
    public boolean TestNetWork()
    {
        if(RegisterActivity.this!= null){
            ConnectivityManager connectivityManager =(ConnectivityManager)RegisterActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
            if(activeNetInfo==null){
                Toast.makeText(RegisterActivity.this,"Your internet is not connected",Toast.LENGTH_SHORT).show();
                return false;
            }
            boolean netInfo = activeNetInfo.isAvailable();
            if(!netInfo){
                Toast.makeText(RegisterActivity.this,"Your internet is not connected",Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }
}