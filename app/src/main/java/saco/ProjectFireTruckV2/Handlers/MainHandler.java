package saco.ProjectFireTruckV2.Handlers;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;

import saco.ProjectFireTruckV2.Activities.ChatActivity;
import saco.ProjectFireTruckV2.Activities.MainActivity;
import saco.ProjectFireTruckV2.Activities.PhotoActivity;
import saco.ProjectFireTruckV2.R;
import saco.ProjectFireTruckV2.StaticFiles.TCPSocket;

import static saco.ProjectFireTruckV2.Activities.MainActivity.chatButton;
import static saco.ProjectFireTruckV2.Activities.MainActivity.connectButton;
import static saco.ProjectFireTruckV2.Activities.MainActivity.connectThread;
import static saco.ProjectFireTruckV2.Activities.MainActivity.failedConnectProcedure;
import static saco.ProjectFireTruckV2.Activities.MainActivity.mToast;
import static saco.ProjectFireTruckV2.Activities.MainActivity.readThread;
import static saco.ProjectFireTruckV2.Activities.MainActivity.sendImageButton;
import static saco.ProjectFireTruckV2.Activities.MainActivity.toastImage;

/**
 * Created by PMGC37 on 1/28/2016.
 */
public class MainHandler {

    //Constants to indicate if connection is established
    public static final int CONNECTED = 1;
    public static final int CONNECTING = 2;
    public static final int FAILED_CONNECT = 3;
    public static final int DISCONNECTED = 4;
    public static final int SEND_SUCCESSFUL = 5;
    public static final int HEADER_ACKNOWLEDGED = 6;
    public static final int RECEIVED_IMAGE = 7;

    //Handler to handle object or message parsed from other thread in order to make changes in the main UI thread
    public static Handler mainHandler = new Handler(){


        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case CONNECTED:
                    connectButton.setImageResource(R.drawable.wifi_connected);
                    connectButton.setEnabled(true);
                    mToast.setText("Connected"); mToast.show();

                    chatButton.setEnabled(true);
                    sendImageButton.setEnabled(true);
                    break;
                case CONNECTING:
                    connectButton.setImageResource(R.drawable.wifi_connecting);
                    mToast.setText("Connecting"); mToast.show();
                    break;
                case FAILED_CONNECT:
                    connectButton.setImageResource(R.drawable.wifi_notconnected);
                    mToast.setText("Failed to connect\n" + "Ensure server is ready to accept socket\n" + "Or select new device"); mToast.show();
                    TCPSocket.setSocket(null);
                    connectButton.setEnabled(true);
                    if (connectThread!= null) {
                        connectThread.interrupt();
                        connectThread = null;
                    }
                    failedConnectProcedure();
                    break;
                case DISCONNECTED:
                    connectButton.setImageResource(R.drawable.wifi_notconnected);
                    connectButton.setEnabled(true);
                    mToast.setText("Disconnected"); mToast.show();
                    TCPSocket.setSocket(null);

                    if (connectThread!= null) {
                        connectThread.interrupt();
                        connectThread = null;
                    }

                    if (readThread != null){
                        readThread.interrupt();
                        readThread = null;
                    }

                    chatButton.setEnabled(false);
                    sendImageButton.setEnabled(false);
                    if (PhotoActivity.photoActivityState == true){
                        PhotoActivity.photoActivity.finish();
                    }
                    if (ChatActivity.chatPageActive == true){
                        ChatActivity.chatActivity.finish();
                    }
                    break;

                case SEND_SUCCESSFUL:
                    mToast.setText("Sent successfully" + "\n" + "in " + String.format("%.3f",(double)msg.obj) + " seconds"); mToast.show();
                    if (PhotoActivity.photoActivityState == true) {
                        PhotoActivity.changeSendButtonState("enabledTrue");
                    }
                    break;
                case RECEIVED_IMAGE:
                    MainActivity.toastForText.cancel();
                    toastImage.setImageBitmap((Bitmap) msg.obj);
                    MainActivity.toastForImage.show();
                    break;
                case HEADER_ACKNOWLEDGED:
                    break;
                default:
                    mToast.setText("Acknowledged"); mToast.show();
                    break;
            }
        }
    };
}
