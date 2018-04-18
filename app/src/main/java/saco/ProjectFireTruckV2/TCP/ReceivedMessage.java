package saco.ProjectFireTruckV2.TCP;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.SystemClock;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.nio.ByteBuffer;

import saco.ProjectFireTruckV2.Activities.MainActivity;
import saco.ProjectFireTruckV2.Handlers.MainHandler;
import saco.ProjectFireTruckV2.StaticFiles.MotoProtocol;
import saco.ProjectFireTruckV2.StaticFiles.ReadOrImage;
import saco.ProjectFireTruckV2.StaticFiles.TCPSocket;
import saco.ProjectFireTruckV2.StaticFiles.TransactionID;
import saco.ProjectFireTruckV2.etc_utilities.Tools;

/**
 * Created by pmgc37 on 1/27/2016.
 */
public class ReceivedMessage {

    public static void Interpret(byte[] message){
        byte[] OPcodeBytes = {0, (byte) (message[0] & 0xFF), (byte) (message[1] & 0xFF)};
        int OPcode = new BigInteger(OPcodeBytes).intValue();

        if (OPcode == 0x8001) {
            byte[] TransactionIDBytes = {0, (byte) (message[5] & 0xFF), (byte) (message[6] & 0xFF)};
            TransactionID.setTransactionID(new BigInteger(TransactionIDBytes).intValue());
            byte[] pathnamelengthBytes = {0, (byte) (message[19] & 0xFF), (byte) (message[20] & 0xFF)};
            int pathnameLength = new BigInteger(pathnamelengthBytes).intValue();
            int fileHeaderReplyPacketLength = pathnameLength + 21;
            Tools.debug("ReceivedMessage.java", "Request acknowledged, Transaction ID obtained: " + String.valueOf(TransactionID.getTransactionID()));
            Tools.printBytes(message, fileHeaderReplyPacketLength);
            SendMessage.ACK = "Acknowledged";
            MainActivity.mainHandler.obtainMessage(MainHandler.HEADER_ACKNOWLEDGED).sendToTarget();
            ReadOrImage.setReadState(true);
        } else if (OPcode == 0x8002) {
            int contentTransferReplyPacketLength = 9;
            byte[] TransactionIDBytes = {0, (byte) (message[3] & 0xFF), (byte) (message[4] & 0xFF)};
            int TransactionIDConfirmation = new BigInteger(TransactionIDBytes).intValue();
            byte[] ResultBytes = {0, (byte) (message[2] & 0xFF)};
            int Result = new BigInteger(ResultBytes).intValue();
            Tools.debug("ReceivedMessage.java", "Received acknowledgement for file with Transaction ID: " + String.valueOf(TransactionIDConfirmation));
            Tools.debug("ReceivedMessage.java", "Result code received is: " + String.valueOf(Result));
            Tools.printBytes(message,contentTransferReplyPacketLength);

            if ((TransactionID.getTransactionID() == TransactionIDConfirmation) && (Result == 0)) {
                Tools.debug("ReceivedMessage.java", "File was sent successfully");
                SendMessage.ACK = "Received";

                SendMessage.tEnd = SystemClock.uptimeMillis();
                SendMessage.tDelta = SendMessage.tStartContent - SendMessage.tStart;
                SendMessage.initialFileHeaderElapsedTimeInSeconds = SendMessage.tDelta / 1000.0;
                SendMessage.tDelta = SendMessage.tEnd - SendMessage.tStartContent;
                SendMessage.contentReceivedElapsedTimeInSeconds = SendMessage.tDelta / 1000.0;
                SendMessage.totalElapsedTimeInSeconds = SendMessage.contentReceivedElapsedTimeInSeconds + SendMessage.initialFileHeaderElapsedTimeInSeconds;

                Tools.debug("ReceivedMessage.java", "TIMING: Initial Header timing: " + String.valueOf(SendMessage.initialFileHeaderElapsedTimeInSeconds));
                Tools.debug("ReceivedMessage.java", "TIMING: Content transfer timing: " + String.valueOf(SendMessage.contentReceivedElapsedTimeInSeconds));
                Tools.debug("ReceivedMessage.java", "TIMING: Total elapsed time: " + String.valueOf(SendMessage.totalElapsedTimeInSeconds));
                System.out.print(".\n.\n.\n");
                MainActivity.mainHandler.obtainMessage(MainHandler.SEND_SUCCESSFUL,SendMessage.totalElapsedTimeInSeconds).sendToTarget();
            }
            ReadOrImage.setReadState(true);
        } else if (OPcode == 0x0001){
            byte[] arr = {message[14], message[15], message[16], message[17]};
            ByteBuffer wrapped = ByteBuffer.wrap(arr); // big-endian by default
            ReadThread.size = wrapped.getInt() + 10;
            Tools.debug("ReceivedMessage.java", "File length is: " + String.valueOf(ReadThread.size));

            int transactionID = TransactionID.getTransactionID();
            TransactionID.setTransactionID(transactionID);
            OutputStream OutStream;
            try {
                OutStream = TCPSocket.getSocket().getOutputStream();
                OutStream.write(MotoProtocol.fileHeaderDynamicPacket("Filename", ReadThread.size, transactionID, "Reply"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            ReadOrImage.setReadState(false);
            ReadOrImage.setImageIncoming(true);
        } else if (OPcode == 0x0002){
            Tools.debug("ReceivedMessage.java", "Received an image file, file size is: " + String.valueOf(message.length-10));
            Bitmap image= BitmapFactory.decodeByteArray(message, 10, message.length - 10);
            if(image != null){
                MainActivity.mainHandler.obtainMessage(MainHandler.RECEIVED_IMAGE,image).sendToTarget();
            } else {
                Tools.debug("ReceivedMessage.java", "no bitmap");
            }
            int transactionID = TransactionID.getTransactionID();
            OutputStream OutStream;
            try {
                OutStream = TCPSocket.getSocket().getOutputStream();
                OutStream.write(MotoProtocol.contentTransfer(message, transactionID, "Reply"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            ReadOrImage.setReadState(true);
            ReadOrImage.setImageIncoming(false);
        }
        else if (OPcode == 0x0211){
            if (MainActivity.connectThread != null){
                Tools.debug("ReceivedMessage", "OPcode of 0x0211 received, interrupting ConnectThread");
                MainActivity.connectThread.interrupt();
            }
            ReadOrImage.setReadState(true);
        }
        return;
    }
}
