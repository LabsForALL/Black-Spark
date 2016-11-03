package com.freelabs.blackspark;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;


public class SearchActivity extends Activity {

    public static final String EXTRA_NAME = "EXTRA_NAME";
    public static final String EXTRA_ADDRESS = "EXTRA_ADDRESS";

    private final String TAG = "SearchActivity";
    private static final String SEARCH_STATE = "SEARCH_STATE";
    private final int REQUEST_CODE_ENABLE_BLT = 9;

    private BluetoothAdapter myBluetooth = null;
    private boolean isSearching = false;

    private ListView devicesListView = null;
    private Button btnSearch = null;
    private DevicesAdapter foundDevicesAdapter = null;
    private BroadcastReceiver searchResultReceiver = null;
    private BroadcastReceiver bluetoothStateReceiver = null;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //setting up layout and needed items
        setContentView(R.layout.setup_layout);
        btnSearch = (Button) findViewById(R.id.btnSearch);

        //restore previous state if available
        if (savedInstanceState != null) {
            isSearching = savedInstanceState.getBoolean(SEARCH_STATE);
        }

        //refreshing layout based on state
        refreshLayout();

        //getting the bluetooth
        myBluetooth = BluetoothAdapter.getDefaultAdapter();

        //the search button starts and stops the searching based on the state
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isSearching){
                    stopSearching();
                }else{
                    tryToSearch();
                }
            }
        });
    }


    private void tryToSearch(){
        if (myBluetooth == null) {
            Toast.makeText(this,"Your device doesn't support bluetooth!",Toast.LENGTH_LONG).show();
            return;
        }
        if (!myBluetooth.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_CODE_ENABLE_BLT);
        }else{
            startSearching();
        }
    }


    private void startSearching(){
        prepareResultsHandling();

        // starting the search and waiting results
        if (myBluetooth.isDiscovering()) {
            myBluetooth.cancelDiscovery();
        }

        //check if discovery is possible
        if(!myBluetooth.startDiscovery()){
            Toast.makeText(this,"Search failed",Toast.LENGTH_SHORT).show();
        }
    }

    private void prepareResultsHandling(){
        if(searchResultReceiver==null){

            //setting up the adapter for devices list
            devicesListView = (ListView) findViewById(R.id.devicesList);
            foundDevicesAdapter = new DevicesAdapter(this, new ArrayList<BluetoothDevice>());
            devicesListView.setAdapter(foundDevicesAdapter);

            final Intent intent = new Intent(this, ControlActivity.class);

            devicesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    TextView tmpView = (TextView) view.findViewById(R.id.deviceName);
                    String deviceName = tmpView.getText().toString();
                    tmpView = (TextView) view.findViewById(R.id.deviceAddress);
                    String deviceAddress = tmpView.getText().toString();

                    intent.putExtra(EXTRA_NAME, deviceName);
                    intent.putExtra(EXTRA_ADDRESS, deviceAddress);

                    startActivity(intent);
                }
            });

            // Add to list if there are paired devices
            Set<BluetoothDevice> pairedDevices = myBluetooth.getBondedDevices();
            if (pairedDevices.size() > 0) {
                foundDevicesAdapter.addAll(pairedDevices);
            }

            // setting up the receiver
            searchResultReceiver = new BroadcastReceiver() {
                public void onReceive(Context context, Intent intent) {
                    String action = intent.getAction();

                    switch (action){
                        case BluetoothDevice.ACTION_FOUND:
                            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                            foundDevicesAdapter.add(device);
                            break;
                        case BluetoothAdapter.ACTION_DISCOVERY_STARTED:
                            //Changing searching state
                            isSearching = true;
                            refreshLayout();
                            break;
                        case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                            //Changing searching state
                            isSearching = false;
                            refreshLayout();
                            break;
                    }
                }
            };

            // registering the receiver
            IntentFilter filter = new IntentFilter();
            filter.addAction(BluetoothDevice.ACTION_FOUND);
            filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
            filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
            registerReceiver(searchResultReceiver, filter);
        }
    }


    private void stopSearching(){
        myBluetooth.cancelDiscovery();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQUEST_CODE_ENABLE_BLT:
                if(resultCode == RESULT_OK){
                    startSearching();
                }else{
                    Toast.makeText(this,"Please, enable bluetooth in order to use this application.",
                            Toast.LENGTH_LONG).show();
                    finish();
                }
                break;
        }
    }


    private void refreshLayout(){
        if(isSearching){
            btnSearch.setText(R.string.btn_bluetooth_stop_search);
            btnSearch.setBackgroundColor(ContextCompat.getColor(this,R.color.colorStopSearch));
        }
        else{
            btnSearch.setText(R.string.btn_bluetooth_search);
            btnSearch.setBackgroundColor(ContextCompat.getColor(this,R.color.colorSearch));
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save the user's current game state
        outState.putBoolean(SEARCH_STATE, isSearching);
    }


    @Override
    protected void onStop() {
        super.onStop();
        if(searchResultReceiver!=null){
            unregisterReceiver(searchResultReceiver);
        }
        if(bluetoothStateReceiver != null){
            unregisterReceiver(bluetoothStateReceiver);
        }
        if(isSearching){
            stopSearching();
            isSearching=false;
        }
    }
}
