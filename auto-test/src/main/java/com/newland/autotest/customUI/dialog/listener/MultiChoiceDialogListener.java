package com.newland.autotest.customUI.dialog.listener;

import java.util.ArrayList;

public interface MultiChoiceDialogListener {
    /**
     * @param choiceList
     */
    void onResult(ArrayList<Integer> choiceList);
}
