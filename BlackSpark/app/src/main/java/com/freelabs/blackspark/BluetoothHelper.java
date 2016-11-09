package com.freelabs.blackspark;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.util.UUID;

class BluetoothHelper {

    private static final String MAC_ADDRESS = "20:16:01:20:66:61";
    private BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;
    private final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private String remoteAddress = null;
    private BluetoothHelperListener listener;


    BluetoothHelper(String deviceAddress) {
        remoteAddress = deviceAddress;
    }


    void setListener(BluetoothHelperListener l){
        listener = l;
    }


    void tryToConnect(){
        new ConnectionThread(remoteAddress).start();
    }


    void disconnect(){

    }

    private class ConnectionThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectionThread(String address) {

            // Use a temporary object that is later assigned to mmSocket,
            // because mmSocket is final
            BluetoothSocket tmp = null;
            mmDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(MAC_ADDRESS);

            // Get a BluetoothSocket to connect with the given BluetoothDevice
            try {
                // MY_UUID is the app's UUID string, also used by the server code
                tmp = mmDevice.createRfcommSocketToServiceRecord(myUUID);
            } catch (IOException e) {
                e.printStackTrace();
            }
            mmSocket = tmp;
        }

        public void run() {
            // Cancel discovery because it will slow down the connection
            BluetoothAdapter.getDefaultAdapter().cancelDiscovery();

            try {
                // Connect the device through the socket. This will block
                // until it succeeds or throws an exception
                mmSocket.connect();
            } catch (IOException connectException) {
                // Unable to connect; close the socket and get out
                try {
                    mmSocket.close();
                } catch (IOException closeException) {
                    closeException.printStackTrace();

                }
                return;
            }

            // Do work to manage the connection (in a separate thread)
            //manageConnectedSocket(mmSocket);
        }

        /** Will cancel an in-progress connection, and close the socket */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }

    /*if (btSocket!=null) //If the btSocket is busy
                    {
                        try
                        {
                            btSocket.close(); //close connection
                            btSocket=null;
                            isBtConnected=false;

                            ((TextView) findViewById(R.id.txtBluetoothStatus)).setText("Disconnected");

                        }
                        catch (IOException e)
                        {
                            makeText(getApplicationContext(),"ERROR: \n" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }*/

    /*
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
                        startActivityForResult(bluetoothIntent, REQUEST_CODE_ENABLE_BLT);

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
*/
/*
        if (btSocket!=null) //If the btSocket is busy
        {
            try
            {
                btSocket.close(); //close connection
                btSocket=null;
                isBtConnected=false;
            }
            catch (IOException e)
            {
                makeText(getApplicationContext(),"ERROR: \n" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }*/


}
