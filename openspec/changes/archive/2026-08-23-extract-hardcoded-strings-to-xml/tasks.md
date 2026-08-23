## 1. auto_test_strings.xml 新增英文报告字符串资源

- [x] 1.1 在 `auto_test_strings.xml` 中新增 `_en` 后缀的英文报告字符串（如 `report_summary_sheet_title_en`）

## 2. auto_test_strings.xml 新增中文字符串资源

- [x] 2.1 新增 Nav 相关字符串：`nav_home`、`nav_settings`
- [x] 2.2 新增 Permission 相关字符串：`permission_storage`
- [x] 2.3 新增 Dialog 按钮字符串：`dialog_confirm`（替换"确定"）
- [x] 2.4 新增 Keyboard 按钮字符串：`keyboard_confirm`（替换"确定"）
- [x] 2.5 新增 OptionsFragment 菜单字符串（含 %d 格式化）
- [x] 2.6 新增 BaseTestCase postValue 提示字符串（含 %s 格式化）
- [x] 2.7 新增 ReportOutput 报告标题和列名字符串（中文版）

## 3. OptionsFragment.java 替换

- [x] 3.1 替换菜单文案为 `getString(R.string.*)` 调用，使用 `String.format` 处理 %d 参数

## 4. BaseTestCase.java 替换

- [x] 4.1 替换 `postValue` 中的硬编码提示字符串为 `getString(R.string.*)` 调用
- [x] 4.2 使用 `String.format` 处理 %s 动态参数

## 5. ReportOutput.java 替换

- [x] 5.1 替换中文报告 sheet 标题和列名为 `getString(R.string.*)` 调用
- [x] 5.2 替换英文报告 sheet 标题和列名为 `getString(R.string.*)` 调用，英文资源使用 `R.string.report_xxx_en` 形式

## 6. Dialog.java 替换

- [x] 6.1 替换 `setPositiveButton("确定")` 为 `getString(R.string.dialog_confirm)`

## 7. NumberKeyBoardView.java 替换

- [x] 7.1 替换 `key.label = "确定"` 为 `getString(R.string.keyboard_confirm)`

## 8. Navigation 注解字符串提取

- [x] 8.1 `HomeFragment.java`：提取 `"首页"` 为 `static final String NAV_NAME`，注解引用该常量
- [x] 8.2 `AutoTestSettingFragment.java`：提取 `"设置"` 和 `"读写外部存储权限"` 为常量，注解引用
- [x] 8.3 修复 `AutoTestSettingFragment.java` 中遗漏的 `"测试失败"` → `TestResult.RESULT_FAIL` 替换

## 9. 验证

- [x] 9.1 确认所有硬编码中文字符串已被提取
- [x] 9.2 确认 `%d`、`%s` 格式化正确