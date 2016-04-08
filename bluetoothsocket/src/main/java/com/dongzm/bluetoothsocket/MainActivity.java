package com.dongzm.bluetoothsocket;

import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends ListActivity {


    private ArrayAdapter<String> adapter;
    private BluetoothAdapter bluetoothAdapter;
    private Button btnSearch;
    private final UUID MY_UUID = UUID.fromString("123f5678-12a3-34d6-e334-123abc567aef");
    private String NAME = "Bluetooth_Socket";
    private BluetoothDevice device;
    private BluetoothSocket clientSocket;
    private OutputStream os;
    private AcceptThread acceptThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        btnSearch = (Button) findViewById(R.id.btnSearch);
        setListAdapter(adapter);
        //获取蓝牙适配器
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        //如果蓝牙没有打开，而打开蓝牙
        if (!bluetoothAdapter.isEnabled()){
            bluetoothAdapter.enable();
        }
        Set<BluetoothDevice> deviceSet = bluetoothAdapter.getBondedDevices();
        for (BluetoothDevice device: deviceSet){
            adapter.add(device.getName() + "：" +device.getAddress());
        }

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver, filter);
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(receiver, filter);

        //开启接受服务
        acceptThread = new AcceptThread();
        acceptThread.start();
    }

    public void searchBluetooth(View view){
        btnSearch.setText("正在扫描...");
        if (bluetoothAdapter.isDiscovering()){
            bluetoothAdapter.cancelDiscovery();
        }
        bluetoothAdapter.startDiscovery();

    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        String s = adapter.getItem(position);
        String address = s.substring(s.indexOf("：")+1).trim();
        try{
            if (bluetoothAdapter.isDiscovering()){
                bluetoothAdapter.cancelDiscovery();
            }
            try{
                if(device == null){ //远程设备
                    device = bluetoothAdapter.getRemoteDevice(address);
                }
                if (clientSocket == null){
                    clientSocket = device.createRfcommSocketToServiceRecord(MY_UUID); //创建客户端Socket
                    clientSocket.connect();//连接
                    os = clientSocket.getOutputStream(); //获取输出流
                }
            }catch (Exception e){}
            if (os != null){ //说明连接成功
                os.write("发送信息到其他设备".getBytes("utf-8"));
            }
        }catch (Exception e){}
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            Toast.makeText(MainActivity.this,String.valueOf(msg.obj), Toast.LENGTH_LONG).show();
            super.handleMessage(msg);
        }
    };

    //服务端获取数据
    private class AcceptThread extends Thread{
        private BluetoothServerSocket serverSocket;
        private BluetoothSocket socket;
        private InputStream is;
        private OutputStream os;
        public AcceptThread(){
            try {
                serverSocket = bluetoothAdapter.listenUsingRfcommWithServiceRecord(NAME,MY_UUID);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            try {
                socket = serverSocket.accept();
                is = socket.getInputStream();
                os = socket.getOutputStream();
                while (true){
                    byte[] buffer = new byte[128];
                    int count = is.read(buffer);
                    Message msg = new Message();
                    msg.obj = new String(buffer, 0, count, "utf-8");
                    handler.sendMessage(msg);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            //触发搜索蓝牙广播
            if (BluetoothDevice.ACTION_FOUND.equals(action)){
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                //如果没有被绑定过，则添加
                if (device.getBondState() != BluetoothDevice.BOND_BONDED){
                    adapter.add(device.getName() + "：" +device.getAddress());
                }
            }else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){ //停止搜索
                btnSearch.setText("搜索蓝牙");
            }
        }
    };
}
