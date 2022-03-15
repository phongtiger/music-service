package com.com.m4u.view;

public interface OnUpdateUI {
    void updateUI(Runnable runnable);

    void closeApp();

    void showFragment(String tag, Object data, boolean isBack);

}
