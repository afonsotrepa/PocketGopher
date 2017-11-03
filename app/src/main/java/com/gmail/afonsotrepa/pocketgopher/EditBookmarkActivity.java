package com.gmail.afonsotrepa.pocketgopher;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;

/**
 * Used to create or edit a bookmark
 */

public class EditBookmarkActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editbookmark);

        final String name;
        final Character type;
        final String selector;
        final String server;
        final Integer port;
        final Integer id;

        //load old values if editing (instead of making) a bookmark
        String m = getIntent().getStringExtra(MainActivity.EXTRA_MESSAGE);
        if (m != null && !m.equals("")) {
            String[] message = m.split("\t");

            //"old" values
            name = message[0];
            type = message[1].charAt(0);
            selector = message[2];
            server = message[3];
            port = Integer.parseInt(message[4]);
            id = Integer.parseInt(message[5]);
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
        final EditText editName = findViewById(R.id.editName);
        editName.setText(name);
        final EditText editType = findViewById(R.id.editType);
        editType.setText(type.toString());
        final EditText editSelector = findViewById(R.id.editSelector);
        editSelector.setText(selector);
        final EditText editServer = findViewById(R.id.editServer);
        editServer.setText(server);
        final EditText editPort = findViewById(R.id.editPort);
        editPort.setText(port.toString());


        //on click of "Save" button: make/edit bookmark and save it to the file
        final Button saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
            }
        });


        //on click of "Remove" button: remove the entry from the bookmarks if it exists
        final Button removeButton = findViewById(R.id.removeButton);
        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    List<Bookmark> bookmarks = Bookmark.read(getApplicationContext());


                    //remove the bookmark from bookmarks (if it exists)
                    for (Bookmark bookmark : bookmarks) {
                        Log.d("EBA", bookmark.id.toString());
                        Log.d("EBA", id.toString());
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
            }
        });
    }
}
