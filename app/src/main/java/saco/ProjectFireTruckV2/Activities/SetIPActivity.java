package saco.ProjectFireTruckV2.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import saco.ProjectFireTruckV2.ConfirmationPopUps.RequestDeleteIPActivity;
import saco.ProjectFireTruckV2.R;
import saco.ProjectFireTruckV2.StaticFiles.IPAddress;
import saco.ProjectFireTruckV2.StaticFiles.TCPSocket;
import saco.ProjectFireTruckV2.etc_utilities.IPAddressKeyListener;
import saco.ProjectFireTruckV2.etc_utilities.ReadWrite;

public class SetIPActivity extends Activity {

    //Variables for IP addresses
    private String IP;
    private TextView IPaddress;
    private ListView listView;
    private ArrayAdapter<String> listAdapter;
    private EditText text;
    private Button add;
    private Button clearAll;
    public static final int SINGLE_ITEM_DELETE = 100;
    public static final int CLEAR_LIST = 50;

    public static boolean setIPActivityState = false;
    /**
     * Create activity on start up
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ip_page);
        setTitle("IP address");
        initialise();
        clicksListener();
    }

    @Override
    protected void onStart() {
        super.onStart();
        setIPActivityState = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        setIPActivityState = false;
    }

    /**
     * Function to initialise variables and get IP addresses from server
     */
    public void initialise(){
        text = (EditText)findViewById(R.id.editText2);
        add = (Button)findViewById(R.id.button2);
        clearAll = (Button)findViewById(R.id.button3);
        listView = (ListView)findViewById(R.id.listView2);
        listAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1);
        listView.setAdapter(listAdapter);
        IP = IPAddress.getIP();
        IPaddress = (TextView)findViewById(R.id.IP_address);
        IPaddress.setText(IP);
        IPaddress.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        readFile(getApplicationContext(), "ipList.txt");
        if (filterIP(IP) && !IP.equals("")) {
            listAdapter.add(IP);
            ReadWrite.writeToFile(IP + "\n", getApplicationContext(), "Append", "ipList.txt");
        }
    }

    /**
     * Function to listen for any button clicks
     */
    public void clicksListener(){
        text.setKeyListener(IPAddressKeyListener.getInstance());
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newIP = text.getText().toString();
                IPAddress.setIP(newIP);
                if (newIP.isEmpty()){
                    IPAddress.setIP(IP);
                    Toast.makeText(getApplicationContext(), "Please enter a valid IP address", Toast.LENGTH_LONG).show();
                }
                else {
                    if (TCPSocket.getSocket() == null || !newIP.matches(IP)) {
                        MainActivity.connectButton.performClick();
                        Toast.makeText(getApplicationContext(), "Setting IP adress ...", Toast.LENGTH_SHORT).show();
                        SystemClock.sleep(1000);
                        if (TCPSocket.getSocket() != null) {
                            if (filterIP(newIP)) {
                                ReadWrite.writeToFile(newIP + "\n", getApplicationContext(), "Append", "ipList.txt");
                                listAdapter.add(newIP);
                                waitAndFinish(1);
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "IP address not reachable", Toast.LENGTH_LONG).show();
                            IPAddress.setIP(IP);
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Already connected", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(!listAdapter.getItem(position).matches(IPAddress.getIP()) || TCPSocket.getSocket()==null) {
                    IPAddress.setTempIP(IP);
                    IP = listAdapter.getItem(position);
                    IPAddress.setIP(IP);
                    MainActivity.connectButton.performClick();
                    SystemClock.sleep(2100);
                    if (TCPSocket.getSocket() == null){
                        IP = IPAddress.getTempIP();
                        IPAddress.setIP(IP);
                    }
                    IPaddress.setText(IP);
                    //waitAndFinish(1);
                }else{
                    Toast.makeText(getApplicationContext(),"Already Connected",Toast.LENGTH_LONG).show();
                }
                finish();
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), RequestDeleteIPActivity.class);
                intent.putExtra("item", listAdapter.getItem(position));
                intent.putExtra("RequestCode", SINGLE_ITEM_DELETE);
                startActivityForResult(intent, SINGLE_ITEM_DELETE);
                return true;
            }
        });

        clearAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RequestDeleteIPActivity.class);
                intent.putExtra("RequestCode", CLEAR_LIST);
                startActivityForResult(intent, CLEAR_LIST);
            }
        });
    }

    public boolean filterIP(String IPadd){
        for(int i=0;i<listAdapter.getCount();i++){
            if(IPadd.equals(listAdapter.getItem(i))) {
                return false;
            }
        }
        return true;
    }

    public void readFile(Context context, String file) {
        try {
            InputStream inputStream = context.openFileInput(file);

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString + "\n");
                    if(receiveString!=null) {
                        listAdapter.add(receiveString);
                        Log.d("Adapter","Adding "+receiveString);
                    }
                    Log.d("READ", receiveString);
                }


                inputStream.close();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        debug(String.valueOf(requestCode));
        if(requestCode == SINGLE_ITEM_DELETE) {
            if (resultCode == RESULT_OK) {
                debug("request code");
                String retrievedItem = data.getExtras().getString("items");
                debug(retrievedItem + " retrieved");
                ReadWrite.writeToFile("", getApplicationContext(), "Replace", "ipList.txt");
                for (int i = 0; i < listAdapter.getCount(); i++) {
                    if (listAdapter.getItem(i).matches(retrievedItem)) {
                        listAdapter.remove(retrievedItem);
                    } else {
                        debug("writing");
                        ReadWrite.writeToFile(listAdapter.getItem(i), getApplicationContext(), "Append", "ipList.txt");
                    }
                }
            } else {
                //do nothing
            }
        }
        else if (requestCode == CLEAR_LIST) {
            if (resultCode == RESULT_OK) {
                listAdapter.clear();
                ReadWrite.writeToFile("", getApplicationContext(), "Replace", "ipList.txt");
            } else {
                //do nothing
            }
        }

    }

    public void waitAndFinish(int duration){
        new CountDownTimer(duration*1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
            }
            @Override
            public void onFinish() {
                finish();
            }
        }.start();
    }
    public void debug(String message){
        Log.d("INFO",message);
    }
}





