package saco.ProjectFireTruckV2.list_adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import saco.ProjectFireTruckV2.Activities.WifiActivity;
import saco.ProjectFireTruckV2.R;

/**
 * Created by PMGC37 on 1/29/2016.
 */
public class WifiActivity_listAdapter<String> extends ArrayAdapter<String> {


    public WifiActivity_listAdapter(Context context, int resource) {
        super(context, resource);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = super.getView(position, convertView, parent);
        if ((position == WifiActivity.connectedPosition)){
            //v.setEnabled(false);
            v.setBackgroundResource(R.color.teal);
        }
        else {
            v.setBackgroundResource(R.color.transparent);
        }
        return v;
    }
}
