# Design: 执行开始后清空案例输入缓存

## Context

现有执行链路（见 proposal.md - Why）：选项 3/4 均经 ExecutionFragment 输入后以 `replace(R.id.main_layout, ...)` + `addToBackStack(...)` 跳转 ExecutionDetailsFragment 执行。

- ExecutionFragment 持有实例字段：选项 3 的 `beginId`/`endId`（editDialog 输入，展示在按钮文本上）；选项 4 的 `selectedIds`（`final List<Integer>`，展示在 tvSelectedIds 上）。
- 返回机制：`addToBackStack` 后按返回键 `popBackStack` 恢复原 Fragment 实例（不重建、不重新走 `onCreateView`），实例字段与视图文本原样保留 → 输入缓存残留的根因。
- 工作区源码为 TSZ# 加密格式，修改需经 git 明文导出/写回（既有坑，见 common_pitfalls 记忆）。

## Goals / Non-Goals

**Goals:**

- 选项 3/4 从 ExecutionDetailsFragment 返回 ExecutionFragment 时，输入状态完全清空（按钮恢复默认文案 / 待执行列表显示"已选案例（0）："）。
- 清空与跳转执行原子绑定，覆盖所有返回路径（物理返回键、界面返回按钮等）。

**Non-Goals:**

- 不处理选项 1/2/5-8 的输入残留（用户未要求；选项 2 的 tvCaseId 文本残留不在本次范围）。
- 不改变执行链路、跳转方式、待执行列表编辑交互等既有行为。
- 不做待执行列表持久化（离开即清空，与既有设计 Non-Goal 一致）。

## Decisions

### D1. 清空时机：跳转执行时点清空，而非返回时清空

在跳转 ExecutionDetailsFragment 的代码路径内清空输入状态。

- 备选 A：onResume 中检测"从详情页返回"再清空——需区分首次进入与返回，依赖 back stack 监听或额外标记位，时序复杂且易漏（如 Activity 重建、执行中多次跳转）。
- 备选 B：在 ExecutionDetailsFragment 弹栈前清空——职责错位，需改动详情页。
- 理由：跳转点语义明确（"开始执行即作废输入"），改动仅限 ExecutionFragment 内两处跳转代码，不依赖生命周期时序；跳转后无论用户何时返回，状态必然已清空。

### D2. 选项 4（RUN_PART_NONCONTINUOUS_CASES）清空内容与顺序

在跳转 Runnable 内、`commit()` 之后依次执行：

- `selectedIds.clear()` 清空待执行列表
- `refreshSelectedIds()` 刷新 tvSelectedIds 显示（复用既有方法，恢复"已选案例（0）："）
- `viewBinding.tvCaseId.setText("")` 清空输入框（防御性，跳转前提是输入框为空）

**顺序约束**：`new ExecutionDetailsFragment(..., selectedIds.stream().mapToInt(...).toArray())` 的构造参数在 Runnable 内才求值，因此 `clear()` 必须放在构造参数求值之后（即先构造并 commit，再清空），否则会传入空列表导致无案例执行。

- 备选：跳转前清空——参数数组尚未拷贝，会传入空列表，不可行。

### D3. 选项 3（RUN_PART_CASES）清空内容

在 btnEndId 校验通过并跳转时（beginId/endId 均已有效的分支）执行：

- `beginId = INVALID_VALUE; endId = INVALID_VALUE;` 重置字段
- `viewBinding.btnBeginId.setText(getString(R.string.begin_id))`、`viewBinding.btnEndId.setText(getString(R.string.end_id))` 恢复默认文案

字符串资源 `begin_id`/`end_id` 已存在于 auto_test_strings.xml，无新增资源。

## Risks / Trade-offs

- [选项 4 清空顺序错误导致传入空执行列表] → 任务清单显式标注"先构造并 commit、后 clear"的顺序约束，并以现有代码注释风格加注释说明。
- [工作区源码加密，直接编辑会写回乱码] → 按既有流程经 git 明文修改并写回，任务清单中显式包含该步骤。
- [选项 2 输入框残留未处理] → 用户未要求，Non-Goal 明确记录；如需统一行为可在后续变更处理。

## Migration Plan

无数据迁移。改动仅 auto-test 模块的 ExecutionFragment.java 一处；回滚：还原该文件改动即可（git revert）。

## Open Questions

无。
