package com.hudou.autotest.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.hudou.autotest.R;
import com.hudou.autotest.base.activity.BaseMainActivity;

import java.util.List;

public class MyPagerAdapter extends PagerAdapter {
    private final FragmentManager fragmentManager;
    private final List<Fragment> fragmentList;
    private final Context mContext;
    private final String[] pageTitles = BaseMainActivity.pageTitlesList.toArray(new String[0]);

    public MyPagerAdapter(Context mContext, List<Fragment> fragmentList, FragmentManager fragmentManager){
        this.mContext = mContext;
        this.fragmentList = fragmentList;
        this.fragmentManager = fragmentManager;
    }

    @Override
    public int getCount() {
        return pageTitles.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        if (position < fragmentList.size()){
            Fragment fragment = fragmentList.get(position);
            if (!fragment.isAdded()) {
                fragmentManager.beginTransaction()
                        .add(((ViewPager) container).getId(), fragment)
                        .commit();
                fragmentManager.executePendingTransactions();

            }
            return fragment.requireView();
        } else {
            View view = LayoutInflater.from(container.getContext()).inflate(R.layout.auto_test_options_fragment, container, false);
            container.addView(view);
            return view;
        }
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        if (position < fragmentList.size()) {
            Fragment fragment = fragmentList.get(position);
            if (fragment.isAdded()) {
                fragmentManager.beginTransaction()
                        .remove(fragment)
                        .commit();
                fragmentManager.executePendingTransactions();
            }
        } else {
            if (object instanceof View) {
                container.removeView((View) object);
            }
        }
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return pageTitles[position];
    }

    public Fragment getItem(int position){
        return fragmentList.get(position);
    }
}