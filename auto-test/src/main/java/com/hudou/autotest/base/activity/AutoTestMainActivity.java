package com.hudou.autotest.base.activity;

import static com.hudou.autotest.constant.FragmentTag.EXECUTION_DETAIL_TAG;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.tabs.TabLayout;
import com.hudou.autotest.BuildConfig;
import com.hudou.autotest.R;
import com.hudou.autotest.adapter.MyPagerAdapter;
import com.hudou.autotest.adapter.MyViewPager;
import com.hudou.autotest.annotation.Navigation;
import com.hudou.autotest.base.fragment.BaseFragment;
import com.hudou.autotest.base.item.BaseTestCase;
import com.hudou.autotest.constant.Config;
import com.hudou.autotest.constant.ResultData;
import com.hudou.autotest.constant.ResultItem;
import com.hudou.autotest.constant.ShowMessage;
import com.hudou.autotest.database.db.AppDatabase;
import com.hudou.autotest.database.entity.ResultDataEntity;
import com.hudou.autotest.database.entity.ResultItemEntity;
import com.hudou.autotest.fragment.ExecutionDetailsFragment;
import com.hudou.autotest.fragment.ExecutionFragment;
import com.hudou.autotest.fragment.HomeFragment;
import com.hudou.autotest.fragment.OptionsFragment;
import com.hudou.autotest.util.ATLoggerUtils;
import com.hudou.autotest.util.ReflectionUtils;
import com.hudou.autotest.util.SharedPreferencesUtil;
import com.hudou.autotest.util.SynchronizedMutableLiveData;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class AutoTestMainActivity extends AppCompatActivity implements AutoTestInterface {
    @SuppressLint("StaticFieldLeak")
    public static LinearLayout llMessage;
    private static SynchronizedMutableLiveData<ShowMessage> mShowMessage = new SynchronizedMutableLiveData<>();
    public static final ArrayList<String> pageTitlesList = new ArrayList<>();
    private static boolean isFirst = true;
    private static List<Fragment> finalFragmentList;
    public static CopyOnWriteArrayList<ResultItem> resultItemList = new CopyOnWriteArrayList<>();
    public static ResultData resultData;
    private static Context mContext;
    public static FileOutputStream fos;
    private static AppDatabase db;

    public static Context getContext() {
        return mContext;
    }

    public static AppDatabase getDb() {
        return db;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        ATLoggerUtils.configPrint(true);
        db = AppDatabase.getInstance(mContext);
        new Thread(() -> {
            List<ResultItemEntity> items = AutoTestMainActivity.getDb().dao().getAllResultItems();
            List<ResultItem> temp = new ArrayList<>();
            for (ResultItemEntity ie : items) {
                try {
                    Class<? extends BaseTestCase> clz =
                            (Class<? extends BaseTestCase>) Class.forName(ie.className);
                    CopyOnWriteArrayList<ResultData> rdList = new CopyOnWriteArrayList<>();
                    for (ResultDataEntity de : AutoTestMainActivity.getDb().dao()
                            .getDataForItem(ie.className)) {
                        ResultData d = new ResultData();
                        d.setId(de.caseName);
                        d.setTestCaseName(de.methodName);
                        d.setResult(de.result);
                        d.setChineseDescription(de.chineseDescription);
                        d.setEnglishDescription(de.englishDescription);
                        d.setDetail(de.detail);
                        rdList.add(d);
                    }
                    ResultItem ri = new ResultItem(clz, rdList);
                    ri.setStartTime(ie.startTime);
                    ri.setEndTime(ie.endTime);
                    // 把标志位也同步
                    try {
                        Field f = ResultItem.class.getDeclaredField("isStartTimeSet");
                        f.setAccessible(true);
                        f.setBoolean(ri, ie.isStartTimeSet);
                    } catch (Exception ignore) {
                    }
                    temp.add(ri);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            // 写回主内存
            resultItemList.clear();
            resultItemList.addAll(temp);
        }).start();

        SharedPreferencesUtil.init(getApplicationContext());
        setContentView(R.layout.auto_test_activity_main);

        if (isFirst) {
            List<Fragment> fragmentList = new ArrayList<>();
            finalFragmentList = new ArrayList<>();
            fragmentList.add(new HomeFragment());
            addNavigationFragment(fragmentList);
            for (Fragment fragment : fragmentList) {
                Class<? extends Fragment> cls = fragment.getClass();
                if (cls.isAnnotationPresent(Navigation.class)) {
                    finalFragmentList.add(fragment);
                    pageTitlesList.add(ReflectionUtils.getAnnotationValue(cls, Navigation.class, Navigation.Members.name));
                }
            }
            isFirst = false;
        }

        TabLayout tabLayout = findViewById(R.id.tab_layout);
        MyViewPager viewPager = findViewById(R.id.viewPager);

        viewPager.setOffscreenPageLimit(getFragments().size()); // 设置为0，不预加载任何页面，实际上没有效果
        MyPagerAdapter pagerAdapter = new MyPagerAdapter(getApplicationContext(), getFragments(), getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                // 根据当前页面位置加载数据
                Fragment fragment = pagerAdapter.getItem(position);
                if (fragment instanceof BaseFragment) {
                    ((BaseFragment<?>) fragment).onFragmentVisibility();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition(), false);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
        mShowMessage.observe(this, message -> setLlMessage(message.getColor(), message.getMessage()));

        String fileName = ReflectionUtils.getConfig(Config.REPORT_PATH);
        File file = new File(fileName);
        if (!file.exists()) {
            file.mkdirs();
        }
        try {
            fos = new FileOutputStream(fileName + ReflectionUtils.getConfig(Config.TXT_REPORT_NAME));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferencesUtil.save(SharedPreferencesUtil.IS_PHYSICAL_KEYBOARD, isPhysicalKeyboard());
    }

    private void setLlMessage(int color, String message) {
        runOnUiThread(() -> {
            TextView textView = new TextView(this);
            textView.setText(message);
            textView.setTextColor(color);
            llMessage.addView(textView, 0);
        });
    }

    public static SynchronizedMutableLiveData<ShowMessage> getRecorder() {
        return mShowMessage;
    }

    public static void recordMessage(int color, String message) throws InterruptedException, IOException {
        getRecorder().synchronizedPostValue(new ShowMessage(color, message));
        if (SharedPreferencesUtil.get(SharedPreferencesUtil.DEBUG_MODE, true)) {
            resultData.appendDetail("\n" + message);
            fos.write((message + "\n").getBytes());
        }
    }

    private List<Fragment> getFragments() {
        return finalFragmentList;
    }

    public abstract void addNavigationFragment(List<Fragment> list);

    private int getWindowWidth() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;
    }

    public String getAuthorName() {
        return BuildConfig.Author;
    }

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
            if (exitAble) {
                new MaterialAlertDialogBuilder(AutoTestMainActivity.this)
                        .setTitle(R.string.exit_application_title)
                        .setPositiveButton(R.string.sure, (dialog, which) -> finish())
                        .setNegativeButton(R.string.cancel, (dialog, which) -> {
                        })
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferencesUtil.clear();
        try {
            fos.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

            } else {
                // 权限被拒绝，提示用户
                Toast.makeText(this, R.string.permission_can_not_accept, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean isPhysicalKeyboard() {
        return false;
    }

}
