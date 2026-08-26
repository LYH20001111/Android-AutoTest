## 1. 字符串资源更新

- [x] 1.1 在 `auto_test_strings.xml` 中新增 `report_summary_case_total_num`（案例总数）和 `report_summary_case_total_num_en`（Case Total）
- [x] 1.2 将 `report_summary_total_num` 的值从"案例总数"改为"测试总数"
- [x] 1.3 将 `report_summary_total_num_en` 的值从"Total Num"改为"Executed Num"

## 2. TableItem 数据模型

- [x] 2.1 在 `TableItem.java` 中新增 `String caseTotalCount` 字段、构造函数参数和 `getCaseTotalCount()` 方法
- [x] 2.2 更新所有 `TableItem` 构造调用处，传入 `caseTotalCount` 参数

## 3. 布局文件更新

- [x] 3.1 在 `auto_test_table_row_item.xml` 中新增 `caseTotalCount` TextView（放在 `caseItem` 之后、`failCount` 之前），`layout_weight` 设为 1

## 4. TableAdapter 更新

- [x] 4.1 在 `TableAdapter.ViewHolder` 中新增 `caseTotalCount` 字段并绑定 `R.id.caseTotalCount`
- [x] 4.2 在 `onBindViewHolder` 中设置 `holder.caseTotalCount.setText(item.getCaseTotalCount())`

## 5. RECORD_SUMMARY 汇总逻辑更新

- [x] 5.1 在 `AutoTestSettingFragment` 的 RECORD_SUMMARY 分支中，对每个测试项 class 调用 `BaseTestCase.testItemCasesNum(clz)` 获取定义案例数
- [x] 5.2 构造 `TableItem` 时传入 `caseTotalCount` 参数（unsupported 传"不支持"，正常传 `String.valueOf(testItemCasesNum(clz))`）

## 6. Excel 汇总 sheet 列结构调整

- [x] 6.1 更新 `ExcelUtils.writeDataToExcel` 中汇总 sheet 的列头写入，新增"案例总数"/"Case Total"列，原"Total Num"改为"Executed Num"
- [x] 6.2 更新 unsupported 分支：列索引 1 写入"不支持"（案例总数），原列索引 1-8 改为 2-9
- [x] 6.3 更新 found 分支：列索引 1 写入 `String.valueOf(testItemCasesNum(clz))`，原列索引 1-8 改为 2-9
- [x] 6.4 更新 not found 分支：列索引 1 写入"0"，原列索引 1-8 改为 2-9
- [x] 6.5 更新仅遍历 `resultItemList` 的回退分支：列索引 1 写入 `String.valueOf(testItemCasesNum(item.getClz()))`，原列索引 1-8 改为 2-9
- [x] 6.6 更新汇总行 `total_number` 和 `total_time` 的列索引

## 7. ReportOutput 更新

- [x] 7.1 检查 `ReportOutput.java` 中汇总 sheet 的列头和数据写入，同步新增"案例总数"列和重命名"测试总数"列