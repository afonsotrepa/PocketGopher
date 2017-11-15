////TODO: fix all the repeated code
package com.gmail.afonsotrepa.pocketgopher;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.gmail.afonsotrepa.pocketgopher.gopherclient.Line;
import com.gmail.afonsotrepa.pocketgopher.gopherclient.Page;

import java.util.List;


public class MainActivity extends AppCompatActivity {
    private Menu menu;
    public static int font = R.style.monospace;

    private static final Integer SETTINGS_FILE = R.string.settings_file;
    private static final String MONOSPACE_FONT_SETTING = "monospace_font";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String file = this.getResources().getString(SETTINGS_FILE);
        //open/create the file in private mode
        SharedPreferences sharedPref = this.getSharedPreferences(file, Context.MODE_PRIVATE);
        if (sharedPref.getInt(MONOSPACE_FONT_SETTING, 1) == 1) {
            font = R.style.monospace;
        } else {
            font = R.style.serif;
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        List<Bookmark> bookmarks;

        try {
            bookmarks = Bookmark.read(this);
        } catch (Exception e) {
            e.printStackTrace();
            //display the error and return
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
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
                bookmarksarray);

        //apply it to listView
        listView.setAdapter(adapter);

        //make the items clickable (open the page/bookmark when clicked)
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //the selected bookmark
                Bookmark bookmark = (Bookmark) parent.getItemAtPosition(position);

                //TODO: make it so it can also open items without an activity
                //open the bookmarked page
                final Intent intent = new Intent(MainActivity.this, bookmark.activity);
                Line line = Line.makeLine(bookmark.type, bookmark.name,
                        bookmark.selector, bookmark.server, bookmark.port);

                intent.putExtra("line", line);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(intent);
                    }
                }).start();
            }
        });

        //make the items "long clickable" (edit the bookmark when long clicked)
        listView.setLongClickable(true);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long
                    id) {
                ((Bookmark) parent.getItemAtPosition(position)).editBookmark(MainActivity.this);

                return true;
            }
        });


        //configure the add bookmark button
        FloatingActionButton addBookmarkFloatingButton = findViewById(R.id
                .addBookmarkFloatingButton);
        addBookmarkFloatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //make the new bookmark
                Bookmark.makeBookmark(MainActivity.this);
            }
        });
    }


    //setup the menu/title bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.client_main, menu);

        this.menu = menu;
        menu.findItem(R.id.monospace_font).setChecked(true);

        String file = this.getResources().getString(SETTINGS_FILE);
        //open/create the file in private mode
        SharedPreferences sharedPref = this.getSharedPreferences(file, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        if (sharedPref.getInt(MONOSPACE_FONT_SETTING, 1) == 1) {
            menu.findItem(R.id.monospace_font).setChecked(true);
        } else {
            menu.findItem(R.id.monospace_font).setChecked(false);
        }
        editor.apply();

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.monospace_font:
                String file = this.getResources().getString(SETTINGS_FILE);
                //open/create the file in private mode
                SharedPreferences sharedPref = this.getSharedPreferences(file, Context
                        .MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();

                if (font == R.style.serif) {
                    font = R.style.monospace;
                    menu.findItem(R.id.monospace_font).setChecked(true);
                    editor.putInt(MONOSPACE_FONT_SETTING, 1);
                } else {
                    font = R.style.serif;
                    menu.findItem(R.id.monospace_font).setChecked(false);
                    editor.putInt(MONOSPACE_FONT_SETTING, 0);
                }
                editor.apply();

                //restart the activity
                this.recreate();

                return true;

            case R.id.link:
                //AlertDialog to be shown when the button gets clicked
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
                alertDialog.setMessage("URL:");
                //the EditText where the user will input the name of the file
                final EditText input = new EditText(this);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                input.setLayoutParams(layoutParams);
                alertDialog.setView(input);


                alertDialog.setPositiveButton("Save",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface dialog, int which) {
                                Page page = new Page(input.getText().toString());
                                final Intent intent = new Intent(MainActivity.this, page.activity);
                                Line line = Line.makeLine(page.type, "", page
                                        .selector, page.server, page.port);

                                intent.putExtra("line", line);

                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        startActivity(intent);
                                    }
                                }).start();
                            }
                        });


                alertDialog.setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                alertDialog.show();

            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
