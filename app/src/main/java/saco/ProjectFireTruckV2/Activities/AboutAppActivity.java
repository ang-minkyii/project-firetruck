package saco.ProjectFireTruckV2.Activities;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import saco.ProjectFireTruckV2.R;

/**
 * Created by PMGC37 on 1/28/2016.
 */
public class AboutAppActivity extends Activity {
    //Variables to be declared
    private TextView text;
    private Button okay;

    /**
     * Create activity on start up
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        setTitle(Html.fromHtml("<small>About</small>"));
        initialise();
        clicksListener();
    }

    /**
     * Function to initialise all variables
     */
    public void initialise(){
        text = (TextView)findViewById(R.id.textView2);
        okay = (Button)findViewById(R.id.button);
        text.setText("App Version: 1.0\n" + "Made by Shan & Andy");
    }

    /**
     * Function to listen for button clicks
     */
    public void clicksListener(){
        okay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
