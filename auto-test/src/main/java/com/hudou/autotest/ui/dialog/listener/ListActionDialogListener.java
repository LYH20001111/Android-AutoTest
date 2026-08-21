package com.hudou.autotest.ui.dialog.listener;

/**
 * 列表 + 操作按钮对话框回调
 */
public interface ListActionDialogListener {

    /**
     * @param selectedIndex 选中的列表项索引（0 开始），取消时为 -1
     * @param actionIndex   操作：0=取消、1=删除、2=修改
     */
    void onResult(int selectedIndex, int actionIndex);
}
