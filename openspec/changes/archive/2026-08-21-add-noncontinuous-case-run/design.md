# Design: 运行部分不连续案例

## Context

现有执行链路（见 proposal.md - Why）：

- OptionsFragment 提供 1-7 号选项，数字键盘输入后跳转对应界面；"2/3"（运行某个/部分连续案例）跳转 ExecutionFragment 完成输入交互后进入 ExecutionDetailsFragment 执行。
- ExecutionFragment 已具备数字键盘监听、物理键盘映射（KEYCODE_ENTER → callOnOK）、全部案例展示（viewCaseDetails 经 postValue 输出到消息区）等完整交互骨架。
- BaseTestCase 的执行统一收敛到私有 `runTestCases(clz, Method[])`（线程池 + CountDownLatch + isCompleted 状态），`runCase`/`runPartContinueCases` 只是把 case 号映射为 Method 数组。
- ExecutionDetailsFragment 通过 option 分支调用执行方法，并负责"等待执行完成才允许返回"的后退拦截。
- 源码工作区文件为 TSZ# 加密格式，修改需经 git 明文写回（既有坑，见 common_pitfalls 记忆）。

## Goals / Non-Goals

**Goals:**

- 在 auto-test 模块内实现"运行部分不连续案例"：选项 4 入口、待执行列表收集/去重/特定删除、按加入顺序依次执行。
- 最大化复用 ExecutionFragment / ExecutionDetailsFragment / runTestCases 既有骨架，执行语义（废弃、设备不支持、统计、postValue 输出）与单个案例完全一致。
- 兼容物理键盘模式与软键盘模式。

**Non-Goals:**

- 不改动案例排序规则（仍按方法名排序映射案例号）。
- 不做待执行列表的持久化（离开界面即清空）。
- 不调整其他执行选项（1/2/3/5-8）的行为，仅编号顺延。

## Decisions

### D1. 入口与编号：Option 常量调整，新常量占用 "4"

OptionsFragment.Option 中新增 `RUN_PART_NONCONTINUOUS_CASES = "4"`，`VIEW_ALL_CASES` 改为 `"5"`、`VIEW_ABANDON_CASES` 改为 `"6"`、`VIEW_UNEXECUTED_CASES` 改为 `"7"`、`VIEW_FAILED_CASES` 改为 `"8"`；菜单文案同步顺延并新增第 4 项。onInsertKeyEvent 中"4"与"2/3"同走 ExecutionFragment 分支。

- 备选：新选项排最后（"8"）。用户已明确要求占用"4"（BREAKING，proposal 已标注）。
- 影响面：所有引用旧常量的文件（ExecutionFragment.actionByOption、ExecutionDetailsFragment import）只需改常量定义处，其余引用点自动跟随。

### D2. 交互载体：扩展 ExecutionFragment 新增 option 分支，而非新建 Fragment

新选项复用 ExecutionFragment：新增 `RUN_PART_NONCONTINUOUS_CASES` 分支（actionByOption 中隐藏 ll_line2 的固定提示、保留键盘），新增字段 `List<Integer> selectedIds`（保持加入顺序）。onOK 改为按"输入框是否有数字"分流；待执行列表 TextView（`tv_selected_ids`）作为唯一编辑入口（点击触发）。

- 备选：新建独立 Fragment。代价是复制约百行键盘/物理键盘样板，且消息区输出（AutoTestMainActivity.llMessage）绑定逻辑重复，收益仅是代码隔离。扩展既有类更符合项目现状（ExecutionFragment 本就是 1/2/3/5/6/7 的通用载体）。
- 布局改动：在 ll_line2 与分隔线之间新增 `tv_selected_ids`（显示"已选案例（N）：1、3、5"，可点击，作为编辑入口）；ll_line3 区域保持原 RUN_PART_CASES 用途不变，不新增任何按钮。

### D3. 确认键语义：输入框有数字 = 加入列表；无数字 = 开始执行（用户已确认）

onOK 分流：

- 输入框非空 → 解析并校验（0 ≤ id < testItemCasesNum，参考 RUN_ONE_CASE 的现有校验与 Dialog.notifyDialog 提示模式）→ 已存在则提示"已在待执行列表"（postValue 红色）→ 否则加入 selectedIds、清空输入、刷新 tv_selected_ids、postValue 提示加入成功。
- 输入框为空 → selectedIds 非空则跳转 ExecutionDetailsFragment 执行；为空则提示先添加案例。

