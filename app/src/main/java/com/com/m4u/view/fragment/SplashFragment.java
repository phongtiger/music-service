package com.com.m4u.view.fragment;


import android.os.Handler;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.com.m4u.databinding.SplashFragmentBinding;
import com.com.m4u.view.base.BaseFragment;
import com.com.m4u.view.viewmodel.CommonViewModel;


public class SplashFragment extends BaseFragment<CommonViewModel, SplashFragmentBinding> {
    public static final String TAG = SplashFragment.class.getName();

    @Override
    protected Class<CommonViewModel> initViewModel() {
        return CommonViewModel.class;
    }

    @Override
    protected SplashFragmentBinding initViewBinding(LayoutInflater inflater, ViewGroup container) {
        return SplashFragmentBinding.inflate(inflater, container, false);
    }

    @Override
    protected void initView() {
        new Handler().postDelayed(() -> {
            eventUI.showFragment(AlbumFragment.TAG, null, false);
        }, 2000);
    }
}
