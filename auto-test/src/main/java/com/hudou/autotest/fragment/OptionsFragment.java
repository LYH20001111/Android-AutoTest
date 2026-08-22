package com.hudou.autotest.fragment;

import static com.hudou.autotest.constant.FragmentTag.EXECUTION_DETAIL_TAG;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import com.hudou.autotest.R;
import com.hudou.autotest.annotation.TestItem;
import com.hudou.autotest.base.fragment.BaseFragment;
import com.hudou.autotest.base.item.BaseTestCase;
import com.hudou.autotest.databinding.AutoTestOptionsFragmentBinding;
import com.hudou.autotest.ui.keyboard.NumberKeyBoardView;
import com.hudou.autotest.util.DeviceUtils;
import com.hudou.autotest.util.ReflectionUtils;
import com.hudou.autotest.util.SharedPreferencesUtil;

public class OptionsFragment extends BaseFragment<AutoTestOptionsFragmentBinding> {
    private final Class<? extends BaseTestCase> clz;
    private final BaseTestCase testItem;
    public static final int INVALID_VALUE = -1;

    public OptionsFragment(Class<? extends BaseTestCase> clz) {
        this.clz = clz;
        this.testItem = (BaseTestCase) ReflectionUtils.createInstance(this.clz);
    }

    public interface Option {
        String RUN_ALL_CASES = "1";
        String RUN_ONE_CASE = "2";
        String RUN_PART_CASES = "3";
        String RUN_PART_NONCONTINUOUS_CASES = "4";
        String VIEW_ALL_CASES = "5";
        String VIEW_ABANDON_CASES = "6";
        String VIEW_UNEXECUTED_CASES = "7";
        String VIEW_FAILED_CASES = "8";
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onInitData() {
        viewBinding.tvOptions.setText(String.format("当前测试项 ： %s" +
                        "\n" +
                        "\n " +
                        "1. 运行所有案例 \n " +
                        "2. 运行某个案例 \n " +
                        "3. 运行部分连续案例 \n " +
                        "4. 运行部分不连续案例 \n\n " +
                        "5. 查看所有案例详情(%d) \n " +
                        "6. 查看废弃案例详情(%d) \n " +
                        "7. 查看未执行案例详情(%d) \n " +
                        "8. 查看失败案例详情(%d)",
                ReflectionUtils.getAnnotationValue(clz, TestItem.class, TestItem.Members.name),
                testItem.testItemCasesNum(clz),
                testItem.testItemAbandonCasesNum(clz),
                testItem.testItemNoExecutedCasesNum(clz),
                testItem.testItemFailedCasesNum(clz)));
        try {
            testItem.onItemStart();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 当前设备命中测试项不支持设备列表时，进入选项页即弹窗提示不适用
        if (isItemUnsupportedOnCurrentDevice()) {
            Dialog.notifyDialog(getContext(), buildItemUnsupportedMessage(R.string.test_item_unsupported_device_entry));
        }
    }

    /**
     * 判断当前设备是否命中该测试项注解声明的不支持设备列表
     */
    private boolean isItemUnsupportedOnCurrentDevice() {
        TestItem testItemAnnotation = clz.getAnnotation(TestItem.class);
        return testItemAnnotation != null && DeviceUtils.isDeviceUnsupported(testItemAnnotation.unsupportedDevice());
    }

    /**
     * 拼装不适用提示文案：默认设备型号提示 + 宿主应用设置的原因说明（非空时）
     */
    private String buildItemUnsupportedMessage(int messageRes) {
        String message = getString(R.string.circle_navigation) + getString(messageRes, Build.MANUFACTURER + " " + Build.MODEL);
        TestItem testItemAnnotation = clz.getAnnotation(TestItem.class);
        if (testItemAnnotation != null && !"".equals(testItemAnnotation.unsupportedDeviceDes())) {
            message = message + "\n"
                    + getString(R.string.circle_navigation) + testItemAnnotation.unsupportedDeviceDes();
        }
        return message;
    }

    @Override
    public void onActionAfterInitData() {
        viewBinding.viewKeyboard.setIOnKeyboardListener(new NumberKeyBoardView.IOnKeyboardListener() {
            @Override
            public void onInsertKeyEvent(String text) {
                switch (text) {
                    case Option.RUN_ALL_CASES:
                        // 测试项不适用当前设备时，拦截运行类选项并弹窗提示，不进入执行流程
                        if (isItemUnsupportedOnCurrentDevice()) {
                            Dialog.notifyDialog(getContext(), buildItemUnsupportedMessage(R.string.test_item_unsupported_device_run_blocked));
                            break;
                        }
                        ExecutionDetailsFragment executionDetailsFragment = new ExecutionDetailsFragment(clz, testItem, Option.RUN_ALL_CASES, INVALID_VALUE, INVALID_VALUE, INVALID_VALUE);
                        getActivity().runOnUiThread(() -> {
                            FragmentManager supportFragmentManager = getActivity().getSupportFragmentManager();
                            supportFragmentManager.beginTransaction()
                                    .replace(R.id.main_layout, executionDetailsFragment, EXECUTION_DETAIL_TAG)
                                    .addToBackStack(executionDetailsFragment.getClass().getSimpleName())
                                    .commit();
                            supportFragmentManager.executePendingTransactions();

                        });
                        break;
                    case Option.RUN_ONE_CASE:
                    case Option.RUN_PART_CASES:
                    case Option.RUN_PART_NONCONTINUOUS_CASES:
                        // 测试项不适用当前设备时，拦截运行类选项并弹窗提示，不进入执行流程
                        if (isItemUnsupportedOnCurrentDevice()) {
                            Dialog.notifyDialog(getContext(), buildItemUnsupportedMessage(R.string.test_item_unsupported_device_run_blocked));
                            break;
                        }
                        ExecutionFragment executionFragment = new ExecutionFragment(clz, testItem, text);
                        getActivity().runOnUiThread(() -> {
                            FragmentManager supportFragmentManager = getActivity().getSupportFragmentManager();
                            supportFragmentManager.beginTransaction()
                                    .replace(R.id.main_layout, executionFragment)
                                    .addToBackStack(executionFragment.getClass().getSimpleName())
                                    .commit();
                            supportFragmentManager.executePendingTransactions();

                        });
                        break;
                    // 查看类选项不受不支持设备拦截影响，正常放行
                    case Option.VIEW_ALL_CASES:
                    case Option.VIEW_ABANDON_CASES:
                    case Option.VIEW_UNEXECUTED_CASES:
                    case Option.VIEW_FAILED_CASES:
                        ExecutionFragment viewFragment = new ExecutionFragment(clz, testItem, text);
                        getActivity().runOnUiThread(() -> {
                            FragmentManager supportFragmentManager = getActivity().getSupportFragmentManager();
                            supportFragmentManager.beginTransaction()
                                    .replace(R.id.main_layout, viewFragment)
                                    .addToBackStack(viewFragment.getClass().getSimpleName())
                                    .commit();
                            supportFragmentManager.executePendingTransactions();

                        });
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onDeleteKeyEvent() {

            }

            @Override
            public void onOK() {

            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (SharedPreferencesUtil.get(SharedPreferencesUtil.IS_PHYSICAL_KEYBOARD, false)) {
            viewBinding.viewKeyboard.setVisibility(View.GONE);
            // 确保视图可以接收按键事件
            view.setFocusableInTouchMode(true);
            view.requestFocus();
            view.setOnKeyListener((v, keyCode, event) -> {
                Log.d("AutoTest", "keyCode = " + keyCode);
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_0:
                        case KeyEvent.KEYCODE_1:
                        case KeyEvent.KEYCODE_2:
                        case KeyEvent.KEYCODE_3:
                        case KeyEvent.KEYCODE_4:
                        case KeyEvent.KEYCODE_5:
                        case KeyEvent.KEYCODE_6:
                        case KeyEvent.KEYCODE_7:
                        case KeyEvent.KEYCODE_8:
                        case KeyEvent.KEYCODE_9:
                            String digit = String.valueOf(keyCode - KeyEvent.KEYCODE_0);
                            viewBinding.viewKeyboard.callOnInsertKey(digit);
                            return true;

                        case KeyEvent.KEYCODE_DEL:
                            viewBinding.viewKeyboard.callOnDeleteKey();
                            return true;

                        case KeyEvent.KEYCODE_ENTER:
                            viewBinding.viewKeyboard.callOnOK();
                            return true;

                        default:
                            return false;
                    }
                }
                return false;
            });
        }
    }

}