物理键盘的 KEYCODE_ENTER 已映射 callOnOK，自动获得相同语义，无需额外处理。

### D4. 特定编辑：单弹窗完成——列表选中目标 + 取消/删除/修改三按钮

点击待执行列表 TextView（`tv_selected_ids` 有效区域）→ 自定义对话框：标题"选择要编辑的案例号"，单选列表列出已选案例号（选中即目标），底部固定三按钮"取消 / 删除 / 修改"：

- 删除：按索引从 selectedIds 移除并刷新显示；
- 修改：`Dialog.editDialog` 输入新案例号（onlyNumber）→ 校验（0 ≤ 新号 < 案例总数；新号不与列表中其他案例号重复）→ `selectedIds.set(index, newId)` 替换并刷新显示。

**Dialog 工具类新增静态方法** `listActionDialog(context, title, items, listener)`（标题 + 单选列表 + 三按钮，风格与现有 Dialog 工具类一致），回调 `onResult(int selectedIndex, int actionIndex)`（actionIndex：0=取消、1=删除、2=修改）；未选中案例时点删除/修改忽略，取消关闭弹窗。现有 singleChoiceDialog 只回调选中项、无法同窗执行操作，故需新增该方法。

列表为空时点击无响应。为避免入口不可见，进入新分支时 postValue 提示"点击上方待执行列表可编辑已选案例"。

- 备选 A：独立"编辑已选案例"按钮入口。用户选择去掉按钮、直接点击列表区域触发，界面更简洁。
- 备选 B：长按删除键逐条删除——只能删最后一个，不满足"特定删除/修改"；多次弹窗逐条删——交互繁琐。单选对话框一次列出全部已选，与"特定编辑"语义最贴合。
- 备选 C：两步对话框（先选案例号、再选删除/修改操作）——即先前实现方案，因操作步骤多被用户否决，改为单弹窗三按钮直接执行。

### D5. 执行链路：BaseTestCase.runPartCases(clz, int[]) + ExecutionDetailsFragment 新构造重载

BaseTestCase 新增公共方法（与 runCase/runPartContinueCases 同构）：

```
runPartCases(Class clz, int[] ids):
  排序方法数组（与 runCase 相同的 filter+sorted）
  按 ids 顺序映射为 Method[]（越界 id 忽略，防御性校验）
  runTestCases(clz, runMethods)
```

ExecutionDetailsFragment 新增构造重载 `(clz, testItem, option, int[] ids)`（保留现有 6 参构造不动，避免影响 RUN_ONE_CASE/RUN_PART_CASES 调用点），新 option 分支在 tvLine2Message 显示"1、3、5"并调用 `runPartCases`。返回拦截、isCompleted 等待逻辑天然复用。

- 备选：复用 6 参构造传数组——int 与 int[] 不兼容，需改现有调用点，破坏性大。

### D6. 提示与文案

执行类提示用 `AutoTestMainActivity.getRecorder().postValue(...)`（消息区，与现有 RUN_ONE_CASE 展示案例详情、执行输出同一通道）；标题/输入框类文案新增到 strings.xml（项目现有资源风格），不新增按钮文案。

## Risks / Trade-offs

- [编号顺延（4→8）破坏既有操作习惯] → proposal 已按 BREAKING 标注；查看类选项行为本身不变，仅编号变化。
- [ll_line3 复用与 RUN_PART_CASES 分支冲突] → 两个分支互斥（不同 option 进入），且 RUN_PART_CASES 分支每次都重新 setVisibility/setText，互不干扰；实现时在 actionByOption 内集中管理可见性。
- [执行中用户快速连按确认导致重复跳转] → 跳转前已判空输入与列表，跳转后 Fragment 即被替换，残留事件由既有 postDelayed(100ms) 模式消化（与 RUN_ONE_CASE 相同）。
- [工作区源码加密，直接编辑会写回乱码] → 按既有流程经 git 明文修改并写回，任务清单中显式包含该步骤。

## Migration Plan

无数据迁移。改动仅 auto-test 模块源码与资源；编号变化属用户可见变更，随版本发布即可。回滚：还原 OptionsFragment/ExecutionFragment/BaseTestCase/ExecutionDetailsFragment 与布局/strings 的改动即可（git revert）。

## Open Questions

无。
