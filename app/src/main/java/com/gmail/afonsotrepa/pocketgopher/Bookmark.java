package com.gmail.afonsotrepa.pocketgopher;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.gmail.afonsotrepa.pocketgopher.gopherclient.ImageActivity;
import com.gmail.afonsotrepa.pocketgopher.gopherclient.MenuActivity;
import com.gmail.afonsotrepa.pocketgopher.gopherclient.SearchActivity;
import com.gmail.afonsotrepa.pocketgopher.gopherclient.TextFileActivity;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */

public class Bookmark {
    public String name;
    public Character type;
    public String selector;
    public String server;
    public Integer port;

    public Class activity; //the activity to call when opening the bookmarked page
    public Integer id; //a unique id that identifies the bookmark

    private static final Integer  BOOKMARKS_FILE_KEY = R.string.booksmarks_file_key;

    private Bookmark(String name, Character type, String selector, String server,
                     Integer port, Integer id) throws Exception {
        this.name = name;
        this.type = type;
        this.selector = selector;
        this.server = server;
        this.port = port;
        this.id = id;

        //determine which activity to call
        switch (type) {
            case '0':
                this.activity = TextFileActivity.class;
                break;

            case '1':
                this.activity = MenuActivity.class;
                break;

            case '7':
                this.activity = SearchActivity.class;
                break;

            case 'g': //gif
            case 'I':
                this.activity = ImageActivity.class;
                break;

            default:
                throw new Exception("Invalid type");
        }
    }

    Bookmark(Context context, String name, Character type, String selector, String server,
             Integer port) throws Exception {
        //generate a new unique id
        String file = context.getResources().getString(BOOKMARKS_FILE_KEY);
        SharedPreferences sharedPref = context.getSharedPreferences(file, Context.MODE_PRIVATE);
        id = sharedPref.getInt("id", 0);

        //update the (id part in the) file
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("id", id+1);
        editor.apply();

        //create the bookmark itself (can't use "this(...)" because it's not the 1st statement)
        this.name = name;
        this.type = type;
        this.selector = selector;
        this.server = server;
        this.port = port;

        //determine which activity to call
        switch (type) {
            case '0':
                this.activity = TextFileActivity.class;
                break;

            case '1':
                this.activity = MenuActivity.class;
                break;

            case '7':
                this.activity = SearchActivity.class;
                break;

            case 'g': //gif
            case 'I':
                this.activity = ImageActivity.class;
                break;

            default:
                throw new Exception("Invalid type");
        }
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
            csvbookmarks.append(b.type).append("\n");
            csvbookmarks.append(b.selector).append("\n");
            csvbookmarks.append(b.server).append("\n");
            csvbookmarks.append(b.port.toString()).append("\n");
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
                        bsplit[1].charAt(0), //type
                        bsplit[2], //selector
                        bsplit[3], //server
                        Integer.parseInt(bsplit[4]), //port
                        Integer.parseInt(bsplit[5])); //id

                //add it to the list of bookmarks
                bookmarks.add(bookmark);
            }
        }

        return bookmarks;
    }
}
