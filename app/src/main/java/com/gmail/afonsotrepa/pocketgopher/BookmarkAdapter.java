package com.gmail.afonsotrepa.pocketgopher;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


/**
 *
 */

public class BookmarkAdapter extends ArrayAdapter<Bookmark>{
    public BookmarkAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public BookmarkAdapter(Context context, int textViewResourceId, Bookmark[] bookmarks) {
        super(context, textViewResourceId, bookmarks);
    }

    @Override
    public @NonNull View getView(int position, @Nullable View convertView,@NonNull ViewGroup parent) {
        //inflate layout if convertView is null
        if (convertView == null) {
            LayoutInflater vi = LayoutInflater.from(getContext());
            convertView = vi.inflate(R.layout.activity_listview, null);
        }

        //the bookmark to display
        Bookmark bookmark = getItem(position);
        //the TextView box to use
        TextView textView = convertView.findViewById(R.id.label);
        //set the font
        textView.setTextAppearance(getContext(), MainActivity.font);

        //display the name of the bookmark
        textView.setText(bookmark.name);

        return convertView;
    }
}
