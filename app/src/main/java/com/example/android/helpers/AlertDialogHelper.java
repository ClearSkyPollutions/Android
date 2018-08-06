package com.example.android.helpers;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.text.Html;


public class AlertDialogHelper {

    public static AlertDialog createOkAlertDialog(String title, String message, Context context){

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        alertDialogBuilder.setTitle(title);
        alertDialogBuilder
                .setMessage(Html.fromHtml(message))
                .setPositiveButton("OK", (dialog, id) -> dialog.cancel());

       return alertDialogBuilder.create();
    }
}
