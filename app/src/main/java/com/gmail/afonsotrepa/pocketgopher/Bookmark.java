package com.gmail.afonsotrepa.pocketgopher;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.gmail.afonsotrepa.pocketgopher.gopherclient.Page;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */

public class Bookmark extends Page
{
    public String name;
    public String url;

    public Class activity; //the activity to call when opening the bookmarked page
    public Integer id; //a unique id that identifies the bookmark

    private static final Integer BOOKMARKS_FILE_KEY = R.string.booksmarks_file;

    private Bookmark(String name, String url, Integer id)
    {
        super(url);
        this.url = url;
        this.name = name;
        this.id = id;

        //determine which activity to call
        this.activity = this.getActivity();
    }

    public Bookmark(Context context, String name, String url)
    {
        super(url);
        this.url = url;
        this.name = name;

        //generate a new unique id
        String file = context.getResources().getString(BOOKMARKS_FILE_KEY);
        SharedPreferences sharedPref = context.getSharedPreferences(file, Context.MODE_PRIVATE);
        id = sharedPref.getInt("id", 0);

        //update the (id part in the) file
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("id", id + 1);
        editor.apply();

        //determine which activity to call
        this.activity = this.getActivity();
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
     * Save bookmarks to a SharedPreferences file
     */
    static void save(Context context, List<Bookmark> bookmarks)
    {
        String file = context.getResources().getString(BOOKMARKS_FILE_KEY);
        //open/create the file in private mode
        SharedPreferences sharedPref = context.getSharedPreferences(file, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        //transform the list into a StringBuilder
        StringBuilder csvbookmarks = new StringBuilder();
        for (Bookmark b : bookmarks)
        {
            csvbookmarks.append(b.name).append("\n");
            csvbookmarks.append(b.url).append("\n");
            csvbookmarks.append(b.id.toString()).append("\n");
            csvbookmarks.append("\u0000");
        }

        //write csvbookmarks to the editor
        editor.putString("bookmarks", csvbookmarks.toString());
        //apply the changes to the file
        editor.apply();
    }


    /**
     * Read the bookmarks from a  SharedPreferences file
     *
     * @return a list of all the bookmarks in the bookmarks file
     */
    static List<Bookmark> read(Context context) throws Exception
    {
        String file = context.getResources().getString(BOOKMARKS_FILE_KEY);
        //open/create the file in private mode
        SharedPreferences sharedPref = context.getSharedPreferences(file, Context.MODE_PRIVATE);


        List<Bookmark> bookmarks = new ArrayList<>();

        //read the bookmark(s) from the file
        String[] csvbookmarks = sharedPref.getString("bookmarks", "").split("\u0000");

        for (String b : csvbookmarks)
        {
            String[] bsplit = b.split("\n");
            if (bsplit.length > 1)
            {
                //parse the bookmark
                Bookmark bookmark = new Bookmark(
                        bsplit[0], //name
                        bsplit[1], //url
                        Integer.parseInt(bsplit[2])
                ); //id

                //add it to the list of bookmarks
                bookmarks.add(bookmark);
            }
        }

        return bookmarks;
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


        dialog.setPositiveButton("Save",
                new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(final DialogInterface dialog, int which)
                    {
                        try
                        {
                            //list of current bookmarks
                            List<Bookmark> bookmarks = Bookmark.read(context);

                            //if editing a bookmark that already exists
                            if (id != 0)
                            {
                                //remove the old bookmark
                                for (Bookmark b : bookmarks)
                                {
                                    if (b.id.equals(id))
                                    {
                                        bookmarks.remove(b);
                                    }
                                }
                            }
                            //create the new bookmark
                            Bookmark b = new Bookmark(
                                    context,
                                    editName.getText().toString(),
                                    editUrl.getText().toString()
                            );

                            //add it to the list
                            bookmarks.add(b);

                            //save the changes to the file
                            Bookmark.save(context, bookmarks);

                            //show "Bookmark saved" and exit this activity
                            Toast.makeText(context, "Bookmark saved", Toast.LENGTH_SHORT)
                                    .show();
                            dialog.cancel();
                        } catch (Exception e)
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
                            List<Bookmark> bookmarks = Bookmark.read(context);

                            //remove the bookmark from bookmarks (if it exists)
                            for (Bookmark bookmark : bookmarks)
                            {
                                if (bookmark.id.equals(id))
                                {
                                    bookmarks.remove(bookmark);
                                    break;
                                }
                            }

                            //save the edited bookmarks list
                            Bookmark.save(context, bookmarks);

                            dialog.cancel();
                        } catch (Exception e)
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
}
