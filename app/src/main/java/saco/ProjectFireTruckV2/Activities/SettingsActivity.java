package saco.ProjectFireTruckV2.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import saco.ProjectFireTruckV2.R;
import saco.ProjectFireTruckV2.etc_utilities.ReadWrite;
import saco.ProjectFireTruckV2.list_adapters.SettingsActivity_listAdapter;

import static saco.ProjectFireTruckV2.Activities.MainActivity.WIFI_MODE;
import static saco.ProjectFireTruckV2.Activities.MainActivity.currentMode;

public class SettingsActivity extends AppCompatActivity {

    //Variables to be declared (self-explanatory)
    private SettingsActivity_listAdapter listAdapter;
    private ListView listView;
    private ArrayList<String> items;
    //List of items on start up
    private String[] lists = {"Scan for Devices (HOTSPOT)","Scan for Networks (WiFi)", "IP address","Check for updates","Reset Log","Debug Modes", "About"};

    /**
     * Create activity on start up
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_page);
        setTitle("Configuration");
        initialise();
        clicksListener();

        System.out.println(String.valueOf(currentMode));
        Log.e("check mode", String.valueOf(currentMode));
        Log.e("check mode", String.valueOf(WIFI_MODE));
    }


    /**
     * Function to initialise all variables and adding items into the settings_default menu
     */
    public void initialise(){
        listView = (ListView)findViewById(R.id.listView);
        listAdapter = new SettingsActivity_listAdapter(this,android.R.layout.simple_list_item_1);

        listView.setAdapter(listAdapter);
        items = new ArrayList<>();
        addItems();

    }


    /**
     * Function to listen for all button clicks
     */
    public void clicksListener() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String itemSelected = listAdapter.getItem(position);
                //If any one of the items is selected, start the next activity
                switch (itemSelected) {
                    case "Check for updates":
                        Intent intent = new Intent(getApplicationContext(), CheckForUpdatesActivity.class);
                        startActivity(intent);
                        break;
                    case "About":
                        Intent intentAbout = new Intent(getApplicationContext(), AboutAppActivity.class);
                        startActivity(intentAbout);
                        break;
                    case "Reset Log":
                        ReadWrite.writeToFile("", getApplicationContext(), "Replace", "config.txt");
                        toastIt("Log has been cleared");
                        break;
                    case "IP address":
                        Intent intentIP = new Intent(getApplicationContext(), SetIPActivity.class);
                        startActivity(intentIP);
                        break;
                    case "Scan for Devices (HOTSPOT)":
                        Intent intentScan = new Intent(getApplicationContext(), DiscoverHotSpotActivity.class);
                        startActivity(intentScan);
                        break;
                    case "Scan for Networks (WiFi)":
                        Intent intentNetwork = new Intent(getApplicationContext(), WifiActivity.class);
                        startActivity(intentNetwork);
                        break;
                    case "Debug Modes":
                        Intent intentDebugMode = new Intent(getApplicationContext(), DebugModeActivity.class);
                        startActivity(intentDebugMode);
                        break;

                    default:
                        break;
                }
            }
        });
    }

    /**
     * Function to add items into the settings_default menu(list adapter form)
     */
    private void addItems(){
        for(int i=0;i<lists.length;i++){
            items.add(lists[i]);
        }
        for(int i=0;i<items.size();i++){
            listAdapter.add(items.get(i));
        }
    }

    /**
     * Function to easily call for toast messages
     * @param message
     */
    public void toastIt(String message){
        MainActivity.mToast.setText(message);
        MainActivity.mToast.show();
    }
}




