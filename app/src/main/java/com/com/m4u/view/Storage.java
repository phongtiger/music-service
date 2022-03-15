package com.com.m4u.view;

import com.com.m4u.view.model.Song;

import java.util.List;

public class Storage {
    public List<Song> lstSong;
    public Song currentSong;

    public void setLstSong(List<Song> lstSong) {
        this.lstSong = lstSong;
    }

    public void setCurrentSong(Song currentSong) {
        this.currentSong = currentSong;
    }
}
