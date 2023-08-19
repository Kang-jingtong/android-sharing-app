package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.qcloud.core.auth.QCloudCredentialProvider;
import com.tencent.qcloud.core.auth.ShortTimeCredentialProvider;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import Etity.Screen;
import Etity.User;

public class MainActivity extends AppCompatActivity {

    private static final String DBDRIVER = "com.mysql.jdbc.Driver";
    private static final String DBURL = "jdbc:mysql://cdb-1hqd6ecg.cd.tencentcdb.com:10189/Final_app";
    private static final String DBUSER = "root";
    private static final String DBPASSWORD = "KJT123acms71260";

    private Button pic;
    private EditText name;
    private EditText pwd;
    private Button submit;
    private Button register;
    private User u ;
    public static String username;
    CheckBox rememberPwd;
    CheckBox autoLogin;
    SharedPreferences sp;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sp=this.getSharedPreferences("userInfo",Context.MODE_PRIVATE);
        pic = (Button)findViewById(R.id.picture);
        name = (EditText)findViewById(R.id.name);
        pwd = (EditText)findViewById(R.id.password);
        submit = (Button) findViewById(R.id.submit);
        register = (Button)findViewById(R.id.register);
        rememberPwd = findViewById(R.id.rememberPwd);
        rememberPwd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(rememberPwd.isChecked()){
                    sp.edit().putBoolean("CHECK",true).commit();
                }
                else{
                    sp.edit().putBoolean("CHECK",false).apply();
                }
            }
        });
        autoLogin = findViewById(R.id.autoLogin);
        autoLogin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(autoLogin.isChecked()){
                    sp.edit().putBoolean("AUTOLOGIN",true).commit();
                }
                else{
                    sp.edit().putBoolean("AUTOLOGIN",false).apply();
                }
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });
        if(TestNetWork()==true){
            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        login();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    String getUsername = name.getText().toString();
                    String getPassword = pwd.getText().toString();
                    if(rememberPwd.isChecked()){
                        editor=sp.edit();
                        editor.putString("USERNAME",getUsername);
                        editor.putString("PASSWORD",getPassword);
                        editor.apply();
                    }
                }
            });
        }
        else{
            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(MainActivity.this,"Your internet is not connected,please check your internet.",Toast.LENGTH_SHORT).show();
                }
            });
        }

        if(sp.getBoolean("CHECK",false)){
            rememberPwd.setChecked(true);
            String autoUser=sp.getString("USERNAME","");
            String autoPassword=sp.getString("PASSWORD","");
            name.setText(autoUser);
            pwd.setText(autoPassword);
            if(sp.getBoolean("AUTOLOGIN",false)){
                Intent intent=new Intent(MainActivity.this, ScreenActivity.class);
                username=autoUser;
                startActivity(intent);
            }

        }

        pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntroductionDialog();
            }
        });
    }
    public void login() throws SQLException, ClassNotFoundException {
        username = name.getText().toString();//用户输入的用户名和密码
        String password = pwd.getText().toString();
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            Toast.makeText(MainActivity.this,"name and password can't be empty",Toast.LENGTH_SHORT).show();
        }
        else{
            Threadcheck thread = new Threadcheck(username,password);
            thread.start();
                try {
                    thread.join();//测试字符串类型的要用.equals();
                    if( u == null){
                        Toast.makeText(MainActivity.this,"username incorrect or password incorrect",Toast.LENGTH_SHORT).show();
                    }
                    else if(u.getUser_name().equals(username) && u.getUser_pwd().equals(password)){
                        Toast.makeText(MainActivity.this,"Login succeeful",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(MainActivity.this,ScreenActivity.class);
                        startActivity(intent);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this,"Login fail",Toast.LENGTH_SHORT).show();
                }
        }
    }

    class Threadcheck extends Thread{
        String username;
        String password;
        Threadcheck(String n,String p){
            this.username = n;
            this.password = p;
        }
        //检查用户名和密码是否正确
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
                ps = conn.prepareStatement("SELECT * FROM User WHERE username = ? and password = ?");
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                ps.setString(1,username);//这里的username和数据库里的username进行比较
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                ps.setString(2,password);
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
            try {
                if(rs.next()){
                    u = new User();
                    u.setUser_name(rs.getString("username"));
                    u.setUser_pwd(rs.getString("password"));
                    username = rs.getString("username");
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

    private void IntroductionDialog() {
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(MainActivity.this);
        normalDialog.setTitle("About Poster");
        normalDialog.setIcon(R.drawable.intro);
        normalDialog.setMessage("Poster is a platform for you share your thoughts and exchange ideas.\n" +
                "Discussion in groups are allowed!\n"+"You can send messages with including text and pictures.\n"+
                "Wish you have a good time here!");
        normalDialog.setNegativeButton("Ok",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        normalDialog.show();
    }

    //defining whether there is internet.
    //Reference: https://www.cnblogs.com/fnlingnzb-learner/p/7531811.html
    public boolean TestNetWork()
    {
        if(MainActivity.this!= null){
            ConnectivityManager connectivityManager =(ConnectivityManager)MainActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
            if(activeNetInfo==null){
                Toast.makeText(MainActivity.this,"Your internet is not connected",Toast.LENGTH_SHORT).show();
                return false;
            }
            boolean netInfo = activeNetInfo.isAvailable();
            if(!netInfo){
                Toast.makeText(MainActivity.this,"Your internet is not connected",Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }
}