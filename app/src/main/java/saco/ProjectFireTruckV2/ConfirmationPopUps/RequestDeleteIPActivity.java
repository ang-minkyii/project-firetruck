package saco.ProjectFireTruckV2.ConfirmationPopUps;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import saco.ProjectFireTruckV2.Activities.SetIPActivity;
import saco.ProjectFireTruckV2.R;

/**
 * Created by PMGC37 on 1/29/2016.
 */
public class RequestDeleteIPActivity extends Activity {

    private TextView text;
    private Button yes;
    private Button no;
    private String retrievedItem;
    private int RequestCode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popup);
        initialise();
        clicksListener();
    }

    public void initialise(){
        setTitle("Delete IP Address");
        yes = (Button)findViewById(R.id.button6);
        no = (Button)findViewById(R.id.button7);
        text = (TextView)findViewById(R.id.textView14);
        Intent intent = getIntent();
        retrievedItem = intent.getExtras().getString("item");
        RequestCode = intent.getExtras().getInt("RequestCode");
        if (RequestCode == SetIPActivity.SINGLE_ITEM_DELETE) {
            text.setText("Are you sure you want to delete device with IP address:\n" + retrievedItem + "\n");
        }
        else if (RequestCode == SetIPActivity.CLEAR_LIST){
            text.setText("Are you sure you want to clear the list?");
        }
    }

    public void clicksListener(){
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                if (RequestCode == SetIPActivity.SINGLE_ITEM_DELETE) {
                    intent.putExtra("items", retrievedItem);
                    setResult(SetIPActivity.RESULT_OK, intent);
                }
                else if (RequestCode == SetIPActivity.CLEAR_LIST){
                    setResult(SetIPActivity.RESULT_OK, intent);
                }
                finish();
            }
        });
    }
}
