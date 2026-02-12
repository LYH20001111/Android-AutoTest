package com.newland.nsdkdemo.common.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;

import com.newland.nsdk.core.api.common.utils.LogUtils;
import com.newland.nsdkdemo.R;

import java.util.ArrayList;

public class DialogUtils {
    private static int yourChoice = 0;
    private static ArrayList<Integer> yourChoices = new ArrayList<>();
    private static String TAG = "DialogUtils";
    private static Dialog singleDialog, customSingleDiaolg, multiDialog, specificDialog;

    /**
     * Create single choice dialog.
     *
     * @param context  Context.
     * @param title    Title.
     * @param items    Option items.
     * @param callback Callback.
     */
    public static void createSingleChoiceDialog(final Context context, final String title, final String[] items, final SingleChoiceDialogCallback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (items == null || items.length < 1) {
                    LogUtils.e(TAG, "createSingleChoiceDialog error,items shouldn't be null");
                    return;
                }
                yourChoice = 0;
                Looper.prepare();
                AlertDialog.Builder singleChoiceDialog = new AlertDialog.Builder(context);
                singleChoiceDialog.setTitle(title);
                // The second parameter is default option, set it to 0.
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
                                try {
                                    callback.onResult(yourChoice);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                singleChoiceDialog.setNegativeButton(R.string.msg_cancel, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        singleDialog.dismiss();
                        try {
                            callback.onResult(-1);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
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
     * Create single choice dialog.
     *
     * @param context  Context.
     * @param title    Title.
     * @param items    Option items.
     * @param buttons  buttons[0] for positive button, button[1] for negative button, button[2] for neutral button.
     * @param callback Callback.
     */
    public static void createSingleChoiceDialogThreeBtn(final Context context, final String title, final String[] items, final int[] buttons, final SingleChoiceDialogCallback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (items == null || items.length < 1) {
                    LogUtils.e(TAG, "createSingleChoiceDialog error,items shouldn't be null");
                    return;
                }
                yourChoice = 0;
                Looper.prepare();
                AlertDialog.Builder singleChoiceDialog = new AlertDialog.Builder(context);
                singleChoiceDialog.setTitle(title);
                // The second parameter is default option, set it to 0.
                singleChoiceDialog.setSingleChoiceItems(items, 0,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                yourChoice = which;
                            }
                        });
                singleChoiceDialog.setPositiveButton(context.getString(buttons[0]),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                singleDialog.dismiss();
                                try {
                                    callback.onResult(yourChoice);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                singleChoiceDialog.setNegativeButton(buttons[1], new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        singleDialog.dismiss();
                        try {
                            callback.onResult(-1);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

                singleChoiceDialog.setNeutralButton(buttons[2], new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        singleDialog.dismiss();
                        try {
                            callback.onResult(-2);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
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
     * Create custom dialog.
     *
     * @param context  Context.
     * @param title    Title.
     * @param items    Option items.
     * @param layoutId Layout ID.
     * @param callback Callback.
     */
    public static void createCustomDialog(final Context context, final String title, final String[] items, final int layoutId, final CustomDialogCallback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                yourChoice = 0;
                Looper.prepare();

                AlertDialog.Builder singleChoiceDialog = new AlertDialog.Builder(context);
                singleChoiceDialog.setTitle(title);
                final View view = LayoutInflater.from(context).inflate(layoutId, null);
                singleChoiceDialog.setView(view);

                if (items != null && items.length > 1) {
                    // The second parameter is default option, set it to 0.
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
                                try {
                                    callback.onResult(yourChoice, view);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                singleChoiceDialog.setNegativeButton(R.string.msg_cancel, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        customSingleDiaolg.dismiss();
                        try {
                            callback.onResult(-1, view);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
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
     * Create multi choices dialog.
     *
     * @param context
     * @param title
     * @param items
     * @param callback
     */
    public static void createMultiChoiceDialog(final Context context, final String title, final String[] items, final MultiChoiceDialogCallback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (items == null || items.length < 1) {
                    LogUtils.e(TAG, "createMultiChoiceDialog error,items shouldn't be null");
                    return;
                }
                Looper.prepare();

                yourChoices = new ArrayList<>();
                final int[] choiceItems = new int[items.length];
                final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(title);
                final boolean[] initChoiceSets = new boolean[items.length];
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
                            }
                        });
                builder.setPositiveButton(context.getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        multiDialog.dismiss();
                        for (int i = 0; i < choiceItems.length; i++) {
                            if (choiceItems[i] == 1) {
                                yourChoices.add(i);
                            }
                        }
                        try {
                            callback.onResult(yourChoices);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
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

    public static void createCustomDialog(final Context context, final int title, final String[] items, final int layoutId, final CustomDialogCallback2 callback) {
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
                        }
                    });
                }
                singleChoiceDialog.setPositiveButton(context.getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        customSingleDiaolg.dismiss();
                        try {
                            callback.onResult(yourChoice, view);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
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

    public interface SingleChoiceDialogCallback {
        /**
         * Invoked on result.
         *
         * @param id Selected ID, started from 0, -1 is cancelled.
         */
        void onResult(int id);

    }
    public interface CustomDialogCallback {
        /**
         * Invoked on result.
         *
         * @param id         Selected ID.
         * @param dialogView
         */
        void onResult(int id, View dialogView);

    }
    public interface CustomDialogCallback2 {

        void onInit(View view);
        void onResult(int id, View view);

    }
    public interface MultiChoiceDialogCallback {
        /**
         * @param yourChoices
         */
        void onResult(ArrayList<Integer> yourChoices);

    }
}
