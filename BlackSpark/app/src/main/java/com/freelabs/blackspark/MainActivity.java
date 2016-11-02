/* ====================================================================
 * Copyright (c) 2014 Alpha Cephei Inc.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY ALPHA CEPHEI INC. ``AS IS'' AND
 * ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL CARNEGIE MELLON UNIVERSITY
 * NOR ITS EMPLOYEES BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * ====================================================================
 */

package com.freelabs.blackspark;
import static android.widget.Toast.makeText;
import static edu.cmu.pocketsphinx.SpeechRecognizerSetup.defaultSetup;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import edu.cmu.pocketsphinx.Assets;
import edu.cmu.pocketsphinx.Hypothesis;
import edu.cmu.pocketsphinx.RecognitionListener;
import edu.cmu.pocketsphinx.SpeechRecognizer;
import android.speech.tts.TextToSpeech;

public class MainActivity extends Activity implements RecognitionListener,TextToSpeech.OnInitListener {

    private static final String MENU_SEARCH = "menu";
    private static final String CHECK_SEARCH = "check";
    private static final String KEY_SEARCH = "key";
    private static final String KEY = "wake up now";
    private TextToSpeech tts;
    private String state = "init";
    private String command = "";
    private boolean isReady=true;
    private static final String MAC_ADDRESS = "20:16:01:20:66:61";
    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");


    //CAR COMMANDS
    private static final String MOVE_FORWARD = "f";
    private static final String MOVE_BACKWARD = "b";
    private static final String MOVE_STOP = "s";
    private static final String MOVE_FORCE_STOP = "x";
    private static final String TURN_LEFT = "l";
    private static final String TURN_RIGHT = "r";



