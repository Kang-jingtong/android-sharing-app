package com.example.finalproject;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import Etity.Category;
import Etity.Screen;

public class EditActivity extends AppCompatActivity {

    private static final int CAMERA_PERMISSION_APPLY_RESULT = 12341;
    private static final int PHOTO_FROM_CAMERA = 1;
    private static final String DBDRIVER = "com.mysql.jdbc.Driver";
    private static final String DBURL = "jdbc:mysql://cdb-1hqd6ecg.cd.tencentcdb.com:10189/Final_app";
    private static final String DBUSER = "root";
    private static final String DBPASSWORD = "KJT123acms71260";

    private Uri imageURI = null;
    private String fileName = null;

    //spinner
    private Spinner spinnertext;
    private List<String> list = new ArrayList<String>();
    private ArrayAdapter<String> adapter;
    String selectText;
    String selectType;

    private Button back;
    private Button post;
    private ImageView photo;
    private EditText editext;
    private Button addPic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_edit);
        editext = (EditText) findViewById(R.id.edit);
        addPic = (Button) findViewById(R.id.add_pic);
        addPic.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                requestCameraPermission();
            }
        });
        back = (Button) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        post = (Button) findViewById(R.id.send);
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadToSQL();
                Intent intent = new Intent(EditActivity.this, ScreenActivity.class);
                startActivity(intent);
            }
        });
        photo = findViewById(R.id.photo);
        try {
            spinner();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void spinner() throws InterruptedException {
        //第一步：定义下拉列表内容
        ThreadCategoryshow1 threadCategoryshow = new ThreadCategoryshow1();
        threadCategoryshow.start();
        threadCategoryshow.join();
        spinnertext = (Spinner) findViewById(R.id.spinneredit);
        //第二步：为下拉列表定义一个适配器
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        //第三步：设置下拉列表下拉时的菜单样式
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //第四步：将适配器添加到下拉列表上
        spinnertext.setAdapter(adapter);
        selectText = spinnertext.getSelectedItem().toString();
        //第五步：添加监听器，为下拉列表设置事件的响应
        spinnertext.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> argO, View argl, int arg2, long arg3) {
                // TODO Auto-generated method stub
                selectType=list.get(arg2);
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

                v.setVisibility(View.VISIBLE);
                Log.i("spinner", "Spinner Touch事件被触发!");
                return false;
            }
        });

        //焦点改变事件处理
        spinnertext.setOnFocusChangeListener(new Spinner.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                // TODO Auto-generated method stub
                v.setVisibility(View.VISIBLE);
                Log.i("spinner", "Spinner FocusChange事件被触发！");
            }
        });
    }
    class ThreadCategoryshow1 extends Thread{
        ThreadCategoryshow1(){

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

    //发布时候将内容和文件名存入数据库
    public void loadToSQL(){
        Screen screen = new Screen();
        screen.setText(editext.getText().toString());
        screen.setImage(fileName);
        ThreadUpload thread = new ThreadUpload(screen);
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Toast.makeText(EditActivity.this,"成功存入数据库",Toast.LENGTH_SHORT).show();
    }

    public class ThreadUpload extends Thread{
        Screen s;
        ThreadUpload(Screen s){
            this.s = s;
        }
        MainActivity m = new MainActivity();
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
                ps = conn.prepareStatement("INSERT INTO Content (text,picture,time,user_name,type) VALUES (?,?,?,?,?)");
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                ps.setString(1,s.getText());
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                ps.setString(2,s.getImage());
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                ps.setString(3,getToday("yyyy-MM-dd HH:mm:ss"));
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                ps.setString(4,m.username);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                ps.setString(5,selectType);
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
    //https://developer.android.com/guide/topics/media/camera#java
    //Here is the function required if you are the first time to open a camera.
    //Because of the formate here ,user needs to get the permission pressing the take a photo button the first time.
    //Then press it again to take the photo.
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
            String[] strings = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
            requestPermissions(strings, CAMERA_PERMISSION_APPLY_RESULT);
        } else {
            openCamera();
        }
    }

    //打开相机open camera
    private void openCamera() {
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String sdStatus = Environment.getExternalStorageState();
        if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { //Environment.MEDIA_MOUNTED stands for the status of SD card.
            Toast.makeText(this, "Can not save photo!", Toast.LENGTH_SHORT).show();
        } else {
            String cachePath = getExternalCacheDir().getAbsolutePath();
            fileName = getToday("yyyyMMddHHmmss") + ".jpg";//Save the current date to the database.
            File tempFile = new File(cachePath);
            if (!tempFile.exists() && !tempFile.isDirectory()) {
                tempFile.mkdirs();
            }
            File targetFile = new File(cachePath + fileName);
            if (currentapiVersion < 24) {
                imageURI = Uri.fromFile(targetFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageURI);
            } else {
                try {
                    ContentValues contentValues = new ContentValues(1);
                    contentValues.put(MediaStore.Images.Media.DATA, targetFile.getAbsolutePath());
                    imageURI = this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageURI);
                } catch (SecurityException e) {
                    Toast.makeText(this, "You did not open the storge permission", Toast.LENGTH_SHORT).show();
                }
            }
            if (imageURI != null) {
                startActivityForResult(intent, PHOTO_FROM_CAMERA);
            }
        }
    }

    private String getToday(String format) {
        Date today = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        return simpleDateFormat.format(today);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED)
            return;
        if (resultCode == RESULT_OK) {
            // 拍照取得的照片
            if (imageURI == null) {
                return;
            }
            String photoPath = getRealFilePath(this, imageURI);

             //imageURI the photo your just take
            addPic.setVisibility(View.GONE);
            photo.setVisibility(View.VISIBLE);
            photo.setImageURI(imageURI);

            CosService cosService = new CosService(this);
            cosService.initCos();
            cosService.upload(this, photoPath, fileName);
        }
    }

    public static String getRealFilePath(final Context context, final Uri uri) {
        if (null == uri) {
            return null;
        }
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        return data;
    }
}