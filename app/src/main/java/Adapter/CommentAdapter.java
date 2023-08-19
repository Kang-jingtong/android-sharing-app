package Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.finalproject.CommentActivity;
import com.example.finalproject.R;
import com.example.finalproject.ScreenActivity;

import java.util.List;

import Etity.Comment;
import Etity.Screen;


public class CommentAdapter extends BaseAdapter {
    private static CommentActivity commentActivity;
    private LayoutInflater mLayoutInflater;
    private final List<Comment> mCommentlist;//存comment
    public CommentAdapter(CommentActivity commentActivity, List<Comment> mCommentlist){
        this.commentActivity=commentActivity;
        mLayoutInflater = LayoutInflater.from(commentActivity);
        this.mCommentlist = mCommentlist;
    }

    public int getCount() {
        return mCommentlist.size(); //列表长度
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    static class ViewHolder{//通过viewholder实现复用效率到提升
        Button portrait;
        TextView name;
        TextView content;
        TextView time;
        //public TextView fruitname;
    }
    @Override
    //getView中的convertView指的是一个个具体的item。使用viewholder是为了减少内存。每一张图片都去new一个ImageView的话，相当于把1000张图片写入内存。
    public View getView(int position, View convertView, ViewGroup parent) { //列表每行样子
        //ViewHolder holder = null;//ViewHolder 这个object就包含了视图所有的信息，使用的时候直接通过geTtag（）获取即可。
        final CommentAdapter.ViewHolder holder = new CommentAdapter.ViewHolder();//ViewHolder 这个object就包含了视图所有的信息，使用的时候直接通过geTtag（）获取即可。
        Comment c = mCommentlist.get(position);
        convertView = mLayoutInflater.inflate(R.layout.comment_items, null);
        holder.portrait=convertView.findViewById(R.id.protrait);
        holder.name=convertView.findViewById(R.id.friend_name);
        holder.content=convertView.findViewById(R.id.edit_content);
        holder.time=convertView.findViewById(R.id.time_text);
        holder.name.setText(c.getName());
        holder.content.setText(c.getComment_text());
        holder.time.setText(c.getTime());
        holder.portrait.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        convertView.setTag(holder);
        return convertView;
    }

    /*  getCount : 要绑定的条目的数目，比如格子的数量
    getItem : 根据一个索引（位置）获得该位置的对象
    getItemId : 获取条目的id
    getView : 获取该条目要显示的界面*/
}
