package com.gmail.afonsotrepa.pocketgopher;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.gmail.afonsotrepa.pocketgopher.gopherclient.GopherPage;
import com.gmail.afonsotrepa.pocketgopher.gopherclient.HtmlActivity;
import com.gmail.afonsotrepa.pocketgopher.gopherclient.ImageActivity;
import com.gmail.afonsotrepa.pocketgopher.gopherclient.MenuActivity;
import com.gmail.afonsotrepa.pocketgopher.gopherclient.SearchActivity;
import com.gmail.afonsotrepa.pocketgopher.gopherclient.TextFileActivity;
import com.gmail.afonsotrepa.pocketgopher.gopherclient.VideoActivity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */

public class Bookmark extends GopherPage {
    public String name;
    public String url;

    public Class activity; //the activity to call when opening the bookmarked page
    public Integer id; //a unique id that identifies the bookmark

    private static final Integer  BOOKMARKS_FILE_KEY = R.string.booksmarks_file;

    private Bookmark(String name, String url, Integer id) {
        super(url);
        this.url = url;
        this.name = name;
        this.id = id;

        //determine which activity to call
        this.activity = GopherPage.activityToCall(type);
    }

    public Bookmark(Context context, String name, String url) {
        super(url);
        this.url = url;
        this.name = name;

        //generate a new unique id
        String file = context.getResources().getString(BOOKMARKS_FILE_KEY);
        SharedPreferences sharedPref = context.getSharedPreferences(file, Context.MODE_PRIVATE);
        id = sharedPref.getInt("id", 0);

        //update the (id part in the) file
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("id", id+1);
        editor.apply();

        //determine which activity to call
        this.activity = GopherPage.activityToCall(type);
    }

    /**
     * A simple wrapper for backwards comparability. Avoid!!
     * @deprecated
     */
    public Bookmark(Context context, String name, Character type, String selector, String server, Integer port)
        throws Exception {
        this(context, name, server+":"+String.valueOf(port)+"/"+type.toString()+selector);
    }


    /**
     * Save bookmarks to a SharedPreferences file
     */
    static void save(Context context, List<Bookmark> bookmarks) {
        String file = context.getResources().getString(BOOKMARKS_FILE_KEY);
        //open/create the file in private mode
        SharedPreferences sharedPref = context.getSharedPreferences(file, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        //transform the list into a StringBuilder
        StringBuilder csvbookmarks = new StringBuilder();
        for (Bookmark b : bookmarks) {
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
     * @return a list of all the bookmarks in the bookmarks file
     */
    static List<Bookmark> read(Context context) throws Exception {
        String file = context.getResources().getString(BOOKMARKS_FILE_KEY);
        //open/create the file in private mode
        SharedPreferences sharedPref = context.getSharedPreferences(file, Context.MODE_PRIVATE);


        List<Bookmark> bookmarks = new ArrayList<>();

        //read the bookmark(s) from the file
        String[] csvbookmarks = sharedPref.getString("bookmarks", "").split("\u0000");

        for (String b : csvbookmarks) {
            String[] bsplit = b.split("\n");
            if (bsplit.length > 1) {
                //parse the bookmark
                Bookmark bookmark = new Bookmark(
                        bsplit[0], //name
                        bsplit[1], //url
                        Integer.parseInt(bsplit[2])); //id

                //add it to the list of bookmarks
                bookmarks.add(bookmark);
            }
        }

        return bookmarks;
    }
}
