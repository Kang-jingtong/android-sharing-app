package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import Etity.User;

public class ModifyUserInfoActivity extends AppCompatActivity implements DataCallBack{

    private static final String DBDRIVER = "com.mysql.jdbc.Driver";
    private static final String DBURL = "jdbc:mysql://cdb-1hqd6ecg.cd.tencentcdb.com:10189/Final_app";
    private static final String DBUSER = "root";
    private static final String DBPASSWORD = "KJT123acms71260";

    private Button selectBirth;
    private RadioGroup groupGender;
    private Button submit;
    private Button back;
    private EditText whatUp;
    private User u;
    String bd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_user_info);
        selectBirth = (Button) findViewById(R.id.select_birth);
        whatUp = (EditText) findViewById(R.id.input_what_up);
        groupGender = (RadioGroup) findViewById(R.id.sex);
        selectBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BirthSelectDialog dislog = new BirthSelectDialog();
                dislog.show(getSupportFragmentManager(),"aa");
            }
        });
        submit = (Button) findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ThreadModify thm = new ThreadModify();
                thm.start();
                try {
                    thm.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                ThreadInsert thread = new ThreadInsert();
                thread.start();
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(ModifyUserInfoActivity.this, UserInformationActivity.class);
                //用于开始到达新的Activity之前移除之前的Activity。这样我们点击back键就会直接回桌面了
                //intent.putExtra("position",position);//记录从哪个界面离开
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//把上一个activity移除，因为每转换一个屏幕都是打开一个新的界面；
                startActivity(intent);
            }
        });
        back = (Button) findViewById(R.id.back_to_userinfo);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    //When you select a date you get the numbers on the screen.
    public void getData(String data){
        selectBirth.setText(data);
        bd = data;
    }

    class ThreadModify extends Thread{
        public void run(){
            u = new User();

            if(groupGender.getCheckedRadioButtonId() == R.id.boy){
                u.setSex(User.Gender.BOY);
            }
            else if (groupGender.getCheckedRadioButtonId() == R.id.girl){
                u.setSex(User.Gender.GIRL);
            }
            else if (groupGender.getCheckedRadioButtonId() == R.id.secret){
                u.setSex(User.Gender.SECRET);
            }
            u.setBirth(bd);
            u.setWhat_up(whatUp.getText().toString());
        }
    }

    class ThreadInsert extends Thread{
       ThreadInsert(){

       }
        MainActivity m = new MainActivity();
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
                ps = conn.prepareStatement("UPDATE User SET sex=?,birth=?,what_up=? WHERE username=?");
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if(u.getSex()==User.Gender.BOY){
                    ps.setString(1,"BOY");
                }
                else if(u.getSex()==User.Gender.GIRL){
                    ps.setString(1,"GIRL");
                }
                else if(u.getSex()==User.Gender.SECRET){
                    ps.setString(1,"SECRET");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                ps.setString(2,u.getBirth());
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                ps.setString(3,u.getWhat_up());
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                ps.setString(4,m.username);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                ps.executeUpdate();
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
}