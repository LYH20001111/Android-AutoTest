# Proposal: 运行部分不连续案例

## Why

当前测试项只支持"运行所有案例"、"运行某个案例"、"运行部分连续案例（区间）"三种执行方式。实际测试中经常需要挑选多个**不连续**的案例重跑（例如只重跑失败或修改过的几个案例），现有功能只能一次执行一个或全量执行，效率低且无法批量组合。

## What Changes

- 在 OptionsFragment 的选项菜单中新增"运行部分不连续案例"，选项编号定为 **4**。
- **BREAKING**：原"4. 查看所有案例详情"及其后选项依次顺延为 5、6、7、8（查看所有 / 废弃 / 未执行 / 失败案例详情）。
- 新增交互流程（基于 ExecutionFragment 扩展）：进入界面后下方展示全部案例；通过数字键盘输入案例号，输入框有数字时点击"确认"将该案例号加入**待执行列表**并清空输入框；期间可对已加入的案例号进行**特定编辑**：删除任意一个已选案例，或将任意一个已选案例号修改为另一个合法案例号（不影响其他已选案例）；输入框为空时点击"确认"开始**依次执行**待执行列表中的所有案例。
- BaseTestCase 新增按案例号列表执行的能力，执行单个案例的既有逻辑（废弃/设备不支持检测、结果统计、postValue 输出）复用不变。
- 待执行列表在执行前持续可见（含已选案例号与数量），编辑操作（删除/修改）在单个弹窗内完成：列出已选案例号供选中，弹窗底部提供"取消/删除/修改"三个按钮直接执行对应操作。

## Capabilities

### New Capabilities

- `test-execution/noncontinuous-case-run`: 运行部分不连续案例的完整行为——选项入口、待执行列表的收集与删除、按列表依次执行、结果展示。

### Modified Capabilities

<!-- openspec/specs 尚无既有规格，本次无修改项 -->

## Impact

- `auto-test/src/main/java/com/hudou/autotest/fragment/OptionsFragment.java`：Option 常量与菜单文案（编号 4-8 调整、新增选项 4）
- `auto-test/src/main/java/com/hudou/autotest/fragment/ExecutionFragment.java`：新增 option 分支与交互逻辑（或新增独立 Fragment）
- `auto-test/src/main/java/com/hudou/autotest/base/item/BaseTestCase.java`：新增按案例号列表执行的方法
- `auto-test/src/main/java/com/hudou/autotest/fragment/ExecutionDetailsFragment.java`：支持新执行选项分支
- `auto-test/src/main/res/layout/auto_test_execution_fragment.xml`：待执行列表展示控件
- `auto-test/src/main/res/values/strings.xml`：新增界面文案
- 仅影响 auto-test 模块，不涉及 app 模块与其他模块
