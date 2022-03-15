package com.com.m4u.view.base;

import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewbinding.ViewBinding;

import com.com.m4u.view.OnUpdateUI;
import com.com.m4u.R;

import java.lang.reflect.Constructor;

public abstract class BaseActivity<V extends ViewModel, B extends ViewBinding>
        extends AppCompatActivity implements View.OnClickListener, OnUpdateUI {
    protected V mModel;
    protected B mBinding;

    @Override
    protected final void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = initViewBinding();
        mModel = new ViewModelProvider(this).get(initViewModel());
        setContentView(mBinding.getRoot());
        initViews();
    }

    protected abstract Class<V> initViewModel();

    protected abstract void initViews();

    protected abstract B initViewBinding();

    @Override
    public void onClick(View v) {
        v.startAnimation(AnimationUtils.loadAnimation(this, androidx.appcompat.R.anim.abc_fade_in));
        //do nothing
    }

    protected final void notify(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    protected final void notify(int msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showFragment(String tag, Object data, boolean isBacked) {
        // Tag tao ra baseFragment;
        try {
            Class<?> clazz = Class.forName(tag);
            Constructor<?> constructor = clazz.getConstructor();
            BaseFragment<?, ?> baseFragment = (BaseFragment<?, ?>) constructor.newInstance();
            baseFragment.setEventUI(this);
            baseFragment.setData(data);
            FragmentTransaction trans = getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.ln_main, baseFragment, tag);
            if (isBacked) {
                trans.addToBackStack(null);
            }
            trans.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateUI(Runnable runnable) {
        runOnUiThread(runnable);
    }
}

