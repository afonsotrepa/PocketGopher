package com.gmail.afonsotrepa.pocketgopher;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.util.Log;

import com.gmail.afonsotrepa.pocketgopher.gopherclient.Page;

import java.util.ArrayList;
import java.util.List;

/**
 * Keeps a record of the pages visited
 */

public abstract class History
{
    private static final Integer HISTORY_FILE = R.string.history_file;


    /**
     * save/add a page/url to the history
     * @param context
     * @param url
     */
    static public void add(Context context, String url)
    {
        //open/create the file in private mode and make the editor
        String file = context.getResources().getString(HISTORY_FILE);
        SharedPreferences sharedPref = context.getSharedPreferences(file, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        //append the url to the end of the list of url's in the file
        editor.putString("history",
                sharedPref.getString("history", "") + url + "\u0000");

        //apply the changes to the file
        editor.apply();
    }

    /**
     * reads the history from a file
     * @return the visited pages/url's in reverse (latest entry is at index 0)
     * TODO: buffer the read for memory consumption reasons?
     */
    static public List<String> read(Context context)
    {
        //open/create the file in private mode
        String file = context.getResources().getString(HISTORY_FILE);
        SharedPreferences sharedPref = context.getSharedPreferences(file, Context.MODE_PRIVATE);


        //read the page(s) from the file
        String[] csvvisited = sharedPref.getString("history", "").split("\u0000");

        List<String> visited = new ArrayList<>();
        for (String url : csvvisited)
        {
            visited.add(0, url);
        }

        return visited;
    }

    /**
     * Clears all of the history
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
                        //open/create the file in private mode and make the editor
                        String file = context.getResources().getString(HISTORY_FILE);
                        SharedPreferences sharedPref = context.getSharedPreferences(file, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();

                        //clear the entry
                        editor.putString("history", "");

                        //apply the changes to the file
                        editor.apply();

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
