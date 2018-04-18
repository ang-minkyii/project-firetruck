package saco.ProjectFireTruckV2.etc_utilities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

/**
 * Created by ftjx73 on 1/28/2016.
 */
public class PermissionCheck {

    private static boolean STORAGE_PERMISSION = false;
    private static boolean CAMERA_PERMISSION = false;
    private static int PermissionCheck;

    public static void permissionCheckStorage(Context context, Activity activity) {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            Log.i("myactivity", "trying to request for permission");
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PermissionCheck);
        }
        else STORAGE_PERMISSION = true;
    }


    public static void permissionCheckCamera(Context context, Activity activity) {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
            Log.i("myactivity", "trying to request for permission");
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.CAMERA},
                    PermissionCheck);
        }
        else CAMERA_PERMISSION = true;
    }

    public static boolean getCameraPermisson(){
        return CAMERA_PERMISSION;
    }

    public static boolean getStoragePermisson(){
        return STORAGE_PERMISSION;
    }
}
