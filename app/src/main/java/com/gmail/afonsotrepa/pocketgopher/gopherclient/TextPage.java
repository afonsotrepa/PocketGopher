package com.gmail.afonsotrepa.pocketgopher.gopherclient;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.widget.TextView;
import android.widget.Toast;


/**
 * Simple informational text line ('i')
 */

public class TextPage extends Page
{
    private final static Float MAIN_TITLE_SIZE = 2f;
    private final static Float SUB_TITLE_SIZE = 1.5f;

    //selector is needed to check for titles
    TextPage(String selector, String line)
    {
        super(null, 0, 'i', selector, line);
    }

    public void render(final TextView textView, Context context, String line)
    {

        final SpannableString text = new SpannableString(line + "\n");

        //Change the font size if it's a title (UNTESTED!!!)
        if (this.selector.equals("TITLE"))
        {
            if (textView.getText().toString().matches(""))
            {
                text.setSpan(new RelativeSizeSpan(MAIN_TITLE_SIZE), 0, text.length(), 0);
            } else
            {
                text.setSpan(new RelativeSizeSpan(SUB_TITLE_SIZE), 0, text.length(), 0);
            }
        }


        final Handler handler = new Handler(Looper.getMainLooper());

        //display the line
        handler.post(new Runnable()
        {
            @Override
            public void run()
            {
                textView.append(text);
            }
        });
    }


    public void open(Context context)
    {
        Toast.makeText(context, "Can't open a page of type 'i'!!", Toast.LENGTH_LONG).show();
    }
}
