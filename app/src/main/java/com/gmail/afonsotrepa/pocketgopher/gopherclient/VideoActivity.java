package com.gmail.afonsotrepa.pocketgopher.gopherclient;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.VideoView;

import com.gmail.afonsotrepa.pocketgopher.Bookmark;
import com.gmail.afonsotrepa.pocketgopher.EditBookmarkActivity;
import com.gmail.afonsotrepa.pocketgopher.R;

import java.io.File;
import java.io.IOException;

/**
 *
 */

public class VideoActivity extends AppCompatActivity {
    VideoGopherLine l;
    String selector;
    String server;
    Integer port;
    File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        //start a new thread to do network stuff
        new Thread(new Runnable() {
            @Override
            public void run() {
                //handler to the main thread
                final Handler handler = new Handler(Looper.getMainLooper());

                //get info
                Intent i = getIntent();
                l = (VideoGopherLine) i.getSerializableExtra("line");
                selector = l.selector;
                server = l.server;
                port = l.port;

                //set the title of the window
                setTitle(server + selector);

                ///Network stuff to save the video to cache
                file = new File(getExternalCacheDir() + "/" + selector.replace('/', '-'));
                try {
                    //create the file
                    file.createNewFile();

                    //start new connection
                    Connection conn = new Connection(server, port);

                    //get the desired video
                    conn.getBinary(selector, file);


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


                final ProgressBar progressBar = findViewById(R.id.progressBar);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        //make the progress bar invisible
                        progressBar.setVisibility(View.GONE);
                    }
                });

                //make and start an intent to call the media player
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setDataAndType(Uri.fromFile(file), "video/*");
                startActivityForResult(intent, 1);
            }
        }).start();
    }

     @Override
     protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
         //delete the cached file to save space
         file.delete();

         //exit the activity
         finish();
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
                    bookmark = new Bookmark(getApplicationContext(), "", 'I', selector, server,
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
