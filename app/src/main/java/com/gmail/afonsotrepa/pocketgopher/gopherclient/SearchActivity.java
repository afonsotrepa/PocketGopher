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
import com.gmail.afonsotrepa.pocketgopher.MainActivity;
import com.gmail.afonsotrepa.pocketgopher.R;

import static com.gmail.afonsotrepa.pocketgopher.MainActivity.EXTRA_MESSAGE;

/**
 *
 */

public class SearchActivity extends AppCompatActivity{
    String selector;
    String server;
    Integer port;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        //get info
        final String[] message= getIntent().getStringExtra(MainActivity.EXTRA_MESSAGE).split("\t");
        selector = message[0];
        server = message[1];
        port = Integer.parseInt(message[2]);


        //setup the widgets
        final TextView editText = findViewById(R.id.editText);
        Button sendButton = findViewById(R.id.sendButton);

        //make and setup the new intent
        final Context context = getApplicationContext();
        final Intent intent = new Intent(context, MenuActivity.class);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String query = editText.getText().toString();
                intent.putExtra(EXTRA_MESSAGE, selector+"\t"+server+"\t"+port+"\t"+query);
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
                final Intent intent = new Intent(getApplicationContext(), EditBookmarkActivity.class);
                //send the message with the values for the bookmark
                intent.putExtra(EXTRA_MESSAGE,
                        ""+"\t"+ //name (empty)
                                "7"+"\t"+ //type
                                selector+"\t"+ //selector
                                server+"\t"+ //server
                                port.toString()+"\t" //port
                );
                //start the intent
                startActivity(intent);

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
