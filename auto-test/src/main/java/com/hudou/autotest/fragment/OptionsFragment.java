package com.hudou.autotest.fragment;

import static com.hudou.autotest.constant.FragmentTag.EXECUTION_DETAIL_TAG;

import androidx.fragment.app.FragmentManager;

import com.hudou.autotest.R;
import com.hudou.autotest.annotation.TestItem;
import com.hudou.autotest.base.fragment.BaseFragment;
import com.hudou.autotest.base.item.BaseTestCase;
import com.hudou.autotest.databinding.AutoTestOptionsFragmentBinding;
import com.hudou.autotest.customUI.keyboard.NumberKeyBoardView;
import com.hudou.autotest.util.ReflectionUtils;

public class OptionsFragment extends BaseFragment<AutoTestOptionsFragmentBinding> {
    private final Class<? extends BaseTestCase> clz;
    private final BaseTestCase testItem;

    public OptionsFragment(Class<? extends BaseTestCase> clz){
        this.clz = clz;
        this.testItem = (BaseTestCase) ReflectionUtils.createInstance(this.clz);
    }

    @Override
    protected void initData() {
        viewBinding.tvOptions.setText(String.format("当前测试项 ： %s" +
                "\n" +
                "\n " +
                "1. 运行所有案例 \n " +
                "2. 运行某个案例 \n " +
                "3. 运行部分连续案例 \n " +
                "4. 查看所有案例详情",
                ReflectionUtils.getAnnotationValue(clz, TestItem.class, "description")));
    }

    @Override
    protected void initActionAfterInitData() {
        viewBinding.viewKeyboard.setIOnKeyboardListener(new NumberKeyBoardView.IOnKeyboardListener() {
            @Override
            public void onInsertKeyEvent(String text) {
                switch (text){
                    case "1":
                        ExecutionDetailsFragment executionDetailsFragment = new ExecutionDetailsFragment(clz, testItem, -2);
                        getActivity().runOnUiThread(() -> {
                            FragmentManager supportFragmentManager = getActivity().getSupportFragmentManager();
                            supportFragmentManager.beginTransaction()
                                    .replace(R.id.main_layout, executionDetailsFragment, EXECUTION_DETAIL_TAG)
                                    .addToBackStack(executionDetailsFragment.getClass().getSimpleName())
                                    .commit();
                            supportFragmentManager.executePendingTransactions();

                        });
                        break;
                    case "2":
                    case "3":
                    case "4":
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
