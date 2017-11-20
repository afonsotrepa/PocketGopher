package com.gmail.afonsotrepa.pocketgopher;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Keeps a record of the pages visited
 */

public abstract class History
{
    private static final String HISTORY_FILE = "history_file";


    /**
     * save/add a page/url to the history
     *
     * @param context
     * @param url
     */
    static public void add(Context context, String url)
    {
        try
        {
            FileOutputStream outputStream = context.openFileOutput(HISTORY_FILE,
                    Context.MODE_APPEND
            );

            outputStream.write((url + "\n").getBytes());
            outputStream.close();
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * reads the history from a file
     *
     * @return the visited pages/url's in reverse (latest entry is at index 0)
     */
    static public List<String> read(Context context)
    {
        try
        {

            FileInputStream inputStream = context.openFileInput(HISTORY_FILE);
            BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(inputStream));


            //read the page(s) from the file
            List<String> visited = new ArrayList<>();
            String received;
            while ((received = bufferedreader.readLine()) != null)
            {
                visited.add(0, received);
            }

            return visited;
        }
        catch (IOException e)
        {
            return null;
        }
    }

    /**
     * Clears all of the history
     *
     * @param context
     */
    static public void clear(final Context context)
    {
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle("Are you sure you want to clear the history?");

        dialog.setPositiveButton("Clear",
                new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        File file = new File(HISTORY_FILE);
                        file.delete();

                        ((Activity) context).recreate();
                    }
                }
        );

        dialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.cancel();
                    }
                }
        );

        dialog.show();
    }
}
