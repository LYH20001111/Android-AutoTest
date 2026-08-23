## 1. 数据层改造

- [x] 1.1 在 `AutoTestMainActivity` 中新增 `static Class<? extends BaseTestCase>[] allTestItemClasses` 字段，用于存储所有测试项 class 列表
- [x] 1.2 在 `AutoTestTestListFragment.onInitData()` 中，构建完 `items` 列表后，将 `testItems` 数组赋值给 `AutoTestMainActivity.allTestItemClasses`

## 2. TableItem 类型改造

- [x] 2.1 将 `TableItem.failCount` 和 `TableItem.totalCount` 从 `int` 改为 `String`，构造函数对应改为 `String` 参数，getter 返回类型改为 `String`
- [x] 2.2 更新 `TableAdapter.onBindViewHolder`：`holder.failCount.setText(item.getFailCount())` 和 `holder.totalCount.setText(item.getTotalCount())`（直接传 String，无需 String.valueOf）

## 3. RECORD_SUMMARY 汇总逻辑改造

- [x] 3.1 在 `AutoTestSettingFragment` 的 `RECORD_SUMMARY` 分支中，从 `AutoTestMainActivity.allTestItemClasses` 获取所有测试项 class，按数组顺序遍历
- [x] 3.2 对每个 class，构造 `Item` 实例并调用 `isUnsupportedOnCurrentDevice()` 判断：不适用则标注"不支持"；适用则在 `resultItemList` 中查找对应 `ResultItem`，获取实际统计数据
- [x] 3.3 更新 `TableItem` 构造调用，将 `int` 参数改为 `String.valueOf(...)` 或 "不支持" 字符串

## 4. Excel 报告汇总 sheet 逻辑改造

- [x] 4.1 在 `ExcelUtils.writeDataToExcel` 中，汇总 sheet 的写入逻辑改为从 `AutoTestMainActivity.allTestItemClasses` 获取所有测试项，按数组顺序遍历
- [x] 4.2 对每个 class，判断不适用状态：不适用则在汇总行中写入"不支持"；适用则在 `resultItemList` 中查找对应数据并写入实际统计
- [x] 4.3 详情 sheet 的写入逻辑保持不变（仍基于 `resultItemList`）