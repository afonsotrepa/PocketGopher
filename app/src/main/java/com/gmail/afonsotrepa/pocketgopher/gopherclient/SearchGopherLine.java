package com.gmail.afonsotrepa.pocketgopher.gopherclient;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gmail.afonsotrepa.pocketgopher.MainActivity;
import com.gmail.afonsotrepa.pocketgopher.R;


/**
 * Index-Search server ('7')
 */

public class SearchGopherLine extends GopherLine {
    private static final Integer IMAGE_TAG = R.drawable.ic_search_white;

    public SearchGopherLine(String text, String selector, String server, Integer port) {
        super(text, server, port, '7', selector);
    }

    public void render(final TextView textView, final Context context) {
        final Handler handler = new Handler(Looper.getMainLooper());
        final SpannableString text = new SpannableString("  " + this.text + "\n");

        //create the span (and the function to be run when it's clicked)
        final ClickableSpan cs1 = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                onLineClick(context, widget);
            }
        };
        final ClickableSpan cs2 = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                onLineClick(context, widget);
            }
        };

        //apply the span to text and append text to textview
        handler.post(new Runnable() {
            @Override
            public void run() {
                //make it clickable
                text.setSpan(cs1, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                text.setSpan(cs2, 2, text.length() - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                //set the image tag behind (left of) the text
                text.setSpan(new ImageSpan(context, IMAGE_TAG), 0, 1, 0);
                //add it to the end of textView
                textView.append(text);
            }
        });
    }

    /**
     * Run when the line is clicked
     */
    private void onLineClick(final Context context, View widget) {
        //AlertDialog to be shown when method gets called
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);

        //the EditText where the user will input the name of the file
        final EditText input = new EditText(context);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(layoutParams);
        input.setTextAppearance(context, MainActivity.font);
        dialog.setView(input);

        dialog.setPositiveButton("Send",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //get the query
                        String query = input.getText().toString();


                        //make the line
                        MenuGopherLine line = new MenuGopherLine(query, selector + "\t" + query,
                                server,
                                port);

                        //start the MenuActivity
                        Intent intent = new Intent(context, MenuActivity.class);
                        intent.putExtra("line", line);
                        context.startActivity(intent);
                    }
                }
        );

        dialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }
        );

        dialog.show();
    }
}
