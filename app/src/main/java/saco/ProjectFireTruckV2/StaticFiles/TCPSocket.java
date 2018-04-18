package saco.ProjectFireTruckV2.StaticFiles;

import java.net.Socket;

/**
 * Created by pmgc37 on 1/27/2016.
 */
public class TCPSocket {
    //Static socket to be used throughout
    private static Socket mSocket = null;
    //Getter for socket
    public static synchronized Socket getSocket(){
        return mSocket;
    }
    //Setter for socket
    public static synchronized void setSocket(Socket socket){
        TCPSocket.mSocket = socket;
    }

}
