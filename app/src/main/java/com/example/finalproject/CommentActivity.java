package com.example.finalproject;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;

import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import Adapter.CommentAdapter;
import Adapter.ScreenAdapter;
import Etity.Comment;
import Etity.Screen;


public class CommentActivity extends AppCompatActivity {

    private static final String DBDRIVER = "com.mysql.jdbc.Driver";
    private static final String DBURL = "jdbc:mysql://cdb-1hqd6ecg.cd.tencentcdb.com:10189/Final_app";
    private static final String DBUSER = "root";
    private static final String DBPASSWORD = "KJT123acms71260";

    ListView mLv2;
    int contentId;
    String commentContent;
    Button portrait;
    TextView name;
    TextView content;
    ImageView contentImage;
    TextView time;
    Button back;
    Button addComment;
    private PopupWindow popupWindow;
    private View popupView = null;
    private EditText inputComment;
    private String nInputContentText;
    private TextView btnSubmit;
    private RelativeLayout rlInputContainer;
    private List<Comment> commentlist = new ArrayList<>();
    MainActivity m =new MainActivity();
    CommentAdapter commentAdapter;

    public CommentActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        portrait=findViewById(R.id.protrait);
        name=findViewById(R.id.friend_name);
        content=findViewById(R.id.edit_content);
        contentImage =findViewById(R.id.friend_image);
        time=findViewById(R.id.time_text);
        back=findViewById((R.id.back));
        addComment=findViewById(R.id.addComment);
        Intent intent = getIntent();
        Screen screen=null;
        Uri uri;
        if(intent!=null){
            screen = (Screen) intent.getSerializableExtra("comment");
            uri = (Uri) intent.getExtras().getParcelable("commentpic");
            contentImage.setImageURI(uri);
            name.setText(screen.getUser_name());
            content.setText(screen.getText());
            time.setText(screen.getTime());
            contentId =screen.getContent_id();
        }
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        addComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupcomment();
            }
        });
        commentAdapter = new CommentAdapter(CommentActivity.this,commentlist);
        mLv2 = (ListView) findViewById(R.id.lv_2);//找到控件
        mLv2.setAdapter(commentAdapter);
        ThreadShowComment threadShowComment=new ThreadShowComment();
        threadShowComment.start();
        try {
            threadShowComment.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    //Reference: get and learn from internet how to pop up a textbox.
    //https://www.jb51.net/article/151546.htm
    @SuppressLint("WrongConstant")
    private void showPopupcomment() {
        if (popupView == null){
            //加载评论框的资源文件
            popupView = LayoutInflater.from(this).inflate(R.layout.comment_popupwindow, null);
        }
        inputComment = (EditText) popupView.findViewById(R.id.et_discuss);
        btnSubmit = (Button) popupView.findViewById(R.id.btn_confirm);
        rlInputContainer = (RelativeLayout)popupView.findViewById(R.id.rl_input_container);

        if (popupWindow == null){
            popupWindow = new PopupWindow(popupView, RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT, false);

        }
        //popupWindow的常规设置，设置点击外部事件，背景色
        popupWindow.setTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
        popupWindow.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_OUTSIDE)
                    popupWindow.dismiss();
                return false;

            }
        });
        //没实现的自动弹出软键盘didn't realize to pop up the keyboard together.
        inputComment.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.showSoftInput(inputComment, InputMethodManager.SHOW_IMPLICIT);

        // 设置弹出窗体需要软键盘，放在setSoftInputMode之前
        popupWindow.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
        // 再设置模式，和Activity的一样，覆盖，调整大小。
        popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        //设置popupwindow的显示位置，这里应该是显示在底部，即Bottom
        popupWindow.showAtLocation(popupView, Gravity.BOTTOM, 0, 0);

        popupWindow.update();

        //设置监听
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {

            // 在dismiss中恢复透明度
            @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
            public void onDismiss() {

               // mInputManager.hideSoftInputFromWindow(inputComment.getWindowToken(), 0); //强制隐藏键盘


            }
        });
        //外部点击事件
        rlInputContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               // mInputManager.hideSoftInputFromWindow(inputComment.getWindowToken(), 0); //强制隐藏键盘
                popupWindow.dismiss();//dismiss是隐藏掉对象，并非销毁所以再次点击的时候东西还可以显示

            }
        });
        //评论框内的发送按钮设置点击事件
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                nInputContentText = inputComment.getText().toString();
                //If you enter nothing
                if (nInputContentText == null || "".equals(nInputContentText)) {
                    //showToastMsgShort("请输入评论内容");
                    Toast.makeText(CommentActivity.this,"Enter something to submit",Toast.LENGTH_SHORT).show();
                    return;
                }
                //mInputManager.hideSoftInputFromWindow(inputComment.getWindowToken(),0);
                commentContent = inputComment.getText().toString();
                //commentlist.clear();
                ThreadComment threadComment=new ThreadComment(contentId, commentContent);
                threadComment.start();
                try {
                    threadComment.join();
                    ThreadShowComment threadShowComment=new ThreadShowComment();
                    commentlist.clear();
                    threadShowComment.start();
                    try {
                        threadShowComment.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                popupWindow.dismiss();
            }
        });

    }
    private String getToday(String format) {
        Date today = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        return simpleDateFormat.format(today);
    }
    class ThreadComment extends Thread{
        int i;
        String c;
        ThreadComment(int i,String c){
            this.i=i;
            this.c = c;
        }
        //add comment to database
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
                ps = conn.prepareStatement("INSERT INTO Comment (content_id,comment_text,name,time) VALUES (?,?,?,?)");
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                ps.setInt(1,i);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                ps.setString(2,c);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                ps.setString(3,m.username);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                ps.setString(4,getToday("yyyy-MM-dd HH:mm:ss"));
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
    class ThreadShowComment extends Thread{
        ThreadShowComment(){

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
                ps = conn.prepareStatement("SELECT comment_text,name,time FROM Comment WHERE content_id=? ORDER BY comment_id DESC");
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                ps.setInt(1, contentId);
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
                    String text = rs.getString("comment_text");
                    String user_name = rs.getString("name");
                    String time = rs.getString("time");
                    Comment c = new Comment();
                    c.setComment_text(text);
                    c.setTime(time);
                    c.setName(user_name);
                    commentlist.add(c);
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