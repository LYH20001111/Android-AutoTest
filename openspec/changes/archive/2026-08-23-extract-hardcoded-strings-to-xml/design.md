## Context

当前 auto-test 模块有约 35 个硬编码中文字符串散落在 7 个 Java 文件中，包括测试项选项菜单、案例执行提示、报告标题列名、按钮文字、Navigation 名称等。这些字符串全部为用户可见的 UI 文案。

## Goals / Non-Goals

**Goals:**
- 将所有用户可见的硬编码中文字符串提取到 `auto_test_strings.xml`，使用 `R.string.*` 引用
- 对带动态参数的字符串（如 `String.format("xxx %d", count)`）使用 `%1$d`、`%1$s` 格式化占位符
- 在 `auto_test_strings.xml` 中为英文报告字符串添加 `_en` 后缀的资源条目，通过 `isEnglishReport()` 分支选择使用中文或英文资源
- 替换后所有功能行为不变

**Non-Goals:**
- 不提取 `TestResult.java` 中的 4 个业务状态常量（保持常量类管理）
- 不提取日志 TAG、异常信息、技术标识等非用户可见字符串
- 不修改 `BaseTestCase.java` 中的 `TestResult` 常量和颜色值引用
- 不修改 `ExcelUtils.java` 中的 `TestResult` 常量引用

## Decisions

### 1. 字符串命名规范

**格式：** 使用 `{source_file_location}_{purpose}` 的 snake_case 命名。

| 源文件 | 命名前缀 | 示例 |
|--------|---------|------|
| OptionsFragment | `options_` | `options_current_item` |
| BaseTestCase | `post_value_` | `post_value_start_case` |
| ReportOutput | `report_` | `report_summary_sheet_title` |
| Dialog | `dialog_` | `dialog_confirm` |
| NumberKeyBoardView | `keyboard_` | `keyboard_confirm` |
| HomeFragment | `nav_` | `nav_home` |
| AutoTestSettingFragment | `nav_` / `permission_` | `nav_settings`, `permission_storage` |

### 2. 格式化占位符方案

| 原始代码 | 替换后 XML | 原始 Java 引用 |
|----------|-----------|---------------|
| `"当前测试项 ： %s\\n1. 运行..."` | `<string name="options_menu">当前测试项 ： %1$s</string>` + 各选项独立 | `String.format(getString(R.string.options_menu), itemName)` |
| `"开始执行案例：%s"` | `<string name="post_value_start_case">开始执行案例：%1$s</string>` | `String.format(getString(R.string.post_value_start_case), caseName)` |
| `"案例提示：%s"` | `<string name="post_value_case_tip">案例提示：%1$s</string>` | `String.format(getString(R.string.post_value_case_tip), tip)` |
| `"查看所有案例详情(%d)"` | `<string name="options_view_all_details">查看所有案例详情(%1$d)</string>` | `String.format(getString(R.string.options_view_all_details), count)` |

### 3. 英文报告字符串管理

英文报告字符串使用 `_en` 后缀命名（如 `report_summary_sheet_title_en`），与中文资源（`report_summary_sheet_title`）一同放在 `auto_test_strings.xml` 中。`ReportOutput.java` 通过 `isEnglishReport()` 条件判断选择使用 `R.string.report_xxx_en` 或 `R.string.report_xxx`。

### 4. @Navigation 注解处理

`@Navigation(name = "首页")` 和 `@Navigation(name = "设置")` 中的字符串值必须为**编译期常量**（`static final`），不能使用 `R.string` 资源引用。因此：
- 提取为 `HomeFragment.java` 和 `AutoTestSettingFragment.java` 中的 `private static final String NAV_NAME = "首页"` 等常量
- 将注解值改为引用常量：`@Navigation(name = HomeFragment.NAV_NAME)`

## Risks / Trade-offs

- **Navigation 注解限制**：`@Navigation(name = ...)` 要求值必须是编译期常量，不能使用 `R.string`，需要提取为 Java 常量
- **String.format 兼容性**：`postValue()` 中的颜色参数与字符串拼接需要保持顺序，确保 `String.format` 后的字符串传递给 `postValue` 正确
- **ReportOutput 英文分支**：`ReportOutput.java` 的英文分支已有硬编码英文字符串，这些应提取为 `auto_test_strings.xml` 中的 `_en` 后缀资源，但当前阶段的英文翻译仅需覆盖报告相关字符串