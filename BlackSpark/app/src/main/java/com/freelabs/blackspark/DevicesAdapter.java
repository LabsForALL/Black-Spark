package com.freelabs.blackspark;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class DevicesAdapter extends ArrayAdapter<BluetoothDevice> {

    public DevicesAdapter(Context context, ArrayList<BluetoothDevice> devices) {
        super(context, 0, devices);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Get the data item for this position
        BluetoothDevice device = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.device_item, parent, false);
        }

        // Lookup view for data population
        TextView deviceName = (TextView) convertView.findViewById(R.id.deviceName);
        TextView deviceAddress = (TextView) convertView.findViewById(R.id.deviceAddress);

        // Populate the data into the template view using the data object

        deviceName.setText(device.getName());
        deviceAddress.setText(device.getAddress());

        // Return the completed view to render on screen
        return convertView;
    }
}
