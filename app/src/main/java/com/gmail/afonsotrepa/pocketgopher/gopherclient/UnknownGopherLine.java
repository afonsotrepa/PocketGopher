package com.gmail.afonsotrepa.pocketgopher.gopherclient;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.widget.TextView;

import com.gmail.afonsotrepa.pocketgopher.R;

/**
 *
 */

public class UnknownGopherLine extends GopherLine {
    private static final Integer IMAGE_TAG = R.drawable.ic_error_white;

    private Character type_tag;

    UnknownGopherLine(String text, Character type_tag) {
        this.text = text;
        this.type_tag = type_tag;
    }

    public void render(final TextView textView, final Context context) {
        final Handler handler = new Handler(Looper.getMainLooper());
        final SpannableStringBuilder text = new SpannableStringBuilder(" "+this.text+
                " <"+this.type_tag+">\n");

        handler.post(new Runnable() {
            @Override
            public void run() {
                //set the image tag behind (left of) the text
                text.setSpan(new ImageSpan(context, IMAGE_TAG), 0, 1, 0);
                textView.append(text);
            }
        });
    }
}
