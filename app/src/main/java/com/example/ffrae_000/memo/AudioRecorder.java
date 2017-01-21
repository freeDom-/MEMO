package com.example.ffrae_000.memo;

import android.media.MediaRecorder;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class AudioRecorder {
    private MediaRecorder mR = null;
    private boolean isStarted = false;
    private boolean isPaused = false;


    public AudioRecorder() {
        setRecorder();
    }

    public void setRecorder() {
        mR = new MediaRecorder();
        mR.setAudioSource(MediaRecorder.AudioSource.MIC);
        mR.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mR.setOutputFile("/temp.3CPP");
        mR.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        try {
            mR.prepare();
        } catch (IOException e) {
            System.out.print("Couldn't prepare!!!!");
        }
    }

    public void startRecord() {
        mR.start();
        isStarted = true;
    }
    //TODO: pause und resume haben ein API Level Problem ma schaun ob wir das lösen oder weglassen

/*    public void pauseRecord(){
        if (isStarted && !isPaused) {
            mR.pause();
            isPaused = true;
        }
    }

    public void resumeRecord(){
        if(isPaused){
            mR.resume();
        }
    }
*/


    public void stopRecord() {
        mR.stop();
        mR.release();
        isStarted = false;
        isPaused = false;
    }

    public void resetRecorder() {
        mR.release();
        mR = null;
        setRecorder();
    }

    public void save(String path) {
        try {
            FileInputStream fis = new FileInputStream("/temp.3CPP");
            FileOutputStream fos = new FileOutputStream(path);
            fos.write(fis.read());
            //TODO: try to delete the temp file
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}