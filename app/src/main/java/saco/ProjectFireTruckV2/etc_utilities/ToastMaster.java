package saco.ProjectFireTruckV2.etc_utilities;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import saco.ProjectFireTruckV2.R;

/**
 * Created by PMGC37 on 2/4/2016.
 */
public class ToastMaster {
    static Toast toast = null;
    static LayoutInflater inflater;
    static View view;
    static ImageView imageView;


    public static void toastText(Context context, String message){
        toast.cancel();
        toast = toast.makeText(context,message,Toast.LENGTH_SHORT);
        toast.show();
    }

    public static void toastImage(Context context, Activity activity, Bitmap image){
        //inflater = getLayoutInflater();
        inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.toast_layout, null);
        //imageView = (ImageView)activity.findViewById(R.id.toastForImage);
        //imageView.setImageBitmap(image);
        if (toast != null){
            toast.cancel();
        } else {
            toast = new Toast(context);
        }
        toast.setView(view);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
}
