package com.gmail.afonsotrepa.pocketgopher.gopherclient;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.gmail.afonsotrepa.pocketgopher.EditBookmarkActivity;
import com.gmail.afonsotrepa.pocketgopher.MainActivity;
import com.gmail.afonsotrepa.pocketgopher.R;

import java.io.IOException;
import java.util.List;

import static com.gmail.afonsotrepa.pocketgopher.MainActivity.EXTRA_MESSAGE;

/**
 *
 */

public class TextFileActivity extends AppCompatActivity{
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
                Intent intent = getIntent();
                String[] message = intent.getStringExtra(EXTRA_MESSAGE).split("\t");
                selector = message[0];
                server = message[1];
                port = Integer.parseInt(message[2]);

                ///Network stuff
                List<String>  lines;
                try {
                    //start new connection
                    Connection conn = new Connection(server, port);

                    //get the desired text file
                    lines = conn.getTextFile(selector);
                }
                catch (final IOException e) {
                    e.printStackTrace();
                    //inform the user of the error
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast toast = Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG);
                            toast.show();
                        }
                    });
                    //kill current activity (go back to the previous one on the stack)
                    finish();
                    return;
                }

                //render the lines on the screen
                for (final String line : lines) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            textView.append(line+"\n");
                        }
                    });
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
                final Intent intent = new Intent(getApplicationContext(), EditBookmarkActivity.class);
                //send the message with the values for the bookmark
                intent.putExtra(EXTRA_MESSAGE,
                        ""+"\t"+ //name (empty)
                                "0"+"\t"+ //type
                                selector+"\t"+ //selector
                                server+"\t"+ //server
                                port.toString()+"\t"+ //port
                                Integer.toString(0) + "\t" //id (default)
                );
                //start the intent
                startActivity(intent);

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
