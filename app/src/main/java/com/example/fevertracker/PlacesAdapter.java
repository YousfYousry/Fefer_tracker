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

public class PlacesAdapter extends ArrayAdapter<PlacesClass> {
    private Context mContext;
    private int mResource;
    private ArrayList<View> all=new ArrayList<>(20);
    private int[] color ;
    void setViewColor(int pos) {
        if (all.get(pos) != null) {
            all.get(pos).setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
        }
    }

    public void setColor(int[] color) {
        this.color = color;
    }

    void setTrackerForAdmin(TrackerForAdmin trackerForAdmin) {
        this.trackerForAdmin = trackerForAdmin;
    }

    TrackerForAdmin trackerForAdmin=new TrackerForAdmin();

    private static class ViewHolder {
        TextView Placename,TimeInter,PlaceDetails,TimeInRoad,Distance,LatLon;
        LinearLayout PlacesLayout,DistanceLayout;
    }

    PlacesAdapter(Context context, int resource, ArrayList<PlacesClass> items) {
        super(context, resource, items);
        mContext = context;
        mResource = resource;
        if(all!=null){
            all.clear();
        }
        for(int i=0;i<20;i++) {
            all.add(null);
        }
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        all.set(position,convertView);
        String Placename = getItem(position).getPlacename().trim()
                ,TimeInter = getItem(position).getTimeInter().trim()
                ,PlaceDetails = getItem(position).getPlaceDetails().trim()
                ,TimeInRoad = getItem(position).getTimeInRoad().trim()
                ,Distance = getItem(position).getDistance().trim()
                ,LatLon = getItem(position).getLatLon().trim();
        if(Placename.length()>20){
            Placename = Placename.substring(0,19)+"...";
        }

        PlacesClass place = new PlacesClass(Placename,TimeInter,PlaceDetails,TimeInRoad,Distance,LatLon);

        final ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false);
            holder = new ViewHolder();
            holder.Placename = (TextView) convertView.findViewById(R.id.Placename);
            holder.TimeInter = (TextView) convertView.findViewById(R.id.TimeInter);
            holder.PlaceDetails = (TextView) convertView.findViewById(R.id.PlaceDetails);
            holder.TimeInRoad = (TextView) convertView.findViewById(R.id.TimeInRoad);
            holder.Distance = (TextView) convertView.findViewById(R.id.Distance);
            holder.LatLon = (TextView) convertView.findViewById(R.id.LatLon);
            holder.PlacesLayout = (LinearLayout) convertView.findViewById(R.id.PlacesLayout);
            holder.DistanceLayout = (LinearLayout) convertView.findViewById(R.id.DistanceLayout);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if(place.getDistance().isEmpty()){
            holder.PlacesLayout.setVisibility(View.VISIBLE);
            holder.DistanceLayout.setVisibility(View.GONE);
            holder.Placename.setText(place.getPlacename());
            holder.TimeInter.setText(place.getTimeInter());
            holder.PlaceDetails.setText(place.getPlaceDetails());
            holder.LatLon.setText(place.getLatLon());
        }else{
            holder.PlacesLayout.setVisibility(View.GONE);
            holder.DistanceLayout.setVisibility(View.VISIBLE);
            holder.TimeInRoad.setText(place.getTimeInRoad());
            holder.Distance.setText(place.getDistance());
        }

        if(color[position]==0) {
            convertView.setBackgroundColor(Color.parseColor("#E3E3E9"));
        }else
        if(color[position]==1) {
            convertView.setBackgroundColor(Color.LTGRAY);
        }

        return convertView;
    }

}
