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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gmail.afonsotrepa.pocketgopher.MainActivity;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

/**
 *
 */

public abstract class Line extends Page implements Serializable {
    public String text;

    Line(String text, String server, Integer port, Character type, String selector) {
        super(server, port, type, selector);
        this.text = text;
    }


    /**
     * Used to render the line onto the screen
     *
     * @param textView current (latest) TextView
     * @param context  context of the current (latest) activity
     */
    public abstract void render(final TextView textView, Context context);


    /**
     * Opens an interface for the user to download the line
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
        alertDialog.setTitle("Save as:");

        //the EditText where the user will input the name of the file
        final EditText input = new EditText(context);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(layoutParams);
        input.setText(selector.substring(selector.lastIndexOf("/") + 1)); //default file name
        input.setTextAppearance(context, MainActivity.font);
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

                        try {
                            if (file.exists()) {
                                Toast.makeText(context, "File already exists", Toast.LENGTH_LONG)
                                        .show();
                            } else {
                                file.createNewFile();

                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            Connection conn = new Connection(server, port);
                                            conn.getBinary(selector, file);

                                        } catch (final IOException e) {
                                            Toast.makeText(context, e.getMessage(), Toast
                                                    .LENGTH_LONG).show();
                                        }
                                    }
                                });

                                Toast.makeText(context, "File saved", Toast.LENGTH_SHORT).show();

                                ///TODO: need to add some code so the files get detected
                                /// by DownloadManager or something (not possible??)
                            }

                        } catch (final IOException e) {
                            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                }
        );


        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener()

                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }
        );

        alertDialog.show();
    }

    public static Line makeLine(Character type, String text, String selector, String
            server, Integer port) {
        switch (type) {
            case '0': //text file
                return new TextFileLine(
                        text, //remove the type tag
                        selector,
                        server,
                        port);

            case '1': //menu/directory
                return new MenuLine(
                        text, //remove the type tag
                        selector,
                        server,
                        port);

            case '7': //Search engine or CGI script
                return new SearchLine(
                        text, //remove the type tag
                        selector,
                        server,
                        port);

            case 'i': //Informational text (not in the protocol but common)
                return new TextLine(text, selector);

            case 'g': //gif (temporary)
            case 'I': //Image
                return new ImageLine(
                        text, //remove the type tag
                        selector,
                        server,
                        port);

            case 'h': //html
                return new HtmlLine(
                        text, //remove the type tag
                        selector,
                        server,
                        port);

            case '4': //macintosh binhex file
            case '5': //binary archive
            case '6': //uuencoded file
            case '9': //binary file
            case 'd': //word-processing document
                return new BinLine(
                        text, //remove the type tag
                        selector,
                        server,
                        port);

            case ';': //video file
                return new VideoLine(
                        text, //remove the type tag
                        selector,
                        server,
                        port);

            case 's': //audio file
                return new AudioLine(
                        text, //remove the type tag
                        selector,
                        server,
                        port);

            default:
                return new BinLine(
                        text, //remove the type tag
                        selector,
                        server,
                        port);
        }
    }


    public static Class getCLass(Character type) {
        //determine which activity to call
        switch (type) {
            case '0':
                return TextFileActivity.class;

            case '1':
                return MenuActivity.class;

            case 'h': //html
                return HtmlActivity.class;

            default:
                throw new RuntimeException("Invalid type");
        }
    }
}
