package com.hudou.autotest.fragment.listener;

import java.util.List;

public interface SettingInterface {
    void onAddActions();

    /**
     * assets中的一级目录，默认加载app/src/main/assets/test下的文件
     * @return
     */
    List<String> addAssetsDirs();
    String onSetReportPath();
    String onSetTestFilesPath();
}
