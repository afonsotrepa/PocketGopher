package com.gmail.afonsotrepa.pocketgopher.gopherclient;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.view.View;
import android.widget.TextView;

import com.gmail.afonsotrepa.pocketgopher.R;

/**
 * Macintosh BinHex file, Binary Archive, UUEncoded file, Binary file, Word-processing  document
 * ('4', '5', '6', '9', 'd')
 */

public class BinLine extends Line {
    private static final Integer IMAGE_TAG = R.drawable.ic_file_download_white;

    BinLine(String text, String selector, String server, Integer port) {
        super(text, server, port, '9', selector);
    }

    public void render(final TextView textView, final Context context) {
        //handler to the main thread
        final Handler handler = new Handler(Looper.getMainLooper());
        final SpannableString text = new SpannableString("  " + this.text + " \n");


        final BinLine line = this;
        //create the span (and the function to be run when it's clicked)
        final ClickableSpan cs1 = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                line.download(context);
            }
        };
        final ClickableSpan cs2 = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                line.download(context);
            }
        };


        //apply the span to text and append text to textView
        handler.post(new Runnable() {
            @Override
            public void run() {
                //make it clickable
                text.setSpan(cs1, 0,1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                text.setSpan(cs2, 2, text.length() - 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                //set the image tag behind (left of) the text
                text.setSpan(new ImageSpan(context, IMAGE_TAG), 0, 1, 0);
                //add it to the end of textView
                textView.append(text);
            }
        });
    }

}
