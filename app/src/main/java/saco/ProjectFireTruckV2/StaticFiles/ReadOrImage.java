package saco.ProjectFireTruckV2.StaticFiles;

/**
 * Created by PMGC37 on 1/28/2016.
 */
public class ReadOrImage {
    public static boolean READ = true;
    public static boolean IMAGE_INCOMING = false;
    public static boolean MESSAGE_PROCESSED=false;
    public static int imageSize = 0;

    public static synchronized void setReadState(boolean status){
        READ = status;
        //Tools.debug("ReadOrImage.java", "current READ is: " + String.valueOf(READ));
        MESSAGE_PROCESSED =true;
        return;
    }

    public static synchronized void setImageIncoming(boolean status){
        IMAGE_INCOMING = status;
        //Tools.debug("ReadOrImage.java", "current IMAGE_INCOMING is: " + String.valueOf(READ));
        return;
    }

    public static synchronized void setImageSize(int size){
        imageSize = size;
        return;
    }

    public static synchronized int getImageSize(){return imageSize;}

    public static synchronized boolean getReadState(){
        return READ;
    }

    public static synchronized boolean getImageIncomingState(){
        return IMAGE_INCOMING;
    }

    public static synchronized boolean getMessageProcessed(){
        return MESSAGE_PROCESSED;
    }

    public static synchronized void resetMessage(){
        MESSAGE_PROCESSED = false;
        return;
    }
}
