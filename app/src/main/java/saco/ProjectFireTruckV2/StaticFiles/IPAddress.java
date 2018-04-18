package saco.ProjectFireTruckV2.StaticFiles;

/**
 * Created by pmgc37 on 1/27/2016.
 */
public class IPAddress {
    public static String SERVERIP = ""; //your computer IP address
    public static int SERVERPORT = 4001;
    public static String TEMPIP = null; //your temp computer IP address

    public static synchronized void setIP(String IP){
        SERVERIP = IP;
        return;
    }

    public static synchronized String getIP(){
        return SERVERIP;
    }

    public static synchronized int getServerport() {return SERVERPORT; }

    public static synchronized void setServerport(int ServerPort){
        SERVERPORT = ServerPort;
        return;
    }

    public static synchronized void setTempIP(String IP) {
        TEMPIP = IP;
        return;
    }

    public static synchronized String getTempIP(){
        return TEMPIP;
    }
}
