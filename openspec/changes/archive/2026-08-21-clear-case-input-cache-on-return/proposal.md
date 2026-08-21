# Proposal: 执行开始后清空案例输入缓存

## Why

使用"3. 运行部分连续案例"或"4. 运行部分不连续案例"时，完成案例输入并确认开始执行后，从 ExecutionDetailsFragment 返回 ExecutionFragment，之前输入的案例仍然保留：选项 3 的起始/结束案例号按钮文案未重置，选项 4 的待执行列表（selectedIds）未清空。这些输入仅在当次执行中有意义，执行开始后即为过期状态，残留会误导用户以为案例仍待执行，或引发重复输入的困惑。期望返回 ExecutionFragment 时界面回到未输入状态。

## What Changes

- ExecutionFragment 在跳转 ExecutionDetailsFragment 开始执行时，同步清空对应选项的输入状态：
  - 选项 4（运行部分不连续案例）：清空待执行列表 `selectedIds` 并刷新列表显示（恢复"已选案例（0）："）。
  - 选项 3（运行部分连续案例）：重置 `beginId`/`endId` 为无效值并恢复两个按钮的默认文案（"起始案例号："、"结束案例号："）。
- 仅在跳转执行时点清空，不依赖 Fragment 生命周期回调，保证所有返回路径（物理返回键等）下输入状态均已重置。
- 仅影响 auto-test 模块源码，复用既有字符串资源，无新增资源。

## Capabilities

### New Capabilities

- `test-execution/continuous-case-run`: 运行部分连续案例的输入状态生命周期——开始执行并返回 ExecutionFragment 时，起始/结束案例号输入 SHALL 被清空。

### Modified Capabilities

- `test-execution/noncontinuous-case-run`: "开始执行"需求新增场景——开始执行并返回 ExecutionFragment 时，待执行列表 SHALL 被清空（当前无此行为）。

## Impact

- `auto-test/src/main/java/com/hudou/autotest/fragment/ExecutionFragment.java`：选项 3/4 跳转执行处清空输入状态（工作区源码为 TSZ# 加密格式，需经 git 明文修改写回）。
- 无布局、字符串资源、API 或依赖变更；其他选项（1/2/5-8）行为不变。
