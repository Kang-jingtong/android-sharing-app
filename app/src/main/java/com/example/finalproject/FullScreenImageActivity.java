package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import java.io.File;

import Adapter.DownloadListener;
import Adapter.ScreenAdapter;
import Etity.Screen;

public class FullScreenImageActivity extends AppCompatActivity{
    Uri uri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image);
        ImageView imageView=findViewById(R.id.image);;
        Intent intent = getIntent();
        if(intent!=null){
            uri = (Uri) intent.getExtras().getParcelable("image");
            imageView.setImageURI(uri);
        }
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

}