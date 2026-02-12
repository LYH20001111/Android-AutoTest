package com.newland.testscanner.util;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.newland.testscanner.R;

import java.util.ArrayList;


/**
 * Created by YJF on 2019/5/13 16:24
 */
public class DialogUtils {
    private static int yourChoice = 0;
    private static ArrayList<Integer> yourChoices = new ArrayList<>();
    private static String TAG = "DialogUtils";
    private static Dialog singleDialog,customSingleDiaolg,multiDialog,specificDialog;

    /**
     * 创建单选对话框
     * @param context
     * @param title 对话框标题
     * @param items 选项
     * @param callback 选择回调
     */
    public static void createSingleChoiceDialog(final Context context,final String title,final String[] items,final SingleChoiceDialogCallback callback){
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(items==null || items.length<1){
                    Log.e(TAG,"createSingleChoiceDialog error,items shouldn't be null");
                    return;
                }
                yourChoice = 0;
                Looper.prepare();
                AlertDialog.Builder singleChoiceDialog = new AlertDialog.Builder(context);
                singleChoiceDialog.setTitle(title);
                // 第二个参数是默认选项，此处设置为0
                singleChoiceDialog.setSingleChoiceItems(items, 0,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                yourChoice = which;
                            }
                        });
                singleChoiceDialog.setPositiveButton(context.getString(R.string.dialog_ok),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                singleDialog.dismiss();
                                callback.onResult(yourChoice);
                            }
                        });
                singleChoiceDialog.setNegativeButton(R.string.msg_cancel,new DialogInterface.OnClickListener(){

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        singleDialog.dismiss();
                    }
                });

                singleDialog = singleChoiceDialog.create();
                singleChoiceDialog.setCancelable(false);
                singleDialog.show();
                Looper.loop();
            }
        }).start();

    }

    /**
     * 创建自定义单选对话框
     * @param context
     * @param title 对话框标题
     * @param items 单选选项
     * @param layoutId 布局id
     * @param callback 选择回调
     */
    public static void createCustomDialog(final Context context, final String title,final  String[] items,final int layoutId,final CustomDialogCallback callback){
        new Thread(new Runnable() {
            @Override
            public void run() {
                yourChoice = 0;
                Looper.prepare();

                AlertDialog.Builder singleChoiceDialog = new AlertDialog.Builder(context);
                singleChoiceDialog.setTitle(title);
                final View view = LayoutInflater.from(context).inflate(layoutId,null);
                singleChoiceDialog.setView(view);

                if(items!=null && items.length>1){
                    // 第二个参数是默认选项，此处设置为0
                    singleChoiceDialog.setSingleChoiceItems(items, 0,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    yourChoice = which;
                                }
                            });
                }

                singleChoiceDialog.setPositiveButton(context.getString(R.string.dialog_ok),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                customSingleDiaolg.dismiss();
                                callback.onResult(yourChoice,view);
                            }
                        });
                singleChoiceDialog.setNegativeButton(R.string.msg_cancel,new DialogInterface.OnClickListener(){

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        customSingleDiaolg.dismiss();
                    }
                });

                customSingleDiaolg = singleChoiceDialog.create();
                customSingleDiaolg.setCancelable(false);
                customSingleDiaolg.show();
                Looper.loop();
            }
        }).start();

    }

    /**
     * 创建多选框
     * @param context
     * @param title
     * @param items
     * @param callback
     */
    public static void createMultiChoiceDialog(final Context context, final String title, final String[] items, final MultiChoiceDialogCallback callback){
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(items==null || items.length<1){
                    Log.e(TAG,"createMultiChoiceDialog error,items shouldn't be null");
                    return;
                }
                Looper.prepare();

                yourChoices = new ArrayList<>();
                final int[] choiceItems = new int[items.length];
                final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(title);
                final boolean initChoiceSets[] = new boolean[items.length];
                builder.setMultiChoiceItems(items, initChoiceSets,
                        new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                if (isChecked) {
                                    choiceItems[which] = 1;
//                                    yourChoices.add(which);
                                } else {
                                    choiceItems[which] = 0;
//                                    yourChoices.remove(which);
                                }
                            }});
                builder.setPositiveButton(context.getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        multiDialog.dismiss();
                        for(int i=0;i< choiceItems.length;i++){
                            if(choiceItems[i]==1){
                                yourChoices.add(i);
                            }
                        }
                        callback.onResult(yourChoices);
                    }
                });
                builder.setNegativeButton(R.string.msg_cancel, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        multiDialog.dismiss();
                    }
                });

                multiDialog = builder.create();
                multiDialog.setCancelable(false);
                multiDialog.show();
                Looper.loop();
            }
        }).start();
    }

    public static interface SingleChoiceDialogCallback{
        /**
         * @param id 选中的id，从0开始，-1为取消
         */
        public void onResult(int id);
    }

    public static interface CustomDialogCallback{
        /**
         * @param id 选择的id
         * @param dialogView
         */
        public void onResult(int id, View dialogView);
    }
    public static interface CustomDialogCallback2{
        public void onInit(View view);
        public void onResult(int id, View view);
    }
    public static interface MultiChoiceDialogCallback{
        /**
         * @param yourChoices
         */
        public void onResult(ArrayList<Integer> yourChoices);
    }

    public static void createCustomDialog(final Context context, final int title,final String[] items,final int layoutId,final CustomDialogCallback2 callback){
        new Thread(new Runnable() {
            @Override
            public void run() {
                yourChoice = 0;
                Looper.prepare();
                AlertDialog.Builder singleChoiceDialog = new AlertDialog.Builder(context);
                singleChoiceDialog.setTitle(title);
                final View view = LayoutInflater.from(context).inflate(layoutId, null);
                singleChoiceDialog.setView(view);
                callback.onInit(view);
                if (items != null && items.length > 1) {
                    singleChoiceDialog.setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            yourChoice = which;
                        }});
                }
                singleChoiceDialog.setPositiveButton(context.getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        customSingleDiaolg.dismiss();
                        callback.onResult(yourChoice, view);
                    }});
                singleChoiceDialog.setNegativeButton(R.string.msg_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        customSingleDiaolg.dismiss();
                    }
                });

                customSingleDiaolg = singleChoiceDialog.create();
                customSingleDiaolg.setCancelable(false);
                customSingleDiaolg.show();
                Looper.loop();
            }
        }).start();

    }

}
