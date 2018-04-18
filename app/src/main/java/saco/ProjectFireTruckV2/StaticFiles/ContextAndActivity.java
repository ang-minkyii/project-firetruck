package saco.ProjectFireTruckV2.StaticFiles;

import android.app.Activity;
import android.content.Context;

import saco.ProjectFireTruckV2.Activities.MainActivity;

/**
 * Created by PMGC37 on 2/4/2016.
 */
public class ContextAndActivity {

    static Context context = MainActivity.context;
    static Activity activity = MainActivity.context;

    public static Context getContext(){

        return context;
    }
}
