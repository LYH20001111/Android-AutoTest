## Context

当前"查看测试记录汇总"（RECORD_SUMMARY）和"输出测试报告"（ExcelUtils.writeDataToExcel）均仅遍历 `AutoTestMainActivity.resultItemList`，该列表只包含已执行的测试项。未执行的测试项（包括未执行的适用项和不适用设备项）完全不在汇总中出现。

`AutoTestTestListFragment` 通过 `@TestItemClass.clz()` 注解获取所有测试项的完整列表，但该信息在 `AutoTestSettingFragment` 中不可直接访问。`TableItem` 类的 `failCount` 和 `totalCount` 为 `int` 类型，无法表示"不支持"这种非数值状态。

## Goals / Non-Goals

**Goals:**
- 汇总和报告中展示所有测试项（按 `@TestItemClass.clz()` 顺序）
- 不适用当前设备的测试项在失败数和总数栏显示"不支持"
- 按 `clz` 数组顺序排序

**Non-Goals:**
- 不修改报告中的详情 sheet（每条测试案例的详细记录保持不变）
- 不修改 `TestItemClass` 注解定义
- 不修改 `DeviceUtils` 工具类

## Decisions

### 1. 全量测试项列表的获取方式：在 AutoTestMainActivity 中增加静态字段

**选择：** 在 `AutoTestMainActivity` 中新增 `static Class<? extends BaseTestCase>[] allTestItemClasses` 字段，在 `AutoTestTestListFragment.onInitData()` 中填充，在 `AutoTestSettingFragment` 中读取。

**理由：**
- 遵循现有模式：`resultItemList` 和 `resultData` 已在 `AutoTestMainActivity` 中作为静态字段存储
- `AutoTestSettingFragment` 和 `AutoTestTestListFragment` 是平级 fragment，没有直接继承关系，需要通过共享静态数据来通信
- 避免在 `AutoTestSettingFragment` 中重复扫描注解的复杂性

**替代方案考虑：**
- 在 `AutoTestSettingFragment` 中自行扫描 `@TestItemClass` 注解：需要知道具体 fragment 子类 class，架构耦合度高
- 通过 FragmentManager 查找 `AutoTestTestListFragment` 实例：生命周期管理复杂，且可能已被销毁

### 2. TableItem 类型改造：failCount / totalCount 从 int 改为 String

**选择：** 将 `TableItem.failCount` 和 `TableItem.totalCount` 从 `int` 改为 `String`，构造函数改为接收 `String` 参数。

**理由：**
- 需要支持"不支持"这种非数值内容
- 现有构造调用 `new TableItem(name, countFail(data), data.size())` 中 `countFail()` 返回 `int`，`data.size()` 返回 `int`，改为 `String.valueOf(...)` 适配即可
- `TableAdapter` 中 `holder.failCount.setText(String.valueOf(item.getFailCount()))` 可简化为 `holder.failCount.setText(item.getFailCount())`

### 3. 汇总数据装配逻辑

**流程：**
1. 从 `AutoTestMainActivity.allTestItemClasses` 获取所有测试项 class 列表（按 `clz` 数组顺序）
2. 为每个 class 在 `resultItemList` 中查找对应的 `ResultItem`
3. 判断逻辑：
   - 找到且不适用当前设备 → 显示"不支持" / "不支持"
   - 找到且适用当前设备 → 显示实际统计数据
   - 未找到（未执行）且不适用当前设备 → 显示"不支持" / "不支持"（通过 `Item.isUnsupportedOnCurrentDevice()` 判断）
   - 未找到（未执行）且适用当前设备 → 显示 0 / 0

**应用到两个场景：**
- RECORD_SUMMARY（`AutoTestSettingFragment` 中的弹窗表格）：使用上述逻辑构建 `List<TableItem>`
- Excel 报告汇总 sheet（`ExcelUtils.writeDataToExcel`）：使用上述逻辑写入汇总数据

## Risks / Trade-offs

- **静态字段生命周期**：`allTestItemClasses` 在 `AutoTestTestListFragment.onInitData()` 中填充，如果用户未进入测试项列表页面就直接进入设置页面查看汇总，该字段可能为空。→ 增加空值保护，为空时退回到原有逻辑（只展示 `resultItemList`）
- **Excel 报告改造范围**：`ExcelUtils.writeDataToExcel` 的汇总 sheet 写入逻辑需要重构，但详情 sheet 保持不变。→ 拆分清晰，风险可控
- **不适用设备判断时机**：`isUnsupportedOnCurrentDevice()` 使用 `DeviceUtils.isDeviceUnsupported()`，内部基于 `Build.MODEL`，在运行时为常量，无性能问题