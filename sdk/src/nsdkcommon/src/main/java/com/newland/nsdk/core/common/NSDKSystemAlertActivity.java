package com.newland.nsdk.core.common;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class NSDKSystemAlertActivity extends Activity {
    private AlertDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Window window = getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.setWindowAnimations(0);
        window.setDimAmount(0f);
        window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        showDialogDirectly();
    }

    private void showDialogDirectly() {
        String title = getIntent().getStringExtra("title");
        String message = getIntent().getStringExtra("message");

        AlertDialog.Builder builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Light_Dialog_Alert);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                safeFinish();
            }
        });

        dialog = builder.create();
        Window dw = dialog.getWindow();
        if (dw != null) {
            dw.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            dw.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
            dw.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            dw.setDimAmount(0.6f);
            dw.setWindowAnimations(0);
            // 显示弹框时屏蔽 home 键
            dw.addFlags(3);
            // 显示弹框时屏蔽菜单键（注：底部导航栏的按键可以配置成 Menu 或 App Switch，只有配置成 Menu 菜单键的时候才可以屏蔽，App Switch 无法屏蔽）
            dw.addFlags(5);
        }
        dialog.show();
    }

    private void safeFinish() {
        if (!isFinishing()) {
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        // 禁用返回键，只能通过对话框按钮关闭
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        dialog = null;
    }

    public static void show(Context context, String sdkName, String sdkVersion) {
        Intent intent = new Intent(context, NSDKSystemAlertActivity.class);
        // 弹框标题和提示语不要修改，会议评审决定采用这个统一的标题和提示语的
        intent.putExtra("title", "Warning!");
        intent.putExtra("message", String.format("%s %s is for development use only. Not for production.", sdkName, sdkVersion));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}