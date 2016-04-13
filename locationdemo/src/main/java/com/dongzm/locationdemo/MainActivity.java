package com.dongzm.locationdemo;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private LocationManager manager;
    private TextView text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        text = (TextView)findViewById(R.id.text);
        //从GPS获取最近的定位信息
        Location location = manager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        updateView(location);

        manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 8, new LocationListener(){
            @Override
            public void onLocationChanged(Location location) {
                //当GPS信息发生变化时
                updateView(location);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {
                //当GPS Location Provider可用时，更新位置
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                updateView(manager.getLastKnownLocation(provider));
            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        });
    }

    //用来更新TextView信息
    private void updateView(Location location) {
        if (location != null) {
            StringBuilder sb = new StringBuilder();
            sb.append("实时的位置信息\n");
            sb.append("经度：");
            sb.append(location.getLongitude());
            sb.append("\n维度：");
            sb.append(location.getLatitude());
            sb.append("\n高度：");
            sb.append(location.getAltitude());
            sb.append("\n速度：");
            sb.append(location.getSpeed());
            sb.append("\n方向：");
            sb.append(location.getBearing());
            System.out.println(sb.toString());
            text.setText(sb.toString());
        }
    }
}
