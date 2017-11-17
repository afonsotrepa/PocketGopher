package com.gmail.afonsotrepa.pocketgopher.gopherclient;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.gmail.afonsotrepa.pocketgopher.R;

import java.io.File;
import java.io.IOException;

/**
 * HTML file ('h')
 */

public class HtmlPage extends Page
{
    private static final Integer IMAGE_TAG = R.drawable.ic_web_asset_white;

    String line = null;

    public HtmlPage(String selector, String server, Integer port, String line)
    {
        super(server, port, 'h', selector);
        this.line = line;
    }

    public void render(final TextView textView, final Context context, String line)
    {
        //handler to the main thread
        final Handler handler = new Handler(Looper.getMainLooper());
        final SpannableString text = new SpannableString("  " + line + " \n");

        final Page page = this;

        //create the span (and the function to be run when it's clicked)
        final ClickableSpan cs1 = new ClickableSpan()
        {
            @Override
            public void onClick(View widget)
            {
                page.open(context);
            }
        };
        final ClickableSpan cs2 = new ClickableSpan()
        {
            @Override
            public void onClick(View widget)
            {
                page.open(context);
            }
        };


        //apply the span to text and append text to textView
        handler.post(new Runnable()
        {
            @Override
            public void run()
            {
                //make it clickable
                text.setSpan(cs1, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                text.setSpan(cs2, 2, text.length() - 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                //set the image tag behind (left of) the text
                text.setSpan(new ImageSpan(context, IMAGE_TAG), 0, 1, 0);
                //add it to the end of textView
                textView.append(text);
            }
        });
    }

    public void open(final Context context)
    {
        final Page page = this;

        final File file = new File(context.getExternalCacheDir() +
                context.getPackageName().replace('/', '-'));

        //get the file
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                final Handler handler = new Handler(Looper.getMainLooper());

                try {
                    Connection conn = new Connection(page.server, page.port);
                    conn.getBinary(page.selector, file);

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
                            Toast toast = Toast.makeText(context, e.getMessage(),
                                    Toast.LENGTH_LONG
                            );
                            toast.show();
                        }
                    });
                }
            }
        }).start();


        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        intent.setData(Uri.fromFile(file));
        context.startActivity(intent);
    }
}
