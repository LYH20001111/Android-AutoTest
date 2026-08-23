## Why

当前测试结果状态字符串（"测试通过"、"测试失败"、"废弃案例"、"设备不支持"）在多个文件中以硬编码字面量形式出现，散落在 `BaseTestCase.java`、`AutoTestTestItem.java`、`ExcelUtils.java` 等文件中。这种重复分散的字符串字面量导致：
- 拼写错误风险高（如某处漏改则行为不一致）
- 难以统一维护（如需要修改文案需逐一搜索替换）
- 代码可读性差（字符串字面量含义不如常量名直观）

## What Changes

- 新建 `TestResult.java` 常量类，位于 `com.hudou.autotest.constant` 包，包含以下常量：
  - `RESULT_PASS = "测试通过"`
  - `RESULT_FAIL = "测试失败"`
  - `RESULT_ABANDON = "废弃案例"`
  - `RESULT_DEVICE_UNSUPPORTED = "设备不支持"`
- 将全模块中所有匹配的硬编码字符串字面量替换为 `TestResult.*` 常量引用
- 此为纯重构，不涉及任何行为变更

## Capabilities

### New Capabilities

无新增能力

### Modified Capabilities

无既有能力变更

此变更为纯重构（`skip_specs: true`），不涉及任何规格级行为变更。

## Impact

- 新建 `TestResult.java` 常量类
- `BaseTestCase.java`：替换 `"测试通过"`、`"测试失败"`、`"废弃案例"`、`"设备不支持"` 字面量
- `AutoTestTestItem.java`：替换 `"测试通过"`、`"测试失败"` 字面量
- `ExcelUtils.java`：替换 `"测试通过"`、`"测试失败"`、`"废弃案例"`、`"设备不支持"` 字面量