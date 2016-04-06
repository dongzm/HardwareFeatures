package com.dongzm.motionsensor;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private TextView tvAccelerometer;
    private SensorManager manager;
    //重力加速度值
    private float[] gravity = new float[3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvAccelerometer = (TextView) findViewById(R.id.tvAccelerometer);
        manager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
    }

    //传感器数据发送变化
    @Override
    public void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:     //加速度传感器,排除重z轴上力加速度影响
                final float alpha = 0.8f; //设置一个系数
                //消除杂音
                gravity[0] = alpha * gravity[0] + (1-alpha) * event.values[0];
                gravity[1] = alpha * gravity[1] + (1-alpha) * event.values[1];
                gravity[2] = alpha * gravity[2] + (1-alpha) * event.values[2];
                tvAccelerometer.setText(String.format("加速度\n X: %f \n Y: %f \n  Z: %f", event.values[0]-gravity[0], event.values[1]-gravity[1], event.values[2]-gravity[2]));
                break;
            case Sensor.TYPE_GRAVITY:       //重力传感器
                gravity[0] = event.values[0];
                gravity[1] = event.values[1];
                gravity[2] = event.values[2];
                break;
            case Sensor.TYPE_PROXIMITY:       //临近传感器
                setTitle(event.values[0]+"");
                break;
            case Sensor.TYPE_LIGHT:       //光线传感器
                ((TextView)findViewById(R.id.textView)).setText("光线传感器："+event.values[0]+"f");
                break;
        }
    }

    //传感器精度发生变化
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        //注册传感器
        manager.registerListener(this, manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_UI);//加速度传感器
        manager.registerListener(this, manager.getDefaultSensor(Sensor.TYPE_GRAVITY), SensorManager.SENSOR_DELAY_FASTEST);//重力传感器
        manager.registerListener(this, manager.getDefaultSensor(Sensor.TYPE_PROXIMITY), SensorManager.SENSOR_DELAY_UI);//临近传感器
        manager.registerListener(this, manager.getDefaultSensor(Sensor.TYPE_LIGHT), SensorManager.SENSOR_DELAY_UI);//光线感器
    }

    @Override
    protected void onPause() {
        super.onPause();
        //卸载传感器
        manager.unregisterListener(this);
    }
}
