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
import android.widget.Toast;

import com.gmail.afonsotrepa.pocketgopher.MainActivity;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

/**
 * A gopher page (extended by Line and Bookmark
 */

public class Page implements Serializable
{
    public String server;
    public Integer port;
    public Character type;
    public String selector;
    public String url;

    public Class activity;

    public Page(String server, Integer port, Character type, String selector)
    {
        this.server = server;
        this.port = port;
        this.type = type;
        this.selector = selector;

        this.url = server +
                ((port == 70) ? "" : ":" + String.valueOf(port)) +
                ((selector.matches("")) ? "" : "/" + type.toString() + selector);
    }

    public Page(String url)
    {
        //remove "gopher://" from the beginning of the url if it's present there
        if (url.indexOf("gopher://") == 0)
        {
            url = url.replaceFirst("gopher://", "");
        }

        String host;
        String path;

        //get the host and the path
        if (url.matches("(.*)/(.*)") || url.matches("(.*)/1"))
        {
            host = url.substring(0, url.indexOf("/"));
            path = url.substring(url.indexOf("/") + 1);
        } else
        {
            host = url;
            path = null;
        }

        //get the server and the port (if it's explicit)
        if (host.contains(":"))
        {
            this.server = host.substring(0, host.indexOf(":"));
            this.port = Integer.parseInt(host.substring(host.indexOf(":") + 1));
        } else
        {
            this.server = host;
            this.port = 70; //default port
        }

        //get the type and selector
        if (path != null)
        {
            this.type = path.charAt(0);
            this.selector = path.substring(1);
        } else
        {
            this.type = '1';
            this.selector = "";
        }

        this.activity = this.getActivity();

        //simplify the url
        this.url = server +
                ((port == 70) ? "" : ":" + String.valueOf(port)) +
                ((path == null) ? "" : "/" + path);
    }


    /**
     * @return the activity to be called
     */
    protected Class getActivity()
    {
        switch (this.type)
        {
            case '0':
                return TextFileActivity.class;

            case '1':
                return MenuActivity.class;

            case 'h':
                return HtmlActivity.class;

            default:
                throw new RuntimeException("Invalid type");
        }
    }


    /**
     * Opens an interface for the user to download the page
     *
     * @param context the context of the current activity
     */
    void download(final Context context)
    {
        //check for permission to save files in 6.0+
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions((Activity) context,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    0
            );
        }

        //AlertDialog to be shown when method gets called
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle("Save as:");

        //the EditText where the user will input the name of the file
        final EditText input = new EditText(context);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        input.setLayoutParams(layoutParams);
        input.setText(selector.substring(selector.lastIndexOf("/") + 1)); //default file name
        input.setTextAppearance(context, MainActivity.font);
        alertDialog.setView(input);


        alertDialog.setPositiveButton("Save",
                new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(final DialogInterface dialog, int which)
                    {
                        //file is always saved in the download directory atm
                        final File file = new File(
                                Environment.getExternalStoragePublicDirectory(Environment
                                        .DIRECTORY_DOWNLOADS)
                                        + "/" + input.getText().toString());

                        try
                        {
                            if (file.exists())
                            {
                                Toast.makeText(context, "File already exists", Toast.LENGTH_LONG)
                                        .show();
                            } else
                            {
                                file.createNewFile();

                                new Thread(new Runnable()
                                {
                                    @Override
                                    public void run()
                                    {
                                        try
                                        {
                                            Connection conn = new Connection(server, port);
                                            conn.getBinary(selector, file);

                                        } catch (final IOException e)
                                        {
                                            Toast.makeText(context, e.getMessage(), Toast
                                                    .LENGTH_LONG).show();
                                        }
                                    }
                                });

                                Toast.makeText(context, "File saved", Toast.LENGTH_SHORT).show();

                                ///TODO: need to add some code so the files get detected
                                /// by DownloadManager or something (not possible??)
                            }

                        } catch (final IOException e)
                        {
                            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                }
        );


        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener()

                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.cancel();
                    }
                }
        );

        alertDialog.show();
    }
}
