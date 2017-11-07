package com.gmail.afonsotrepa.pocketgopher.gopherclient;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.gmail.afonsotrepa.pocketgopher.EditBookmarkActivity;
import com.gmail.afonsotrepa.pocketgopher.R;

import java.io.IOException;

/**
 *
 */

public class HtmlActivity extends AppCompatActivity{
    String selector;
    String server;
    Integer port;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_html);

        //widget to write to
        final WebView webView = findViewById(R.id.constraintLayout).findViewById(R.id.webView);

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

                //set the title of the window
                setTitle(server + selector);

                ///Network stuff to get the html text
                final String html;
                try {
                    //start new connection
                    Connection conn = new Connection(server, port);

                    //get the desired html text
                    html = conn.getText(selector);


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


                //render the html on webView
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        webView.loadData(html, "text/html", null);
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
                intent.putExtra("name", "");
                intent.putExtra("type", 'h');
                intent.putExtra("selector", selector);
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
