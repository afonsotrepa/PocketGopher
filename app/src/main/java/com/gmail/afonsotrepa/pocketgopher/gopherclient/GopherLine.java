package com.gmail.afonsotrepa.pocketgopher.gopherclient;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;

/**
 *
 */

public abstract class GopherLine implements Serializable{
    String text;
    String selector;
    String server;
    Integer port;

    /**
     * Used to render the line onto the screen
     *
     * @param textView current (latest) TextView
     * @param context  context of the current (latest) activity
     */
    public abstract void render(final TextView textView, Context context);


    /**
     * Used to download/save a file
     *
     * @param context the context of the current activity
     */
    void download(final Context context) {
        //check for permission to save files in 6.0+
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    0);
        }

        //AlertDialog to be shown when method gets called
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle("Download");
        alertDialog.setMessage("Save as:");

        //the EditText where the user will input the name of the file
        final EditText input = new EditText(context);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(layoutParams);
        input.setText(text); //default file name
        alertDialog.setView(input);


        alertDialog.setPositiveButton("Save",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, int which) {
                        //file is always saved in the download directory atm
                        final File file = new File(
                                Environment.getExternalStoragePublicDirectory(Environment
                                        .DIRECTORY_DOWNLOADS)
                                        + "/" + input.getText().toString());

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    if (!file.createNewFile()) {
                                        //quit if file already exists
                                        Toast.makeText(context, "File already exists", Toast
                                                .LENGTH_LONG)
                                                .show();
                                        dialog.cancel();
                                    } else {
                                        //read the file
                                        Connection conn = new Connection(server, port);
                                        String in = conn.getText(selector);


                                        //write the file
                                        DataOutputStream os = new DataOutputStream(new
                                                FileOutputStream(file));
                                        os.writeBytes(in);
                                        os.flush();
                                        os.close();

                                        //TODO: need to add some code so the files get detected
                                        // by DownloadManager or something
                                    }

                                } catch (IOException e) {
                                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG)
                                            .show();
                                    dialog.cancel();
                                }
                            }
                        }).start();

                        Toast.makeText(context, "File saved", Toast.LENGTH_SHORT).show();
                    }
                });


        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        alertDialog.show();
    }
}
