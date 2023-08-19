package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import Adapter.ScreenAdapter;
import Etity.Category;
import Etity.Screen;

public class ScreenActivity extends AppCompatActivity {

    final static String DBDRIVER = "com.mysql.jdbc.Driver";
    final static String DBURL = "jdbc:mysql://cdb-1hqd6ecg.cd.tencentcdb.com:10189/Final_app";
    final static String DBUSER = "root";
    final static String DBPASSWORD = "KJT123acms71260";

    private ListView mLv1;//声明控件
    private List<String> list = new ArrayList<String>();
    private Spinner spinnertext;
    private ArrayAdapter<String> adapter;
    private Button post;
    private Button mainScreen;
    private Button userInfo;
    private Button addCategory;
    Category c;
    private List<Screen> screenlist = new ArrayList<>();
    String selectText;

    //需要在数据源和显示中间增加适配器（桥梁，利用适配器，列表视图可以显示多种不同来源多数据
    @SuppressLint("ResourceAsColor")
    @Override
    public void onCreate(Bundle savedlnstanceState) {
        super.onCreate(savedlnstanceState);
        setContentView(R.layout.activity_screen);
        addCategory = findViewById(R.id.addCategory);
        addCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText inputServer = new EditText(ScreenActivity.this);
                AlertDialog.Builder builder = new AlertDialog.Builder(ScreenActivity.this);
                c = new Category();
                builder.setTitle("Build a new Category").setView(inputServer)
                        .setNegativeButton("Cancel", null);
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String b = inputServer.getText().toString();
                        c.setCategory_name(b);
                        ThreadInputCategory threadInputCategory = new ThreadInputCategory(c);
                        threadInputCategory.start();
                        try {
                            threadInputCategory.join();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        try {
                            spinner();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });
                builder.show();
            }
        });

        post = findViewById(R.id.Post);
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ObjectAnimator animator = ObjectAnimator.ofFloat(post, "scaleY", 1f, 1.2f, 1f);
                animator.setDuration(150);
                animator.start();
                Thread wait=new Thread(){
                    public void run()
                    {
                        try {
                            sleep(150);
                            Intent intent = new Intent(ScreenActivity.this, EditActivity.class);
                            startActivity(intent);

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }
                };wait.start();
            }
        });

        mainScreen = findViewById(R.id.main_screen);
        mainScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ScreenActivity.this, ScreenActivity.class);
                startActivity(intent);
            }
        });
        userInfo = findViewById(R.id.user_info);
        userInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ScreenActivity.this, UserInformationActivity.class);
                startActivity(intent);
            }
        });

        try {
            spinner();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mLv1 = (ListView) findViewById(R.id.lv_1);//找到控件
        mLv1.setAdapter(new ScreenAdapter((ScreenActivity.this), screenlist));
    }
    //This is for the spinner button;

    public void spinner() throws InterruptedException {
        //第一步：定义下拉列表内容 Define the content of dropdownlist
        ThreadCategoryshow threadCategoryshow = new ThreadCategoryshow();
        threadCategoryshow.start();
        threadCategoryshow.join();
        spinnertext = (Spinner) findViewById(R.id.spinner1);
        //第二步：为下拉列表定义一个适配器 Define an adapter for the drop-down list
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        //第三步：设置下拉列表下拉时的菜单样式 Sets the menu style when a drop-down list is pulled down
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //第四步：将适配器添加到下拉列表上 Add the adapter to the drop-down list
        spinnertext.setAdapter(adapter);
        //selectText = spinnertext.getSelectedItem().toString();
        //第五步：添加监听器，为下拉列表设置事件的响应 Add a listener to set the response to the event for the drop-down list
        spinnertext.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> argO, View argl, int arg2, long arg3) {
                // TODO Auto-generated method stub
                // if arg2=0 是movie
                selectText = list.get(arg2);

                screenlist.clear();
                    ThreadSelectType threadSelectType = new ThreadSelectType(list.get(arg2));
                    threadSelectType.start();
                    try {
                        threadSelectType.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                mLv1.setAdapter(new ScreenAdapter((ScreenActivity.this), screenlist));
                argO.setVisibility(View.VISIBLE);
            }

            @Override

            public void onNothingSelected(AdapterView<?> argO) {
                // TODO Auto-generated method stub
                argO.setVisibility(View.VISIBLE);
            }
        });

        //将spinnertext添加到OnTouchListener对内容选项触屏事件处理
        spinnertext.setOnTouchListener(new Spinner.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                // 将mySpinner隐藏
                // click other place instead of the spinner still works.
                v.setVisibility(View.VISIBLE);
                return false;
            }
        });

        //焦点改变事件处理
        spinnertext.setOnFocusChangeListener(new Spinner.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                // TODO Auto-generated method stub
                v.setVisibility(View.VISIBLE);
                //Log.i("spinner", "Spinner FocusChange事件被触发！");
            }
        });
    }


    class ThreadRead extends Thread{
        ThreadRead(){

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
                ps = conn.prepareStatement("SELECT text,picture,time,user_name FROM Content ORDER BY content_id DESC");
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
                while(rs.next()){
                    String text = rs.getString("text");
                    String picture = rs.getString("picture");
                    String time = rs.getString("time");
                    String user_name = rs.getString("user_name");
                   // e.downloadToBucket(picture);
                    CosService cosService = new CosService(ScreenActivity.this);
                    cosService.initCos();
                    //cosService.download(picture);
                    Screen s = new Screen();
                    s.setText(text);
                    s.setImage(picture);
                    s.setTime(time);
                    s.setUser_name(user_name);
                    screenlist.add(s);
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

    class ThreadSearch extends Thread{
        Screen screen;
        String keyword;
        ThreadSearch(Screen screen,String keyword){
            this.screen=screen;
            this.keyword=keyword;
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
                ps = conn.prepareStatement("SELECT text,picture,time,user_name FROM Content WHERE type = ? and text LIKE ? ORDER BY content_id DESC");
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                ps.setString(1,screen.getType()); //%任意个数的字符0-无穷
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                ps.setString(2,"%"+keyword+"%"); //%任意个数的字符0-无穷
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
                while(rs.next()){
                    String text = rs.getString("text");
                    String picture = rs.getString("picture");
                    String time = rs.getString("time");
                    String user_name = rs.getString("user_name");
                    Screen s = new Screen();
                    s.setText(text);
                    s.setImage(picture);
                    s.setTime(time);
                    s.setUser_name(user_name);
                    screenlist.add(s);
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

    class ThreadCategoryInsert extends Thread{
        Category c;
        ThreadCategoryInsert(Category c){
            this.c = c;
        }
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
                ps = conn.prepareStatement("INSERT INTO Category category_name VALUES ?");
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                ps.setString(1,c.getCategory_name());
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
    class ThreadCategoryshow extends Thread{
        ThreadCategoryshow(){

        }
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
                ps = conn.prepareStatement("SELECT category_name FROM Category");
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
                while(rs.next()){
                    String name = rs.getString("category_name");
                    Category c = new Category();
                    c.setCategory_name(name);
                    list.add(c.getCategory_name());
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

    class ThreadSelectType extends Thread{
        String type;
        ThreadSelectType(String type){
            this.type=type;
        }
        public void run(){
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
                ps = conn.prepareStatement("SELECT text,picture,time,user_name FROM Content WHERE type = ? ORDER BY content_id DESC");
                ps.setString(1,type);
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
                while(rs.next()){
                    String text = rs.getString("text");
                    String picture = rs.getString("picture");
                    String time = rs.getString("time");
                    String user_name = rs.getString("user_name");
                    // e.downloadToBucket(picture);
                    CosService cosService = new CosService(ScreenActivity.this);
                    cosService.initCos();
                    //cosService.download(picture);
                    Screen s = new Screen();
                    s.setText(text);
                    s.setImage(picture);
                    s.setTime(time);
                    s.setUser_name(user_name);
                    screenlist.add(s);
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


    class ThreadInputCategory extends Thread{
        Category category;
        ThreadInputCategory(Category category){
            this.category = category;
        }
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
                ps = conn.prepareStatement("INSERT INTO Category (category_name) VALUES (?) ");
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                ps.setString(1,category.getCategory_name());
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