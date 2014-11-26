package com.coursera.wfernandes.dailyselfie;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class CustomAdapter extends ArrayAdapter<Selfie> {

    private final Context context;

    public CustomAdapter(Context context, int resource, int textViewResource, List<Selfie> objects) {
        super(context, resource, textViewResource, objects);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Selfie selfie = getItem(position);
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        final ViewHolder viewHolder;


        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.list_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.textView = (TextView) convertView.findViewById(R.id.item_txt);
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.item_img);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (selfie != null) {
            viewHolder.textView.setText(selfie.getSelfieName());
            viewHolder.imageView.setImageBitmap(selfie.getSelfieThumb());
        }

        return convertView;

    }
}
