package com.hudou.autotest.customUI.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Looper;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.StringRes;


import com.hudou.autotest.R;
import com.hudou.autotest.customUI.dialog.listener.NotifyDialogListener;
import com.hudou.autotest.customUI.dialog.listener.NotifyOptionDialogListener;
import com.hudou.autotest.customUI.dialog.listener.SingleChoiceDialogListener;
import com.hudou.autotest.customUI.dialog.listener.CustomDialogListener;
import com.hudou.autotest.customUI.dialog.listener.EditDialogListener;
import com.hudou.autotest.customUI.dialog.listener.MultiChoiceDialogListener;
import com.hudou.autotest.customUI.dialog.listener.NewCustomDialogListener;
import com.hudou.autotest.report.excel.ReportOutput;
import com.hudou.autotest.util.FileUtil;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DialogUtils {
    private static int yourChoice = 0;
    private static ArrayList<Integer> choiceList = new ArrayList<>();
    private static Dialog notifyDialog, editTextDialog, singleDialog, customSingleDialog, multiDialog;

    public static void createNotifyDialog(final Context context, final String title){
        new Thread(() -> {
            Looper.prepare();
            notifyDialog = new AlertDialog.Builder(context)
                    .setTitle(title)
                    .setPositiveButton(R.string.sure, (dialog, which) -> notifyDialog.dismiss())
                    .create();
            notifyDialog.show();
            Looper.loop();
        }).start();
    }

    /**
     * 创建提示对话框
     *
     * @param context
     * @param title 对话框标题
     */
    public static void createNotifyDialog(final Context context, final String title, final NotifyDialogListener callback){
        new Thread(() -> {
            Looper.prepare();
            notifyDialog = new AlertDialog.Builder(context)
                    .setTitle(title)
                    .setPositiveButton(R.string.sure, (dialog, which) -> {
                        notifyDialog.dismiss();
                        callback.onAction();
                    })
                    .create();
            notifyDialog.show();
            Looper.loop();
        }).start();
    }

    public static void createNotifyOptionsDialog(final Context context, final String title, final NotifyOptionDialogListener callback){
        new Thread(() -> {
            Looper.prepare();
            notifyDialog = new AlertDialog.Builder(context)
                    .setTitle(title)
                    .setPositiveButton(R.string.sure, (dialog, which) -> {
                        notifyDialog.dismiss();
                        callback.onPositive();
                    })
                    .setNegativeButton(R.string.cancel, (dialog, which) -> {
                        notifyDialog.dismiss();
                        callback.onNegative();
                    })
                    .create();
            notifyDialog.show();
            Looper.loop();
        }).start();
    }

    public static void createEditTextDialog(final Context context, @StringRes int hint, final boolean onlyNumber, final EditDialogListener callback){
        createEditTextDialog(context, context.getString(hint, ""), onlyNumber, callback);
    }

    public static void createEditTextDialog(final Context context, final String hint, final boolean onlyNumber, final EditDialogListener callback){
        new Thread(() -> {
            Looper.prepare();
            final EditText editText = new EditText(context);
            if (onlyNumber) {
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
            }
            editText.setHint(hint);
            editText.setBackgroundResource(R.drawable.auto_test_border_input_box);
            editTextDialog = new AlertDialog.Builder(context)
                    .setView(editText)
                    .setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            callback.onResult(editText.getText().toString());
                            editTextDialog.dismiss();
                        }
                    })
                    .setCancelable(false)
                    .create();
            editTextDialog.show();
            Looper.loop();
        }).start();
    }

    /**
     * 创建单选对话框
     *
     * @param context
     * @param titleId  对话框标题
     * @param items    选项
     * @param callback 选择回调
     */
    public static void createSingleChoiceDialog(final Context context, @StringRes int titleId, final String[] items, final SingleChoiceDialogListener callback) {
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
                     })
                    .setNegativeButton(R.string.cancel, (dialog, which) -> {
                        singleDialog.dismiss();
                        callback.onResult(-1);
                    })
                    .setCancelable(false)
                    .create();
            singleDialog.show();
            Looper.loop();
        }).start();
    }

    /**
     * 创建多选框
     *
     * @param context
     * @param titleId 对话框标题
     * @param items 选项
     * @param callback
     */
    public static void createMultiChoiceDialog(final Context context, @StringRes int titleId, final String[] items, final MultiChoiceDialogListener callback) {
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
                    })
                    .setNegativeButton(R.string.cancel, (arg0, arg1) -> multiDialog.dismiss())
                    .setCancelable(false)
                    .create();
            multiDialog.show();
            Looper.loop();
        }).start();
    }

    /**
     * 创建自定义单选对话框
     *
     * @param context
     * @param titleId    对话框标题
     * @param items    单选选项
     * @param layoutId 布局id
     * @param callback 选择回调
     */
    public static void createCustomDialog(final Context context, @StringRes int titleId, final String[] items, final int layoutId, final CustomDialogListener callback) {
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
                            })
                    .setNegativeButton(R.string.cancel, (dialog, which) -> {
                        customSingleDialog.dismiss();
                        try {
                            callback.onResult(-1, view);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    })
                    .setCancelable(false)
                    .create();
            customSingleDialog.show();
            Looper.loop();
        }).start();
    }


    public static void createCustomDialog(final Context context, final int title, final String[] items, final int layoutId, final NewCustomDialogListener callback) {
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
                    })
                    .setNegativeButton(R.string.cancel, (dialog, which) -> customSingleDialog.dismiss())
                    .setCancelable(false)
                    .create();
            customSingleDialog.show();
            Looper.loop();
        }).start();

    }

    private static androidx.appcompat.app.AlertDialog loadingAlertDialog;
    private static void showLoadingDialog(Activity activity, String loadingMessage) {
        if (loadingAlertDialog != null && loadingAlertDialog.isShowing()) {
            return; // 如果对话框已经显示，则不重复创建
        }
        loadingAlertDialog = new androidx.appcompat.app.AlertDialog.Builder(activity).create();
        LayoutInflater inflater = activity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.auto_test_loading_view, null);
        TextView textView = dialogView.findViewById(R.id.tv_content);
        textView.setText(loadingMessage);
        loadingAlertDialog.setView(dialogView);
        loadingAlertDialog.setCanceledOnTouchOutside(false); // 设置点击外部区域是否取消对话框
        loadingAlertDialog.show();
    }

    public static void loadingFilesDialog(Activity activity, List<String> fileDirList, String loadedDirectory) {
        showLoadingDialog(activity, "Loading assets files ......");
        new Thread(() -> {
            try {
                for (int i = 0; i < fileDirList.size(); i++) {
                    FileUtil.loadAssetsFolder(activity, fileDirList.get(i), loadedDirectory);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            // 回到主线程更新UI
            boolean finalResult = true;
            activity.runOnUiThread(() -> {
                if (loadingAlertDialog != null && loadingAlertDialog.isShowing()) {
                    loadingAlertDialog.dismiss();
                }
                if (finalResult) {
                    Toast.makeText(activity, "Success", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(activity, "Failed", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }

    public static void outputDialog(Activity activity){
        showLoadingDialog(activity, "Outputting Report......");
        new Thread(() -> {
            boolean result;
            result = new ReportOutput().outputExcel();
            boolean finalResult = result;
            activity.runOnUiThread(() -> {
                if (loadingAlertDialog != null && loadingAlertDialog.isShowing()) {
                    loadingAlertDialog.dismiss();
                }
                if (finalResult) {
                    Toast.makeText(activity, "Success", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(activity, "Failed", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }


}
