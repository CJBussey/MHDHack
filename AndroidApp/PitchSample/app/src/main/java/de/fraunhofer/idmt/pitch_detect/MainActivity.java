package de.fraunhofer.idmt.pitch_detect;

import java.util.Arrays;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.balazsbela.pitchsample.R;

public class MainActivity extends Activity
{
    private Button startButton;
    private Button stopButton;
    private TextView sliderText;
    private SeekBar slider;

    private float threshold;

    private Dialog settingsDialog;

    private final int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
    private final int sampleRate = 44100;

    private int bufferSize = 0;
    private final int channelConfiguration = AudioFormat.CHANNEL_IN_MONO;

    private AudioRecord audioRecord;

    private AudioTask audioTask;

    private short[] sampleBuffer = null;

    // we display current freq by sending msg to handler and updating ui here
    // -> not possible directly in doInBackground of async task!
    private static float currentFreq = 0;
    private static TextView resultText = null;
    private static Handler uiHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            if (resultText != null)
                resultText.setText(String.valueOf(currentFreq));
        }
    };

    private OscConnection oscConnection = new OscConnection(this);

    private AudioRecord findAudioRecord()
    {
        try
        {
            int bufferSize = AudioRecord.getMinBufferSize(sampleRate,
                    channelConfiguration, audioEncoding);

            if (bufferSize != AudioRecord.ERROR_BAD_VALUE)
            {
                // check if we can instantiate and have a success
                AudioRecord recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
                        sampleRate, channelConfiguration, audioEncoding, bufferSize);

                if (recorder.getState() == AudioRecord.STATE_INITIALIZED)
                    return recorder;
            }
        } catch (Exception e)
        {
            Log.e("Main", "Couldn't setup mic!");
        }
        return null;
    }

//    private static int[] mSampleRates = new int[] { 8000, 11025, 22050, 44100 };
//    public AudioRecord findAudioRecord() {
//        for (int rate : mSampleRates) {
//            for (short audioFormat : new short[] { AudioFormat.ENCODING_PCM_8BIT, AudioFormat.ENCODING_PCM_16BIT }) {
//                for (short channelConfig : new short[] { AudioFormat.CHANNEL_IN_MONO, AudioFormat.CHANNEL_IN_STEREO }) {
//                    try {
//                        Log.d("Main", "Attempting rate " + rate + "Hz, bits: " + audioFormat + ", channel: "
//                                + channelConfig);
//                        int bufferSize = AudioRecord.getMinBufferSize(rate, channelConfig, audioFormat);
//
//                        if (bufferSize != AudioRecord.ERROR_BAD_VALUE) {
//                            // check if we can instantiate and have a success
//                            AudioRecord recorder = new AudioRecord(MediaRecorder.AudioSource.DEFAULT, rate, channelConfig, audioFormat, bufferSize);
//
//                            if (recorder.getState() == AudioRecord.STATE_INITIALIZED)
//                                return recorder;
//                        }
//                    } catch (Exception e) {
//                        Log.e("Main", "Couldn't setup mic!");
//                    }
//                }
//            }
//        }
//        return null;
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startButton = (Button) findViewById(R.id.startButton);
        stopButton = (Button) findViewById(R.id.stopButton);
        resultText = (TextView) findViewById(R.id.resultText);
        slider = (SeekBar) findViewById(R.id.slider);
        sliderText = (TextView) findViewById(R.id.sliderText);

        int value = slider.getProgress();
        sliderText.setText("" + value);

        slider.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        threshold = progress * 100;
                        sliderText.setText("" + threshold);
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                }
        );

        bufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfiguration,
                audioEncoding);

        // using short array -> half size
        bufferSize /= 2;

        sampleBuffer = new short[bufferSize];

        audioRecord = this.findAudioRecord();

        if (audioRecord == null)
        {
            startButton.setEnabled(false);
            stopButton.setEnabled(false);
            resultText.setText("Mic error!");
            return;
        }

        startButton.setEnabled(true);
        stopButton.setEnabled(false);

        PitchDetect.createPitchDetect(sampleRate);
        //set for polyphonic
//    float[] freqs = new float[1];
//    freqs[0] = 220;
//    PitchDetect.setReferenceFrequencies(freqs); 
//    PitchDetect.setPitchDetectVersion(PitchDetect.POLYPHONIC_DETECT); 
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        settingsDialog = new Dialog(this);

        settingsDialog.setContentView(R.layout.options_menu);
        settingsDialog.setTitle("Settings");
        settingsDialog.setCancelable(true);
        settingsDialog.show();

        final EditText ipText = (EditText) settingsDialog.findViewById(R.id.ipText);
        final EditText portText = (EditText) settingsDialog.findViewById(R.id.portText);

        SharedPreferences preferences = getSharedPreferences("settings", MODE_PRIVATE);

        ipText.setText(preferences.getString("host", ""), TextView.BufferType.EDITABLE);
        portText.setText("" + preferences.getLong("port", 7000), TextView.BufferType.EDITABLE);

        final Button button = (Button) settingsDialog.findViewById(R.id.saveButton);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onSaveSettingsButton();
            }
        });

        return true;
    }

    public void onSaveSettingsButton() {
        SharedPreferences preferences = getSharedPreferences("settings", MODE_PRIVATE);
        SharedPreferences.Editor edit= preferences.edit();

        final EditText ipText = (EditText) settingsDialog.findViewById(R.id.ipText);
        final EditText portText = (EditText) settingsDialog.findViewById(R.id.portText);

        edit.putString("host", ipText.getText().toString());
        edit.putLong("port", Integer.parseInt(portText.getText().toString()));
        edit.commit();

        settingsDialog.hide();
    }


    public void startAnalyze(View v)
    {
        audioTask = new AudioTask();
        audioTask.execute();
        startButton.setEnabled(false);
        stopButton.setEnabled(true);
    }

    public void stopAnalyze(View v)
    {
        audioTask.stopExecute();
        startButton.setEnabled(true);
        stopButton.setEnabled(false);
    }

    private class AudioTask extends AsyncTask<Void, Void, Void>
    {
        private boolean execute = false;

        public synchronized void stopExecute()
        {
            this.execute = false;
        }

        @SuppressLint("NewApi")
        @Override
        protected Void doInBackground(Void... params)
        {
            audioRecord.startRecording();

            while (this.execute)
            {
                int read = audioRecord.read(sampleBuffer, 0, bufferSize);

                if (read <= 0)
                    continue;

                // resize if less or more was read
                if (read != bufferSize)
                {
                    sampleBuffer = Arrays.copyOf(sampleBuffer, read);
                    bufferSize = read;
                }

                double currentRMS = calculateRMS(sampleBuffer);

                currentFreq = 0;
                if (currentRMS > threshold)
                {
                    //send to c++
                    float[] results = PitchDetect.processSampleBuff(sampleBuffer);

                    //something went wrong
                    if (results == null)
                        continue;

                    //we always get at least one value
                    currentFreq = results[0];
                    uiHandler.sendEmptyMessage(0); // tell ui it should update
                    oscConnection.sendMessage(currentFreq);
                } else {
                    oscConnection.sendMessage(0);
                }

            }
            audioRecord.stop();
            return null;
        }


        private double calculateRMS(short[] audioBuffer) {
            double rms = 0.0;
            for (int i = 0; i < audioBuffer.length; i++) {
                rms += audioBuffer[i] * audioBuffer[i];
            }
            rms = rms / audioBuffer.length;
            return Math.sqrt(rms);
        }

        @Override
        protected void onPreExecute()
        {
            this.execute = true;
        }
    }
}
