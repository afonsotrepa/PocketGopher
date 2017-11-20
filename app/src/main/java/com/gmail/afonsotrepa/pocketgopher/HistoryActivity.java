package com.gmail.afonsotrepa.pocketgopher;

import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.gmail.afonsotrepa.pocketgopher.gopherclient.Page;

import java.util.List;

/**
 * Displays the history
 */

public class HistoryActivity extends AppCompatActivity
{
    @Override
    protected void onResume()
    {
        super.onResume();
        setContentView(R.layout.activity_history);


        //get the history and display it on the ListView
        List<String> history = History.read(this);
        if (history == null)
        {
            return;
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                R.layout.activity_listview,
                history
        );
        ListView listView = findViewById(R.id.listView);
        listView.setAdapter(adapter);

        //make each item clickable (open it when clicked)
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                final String url = (String) parent.getItemAtPosition(position);

                new Thread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Page.makePage(url).open(getApplicationContext());
                    }
                }).start();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.client_history, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.clearHistory:
                History.clear(this);

                return true;


            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
