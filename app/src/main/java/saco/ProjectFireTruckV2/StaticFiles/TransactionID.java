package saco.ProjectFireTruckV2.StaticFiles;

/**
 * Created by PMGC37 on 1/28/2016.
 */
public class TransactionID {
    public static int TransactionID = 0;

    public static synchronized void setTransactionID(int newTransactionID){
        TransactionID = newTransactionID;
        return;
    }

    public static synchronized int getTransactionID(){
        return TransactionID;
    }
}
