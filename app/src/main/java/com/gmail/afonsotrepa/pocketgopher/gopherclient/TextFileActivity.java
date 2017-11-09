package com.gmail.afonsotrepa.pocketgopher.gopherclient;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.gmail.afonsotrepa.pocketgopher.Bookmark;
import com.gmail.afonsotrepa.pocketgopher.EditBookmarkActivity;
import com.gmail.afonsotrepa.pocketgopher.R;

import java.io.IOException;


/**
 *
 */

public class TextFileActivity extends AppCompatActivity {
    TextFileGopherLine l;
    String text;
    String selector;
    String server;
    Integer port;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu); //same layout as MenuAcitivity

        //widget to write to
        final TextView textView = (TextView) findViewById(R.id.textView);

        //start a new thread to do network stuff
        new Thread(new Runnable() {
            @Override
            public void run() {
                //handler to the main thread
                final Handler handler = new Handler(Looper.getMainLooper());

                //intent stuff
                Intent i = getIntent();
                l= (TextFileGopherLine) i.getSerializableExtra("line");
                text = l.text;
                selector = l.selector;
                server = l.server;
                port = l.port;


                //set the title of the window
                setTitle(server + selector);

                ///Network stuff
                final String lines;
                try {
                    //start new connection
                    Connection conn = new Connection(server, port);

                    //get the desired text file
                    lines = conn.getText(selector);


                    //make the progress bar invisible
                    final ProgressBar progressBar = findViewById(R.id.progressBar);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setVisibility(View.GONE);
                        }
                    });

                } catch (final IOException e) {
                    e.printStackTrace();
                    //inform the user of the error
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast toast = Toast.makeText(getApplicationContext(), e.getMessage(),
                                    Toast.LENGTH_LONG);
                            toast.show();
                        }
                    });
                    //kill current activity (go back to the previous one on the stack)
                    finish();
                    return;
                }

                //render the lines on the screen
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        textView.append(lines);
                    }
                });

                //some settings for textView
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        textView.setLineSpacing(18, 1);
                        textView.setMovementMethod(LinkMovementMethod.getInstance());
                    }
                });
            }
        }).start();

    }

    //setup the menu/title bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.client_downloadable, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.addBookmarkButton:
                //setup the intent
                final Intent intent = new Intent(getApplicationContext(), EditBookmarkActivity
                        .class);
                //send the message with the values for the bookmark
                Bookmark bookmark;
                try {
                    bookmark = new Bookmark(getApplicationContext(), "", '0', selector, server,
                            port);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

                intent.putExtra("bookmark", bookmark);

                //start the intent
                startActivity(intent);

                return true;

            case R.id.downloadButton:
                l.download(this);

                return true;


            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
