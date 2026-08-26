## Why

当前测试报告汇总表中，"案例总数"一列显示的是实际执行的测试数量，但用户无法区分一个测试项"定义了多少案例"和"执行了多少案例"。测试项可能定义了10个案例，但只执行了8个（例如因中断或条件限制），现有界面无法直观体现这一差异，导致用户无法快速判断测试覆盖率。

## What Changes

- **测试报告汇总表**：新增"案例总数"列，显示每个测试项定义的案例总数；将原"案例总数"列重命名为"测试总数"，表示实际执行的案例数量
- **设置页面的"查看测试记录汇总"**：同步上述列名变更和新增列逻辑
- 相应的中英文字符串资源更新

## Capabilities

### New Capabilities

- `test-execution/test-report-columns`: 测试报告汇总表新增"案例总数"列，原"案例总数"列重命名为"测试总数"

### Modified Capabilities

- *(无现有 spec 行为变更，为新能力)*

## Impact

- **auto_test_strings.xml**: 新增 `report_summary_case_total_num`（案例总数）和英文对应字符串；`report_summary_total_num` 改为"测试总数"（英文保持 `Total Num` 不变或调整为 `Executed Num`）
- **TableItem.java**: 新增 `caseTotalCount` 字段，用于存储测试项定义的案例总数
- **TableAdapter.java**: 新增一列显示"案例总数"，原"案例总数"改为"测试总数"
- **AutoTestSettingFragment.java**: RECORD_SUMMARY 分支同步新增和重命名列
- **ReportOutput.java / ExcelUtils.java**: Excel 汇总 sheet 同步新增和重命名列