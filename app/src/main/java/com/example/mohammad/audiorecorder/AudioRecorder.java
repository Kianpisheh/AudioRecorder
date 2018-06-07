package com.example.mohammad.audiorecorder;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static java.lang.Byte.toUnsignedInt;

public class AudioRecorder {

    private String LOG_TAG="";
    private static final int SAMPLE_RATE = 8000;
    private static final int CHANNELS = AudioFormat.CHANNEL_IN_MONO;
    private static final int ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    private AudioRecord recorder = null;
    public boolean isRecording = false;
    private int bufferSize;
    private Thread recordingThread = null;
    private byte[] audioBuffer;
    private boolean writeToFile;


    public AudioRecorder(boolean write2File) {
        writeToFile = write2File;
    }

    public void startRecording() {
        recordingThread = new Thread(new Runnable() {
            @Override
            public void run() {

                bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNELS, ENCODING);
                if (bufferSize == AudioRecord.ERROR || bufferSize == AudioRecord.ERROR_BAD_VALUE) {
                    bufferSize = SAMPLE_RATE * 2;
                }
                System.out.println("buffer size: " + Integer.toString(bufferSize));
                audioBuffer = new byte[bufferSize]; // container for read values

                recorder = new AudioRecord(MediaRecorder.AudioSource.DEFAULT, SAMPLE_RATE,
                        CHANNELS, ENCODING, bufferSize);

                if (recorder.getState() != AudioRecord.STATE_INITIALIZED) {
                    Log.e(LOG_TAG, "Audio Record can't initialize!");
                    return;
                }

                recorder.startRecording();
                isRecording = true;
                Log.v(LOG_TAG, "Start recording");

                if (writeToFile) {
                    String fileName = "/" + Long.toString(System.currentTimeMillis()) + "_audio1.txt";
                    File file = new File(Environment.getExternalStorageDirectory(), fileName);
                    System.out.println(file.toString());
                    FileOutputStream outputStream = null;
                    try {
                        outputStream = new FileOutputStream(file);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    while (isRecording) {
                        writeAudioDataToFile(outputStream);
                    }
                    try {
                        outputStream.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    stopRecording();
                    isRecording = false;
                }
            }
        });
        recordingThread.start();
    }

    public void stopRecording() {
        isRecording = false;
        recorder.stop();
        recorder.release();
        recorder = null;
        recordingThread = null;
    }

    public byte[] readAudioData() {
        byte[] audioSample = new byte[2];
        int status = recorder.read(audioSample, 0, 2);
        if (status == AudioRecord.ERROR_INVALID_OPERATION ||
                status == AudioRecord.ERROR_BAD_VALUE) {
            Log.e(LOG_TAG, "Error reading audio data!");
            return null;
        }
        return audioSample;
    }

    private void writeAudioDataToFile(FileOutputStream outputStream) {

        int status = recorder.read(audioBuffer, 0, audioBuffer.length);
        if (status == AudioRecord.ERROR_INVALID_OPERATION ||
                status == AudioRecord.ERROR_BAD_VALUE) {
            Log.e(LOG_TAG, "Error reading audio data!");
            return;
        }

        //System.out.println("0: " + audioBuffer[0]);
        System.out.println("1: " + audioBuffer[1]);
        try {
            outputStream.write(audioBuffer, 0, audioBuffer.length);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

