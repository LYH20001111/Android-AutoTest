package com.newland.autotest.base.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewbinding.ViewBinding;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class BaseFragment<VB extends ViewBinding/*, VM extends ViewModel*/> extends Fragment {
    public VB viewBinding;
    //public VM viewModel;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return getView(inflater, container, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        viewBinding.getRoot().setClickable(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
        initActionAfterInitData();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    protected View getView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        try {
            Class cls = getClass();
            Type type = cls.getGenericSuperclass();
            while (type != null && cls != null && !(type instanceof ParameterizedType)) {
                cls = getClass().getSuperclass();
                if (cls != null) {
                    type = cls.getGenericSuperclass();
                }

            }
            Class<VB> vbClass = (Class<VB>) ((ParameterizedType) type).getActualTypeArguments()[0];
            Method method = vbClass.getDeclaredMethod("inflate", LayoutInflater.class, ViewGroup.class, boolean.class);
            viewBinding = (VB) method.invoke(this, inflater, container, false);
            return viewBinding.getRoot();
        } catch (Exception e) {
            throw new RuntimeException("create viewBinding exception:" + e.getMessage(), e);
        }
    }

    protected abstract void initData();
    protected abstract void initActionAfterInitData();


}
