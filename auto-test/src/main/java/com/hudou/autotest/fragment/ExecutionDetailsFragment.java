package com.hudou.autotest.fragment;

import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.FragmentManager;

//import com.hudou.autotest.MainActivity;
import com.hudou.autotest.R;
import com.hudou.autotest.annotation.TestItem;
import com.hudou.autotest.base.activity.BaseMainActivity;
import com.hudou.autotest.base.fragment.BaseFragment;
import com.hudou.autotest.base.item.BaseTestCase;
import com.hudou.autotest.base.item.BaseTestItem;
import com.hudou.autotest.customUI.dialog.DialogUtils;
import com.hudou.autotest.customUI.dialog.listener.NotifyOptionDialogListener;
import com.hudou.autotest.databinding.AutoTestExcutionDetailsFragmentBinding;
import com.hudou.autotest.util.ReflectionUtils;

import java.text.MessageFormat;

public class ExecutionDetailsFragment extends BaseFragment<AutoTestExcutionDetailsFragmentBinding> {

    private final Class<? extends BaseTestCase> clz;
    private final BaseTestCase testItem;
    private int testID = -1;
    private int beginID = -1;
    private int endID = -1;
    private boolean returnAllowed = false;
    private OnBackPressedCallback onBackPressedCallback;

    public ExecutionDetailsFragment(Class<? extends BaseTestCase> clz, BaseTestCase testItem, int testID){
        this.clz = clz;
        this.testItem = testItem;
        this.testID = testID;
    }
    public ExecutionDetailsFragment(Class<? extends BaseTestCase> clz, BaseTestCase testItem, int beginID, int endID){
        this.clz = clz;
        this.testItem = testItem;
        this.beginID = beginID;
        this.endID = endID;
    }

    @Override
    protected void initData() {
        viewBinding.tvItem.setText(String.format("%s%s", viewBinding.tvItem.getText(), ReflectionUtils.getAnnotationValue(clz, TestItem.class, "description")));
        BaseMainActivity.llMessage = viewBinding.llMessage;
        initAction();
    }

    @Override
    protected void initActionAfterInitData() {
        if (testID == -2){
            if (testItem.testItemCasesNum(clz) == 0){
                viewBinding.tvLine2Message.setText(String.format("%s未找到任何案例", viewBinding.tvLine2Message.getText()));
            }else {
                viewBinding.tvLine2Message.setText(MessageFormat.format("{0}0  ~  {1}", viewBinding.tvLine2Message.getText().toString(), testItem.testItemCasesNum(clz) - 1));
            }
            testItem.runAllCases(clz);
        }else if (testID != -1 ) {
            viewBinding.tvLine2Message.setText(MessageFormat.format("{0}{1}", viewBinding.tvLine2Message.getText().toString(), testID));
            testItem.runCase(clz, testID);
        }else if (beginID != -1 && endID != -1){
            viewBinding.tvLine2Message.setText(MessageFormat.format("{0}{1}  ~  {2}", viewBinding.tvLine2Message.getText().toString(), beginID, endID));
            testItem.runPartContinueCases(clz, beginID, endID);
        }

        new Thread(() -> {
            while (true){
                if (BaseTestCase.isCompleted){
                    returnAllowed = true;
                    return;
                }
            }
        }).start();

    }

    private void initAction(){
        onBackPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (returnAllowed) {
                    onBackPressedCallback.setEnabled(false);
                    if (isAdded() && getActivity() != null) {
                        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                        if (fragmentManager.getBackStackEntryCount() > 0) {
                            fragmentManager.popBackStack();
                        } else {
                            fragmentManager.beginTransaction()
                                    .remove(ExecutionDetailsFragment.this)
                                    .commit();
                        }
                    }
                } else {
                    // 不允许返回
                }
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(onBackPressedCallback);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        onBackPressedCallback.remove();
    }

    public void onBackPressedLongPress() {
        DialogUtils.createNotifyOptionsDialog(getContext(), getString(R.string.pause_tes), new NotifyOptionDialogListener() {
            @Override
            public void onPositive() {
                BaseTestItem.isPaused = true;
                Toast.makeText(getContext(), R.string.pause_positive, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNegative() {
                Toast.makeText(getContext(), R.string.cancel, Toast.LENGTH_SHORT).show();
            }
        });
    }


}
