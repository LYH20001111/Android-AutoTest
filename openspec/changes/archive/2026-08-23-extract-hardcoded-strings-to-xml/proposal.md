## Why

当前 auto-test 模块有大量用户可见的字符串（如菜单文案、提示消息、报告标题等）以硬编码中文字面量形式直接写在 Java 源文件中。这种方式导致：
- 无法支持多语言（无独立英文 strings 文件）
- 字符串散落在多个文件中，难以统一维护
- 一些字符串已存在于 `auto_test_strings.xml` 中却未被引用，存在重复定义
- 部分字符串（如点按菜单文案、按钮文案）未使用 `%d`、`%s` 格式化规则优化

## What Changes

- 将 Java 源文件中所有用户可见的硬编码中文字符串提取到 `auto_test_strings.xml` 中统一管理
- 使用 `%d`、`%s` 格式化占位符替换拼接字符串，消除 `+` 拼接
- 在 `auto_test_strings.xml` 中为英文报告字符串添加 `_en` 后缀的资源条目（如 `report_summary_sheet_title_en`），使英文报告模式可复用资源
- 替换 Java 源文件中的字面量引用为 `R.string.*` 或 `getString(R.string.*)` 调用
- 此为纯重构，不涉及任何行为变更

## Capabilities

### New Capabilities

无新增能力

### Modified Capabilities

无既有能力变更

此变更为纯重构（`skip_specs: true`），不涉及任何规格级行为变更。

## Impact

### 涉及文件

| 文件 | 硬编码字符串数量 | 说明 |
|------|-----------------|------|
| `OptionsFragment.java` | ~10 | 测试项选项菜单文案（含 %d 格式化） |
| `BaseTestCase.java` | ~8 | 案例执行中的提示消息（postValue） |
| `ReportOutput.java` | ~12 | 测试报告 sheet 标题和列名 |
| `Dialog.java` | 1 | 弹窗按钮"确定" |
| `NumberKeyBoardView.java` | 1 | 键盘按钮"确定" |
| `HomeFragment.java` | 1 | `@Navigation(name = "首页")` |
| `AutoTestSettingFragment.java` | 2 | `@Navigation` + 权限数组 |

### 资源文件

| 文件 | 操作 |
|------|------|
| `auto_test_strings.xml` | 新增约 50 个字符串条目（含中文及英文报告字符串） |

### 排除项

- `TestResult.java` 中的 4 个常量：保持原有常量类管理，不移至 XML（这些是业务状态值，非 UI 字符串）
- 日志 TAG 字符串、异常信息：非用户可见，无需提取
- 纯英文字符串（如报告文件名、技术标识）：无需提取