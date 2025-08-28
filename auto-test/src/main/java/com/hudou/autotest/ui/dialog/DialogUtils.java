package com.hudou.autotest.ui.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.ConditionVariable;
import android.os.Looper;
import android.text.InputType;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.StringRes;

import com.hudou.autotest.R;
import com.hudou.autotest.ui.dialog.listener.NotifyDialogListener;
import com.hudou.autotest.ui.dialog.listener.NotifyOptionDialogListener;
import com.hudou.autotest.ui.dialog.listener.SingleChoiceDialogListener;
import com.hudou.autotest.ui.dialog.listener.CustomDialogListener;
import com.hudou.autotest.ui.dialog.listener.EditDialogListener;
import com.hudou.autotest.ui.dialog.listener.MultiChoiceDialogListener;
import com.hudou.autotest.ui.dialog.listener.NewCustomDialogListener;

import java.util.ArrayList;

public class DialogUtils {
    private static int yourChoice = 0;
    private static ArrayList<Integer> choiceList = new ArrayList<>();
    private static Dialog notifyDialog, editTextDialog, singleDialog, customSingleDialog, multiDialog;

    public static void notifyDialog(final Context context, final String title) {
        ConditionVariable cv = new ConditionVariable();
        new Thread(() -> {
            Looper.prepare();
            // 创建自定义标题视图
            LayoutInflater inflater = LayoutInflater.from(context);
            View customTitleView = inflater.inflate(R.layout.auto_test_custom_dialog_title, null);
            TextView titleTextView = customTitleView.findViewById(R.id.dialog_title);
            titleTextView.setText(title);
            notifyDialog = new AlertDialog.Builder(context)
                    .setCustomTitle(customTitleView)
                    .setPositiveButton(R.string.sure, (dialog, which) -> {
                        notifyDialog.dismiss();
                        cv.open();
                    })
                    .setCancelable(false)
                    .setOnCancelListener(dialog -> cv.open())
                    .create();
            notifyDialog.show();
            Looper.loop();
        }).start();
        cv.block();
    }

    /**
     * 创建提示对话框
     *
     * @param context
     * @param title   对话框标题
     */
    public static void notifyDialog(final Context context, final String title, final NotifyDialogListener callback) {
        ConditionVariable cv = new ConditionVariable();
        new Thread(() -> {
            Looper.prepare();
            // 创建自定义标题视图
            LayoutInflater inflater = LayoutInflater.from(context);
            View customTitleView = inflater.inflate(R.layout.auto_test_custom_dialog_title, null);
            TextView titleTextView = customTitleView.findViewById(R.id.dialog_title);
            titleTextView.setText(title);
            notifyDialog = new AlertDialog.Builder(context)
                    .setCustomTitle(customTitleView)
                    .setPositiveButton(R.string.sure, (dialog, which) -> {
                        notifyDialog.dismiss();
                        callback.onAction();
                        cv.open();
                    })
                    .setCancelable(false)
                    .setOnCancelListener(dialog -> cv.open())
                    .create();
            notifyDialog.show();
            Looper.loop();
        }).start();
        cv.block();
    }

    public static void notifyOptionsDialog(final Context context, final String title, final NotifyOptionDialogListener callback) {
        ConditionVariable cv = new ConditionVariable();
        new Thread(() -> {
            Looper.prepare();
            // 创建自定义标题视图
            LayoutInflater inflater = LayoutInflater.from(context);
            View customTitleView = inflater.inflate(R.layout.auto_test_custom_dialog_title, null);
            TextView titleTextView = customTitleView.findViewById(R.id.dialog_title);
            titleTextView.setText(title);
            notifyDialog = new AlertDialog.Builder(context)
                    .setCustomTitle(customTitleView)
                    .setPositiveButton(R.string.sure, (dialog, which) -> {
                        notifyDialog.dismiss();
                        callback.onPositive();
                        cv.open();
                    })
                    .setNegativeButton(R.string.cancel, (dialog, which) -> {
                        notifyDialog.dismiss();
                        callback.onNegative();
                        cv.open();
                    })
                    .setCancelable(false)
                    .setOnCancelListener(dialog -> cv.open())
                    .create();
            notifyDialog.show();
            Looper.loop();
        }).start();
        cv.block();
    }

