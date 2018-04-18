package saco.ProjectFireTruckV2.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import saco.ProjectFireTruckV2.R;

public class PasswordActivity extends Activity {

    private TextView text;
    private Button connect;
    private EditText editText;
    private String networkSSID = null;
    private String networkPass = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);
        setTitle("Enter Password: ");
        initialise();
        clicksListener();
    }

    public void initialise(){
        text = (TextView)findViewById(R.id.textView15);
        connect = (Button)findViewById(R.id.button8);
        editText = (EditText)findViewById(R.id.editText3);
        editText.setTransformationMethod(PasswordTransformationMethod.getInstance());
        Intent intent = getIntent();
        networkSSID = intent.getExtras().getString("ssid");
        text.setText("Network SSID: " + networkSSID);
    }

    public void clicksListener(){
        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                networkPass = String.valueOf(editText.getText());
                Intent intent = new Intent();
                intent.putExtra("password",networkPass);
                setResult(RESULT_OK,intent);
                finish();
            }
        });
    }
}
