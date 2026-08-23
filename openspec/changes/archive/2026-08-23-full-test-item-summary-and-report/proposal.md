## Why

当前"查看测试记录汇总"和"输出测试报告"功能仅汇总已执行的测试项数据（`resultItemList`），未执行的测试项（包括不适用当前设备的测试项）完全不出现在汇总中。用户无法从汇总中看到完整的测试项列表，也无法直观了解哪些测试项因设备不适用而被跳过。

## What Changes

- **"查看测试记录汇总"**：从仅遍历 `resultItemList` 改为遍历 `@TestItemClass.clz()` 中声明的所有测试项，按 `clz` 数组顺序排序
- **"输出测试报告"**：报告中的"测试案例结果汇总" sheet 同样改为遍历所有测试项，按 `clz` 顺序排序
- 对于不适用当前设备的测试项，失败数和总数两栏标注"不支持"（而非数值 0）
- `TableItem` 类改造：`failCount` 和 `totalCount` 从 `int` 改为 `String`，支持显示"不支持"文字
- 对于已执行且适用当前设备的测试项，显示实际统计数据（与现有逻辑一致）
- 对于未执行但适用当前设备的测试项，显示 0/0

## Capabilities

### New Capabilities

无新增能力。

### Modified Capabilities

- `test-execution/unsupported-device-test-item`: 新增一条需求，描述汇总和报告中不适用测试项的展示方式

## Impact

- **TableItem.java**: `failCount` 和 `totalCount` 字段从 `int` 改为 `String`
- **TableAdapter.java**: `onBindViewHolder` 中 `setText(String.valueOf(...))` 改为直接 `setText(...)`（适配 String 类型）
- **AutoTestSettingFragment.java**: `RECORD_SUMMARY` 分支改为从 `TestItemClass` 注解获取所有测试项，合并 `resultItemList` 数据，按 `clz` 顺序排序，不适用项标注"不支持"
- **ExcelUtils.java**: `writeDataToExcel` 方法中的汇总 sheet 写入逻辑改为遍历所有测试项，不适用项标注"不支持"
- **AutoTestTestListFragment.java**: 无需修改；其获取完整测试项列表的逻辑作为参考模式