    public static void messageOptionsDialog(final Context context, final String title, final String message,
                                            final int size, final NotifyOptionDialogListener callback) {
        ConditionVariable cv = new ConditionVariable();
        new Thread(() -> {
            Looper.prepare();
            // 创建一个可滚动的 TextView
            TextView textView = new TextView(context);
            textView.setText(message);
            textView.setPadding(20, 20, 20, 20); // 设置内边距
            textView.setMovementMethod(ScrollingMovementMethod.getInstance()); // 启用滚动
            textView.setVerticalScrollBarEnabled(true); // 启用垂直滚动条
            textView.setScrollBarStyle(View.SCROLLBARS_INSIDE_INSET); // 设置滚动条样式
            textView.setTextSize(size);

            notifyDialog = new AlertDialog.Builder(context)
                    .setTitle(title)
                    .setView(textView) // 使用自定义的 TextView
                    .setPositiveButton(R.string.sure, (dialog, which) -> {
                        notifyDialog.dismiss();
                        callback.onPositive();
                        cv.open();
                    })
                    .setNegativeButton(R.string.cancel, (dialog, which) -> {
                        notifyDialog.dismiss();
                        callback.onNegative();
                        cv.open();
                    })
                    .setCancelable(false)
                    .setOnCancelListener(dialog -> cv.open())
                    .create();
            notifyDialog.show();
            Looper.loop();
        }).start();
        cv.block();
    }

    public static void editDialog(final Context context, @StringRes int hint, final boolean onlyNumber, final EditDialogListener callback) {
        editDialog(context, context.getString(hint, ""), onlyNumber, callback);
    }

