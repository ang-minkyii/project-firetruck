package saco.ProjectFireTruckV2.TCP;

import android.os.SystemClock;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import saco.ProjectFireTruckV2.Activities.MainActivity;
import saco.ProjectFireTruckV2.Handlers.MainHandler;
import saco.ProjectFireTruckV2.StaticFiles.MotoProtocol;
import saco.ProjectFireTruckV2.StaticFiles.TCPSocket;
import saco.ProjectFireTruckV2.StaticFiles.TransactionID;
import saco.ProjectFireTruckV2.etc_utilities.Tools;

/**
 * Created by PMGC37 on 1/28/2016.
 */
public class SendMessage {

    public static String ACK = "";
    private final static String RESET = "";
    private static final String REQUEST = "Request";
    private static final String REPLY = "Reply";
    private static final String ACKNOWLEDGED = "Acknowledged";
    private static final String RECEIVED = "Received";
    private static int timeOutCounter = 0;

    static long tStart;
    static long tStartContent;
    static long tEnd;
    static long tDelta;
    static double initialFileHeaderElapsedTimeInSeconds;
    static double contentReceivedElapsedTimeInSeconds;
    static double totalElapsedTimeInSeconds;


    public static boolean SendAsRequest(byte[] Content, String fileName){
        tStart = SystemClock.uptimeMillis();

        byte[] protocolPacket = null;
        Tools.debug("SendMessage.java", "Filename being sent is: " + fileName);

        protocolPacket = MotoProtocol.fileHeaderDynamicPacket(fileName, Content.length, 0, REQUEST);
        Tools.debug("SendMessage.java", "Sending FileHeaderRequest packet");
        Tools.printBytes(protocolPacket);
        sendMessage(protocolPacket);
        while (ACK != ACKNOWLEDGED){
            SystemClock.sleep(100);
            timeOutCounter++;
            if (timeOutCounter > 10){
                if (MainActivity.connectThread != null) {
                    MainActivity.connectThread.interrupt();
                    MainActivity.connectThread = null;
                }
                timeOutCounter = 0;
                ACK = RESET;
                return false;
            }
        }
        timeOutCounter = 0;

        if (ACK == ACKNOWLEDGED) {

            tStartContent = SystemClock.uptimeMillis();

            protocolPacket = MotoProtocol.contentTransfer(Content, TransactionID.getTransactionID(),REQUEST);
            Tools.debug("SendMessage.java", "Sending ContentRequest packet with Transaction ID: " + String.valueOf(TransactionID.getTransactionID()));
            Tools.printBytes(protocolPacket);
            sendMessage(protocolPacket);
            while (ACK != RECEIVED){
                SystemClock.sleep(100);
                timeOutCounter++;
                if (timeOutCounter > 150){
                    Tools.debug("SendMessage.java", "Sending time out reached, closing the socket");
                    if (MainActivity.connectThread != null) {
                        MainActivity.connectThread.interrupt();
                        MainActivity.connectThread = null;
                    }
                    timeOutCounter = 0;
                    ACK = RESET;
                    return false;
                }
            }
            timeOutCounter = 0;
            ACK = RESET;
        }
        ACK = RESET;
        return true;
    }

    public static boolean SendAsReply(byte[] Content, String fileName){


        byte[] protocolPacket = null;
        Tools.debug("SendMessage.java", "Filename being sent is: " + fileName);

        protocolPacket = MotoProtocol.fileHeaderDynamicPacket(fileName, Content.length, 0, REQUEST);
        Tools.debug("SendMessage.java", "Sending FileHeaderRequest packet");
        Tools.printBytes(protocolPacket);
        sendMessage(protocolPacket);
        while (ACK != ACKNOWLEDGED){
            SystemClock.sleep(100);
            timeOutCounter++;
            if (timeOutCounter > 10){
                if (MainActivity.connectThread != null) {
                    MainActivity.connectThread.interrupt();
                    MainActivity.connectThread = null;
                }
                timeOutCounter = 0;
                ACK = RESET;
                return false;
            }
        }
        timeOutCounter = 0;

        if (ACK == ACKNOWLEDGED) {

            protocolPacket = MotoProtocol.contentTransfer(Content, TransactionID.getTransactionID(),REQUEST);
            Tools.debug("SendMessage.java", "Sending ContentRequest packet with Transaction ID: " + String.valueOf(TransactionID.getTransactionID()));
            Tools.printBytes(protocolPacket);
            sendMessage(protocolPacket);
            while (ACK != RECEIVED){
                SystemClock.sleep(100);
                timeOutCounter++;
                if (timeOutCounter > 150){
                    Tools.debug("SendMessage.java", "Sending time out reached, closing the socket");
                    if (MainActivity.connectThread != null) {
                        MainActivity.connectThread.interrupt();
                        MainActivity.connectThread = null;
                    }
                    timeOutCounter = 0;
                    ACK = RESET;
                    return false;
                }
            }
            timeOutCounter = 0;
            ACK = RESET;
        }
        ACK = RESET;
        return true;
    }




    /**
     * Sends the message entered by client to the server
     * @param message text entered by client
     */
    private static boolean sendMessage(byte[] message){
        OutputStream out = null;
        try {
            out = new BufferedOutputStream(TCPSocket.getSocket().getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            out.write(message);
            return true;
        }catch (IOException e){
            e.printStackTrace();
            Log.e("TCP", "FAILED to send message");
            MainActivity.mainHandler.obtainMessage(MainHandler.FAILED_CONNECT).sendToTarget();
            return false;
        } finally {
            try {
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
