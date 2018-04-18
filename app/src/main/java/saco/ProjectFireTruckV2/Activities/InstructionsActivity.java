package saco.ProjectFireTruckV2.Activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.Window;
import android.widget.CheckBox;

import saco.ProjectFireTruckV2.R;
import saco.ProjectFireTruckV2.etc_utilities.ReadWrite;

public class InstructionsActivity extends Activity {

    CheckBox noshowCheckbox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getActionBar().hide();
        setContentView(R.layout.activity_instructions);

        noshowCheckbox = (CheckBox)findViewById(R.id.checkBox);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (noshowCheckbox.isChecked()){
            ReadWrite.writeToFile("Checked", getApplicationContext(), "Replace", "tutorial.txt");
        }
        finish();
        return super.onTouchEvent(event);
    }
}
