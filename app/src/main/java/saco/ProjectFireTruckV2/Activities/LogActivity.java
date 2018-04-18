package saco.ProjectFireTruckV2.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

import saco.ProjectFireTruckV2.R;
import saco.ProjectFireTruckV2.etc_utilities.ReadWrite;

public class LogActivity extends AppCompatActivity {

    private String log;
    private TextView text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);
        setTitle("Recent Activity");
        initialise();
    }

    public void initialise(){
        text = (TextView)findViewById(R.id.textView3);
        text.setTextSize(15);
        text.setMovementMethod(new ScrollingMovementMethod());
        this.log = ReadWrite.readFromFile(getApplicationContext(), "config.txt");
/*        SocketHandler.writeToFile(SocketHandler.getString(),getApplicationContext());
        this.log = SocketHandler.readFromFile(getApplicationContext());*/
        text.setText(log);
    }
}
