package saco.ProjectFireTruckV2.Activities;

import android.app.Activity;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import saco.ProjectFireTruckV2.R;

public class DebugModeActivity extends Activity {

    RadioButton radioDebugOn;
    RadioButton radioDebugOff;
    RadioGroup radioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug_mode);

        radioDebugOn = (RadioButton)findViewById(R.id.debugOn);
        radioDebugOff = (RadioButton)findViewById(R.id.debugOff);
        radioGroup = (RadioGroup)findViewById(R.id.debugRadioGroup);

        initialise();
        onClicksListener();
    }


    private void initialise() {
        radioGroup.clearCheck();
        if (MainActivity.DEBUG_MODE == MainActivity.DEBUG_AS_ERROR){
            radioDebugOn.setChecked(true);
        }
        else {
            radioDebugOff.setChecked(true);
        }
    }

    private void onClicksListener() {
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == radioDebugOn.getId()){
                    MainActivity.DEBUG_MODE = MainActivity.DEBUG_AS_ERROR;
                    MainActivity.mToast.setText("Debug mode enabled");
                    MainActivity.mToast.show();
                    finish();
                }
                else if (checkedId == radioDebugOff.getId()){
                    MainActivity.DEBUG_MODE = MainActivity.DEBUG_OFF;
                    MainActivity.mToast.setText("Debug mode disabled");
                    MainActivity.mToast.show();
                    finish();
                }
            }
        });
    }
}
