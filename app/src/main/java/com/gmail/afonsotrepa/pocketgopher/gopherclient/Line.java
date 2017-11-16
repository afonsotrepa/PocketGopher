package com.gmail.afonsotrepa.pocketgopher.gopherclient;


import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.TextView;

import java.io.Serializable;

/**
 *
 */

public abstract class Line extends Page implements Serializable
{
    public String text;

    Line(String text, String server, Integer port, Character type, String selector)
    {
        super(server, port, type, selector);
        this.text = text;
    }


    /**
     * Used to render the line onto the screen
     *
     * @param textView current (latest) TextView
     * @param context  context of the current (latest) activity
     */
    public abstract void render(final TextView textView, Context context);


    @NonNull
    public static Line makeLine(Character type, String text, String selector, String
            server, Integer port
    )
    {
        switch (type)
        {
            case '0': //text file
                return new TextFileLine(
                        text, //remove the type tag
                        selector,
                        server,
                        port
                );

            case '1': //menu/directory
                return new MenuLine(
                        text, //remove the type tag
                        selector,
                        server,
                        port
                );

            case '7': //Search engine or CGI script
                return new SearchLine(
                        text, //remove the type tag
                        selector,
                        server,
                        port
                );

            case 'i': //Informational text (not in the protocol but common)
                return new TextLine(text, selector);

            case 'g': //gif (temporary)
            case 'I': //Image
                return new ImageLine(
                        text, //remove the type tag
                        selector,
                        server,
                        port
                );

            case 'h': //html
                return new HtmlLine(
                        text, //remove the type tag
                        selector,
                        server,
                        port
                );

            case '4': //macintosh binhex file
            case '5': //binary archive
            case '6': //uuencoded file
            case '9': //binary file
            case 'd': //word-processing document
                return new BinLine(
                        text, //remove the type tag
                        selector,
                        server,
                        port
                );

            case ';': //video file
                return new VideoLine(
                        text, //remove the type tag
                        selector,
                        server,
                        port
                );

            case 's': //audio file
                return new AudioLine(
                        text, //remove the type tag
                        selector,
                        server,
                        port
                );

            default:
                return new BinLine(
                        text, //remove the type tag
                        selector,
                        server,
                        port
                );
        }
    }
}
