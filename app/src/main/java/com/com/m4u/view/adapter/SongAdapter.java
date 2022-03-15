package com.com.m4u.view.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.com.m4u.R;
import com.com.m4u.view.App;
import com.com.m4u.view.OnUpdateUI;
import com.com.m4u.view.model.Song;

import java.util.List;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongHolder> {
    private final Context context;
    private final boolean isTypeFavorite;
    private final OnUpdateUI updateUI;
    public final View.OnClickListener eventClick;
    private List<Song> songList;

    public SongAdapter(List<Song> songList, OnUpdateUI updateUI,
                       boolean isTypeFavorite,
                       View.OnClickListener eventClick,
                       Context context) {
        this.context = context;
        this.isTypeFavorite = isTypeFavorite;
        this.updateUI = updateUI;
        this.eventClick = eventClick;
        this.songList = songList;
    }

    @NonNull
    @Override
    public SongHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_song, parent, false);
        return new SongHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SongHolder holder, int position) {
        Song song = songList.get(position);
        if(song == null) return;
        if (song.photo != null) {
            holder.ivSong.setImageBitmap(song.photo);
        } else {
            holder.ivSong.setImageResource(R.drawable.ic_song);
        }
        holder.tvAlbum.setText(String.format("%s,%s", song.album, song.artist));
        holder.tvSong.setText(song.title);
        holder.trSong.setTag(song);
    }

    @Override
    public int getItemCount() {
        return songList.size();
    }

    public void updateSongs(List<Song> songList) {
        this.songList = songList;
        notifyItemRangeChanged(0, songList.size());
    }

    public class SongHolder extends RecyclerView.ViewHolder {
        TableRow trSong;
        ImageView ivSong;
        TextView tvSong, tvAlbum;

        public SongHolder(@NonNull View v) {
            super(v);
            trSong = v.findViewById(R.id.tr_song);
            ivSong = v.findViewById(R.id.iv_song);
            tvSong = v.findViewById(R.id.tv_song);
            tvAlbum = v.findViewById(R.id.tv_album);
            trSong.setOnClickListener(eventClick);
            trSong.setOnLongClickListener(view -> {
                showDialog((Song) view.getTag());
                return true;
            });
        }
    }

    private void showDialog(Song song) {
        AlertDialog alert = new AlertDialog.Builder(context).create();
        alert.setMessage(isTypeFavorite ? "Remove form Favorite list" : "Add to favorite");
        alert.setButton(AlertDialog.BUTTON_POSITIVE, "Ok", (dialogInterface, i) ->{
            if (!isTypeFavorite) {
                addToFavorite(song);
            } else {
                remoteFavorite(song);
            }
        });
        alert.show();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void remoteFavorite(Song song) {
        new Thread(() ->{
            App.getInstance().getDb().getSongDAO().deleteFavorite(song.path);
            updateUI.updateUI(() ->{
                songList.remove(song);
                notifyDataSetChanged();
            });
        }).start();
    }

    private void addToFavorite(Song song) {
        new Thread(() ->{
            App.getInstance().getDb().getSongDAO().addFavorite(song.toFavorite());
            updateUI.updateUI(() -> Toast.makeText(context, "Song is add", Toast.LENGTH_SHORT).show());
        }).start();
    }
}
