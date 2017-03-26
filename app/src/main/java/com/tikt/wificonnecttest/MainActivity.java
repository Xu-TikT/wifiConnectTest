package com.tikt.wificonnecttest;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        wifiList = (ListView) findViewById(R.id.wifi_list);
        init();

    }


    private void init() {
        wifiUtil = new WifiUtil(getApplicationContext());
        wifiUtil.openWifi();
        wifiUtil.startScan();
        scanResultList = wifiUtil.getmWifiList();
        wifiList.setAdapter(new MyAdapter(this, scanResultList));

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
            signalStrenth.setText(String.valueOf(Math.abs(scanResult.level)));
            ImageView imageView = (ImageView) view.findViewById(R.id.im_wifi);
            //判断信号强度，显示对应的指示图标
            if (Math.abs(scanResult.level) > 100) {
                imageView.setImageDrawable(getResources().getDrawable(R.mipmap.wifi_fourth));
            } else if (Math.abs(scanResult.level) > 80) {
                imageView.setImageDrawable(getResources().getDrawable(R.mipmap.wifi_third));
            } else if (Math.abs(scanResult.level) > 70) {
                imageView.setImageDrawable(getResources().getDrawable(R.mipmap.wifi_third));
            } else if (Math.abs(scanResult.level) > 60) {
                imageView.setImageDrawable(getResources().getDrawable(R.mipmap.wifi_second));
            } else if (Math.abs(scanResult.level) > 50) {
                imageView.setImageDrawable(getResources().getDrawable(R.mipmap.wifi_second));
            } else {
                imageView.setImageDrawable(getResources().getDrawable(R.mipmap.wifi_first));
            }
            return view;
        }

    }
}
