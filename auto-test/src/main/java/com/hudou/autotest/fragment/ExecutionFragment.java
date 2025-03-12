package com.hudou.autotest.fragment;

import static com.hudou.autotest.constant.FragmentTag.EXECUTION_DETAIL_TAG;

import android.graphics.Color;
import android.view.View;
import android.widget.Toast;

import androidx.fragment.app.FragmentManager;

//import com.hudou.autotest.MainActivity;
import com.hudou.autotest.R;
import com.hudou.autotest.annotation.TestItem;
import com.hudou.autotest.base.activity.BaseMainActivity;
import com.hudou.autotest.base.fragment.BaseFragment;
import com.hudou.autotest.base.item.BaseTestCase;
import com.hudou.autotest.constant.ShowMessage;
import com.hudou.autotest.databinding.AutoTestExecutionFragmentBinding;
import com.hudou.autotest.customUI.dialog.DialogUtils;
import com.hudou.autotest.customUI.keyboard.NumberKeyBoardView;
import com.hudou.autotest.util.ReflectionUtils;

public class ExecutionFragment extends BaseFragment<AutoTestExecutionFragmentBinding> {

    private final Class<? extends BaseTestCase> clz;
    private final BaseTestCase testItem;
    private final String option;
    private int beginId = -1;
    private int endId = -1;

    public ExecutionFragment(Class<? extends BaseTestCase> clz, BaseTestCase testItem, String option){
        this.clz = clz;
        this.testItem = testItem;
        this.option = option;
    }

    @Override
    protected void initData() {
        viewBinding.tvItem.setText(viewBinding.tvItem.getText() + ReflectionUtils.getAnnotationValue(clz, TestItem.class, "description"));
        BaseMainActivity.llMessage = viewBinding.llMessage;
        initAction();
    }

    private void initAction(){
        viewBinding.viewKeyboard.setIOnKeyboardListener(new NumberKeyBoardView.IOnKeyboardListener() {
            @Override
            public void onInsertKeyEvent(String text) {
                viewBinding.tvCaseId.setText(viewBinding.tvCaseId.getText() + text);
            }

            @Override
            public void onDeleteKeyEvent() {
                if (viewBinding.tvCaseId.length() != 0) {
                    viewBinding.tvCaseId.setText(viewBinding.tvCaseId.getText().subSequence(0, viewBinding.tvCaseId.length() - 1));
                }else {
                    viewBinding.tvCaseId.setText("");
                }
            }

            @Override
            public void onOK() {
                if (!viewBinding.tvCaseId.getText().toString().equals("")) {
                    int testID = -1;
                    try {
                        testID = Integer.parseInt(viewBinding.tvCaseId.getText().toString());
                    }catch (NumberFormatException ignored){

                    }
                    if (testID >= testItem.testItemCasesNum(clz) || testID == -1){
                        DialogUtils.createNotifyDialog(getContext(), "请输入正确的案例号", () -> getActivity().runOnUiThread(() -> viewBinding.tvCaseId.setText("")));
                    }else {
                        ExecutionDetailsFragment executionDetailsFragment = new ExecutionDetailsFragment(clz, testItem, testID);
                        getActivity().runOnUiThread(() -> {
                            FragmentManager supportFragmentManager = getActivity().getSupportFragmentManager();
                            supportFragmentManager.beginTransaction()
                                    .replace(R.id.main_layout, executionDetailsFragment)
                                    .addToBackStack(executionDetailsFragment.getClass().getSimpleName())
                                    .commit();
                            supportFragmentManager.executePendingTransactions();
                        });
                    }

                }
            }
        });

    }

    @Override
    protected void initActionAfterInitData() {
        actionByOption(option);
        viewBinding.btnBeginId.setOnClickListener(v -> DialogUtils.createEditTextDialog(getContext(), "请输入起始案例号", message -> getActivity().runOnUiThread(() -> {
            try {
                beginId = Integer.parseInt(message);
            }catch (NumberFormatException ignored){
            }
            if (beginId >= (testItem.testItemCasesNum(clz) - 1) || beginId == -1){
                Toast.makeText(getContext(), "输入的起始案例号不符合", Toast.LENGTH_SHORT).show();
            }else {
                viewBinding.btnBeginId.setText("起始案例号 : " + message);
            }
        })));
        viewBinding.btnEndId.setOnClickListener(v -> DialogUtils.createEditTextDialog(getContext(), "请输入结束案例号", message -> getActivity().runOnUiThread(() -> {
            try {
                endId = Integer.parseInt(message);
            }catch (NumberFormatException ignored){
            }
            if (endId >= testItem.testItemCasesNum(clz) || endId == -1 || endId <= beginId){
                Toast.makeText(getContext(), "输入的结束案例号不符合", Toast.LENGTH_SHORT).show();
            }else {
                viewBinding.btnEndId.setText("结束案例号 : " + message);
                if (beginId != -1){
                    ExecutionDetailsFragment executionDetailsFragment = new ExecutionDetailsFragment(clz, testItem, beginId, endId);
                    getActivity().runOnUiThread(() -> {
                        FragmentManager supportFragmentManager = getActivity().getSupportFragmentManager();
                        supportFragmentManager.beginTransaction()
                                .replace(R.id.main_layout, executionDetailsFragment, EXECUTION_DETAIL_TAG)
                                .addToBackStack(executionDetailsFragment.getClass().getSimpleName())
                                .commit();
                        supportFragmentManager.executePendingTransactions();
                    });
                }



            }
        })));
    }


    private void actionByOption(String option){
        switch (option){
            case "2":
                BaseMainActivity.mShowMessage.postValue(new ShowMessage(Color.BLUE, testItem.viewCaseDetails(clz)));
                break;
            case "3":
                viewBinding.llLine2.setVisibility(View.GONE);
                viewBinding.viewKeyboard.setVisibility(View.GONE);
                viewBinding.llLine3.setVisibility(View.VISIBLE);
                BaseMainActivity.mShowMessage.postValue(new ShowMessage(Color.BLUE, testItem.viewCaseDetails(clz)));
                break;
            case "4":
                viewBinding.tvItem.setText("当前查看项 ： " + ReflectionUtils.getAnnotationValue(clz, TestItem.class, "description"));
                viewBinding.llLine2.setVisibility(View.GONE);
                viewBinding.viewKeyboard.setVisibility(View.GONE);
                BaseMainActivity.mShowMessage.postValue(new ShowMessage(Color.BLUE, testItem.viewCaseDetails(clz)));
            default:
                break;
        }

    }

}
