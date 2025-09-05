package com.hudou.autotest.fragment;

import static com.hudou.autotest.fragment.OptionsFragment.INVALID_VALUE;
import static com.hudou.autotest.fragment.OptionsFragment.Option.RUN_ALL_CASES;
import static com.hudou.autotest.fragment.OptionsFragment.Option.RUN_ONE_CASE;
import static com.hudou.autotest.fragment.OptionsFragment.Option.RUN_PART_CASES;
import static com.hudou.autotest.fragment.OptionsFragment.Option.VIEW_ALL_CASES;

import android.annotation.SuppressLint;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;

import com.hudou.autotest.R;
import com.hudou.autotest.annotation.TestItem;
import com.hudou.autotest.base.activity.AutoTestMainActivity;
import com.hudou.autotest.base.fragment.BaseFragment;
import com.hudou.autotest.base.item.AutoTestTestItem;
import com.hudou.autotest.base.item.BaseTestCase;
import com.hudou.autotest.databinding.AutoTestExcutionDetailsFragmentBinding;
import com.hudou.autotest.ui.dialog.listener.NotifyOptionDialogListener;
import com.hudou.autotest.util.ReflectionUtils;
import com.hudou.autotest.util.SpannableUtil;

@SuppressLint("SetTextI18n")
public class ExecutionDetailsFragment extends BaseFragment<AutoTestExcutionDetailsFragmentBinding> {

    private final Class<? extends BaseTestCase> clz;
    private final BaseTestCase testItem;
    private int testId = INVALID_VALUE;
    private int beginId = INVALID_VALUE;
    private int endId = INVALID_VALUE;
    private boolean returnAllowed = false;
    private OnBackPressedCallback onBackPressedCallback;
    private String option;

    public ExecutionDetailsFragment(Class<? extends BaseTestCase> clz, BaseTestCase testItem, @NonNull String option,
                                    @IntRange(from = INVALID_VALUE) int testId, @IntRange(from = INVALID_VALUE) int beginId, @IntRange(from = INVALID_VALUE) int endId) {
        this.clz = clz;
        this.testItem = testItem;
        this.option = option;
        this.testId = testId;
        this.beginId = beginId;
        this.endId = endId;
    }

    @Override
    public void onInitData() {
        viewBinding.tvItem.setText(viewBinding.tvItem.getText() + ReflectionUtils.getAnnotationValue(clz, TestItem.class, TestItem.Members.name));
        AutoTestMainActivity.llMessage = viewBinding.llMessage;
        initAction();
    }

    @Override
    public void onActionAfterInitData() {
        switch (option) {
            case RUN_ALL_CASES:
                if (testItem.testItemCasesNum(clz) == 0) {
                    viewBinding.tvLine2Message.setText(viewBinding.tvLine2Message.getText() + getString(R.string.no_cases_found));
                    BaseTestCase.isCompleted = true;
                } else {
                    viewBinding.tvLine2Message.setText(viewBinding.tvLine2Message.getText().toString() + "0  ~  " + (testItem.testItemCasesNum(clz) - 1));
                    testItem.runAllCases(clz);
                }
                break;
            case RUN_ONE_CASE:
                viewBinding.tvLine2Message.setText(viewBinding.tvLine2Message.getText().toString() + testId);
                testItem.runCase(clz, testId);
                break;
            case RUN_PART_CASES:
                viewBinding.tvLine2Message.setText(viewBinding.tvLine2Message.getText().toString() + beginId + "  ~  " + endId);
                testItem.runPartContinueCases(clz, beginId, endId);
                break;
            case VIEW_ALL_CASES:
                break;
            default:
                break;
        }

        new Thread(() -> {
            while (true) {
                if (BaseTestCase.isCompleted) {
                    returnAllowed = true;
                    return;
                }
            }
        }).start();

    }

    private void initAction() {
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
        Dialog.notifyOptionsDialog(getContext(),
                SpannableUtil.setSpan(requireContext(), getString(R.string.pause_tes), getString(R.string.pause_test_part_content), R.color.red),
                new NotifyOptionDialogListener() {
                    @Override
                    public void onPositive() {
                        AutoTestTestItem.isPaused = true;
                        Toast.makeText(getContext(), R.string.pause_positive, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNegative() {
                        Toast.makeText(getContext(), R.string.cancel, Toast.LENGTH_SHORT).show();
                    }
                });
    }


}
