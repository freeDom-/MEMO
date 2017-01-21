package com.example.ffrae_000.memo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;

import java.util.concurrent.Callable;

abstract class Helpers {

    static void showAlert(Context context, String message, String positive, String negative,
                                       View view, final Callable<Void> result) {
        // String negative, View view, Callable function must be null if not used
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setMessage(message);
        alert.setPositiveButton(positive, new DialogInterface.OnClickListener() {
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
        if (negative != null) {
            alert.setNegativeButton(negative, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
        }
        if (view != null) {
            alert.setView(view);
        }

        alert.create();
        alert.show();
    }
}
