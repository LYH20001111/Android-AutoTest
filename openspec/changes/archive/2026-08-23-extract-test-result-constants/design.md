## Context

当前测试结果状态字符串（"测试通过"、"测试失败"、"废弃案例"、"设备不支持"）以硬编码字面量形式散落在 `BaseTestCase.java`、`AutoTestTestItem.java`、`ExcelUtils.java` 三个文件中，共约 15 处引用。详见 proposal.md - Why。

## Goals / Non-Goals

**Goals:**
- 新建 `TestResult.java` 常量类，集中定义测试结果状态字符串
- 将全模块中所有匹配的硬编码字面量替换为 `TestResult.*` 常量引用
- 确保替换后行为完全一致

**Non-Goals:**
- 不修改 `ResultData.result` 字段的存储格式（仍为中文字符串）
- 不修改 `ResultData` 类本身
- 不引入新的结果状态值
- 不修改 Excel 报告中的英文映射逻辑（`PASS`/`NA`/`FAIL`）

## Decisions

### 1. 常量类命名：`TestResult` 而非 `TestResultConstants`

**选择：** 命名为 `TestResult`，位于 `com.hudou.autotest.constant` 包。

**理由：** 类名本身就是明确的常量含义，无需额外后缀。项目中已有 `SetMode` 等枚举类位于同一包，命名风格一致。

### 2. 常量命名风格：`RESULT_PASS` 而非 `PASS`

**选择：** 使用 `RESULT_` 前缀，如 `RESULT_PASS`、`RESULT_FAIL`、`RESULT_ABANDON`、`RESULT_DEVICE_UNSUPPORTED`。

**理由：** `PASS` 等名称过于通用，加 `RESULT_` 前缀在 import static 时不易混淆，IDE 自动补全也更清晰。

### 3. 引用方式：直接 import static

**选择：** 在引用文件中使用 `import static com.hudou.autotest.constant.TestResult.*` 静态导入。

**理由：** 代码最简洁，修改最少。`BaseTestCase` 中引用 4 个常量，`AutoTestTestItem` 中 2 个，`ExcelUtils` 中 4 个，静态导入最为合适。

## Risks / Trade-offs

- **Missed occurrence 风险**：手动替换可能遗漏某个字面量。→ 使用 `Grep` 全量搜索确认，替换后再次搜索确认无残留
- **import static 冲突**：`BaseTestCase` 和 `ExcelUtils` 中已有大量 import，静态导入不会与现有 import 冲突