package saco.ProjectFireTruckV2.Activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

import saco.ProjectFireTruckV2.R;
import saco.ProjectFireTruckV2.StaticFiles.IPAddress;
import saco.ProjectFireTruckV2.hotspot_utilities.ClientScanResult;
import saco.ProjectFireTruckV2.hotspot_utilities.FinishScanListener;
import saco.ProjectFireTruckV2.hotspot_utilities.HotspotManager;
import saco.ProjectFireTruckV2.hotspot_utilities.WIFI_AP_STATE;

public class DiscoverHotSpotActivity extends Activity {

    ListView discoveredDevices;
    Button scanButton;
    ArrayAdapter<String> FoundDeviceList;
    HotspotManager hotspotManager;
    TextView textView1;
    public static boolean discoverHotSpotActivityState = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discover_hs);

        textView1 = (TextView)findViewById(R.id.displayText);

        discoveredDevices = (ListView)findViewById(R.id.discoveredDevicesList);
        FoundDeviceList = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,0);
        discoveredDevices.setAdapter(FoundDeviceList);


        discoveredDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String string = FoundDeviceList.getItem(position);
                String[] lines = string.split("\n");
                System.out.println("Before "+ Arrays.toString(lines));
                lines[0] = lines[0].replace("IP addr: ", "");
                System.out.println("After "+ Arrays.toString(lines));

                IPAddress.setIP(lines[0]);
                MainActivity.connectButton.performClick();
                finish();
            }
        });

        hotspotManager = new HotspotManager(this);

        scan();

        scanButton = (Button)findViewById(R.id.scanButton);
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FoundDeviceList.clear();
                textView1.setText("");
                scan();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        discoverHotSpotActivityState = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        discoverHotSpotActivityState = false;
    }

    private void scan() {
        hotspotManager.getClientList(false, new FinishScanListener() {

            @Override
            public void onFinishScan(final ArrayList<ClientScanResult> clients) {

                if (hotspotManager.getWifiApState().equals(WIFI_AP_STATE.WIFI_AP_STATE_ENABLED)){
                    textView1.setText("HotSpot state: " + "ENABLED" + "\n");
                }

                else {textView1.setText("HotSpot state: " + "DISABLED" + "\n");}

                //MainPage.mToast.setText(hotspotManager.getHotspotState()););

                for (ClientScanResult clientScanResult : clients) {
                    FoundDeviceList.add("IP addr: " + clientScanResult.getIpAddr() + "\n"
                            //+ "Device: " + clientScanResult.getDevice() + "\n"
                            + "Mac addr: " + clientScanResult.getHWAddr() );
                            //+ "isReachable: " + clientScanResult.isReachable());
                }
                if (clients.size() == 0){
                    FoundDeviceList.add("No Devices connected to this HotSpot");
                    discoveredDevices.setEnabled(false);
                }
                else {
                    discoveredDevices.setEnabled(true);
                }
            }
        });
    }

}
