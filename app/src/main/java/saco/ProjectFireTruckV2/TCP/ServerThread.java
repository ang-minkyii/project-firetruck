package saco.ProjectFireTruckV2.TCP;

import android.util.Log;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

import saco.ProjectFireTruckV2.Activities.MainActivity;
import saco.ProjectFireTruckV2.StaticFiles.IPAddress;
import saco.ProjectFireTruckV2.StaticFiles.TCPSocket;

/**
 * Created by pmgc37 on 1/27/2016.
 */
public class ServerThread implements Runnable {

    private Socket socket;
    private boolean ThreadActive = true;

    public void run() {
        if (TCPSocket.getSocket() == null) {
            try {
                MainActivity.serverSocket = new ServerSocket();
                MainActivity.serverSocket.setReuseAddress(true);
                MainActivity.serverSocket.bind(new InetSocketAddress(IPAddress.getServerport()));
                while (!Thread.currentThread().isInterrupted() && ThreadActive==true) {

                    try {

                        socket = MainActivity.serverSocket.accept();
                        TCPSocket.setSocket(socket);
                        MainActivity.connectThread = new Thread(new ConnectThread());
                        MainActivity.connectThread.start();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                Log.d("ServerThread","Thread is interrupted, cancelling...");
                cancel();
            }

        }
    }


    public void cancel(){
        Thread.currentThread().interrupt();
        ThreadActive = false;
        if (MainActivity.serverSocket != null) {
            try {
                MainActivity.serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



}