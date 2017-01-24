package com.example.ffrae_000.memo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.View;

import java.io.File;
import java.util.concurrent.Callable;

import static android.content.ContentValues.TAG;

abstract class Utilities {

    /**
     * Function to easily create an AlertDialog
     *
     * @param context Calling Activity
     * @param message Message to display
     * @param positive Message for the positive button. Iff no positive button is used null is required
     * @param negative Mesage for the negative button. If no negative button is used null is required
     * @param view Any additional GUI elements for the AlertDialog. If none used null is required
     * @param result A Callable<Void>() function, which is called if the positive button is clicked
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

    static void moveFile(File oldName, File newName) {
        if (oldName.exists()) {
            if (!oldName.renameTo(newName)) {
                Log.i(TAG, "Could not rename " + oldName.getPath() + " into " + newName.getPath());
            }
        }
    }

    /**
     * Function to delete a File
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
     * Function to convert milliseconds time to Timer Format Hours:Minutes:Seconds
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
}