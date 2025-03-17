package com.hudou.autotest.base.activity;

import static com.hudou.autotest.constant.FragmentTag.EXECUTION_DETAIL_TAG;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.tabs.TabLayout;
import com.hudou.autotest.R;
import com.hudou.autotest.annotation.Navigation;
import com.hudou.autotest.constant.ShowMessage;
import com.hudou.autotest.fragment.ExecutionDetailsFragment;
import com.hudou.autotest.fragment.ExecutionFragment;
import com.hudou.autotest.fragment.HomeFragment;
import com.hudou.autotest.adapter.MyPagerAdapter;
import com.hudou.autotest.fragment.OptionsFragment;
import com.hudou.autotest.util.ReflectionUtils;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseMainActivity extends AppCompatActivity {
    @SuppressLint("StaticFieldLeak")
    public static LinearLayout llMessage;
    public static MutableLiveData<ShowMessage> mShowMessage = new MutableLiveData<>();
    public static final ArrayList<String> pageTitlesList = new ArrayList<>();
    private static boolean isFirst = true;
    private static List<Fragment> finalFragmentList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.auto_test_activity_main);

        if (isFirst) {
            List<Fragment> fragmentList = new ArrayList<>();
            finalFragmentList = new ArrayList<>();
            fragmentList.add(new HomeFragment());
            addNavFragment(fragmentList);
            for (Fragment fragment : fragmentList) {
                Class<? extends Fragment> cls = fragment.getClass();
                if (cls.isAnnotationPresent(Navigation.class)) {
                    finalFragmentList.add(fragment);
                    pageTitlesList.add(ReflectionUtils.getAnnotationValue(cls, Navigation.class, "name"));
                }
            }
            isFirst = false;
        }

        TabLayout tabLayout = findViewById(R.id.tab_layout);
        ViewPager viewPager = findViewById(R.id.viewPager);

        viewPager.setAdapter(new MyPagerAdapter(getApplicationContext(), getFragments(), getSupportFragmentManager()));
        tabLayout.setupWithViewPager(viewPager);
        mShowMessage.observe(this, message -> setLlMessage(message.getColor(), message.getMessage()));
    }

    private void setLlMessage(int color, String message){
        runOnUiThread(() -> {
            TextView textView = new TextView(this);
            textView.setText(message);
            textView.setTextColor(color);
            llMessage.addView(textView, 0);
        });
    }

    private List<Fragment> getFragments(){
        return finalFragmentList;
    }



    public abstract void addNavFragment(List<Fragment> list);

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            boolean exitAble = true;
            for (Fragment fragment : getSupportFragmentManager().getFragments()) {
                if ((!(fragment instanceof OptionsFragment) &&
                        !(fragment instanceof ExecutionFragment) &&
                        !(fragment instanceof ExecutionDetailsFragment))) {
                    continue;
                }
                exitAble = false;
            }
            if (exitAble){
                new MaterialAlertDialogBuilder(BaseMainActivity.this)
                        .setTitle(R.string.exit_application_title)
                        .setPositiveButton(R.string.sure, (dialog, which) -> finish())
                        .setNegativeButton(R.string.cancel, (dialog, which) -> {})
                        .show();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            ExecutionDetailsFragment fragment = (ExecutionDetailsFragment) getSupportFragmentManager().findFragmentByTag(EXECUTION_DETAIL_TAG);
            if (fragment != null) {
                fragment.onBackPressedLongPress();
            }
            return true;
        }
        return super.onKeyLongPress(keyCode, event);
    }


}
