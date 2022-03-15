package com.com.m4u.view.base;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewbinding.ViewBinding;

import com.com.m4u.view.OnUpdateUI;
import com.com.m4u.view.viewmodel.BaseViewModel;


public abstract class BaseFragment<V extends BaseViewModel, B extends ViewBinding>
        extends Fragment implements View.OnClickListener {
    protected Object data;
    protected Context mContext;
    protected V mModel;
    protected OnUpdateUI eventUI;
    protected B mBinding;

    public void setData(Object data) {
        this.data = data;
    }

    public void setEventUI(OnUpdateUI eventUI) {
        this.eventUI = eventUI;
    }


    @Override
    public final void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    @Nullable
    @Override
    public final View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = initViewBinding(inflater, container);
        mModel = new ViewModelProvider(this).get(initViewModel());
        initView();
        return mBinding.getRoot();
    }

    protected abstract Class<V> initViewModel();

    protected abstract B initViewBinding(LayoutInflater inflater, ViewGroup container);

    @Override
    public final void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }

    protected abstract void initView();

    @Override
    public final void onClick(View view) {
        view.startAnimation(AnimationUtils.loadAnimation(mContext, androidx.appcompat.R.anim.abc_fade_in));
        clickView(view);
    }

    protected void clickView(View view) {

    }

    protected void notify(String text) {
        Toast.makeText(mContext, text, Toast.LENGTH_SHORT).show();
    }

    protected int getLayout() {
        return 0;
    }

}
