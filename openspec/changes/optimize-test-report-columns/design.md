## Context

当前测试报告汇总表（RECORD_SUMMARY 和 Excel 输出）中，列结构为：
- 案例测试项 (Test Item)
- 案例总数 (Total Num) → 实际显示的是已执行案例数
- 案例通过数 (Pass Num)
- 案例废弃数 (Abandon Num)
- 案例失败数 (Fail Num)
- 通过率 (Pass Rate)
- 开始时间 / 结束时间 / 总时长

`BaseTestCase.testItemCasesNum(Class<?>)` 已存在，通过反射扫描 `@TestCase` 注解获取测试项定义的案例总数。`resultDataList.size()` 获取实际执行的案例数。两者在现有代码中均可获取，但界面只显示了后者。

参见 `proposal.md - Why` 获取变更动机。

## Goals / Non-Goals

**Goals:**
- 在测试报告汇总表（RECORD_SUMMARY 弹窗）中新增"案例总数"列，显示 `testItemCasesNum()` 返回值
- 将原"案例总数"列重命名为"测试总数"，`resultDataList.size()` 语义不变
- 在 Excel 汇总 sheet 中同步新增列和列名变更
- 更新中英文字符串资源

**Non-Goals:**
- 不修改 `TableItem` 的 `totalCount` 字段类型或语义（保持 `String`，支持"不支持"）
- 不修改详情 sheet 的数据结构
- 不修改 `BaseTestCase.testItemCasesNum()` 方法本身
- 不修改 `AutoTestMainActivity` 或 `AutoTestTestListFragment`

## Decisions

### 1. TableItem 新增 caseTotalCount 字段

**选择：** 在 `TableItem` 中新增 `String caseTotalCount` 字段，构造函数新增对应参数，添加 getter 方法。

**理由：**
- 与现有 `failCount`、`totalCount` 字段类型一致（均为 `String`，支持"不支持"文本）
- 最小化对现有代码的影响

### 2. 布局文件新增案例总数列

**选择：** 在 `auto_test_table_row_item.xml` 中新增一个 `TextView`（id: `caseTotalCount`），放在 `caseItem` 之后、`failCount` 之前。

**理由：**
- 列顺序为：案例测试项 → 案例总数(新) → 测试总数(原totalCount) → 案例通过数 → 案例废弃数 → 案例失败数
- 逻辑上"案例总数"应在最前面

### 3. 数据来源：BaseTestCase.testItemCasesNum()

**选择：** 在 `AutoTestSettingFragment.RECORD_SUMMARY` 分支中，对每个测试项 class 调用 `testItemCasesNum(clz)` 获取定义案例数，作为 `caseTotalCount` 的值。

**理由：**
- `testItemCasesNum()` 已是 `BaseTestCase` 的公有方法，可直接调用
- 对于不适用设备（unsupported）的测试项，`caseTotalCount` 显示"不支持"

### 4. Excel 汇总 sheet 列结构调整

**选择：** 在 Excel 汇总 sheet 中，列索引 1 插入新列"案例总数"（或 "Case Total"），原列索引 1-8 依次右移至 2-9。更新所有对应列索引的数据写入逻辑。

**理由：**
- 保持与 UI 汇总表的列顺序一致
- 需要更新的代码区域：列头写入、数据行写入（unsupported/found/not found 三种分支）、汇总行

### 5. 字符串资源变更

| 资源名 | 原值 | 新值 |
|--------|------|------|
| `report_summary_total_num` | 案例总数 | 测试总数 |
| `report_summary_total_num_en` | Total Num | Executed Num |
| (新增) `report_summary_case_total_num` | - | 案例总数 |
| (新增) `report_summary_case_total_num_en` | - | Case Total |

## Risks / Trade-offs

- **Excel 列索引变更风险**：`writeDataToExcel` 中汇总 sheet 的列索引 1-8 均需 +1（新增列后变成 2-9），需要逐一检查所有数据写入和汇总行，确保不遗漏。详情 sheet 不受影响。
- **Layout 宽度问题**：`auto_test_table_row_item.xml` 原有 3 列（caseItem, failCount, totalCount），新增 1 列后变为 4 列，`layout_weight` 分配仍为 1:1:1:1，需确保在小屏幕设备上不出现文字截断。
- **向后兼容性**：现有已输出的 Excel 文件不会自动更新列结构，仅新生成的报告包含新列。

## Open Questions

无。所有设计细节已明确。