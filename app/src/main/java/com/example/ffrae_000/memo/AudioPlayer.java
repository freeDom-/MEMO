package com.example.ffrae_000.memo;

import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by sir_cancle_a_lot on 19.01.17.
 */

public class AudioPlayer {
    private MediaPlayer mP;

    public AudioPlayer(String path) throws FileNotFoundException{
        mP = new MediaPlayer();
        try{
            mP.setDataSource(path);
            mP.prepare();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startPlaying(){
        mP.start();
    }

    public void stopPlaying(){
        mP.stop();
        try {
            mP.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void pausePlaying(){
        if (mP.isPlaying()){
            mP.pause();
        }
    }

    public void releasePlayer(){
        mP.release();
    }

}