package com.hudou.autotest.fragment;

import static com.hudou.autotest.constant.FragmentTag.EXECUTION_DETAIL_TAG;

import androidx.fragment.app.FragmentManager;

import com.hudou.autotest.R;
import com.hudou.autotest.annotation.TestItem;
import com.hudou.autotest.base.fragment.BaseFragment;
import com.hudou.autotest.base.item.BaseTestCase;
import com.hudou.autotest.databinding.AutoTestOptionsFragmentBinding;
import com.hudou.autotest.ui.keyboard.NumberKeyBoardView;
import com.hudou.autotest.util.ReflectionUtils;

public class OptionsFragment extends BaseFragment<AutoTestOptionsFragmentBinding> {
    private final Class<? extends BaseTestCase> clz;
    private final BaseTestCase testItem;
    public static final int INVALID_VALUE = -1;

    public OptionsFragment(Class<? extends BaseTestCase> clz){
        this.clz = clz;
        this.testItem = (BaseTestCase) ReflectionUtils.createInstance(this.clz);
    }

    public interface Option{
        String RUN_ALL_CASES = "1";
        String RUN_ONE_CASE = "2";
        String RUN_PART_CASES = "3";
        String VIEW_ALL_CASES = "4";
    }

    @Override
    public void onInitData() {
        viewBinding.tvOptions.setText(String.format("当前测试项 ： %s" +
                "\n" +
                "\n " +
                "1. 运行所有案例 \n " +
                "2. 运行某个案例 \n " +
                "3. 运行部分连续案例 \n " +
                "4. 查看所有案例详情",
                ReflectionUtils.getAnnotationValue(clz, TestItem.class, TestItem.Members.name)));
    }

    @Override
    public void onActionAfterInitData() {
        viewBinding.viewKeyboard.setIOnKeyboardListener(new NumberKeyBoardView.IOnKeyboardListener() {
            @Override
            public void onInsertKeyEvent(String text) {
                switch (text){
                    case Option.RUN_ALL_CASES:
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
                    case Option.VIEW_ALL_CASES:
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

}
