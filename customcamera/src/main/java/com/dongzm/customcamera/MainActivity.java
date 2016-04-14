package com.dongzm.customcamera;

import android.content.Context;
import android.hardware.Camera;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Camera camera;
    private Preview preview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN); //全屏
        preview = new Preview(this);
        setContentView(preview);
    }

    @Override
    protected void onResume() {
        super.onResume();
        camera = Camera.open();//Activity获取焦点时打开照相机
        preview.setCamera(camera);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //Activity失去焦点时释放资源
        if (camera != null) {
            camera.release();
            camera = null;
            preview.setCamera(null);
        }
    }

    class Preview extends ViewGroup implements SurfaceHolder.Callback, View.OnClickListener {

        SurfaceView surfaceView;
        SurfaceHolder holder;
        Camera.Size mPreviewSize;
        List<Camera.Size> mSupportdPreviewSize;
        Camera camera;
        Context context;

        public Preview(Context context) {
            super(context);
            this.context = context;
            surfaceView = new SurfaceView(context);
            addView(surfaceView);
            holder = surfaceView.getHolder();
            holder.addCallback(this);
        }

        public void setCamera(Camera camera) {
            this.camera = camera;
            if (this.camera != null) {
                mSupportdPreviewSize = this.camera.getParameters().getSupportedPreviewSizes();
                requestLayout();//重新调整布局
            }
        }

        @Override
        protected void onLayout(boolean changed, int l, int t, int r, int b) {
            if (changed && getChildCount() > 0) {
                final View child = getChildAt(0);
                int width = r - l;
                int height = b - t;
                int previewWidth = width;
                int previewHeight = height;
                if (mPreviewSize != null) {
                    previewHeight = mPreviewSize.height;
                    previewWidth = mPreviewSize.width;
                }
                //手机屏幕的宽高大于采集图形的宽高比
                if (width * previewHeight > height * previewWidth) {
                    final int scaledChildWidth = previewWidth * height / previewHeight;
                    child.layout((width - scaledChildWidth) / 2, 0, (width + scaledChildWidth) / 2, height);
                } else {
                    final int scaledChildHeight = previewHeight * width / previewWidth;
                    child.layout(0, (height - scaledChildHeight) / 2, width, (height + scaledChildHeight) / 2);
                }
            }
        }

        private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
            double ASPECT_TOLERANCE = 0.1;
            double targetRatio = (double) w / h;
            if (sizes == null) return null;
            Camera.Size optimalSize = null;
            double minDiff = Double.MAX_VALUE;
            int targetHeight = h;
            for (Camera.Size size : sizes) {
                double ratio = (double) size.width / size.height;
                if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
            if (optimalSize == null) {
                for (Camera.Size size : sizes) {
                    if (Math.abs(size.height - targetHeight) < minDiff) {
                        optimalSize = size;
                        minDiff = Math.abs(size.height - targetHeight);
                    }
                }
            }
            return optimalSize;
        }

        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int width = resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec);
            int height = resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec);

            setMeasuredDimension(width, height);
            if (mSupportdPreviewSize != null){
                mPreviewSize = getOptimalPreviewSize(mSupportdPreviewSize,width,height);
            }
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            try {
                if (camera != null) {
                    camera.setPreviewDisplay(holder);//将SurfaceView和camera绑定
                }
            } catch (Exception e) {
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            Camera.Parameters parameters = camera.getParameters();
            parameters.setPictureSize(mPreviewSize.width, mPreviewSize.height);//预览尺寸的长宽比，不能随便设置
            camera.setParameters(parameters);
            camera.startPreview();//开始预览
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            if (camera != null) {
                camera.stopPreview();//停止预览
            }
        }

        //拍照后的回掉
        private Camera.PictureCallback pictureCallback = new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                camera.startPreview();//重新开始预览
                //写入文件夹流
                File pictureFile = new File("/sdcard/image.jpg");
                try {
                    FileOutputStream fos = new FileOutputStream(pictureFile);
                    fos.write(data);
                    fos.close();
                }catch (Exception e){}
            }
        };

        @Override
        public void onClick(View v) {
            camera.takePicture(null, null, pictureCallback);
        }
    }
}
