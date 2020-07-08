package com.example.fevertracker;

import android.content.Context;
import android.text.Spanned;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class announceAdapter extends ArrayAdapter<Announce> {

    announcement anouncementC;
    private Context mContext;
    private int mResource;

    public void setAnouncement(announcement anouncement) {
        this.anouncementC = anouncement;
    }

    private static class ViewHolder {
        TextView announce;
        ImageButton deletePost, ecitPost;
    }

    public announceAdapter(Context context, int resource, ArrayList<Announce> items) {
        super(context, resource, items);
        mContext = context;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Spanned announce = getItem(position).getAnnounce();
        announceAdapter.ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false);
            holder = new announceAdapter.ViewHolder();
            holder.announce = (TextView) convertView.findViewById(R.id.announceText);
            holder.deletePost = convertView.findViewById(R.id.deletePost);
            holder.ecitPost = convertView.findViewById(R.id.ecitPost);

            convertView.setTag(holder);
        } else {
            holder = (announceAdapter.ViewHolder) convertView.getTag();
        }
        if (holder.deletePost != null) {
            holder.deletePost.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    anouncementC.deletePost(position);
                }
            });
        }
        if (holder.ecitPost != null) {
            holder.ecitPost.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    anouncementC.ecitPost(position);
                }
            });
        }
        holder.announce.setText(announce);
        holder.announce.setGravity(Gravity.LEFT);

        return convertView;
    }
}
