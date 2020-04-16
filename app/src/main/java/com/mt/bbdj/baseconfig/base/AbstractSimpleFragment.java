package com.mt.bbdj.baseconfig.base;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import me.yokeyword.fragmentation.SupportFragment;

/**
 * Author : ZSK
 * Date : 2019/5/22
 * Description :
 */
public abstract class AbstractSimpleFragment extends SupportFragment {

    private Unbinder unBinder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutId(), container, false);
        unBinder = ButterKnife.bind(this, view);
        Log.d("tag+++onCreateView",setLazyLoad()+"");
        //正常加载这里
        onNormalInitView();
        return view;
    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
        Log.d("tag+++LazyInit",setLazyLoad()+"");
        //懒加载这里
        if (setLazyLoad()) {
            initParams();
            initData();
            initListener();
        }
    }

    protected abstract int getLayoutId();

    protected abstract void initParams();

    protected abstract void initData();

    protected boolean setLazyLoad() {
        return true;
    }


    protected void initListener() {

    }

    protected void onNormalInitView() {
        Log.d("tag+++NormalInit",setLazyLoad()+"");
        if (!setLazyLoad()) {
            initParams();
            initData();
            initListener();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unBinder.unbind();
    }

}
