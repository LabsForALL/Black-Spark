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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import edu.cmu.pocketsphinx.SpeechRecognizer;
import android.speech.tts.TextToSpeech;


public class ControlActivity extends Activity {

    //CAR COMMANDS
    private static final String MOVE_FORWARD = "f";
    private static final String MOVE_BACKWARD = "b";
    private static final String MOVE_STOP = "s";
    private static final String MOVE_FORCE_STOP = "x";
    private static final String TURN_LEFT = "l";
    private static final String TURN_RIGHT = "r";

    private boolean isConnected = false;
    private BluetoothHelper bluetoothHelper = null;
    private Button btnConnect;

    @Override
    public void onCreate(Bundle state, PersistableBundle persistableBundle) {
        super.onCreate(state);

        //Getting the device information
        Intent intent = getIntent();
        String remoteDeviceName = intent.getStringExtra(SearchActivity.EXTRA_NAME);
        String remoteDeviceAddress = intent.getStringExtra(SearchActivity.EXTRA_ADDRESS);

        //Setting up a bluetooth helper object
        bluetoothHelper = new BluetoothHelper(remoteDeviceAddress);
        bluetoothHelper.setListener(new BluetoothHelperListener() {
            @Override
            public void onConnectionSuccess() {
                isConnected = true;
                btnConnect.setText(R.string.btn_state_connected);
            }

            @Override
            public void onConnectionError() {
                isConnected = false;
                btnConnect.setText(R.string.btn_state_disconnected);
            }
        });

        //Setting up the layout
        setContentView(R.layout.control_layout);
        btnConnect = (Button) findViewById(R.id.btnConnection);
        Button btnForward = (Button) findViewById(R.id.btnForward);
        Button btnBackward = (Button) findViewById(R.id.btnBackward);
        Button btnStop = (Button) findViewById(R.id.btnStop);
        Button btnLeft = (Button) findViewById(R.id.btnLeft);
        Button btnRight = (Button) findViewById(R.id.btnRight);

        //Setting up the buttons listeners
        btnConnect.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(isConnected){
                    bluetoothHelper.tryToConnect();
                    btnConnect.setText(R.string.btn_state_connecting);
                }else{
                    bluetoothHelper.disconnect();
                }
            }
        });

        btnForward.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    //requestCommand(MOVE_FORWARD);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    //requestCommand(MOVE_STOP);
                }
                return true;
            }
        });

        btnBackward.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    //requestCommand(MOVE_BACKWARD);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    //requestCommand(MOVE_STOP);
                }
                return true;
            }

        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //requestCommand(MOVE_FORCE_STOP);
            }
        });

        btnLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });





    }

    @Override
    protected void onPause() {
        super.onPause();

    }
}

