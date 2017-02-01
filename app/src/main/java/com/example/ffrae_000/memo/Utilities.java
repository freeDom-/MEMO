package com.example.ffrae_000.memo;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;

import static android.content.ContentValues.TAG;

abstract class Utilities {

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final String[] PERMISSIONS_STORAGE = {
            //Manifest.permission.READ_EXTERNAL_STORAGE,            // ONLY usable for API 16+
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    /**
     * Function to easily create an AlertDialog
     *
     * @param context  Calling Activity
     * @param message  Message to display
     * @param positive Message for the positive button. Iff no positive button is used null is required
     * @param negative Mesage for the negative button. If no negative button is used null is required
     * @param view     Any additional GUI elements for the AlertDialog. If none used null is required
     * @param result   A Callable<Void>() function, which is called if the positive button is clicked
     * @return The created and shown AlertDialog is returned.
     */
    static AlertDialog showAlert(Context context, String message, String positive, String negative,
                                 View view, final Callable<Void> result) {
        // String negative, View view, Callable function must be null if not used
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message);
        if (positive != null) {
            builder.setPositiveButton(positive, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    if (result != null) {
                        try {
                            result.call();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
        if (negative != null) {
            builder.setNegativeButton(negative, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
        }
        if (view != null) {
            builder.setView(view);
        }

        AlertDialog alert = builder.create();
        alert.show();
        return alert;
    }

    /**
     * Function to move or rename a File
     *
     * @param oldName File with the old path
     * @param newName File with the new path
     */
    static void moveFile(File oldName, File newName) {
        if (oldName.exists()) {
            if (!oldName.renameTo(newName)) {
                Log.i(TAG, "Could not rename " + oldName.getPath() + " into " + newName.getPath());
            }
        }
    }

    /**
     * Function to delete a File
     *
     * @param delfile File to delete
     */
    static void delete(File delfile) {
        if (delfile.exists()) {
            if (!delfile.delete()) {
                Log.i(TAG, "Could not delete " + delfile.getPath());
            }
        }
    }

    /**
     * Function to create a Directory
     *
     * @param dir File to the Path where directory should be created
     */
    static void createDirectory(File dir) {
        if (!dir.exists()) {
            if (!dir.mkdir()) {
                Log.i(TAG, "Could not create " + dir.getPath());
            }
        }
    }

    /**
     * Function to Check if there is an External Storage available
     *
     * @return a Boolean which is true if there is External Storage available
     */

    static boolean externalStoragecheck() {
        boolean bool = false;
        if (Environment.isExternalStorageRemovable()) {
            if (!Environment.isExternalStorageEmulated()) {
                bool = Environment.getExternalStorageState().compareTo(Environment.MEDIA_MOUNTED) == 0;
            }
        }
        return bool;
    }

    /**
     * Checks if the app has permission to write to device storage
     * <p>
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity The activity in which the code is being executed
     */
    static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }


    /**
     * Function to convert milliseconds time to Timer Format Hours:Minutes:Seconds
     *
     * @param milliseconds Number of miliseconds to convert
     * @return A time-formatted String is returned.
     */
    static String milliSecondsToTimer(long milliseconds) {
        String finalTimerString = "";
        String secondsString;

        // Convert total duration into time
        int hours = (int) (milliseconds / (1000 * 60 * 60));
        int minutes = (int) (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);
        // Add hours if there
        if (hours > 0) {
            finalTimerString = hours + ":";
        }

        // Prepending 0 to seconds if it is one digit
        if (seconds < 10) {
            secondsString = "0" + seconds;
        } else {
            secondsString = "" + seconds;
        }

        finalTimerString = finalTimerString + minutes + ":" + secondsString;

        // return timer string
        return finalTimerString;
    }

    static int getNextId(List<Memo> memos) {
        int nextId = 0;
        List<Memo> temp = new LinkedList<>(memos);

        // Sort memos by their id
        Collections.sort(temp, new Comparator<Memo>() {
            @Override
            public int compare(Memo memo, Memo t1) {
                return Integer.valueOf(memo.getId()).compareTo(t1.getId());
            }
        });

        for (Memo m : temp) {
            if (m.getId() != nextId) break;
            else nextId++;
        }
        return nextId;
    }
}