    private SpeechRecognizer recognizer = null;


    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);

        setContentView(R.layout.bluetooth_control);
        //Setting up buttons listeners
        Button btnConnect = (Button) findViewById(R.id.btnConnect);
        Button btnPing = (Button) findViewById(R.id.btnDiscover);
        Button btnForward = (Button) findViewById(R.id.btnForward);
        Button btnStop = (Button) findViewById(R.id.btnStop);
        Button btnBackward = (Button) findViewById(R.id.btnBackward);
        Button btnStartConv = (Button) findViewById(R.id.btnStartConv);
        Button btnDisconnect = (Button) findViewById(R.id.btnDisconnect);

        btnConnect.setOnClickListener(new View.OnClickListener(){

            public void onClick(View v) {
                ((TextView) findViewById(R.id.txtBluetoothStatus)).setText("Connecting");
                new ConnectBT().execute();
            }

        });

        btnStop.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                requestCommand(MOVE_FORCE_STOP);
            }

        });

        btnPing.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                ping();
            }

        });

        btnForward.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_DOWN) {


                    requestCommand(MOVE_FORWARD);


                } else if (event.getAction() == MotionEvent.ACTION_UP) {


                    requestCommand(MOVE_STOP);

                }

                return true;

            }

        });


        btnBackward.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_DOWN) {

                    requestCommand(MOVE_BACKWARD);


                } else if (event.getAction() == MotionEvent.ACTION_UP) {

                    requestCommand(MOVE_STOP);

                }

                return true;

            }

        });


        btnStartConv.setOnClickListener(new View.OnClickListener(){

            public void onClick(View v) {
                speakOut("welcome");
            }

        });


        btnDisconnect.setOnClickListener(new View.OnClickListener(){

            public void onClick(View v) {

                if (btSocket!=null) //If the btSocket is busy
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
                }

            }

        });

        ((TextView) findViewById(R.id.caption_text)).setText("Preparing the recognizer");

        // Recognizer initialization is a time-consuming and it involves IO,
        // so we execute it in async task

        new AsyncTask<Void, Void, Exception>() {
            @Override
            protected Exception doInBackground(Void... params) {
                try {
                    Assets assets = new Assets(MainActivity.this);
                    File assetDir = assets.syncAssets();
                    setupRecognizer(assetDir);

                } catch (IOException e) {
                    return e;
                }
                return null;
            }

            @Override
            protected void onPostExecute(Exception result) {
                if (result != null) {
                    ((TextView) findViewById(R.id.caption_text))
                            .setText("Failed to init recognizer " + result);
                    recognizer=null;
                }else{

                    ((TextView) findViewById(R.id.caption_text))
                            .setText("Recognizer is ready");

                }
            }
        }.execute();

    }



    private void setupRecognizer(File assetsDir) throws IOException {

        recognizer = defaultSetup()
                .setAcousticModel(new File(assetsDir, "en-us-ptm"))
                .setDictionary(new File(assetsDir, "cmudict-en-us.dict"))
                .setRawLogDir(assetsDir)
                .setKeywordThreshold(1e-45f)
                .setBoolean("-allphone_ci", true)
                .getRecognizer();

        recognizer.addListener(this);

        recognizer.addKeyphraseSearch(KEY_SEARCH,KEY);

        File menuGrammar = new File(assetsDir, "menu.gram");
        recognizer.addGrammarSearch(MENU_SEARCH, menuGrammar);

        File checkGrammar = new File(assetsDir, "checkMenu.gram");
        recognizer.addGrammarSearch(CHECK_SEARCH, checkGrammar);

        tts = new TextToSpeech(this, this);

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
                makeText(getApplicationContext(),"ERROR: \n" + e.getMessage(), Toast.LENGTH_SHORT).show();
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

            TextView txtResult = (TextView) findViewById(R.id.result_text);
            txtResult.setText(String.valueOf(pingResult));


            return pingResult;

        }catch(Exception e){

            makeText(getApplicationContext(),"ERROR: \n" + e.getMessage(), Toast.LENGTH_SHORT).show();

        }


        return 0;
    }




    @Override
    public void onBeginningOfSpeech() {

    }



    @Override
    public void onPartialResult(Hypothesis hypothesis) {


    }

    @Override
    public void onEndOfSpeech() {

        makeText(getApplicationContext(),"END of speech", Toast.LENGTH_SHORT).show();
        recognizer.stop();

        if(state.equals("menu")) {

            recognizer.startListening(MENU_SEARCH, 5000);
            Log.e("end of speech", "MENU SEARCH ");

        }else if(state.equals("go_on")){

            recognizer.startListening(CHECK_SEARCH, 5000);
            Log.e("end of speech", "CHECK SEARCH");

        }

    }



    @Override
    public void onResult(Hypothesis hypothesis) {

        if (hypothesis != null) {

            String text = hypothesis.getHypstr();

            if (text.equals("forward")) {

                speakOut("Do you really want to go forward ?");
                command="forward";

            }else if(text.equals("backward")){

                speakOut("Do you really want to go backward ?");
                command="backward";

            }else if(text.equals("turn around")){

                speakOut("Do you really want to turn around ?");
                command="turn around";

            }else if(text.equals("connect")){

                speakOut("Do you really want to go connect ?");
                command="connect";

            }else if(text.equals("turn left")){

                speakOut("Do you really want to turn left ?");
                command="turn left";

            }else if(text.equals("turn right")){

                speakOut("Do you really want to turn right ?");
                command="turn right";

            }else if(text.equals("correct")){

                speakOut("Ok, we are going on");

                if(command.equals("forward")){

                    makeText(getApplicationContext(), "Going forward", Toast.LENGTH_SHORT).show();
                    requestCommand(MOVE_FORWARD);

                }else if (command.equals("backward")){

                    makeText(getApplicationContext(), "Going backward", Toast.LENGTH_SHORT).show();
                    requestCommand(MOVE_BACKWARD);

                }else if (command.equals("connect")){

                    makeText(getApplicationContext(), "Connecting", Toast.LENGTH_SHORT).show();
                    new ConnectBT().execute();

                }else if (command.equals("turn left")){

                    makeText(getApplicationContext(), "Turning left", Toast.LENGTH_SHORT).show();
                    requestCommand(TURN_LEFT);
                }else if (command.equals("turn right")){

                    makeText(getApplicationContext(), "Turning right", Toast.LENGTH_SHORT).show();
                    requestCommand(TURN_RIGHT);

                }

            }else if(text.equals("wrong")){

                speakOut("Let's try again");

            }

            makeText(getApplicationContext(),"onResult!!", Toast.LENGTH_SHORT).show();

        }



    }


    @Override
    public void onTimeout() {
        makeText(getApplicationContext(), "Time out reset", Toast.LENGTH_SHORT).show();

        if(state.equals("menu")) {

            recognizer.startListening(CHECK_SEARCH, 5000);

            Log.e("end of speech", "CHECK SEARCH ");

        }else if(state.equals("go_on")){

            recognizer.startListening(MENU_SEARCH, 5000);
            Log.e("end of speech", "MENU SEARCH");

        }
    }


    @Override
    public void onError(Exception error) {

        makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();

    }


    //TEXT TO SPEECH

    private void speakOut(String speakingWords)
    {
        this.tts.speak(speakingWords, 0, null,"utteranceID");
    }

    @Override
    public void onInit(int status) {

        if (status == TextToSpeech.SUCCESS) {

            int result = tts.setLanguage(Locale.US);

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {

                Log.e("TTS", "This Language is not supported");
                makeText(getApplicationContext(), "This Language is not supported", Toast.LENGTH_SHORT).show();

            } else {

                this.tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {

                    @Override
                    public void onStart(String utteranceId) {
                        if (recognizer != null) {

                            recognizer.stop();
                            Log.e("Progress", "REC STOP");

                        } else {
                            Log.e("Progress", "recognizer is not defined");
                        }
                    }

                    @Override
                    public void onDone(String utteranceId) {

                        if (recognizer != null) {

                            if (state.equals("init")) {

                                recognizer.startListening(MENU_SEARCH, 5000);
                                state = "menu";
                                Log.e("Progress", "MENU SEARCH");

                            } else if (state.equals("menu")) {

                                recognizer.startListening(CHECK_SEARCH, 5000);
                                state = "go_on";
                                Log.e("Progress", "CHECK SEARCH");

                            } else if (state.equals("go_on")) {

                                recognizer.startListening(MENU_SEARCH, 5000);
                                state = "menu";
                                Log.e("Progress", "MENU SEARCH");

                            }

                        } else {
                            Log.e("Progress", "recognizer is not defined");
                        }
                    }

                    @Override
                    public void onError(String utteranceId) {
                        Log.e("Progress", "ERROR");
                    }

                });

                // speakOut("welcome");

            }

        } else {
            Log.e("TTS", "Initilization Failed!");
        }

    }


    //BLUETOOTH CONNECTION

    private class ConnectBT extends AsyncTask<Void, Void, Exception> {

        private boolean ConnectSuccess = true; //if it's here, it's almost connected

        @Override
        protected void onPreExecute() {
            makeText(getApplicationContext(), "Connecting to bluetooth", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected Exception doInBackground(Void... devices) {

            try {

                if (btSocket == null || !isBtConnected) {

                    //get the mobile bluetooth device
                    myBluetooth = BluetoothAdapter.getDefaultAdapter();

                    if (myBluetooth == null) {

                        //Show a mensag. that the device has no bluetooth adapter
                        Toast.makeText(getApplicationContext(), "Bluetooth Device Not Available", Toast.LENGTH_LONG).show();
                        //finish apk
                        finish();

                    } else if (!myBluetooth.isEnabled()) {

                        //Ask to the user turn the bluetooth on
                        Intent turnBTon = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(turnBTon, 1);

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

                ((TextView) findViewById(R.id.txtBluetoothStatus)).setText("Connecting failed try again");
                makeText(getApplicationContext(), result.getMessage(), Toast.LENGTH_SHORT).show();

            } else {

                ((TextView) findViewById(R.id.txtBluetoothStatus)).setText("Connected to bluetooth");
                isBtConnected = true;

            }

        }
    }




    @Override
    public void onStop(){

        if(recognizer!=null) {
            recognizer.cancel();
            recognizer.shutdown();
            recognizer=null;
        }

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
        }

        super.onStop();
    }





    @Override
    public void onDestroy() {


        if (this.tts != null)
        {
            this.tts.stop();
            this.tts.shutdown();
        }

        super.onDestroy();
    }

}

