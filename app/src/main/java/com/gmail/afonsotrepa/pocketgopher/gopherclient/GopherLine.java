package com.gmail.afonsotrepa.pocketgopher.gopherclient;


import android.content.Context;
import android.widget.TextView;

/**
 *
 */

public abstract class GopherLine {
    String text;
    String selector;
    String server;
    Integer port;

    /**
     * Used to render the line onto the screen
     * @param textView current (latest) textview
     * @param context context of the current (latest) activity
     */
    public abstract void render(final TextView textView, Context context);
}
