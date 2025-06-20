package com.hudou.autotest.fragment;

import static com.hudou.autotest.fragment.OptionsFragment.INVALID_VALUE;
import static com.hudou.autotest.constant.FragmentTag.EXECUTION_DETAIL_TAG;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.view.View;
import android.widget.Toast;

import androidx.fragment.app.FragmentManager;

import com.hudou.autotest.R;
import com.hudou.autotest.annotation.TestItem;
import com.hudou.autotest.base.activity.AutoTestMainActivity;
import com.hudou.autotest.base.fragment.BaseFragment;
import com.hudou.autotest.base.item.BaseTestCase;
import com.hudou.autotest.constant.ShowMessage;
import com.hudou.autotest.databinding.AutoTestExecutionFragmentBinding;
import com.hudou.autotest.customUI.dialog.DialogUtils;
import com.hudou.autotest.customUI.keyboard.NumberKeyBoardView;
import com.hudou.autotest.listener.MyOnClickListener;
import com.hudou.autotest.util.ReflectionUtils;

@SuppressLint("SetTextI18n")
public class ExecutionFragment extends BaseFragment<AutoTestExecutionFragmentBinding> {

    private final Class<? extends BaseTestCase> clz;
    private final BaseTestCase testItem;
    private final String option;
    private int testId = INVALID_VALUE;
    private int beginId = INVALID_VALUE;
    private int endId = INVALID_VALUE;

    public ExecutionFragment(Class<? extends BaseTestCase> clz, BaseTestCase testItem, String option){
        this.clz = clz;
        this.testItem = testItem;
        this.option = option;
    }

    @Override
    public void onInitData() {
        viewBinding.tvItem.setText(viewBinding.tvItem.getText() + ReflectionUtils.getAnnotationValue(clz, TestItem.class, TestItem.Members.name));
        AutoTestMainActivity.llMessage = viewBinding.llMessage;
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
                    try {
                        testId = Integer.parseInt(viewBinding.tvCaseId.getText().toString());
                    }catch (NumberFormatException ignored){

                    }
                    if (testId >= testItem.testItemCasesNum(clz) || testId == INVALID_VALUE){
                        DialogUtils.createNotifyDialog(getContext(), getString(R.string.please_input_correct_case_id), () -> getActivity().runOnUiThread(() -> viewBinding.tvCaseId.setText("")));
                    }else {
                        ExecutionDetailsFragment executionDetailsFragment = new ExecutionDetailsFragment(clz, testItem, OptionsFragment.Option.RUN_ONE_CASE, testId, INVALID_VALUE, INVALID_VALUE);
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
    public void onActionAfterInitData() {
        actionByOption(option);
        viewBinding.btnBeginId.setOnClickListener(new MyOnClickListener() {
            @Override
            public void dealClick(View v) {
                DialogUtils.createEditTextDialog(getContext(), R.string.input_begin_id_hint, true, message -> getActivity().runOnUiThread(() -> {
                    try {
                        beginId = Integer.parseInt(message);
                    }catch (NumberFormatException ignored){
                    }
                    if (beginId >= (testItem.testItemCasesNum(clz) - 1) || beginId == -1){
                        Toast.makeText(getContext(), R.string.begin_id_not_allowed, Toast.LENGTH_SHORT).show();
                    }else {
                        viewBinding.btnBeginId.setText(getString(R.string.begin_id) + beginId);
                    }
                }));
            }
        });
        viewBinding.btnEndId.setOnClickListener(new MyOnClickListener() {
            @Override
            public void dealClick(View v) {
                DialogUtils.createEditTextDialog(getContext(), R.string.input_end_id_hint, true, message -> getActivity().runOnUiThread(() -> {
                    try {
                        endId = Integer.parseInt(message);
                    }catch (NumberFormatException ignored){
                    }
                    if (endId >= testItem.testItemCasesNum(clz) || endId == -1 || endId <= beginId){
                        Toast.makeText(getContext(), R.string.end_id_not_allowed, Toast.LENGTH_SHORT).show();
                    }else {
                        viewBinding.btnEndId.setText(getString(R.string.end_id) + endId);
                        if (beginId != -1){
                            ExecutionDetailsFragment executionDetailsFragment = new ExecutionDetailsFragment(clz, testItem, OptionsFragment.Option.RUN_PART_CASES, INVALID_VALUE, beginId, endId);
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
                }));
            }
        });
    }


    private void actionByOption(String option){
        switch (option){
            case OptionsFragment.Option.RUN_ONE_CASE:
                AutoTestMainActivity.getRecorder().postValue(new ShowMessage(Color.BLUE, testItem.viewCaseDetails(clz)));
                break;
            case OptionsFragment.Option.RUN_PART_CASES:
                viewBinding.llLine2.setVisibility(View.GONE);
                viewBinding.viewKeyboard.setVisibility(View.GONE);
                viewBinding.llLine3.setVisibility(View.VISIBLE);
                AutoTestMainActivity.getRecorder().postValue(new ShowMessage(Color.BLUE, testItem.viewCaseDetails(clz)));
                break;
            case OptionsFragment.Option.VIEW_ALL_CASES:
                viewBinding.tvItem.setText(getString(R.string.current_item) + ReflectionUtils.getAnnotationValue(clz, TestItem.class, TestItem.Members.name));
                viewBinding.llLine2.setVisibility(View.GONE);
                viewBinding.viewKeyboard.setVisibility(View.GONE);
                AutoTestMainActivity.getRecorder().postValue(new ShowMessage(Color.BLUE, testItem.viewCaseDetails(clz)));
            default:
                break;
        }

    }

}
