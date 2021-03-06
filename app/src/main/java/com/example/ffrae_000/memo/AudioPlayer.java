package com.example.ffrae_000.memo;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;

// TODO: AudioPlayer/Recorder crashes when adding an AudioMemo in API 25

class AudioPlayer {
    private final MediaPlayer mP;
    private final Runnable timedUpdate;
    private final Handler seekHandler = new Handler();
    private final String finalTime;
    private boolean isPlaying;  // required for seeking
    // Layout
    private String currentTime;
    private TextView time;
    private SeekBar seekBar;
    private ImageButton playPauseBtn;

    AudioPlayer(String path) throws IOException {
        mP = new MediaPlayer();
        mP.setDataSource(path);
        mP.prepare();

        isPlaying = false;
        currentTime = Utilities.milliSecondsToTimer(mP.getCurrentPosition());
        finalTime = Utilities.milliSecondsToTimer(mP.getDuration());
        // Create Runnable for updating the UI
        timedUpdate = new Runnable() {
            @Override
            public void run() {
                update();
                seekHandler.postDelayed(this, 250);
            }
        };
    }

    /**
     * Releases MediaPlayer and frees memory
     */
    public void releasePlayer() {
        stopPlaying();
        mP.release();
    }

    /**
     * Creates the Layout of the player
     *
     * @param context The context in which the layout should be created
     * @return The root LinearLayout of the AudioPlayer is returned.
     */
    public LinearLayout createLayout(Context context) {
        // Create LinearLayouts
        LinearLayout playerLayout = new LinearLayout(context);
        playerLayout.setOrientation(LinearLayout.HORIZONTAL);
        playerLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        LinearLayout verticalLayout = new LinearLayout(context);
        verticalLayout.setOrientation(LinearLayout.VERTICAL);
        verticalLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        // Create TimeView
        time = new TextView(context);
        time.setText(currentTime + "/" + finalTime);
        // Create SeekBar
        seekBar = new SeekBar(context);
        seekBar.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        seekBar.setMax(mP.getDuration());
        // CreateButtons
        playPauseBtn = new ImageButton(context);
        playPauseBtn.setImageResource(android.R.drawable.ic_media_play);
        // Add Views to verticalLayout
        verticalLayout.addView(time);
        verticalLayout.addView(seekBar);
        // Add Views to playerLayout
        playerLayout.addView(playPauseBtn);
        playerLayout.addView(verticalLayout);

        // Set OnSeekbarChangeListener to seek through the recording
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                // Update the time
                currentTime = Utilities.milliSecondsToTimer(seekBar.getProgress());
                time.setText(currentTime + "/" + finalTime);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (mP.isPlaying()) {
                    mP.pause();
                    // disabling updating Seekbar
                    seekHandler.removeCallbacks(timedUpdate);
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mP.seekTo(seekBar.getProgress());
                // Start playing if it was playing before seeking
                if (isPlaying) {
                    mP.start();
                    // re-enabling updating Seekbar
                    timedUpdate.run();
                }
            }
        });

        // Set OnCompletionListener for mP
        mP.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                // Update UI to show final positions
                update();
                stopPlaying();
            }
        });

        // Set OnClickListener for playPauseBtn
        playPauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mP.isPlaying()) {
                    pausePlaying();
                } else {
                    startPlaying();
                }
            }
        });
        return playerLayout;
    }

    /**
     * Updates the SeekBar, which is also updating the time
     */
    private void update() {
        seekBar.setProgress(mP.getCurrentPosition());
    }

    /**
     * Starts playing the audio
     */
    private void startPlaying() {
        mP.start();
        timedUpdate.run();
        playPauseBtn.setImageResource(android.R.drawable.ic_media_pause);
        isPlaying = true;
    }

    /**
     * Stops playing the audio
     */
    private void stopPlaying() {
        mP.stop();
        seekHandler.removeCallbacks(timedUpdate);
        playPauseBtn.setImageResource(android.R.drawable.ic_media_play);
        try {
            mP.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        isPlaying = false;
    }

    /**
     * Pauses the audio
     */
    private void pausePlaying() {
        if (mP.isPlaying()) {
            mP.pause();
            seekHandler.removeCallbacks(timedUpdate);
            playPauseBtn.setImageResource(android.R.drawable.ic_media_play);
            isPlaying = false;
        }
    }
}
