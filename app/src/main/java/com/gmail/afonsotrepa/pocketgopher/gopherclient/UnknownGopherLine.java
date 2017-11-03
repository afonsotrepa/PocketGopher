package com.gmail.afonsotrepa.pocketgopher.gopherclient;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;

/**
 *
 */

public class UnknownGopherLine extends GopherLine {
    Character type;

    UnknownGopherLine(String text, Character type) {
        this.text = text;
        this.type = type;
    }

    public void render(final TextView textView, Context context) {
        final Handler handler = new Handler(Looper.getMainLooper());
        final String text = this.text;
        final Character type = this.type;

        handler.post(new Runnable() {
            @Override
            public void run() {
                textView.append(text + " <"+type+">\n");
            }
        });
    }
}
