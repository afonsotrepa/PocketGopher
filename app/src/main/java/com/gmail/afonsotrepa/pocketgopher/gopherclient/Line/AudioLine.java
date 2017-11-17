package com.gmail.afonsotrepa.pocketgopher.gopherclient.Line;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.gmail.afonsotrepa.pocketgopher.R;
import com.gmail.afonsotrepa.pocketgopher.gopherclient.Connection;

import java.io.File;
import java.io.IOException;

/**
 * Audio ('s')
 */

public class AudioLine extends Line
{
    private static final Integer IMAGE_TAG = R.drawable.ic_video_label_white;


    public AudioLine(String text, String selector, String server, Integer port)
    {
        super(text, server, port, 's', selector);
    }

    public void render(final TextView textView, final Context context)
    {
        //handler to the main thread
        final Handler handler = new Handler(Looper.getMainLooper());
        final SpannableString text = new SpannableString("  " + this.text + " \n");

        final Line line = this;

        //TODO: download on long click (instead of using the type icon for that)
        //create the span (and the function to be run when it's clicked)
        final ClickableSpan cs1 = new ClickableSpan()
        {
            @Override
            public void onClick(View widget)
            {
                line.download(context);
            }
        };
        final ClickableSpan cs2 = new ClickableSpan()
        {
            @Override
            public void onClick(View widget)
            {
                onLineClick(context);
            }
        };


        //apply the span to text and append text to textView
        handler.post(new Runnable()
        {
            @Override
            public void run()
            {
                //make it clickable
                text.setSpan(cs1, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                text.setSpan(cs2, 2, text.length() - 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
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
    public void onLineClick(final Context context)
    {
        final ProgressBar progressBar = ((Activity) context).findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);


        //start a new thread to do network stuff
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                //handler to the main thread
                final Handler handler = new Handler(Looper.getMainLooper());

                ///Network stuff to save the video to cache
                File file = new File(context.getExternalCacheDir() + "/" +
                        context.getPackageName().replace('/', '-'));
                try
                {
                    //create the file
                    file.createNewFile();

                    //start new connection
                    Connection conn = new Connection(server, port);

                    //get the desired video
                    conn.getBinary(selector, file);


                }
                catch (final IOException e)
                {
                    //inform the user of the error and exit
                    e.printStackTrace();
                    handler.post(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            Toast toast = Toast.makeText(context.getApplicationContext(),
                                    e.getMessage(),
                                    Toast.LENGTH_LONG
                            );
                            toast.show();
                        }
                    });
                    return;
                }


                handler.post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        //make the progress bar invisible
                        progressBar.setVisibility(View.GONE);
                    }
                });

                //make and start an intent to call the media player
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setDataAndType(Uri.fromFile(file), "audio/*");
                ((Activity) context).setResult(Activity.RESULT_OK, intent);
                context.startActivity(intent);

                //TODO: delete the file after intent is finished
            }
        }).start();
    }
}
