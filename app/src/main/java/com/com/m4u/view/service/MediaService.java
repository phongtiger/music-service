package com.com.m4u.view.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.com.m4u.R;
import com.com.m4u.view.MediaManager;
import com.com.m4u.view.model.Song;

public class MediaService extends Service {
    public static final String TAG = MediaService.class.getName();
    private static final String CHANNEL_ID = "m4u_channel";
    private static final String KEY_EVENT = "KEY_EVENT";
    private static final String PLAY_EVENT = "PLAY_EVENT";
    private static final String NEXT_EVENT = "NEXT_EVENT";
    private static final String BACK_EVENT = "BACK_EVENT";
    private Song song;
    private RemoteViews views;
    private boolean appRunning = false;
    private Notification notify;
    private final Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {
            Log.i(TAG, "da vao day" + song.title);
            song = MediaManager.getInstance().getCurrentSong();
            views = new RemoteViews(getPackageName(), R.layout.item_notify_media);
            views.setTextViewText(R.id.tv_song_notice, song.title);
            views.setTextViewText(R.id.tv_album_notice, song.album);

            String currentTimeText = MediaManager.getInstance().getCurrentTimeText();
            String totalTimeText = MediaManager.getInstance().getTotalTimeText();
            int currentTime = MediaManager.getInstance().getCurrentTime();
            int totalTime = MediaManager.getInstance().getTotalTime();
            views.setProgressBar(R.id.seekbar, totalTime, currentTime, false);
            views.setTextViewText(R.id.tv_duration, String.format("%s/%s", currentTimeText, totalTimeText));
            if (MediaManager.getInstance().isPlaying()) {
                views.setImageViewResource(R.id.iv_play_n, R.drawable.ic_baseline_play);
            } else {
                views.setImageViewResource(R.id.iv_play_n, R.drawable.ic_pause);
            }
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.notify(10001, notify);
            return false;
        }
    });

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        song = MediaManager.getInstance().getCurrentSong();
        createNotificationChannel();

        Log.i(TAG, "onCreate on service");
        views = new RemoteViews(getPackageName(), R.layout.item_notify_media);
        views.setTextViewText(R.id.tv_song_notice, song.title);
        views.setTextViewText(R.id.tv_album_notice, song.album);

        Intent intentPlay = new Intent(this, MediaService.class);
        intentPlay.putExtra(KEY_EVENT, PLAY_EVENT);
        @SuppressLint("UnspecifiedImmutableFlag")
        PendingIntent playI = PendingIntent.getService(this, 105, intentPlay, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.iv_play_n, playI);

        Intent intentBack = new Intent(this, MediaService.class);
        intentBack.putExtra(KEY_EVENT, BACK_EVENT);
        @SuppressLint("UnspecifiedImmutableFlag")
        PendingIntent playB = PendingIntent.getService(this, 106, intentBack, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.iv_back, playB);

        Intent intentNext = new Intent(this, MediaService.class);
        intentNext.putExtra(KEY_EVENT, NEXT_EVENT);
        @SuppressLint("UnspecifiedImmutableFlag")
        PendingIntent playN = PendingIntent.getService(this, 107, intentNext, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.iv_next, playN);

        appRunning = true;
        new Thread(this::updateSeekBar).start();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID);
        builder.setSmallIcon(R.drawable.ic_app_icon)
                .setAutoCancel(false)
                .setChannelId(CHANNEL_ID)
                .setCustomBigContentView(views)
                .setOngoing(true)
                .setOnlyAlertOnce(true)
                .setTicker("hello");
        notify = builder.build();
        startForeground(10001, notify);
    }

    private void updateSeekBar() {
        while (appRunning) {
            try {
                Thread.sleep(500);
                Message.obtain(handler).sendToTarget();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    private void createNotificationChannel() {
        String des = "Enjoy music";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_ID, importance);
            channel.setDescription(des);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String key = intent.getStringExtra(KEY_EVENT);
            if (key != null && key.equals(PLAY_EVENT)) {
                MediaManager.getInstance().play(key2 -> {
                });
                Message.obtain(handler).sendToTarget();
            }
            if (key != null && key.equals(NEXT_EVENT)) {
                MediaManager.getInstance().next(key2 -> {
                });
                Message.obtain(handler).sendToTarget();
            }
            if (key != null && key.equals(BACK_EVENT)) {
                MediaManager.getInstance().previous(key2 -> {
                });
                Message.obtain(handler).sendToTarget();

            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        appRunning = false;
        super.onDestroy();
    }
}
