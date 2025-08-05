package com.hudou.autotest.ui.dialog.listener;

public interface SingleChoiceDialogListener {
    /**
     * @param id 选中的id，从0开始，-1为取消
     */
    void onResult(int id);
}
