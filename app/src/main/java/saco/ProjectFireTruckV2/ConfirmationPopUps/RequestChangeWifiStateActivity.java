package saco.ProjectFireTruckV2.ConfirmationPopUps;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import saco.ProjectFireTruckV2.R;

import static saco.ProjectFireTruckV2.Activities.MainActivity.HOTSPOT_MODE;
import static saco.ProjectFireTruckV2.Activities.MainActivity.WIFI_MODE;
import static saco.ProjectFireTruckV2.Activities.MainActivity.currentMode;

public class RequestChangeWifiStateActivity extends Activity {
    private Button yes;
    private Button no;
    private TextView text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popup);
        initialise();
        clicksListener();
    }

    public void initialise(){
        yes = (Button)findViewById(R.id.button6);
        no = (Button)findViewById(R.id.button7);
        text = (TextView)findViewById(R.id.textView14);

        if (currentMode == HOTSPOT_MODE) {
            text.setText("To start connection, HotSpot has to be enabled. Turn on Hotspot?");
            setTitle("Turn on HotSpot");
        }
        else if (currentMode == WIFI_MODE) {
            text.setText("To start connection, WiFi has to be enabled. Turn on WiFi?");
            setTitle("Turn on WiFi");
        }
    }

    public void clicksListener(){
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_OK);
                finish();
            }
        });

        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
    }
}
