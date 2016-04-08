package com.dongzm.alllocactionproviders;

import android.app.ListActivity;
import android.content.Context;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import java.util.List;

public class MainActivity extends ListActivity {

    private LocationManager locationManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);

        //获取所有的LocationManager
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //获取所有的locationProvider
        List<String> providerNames =  locationManager.getAllProviders();
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, providerNames);
        setListAdapter(arrayAdapter);
    }
}
