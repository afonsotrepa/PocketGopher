package com.gmail.afonsotrepa.pocketgopher;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.gmail.afonsotrepa.pocketgopher.gopherclient.Page;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */

public class Bookmark
{
    public Page page;
    public String name;
    public String url;

    public Integer id; //a unique id that identifies the bookmark

    private static final String BOOKMARKS_FILE = "bookmarks";

    private Bookmark(String name, String url, Integer id)
    {
        this.page = Page.makePage(url);
        this.url = url;
        this.name = name;
        this.id = id;
    }

    public Bookmark(Context context, String name, String url)
    {
        this.page = Page.makePage(url);
        this.url = url;
        this.name = name;

        //generate a new unique id
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences
                (context);
        id = sharedPreferences.getInt("id", 0);

        //update the file
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("id", id + 1);
        editor.apply();
    }

    /**
     * A simple wrapper for backwards comparability. Avoid!!
     *
     * @deprecated
     */
    public Bookmark(Context context, String name, Character type, String selector, String server,
                    Integer port
    )
            throws Exception
    {
        this(context, name, server + ":" + String.valueOf(port) + "/" + type.toString() + selector);
    }


    /**
     * Add a bookmark to the file
     */
    void add(Context context)
    {
        try
        {
            FileOutputStream outputStream = context.openFileOutput(BOOKMARKS_FILE,
                    Context.MODE_APPEND
            );

            outputStream.write((
                    this.name + "\t" +
                            this.url + "\t" +
                            this.id.toString() + "\n"
            ).getBytes());
            outputStream.close();
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }


    /**
     * Read the bookmarks from the bookmarks file
     *
     * @return a list of all the bookmarks in the bookmarks file
     */
    static List<Bookmark> read(Context context)
    {
        try
        {
            FileInputStream inputStream = context.openFileInput(BOOKMARKS_FILE);
            BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(inputStream));

            //read the bookmark(s) from the file
            List<Bookmark> bookmarks = new ArrayList<>();
            String b;
            while ((b = bufferedreader.readLine()) != null)
            {
                String[] bsplit = b.split("\t");
                if (bsplit.length > 1)
                {
                    //parse the bookmark
                    Bookmark bookmark = new Bookmark(
                            bsplit[0], //name
                            bsplit[1], //url
                            Integer.parseInt(bsplit[2]) //id
                    );

                    //add it to the list of bookmarks
                    bookmarks.add(bookmark);
                }
            }

            return bookmarks;
        }
        catch (IOException e)
        {
            return null;
        }
    }

    public void remove(Context context)
    {
        try
        {
            List<Bookmark> bookmarks = Bookmark.read(context);

            //clear the file
            context.openFileOutput(BOOKMARKS_FILE, Context.MODE_PRIVATE ).write("".getBytes());

            for (Bookmark b : bookmarks)
            {
                if (b.id != this.id)
                {
                    b.add(context);
                }
            }

        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }


    public void editBookmark(final Context context)
    {
        //AlertDialog to be shown when method gets called
        final AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle("Edit Bookmark");


        //setup the layout
        LinearLayout layout = new LinearLayout(context);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        layout.setLayoutParams(layoutParams);
        layout.setOrientation(LinearLayout.VERTICAL);

        //setup the EditText's and add them to the layout
        final EditText editName = new EditText(context);
        final EditText editUrl = new EditText(context);
        editName.setHint("Name");
        editUrl.setHint("Url");
        editName.setText(this.name);
        editUrl.setText(this.url);
        editName.setTextAppearance(context, MainActivity.font);
        editUrl.setTextAppearance(context, MainActivity.font);
        layout.addView(editName);
        layout.addView(editUrl);

        //apply the layout
        dialog.setView(layout);

        final Bookmark bookmark = this;

        dialog.setPositiveButton("Save",
                new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(final DialogInterface dialog, int which)
                    {
                        try
                        {
                            bookmark.name = editName.getText().toString();
                            bookmark.url = editUrl.getText().toString();

                            bookmark.add(context);

                            Toast.makeText(context, "Bookmark saved", Toast.LENGTH_SHORT)
                                    .show();
                            dialog.cancel();
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG)
                                    .show();
                            dialog.cancel();
                        }

                        //refresh MainActivity (when called by it)
                        if (context.getClass() == MainActivity.class)
                        {
                            ((Activity) context).recreate();
                        }
                    }
                }
        );

        dialog.setNegativeButton("Remove",
                new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        try
                        {
                            bookmark.remove(context);

                            dialog.cancel();
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();

                            dialog.cancel();
                        }

                        //refresh MainActivity (when called by it)
                        if (context.getClass() == MainActivity.class)
                        {
                            ((Activity) context).recreate();
                        }
                    }
                }
        );


        dialog.show();
    }


    static public Bookmark makeBookmark(Context context)
    {
        Bookmark bookmark = new Bookmark(context, "", "");
        bookmark.editBookmark(context);
        return bookmark;
    }

    public void open(Context context)
    {
        this.page.open(context);
    }
}