    public static void editDialog(final Context context, final String hint, final boolean onlyNumber, final EditDialogListener callback) {
        ConditionVariable cv = new ConditionVariable();
        new Thread(() -> {
            Looper.prepare();
            // 创建自定义布局
            View dialogView = LayoutInflater.from(context).inflate(R.layout.auto_test_dialog_edit_text, null);
            EditText editText = dialogView.findViewById(R.id.edit_text);

            // 设置输入类型
            if (onlyNumber) {
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
            }
            editText.setHint(hint);
            editTextDialog = new AlertDialog.Builder(context)
                    .setView(dialogView)
                    .setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            callback.onResult(editText.getText().toString());
                            editTextDialog.dismiss();
                            cv.open();
                        }
                    })
                    .setCancelable(false)
                    .setOnCancelListener(dialog -> cv.open())
                    .create();
            editTextDialog.show();
            Looper.loop();
        }).start();
        cv.block();
    }

    /**
     * 创建单选对话框
     *
     * @param context
     * @param titleId  对话框标题
     * @param items    选项
     * @param callback 选择回调
     */
    public static void singleChoiceDialog(final Context context, @StringRes int titleId, final String[] items, final SingleChoiceDialogListener callback) {
        ConditionVariable cv = new ConditionVariable();
        new Thread(() -> {
            if (items == null || items.length < 1) {
                return;
            }
            yourChoice = 0;
            Looper.prepare();
            singleDialog = new AlertDialog.Builder(context)
                    .setTitle(titleId)
                    .setSingleChoiceItems(items, 0,// 第二个参数是默认选项，此处设置为0
                            (dialog, which) -> yourChoice = which)
                    .setPositiveButton(R.string.sure, (dialog, which) -> {
                        singleDialog.dismiss();
                        callback.onResult(yourChoice);
                        cv.open();
                    })
                    .setNegativeButton(R.string.cancel, (dialog, which) -> {
                        singleDialog.dismiss();
                        callback.onResult(-1);
                        cv.open();
                    })
                    .setCancelable(false)
                    .setOnCancelListener(dialog -> cv.open())
                    .create();
            singleDialog.show();
            Looper.loop();
        }).start();
        cv.block();
    }

    /**
     * 创建多选框
     *
     * @param context
     * @param titleId  对话框标题
     * @param items    选项
     * @param callback
     */
    public static void multiChoiceDialog(final Context context, @StringRes int titleId, final String[] items, final MultiChoiceDialogListener callback) {
        ConditionVariable cv = new ConditionVariable();
        new Thread(() -> {
            if (items == null || items.length < 1) {
                return;
            }
            Looper.prepare();
            choiceList = new ArrayList<>();
            final int[] choiceItems = new int[items.length];
            multiDialog = new AlertDialog.Builder(context)
                    .setTitle(titleId).setMultiChoiceItems(items, new boolean[items.length],
                            (dialog, which, isChecked) -> {
                                if (isChecked) {
                                    choiceItems[which] = 1;
                                } else {
                                    choiceItems[which] = 0;
                                }
                            })
                    .setPositiveButton(R.string.sure, (arg0, arg1) -> {
                        multiDialog.dismiss();
                        for (int i = 0; i < choiceItems.length; i++) {
                            if (choiceItems[i] == 1) {
                                choiceList.add(i);
                            }
                        }
                        try {
                            callback.onResult(choiceList);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        cv.open();
                    })
                    .setNegativeButton(R.string.cancel, (arg0, arg1) -> {
                        multiDialog.dismiss();
                        cv.open();
                    })
                    .setCancelable(false)
                    .setOnCancelListener(dialog -> cv.open())
                    .create();
            multiDialog.show();
            Looper.loop();
        }).start();
        cv.block();
    }

    /**
     * 创建自定义单选对话框
     *
     * @param context
     * @param titleId  对话框标题
     * @param items    单选选项
     * @param layoutId 布局id
     * @param callback 选择回调
     */
    public static void customDialog(final Context context, @StringRes int titleId, final String[] items, final int layoutId, final CustomDialogListener callback) {
        ConditionVariable cv = new ConditionVariable();
        new Thread(() -> {
            yourChoice = 0;
            Looper.prepare();
            AlertDialog.Builder singleChoiceDialog = new AlertDialog.Builder(context);
            if (items != null && items.length > 1) {
                singleChoiceDialog.setSingleChoiceItems(items, 0,
                        (dialog, which) -> yourChoice = which);
            }
            final View view = LayoutInflater.from(context).inflate(layoutId, null);
            customSingleDialog = singleChoiceDialog
                    .setTitle(titleId)
                    .setView(view)
                    .setPositiveButton(R.string.sure,
                            (dialog, which) -> {
                                customSingleDialog.dismiss();
                                try {
                                    callback.onResult(yourChoice, view);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                cv.open();
                            })
                    .setNegativeButton(R.string.cancel, (dialog, which) -> {
                        customSingleDialog.dismiss();
                        try {
                            callback.onResult(-1, view);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        cv.open();
                    })
                    .setCancelable(false)
                    .setOnCancelListener(dialog -> cv.open())
                    .create();
            customSingleDialog.show();
            Looper.loop();
        }).start();
        cv.block();
    }


    public static void customDialog(final Context context, final int title, final String[] items, final int layoutId, final NewCustomDialogListener callback) {
        ConditionVariable cv = new ConditionVariable();
        new Thread(() -> {
            yourChoice = 0;
            Looper.prepare();
            AlertDialog.Builder singleChoiceDialog = new AlertDialog.Builder(context);
            final View view = LayoutInflater.from(context).inflate(layoutId, null);
            callback.onInit(view);
            if (items != null && items.length > 1) {
                singleChoiceDialog.setSingleChoiceItems(items, 0, (dialog, which) -> yourChoice = which);
            }
            customSingleDialog = singleChoiceDialog
                    .setTitle(title)
                    .setView(view)
                    .setPositiveButton(R.string.sure, (dialog, which) -> {
                        customSingleDialog.dismiss();
                        try {
                            callback.onResult(yourChoice, view);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        cv.open();
                    })
                    .setNegativeButton(R.string.cancel, (dialog, which) -> {
                        customSingleDialog.dismiss();
                        cv.open();
                    })
                    .setCancelable(false)
                    .setOnCancelListener(dialog -> cv.open())
                    .create();
            customSingleDialog.show();
            Looper.loop();
        }).start();
        cv.block();
    }


}
