package com.example.trialproject3.Helper;

import android.content.Context;
import android.content.DialogInterface;

import androidx.appcompat.app.AlertDialog;

public class AlertDialogHelper {

    private Context context;
    private AlertDialog dialog; // Reference to the dialog

    public AlertDialogHelper(Context context) {
        this.context = context;
    }

    public void showAlertDialog(String title, String message,
                                String positiveButtonText, DialogInterface.OnClickListener positiveButtonListener,
                                String negativeButtonText, DialogInterface.OnClickListener negativeButtonListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);

        if (positiveButtonText != null) {
            builder.setPositiveButton(positiveButtonText, positiveButtonListener);
        }

        if (negativeButtonText != null) {
            builder.setNegativeButton(negativeButtonText, negativeButtonListener);
        }

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void showSimpleAlertDialog(String title, String message) {
        showAlertDialog(title, message,
                "OK",
                null,
                null,
                null);
    }

    public void dismissDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss(); // Dismiss the dialog
        }
    }
}
