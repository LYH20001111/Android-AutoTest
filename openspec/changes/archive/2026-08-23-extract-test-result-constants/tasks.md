## 1. 新建 TestResult 常量类

- [x] 1.1 在 `com.hudou.autotest.constant` 包下新建 `TestResult.java`，定义 4 个常量：`RESULT_PASS`、`RESULT_FAIL`、`RESULT_ABANDON`、`RESULT_DEVICE_UNSUPPORTED`

## 2. AutoTestTestItem.java 替换

- [x] 2.1 添加 `import static com.hudou.autotest.constant.TestResult.*`
- [x] 2.2 替换 `recordPass()` 中的 `"测试通过"` → `RESULT_PASS`（3 处：if 判断、setResult、postValue）
- [x] 2.3 替换 `recordFail()` 中的 `"测试失败"` → `RESULT_FAIL`（3 处：if 判断、setResult、postValue）

## 3. BaseTestCase.java 替换

- [x] 3.1 添加 `import static com.hudou.autotest.constant.TestResult.*`
- [x] 3.2 替换执行逻辑中的 `"废弃案例"` → `RESULT_ABANDON`（2 处：setResult、postValue indexOf）
- [x] 3.3 替换执行逻辑中的 `"设备不支持"` → `RESULT_DEVICE_UNSUPPORTED`（1 处：setResult）
- [x] 3.4 替换执行逻辑中的 `"测试失败"` → `RESULT_FAIL`（2 处：setResult、postValue）
- [x] 3.5 替换 `testItemNoExecutedCasesNum()` 中的 `"测试通过"`、`"废弃案例"`、`"设备不支持"` → `RESULT_PASS`、`RESULT_ABANDON`、`RESULT_DEVICE_UNSUPPORTED`
- [x] 3.6 替换 `testItemFailedCasesNum()` 中的 `"测试失败"` → `RESULT_FAIL`

## 4. ExcelUtils.java 替换

- [x] 4.1 添加 `import static com.hudou.autotest.constant.TestResult.*`
- [x] 4.2 替换 detail sheet 写入中的 `"测试通过"`、`"废弃案例"`、`"设备不支持"`、`"测试失败"` → 对应常量
- [x] 4.3 替换 `countPass()` 中的 `"测试通过"` → `RESULT_PASS`
- [x] 4.4 替换 `countFail()` 中的 `"测试失败"` → `RESULT_FAIL`
- [x] 4.5 替换 `countAbandon()` 中的 `"废弃案例"`、`"设备不支持"` → `RESULT_ABANDON`、`RESULT_DEVICE_UNSUPPORTED`