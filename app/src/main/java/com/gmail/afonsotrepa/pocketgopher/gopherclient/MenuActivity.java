package com.gmail.afonsotrepa.pocketgopher.gopherclient;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.gmail.afonsotrepa.pocketgopher.EditBookmarkActivity;
import com.gmail.afonsotrepa.pocketgopher.R;

import java.io.IOException;
import java.util.List;


/**
 *
 */

public class MenuActivity extends AppCompatActivity {
    String selector;
    String server;
    Integer port;
    String query;

    @Override
    protected void onCreate(Bundle savedInstaceState) {
        super.onCreate(savedInstaceState);
        setContentView(R.layout.activity_menu);

        //widget to write to
        final TextView textView = (TextView) findViewById(R.id.textView);

        final Context context = this;

        //start a new thread to do network stuff
        new Thread(new Runnable() {
            @Override
            public void run() {
                //handler to the main thread
                final Handler handler = new Handler(Looper.getMainLooper());

                //get info
                Intent i = getIntent();
                selector =i.getStringExtra("selector");
                server = i.getStringExtra("server");
                port = i.getIntExtra("port",  70);
                query = i.getStringExtra("query");
                if (query == null) {
                    query = "";
                }

                //set the title of the window
                setTitle(server + selector);

                ///Network stuff
                List<GopherLine> lines;
                try {
                    //start new connection
                    Connection conn = new Connection(server, port);

                    //get the desired directory/menu
                    lines = conn.getMenu(selector + "\t" + query);
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
                for (GopherLine line : lines) {
                    line.render(textView, context);
                }

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
        getMenuInflater().inflate(R.menu.client_menu, menu);
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
                intent.putExtra("name", "");
                intent.putExtra("type", '1');
                intent.putExtra("selector", selector+"\t"+query);
                intent.putExtra("server", server);
                intent.putExtra("port", port);
                intent.putExtra("id", 0);

                //start the intent
                startActivity(intent);

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
