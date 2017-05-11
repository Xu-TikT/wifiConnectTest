package com.tikt.wificonnecttest;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ListView wifiList;
    private WifiUtil wifiUtil;
    private List<ScanResult> scanResultList;//Return the results of the latest access point scan

    private List<WifiConfiguration> wifiConfigurationList = new ArrayList<>();

    private boolean search_or_not = true;

    private View dialog_view;
    private String SSID = "SSID";
    private String ContentSSID = "ContentSSID";//连接上的ssid
    private MyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        wifiList = (ListView) findViewById(R.id.wifi_list);
        init();
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        ContentSSID = wifiManager.getConnectionInfo().getSSID().replace("\"", "");
        SSID = wifiManager.getConnectionInfo().getSSID().replace("\"", "");
        checknet();
    }


    private void init() {
        getListener();
        wifiUtil = new WifiUtil(getApplicationContext());
        wifiUtil.openWifi();
        wifiUtil.startScan();
        scanResultList = wifiUtil.getmWifiList();
        adapter = new MyAdapter(this, scanResultList);
        wifiList.setAdapter(adapter);

        wifiList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.e("SSID---->", scanResultList.get(i).SSID);
                Log.e("wifiConfigList---->", wifiConfigurationList.size() + "");
//                for(int a = 0; a< wifiConfigurationList.size(); a++){
//                    if(wifiConfigurationList.get(a).SSID.equals("\""+ scanResultList.get(i).SSID+"\"")){
//                        int id= wifiConfigurationList.get(a).networkId;
//                        Log.e("networkId--->",id+"");
//                        wifiManager.enableNetwork(id,true);
//                        Toast.makeText(MainActivity.this,"连接中",Toast.LENGTH_SHORT).show();
//                        search_or_not=true;
//                        Log.e("SSId---->", wifiConfigurationList.get(a).SSID+"");
//                        Log.e("netId---->", wifiConfigurationList.get(a).networkId+"");
//                        Log.e("在这儿----","匹配");
//                        break;
//                    }else{
//                        Log.e("SSId---->", wifiConfigurationList.get(a).SSID+"");
//                        Log.e("netId---->", wifiConfigurationList.get(a).networkId+"");
//                        Log.e("在这儿----","不匹配");
//                        wifiManager.disableNetwork(wifiConfigurationList.get(a).networkId);
//                        search_or_not=false;
//                    }
//                }
                Log.e("search---->", search_or_not + "");
                SSID = scanResultList.get(i).SSID;
                Log.w("SSID", "click==" + SSID);
                showDialog(scanResultList.get(i).SSID);


            }
        });

        wifiList.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                //TODO 长按显示清楚网络、修改密码

                return false;
            }
        });
    }


    /**
     * 没有配置过的WiFi，需要输入密码连接
     *
     * @param wifi_name
     */
    private void showDialog(final String wifi_name) {
        dialog_view = View.inflate(this, R.layout.password_dialog, null);
        TextView tv_wifi_name = (TextView) dialog_view.findViewById(R.id.tv_use_wifi_name);
        final EditText editText = (EditText) dialog_view.findViewById(R.id.ed_pass);
        tv_wifi_name.setText(wifi_name + "");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);  //先得到构造器
        builder.setView(dialog_view);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() { //设置确定按钮
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (editText.getText().toString().trim().length() == 0) {
                    Toast.makeText(MainActivity.this, "........", Toast.LENGTH_SHORT).show();
                } else {
                    CreateNewConnect(wifi_name, editText.getText().toString().trim());
                }

            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() { //设置取消按钮
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        //参数都设置完成了，创建并显示出来
        builder.create().show();
    }

    private void CreateNewConnect(String SSID, String pass) {
        wifiUtil.addNetWork(wifiUtil.createWifiInfo(SSID, pass, pass.length() == 0 ? 1 : 3));

    }

    protected void showToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public class MyAdapter extends BaseAdapter {

        LayoutInflater inflater;
        List<ScanResult> list;

        public MyAdapter(Context context, List<ScanResult> list) {
            this.inflater = LayoutInflater.from(context);
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = null;
            view = inflater.inflate(R.layout.item_wifi, null);
            ScanResult scanResult = list.get(position);
            TextView textView = (TextView) view.findViewById(R.id.tv_wifi_name);
            textView.setText(scanResult.SSID);
            TextView signalStrenth = (TextView) view.findViewById(R.id.tv_status);
            Log.w("scanResult.SSID", scanResult.SSID);
            if (ContentSSID.equals(scanResult.SSID)) {
                signalStrenth.setText("已连接");
            } else {
                signalStrenth.setText("");

            }
            ImageView imageView = (ImageView) view.findViewById(R.id.im_wifi);
            int wifiSignalLevel = scanResult.level;
            //判断信号强度，显示对应的指示图标
            //level根据数值可以分为5个等级的信号强弱：
//            Level>-50           信号最强4格
//            -50<Level<-65  信号3格
//            -65<Level<-75  信号2格
//            -75<Level<-90  信号1格
//            -90<Level   信号0格
            if (scanResult.level >= -60) {
                imageView.setImageDrawable(getResources().getDrawable(R.mipmap.wifi_fourth));
            } else if (-60 > wifiSignalLevel && wifiSignalLevel >= -65) {
                imageView.setImageDrawable(getResources().getDrawable(R.mipmap.wifi_third));
            } else if (-65 > wifiSignalLevel && wifiSignalLevel >= -75) {
                imageView.setImageDrawable(getResources().getDrawable(R.mipmap.wifi_third));
            } else if (-75 > wifiSignalLevel && wifiSignalLevel >= -90) {
                imageView.setImageDrawable(getResources().getDrawable(R.mipmap.wifi_second));
            } else if (wifiSignalLevel < -90) {
                imageView.setImageDrawable(getResources().getDrawable(R.mipmap.wifi_second));
            } else {
                imageView.setImageDrawable(getResources().getDrawable(R.mipmap.wifi_first));
            }
            return view;
        }

    }

    private void getListener() {

        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(WifiManager.RSSI_CHANGED_ACTION); //信号强度变化
        mFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION); //网络状态变化
        mFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION); //wifi状态，是否连上，密码
        mFilter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);  //是不是正在获得IP地址
        mFilter.addAction(WifiManager.NETWORK_IDS_CHANGED_ACTION);
        mFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);//连上与否
