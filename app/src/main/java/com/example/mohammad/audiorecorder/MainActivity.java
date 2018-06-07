package com.example.mohammad.audiorecorder;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button recordBtn;
    private AudioRecorder recorder = null;
    private boolean isRecording = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recordBtn = findViewById(R.id.record_btn);
        recordBtn.setText(R.string.record);
        recordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isRecording) {
                    recorder = new AudioRecorder(true);
                    recorder.startRecording();
                    recordBtn.setText(R.string.stop);
                    isRecording =true;
                } else {
                    if (recorder != null) {
                        recorder.stopRecording();
                        recordBtn.setText(R.string.record);
                        isRecording =false;
                    }
                }
            }
        });
    }
}
