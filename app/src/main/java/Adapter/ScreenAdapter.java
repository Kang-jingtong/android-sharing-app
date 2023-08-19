package Adapter;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.finalproject.CommentActivity;
import com.example.finalproject.CosService;
import com.example.finalproject.FullScreenImageActivity;
import com.example.finalproject.MainActivity;
import com.example.finalproject.R;
import com.example.finalproject.ScreenActivity;
import com.example.finalproject.ShowUserInfoActivity;
import com.mysql.jdbc.PreparedStatement;


import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import Etity.Screen;
import Etity.User;

import static Etity.User.Gender.BOY;
import static Etity.User.Gender.GIRL;


public class ScreenAdapter extends BaseAdapter {

    private static final String DBDRIVER = "com.mysql.jdbc.Driver";
    private static final String DBURL = "jdbc:mysql://cdb-1hqd6ecg.cd.tencentcdb.com:10189/Final_app";
    private static final String DBUSER = "root";
    private static final String DBPASSWORD = "KJT123acms71260";

    private static ScreenActivity screenActivity;
    private final LayoutInflater mLayoutInflater;//layoutInflater是一个控件，作用与findViewbyId类似，都是为了寻找某个view
    private final List<Screen> mScreenlist;
    User u;
    Screen screen;
    MainActivity m = new MainActivity();
    CosService cosService;
    public static String name;
    public static Bitmap bitmap;
    private static CommentActivity commentActivity;
    public ScreenAdapter(ScreenActivity screenActivity, List<Screen> mScreenlist){
        ScreenAdapter.screenActivity = screenActivity;
        this.mScreenlist=mScreenlist;
        mLayoutInflater = LayoutInflater.from(screenActivity);
        cosService = new CosService(screenActivity);
        cosService.initCos();
    }

