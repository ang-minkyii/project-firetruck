package saco.ProjectFireTruckV2.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.ServerSocket;

import saco.ProjectFireTruckV2.ConfirmationPopUps.OnBackPressedRequestActivity;
import saco.ProjectFireTruckV2.ConfirmationPopUps.RequestChangeWifiStateActivity;
import saco.ProjectFireTruckV2.Handlers.MainHandler;
import saco.ProjectFireTruckV2.R;
import saco.ProjectFireTruckV2.StaticFiles.IPAddress;
import saco.ProjectFireTruckV2.StaticFiles.TCPSocket;
import saco.ProjectFireTruckV2.TCP.ConnectThread;
import saco.ProjectFireTruckV2.TCP.ServerThread;
import saco.ProjectFireTruckV2.etc_utilities.Loading;
import saco.ProjectFireTruckV2.etc_utilities.ReadWrite;
import saco.ProjectFireTruckV2.hotspot_utilities.HotspotManager;

public class MainActivity extends AppCompatActivity {

    public static int DEBUG_MODE = 99;
    public static final int DEBUG_AS_ERROR = 1;
    public static final int DEBUG_STANDARD = 0;
    public static final int DEBUG_OFF = 99;

    public static Thread connectThread = null;
    public static Thread serverThread = null;
    public static Thread readThread = null;
    public static Handler mainHandler;
    public static MainActivity context;
    public static Activity mainActivity;
    public static ServerSocket serverSocket;

    public static ImageButton connectButton;
    public static ImageButton chatButton;
    public static ImageButton sendImageButton;

    public static Toast mToast;
    public static Toast toastForText;
    public static Toast toastForImage;
    public static LayoutInflater inflater;
    public static View toastView;
    public static ImageView toastImage;


    public static TextView ipText;

    private static final int CHECKWIFIHOTSPOT = 100;
    public static final int WIFI_MODE = 0;
    public static final int HOTSPOT_MODE = 1;
    public static final int NO_MODE = 2;
    public static int currentMode = NO_MODE;

    //Request <code>
    private static final int SETIP = 5;
    private static final int BACK_PRESSED = 87;

    public static int count = 0;

    public ImageButton logButton;
    public ImageButton settingsButton;
    public ImageView image;
    public Drawable firetruck;
    public RadioButton wifiButton;
    public RadioButton hotspotButton;
    public RadioGroup radioGroup;
    public RadioButton initiateButton;

    private HotspotManager hotspot;
    private WifiManager wifi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialise();
        toastInitialise();
        clicksListener();

        mainHandler = MainHandler.mainHandler;

        MainActivity.context = this;
        MainActivity.mainActivity = this;

        this.serverThread = new Thread(new ServerThread());
        this.serverThread.start();

        checkApWifi(NO_MODE);

