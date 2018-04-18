package saco.ProjectFireTruckV2.TCP;

import android.os.SystemClock;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import saco.ProjectFireTruckV2.Activities.MainActivity;
import saco.ProjectFireTruckV2.Handlers.MainHandler;
import saco.ProjectFireTruckV2.StaticFiles.IPAddress;
import saco.ProjectFireTruckV2.StaticFiles.TCPSocket;

/**
 * Created by pmgc37 on 1/27/2016.
 */
public class    ConnectThread implements Runnable {
    private boolean ThreadActive = false;
    private OutputStream out;
    private InputStream in;

    private Thread readThread = null;


    /**
     *  Constructor of the class. OnMessagedReceived listens for the messages received from server
     */
    public ConnectThread(){
    }

    /**
     * Function to stop the client
     */
    public void cancel(){
        Log.d("ConnectThread", "is interrupted, closing Thread");
        ThreadActive = false;
        Thread.currentThread().interrupt();
        MainActivity.connectThread=null;
        Socket socket = TCPSocket.getSocket();
        if ((socket != null)){
            try {
                if (out != null) {
                    out.close();
                    out = null;
                }
                if (in != null) {
                    in.close();
                    in = null;
                }
                socket.close();
                socket = null;
                TCPSocket.setSocket(socket);
                MainActivity.mainHandler.obtainMessage(MainHandler.DISCONNECTED).sendToTarget();
                Log.e("ConnectThread", "Socket closed");
                System.out.print("\n.\n.\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void run() {
        ThreadActive = true;
        try {
            Socket socket = TCPSocket.getSocket();
            if (socket == null) {
                Log.d("ConnectThread", "C: Connecting...");
                MainActivity.mainHandler.obtainMessage(MainHandler.CONNECTING).sendToTarget();
                socket = new Socket();
                socket.connect(new InetSocketAddress(IPAddress.getIP(), IPAddress.getServerport()), 2000);
                TCPSocket.setSocket(socket);
            }
            else {
            }
            out = socket.getOutputStream();

            MainActivity.readThread = new Thread(new ReadThread(new ReadThread.OnReadMessageReceived() {
                @Override
                public void messageReceived(byte[] message) {
                    ReceivedMessage.Interpret(message);
                }
            }));
            MainActivity.readThread.start();

            MainActivity.mainHandler.obtainMessage(MainHandler.CONNECTED).sendToTarget();
            Log.d("ConnectThread", "Connected");

;            while ((!Thread.currentThread().isInterrupted()) && (ThreadActive = true)){
                SystemClock.sleep(100);
            }
        } catch (Exception e) {
            Log.e("ConnectThread", "C: Error", e);
            MainActivity.mainHandler.obtainMessage(MainHandler.FAILED_CONNECT).sendToTarget();
        } finally {
            cancel();
        }
    }



}
