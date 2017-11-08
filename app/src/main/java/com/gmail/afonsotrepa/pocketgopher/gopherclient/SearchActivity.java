package com.gmail.afonsotrepa.pocketgopher.gopherclient;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.gmail.afonsotrepa.pocketgopher.EditBookmarkActivity;
import com.gmail.afonsotrepa.pocketgopher.R;


/**
 *
 */

public class SearchActivity extends AppCompatActivity {
    String selector;
    String server;
    Integer port;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        //get info
        Intent i = getIntent();
        SearchGopherLine l = (SearchGopherLine) i.getSerializableExtra("line");
        selector = l.selector;
        server = l.server;
        port = l.port;


        //setup the widgets
        final TextView editText = findViewById(R.id.editText);
        Button sendButton = findViewById(R.id.sendButton);

        //make and setup the new intent
        final Context context = getApplicationContext();
        final Intent intent = new Intent(context, MenuActivity.class);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get the query from the editText box
                String query = editText.getText().toString();

                //make the line
                MenuGopherLine line = new MenuGopherLine(query, selector + "\t" + query, server,
                        port);

                intent.putExtra("line", line);
                context.startActivity(intent);
            }
        });
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
                intent.putExtra("name", ""); //name (empty)
                intent.putExtra("type", '7'); //type
                intent.putExtra("selector", selector); //selector
                intent.putExtra("server", server); //server
                intent.putExtra("port", port); //port
                intent.putExtra("id", 0); //id (defaults to 0)

                //start the intent
                startActivity(intent);

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
