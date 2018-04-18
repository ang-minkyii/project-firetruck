package saco.ProjectFireTruckV2.etc_utilities;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import saco.ProjectFireTruckV2.StaticFiles.IPAddress;

/**
 * Created by ftjx73 on 1/8/2016.
 */
public class ReadWrite {

    public static synchronized String readFromFile(Context context, String file) {

        String ret = "";

        try {
            InputStream inputStream = context.openFileInput(file);

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString +"\n");
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }
        return ret;
    }

    public static synchronized void writeToFile(String data, Context context, String mode, String file) {
        switch (mode){
            case "Replace":
                try {
                    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(file, Context.MODE_PRIVATE));
                    outputStreamWriter.write(data);
                    outputStreamWriter.close();
                }
                catch (IOException e) {
                    Log.e("Exception", "File write failed: " + e.toString());
                }
                break;
            case "Append":
                try {
                    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(file, Context.MODE_PRIVATE | Context.MODE_APPEND));
                    outputStreamWriter.write(data);
                    outputStreamWriter.close();
                }
                catch (IOException e) {
                    Log.e("Exception", "File write failed: " + e.toString());
                }
                break;
        }
    }

    public static synchronized void storeData(Context context, String fileName, String type, String file){
        String timeStamp = new SimpleDateFormat("yyyy/MM/dd_HH:mm:ss").format(Calendar.getInstance().getTime());
        writeToFile(">> " + type + "\r\n", context, "Append",file);
        writeToFile(fileName + "\r\n", context, "Append",file);
        writeToFile("Delivered: " + timeStamp + "\r\n", context, "Append",file);
        writeToFile("To IP Address: "+ IPAddress.getIP()+"\r\n",context,"Append",file);
        writeToFile("\r\n", context, "Append",file);
    }
}

