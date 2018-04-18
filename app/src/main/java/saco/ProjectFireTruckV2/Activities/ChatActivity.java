package saco.ProjectFireTruckV2.Activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;

import saco.ProjectFireTruckV2.R;
import saco.ProjectFireTruckV2.StaticFiles.TCPSocket;
import saco.ProjectFireTruckV2.TCP.SendMessage;
import saco.ProjectFireTruckV2.etc_utilities.ReadWrite;
import saco.ProjectFireTruckV2.list_adapters.ChatActivity_listAdapter;

public class ChatActivity extends Activity
{
    //Variables to be declared (self-explanatory)
    private ListView mList;
    private ArrayList<String> arrayList;
    private ChatActivity_listAdapter mAdapter;
    private Socket socket;
    private OutputStream out;
    private Button send;
    private EditText editText;

    public static Activity chatActivity;
    public static boolean chatPageActive = false;

    /**
     * Create activity on start up
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_page);
        setTitle("Chat");
        initialise();
        clicksListener();

        chatActivity = this;
    }

    @Override
    protected void onStart() {
        super.onStart();
        chatPageActive = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        chatPageActive = false;
    }

    /**
     * Function to initialise all variables
     */
    public void initialise(){
        arrayList = new ArrayList<>();
        editText = (EditText) findViewById(R.id.editText);
        send = (Button)findViewById(R.id.send_button);
        mList = (ListView)findViewById(R.id.list);
        mAdapter = new ChatActivity_listAdapter(this, arrayList);
        mList.setAdapter(mAdapter);

        if (TCPSocket.getSocket() == null){
            editText.setHint("Not Connected");
            editText.setEnabled(false);
            send.setEnabled(false);
        }
    }

    /**
     * Function to listen for all button clicks
     */
    public void clicksListener(){
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = editText.getText().toString();
                arrayList.add("c: " + message);

                //sends the message to the server
                if (TCPSocket.getSocket() != null) {
                    socket = TCPSocket.getSocket();
                    try {
                        out = socket.getOutputStream();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    ReadWrite.storeData(getApplicationContext(), "//" + message, "Message", "config.txt");
                    SendMessage.SendAsRequest(message.getBytes(),"");
                }
                //refresh the list
                mAdapter.notifyDataSetChanged();
                editText.setText("");
            }
        });
    }
}

