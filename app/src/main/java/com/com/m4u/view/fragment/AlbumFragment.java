package com.com.m4u.view.fragment;


import android.Manifest;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.app.ActivityCompat;

import com.com.m4u.R;
import com.com.m4u.database.Favorite;
import com.com.m4u.databinding.AlbumFragmentBinding;
import com.com.m4u.view.App;
import com.com.m4u.view.MediaManager;
import com.com.m4u.view.adapter.SongAdapter;
import com.com.m4u.view.base.BaseFragment;
import com.com.m4u.view.model.Song;
import com.com.m4u.view.viewmodel.CommonViewModel;

import java.util.ArrayList;
import java.util.List;


public class AlbumFragment extends BaseFragment<CommonViewModel, AlbumFragmentBinding> {
    public static final String TAG = AlbumFragment.class.getName();

    @Override
    protected Class<CommonViewModel> initViewModel() {
        return CommonViewModel.class;
    }

    @Override
    protected AlbumFragmentBinding initViewBinding(LayoutInflater inflater, ViewGroup container) {
        return AlbumFragmentBinding.inflate(inflater, container, false);
    }

    @Override
    protected void initView() {
       checkPermission();
       mBinding.tvAllSong.setOnClickListener(this);
       mBinding.tvFavorite.setOnClickListener(this);
    }

    @Override
    protected void clickView(View view) {
        if (view.getId() == R.id.tv_all_song) {
            showAllSong();
        } else if (view.getId() == R.id.tv_favorite) {
            showFavorite();
        }
    }

    private void showFavorite() {
        mBinding.vAllSong.setVisibility(View.GONE);
        mBinding.vFavorite.setVisibility(View.VISIBLE);
        new Thread(() -> {
            List<Favorite> lstFavorite = App.getInstance().getDb().getSongDAO().getAllFavorites();
            List<Song> listSong = new ArrayList<>();
            for (Favorite favorite : lstFavorite) {
                Song song = new Song(favorite.title, favorite.path, favorite.album, null);
                song.photo = MediaManager.getInstance().createAlbumArt(song.path);
                listSong.add(song);
            }
            eventUI.updateUI(() -> mBinding.rvSong.setAdapter(new SongAdapter(listSong,
                    eventUI, true, view -> {
                showPlaying(listSong, view.getTag());
            }, mContext)));
        }).start();
    }

    private void checkPermission() {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE
            }, 101);
        } else {
            loadSongOffline();
        }
    }

    private void loadSongOffline() {
        MediaManager.getInstance().loadSongOffline(key -> {
            eventUI.updateUI(() ->{
                if (key.equals(MediaManager.KEY_READY_LIST)) {
                    showAllSong();
                } else {
                    SongAdapter adapter = (SongAdapter) mBinding.rvSong.getAdapter();
                    assert adapter != null;
                    adapter.updateSongs(MediaManager.getInstance().getSongList());
                }
            });
        });
    }

    private void showAllSong() {
        mBinding.vAllSong.setVisibility(View.VISIBLE);
        mBinding.vFavorite.setVisibility(View.GONE);
        mBinding.rvSong.setAdapter(new SongAdapter(MediaManager.getInstance().getSongList(), eventUI,
                false, v -> { showPlaying(MediaManager.getInstance().getSongList(),
                v.getTag()); }, mContext)
        );
    }

    private void showPlaying(List<Song> songList, Object tag) {
        App.getInstance().getStorage().setLstSong(songList);
        App.getInstance().getStorage().setCurrentSong((Song) tag);
        eventUI.showFragment(PlayingFragment.TAG, null, true);
    }
}
