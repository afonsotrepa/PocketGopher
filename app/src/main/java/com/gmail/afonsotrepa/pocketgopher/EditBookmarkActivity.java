package com.gmail.afonsotrepa.pocketgopher;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;
import java.util.Locale;

/**
 * Used to create or edit a bookmark
 */

public class EditBookmarkActivity extends AppCompatActivity {
    EditText editName;
    EditText editType;
    EditText editSelector;
    EditText editServer;
    EditText editPort;
    Integer id; //can't be "method private"

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editbookmark);

        String name;
        Character type;
        String selector;
        String server;
        Integer port;

        //load old values if editing (instead of making) a bookmark
        Intent i = getIntent();
        Bookmark b = (Bookmark) i.getSerializableExtra("bookmark");
        name = b.name;
        type = b.type;
        selector = b.selector;
        server = b.server;
        port = b.port;
        id = b.id;


        //setup the text boxes to be used
        editName = findViewById(R.id.editName);
        editName.setText(name);
        editType = findViewById(R.id.editType);
        editType.setText(type.toString());
        editSelector = findViewById(R.id.editSelector);
        editSelector.setText(selector);
        editServer = findViewById(R.id.editServer);
        editServer.setText(server);
        editPort = findViewById(R.id.editPort);
        editPort.setText(String.format(Locale.getDefault(), "%d", port));
    }


    //setup the menu/title bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.client_editbookmark, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //on button click: remove the entry from the bookmarks if it exists
            case R.id.removeButton:
                try {
                    List<Bookmark> bookmarks = Bookmark.read(getApplicationContext());

                    //remove the bookmark from bookmarks (if it exists)
                    for (Bookmark bookmark : bookmarks) {
                        if (bookmark.id.equals(id)) {
                            bookmarks.remove(bookmark);
                            break;
                        }
                    }

                    //save the edited bookmarks list
                    Bookmark.save(getApplicationContext(), bookmarks);

                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG)
                            .show();
                    finish();
                }

                return true;

            //on button click: make/edit bookmark and save it to the file
            case R.id.saveButton:
                try {
                    //list of current bookmarks
                    List<Bookmark> bookmarks = Bookmark.read(getApplicationContext());

                    //if editing a bookmark that already exists
                    if (id != 0) {
                        //remove the old bookmark
                        for (Bookmark b : bookmarks) {
                            if (b.id.equals(id)) {
                                bookmarks.remove(b);
                            }
                        }
                    }
                    //create the new bookmark
                    Bookmark b = new Bookmark(
                            getApplicationContext(),
                            editName.getText().toString(),
                            editType.getText().charAt(0),
                            editSelector.getText().toString(),
                            editServer.getText().toString(),
                            Integer.parseInt(editPort.getText().toString())
                    );

                    //add it to the list
                    bookmarks.add(b);

                    //save the changes to the file
                    Bookmark.save(getApplicationContext(), bookmarks);

                    //show "Bookmark saved" and exit this activity
                    Toast.makeText(getApplicationContext(), "Bookmark saved", Toast.LENGTH_SHORT)
                            .show();
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG)
                            .show();
                    finish();
                }

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
