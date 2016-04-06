package com.dongzm.listsensor;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    TextView tvSensor;
    SensorManager manager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvSensor = (TextView) findViewById(R.id.tvSensor);
        manager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> sensorList = manager.getSensorList(Sensor.TYPE_ALL);
        for (Sensor s: sensorList){
            tvSensor.append(s.getName() + "\n");
        }
    }
}
