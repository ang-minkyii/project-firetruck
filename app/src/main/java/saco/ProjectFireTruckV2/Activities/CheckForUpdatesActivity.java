package saco.ProjectFireTruckV2.Activities;

import android.app.Activity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Html;
import android.widget.ProgressBar;
import android.widget.TextView;

import saco.ProjectFireTruckV2.R;

/**
 * Created by PMGC37 on 1/28/2016.
 */
public class CheckForUpdatesActivity extends Activity {
    //Variables to be declared
    private TextView text;
    private ProgressBar pBar;
    /**
     * Create activity on start up
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        setTitle(Html.fromHtml("<small>Updates</small>"));
        initialise();
        progress();
    }

    /**
     * Function to initialise all variables
     */
    public void initialise(){
        text = (TextView)findViewById(R.id.textView);
        text.setText("Checking for updates");
        text.setTextSize(20);
        pBar = (ProgressBar)findViewById(R.id.progressBar);
    }

    public void progress(){
        new CountDownTimer(2500, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                text.setTextSize(15);
                text.setText("Latest version is already installed");
                pBar.setEnabled(false);
                new CountDownTimer(1000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                    }

                    @Override
                    public void onFinish() {
                        finish();
                    }
                }.start();
            }
        }.start();
    }
}