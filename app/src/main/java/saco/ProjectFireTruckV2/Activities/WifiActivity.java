package saco.ProjectFireTruckV2.Activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import saco.ProjectFireTruckV2.R;
import saco.ProjectFireTruckV2.list_adapters.WifiActivity_listAdapter;

public class WifiActivity extends AppCompatActivity {

    public static int connectedPosition = 99;

    private static final int REQUEST_WIFI = 1;
    private ListView listView;
    private WifiActivity_listAdapter<String> listAdapter;
    private WifiManager wifi;
    private String[] wifis;
    private WifiScanReceiver wifiReceiver;
    private String networkSSID;
    private String networkPass;
    private int itemPos;
    private Button scan;
    private int count = 0;
    private ConnectivityManager connManager;
    private NetworkInfo mWifi;
    List<WifiConfiguration> configurationList;
    private String filteredString;
    private String filteredConfig;
    private Toast mToast;
    private WifiInfo wInfo;
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
                NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                if(networkInfo.isConnected()){
                    connectedPosition = 99; //reset connectedPosition
                    listAdapter.clear();
                    retrieveConfig();
                    wifi.startScan();
                }else if(networkInfo.getDetailedState() == NetworkInfo.DetailedState.FAILED){
                    mToast.setText("Connection Failed");
                    mToast.show();
                    Log.e("BC","Connecton Failed");
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi);
        setTitle("Connect to a Network:");
        initialise();
        retrieveConfig();
        scanForWifi();
        clicksListener();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }

    public void initialise(){
        mToast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT);
        scan = (Button)findViewById(R.id.button9);
        listView = (ListView)findViewById(R.id.listView3);
        listAdapter = new WifiActivity_listAdapter<>(this,android.R.layout.simple_list_item_1);
        listView.setAdapter(listAdapter);
        wifi = (WifiManager)getSystemService(Context.WIFI_SERVICE);
        wifiReceiver = new WifiScanReceiver();
        connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        registerReceiver(broadcastReceiver, intentFilter);
    }

    public void retrieveConfig(){
        configurationList = wifi.getConfiguredNetworks();
        mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if(mWifi.isConnected()){
            wInfo = wifi.getConnectionInfo();
            for(int i=0;i<configurationList.size();i++){
                filteredString = configurationList.get(i).SSID;
                if(filteredString.equals(wInfo.getSSID())) {
                    listAdapter.add(filteredString.replaceAll("\"", "") + "\n" + "Connected");
                    connectedPosition = i;
                }else{
                    listAdapter.add(filteredString.replaceAll("\"", "") + "\n" + "Saved, Secured");
                }
            }
        }else {
            for (int i = 0; i < configurationList.size(); i++) {
                filteredString = configurationList.get(i).SSID;
                listAdapter.add(filteredString.replaceAll("\"", "") + "\n" + "Saved, Secured");
            }
        }
    }

    public void clicksListener(){
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(listAdapter.getItem(position).contains("\n"+"Saved, Secured")){
                    networkSSID = configurationList.get(position).SSID;
                    mToast.setText("Attempting connection to " + networkSSID);
                    mToast.show();
                    connectedPosition = 99; //reset the position
                    int netId = configurationList.get(position).networkId;
                    wifi.disconnect();
                    wifi.enableNetwork(netId, true);
                    wifi.reconnect();
                    listAdapter.clear();
                    listView.setAdapter(listAdapter);
                    retrieveConfig();
                    wifi.startScan();
                }else if(listAdapter.getItem(position).contains("\n"+"Connected")){
                    mToast.setText("Already connected to "+listAdapter.getItem(position).replaceAll("\n"+"Connected",""));
                    mToast.show();
                }else{
                    Intent intent = new Intent(getApplicationContext(), PasswordActivity.class);
                    networkSSID = listAdapter.getItem(position);
                    intent.putExtra("ssid", networkSSID);
                    startActivityForResult(intent, REQUEST_WIFI);
                    itemPos = position;
                }
            }
        });

        scan.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                connectedPosition = 99; //reset connectedPosition
                listAdapter.clear();
                listView.setAdapter(listAdapter);
                retrieveConfig();
                wifi.startScan();
            }
        });
    }

    public void scanForWifi(){
        connectedPosition = 99; //reset connectedPosition
        wifi.startScan();
        scanTimeout(7);
    }

    public void scanTimeout(final int sec){
        new CountDownTimer(1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {}

            @Override
            public void onFinish() {
                count++;
                if (count != sec) {
                    scanTimeout(sec);
                }
                else {
                    count = 0;
                }
            }
        }.start();
    }


    @Override
    protected void onResume() {
        registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        super.onResume();
    }

    @Override
    protected void onPause() {
        unregisterReceiver(wifiReceiver);
        super.onPause();
    }

    private class WifiScanReceiver extends BroadcastReceiver {
        public void onReceive(Context c, Intent intent) {
            List<ScanResult> wifiScanList = wifi.getScanResults();
            wifis = new String[wifiScanList.size()];

            for(int i = 0; i < wifiScanList.size(); i++){
                wifis[i] = ((wifiScanList.get(i)).SSID.toString());
                if(wifis[i].equals("")){
                    wifis[i] = "Null";
                }
                if(filterResults(wifis[i])==false && wifis[i]!=null) {
                    listAdapter.add(wifis[i]);
                }
            }
        }
    }

    public boolean filterResults(String ssid){
        for(int i=0;i<listAdapter.getCount();i++){
            filteredConfig = listAdapter.getItem(i);
            if(listAdapter.getItem(i).equals(ssid) || filteredConfig.replaceAll("\n"+"Saved, Secured","").equals(ssid) || filteredConfig.replaceAll("\n"+"Connected","").equals(ssid)){
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_WIFI){
            if(resultCode == RESULT_OK){
                String password = data.getStringExtra("password");
                networkPass = password;
                networkSSID = listAdapter.getItem(itemPos);
                connectWifi();
            }
        }
    }

    public void connectWifi(){
        WifiConfiguration wc = new WifiConfiguration();
        wc.SSID = String.format("\"%s\"", networkSSID);
        wc.preSharedKey = String.format("\"%s\"",networkPass);
        int nedID = wifi.addNetwork(wc);
        wifi.disconnect();
        wifi.enableNetwork(nedID, true);
        wifi.reconnect();
        mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        finish();
    }
}




