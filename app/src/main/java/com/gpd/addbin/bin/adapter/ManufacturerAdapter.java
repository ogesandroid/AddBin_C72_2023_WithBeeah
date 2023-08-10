package com.gpd.addbin.bin.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import com.gpd.addbin.R;
import com.gpd.addbin.bin.db.Manufacturer;

import java.util.List;

public class ManufacturerAdapter extends ArrayAdapter<Manufacturer> {

    LayoutInflater flater;

    public ManufacturerAdapter(Activity context, int resouceId, int textviewId, List<Manufacturer> list){

        super(context,resouceId,textviewId, list);
//        flater = context.getLayoutInflater();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        return rowview(convertView,position);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {

        Manufacturer rowItem = getItem(position);

        LayoutInflater inflaterDD = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dropDownView = inflaterDD.inflate(R.layout.single_spinner_dropdown_item, parent, false);

        TextView dd_Text = (TextView)dropDownView.findViewById(R.id.tv_spinner_dropdown_item_governorate_Spinner);
        dd_Text.setText(rowItem.getManufacturer_name());

        return dropDownView;


    }

    private View rowview(View convertView , int position){

        Manufacturer rowItem = getItem(position);

        viewHolder holder ;
        View rowview = convertView;
        if (rowview==null) {

            holder = new viewHolder();
            flater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowview = flater.inflate(R.layout.single_spinner_governorate, null, false);

            holder.txtTitle = (TextView) rowview.findViewById(R.id.tv_governorate_Spinner);
            rowview.setTag(holder);
        }else{
            holder = (viewHolder) rowview.getTag();
        }
        holder.txtTitle.setText(rowItem.getManufacturer_name());

        return rowview;
    }

    private class viewHolder{
        TextView txtTitle;
    }
}
