package com.gpd.addbin.bin.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.cocosw.favor.FavorAdapter;
import com.gpd.addbin.R;
import com.gpd.addbin.bin.db.Capacity;
import com.gpd.addbin.bin.info.Account;

import java.util.List;

public class CapacityListAdapter extends ArrayAdapter<Capacity> {

    private List<Capacity> dataSet;
    Context mContext;

    Account accountCurrentBinDetails;

    // View lookup cache
    private static class ViewHolder {
        TextView txtName;
    }

    public CapacityListAdapter(List<Capacity> data, Context context) {
        super(context, R.layout.single_spinner_dropdown_item, data);
        this.dataSet = data;

        this.mContext=context;
        accountCurrentBinDetails = new FavorAdapter.Builder(getContext()).build().create(Account.class);

    }

//    @Override
//    public void onClick(View v) {
//
//        int position=(Integer) v.getTag();
//        Object object= getItem(position);
//        Governorate dataModel=(Governorate)object;
//
//        switch (v.getId())
//        {
//            case R.id.name_select_spinner_item_single:
//                Snackbar.make(v, "Release date " +dataModel.getGovernorate_id(), Snackbar.LENGTH_LONG)
//                        .setAction("No action", null).show();
//
//                break;
//        }
//    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Capacity dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.select_spinner_item_single, parent, false);
            viewHolder.txtName = (TextView) convertView.findViewById(R.id.name_select_spinner_item_single);

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }


        lastPosition = position;

        viewHolder.txtName.setText(dataModel.getCapacity_name());
        // Return the completed view to render on screen
        return convertView;
    }
}