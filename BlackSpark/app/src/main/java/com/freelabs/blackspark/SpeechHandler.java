package com.freelabs.blackspark;


import android.os.AsyncTask;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import edu.cmu.pocketsphinx.Assets;
import edu.cmu.pocketsphinx.Hypothesis;
import edu.cmu.pocketsphinx.RecognitionListener;
import edu.cmu.pocketsphinx.SpeechRecognizer;

import static android.widget.Toast.makeText;
import static edu.cmu.pocketsphinx.SpeechRecognizerSetup.defaultSetup;

public class SpeechHandler implements RecognitionListener, TextToSpeech.OnInitListener {
    private static final String MENU_SEARCH = "menu";
    private static final String CHECK_SEARCH = "check";
    private static final String KEY_SEARCH = "key";
    private static final String KEY = "wake up now";
    private TextToSpeech tts;
    private String state = "init";
    private String command = "";
    private SpeechRecognizer recognizer = null;
    // Recognizer initialization is a time-consuming and it involves IO,
    // so we execute it in async task
/*
    new AsyncTask<Void, Void, Exception>() {
        @Override
        protected Exception doInBackground(Void... params) {
            try {
                setupRecognizer();
            } catch (IOException e) {
                return e;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Exception result) {
            if (result != null) {
                Toast.makeText(getApplicationContext(), "Failed to init recognizer ", Toast.LENGTH_LONG).show();
                recognizer = null;
            } else {
                Toast.makeText(getApplicationContext(), "Recognizer is ready", Toast.LENGTH_LONG).show();
            }
        }
    }.execute();*/
/*
    private void setupRecognizer() throws IOException {

        Assets assets = new Assets(ControlActivity.this);
        File assetsDir = assets.syncAssets();

        recognizer = defaultSetup()
                .setAcousticModel(new File(assetsDir, "en-us-ptm"))
                .setDictionary(new File(assetsDir, "cmudict-en-us.dict"))
                .setRawLogDir(assetsDir)
                .setKeywordThreshold(1e-45f)
                .setBoolean("-allphone_ci", true)
                .getRecognizer();

        recognizer.addListener(this);

        recognizer.addKeyphraseSearch(KEY_SEARCH, KEY);

        File menuGrammar = new File(assetsDir, "menu.gram");
        recognizer.addGrammarSearch(MENU_SEARCH, menuGrammar);

        File checkGrammar = new File(assetsDir, "checkMenu.gram");
        recognizer.addGrammarSearch(CHECK_SEARCH, checkGrammar);

        tts = new TextToSpeech(this, this);
    }*/


    @Override
    public void onBeginningOfSpeech() {

    }

    @Override
    public void onEndOfSpeech() {

        /*
        makeText(getApplicationContext(), "END of speech", Toast.LENGTH_SHORT).show();
        recognizer.stop();

        if (state.equals("menu")) {

            recognizer.startListening(MENU_SEARCH, 5000);
            Log.e("end of speech", "MENU SEARCH ");

        } else if (state.equals("go_on")) {

            recognizer.startListening(CHECK_SEARCH, 5000);
            Log.e("end of speech", "CHECK SEARCH");

        }*/
    }

    @Override
    public void onPartialResult(Hypothesis hypothesis) {

    }

    @Override
    public void onResult(Hypothesis hypothesis) {
        /*
        if (hypothesis != null) {

            String text = hypothesis.getHypstr();

            if (text.equals("forward")) {

                speakOut("Do you really want to go forward ?");
                command = "forward";

            } else if (text.equals("backward")) {

                speakOut("Do you really want to go backward ?");
                command = "backward";

            } else if (text.equals("turn around")) {

                speakOut("Do you really want to turn around ?");
                command = "turn around";

            } else if (text.equals("connect")) {

                speakOut("Do you really want to go connect ?");
                command = "connect";

            } else if (text.equals("turn left")) {

                speakOut("Do you really want to turn left ?");
                command = "turn left";

            } else if (text.equals("turn right")) {

                speakOut("Do you really want to turn right ?");
                command = "turn right";

            } else if (text.equals("correct")) {

                speakOut("Ok, we are going on");

                if (command.equals("forward")) {

                    makeText(getApplicationContext(), "Going forward", Toast.LENGTH_SHORT).show();
                    //requestCommand(MOVE_FORWARD);

                } else if (command.equals("backward")) {

                    makeText(getApplicationContext(), "Going backward", Toast.LENGTH_SHORT).show();
                    //requestCommand(MOVE_BACKWARD);

                } else if (command.equals("connect")) {

                    makeText(getApplicationContext(), "Connecting", Toast.LENGTH_SHORT).show();
                    //new ConnectBT().execute();

                } else if (command.equals("turn left")) {

                    makeText(getApplicationContext(), "Turning left", Toast.LENGTH_SHORT).show();
                    //requestCommand(TURN_LEFT);
                } else if (command.equals("turn right")) {

                    makeText(getApplicationContext(), "Turning right", Toast.LENGTH_SHORT).show();
                    //requestCommand(TURN_RIGHT);

                }

            } else if (text.equals("wrong")) {

                speakOut("Let's try again");

            }

            makeText(getApplicationContext(), "onResult!!", Toast.LENGTH_SHORT).show();

        }
         */
    }

    @Override
    public void onError(Exception e) {
//makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onTimeout() {
            /*
            * makeText(getApplicationContext(), "Time out reset", Toast.LENGTH_SHORT).show();

        if (state.equals("menu")) {

            recognizer.startListening(CHECK_SEARCH, 5000);

            Log.e("end of speech", "CHECK SEARCH ");

        } else if (state.equals("go_on")) {

            recognizer.startListening(MENU_SEARCH, 5000);
            Log.e("end of speech", "MENU SEARCH");

        }*/
    }



    //TEXT TO SPEECH

    private void speakOut(String speakingWords) {
        //this.tts.speak(speakingWords, 0, null, "utteranceID");
    }

    @Override
    public void onInit(int status) {
/*
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
*/
    }


}

