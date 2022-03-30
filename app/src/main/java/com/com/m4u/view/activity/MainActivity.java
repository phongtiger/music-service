package com.com.m4u.view.activity;


import android.content.Intent;

import com.com.m4u.databinding.ActivityMainBinding;
import com.com.m4u.view.base.BaseActivity;
import com.com.m4u.view.fragment.SplashFragment;
import com.com.m4u.view.service.MediaService;
import com.com.m4u.view.viewmodel.MainViewModel;

public class MainActivity extends BaseActivity<MainViewModel, ActivityMainBinding> {

    @Override
    protected Class<MainViewModel> initViewModel() {
        return MainViewModel.class;
    }

    @Override
    protected void initViews() {
        showFragment(SplashFragment.TAG, null, false);
    }

    @Override
    protected ActivityMainBinding initViewBinding() {
        return ActivityMainBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        startService(new Intent(this, MediaService.class));
    }

    @Override
    public void closeApp() {
        finish();
    }
}