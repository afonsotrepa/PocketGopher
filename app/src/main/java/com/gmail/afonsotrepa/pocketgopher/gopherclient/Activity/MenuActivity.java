package com.gmail.afonsotrepa.pocketgopher.gopherclient.Activity;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.gmail.afonsotrepa.pocketgopher.Bookmark;
import com.gmail.afonsotrepa.pocketgopher.MainActivity;
import com.gmail.afonsotrepa.pocketgopher.R;
import com.gmail.afonsotrepa.pocketgopher.gopherclient.Connection;
import com.gmail.afonsotrepa.pocketgopher.gopherclient.Page;

import java.io.IOException;
import java.util.List;


/**
 *
 */

public class MenuActivity extends AppCompatActivity
{
    String selector;
    String server;
    Integer port;
    String url;


    @Override
    protected void onCreate(Bundle savedInstaceState)
    {
        super.onCreate(savedInstaceState);
        setContentView(R.layout.activity_menu);

        //widget to write to
        final TextView textView = findViewById(R.id.textView);
        //set the font
        textView.setTextAppearance(this, MainActivity.font);

        final Context context = this;

        //start a new thread to do network stuff
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                //handler to the main thread
                final Handler handler = new Handler(Looper.getMainLooper());

                //get info
                Intent i = getIntent();
                final Page p = (Page) i.getSerializableExtra("page");
                selector = p.selector;
                server = p.server;
                port = p.port;
                url = p.url;

                setTitle(url);

                ///Network stuff
                List<Page> lines;
                try
                {
                    //start new connection
                    Connection conn = new Connection(server, port);

                    //get the desired directory/menu
                    lines = conn.getMenu(selector);

                }
                catch (final IOException e)
                {
                    e.printStackTrace();
                    //inform the user of the error
                    handler.post(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            Toast toast = Toast.makeText(getApplicationContext(), e.getMessage(),
                                    Toast.LENGTH_LONG
                            );
                            toast.show();
                        }
                    });
                    //kill current activity (go back to the previous one on the stack)
                    finish();
                    return;
                }

                //render the lines on the screen
                for (Page line : lines)
                {
                    line.render(textView, MenuActivity.this, line.line);
                }

                //make the progress bar invisible
                final ProgressBar progressBar = findViewById(R.id.progressBar);
                handler.post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        progressBar.setVisibility(View.GONE);
                    }
                });


                //some settings for textView
                handler.post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        textView.setLineSpacing(18, 1);
                        textView.setMovementMethod(LinkMovementMethod.getInstance());
                    }
                });
            }
        }).start();
    }


    //setup the menu/title bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.client_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.addBookmarkButton:
                try
                {
                    new Bookmark(getApplicationContext(), "", '1', selector, server, port)
                            .editBookmark(MenuActivity.this);
                }
                catch (Exception e)
                {
                    throw new RuntimeException(e);
                }

                return true;

            case R.id.link:
                //create the dialog to be shown when the button gets clicked
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
                alertDialog.setMessage("URL:");

                //setup the EditText where the user will input url to the page
                final EditText input = new EditText(this);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT
                );
                input.setLayoutParams(layoutParams);
                input.setInputType(InputType.TYPE_TEXT_VARIATION_URI);
                input.setText(url);
                alertDialog.setView(input);


                alertDialog.setPositiveButton("Go",
                        new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(final DialogInterface dialog, int which)
                            {
                                //setup the page
                                Page page = Page.makePage(input.getText().toString());

                                page.open(MenuActivity.this);
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

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