    public int getCount() {
        return mScreenlist.size(); //列表长度
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    static class ViewHolder implements DownloadListener {//通过viewholder实现复用效率到提升
        Button portrait;
        TextView name;
        TextView content;
        ImageView contentImage;
        TextView time;
        Button delete;
        Button comment;
        String path;
        File copyfile;
        @Override
        public void onDownloadSuccess() {
            final File file = new File(path);
            copyfile=file;
            if(file.exists()){
                screenActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        contentImage.setVisibility(View.VISIBLE);
                        contentImage.setImageURI(Uri.fromFile(file));
                        contentImage.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent=new Intent(screenActivity, FullScreenImageActivity.class);
                                intent.putExtra("image", Uri.fromFile(file));
                                screenActivity.startActivity(intent);
                            }
                        });
                    }
                });
            }
        }
    }
    @Override
    //getView中的convertView指的是一个个具体的item。使用viewholder是为了减少内存。每一张图片都去new一个ImageView的话，相当于把1000张图片写入内存。
    public View getView(int position, View convertView, final ViewGroup parent) { //列表每行样子
        final ViewHolder holder = new ViewHolder();//ViewHolder 这个object就包含了视图所有的信息，使用的时候直接通过geTtag（）获取即可。
        Screen s = mScreenlist.get(position);
        convertView = mLayoutInflater.inflate(R.layout.screen_items, null);

        holder.portrait=convertView.findViewById(R.id.protrait);
        holder.name=convertView.findViewById(R.id.friend_name);
        holder.content=convertView.findViewById(R.id.edit_content);
        holder.contentImage =convertView.findViewById(R.id.friend_image);
        holder.time=convertView.findViewById(R.id.time_text);
        holder.comment=convertView.findViewById(R.id.comment);
        final Button deleteButton = convertView.findViewById(R.id.delete);

        if ((mScreenlist.get(position).getUser_name()).equals(m.username)) {
            deleteButton.setVisibility(View.VISIBLE);
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ObjectAnimator animator = ObjectAnimator.ofFloat(deleteButton, "scaleY", 1f, 1.2f, 1f);
                    animator.setDuration(150);
                    animator.start();
                    Thread wait=new Thread(){
                        public void run()
                        {
                            try {
                                sleep(150);
                                ThreadDelete threadDelete = new ThreadDelete(holder.time.getText().toString());
                                threadDelete.start();
                                try {
                                    threadDelete.join();
                                } catch (InterruptedException ex) {
                                    ex.printStackTrace();
                                }
                                screenActivity.finish();
                                Intent i = new Intent(screenActivity,ScreenActivity.class);
                                screenActivity.startActivity(i);

                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                        }
                    };wait.start();

                }
            });
        }
        name = mScreenlist.get(position).getUser_name();
        holder.delete = deleteButton;
        holder.name.setText(s.getUser_name());
        holder.content.setText(s.getText());
        holder.time.setText(s.getTime());

        holder.portrait.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String a = holder.name.getText().toString();
                ThreadshowUserInfo threadshowUserInfo = new ThreadshowUserInfo(a);
                threadshowUserInfo.start();
                try {
                    threadshowUserInfo.join();
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                //Log.d("msg", "onClick: "+u+"-----------------------------");
                Intent intent = new Intent(screenActivity, ShowUserInfoActivity.class);
                intent.putExtra("username",u);
                screenActivity.startActivity(intent);
            }
        });

        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ObjectAnimator animator = ObjectAnimator.ofFloat(holder.comment, "scaleY", 1f, 1.2f, 1f);
                animator.setDuration(150);
                animator.start();
                Thread wait=new Thread(){
                    public void run()
                    {
                        try {
                            sleep(150);
                            String a = holder.time.getText().toString();
                            ThreadComment threadComment = new ThreadComment(a);
                            threadComment.start();
                            try {
                                threadComment.join();
                            } catch (InterruptedException ex) {
                                ex.printStackTrace();
                            }
                            //Log.d("msg", "onClick: "+u+"-----------------------------");
                            Intent intent = new Intent(screenActivity, CommentActivity.class);
                            intent.putExtra("comment", screen);
                            intent.putExtra("commentpic", Uri.fromFile(holder.copyfile));
                            screenActivity.startActivity(intent);

                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                        }
                    };wait.start();
                }
            });


        convertView.setTag(holder);
        holder.path = screenActivity.getExternalCacheDir().getAbsolutePath() + "/" + s.getImage();
        cosService.download(screenActivity.getExternalCacheDir().getAbsolutePath(), s.getImage(), holder);
        return convertView;
    }


    /*  getCount : 要绑定的条目的数目，比如格子的数量
    getItem : 根据一个索引（位置）获得该位置的对象
    getItemId : 获取条目的id
    getView : 获取该条目要显示的界面*/
    class ThreadshowUserInfo extends Thread{
        String a;
        ThreadshowUserInfo(String a){
            this.a = a;
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
                ps = (PreparedStatement) conn.prepareStatement("SELECT username,sex,birth,what_up from User where username = ? ");
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                ps.setString(1, a);//这里的username和数据库里的username进行比较
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

    class ThreadDelete extends Thread{
        String a;
        ThreadDelete(String a){
            this.a = a;
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
                ps = (PreparedStatement) conn.prepareStatement("DELETE FROM Content where time = ? ");
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                ps.setString(1, a);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                ps.executeUpdate();
            } catch (SQLException ex) {
                ex.printStackTrace();
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

    class ThreadComment extends Thread{
        String a;
        ThreadComment(String a){
            this.a = a;
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
                ps = (PreparedStatement) conn.prepareStatement("SELECT content_id,text,picture,time,user_name from Content where time = ? ");
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                ps.setString(1, a);
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
                    screen = new Screen();
                    screen.setContent_id(rs.getInt("content_id"));
                    screen.setText(rs.getString("text"));
                    if(rs.getString("picture")!=null){
                        screen.setImage(rs.getString("picture"));
                    }
                    screen.setTime(rs.getString("time"));
                    screen.setUser_name(rs.getString("user_name"));
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

