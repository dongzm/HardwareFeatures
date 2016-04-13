package com.dongzm.systemcamera;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    private ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = (ImageView) findViewById(R.id.imageView);
    }

    public void camera(View view){
        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(i, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        System.out.println("resultCode："+resultCode);
        if (requestCode == 1){
            //确定拍照
            if (resultCode == Activity.RESULT_OK){
                //获取字节流
                Bitmap cameraBitmap = (Bitmap) data.getExtras().get("data");
                imageView.setImageBitmap(cameraBitmap);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
