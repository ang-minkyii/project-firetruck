package saco.ProjectFireTruckV2.etc_utilities;

import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import saco.ProjectFireTruckV2.Activities.MainActivity;

/**
 * Created by PMGC37 on 1/28/2016.
 */
public class Tools {

    private static int count_1000;

    public static void printBytes(byte[] protocolPacket) {
        printBytes(protocolPacket,protocolPacket.length);
    }

    public static void printBytes(byte[] protocolPacket, int printLimit) {
        if (MainActivity.DEBUG_MODE == MainActivity.DEBUG_OFF){
            return;
        }
        int bufferSize = printLimit;
        System.out.print("Printing BytePacket of size: " + String.valueOf(bufferSize));
        System.out.println();
        System.out.print("Byte content: \n");
        count_1000 = 0;
        for (int i = 0; i< bufferSize; i++){
            count_1000 ++;
            if (count_1000>1000){
                System.out.print("\n");
                count_1000 = 0;
            }
            System.out.print(Integer.toHexString(protocolPacket[i] & 0xFF | 0x100).substring(1));
            if (Integer.toHexString(protocolPacket[i] & 0xFF | 0x100)== "\0"){
                break;
            }
        }
        System.out.print("\n.\n.\n.\n");
    }

    public static byte[] longToBytes(long integer, int size){
        ByteBuffer bytesBuffer = ByteBuffer.allocate(8).order(ByteOrder.BIG_ENDIAN).putLong(integer);
        byte[] bytes = bytesBuffer.array();
        byte[] byteTruncated = new byte[size];

        for (int count = 0; count<size; count++){
            byteTruncated[count] = bytes[bytes.length-(size-count)];
        }
        return byteTruncated;
    }

    public static byte[] hexToBytes(String hexString, int size){
        int intFromString= Integer.parseInt(hexString, 16);
        byte[] byteTruncated = longToBytes(intFromString, size);
        return byteTruncated;
    }

    public static void debug(String tag, String msg){
        if (MainActivity.DEBUG_MODE == MainActivity.DEBUG_STANDARD){
            Log.d(tag,msg);
        }
        else if (MainActivity.DEBUG_MODE == MainActivity.DEBUG_AS_ERROR){
            Log.e(tag,msg);
        }
    }
}
