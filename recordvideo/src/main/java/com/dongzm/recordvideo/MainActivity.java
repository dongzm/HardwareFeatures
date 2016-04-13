package com.dongzm.recordvideo;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.MediaController;
import android.widget.VideoView;

public class MainActivity extends AppCompatActivity {

    private VideoView videoView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        videoView = (VideoView) findViewById(R.id.videoView);
    }

    public void recordVideo(View view){
        Intent i = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        startActivityForResult(i, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == Activity.RESULT_OK){
            //视频已经存在本地，返回系统数据库uri中存储路径
            Uri uri = data.getData();
            Cursor cursor = this.getContentResolver().query(uri,null,null,null,null);
            if (cursor.moveToFirst()){
                String videoPath = cursor.getString(cursor.getColumnIndex("_data"));
                videoView.setVideoURI(Uri.parse(videoPath));
                videoView.setMediaController(new MediaController(this));
                videoView.start();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
