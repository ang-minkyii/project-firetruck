package saco.ProjectFireTruckV2.list_adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import static saco.ProjectFireTruckV2.Activities.MainActivity.HOTSPOT_MODE;
import static saco.ProjectFireTruckV2.Activities.MainActivity.WIFI_MODE;
import static saco.ProjectFireTruckV2.Activities.MainActivity.currentMode;

/**
 * Created by PMGC37 on 1/29/2016.
 */
public class SettingsActivity_listAdapter extends ArrayAdapter<String> {
    public SettingsActivity_listAdapter(Context context, int resource) {super(context, resource);}
    @Override
    public boolean isEnabled(int position) {
        if (position == 0){
            if (currentMode != HOTSPOT_MODE) {return false;}
        }
        if (position == 1){
            if (currentMode != WIFI_MODE) {return false;}
        }
        return true;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = super.getView(position, convertView, parent);
        if (!isEnabled(position)){v.setEnabled(false);}
        else v.setEnabled(true);
        return v;
    }
}
