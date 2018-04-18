package saco.ProjectFireTruckV2.ConfirmationPopUps;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import saco.ProjectFireTruckV2.R;

public class OnBackPressedRequestActivity extends Activity {
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

        text.setText("Are you sure you want to exit this app?");
        setTitle("Quit");
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
