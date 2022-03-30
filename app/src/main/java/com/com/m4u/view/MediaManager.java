package com.com.m4u.view;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.com.m4u.view.model.Song;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MediaManager {
    public static final String KEY_READY_LIST = "KEY_READY_LIST";
    public static final String KEY_READY_PHOTO = "KEY_READY_PHOTO";
    public static final String KEY_PLAYING = "KEY_PLAYING";
    private static final MediaManager instance = new MediaManager();
    private static final String TAG = MediaManager.class.getName();
    private static final int STATE_IDLE = 0;
    private static final int STATE_PLAYING = 1;
    private static final int STATE_PAUSED = 2;
    private final List<Song> songList = new ArrayList<>();
    private final MediaPlayer player = new MediaPlayer();
    private int state = STATE_IDLE;
    private int currentIndex = 0;
    private Song currentSong;

    private MediaManager() {
        //do nothing
    }

    public static MediaManager getInstance() {
        return instance;
    }

    public List<Song> getSongList() {
        return songList;
    }

    public Bitmap createAlbumArt(final String filePath) {
        Bitmap bitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(App.getInstance(), Uri.fromFile(new File(filePath)));
            byte[] embedPic = retriever.getEmbeddedPicture();
            if (embedPic != null)
                bitmap = BitmapFactory.decodeByteArray(embedPic, 0, embedPic.length);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                retriever.release();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return bitmap;
    }

    public void loadSongOffline(OnMediaManagerCallBack callBack) {
        new Thread(() -> {
            if (songList.size() == 0) {
                Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                Cursor c = App.getInstance().getContentResolver()
                        .query(uri, null, null, null,
                                MediaStore.Audio.Media.TITLE + " ASC");
                if (c == null) return;
                c.moveToFirst();
                int indexTitle = c.getColumnIndex(MediaStore.Audio.Media.TITLE);
                int indexAlbum = c.getColumnIndex(MediaStore.Audio.Media.ALBUM);
                int indexPath = c.getColumnIndex(MediaStore.Audio.Media.DATA);
                int indexArtist = c.getColumnIndex(MediaStore.Audio.Media.ARTIST);
                int indexDuration = c.getColumnIndex(MediaStore.Audio.Media.DURATION);

                songList.clear();
                while (!c.isAfterLast()) {
                    String title = c.getString(indexTitle);
                    String album = c.getString(indexAlbum);
                    String path = c.getString(indexPath);
                    String artist = c.getString(indexArtist);
                    long duration = c.getLong(indexDuration);
                    if (duration > 180000 && path.endsWith(".mp3")) {
                        songList.add(new Song(title, path, album, artist));
                    }
                    c.moveToNext();
                }
                c.close();

                //for the first time!
                currentIndex = 0;
                if (songList.size() > 0) {
                    currentSong = songList.get(0);
                }
            }

            callBack.action(KEY_READY_LIST);
            Log.i(TAG, "loadSongOffline..." + songList.size());
            for (int i = 0; i < songList.size(); i++) {
                Song song = songList.get(i);
                if (song != null && song.path != null)
                    song.photo = createAlbumArt(song.path);
            }
            callBack.action(KEY_READY_PHOTO);
        }).start();
    }

    public void play(OnMediaManagerCallBack callBack) {
        try {
            currentSong = songList.get(currentIndex);
            if (state == STATE_IDLE) {
                player.reset();
                player.setDataSource(currentSong.path);
                player.prepare();
                player.start();
                state = STATE_PLAYING;
            } else if (state == STATE_PLAYING) {
                player.pause();
                state = STATE_PAUSED;
            } else if (state == STATE_PAUSED) {
                player.start();
                state = STATE_PLAYING;
            }
            callBack.action(KEY_PLAYING);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void pause() {
        try {
            player.pause();
            state = STATE_PLAYING;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isPlaying() {
        return state == STATE_PLAYING;
    }

    public void next(OnMediaManagerCallBack callBack) {
        currentIndex++;
        if (currentIndex >= songList.size()) {
            currentIndex = 0;
        }
        state = STATE_IDLE;
        play(callBack);
    }

    public void previous(OnMediaManagerCallBack callBack) {
        currentIndex--;
        if (currentIndex < 0) {
            currentIndex = songList.size() - 1;
        }
        state = STATE_IDLE;
        play(callBack);
    }

    public void stop() {
        player.reset();
        state = STATE_IDLE;
    }

    public Song getCurrentSong() {
        return currentSong;
    }

    public void setCurrentSong(Song song) {
        currentSong = song;
        currentIndex = songList.indexOf(currentSong);
    }

    public int getCurrentTime() {
        if (state == STATE_IDLE) return 0;
        return player.getCurrentPosition();
    }

    public int getTotalTime() {
        if (state == STATE_IDLE) return 0;
        return player.getDuration();
    }

    public String getCurrentTimeText() {
        if (state == STATE_IDLE) return "--:--";
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat df = new SimpleDateFormat("mm:ss");
        return df.format(new Date(player.getCurrentPosition()));
    }

    public String getTotalTimeText() {
        if (state == STATE_IDLE) return "--:--";
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat df = new SimpleDateFormat("mm:ss");
        return df.format(new Date(player.getDuration()));
    }

    public void forcePlay(OnMediaManagerCallBack callBack) {
        state = STATE_IDLE;
        play(callBack);
    }

    public void seek(int progress) {
        if (state == STATE_IDLE) return;
        player.seekTo(progress);
    }

    public interface OnMediaManagerCallBack {
        void action(String key);
    }
}



