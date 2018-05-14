package com.example.android.helpers;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.Html;


public class AlertDialogHelper {

    public static AlertDialog createOkAlertDialog(String title, String message, Context context){

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        alertDialogBuilder.setTitle(title);
        alertDialogBuilder
                .setMessage(Html.fromHtml(message))
                .setPositiveButton("OK",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        dialog.cancel();
                    }
                });

       return alertDialogBuilder.create();
    }

}
