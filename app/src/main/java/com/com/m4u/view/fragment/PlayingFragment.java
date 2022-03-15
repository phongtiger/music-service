package com.com.m4u.view.fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.com.m4u.R;
import com.com.m4u.databinding.PlayingFragmentBinding;
import com.com.m4u.view.App;
import com.com.m4u.view.MediaManager;
import com.com.m4u.view.OnSeekBarSimpleChangeListener;
import com.com.m4u.view.base.BaseFragment;
import com.com.m4u.view.model.Song;
import com.com.m4u.view.viewmodel.CommonViewModel;

public class PlayingFragment extends BaseFragment<CommonViewModel, PlayingFragmentBinding> {
    public static final String TAG = PlayingFragment.class.getName();

    private boolean isRunning = false;

    @Override
    protected Class<CommonViewModel> initViewModel() {
        return CommonViewModel.class;
    }

    @Override
    protected PlayingFragmentBinding initViewBinding(LayoutInflater inflater, ViewGroup container) {
        return PlayingFragmentBinding.inflate(inflater, container, false);
    }

    @Override
    protected void initView() {
        mBinding.ivPlay.setImageResource(R.drawable.ic_pause);
        mBinding.ivPlay.setOnClickListener(this);
        mBinding.ivBack.setOnClickListener(this);
        mBinding.ivNext.setOnClickListener(this);
        mBinding.seekbar.setOnSeekBarChangeListener((OnSeekBarSimpleChangeListener) seekbar -> {
            MediaManager.getInstance().seek(seekbar.getProgress());
        });
        if (MediaManager.getInstance().isPlaying()) {
            MediaManager.getInstance().stop();
        }
        Song s = App.getInstance().getStorage().currentSong;
        MediaManager.getInstance().setCurrentSong(s);
        startRolling();
        MediaManager.getInstance().play(key -> updateUi(s));
    }

    private void updateUi(Song s) {
        mBinding.tvSong.setText(s.title);
        mBinding.tvAlbum.setText(String.format("%s,%s", s.album, s.artist));
        if (s.photo != null) {
            mBinding.ivCover.setImageBitmap(s.photo);
        } else {
            mBinding.ivCover.setImageResource(R.drawable.ic_song);
        }
        mBinding.ivPlay.setImageLevel(MediaManager.getInstance().isPlaying() ? 1 : 0);
    }

    private void startRolling() {
        isRunning = true;
        new Thread(() -> {
            while (isRunning) {
                try {
                    Thread.sleep(200);
                    eventUI.updateUI(() -> {
                        mBinding.ivCover.setRotation(mBinding.ivCover.getRotation() + 0.05f);
                        mBinding.tvCurrentTime.setText(MediaManager.getInstance().getCurrentTimeText());
                        mBinding.tvTotalTime.setText(MediaManager.getInstance().getCurrentTimeText());
                        mBinding.tvTotalTime.setText(MediaManager.getInstance().getCurrentTimeText());
                        mBinding.seekbar.setProgress(MediaManager.getInstance().getCurrentTime());
                        mBinding.seekbar.setMax(MediaManager.getInstance().getTotalTime());
                    });
                } catch (InterruptedException e) {

                }
            }
        }).start();
    }

    @Override
    protected void clickView(View view) {
        if (view.getId() == R.id.iv_back) {
            MediaManager.getInstance().previous(key -> updateUi(MediaManager.getInstance().getCurrentSong()));
        } else if (view.getId() == R.id.iv_next) {
            MediaManager.getInstance().next(key -> updateUi(MediaManager.getInstance().getCurrentSong()));
        } else if (view.getId() == R.id.iv_play) {
                MediaManager.getInstance().play(key -> {
                    mBinding.ivPlay.setImageLevel(MediaManager.getInstance().isPlaying() ? 1 : 0);
                });
            if (MediaManager.getInstance().isPlaying()) {
                mBinding.ivPlay.setImageResource(R.drawable.ic_pause);
            } else {
                mBinding.ivPlay.setImageResource(R.drawable.ic_baseline_play);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isRunning = false;
    }
}