        if(!ReadWrite.readFromFile(getApplicationContext(), "tutorial.txt").equals("Checked"+"\n")){
            Intent intent = new Intent(getApplicationContext(), InstructionsActivity.class);
            startActivity(intent);
        }
    }

    private void toastInitialise() {
        inflater = getLayoutInflater();
        toastView = inflater.inflate(R.layout.toast_layout,
                (ViewGroup) findViewById(R.id.relativeLayout1));
        toastImage = (ImageView)toastView.findViewById(R.id.toastImage);
        toastForImage = new Toast(getApplicationContext());
        toastForImage.setView(toastView);
        toastForImage.setGravity(Gravity.CENTER,0,0);
        toastForText = Toast.makeText(getApplicationContext(),"",Toast.LENGTH_SHORT);
    }

    /**
     * Function to initialise all the variables needed.
     */
    public void initialise(){
        image = (ImageView)findViewById(R.id.imageView2);
        ipText = (TextView)findViewById(R.id.ipAddress);
        mToast = Toast.makeText(getApplicationContext(),"",Toast.LENGTH_SHORT);
        connectButton = (ImageButton)findViewById(R.id.wifi_button);
        chatButton = (ImageButton)findViewById(R.id.chat_button);
        sendImageButton = (ImageButton)findViewById(R.id.gallery_button);
        logButton = (ImageButton)findViewById(R.id.log_button);
        settingsButton = (ImageButton) findViewById(R.id.settings_button);
        wifiButton = (RadioButton)findViewById(R.id.radioButton2);
        hotspotButton = (RadioButton)findViewById(R.id.radioButton);
        initiateButton = (RadioButton)findViewById(R.id.radioButton3);
        radioGroup = (RadioGroup)findViewById(R.id.radiogroup);

        firetruck = getResources().getDrawable(R.drawable.firetruck2);
        image.setImageDrawable(firetruck);
        image.setAlpha(30);

        radioGroup.clearCheck();
        chatButton.setEnabled(false);
        sendImageButton.setEnabled(false);


        wifi = (WifiManager)getSystemService(Context.WIFI_SERVICE);
        hotspot = new HotspotManager(this);


    }

    /**
     * Function to listen for all incoming inputs, mainly button clicks
     */
    public void clicksListener(){

        //Listens for clicks on the connect button
        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentMode == NO_MODE) {
                    mToast.setText("Please select a mode (bottom left corner)");
                    mToast.show();
                } else if ((!wifi.isWifiEnabled() && currentMode == WIFI_MODE) | (!hotspot.isHotspotEnabled() && currentMode == HOTSPOT_MODE)) {
                    checkApWifi(currentMode);
                } else {
                    if (TCPSocket.getSocket() != null) {
                        connectButton.setEnabled(false);
                        if (connectThread != null) {
                            connectThread.interrupt();
                            connectThread = null;
                        }
                    } else if (IPAddress.getIP().length() < 4) {
                        Log.d("ConnectButton Clicked", "IP address invalid, unable to connect.");
                        MainActivity.mainHandler.obtainMessage(MainHandler.FAILED_CONNECT).sendToTarget();
                    } else {
                        connect();
                        connectButton.setEnabled(false);
                    }

                }
            }
        });

        //Listen for clicks on the chat button
        chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                startActivity(intent);
            }
        });

        //Listen for clicks on the send_default photo button
        sendImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PhotoActivity.class);
                startActivity(intent);
            }
        });

        //Listen for clicks on the log button
        logButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LogActivity.class);
                startActivity(intent);
            }
        });

        //Listen for clicks on the setting button
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(intent);
            }
        });

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if (checkedId == wifiButton.getId()) {
                    //currentMode = WIFI_MODE;
                    hotspotButton.setTextColor(Color.parseColor("#d3d3d3"));
                    wifiButton.setTextColor(Color.parseColor("#3F51B5"));
                    checkApWifi(WIFI_MODE);
                } else if (checkedId == hotspotButton.getId()) {
                    //currentMode = HOTSPOT_MODE;
                    wifiButton.setTextColor(Color.parseColor("#d3d3d3"));
                    hotspotButton.setTextColor(Color.parseColor("#3F51B5"));
                    checkApWifi(HOTSPOT_MODE);
                } else if (checkedId == initiateButton.getId()) {
                    currentMode = NO_MODE;
                    wifiButton.setTextColor(Color.parseColor("#d3d3d3"));
                    hotspotButton.setTextColor(Color.parseColor("#d3d3d3"));
                    ipText.setText("");
                }
            }
        });
    }

    public boolean onCreateOptionsMenu(final Menu menu) {
        menu.add(0, 0, 0, "HotSpot/WiFi on");
        menu.add(0, 1, 0, "HotSpot/WiFi off");
        menu.add(0, 2, 0, "Enable instruction");
        menu.add(0, 3, 0, "Show/Hide my IP Address");
        return super.onCreateOptionsMenu(menu);
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case 0:
                if (currentMode == HOTSPOT_MODE) {
                    if (!hotspot.isHotspotEnabled()) {
                        mToast.setText("Hotspot is turning on");
                        mToast.show();
                        hotspot.setWifiApEnabled(null, true);
                    } else {
                        mToast.setText("Hotspot is already turned on");
                        mToast.show();
                    }
                }
                else if (currentMode == WIFI_MODE) {
                    if (!wifi.isWifiEnabled()) {
                        mToast.setText("Wifi is turning on");
                        mToast.show();
                        wifi.setWifiEnabled(true);
                    } else {
                        mToast.setText("Wifi is already turned on");
                        mToast.show();
                    }
                }
                break;
            case 1:
                mToast.setText("HotSpot/Wifi is turned off");
                mToast.show();
                ipText.setText("");
                hotspot.setWifiApEnabled(null, false);
                wifi.setWifiEnabled(false);
                currentMode = NO_MODE;
                initiateButton.setChecked(true);
                break;
            case 2:
                mToast.setText("Instructions enabled");
                mToast.show();
                ReadWrite.writeToFile("NotChecked", getApplicationContext(), "Replace", "tutorial.txt");
                break;
            case 3:
                if(ipText.getVisibility()==View.VISIBLE){
                    ipText.setVisibility(View.INVISIBLE);
                }else {
                    ipText.setVisibility(View.VISIBLE);
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }



    public void connect(){
        this.connectThread = new Thread(new ConnectThread());
        this.connectThread.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(MainActivity.connectThread!=null) {
            MainActivity.connectThread.interrupt();
            MainActivity.connectThread=null;
            TCPSocket.setSocket(null);
        }
        if (MainActivity.serverThread != null){
            MainActivity.serverThread.interrupt();
            try {
                MainActivity.serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            MainActivity.serverThread = null;
        }
        if (MainActivity.readThread != null){
            MainActivity.readThread.interrupt();
            MainActivity.readThread = null;
        }
        finish();
    }

    @Override
    public void onBackPressed() {
        Intent backPressedIntent = new Intent(getApplicationContext(), OnBackPressedRequestActivity.class);
        startActivityForResult(backPressedIntent,BACK_PRESSED);
    }

    public static void failedConnectProcedure() {
        if ((currentMode == WIFI_MODE) && (SetIPActivity.setIPActivityState==false)){
            Intent intent = new Intent(context,SetIPActivity.class);
            context.startActivity(intent);
        }
        else if ((currentMode == HOTSPOT_MODE) && (SetIPActivity.setIPActivityState==false)){
            Intent intent = new Intent(context,DiscoverHotSpotActivity.class);
            context.startActivity(intent);
        }
    }


    /**
     *
     * @param MODE_TYPE
     */
    public void checkApWifi(int MODE_TYPE){
        currentMode = MODE_TYPE;
        if (currentMode == HOTSPOT_MODE){
            if (!hotspot.isHotspotEnabled()){
                Intent intent = new Intent(getApplicationContext(),RequestChangeWifiStateActivity.class);
                startActivityForResult(intent, CHECKWIFIHOTSPOT);
            }
        }
        else if (currentMode == WIFI_MODE){
            if (!wifi.isWifiEnabled()) {
                Intent intent = new Intent(getApplicationContext(), RequestChangeWifiStateActivity.class);
                startActivityForResult(intent, CHECKWIFIHOTSPOT);
            }
        }
        else{
            if (hotspot.isHotspotEnabled()){
                currentMode = HOTSPOT_MODE;
                ipText.setText("This device's IP Address: 192.168.43.1");
                hotspotButton.setChecked(true);
            }
            else if (wifi.isWifiEnabled()) {
                currentMode = WIFI_MODE;
                WifiInfo wifiInfo = wifi.getConnectionInfo();
                int ip = wifiInfo.getIpAddress();
                String IpAddress = Formatter.formatIpAddress(ip);
                ipText.setText("This device's IP Address: "+IpAddress);
                System.out.println(IpAddress);
                wifiButton.setChecked(true);
            }
            else {
                initiateButton.setChecked(true);
            }
        }
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CHECKWIFIHOTSPOT){
            if(resultCode == RESULT_OK){
                if (currentMode == HOTSPOT_MODE){
                    wifi.setWifiEnabled(false);
                    hotspot.setWifiApEnabled(null, true);
                }
                else {
                    hotspot.setWifiApEnabled(null, false);
                    wifi.setWifiEnabled(true);
                }
                Intent intent = new Intent( getApplicationContext(),Loading.class);
                startActivity(intent);
                timedOutFunction(12);
            }
            else if (resultCode == RESULT_CANCELED){
                checkApWifi(NO_MODE);
            }
        } else if(requestCode == SETIP){
            if(resultCode == RESULT_OK){
                WifiInfo wInfo = wifi.getConnectionInfo();
                int ip = wInfo.getIpAddress();
                String IpAddress = Formatter.formatIpAddress(ip);
                ipText.setText("Current IP Address: "+IpAddress);
                System.out.println(IpAddress);
            }
        } else if (requestCode == BACK_PRESSED){
            if(resultCode == RESULT_OK){
                finish();
            }
        }
    }




    public void timedOutFunction(final int sec){
        new CountDownTimer(1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {}

            @Override
            public void onFinish() {
                count++;
                if (count != sec) {
                    if (currentMode == HOTSPOT_MODE) {
                        if (hotspot.isHotspotEnabled()) {
                            //getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            Intent intent = new Intent("finish_activity");
                            sendBroadcast(intent);
                            intent = new Intent(getApplicationContext(), DiscoverHotSpotActivity.class);
                            startActivity(intent);
                            ipText.setText("This device's IP Address: 192.168.43.1");
                            count = 0;
                        } else {
                            timedOutFunction(sec);
                        }
                    } else {
                        if (wifi.isWifiEnabled()) {
                            //getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            Intent intent = new Intent("finish_activity");
                            sendBroadcast(intent);
                            intent = new Intent(getApplicationContext(), WifiActivity.class);
                            startActivityForResult(intent, SETIP);
                            count = 0;
                        } else {
                            timedOutFunction(sec);
                        }
                    }
                }
                else {
                    mToast.setText("Connection Timed Out");
                    mToast.show();
                    count = 0;
                    //getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    Intent intent = new Intent("finish_activity");
                    sendBroadcast(intent);
                    initiateButton.setChecked(true);
                }
            }
        }.start();
    }

}

