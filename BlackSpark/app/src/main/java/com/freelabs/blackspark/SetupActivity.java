package com.freelabs.blackspark;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.UUID;


class SetupActivity extends Activity {

    private static final String MAC_ADDRESS = "20:16:01:20:66:61";
    private BluetoothAdapter myBluetooth = null;
    private BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;
    private final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private final int REQ_CODE = 9;

    //CAR COMMANDS
    private static final String MOVE_FORWARD = "f";
    private static final String MOVE_BACKWARD = "b";
    private static final String MOVE_STOP = "s";
    private static final String MOVE_FORCE_STOP = "x";
    private static final String TURN_LEFT = "l";
    private static final String TURN_RIGHT = "r";


    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.setup);

    }

    public void discoverDevices(){

    }


    public void connectToDevice(){

    }

    private void getPairedDevices(){
        /*
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

        // If there are paired devices
        if (pairedDevices.size() > 0) {
            // Loop through paired devices
            for (BluetoothDevice device : pairedDevices) {
                // Add the name and address to an array adapter to show in a ListView
                mArrayAdapter.add(device.getName() + "\n" + device.getAddress());
            }
        }
        */
    }



    //CAR FEATURES
    private void requestCommand(String cmd){

        if (btSocket!=null)
        {
            try {

                btSocket.getOutputStream().write(cmd.getBytes());
                btSocket.getOutputStream().flush();

                Log.e("BLUETOOTH COMMAND:", "sending command " + cmd);

            }
            catch (IOException e)
            {
                Toast.makeText(this,"ERROR: \n" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }


    }



    private long ping(){

        long pingResult;

        byte[] buffer = new byte[256];  // buffer store for the stream
        int bytes;

        try {

            long startTime = System.nanoTime();
            // ... the code being measured ...

            btSocket.getOutputStream().write("P".getBytes());
            btSocket.getOutputStream().flush();

            DataInputStream mmInStream = new DataInputStream(btSocket.getInputStream());

            // Read from the InputStream
            bytes = mmInStream.read(buffer);

            // ... the code being measured ...
            pingResult = System.nanoTime() - startTime;

            String readMessage = new String(buffer, 0, bytes);


            Toast.makeText(this,String.valueOf(pingResult),Toast.LENGTH_LONG);


            return pingResult;

        }catch(Exception e){

            Toast.makeText(this,"ERROR: \n" + e.getMessage(), Toast.LENGTH_SHORT).show();

        }


        return 0;
    }

    //BLUETOOTH CONNECTION

    private class ConnectThread extends AsyncTask<Void, Void, Exception> {

        private boolean ConnectSuccess = true; //if it's here, it's almost connected

        @Override
        protected void onPreExecute() {
            Toast.makeText(getApplicationContext(), "Connecting to bluetooth", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected Exception doInBackground(Void... devices) {

            try {

                if (btSocket == null || !isBtConnected) {

                    //get the mobile bluetooth device
                    myBluetooth = BluetoothAdapter.getDefaultAdapter();

                    if (myBluetooth == null) {

                        //Show a message that the device has no bluetooth adapter
                        Toast.makeText(getApplicationContext(), "Bluetooth Device Not Available", Toast.LENGTH_LONG).show();

                    } else if (!myBluetooth.isEnabled()) {

                        //Ask to the user turn the bluetooth on
                        Intent bluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(bluetoothIntent, REQ_CODE);

                    }


                    BluetoothDevice remoteDevice = myBluetooth.getRemoteDevice(MAC_ADDRESS);//connects to the device's address and checks if it's available
                    btSocket = remoteDevice.createInsecureRfcommSocketToServiceRecord(myUUID);//create a RFCOMM (SPP) connection

                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();//start connection

                }

            } catch (IOException e) {

                ConnectSuccess = false;//if the try failed, you can check the exception here
                return e;

            }
            return null;
        }

        @Override
        protected void onPostExecute(Exception result) {

            //after the doInBackground, it checks if everything went fine
            super.onPostExecute(result);

            if (!ConnectSuccess) {

                Toast.makeText(getApplicationContext(), result.getMessage(), Toast.LENGTH_SHORT).show();

            } else {

                Toast.makeText(getApplicationContext(),"Connected to bluetooth",Toast.LENGTH_LONG).show();
                isBtConnected = true;
            }

        }
    }

}
