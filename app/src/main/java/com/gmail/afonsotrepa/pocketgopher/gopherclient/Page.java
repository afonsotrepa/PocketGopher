package com.gmail.afonsotrepa.pocketgopher.gopherclient;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
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
 * A gopher page (extended Bookmark)
 */

public abstract class Page implements Serializable
{
    public String server;
    public Integer port;
    public String selector;
    public String url;

    public String line = null; //optional

    public Page(String server, Integer port, Character type, String selector, String line)
    {
        this.server = server;
        this.port = port;
        this.selector = selector;

        this.url = server +
                ((port == 70) ? "" : ":" + String.valueOf(port)) +
                ((selector.matches("")) ? "" : "/" + type.toString() + selector);

        this.line = line;
    }


    public Page(String server, Integer port, Character type, String selector)
    {
        this(server, port, type, selector, null);
    }


    public abstract void open(final Context context);

    public abstract void render(final TextView textView, Context context, String line);


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
                                        }
                                        else
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
                                }
                                catch (
                                        final IOException e)

                                {
                                    handler.post(new Runnable()
                                    {
                                        @Override
                                        public void run()
                                        {
                                            Toast.makeText(context, e.getMessage(), Toast
                                                    .LENGTH_LONG).show();
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


    @NonNull
    public static Page makePage(Character type, String selector, String server, Integer port,
                                String line
    )
    {
        switch (type)
        {
            case '0': //text file
                return new TextFilePage(
                        selector,
                        server,
                        port,
                        line
                );

            case '1': //menu/directory
                return new MenuPage(
                        selector,
                        server,
                        port,
                        line
                );

            case '7': //Search engine or CGI script
                return new SearchPage(
                        selector,
                        server,
                        port,
                        line
                );

            case 'i': //Informational text (not in the protocol but common)
                return new TextPage(selector, line);

            case 'g': //gif (temporary)
            case 'I': //Image
                return new ImagePage(
                        selector,
                        server,
                        port,
                        line
                );

            case 'h': //html
                return new HtmlPage(
                        selector,
                        server,
                        port,
                        line
                );

            case '4': //macintosh binhex file
            case '5': //binary archive
            case '6': //uuencoded file
            case '9': //binary file
            case 'd': //word-processing document
                return new BinPage(
                        selector,
                        server,
                        port,
                        line
                );

            case ';': //video file
                return new VideoPage(
                        selector,
                        server,
                        port,
                        line
                );

            case 's': //audio file
                return new AudioPage(
                        selector,
                        server,
                        port,
                        line
                );

            default:
                return new BinPage(
                        selector,
                        server,
                        port,
                        line
                );
        }
    }

    @NonNull
    public static Page makePage(Character type, String selector, String server, Integer port)
    {
        return makePage(type, selector, server, port, null);
    }


    public static Page makePage(String url, String line)
    {
        //remove "gopher://" from the beginning of the url if it's present there
        if (url.indexOf("gopher://") == 0)
        {
            url = url.replaceFirst("gopher://", "");
        }

        String host;
        String path;

        String server;
        Integer port;
        String selector;
        Character type;

        //get the host and the path
        if (url.matches("(.*)/(.*)") || url.matches("(.*)/1"))
        {
            host = url.substring(0, url.indexOf("/"));
            path = url.substring(url.indexOf("/") + 1);
        }
        else
        {
            host = url;
            path = null;
        }

        //get the server and the port (if it's explicit)
        if (host.contains(":"))
        {
            server = host.substring(0, host.indexOf(":"));
            port = Integer.parseInt(host.substring(host.indexOf(":") + 1));
        }
        else
        {
            server = host;
            port = 70; //default port
        }

        //get the type and selector
        if (path != null)
        {
            type = path.charAt(0);
            selector = path.substring(1);
        }
        else
        {
            type = '1';
            selector = "";
        }

        return Page.makePage(type, selector, server, port, line);
    }


    public static Page makePage(String url)
    {
        return Page.makePage(url, null);
    }
}
