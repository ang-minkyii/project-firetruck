package saco.ProjectFireTruckV2.TCP;

import android.os.SystemClock;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;

import saco.ProjectFireTruckV2.Activities.MainActivity;
import saco.ProjectFireTruckV2.StaticFiles.MotoProtocol;
import saco.ProjectFireTruckV2.StaticFiles.ReadOrImage;
import saco.ProjectFireTruckV2.StaticFiles.TCPSocket;
import saco.ProjectFireTruckV2.etc_utilities.Tools;

public class ReadThread implements Runnable{
    private OnReadMessageReceived readMessageListener = null;
    InputStream in;
    boolean ThreadActive = true;
    boolean readState;
    boolean imageIncomingState;
    boolean messageProcessed;
    byte[] buffer;
    int bytes;
    static int size = 0;
    int bytesAccum = 0;

    public ReadThread(OnReadMessageReceived listener){
        readMessageListener = listener;
        readState = ReadOrImage.getReadState();
        imageIncomingState = ReadOrImage.getImageIncomingState();
        messageProcessed = ReadOrImage.getMessageProcessed();
    }


    public void run(){
        try {
            in = TCPSocket.getSocket().getInputStream();
            while (!Thread.currentThread().isInterrupted() && (ThreadActive ==true)){

                //checks for the new states
                readState = ReadOrImage.getReadState();
                imageIncomingState = ReadOrImage.getImageIncomingState();

                while (readState && (!Thread.currentThread().isInterrupted()) && (ThreadActive ==true)) {
                    buffer = new byte[500];
                    bytes = 0;
                    bytes = in.read(buffer);
                    readState = false;
                    ReadOrImage.setReadState(false);
                    if (!(bytes < 0)) {
                        readMessageListener.messageReceived(buffer);
                    } else if (bytes < 0) {
                        cancel();
                    }
                }
                while ((imageIncomingState) && (!Thread.currentThread().isInterrupted()) && (ThreadActive ==true)){
                    Tools.debug("ReadThread.java", "Begin reading image file with size of " + String.valueOf(size) + " bytes");
                    buffer = new byte[size];
                    bytes = 0;
                    bytesAccum = 0;

                    while ((bytesAccum < size) && (size != 0)){
                        bytes = in.read(buffer,bytesAccum,buffer.length-bytesAccum);
                        bytesAccum += bytes;
                        Tools.debug("ReadThread.java", "Bytes read is " + String.valueOf(bytesAccum) + "/" + String.valueOf(size) + " bytes");
                        if (bytes <0){
                            Tools.debug("ReadThread.java", "Socket interrupted suddenly, closing threads.");
                            cancel();
                            break;
                        }
                    }
                    imageIncomingState= false;
                    ReadOrImage.setImageIncoming(false);
                    readMessageListener.messageReceived(buffer);
                }

                //Waits till the message is processed before repeating the loop.
                while (!messageProcessed  && (!Thread.currentThread().isInterrupted()) && (ThreadActive ==true)){
                    SystemClock.sleep(100);
                    messageProcessed = ReadOrImage.getMessageProcessed();
                }
                ReadOrImage.resetMessage();
                messageProcessed = false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            cancel();
        }
    }

    public interface OnReadMessageReceived {
        void messageReceived(byte[] message);
    }

    public void cancel(){
        Log.d("ReadThread", "interrupted, closing Thread");
        Thread.currentThread().interrupt();
        ThreadActive = false;
        readMessageListener.messageReceived(MotoProtocol.CloseSocket());
        if (in != null){
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            in = null;
        }
        MainActivity.readThread = null;
    }
}
