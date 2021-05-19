package com.cosmosrsvp.firebasedemo;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * Created by Belal on 4/17/2018.
 */

public class ArtistsAdapter extends RecyclerView.Adapter<ArtistsAdapter.ArtistViewHolder> {

    private Context mCtx;
    private List<Artist> artistList;

    public ArtistsAdapter(Context mCtx, List<Artist> artistList) {
        this.mCtx = mCtx;
        this.artistList = artistList;
    }


    @Override
    public ArtistViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mCtx).inflate(R.layout.recyclerview_artists, parent, false);
        return new ArtistViewHolder(view);
    }

    @Override
    public void onBindViewHolder( ArtistViewHolder holder, int position) {
        Artist artist = artistList.get(position);
        holder.textViewName.setText(artist.fullname);
        holder.textViewGenre.setText("Contact no: " + artist.contactno);
        holder.textViewAge.setText("Profession: " + artist.profession);
        holder.textViewCountry.setText("Country: " + artist.country);
        holder.profile.setImageBitmap(artist.pro);
    }


    @Override
    public int getItemCount() {
        return artistList.size();
    }

    class ArtistViewHolder extends RecyclerView.ViewHolder {

        TextView textViewName, textViewGenre, textViewAge, textViewCountry;
        ImageView profile;

        public ArtistViewHolder( View itemView) {
            super(itemView);

            textViewName =(TextView)itemView.findViewById(R.id.text_view_name);
            textViewGenre = (TextView)itemView.findViewById(R.id.text_view_genre);
            textViewAge = (TextView)itemView.findViewById(R.id.text_view_age);
            textViewCountry =(TextView) itemView.findViewById(R.id.text_view_country);
            profile=(ImageView)itemView.findViewById(R.id.profilepic);

        }
    }
}
