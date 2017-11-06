package com.gmail.afonsotrepa.pocketgopher;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;

/**
 * Used to create or edit a bookmark
 */

public class EditBookmarkActivity extends AppCompatActivity {
    EditText editName;
    EditText editType;
    EditText editSelector;
    EditText editServer;
    EditText editPort;
    Integer id;

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
        Intent i= getIntent();

        if (i.getStringExtra("name") != null) {
            //"old" values
            name = i.getStringExtra("name");
            type = i.getCharExtra("type", '1');
            selector = i.getStringExtra("selector");
            server = i.getStringExtra("server");
            port = i.getIntExtra("port", 70);
            id = i.getIntExtra("id", 0);
        } else {
            //default values
            name = "";
            type = '1';
            selector = "";
            server = "";
            port = 70;
            id = 0;
        }


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
        editPort.setText(port.toString());
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
            //on click of button: remove the entry from the bookmarks if it exists
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
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG) .show();
                    finish();
                }

                return true;

            //on click of button: make/edit bookmark and save it to the file
            case R.id.saveButton:
                try {
                    List<Bookmark> bookmarks = Bookmark.read(getApplicationContext());

                    //if editing a bookmark that already exists
                    if (id !=0) {
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
