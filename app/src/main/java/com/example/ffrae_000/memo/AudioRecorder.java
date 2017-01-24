package com.example.ffrae_000.memo;

import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;

import static android.content.ContentValues.TAG;

class AudioRecorder {
    static final String OUTPUT_FILE = Environment.getExternalStorageDirectory() + "/MEMO/temp.3gpp";
    private MediaRecorder mR = null;
    private boolean isStarted = false;
    private File outFile = null;

    AudioRecorder() {
        setRecorder();
    }

    /**
     * Prepares the AudioRecorder
     */
    public void setRecorder() {

        if (Utilities.externalStoragecheck()) {
            outFile = new File(OUTPUT_FILE);
        } else {
            outFile = new File(Environment.getDataDirectory().getPath() + "/temp.3gpp");
        }



        Utilities.delete(outFile);
        mR = new MediaRecorder();
        mR.setAudioSource(MediaRecorder.AudioSource.MIC);
        mR.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mR.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mR.setOutputFile(OUTPUT_FILE);

        try {
            mR.prepare();
        } catch (IOException | IllegalStateException e) {
            Log.e(TAG, "prepare() failed: " + e);
        }
        mR.setOnInfoListener(new MediaRecorder.OnInfoListener() {
            @Override
            public void onInfo(MediaRecorder mediaRecorder, int what, int extra) {
                Log.i(TAG, "Info what: " + what + " extra: " + extra);
                switch (what) {
                    case MediaRecorder.MEDIA_RECORDER_INFO_UNKNOWN:
                        Log.i(TAG, "Info: MEDIA_RECORDER_INFO_UNKNOWN");
                        break;
                    case MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED:
                        Log.i(TAG, "Info: MEDIA_RECORDER_INFO_DURATION_REACHED");
                        break;
                    case MediaRecorder.MEDIA_RECORDER_INFO_MAX_FILESIZE_REACHED:
                        Log.i(TAG, "Info: MEDIA_RECORDER_INFO_MAX_FILESIZE_REACHED");
                        break;
                }
            }
        });
        mR.setOnErrorListener(new MediaRecorder.OnErrorListener() {
            @Override
            public void onError(MediaRecorder mediaRecorder, int what, int extra) {
                switch (what) {
                    case MediaRecorder.MEDIA_RECORDER_ERROR_UNKNOWN:
                        Log.e(TAG, "Info: MEDIA_RECORDER_ERROR_UNKNOWN");
                        break;
                    case MediaRecorder.MEDIA_ERROR_SERVER_DIED:
                        Log.e(TAG, "Info: MEDIA_ERROR_SERVER_DIED");
                        break;
                }
            }
        });

    }

    /**
     * Starts the record
     */
    public void startRecord() {
        try {
            mR.start();
            isStarted = true;
        } catch (IllegalStateException e) {
            Log.e(TAG, "start() failed: " + e);
        }
    }

    /**
     * Stops the record
     */
    public void stopRecord() {
        try {
            mR.stop();
            mR.release();
            mR = null;
            isStarted = false;
        } catch (NullPointerException | IllegalStateException e) {
            Log.i(TAG, "stop() failed: " + e);
        }
    }

    /**
     * Resets the MediaRecorder by releasing it frees memory
     */
    public void resetRecorder() {
        mR.release();
        mR = null;
        setRecorder();
    }

    public boolean isRecording() {
        return isStarted;
    }

    public File getOutFile() {
        return outFile;
    }
}