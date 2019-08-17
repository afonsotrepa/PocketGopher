package com.gmail.afonsotrepa.pocketgopher;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;

import com.gmail.afonsotrepa.pocketgopher.gopherclient.Page;

import java.util.List;


public class MainActivity extends AppCompatActivity
{
    private Menu menu;
    public static int font = R.style.monospace;
    public static int fontSize = 14; // 14sp is the default textview text size

    private static final String FONT_SIZE_SETTING = "font_size";
    private static final String MONOSPACE_FONT_SETTING = "monospace_font";
    private static final String FIRST_RUN = "first_run";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (sharedPreferences.getInt(MONOSPACE_FONT_SETTING, 1) == 1)
        {
            font = R.style.monospace;
        }
        else
        {
            font = R.style.serif;
        }

        fontSize = sharedPreferences.getInt(FONT_SIZE_SETTING, fontSize);

        if (sharedPreferences.getBoolean(FIRST_RUN, true))
        {
            editor.putBoolean(FIRST_RUN, false);
            editor.apply();

            new Bookmark(this, "Search Veronica-2", "gopher.floodgap.com/1/v2")
                    .add(this);
            new Bookmark(this, "SDF", "sdf.org").add(this);
            new Bookmark(this, "Khzae", "khzae.net").add(this);
        }

        Intent intent = getIntent();
        if (Intent.ACTION_VIEW.equals(intent.getAction()))
        {
            Uri uri = intent.getData();
            if (uri != null) {
                Page page = Page.makePage(uri.toString());
                page.open(this);
            }
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();

        //configure the add bookmark button
        FloatingActionButton addBookmarkFloatingButton = findViewById(R.id
                .addBookmarkFloatingButton);
        addBookmarkFloatingButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //make the new bookmark
                Bookmark.makeBookmark(MainActivity.this);
            }
        });


        List<Bookmark> bookmarks = Bookmark.read(MainActivity.this);

        if (bookmarks == null)
        {
            return;
        }

        //get the ListView to display
        ListView listView = findViewById(R.id.listView);

        //make an array holding the bookmarks (used to make the adapter)
        Bookmark[] bookmarksarray = new Bookmark[bookmarks.size()];
        bookmarksarray = bookmarks.toArray(bookmarksarray);

        //make the adapter
        BookmarkAdapter adapter = new BookmarkAdapter(
                this,
                R.layout.activity_listview,
                bookmarksarray
        );

        //apply it to listView
        listView.setAdapter(adapter);

        //make the items clickable (open the page/bookmark when clicked)
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                //the selected bookmark
                final Bookmark bookmark = (Bookmark) parent.getItemAtPosition(position);

                new Thread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        bookmark.open(MainActivity.this);
                    }
                }).start();
            }
        });

        //make the items "long clickable" (edit the bookmark when long clicked)
        listView.setLongClickable(true);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
        {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long
                    id
            )
            {
                ((Bookmark) parent.getItemAtPosition(position)).editBookmark(MainActivity.this);

                return true;
            }
        });
    }


    //setup the menu/title bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.client_main, menu);

        this.menu = menu;
        menu.findItem(R.id.monospace_font).setChecked(true);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if (sharedPreferences.getInt(MONOSPACE_FONT_SETTING, 1) == 1)
        {
            menu.findItem(R.id.monospace_font).setChecked(true);
        }
        else
        {
            menu.findItem(R.id.monospace_font).setChecked(false);
        }
        editor.apply();

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // get the preferences editor
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        // get a dialog builder
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        switch (item.getItemId())
        {
            case R.id.monospace_font:
                if (font == R.style.serif)
                {
                    font = R.style.monospace;
                    menu.findItem(R.id.monospace_font).setChecked(true);
                    editor.putInt(MONOSPACE_FONT_SETTING, 1);
                }
                else
                {
                    font = R.style.serif;
                    menu.findItem(R.id.monospace_font).setChecked(false);
                    editor.putInt(MONOSPACE_FONT_SETTING, 0);
                }
                editor.apply();

                //restart the activity
                this.recreate();

                return true;

            case R.id.font_size:
                //create the font size number picker
                final NumberPicker numberPicker = new NumberPicker(this);
                numberPicker.setMinValue(1);
                numberPicker.setMaxValue(64);
                numberPicker.setValue(sharedPreferences.getInt(FONT_SIZE_SETTING, 12));

                //setup the dialog with the number picker
                alertDialog.setMessage("Font Size");
                alertDialog.setView(numberPicker);

                //setup the ok button callback to save the number picker value into shared preferences
                alertDialog.setPositiveButton("OK",
                        new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(final DialogInterface dialog, int which)
                            {
                                // store the new font size setting into shared preferences
                                editor.putInt(FONT_SIZE_SETTING, numberPicker.getValue());
                                editor.apply();
                            }
                        }
                );

                // show the dialog
                alertDialog.show();

                return true;

            case R.id.link:
                //create the dialog to be shown when the button gets clicked
                alertDialog.setMessage("URL:");

                //setup the EditText where the user will input url to the page
                final EditText input = new EditText(this);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT
                );
                input.setLayoutParams(layoutParams);
                input.setInputType(InputType.TYPE_TEXT_VARIATION_URI);
                alertDialog.setView(input);

                alertDialog.setPositiveButton("Go",
                        new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(final DialogInterface dialog, int which)
                            {
                                //setup the page
                                Page page = Page.makePage(input.getText().toString());

                                page.open(MainActivity.this);
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

                return true;

            case R.id.history:
                Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
                startActivity(intent);

                return true;


            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
