package com.dongzm.searchbluetoothdevice;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private BluetoothAdapter bluetoothAdapter;
    private TextView tvBluetooth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvBluetooth = (TextView) findViewById(R.id.tvBluetooth);
        //获取蓝牙适配器
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        //事先配对过的，先加入页面中
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0){
            for (BluetoothDevice device: pairedDevices){
                tvBluetooth.append(device.getName() + ":" + device.getAddress());
            }
        }
        //注册广播，用于搜索蓝牙
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);//找到一个设置发送一个广播
        this.registerReceiver(receiver,filter);
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(receiver,filter);
    }

    public void openBluetooth(View view){
        Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivity(enableIntent);
    }

    public void searchBluetooth(View view){
        setProgressBarVisibility(true);
        setTitle("正在扫描...");
        tvBluetooth.setText("");
        //打开蓝牙
        bluetoothAdapter.enable();
        //如果正在搜索，先关闭
        if (bluetoothAdapter.isDiscovering()){
            bluetoothAdapter.cancelDiscovery();
        }
        //开始搜索
        bluetoothAdapter.startDiscovery();
    }

    //广播接收器
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            //获取找到的广播
            if (BluetoothDevice.ACTION_FOUND.equals(action)){
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                //添加没有被绑定的，绑定的在onCreat里面已经添加了
                if (device.getBondState() != BluetoothDevice.BOND_BONDED){
                    tvBluetooth.append(device.getName() + ":" + device.getAddress() + "\n");
                }
            }else if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) { //结束搜索
                setProgressBarVisibility(false);
                setTitle("扫描完成");
                //打开蓝牙
                bluetoothAdapter.disable();
            }
        }
    };
}
