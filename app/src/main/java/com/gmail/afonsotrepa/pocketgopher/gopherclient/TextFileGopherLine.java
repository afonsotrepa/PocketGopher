package com.gmail.afonsotrepa.pocketgopher.gopherclient;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.TextView;


/**
 * Text File ('0')
 */
public class TextFileGopherLine extends GopherLine {
    private static final Integer COLOR_TAG = Color.DKGRAY;
    private static final String EXTRA_MESSAGE = "com.gmail.afonsotrepa.pocketgopher.MESSAGE";

    TextFileGopherLine(String text, String selector, String server, Integer port) {
        this.text = text;
        this.selector = selector;
        this.server = server;
        this.port = port;
    }

    public void render(final TextView textView, final Context context) {
        //handler to the main thread
        final Handler handler = new Handler(Looper.getMainLooper());
        final SpannableString text = new SpannableString(this.text+"\n");

        //make and setup the new intent
        final Intent intent = new Intent(context, TextFileActivity.class);
        intent.putExtra(EXTRA_MESSAGE, selector+"\t"+server+"\t"+port);

        //create the span (and the function to be run when it's clicked)
        final ClickableSpan cs = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                context.startActivity(intent);
            }
        };

        //apply the span to text and append text to textview
        handler.post(new Runnable() {
            @Override
            public void run() {
                //make it clickable
                text.setSpan(cs, 0, text.length()-1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                //set the color
                text.setSpan(new ForegroundColorSpan(COLOR_TAG), 0, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                //add it to the end of textview
                textView.append(text);
            }
        });
    }
}