//        mFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);//连上与否
        registerReceiver(mBroadcastReceiver, mFilter);
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION)) {
                int linkWifiResult = intent.getIntExtra(WifiManager.EXTRA_SUPPLICANT_ERROR, 123);
                if (linkWifiResult == WifiManager.ERROR_AUTHENTICATING) {
                    Log.e("TAG", "wifi密码错误广播==" + linkWifiResult);
                    showToast(MainActivity.this, "密码错误");
                    wifiUtil.disconnectWifi(wifiUtil.getWcgID());
                }

                if (intent.getBooleanExtra(WifiManager.EXTRA_SUPPLICANT_CONNECTED, false)) {
                    Log.w("TAG", "连接成功: ==");
                }
                Log.e("Tag", "SUPPLICANT_STATE_CHANGED_ACTION-------->" + action);
                Log.e("Tag", "SUPPLICANT_STATE_CHANGED_ACTION-------->" + intent.toString());
            }
            if (action.equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {

                Log.e("Tag", "WIFI_STATE_CHANGED_ACTION-------->" + action);
                Log.e("Tag", "WIFI_STATE_CHANGED_ACTION-------->" + intent.toString());
                Log.i("TAG", "NETWORK_STATE_CHANGED_ACTION==" + intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 123));
                if (intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 123) == WifiManager.WIFI_STATE_ENABLED) {

                    Log.i("111111>>>>>>>>>>", "成功");
                } else if (intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 123) == WifiManager.WIFI_STATE_DISABLED) {

                    Log.i("22222222>>>>>>>>>>", "失败");
                }
            }
            if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction())) {


                if (checknet()) {
                    Log.i("111111>>>>>>>>>>", "成功");
                } else {
                    Log.i("22222222>>>>>>>>>>", "失败");
                }
                Log.e("Tag", "NETWORK_STATE_CHANGED_ACTION-------->" + action);
                Log.e("Tag", "NETWORK_STATE_CHANGED_ACTION-------->" + intent.toString());
            }
            Log.i("Tag", "action-------->" + action);
            Log.i("Tag", "intent.toString-------->" + intent.toString());
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
    }

    /**
     * 获取网络
     */
    private NetworkInfo networkInfo;

    /**
     * 监测网络链接
     *
     * @return true 链接正常 false 链接断开
     */
    private boolean checknet() {
        ConnectivityManager connManager = (ConnectivityManager) this.getSystemService(CONNECTIVITY_SERVICE);
        // 获取代表联网状态的NetWorkInfo对象
        networkInfo = connManager.getActiveNetworkInfo();
        if (null != networkInfo) {
            WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
            Log.w("SSID", wifiManager.getConnectionInfo().getSSID());
            ContentSSID = wifiManager.getConnectionInfo().getSSID().replace("\"", "");
            SSID = wifiManager.getConnectionInfo().getSSID().replace("\"", "");
            adapter.notifyDataSetChanged();
            return networkInfo.isAvailable();
        }
        return false;
    }

}
