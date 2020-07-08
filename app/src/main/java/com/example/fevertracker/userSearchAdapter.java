package com.example.fevertracker;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;

public class userSearchAdapter extends ArrayAdapter<userSearch> {
    private Context mContext;
    private int mResource;


    private static class ViewHolder {
        TextView userName, userPassport, userId;
    }

    userSearchAdapter(Context context, int resource, ArrayList<userSearch> users) {
        super(context, resource, users);
        mContext = context;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        String name = getItem(position).getName().trim(), passport = getItem(position).getPassport().trim(), id = getItem(position).getId().trim();
        int status = getItem(position).getStatus();

        userSearch user = new userSearch(name, passport, id, status);

        final ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false);
            holder = new ViewHolder();
            holder.userName = (TextView) convertView.findViewById(R.id.userName);
            holder.userPassport = (TextView) convertView.findViewById(R.id.userPassport);
            holder.userId = (TextView) convertView.findViewById(R.id.userId);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.userName.setText(user.getName());
        holder.userPassport.setText(user.getPassport());
        holder.userId.setText(user.getId());

        if (user.getStatus() == 2) {
            convertView.setBackgroundResource(R.drawable.users_search_ripple_yellow);
        } else if (user.getStatus() == 3) {
            convertView.setBackgroundResource(R.drawable.users_search_ripple_red);
        } else {
            convertView.setBackgroundResource(R.drawable.users_search_ripple_green);
        }

        return convertView;
    }

}
