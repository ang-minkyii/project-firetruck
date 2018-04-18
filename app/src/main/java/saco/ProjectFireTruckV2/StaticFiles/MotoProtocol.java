package saco.ProjectFireTruckV2.StaticFiles;

import java.nio.ByteBuffer;

import static saco.ProjectFireTruckV2.etc_utilities.Tools.longToBytes;

/**
 * Created by PMGC37 on 1/14/2016.
 */
public class MotoProtocol {
    private static int TRANSFER_REQUEST = 0x0002;
    private static int TRANSFER_REPLY = 0x8002;
    private static int HEADER_REQUEST = 0x0001;
    private static int HEADER_REPLY = 0x8001;
    private static int RESULT_OK = 0;
    private static int RESULT_NO = 0;
    private static int FILE_TYPE_BMP = 1;
    private static int FILE_TYPE_JPEG = 2;
    private static int FILE_TYPE_RAW = 3;
    private static int FILE_TYPE_PNG = 4;
    private static int transactionID = 0;
    private static int checkSum = 0;

    public static byte[] fileHeaderDynamicPacket(String fileName, int fileSize, int TransactionID, String reqOrReply){

        //String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        //String filename = desiredFileName + "_" + File.separator+ "IMG_" +timeStamp+".jpg";
        ByteBuffer fileHeaderPacket_buffer;
        String nullTerminator = "\0";

        int fileLength = fileSize;
        int filenameLength = fileName.length();
        int flag = 0;
        byte[] Opcode_bytes = longToBytes(0, 2);
        byte[] result_bytes = longToBytes(RESULT_OK, 1);
        byte[] fileTypeJPEG_bytes = longToBytes(FILE_TYPE_JPEG, 2);
        byte[] transactionID_bytes = longToBytes(TransactionID, 2);
        byte[] checkSum_bytes = longToBytes(checkSum, 8);
        byte[] fileLength_bytes = longToBytes(fileLength, 4);
        byte[] filenameLength_bytes = longToBytes(filenameLength + nullTerminator.getBytes().length, 2);
        byte[] filename_bytes = fileName.getBytes();


        switch (reqOrReply){
            case "Reply":
                flag = 1;
                Opcode_bytes = longToBytes(HEADER_REPLY, 2);
                break;
            case "Request":
                flag = 0;
                Opcode_bytes = longToBytes(HEADER_REQUEST, 2);
                break;
            default:
                break;
        }

        int initByteSize = Opcode_bytes.length + fileTypeJPEG_bytes.length + transactionID_bytes.length
                + checkSum_bytes.length + fileLength_bytes.length + filenameLength_bytes.length;

        if(flag == 1){
            fileHeaderPacket_buffer = ByteBuffer.allocate(result_bytes.length + initByteSize + filename_bytes.length );
        }else{
            fileHeaderPacket_buffer = ByteBuffer.allocate(initByteSize + filename_bytes.length );
        }

        fileHeaderPacket_buffer.put(Opcode_bytes);
        if(flag == 1) {
            fileHeaderPacket_buffer.put(result_bytes);
        }
        fileHeaderPacket_buffer.put(fileTypeJPEG_bytes);
        fileHeaderPacket_buffer.put(transactionID_bytes);
        fileHeaderPacket_buffer.put(checkSum_bytes);
        fileHeaderPacket_buffer.put(fileLength_bytes);
        fileHeaderPacket_buffer.put(filenameLength_bytes);
        fileHeaderPacket_buffer.put(filename_bytes);
        //fileHeaderPacket_buffer.put("\0".getBytes());

        byte[] fileHeaderPacket = fileHeaderPacket_buffer.array();
        return fileHeaderPacket;
    }

    public static byte[] contentTransfer(byte[] file, int TransactionID,  String reqOrReply){

        ByteBuffer contentTransferPacket_buffer;

        int flag = 0;
        byte[] Opcode_bytes = longToBytes(0,2);
        byte[] result_bytes = longToBytes(RESULT_OK,1);
        byte[] transactionID_bytes = longToBytes(TransactionID, 2);
        byte[] byte_index = longToBytes(0, 4);
        byte[] fileLength_bytes = longToBytes(file.length, 2);

        switch (reqOrReply){
            case "Reply":
                flag = 1;
                Opcode_bytes = longToBytes(0x8002, 2);
                break;
            case "Request":
                flag = 0;
                Opcode_bytes = longToBytes(0x0002, 2);
                break;
            default:
                break;
        }

        int initByteSize = Opcode_bytes.length  + transactionID_bytes.length + byte_index.length;

        if(flag == 1){  //Reply
            contentTransferPacket_buffer = ByteBuffer.allocate(initByteSize + result_bytes.length  /*+ "\n".length()*/);
        }else{  //Request
            contentTransferPacket_buffer = ByteBuffer.allocate(initByteSize + fileLength_bytes.length + file.length  /*+ "\n".length()*/);
        }

        contentTransferPacket_buffer.put(Opcode_bytes);
        if(flag == 1) {
            contentTransferPacket_buffer.put(result_bytes);
        }
        contentTransferPacket_buffer.put(transactionID_bytes);
        contentTransferPacket_buffer.put(byte_index);
        if (flag == 0) {
            contentTransferPacket_buffer.put(fileLength_bytes);
            contentTransferPacket_buffer.put(file);
        }
        //contentTransferPacket_buffer.put("\n".getBytes());

        byte[] contentTransferPacket = contentTransferPacket_buffer.array();
        return contentTransferPacket;
    }


    public static byte[] CloseSocket(){
        ByteBuffer buffer;
        byte[] Opcode_bytes = longToBytes(0x0211,2);
        buffer = ByteBuffer.allocate(Opcode_bytes.length);
        buffer.put(Opcode_bytes);
        byte[] CloseSocket = buffer.array();
        return CloseSocket;
    }
}
