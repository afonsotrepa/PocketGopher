package com.gmail.afonsotrepa.pocketgopher.gopherclient;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.widget.TextView;
import android.widget.Toast;

import com.gmail.afonsotrepa.pocketgopher.R;

/**
 *
 */

public class UnknownPage extends Page
{
    private static final Integer IMAGE_TAG = R.drawable.ic_error_white;

    public String line;
    private Character type;


    public UnknownPage(String line)
    {
        super(null, 0, '3', "", line);
    }

    public void render(final TextView textView, final Context context, String line)
    {
        final Handler handler = new Handler(Looper.getMainLooper());
        final SpannableString text = new SpannableString("  " + line + "\n");

        handler.post(new Runnable()
        {
            @Override
            public void run()
            {
                //set the image tag behind (left of) the text
                text.setSpan(new ImageSpan(context, IMAGE_TAG), 0, 1, 0);
                textView.append(text);
            }
        });
    }

    public void open(Context context)
    {
        Toast.makeText(context, "Can't open a page of type '" + this.type + "' !!",
                Toast.LENGTH_LONG).show();
    }
}
