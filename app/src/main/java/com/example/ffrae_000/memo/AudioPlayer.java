package com.example.ffrae_000.memo;

import android.content.Context;
import android.media.Image;
import android.media.MediaPlayer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.io.IOException;

public class AudioPlayer {
    private MediaPlayer mP;

    public AudioPlayer(String path) throws FileNotFoundException {
        mP = new MediaPlayer();
        try {
            mP.setDataSource(path);
            mP.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startPlaying() {
        mP.start();
    }

    public void stopPlaying() {
        mP.stop();
        try {
            mP.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void pausePlaying() {
        if (mP.isPlaying()) {
            mP.pause();
        }
    }

    public void releasePlayer() {
        mP.release();
    }

    //TODO: MAKE LAYOUT LOOK GREAT AGAIN!!!!!

    public LinearLayout setPlayerLayout(Context context, String length, String currTime){
        String currentTime = currTime;
        String finalTime = length;

        LinearLayout playerLayout = new LinearLayout(context);
        playerLayout.setOrientation(LinearLayout.HORIZONTAL);

        TextView time = new TextView(context);
        String timeString = currentTime + "/" + finalTime;
        time.setText(timeString);

        SeekBar seekBar = new SeekBar(context);                         //TODO: Layout and configurate seekbar

        final ImageButton play_pause = new ImageButton(context);
        play_pause.setImageResource(android.R.drawable.ic_media_play);

        ImageButton stop = new ImageButton(context);                    //TODO: vllt überflüssig
        stop.setImageResource(android.R.drawable.ic_media_previous);


        playerLayout.addView(time);
        playerLayout.addView(seekBar);
        playerLayout.addView(play_pause);
        playerLayout.addView(stop);

        final View.OnClickListener play = new View.OnClickListener() {

            //TODO fix Buttonproblem (doesen't change back) maybe a problem with the audiofile

            @Override
            public void onClick(View view) {
                if (mP.isPlaying()){
                    if (mP != null) {
                        pausePlaying();
                        play_pause.setImageResource(android.R.drawable.ic_media_play);
                    }
                } else {
                    if(mP != null) {
                        startPlaying();
                        play_pause.setImageResource(android.R.drawable.ic_media_pause);
                    }
                }
            }
        };

        play_pause.setOnClickListener(play);

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopPlaying();
            }
        });

        return playerLayout;
    }

}
