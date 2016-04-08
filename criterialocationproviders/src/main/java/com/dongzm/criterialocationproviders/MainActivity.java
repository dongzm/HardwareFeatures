package com.dongzm.criterialocationproviders;

import android.app.ListActivity;
import android.content.Context;
import android.location.Criteria;
import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import java.util.List;

public class MainActivity extends ListActivity {

    private LocationManager manager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        //获取LocationManager
        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        //创建一个LocationProvider过滤器
        Criteria criteria = new Criteria();

        //要求LocationProvider必须是免费的
        criteria.setCostAllowed(false);

        //要求LocationProvider能提供高度信息
        criteria.setAltitudeRequired(true);

        //要求LocationProvider能提供方向信息
        criteria.setBearingRequired(true);

        List<String> providerNames = manager.getProviders(criteria,true);

        setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, providerNames));

    }
}
