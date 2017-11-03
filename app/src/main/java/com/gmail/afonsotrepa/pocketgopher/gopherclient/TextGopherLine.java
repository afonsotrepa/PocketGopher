package com.gmail.afonsotrepa.pocketgopher.gopherclient;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.widget.TextView;


/**
 * Simple informational text line ('i')
 */

class TextGopherLine extends GopherLine {
    private final static Float MAIN_TITLE_SIZE = 2f;
    private final static Float SUB_TITLE_SIZE = 1.5f;

    TextGopherLine(String text, String selector) {
        this.text = text;
        this.selector = selector;
        this.server = "null.host";
        this.port = 1;
    }

    public void render(final TextView textView, Context context) {

        final SpannableString ss = new SpannableString(this.text+"\n");

        //Change the font size if it's a title (UNTESTED!!!)
        if (this.selector.equals("TITLE"))
                if (textView.getText().toString().matches(""))
                    ss.setSpan(new RelativeSizeSpan(MAIN_TITLE_SIZE), 0, text.length(), 0);
                else
                    ss.setSpan(new RelativeSizeSpan(SUB_TITLE_SIZE), 0, text.length(), 0);


        final Handler handler = new Handler(Looper.getMainLooper());

        //add ss to textview
        handler.post(new Runnable() {
            @Override
            public void run() {
                textView.append(ss);
            }
        });
    }
}
