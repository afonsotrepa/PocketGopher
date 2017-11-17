package com.gmail.afonsotrepa.pocketgopher.gopherclient;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.gmail.afonsotrepa.pocketgopher.History;
import com.gmail.afonsotrepa.pocketgopher.MainActivity;
import com.gmail.afonsotrepa.pocketgopher.gopherclient.Activity.HtmlActivity;
import com.gmail.afonsotrepa.pocketgopher.gopherclient.Activity.MenuActivity;
import com.gmail.afonsotrepa.pocketgopher.gopherclient.Activity.TextFileActivity;
import com.gmail.afonsotrepa.pocketgopher.gopherclient.Line.AudioLine;
import com.gmail.afonsotrepa.pocketgopher.gopherclient.Line.ImageLine;
import com.gmail.afonsotrepa.pocketgopher.gopherclient.Line.SearchLine;
import com.gmail.afonsotrepa.pocketgopher.gopherclient.Line.VideoLine;

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

        //simplify the url
        this.url = server +
                ((port == 70) ? "" : ":" + String.valueOf(port)) +
                ((path == null) ? "" : "/" + path);
    }


    /**
     * Opens an interface for the user to download the page
     *
     * @param context the context of the current activity
     */
    public void download(final Context context)
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
        final String fileName = input.getText().toString();


        alertDialog.setPositiveButton("Save",
                new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(final DialogInterface dialog, int which)
                    {
                        new Thread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                Handler handler = new Handler(Looper.getMainLooper());

                                //file is always saved in the download directory atm
                                File file = new File(
                                        Environment.getExternalStoragePublicDirectory(Environment
                                                .DIRECTORY_DOWNLOADS) +
                                                "/" + fileName
                                );

                                try
                                {
                                    Integer n = 0;
                                    while (file.exists())
                                    {
                                        n += 1;

                                        if (fileName.matches("(.*).(.*)"))
                                        {
                                            file = new File(
                                                    Environment.getExternalStoragePublicDirectory
                                                            (Environment
                                                                    .DIRECTORY_DOWNLOADS) +
                                                            "/" + fileName.substring(0, fileName
                                                            .indexOf
                                                                    ('.')) +
                                                            "(" + String.valueOf(n) + ")" +
                                                            fileName.substring(fileName.indexOf(
                                                                    '.'))
                                            );
                                        } else
                                        {
                                            file = new File(
                                                    Environment.getExternalStoragePublicDirectory
                                                            (Environment
                                                                    .DIRECTORY_DOWNLOADS) +
                                                            fileName +
                                                            "(" + String.valueOf(n) + ")"
                                            );
                                        }
                                    }

                                    final File f = file;
                                    f.createNewFile();

                                    try
                                    {
                                        Connection conn = new Connection(server, port);
                                        conn.getBinary(selector, f);
                                    }
                                    catch (final IOException e)
                                    {
                                        handler.post(new Runnable()
                                        {
                                            @Override
                                            public void run()
                                            {
                                                Toast.makeText(context,
                                                        e.getMessage(),
                                                        Toast.LENGTH_LONG
                                                ).show();
                                            }
                                        });
                                    }


                                    handler.post(new Runnable()
                                    {
                                        @Override
                                        public void run()
                                        {
                                            Toast.makeText(context,
                                                    "File saved saved as: " + f.getName(),
                                                    Toast.LENGTH_SHORT
                                            ).show();
                                        }
                                    });

                                    ///TODO: need to add some code so the files get detected
                                    /// by DownloadManager or something (not possible??)

                                }
                                catch (
                                        final IOException e)

                                {
                                    handler.post(new Runnable()
                                    {
                                        @Override
                                        public void run()
                                        {
                                            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG)
                                                    .show();
                                        }
                                    });
                                }
                            }
                        }).start();
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


    public void open(final Context context)
    {
        //add the page to history (done on another thread to avoid blocking this thread)
        final String url = this.url;
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                History.add(context, url);
            }
        });


        Intent intent;

        switch (type)
        {
            case '0':
                intent = new Intent(context, TextFileActivity.class);
                intent.putExtra("page", this);
                context.startActivity(intent);
                break;

            case '1':
                intent = new Intent(context, MenuActivity.class);
                intent.putExtra("page", this);
                context.startActivity(intent);
                break;

            case 'h':
                intent = new Intent(context, HtmlActivity.class);
                intent.putExtra("page", this);
                context.startActivity(intent);
                break;


            case 's':
                ((AudioLine) this).onLineClick(context);
                break;

            case 'I':
                ((ImageLine) this).onLineClick(context);
                break;

            case '7':
                ((SearchLine) this).onLineClick(context);
                break;

            case ';':
                ((VideoLine) this).onLineClick(context);
                break;


            case '9':
            default:
                this.download(context);
                break;

        }
    }
}
