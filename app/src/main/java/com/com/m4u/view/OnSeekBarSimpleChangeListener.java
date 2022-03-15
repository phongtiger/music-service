package com.com.m4u.view;

import android.widget.SeekBar;

public interface OnSeekBarSimpleChangeListener extends SeekBar.OnSeekBarChangeListener {
    default void onProgressChanged(SeekBar seekBar, int i, boolean b) {

    }

    default void onStartTrackingTouch(SeekBar seekBar) {

    }
}
