# Design: @TestItem 增加测试项级不支持设备能力

## Context

`@TestCase` 已支持 `unsupportedDevice` 案例级不支持设备声明，匹配逻辑实现在 `BaseTestCase.isDeviceUnsupported`（私有），执行时命中则跳过并记录"设备不支持"。选项页 `OptionsFragment` 通过数字键盘（软键盘 `NumberKeyBoardView` 与物理键盘统一走 `onInsertKeyEvent`）分发 8 个选项："1"~"4" 为运行类，"5"~"8" 为查看类。现有 `@TestCase` 已有 `abandon`/`abandonDes` 成对参数的先例（开关 + 说明文案）。详见 proposal.md 的 Why。

## Goals / Non-Goals

**Goals:**

- 在 `@TestItem` 上以测试项粒度声明不支持设备与不适用原因说明。
- 进入选项页时弹窗提示；运行类选项（"1"~"4"）拦截并弹窗；查看类选项（"5"~"8"）不受影响。
- 设备匹配逻辑与 `@TestCase` 现有规则完全一致，且两处共用同一实现。

**Non-Goals:**

- 不改变案例执行引擎行为：`@TestCase` 级 `unsupportedDevice` 逻辑保持不变，不引入"整项案例自动标记设备不支持结果"的统计变更。
- 不修改测试列表页（`AutoTestTestListFragment`）对测试项的展示样式。
- 不处理英文文案国际化（现有弹窗文案同样为中文硬编码，保持一致）。

## Decisions

### 决策 1：新增 `unsupportedDeviceDes` 参数，而非复用 `unsupportedDevice` 改返回值

**选择**：`@TestItem` 新增 `String unsupportedDeviceDes() default "";`。

**理由**：

- 与现有 `abandon`/`abandonDes` 的"开关 + 说明"成对模式一致，使用者心智模型统一。
- 复用 `unsupportedDevice` 塞入说明文案（如 `{"P70", "原因:该设备无打印机"}`）需要约定分隔/前缀格式并解析，破坏"列表即型号"的语义，易写错且无法编译期校验。
- 两个参数职责单一：一个声明"哪些设备不适用"，一个声明"为什么不适用"。

**备选方案**：复用 `unsupportedDevice` 并约定特殊格式（如最后一个元素或前缀 `des:` 作为说明）——被否决，格式脆弱、语义混淆。

### 决策 2：提取公共设备匹配工具 `DeviceUtils`

将 `BaseTestCase.isDeviceUnsupported(String[])` 的判断逻辑提取为 `util/DeviceUtils.isDeviceUnsupported(String[])` 静态方法（`Build.MODEL` 与 `MANUFACTURER + " " + MODEL` 忽略大小写匹配，空列表返回 false）。`BaseTestCase` 与 `OptionsFragment` 均调用该工具，保证案例级与测试项级匹配规则一致。

**备选方案**：在 `OptionsFragment` 内复制一份判断逻辑——被否决，两处规则可能漂移。

### 决策 3：注解读取方式

`OptionsFragment` 直接通过 `clz.getAnnotation(TestItem.class)` 获取注解实例读取 `unsupportedDevice()` 与 `unsupportedDeviceDes()`（`ReflectionUtils.getAnnotationValue` 仅支持 String 成员，不为数组成员扩展该工具）。

### 决策 4：拦截点统一放在 `OptionsFragment.onInsertKeyEvent`

软键盘与物理键盘输入都汇聚到 `NumberKeyBoardView` 的 `onInsertKeyEvent`，在此对运行类选项（"1"~"4"）统一判断：设备命中时调用 `Dialog.notifyDialog` 弹窗（文案 = 不适用提示 + `unsupportedDeviceDes` 或默认设备型号提示），不再创建 `ExecutionDetailsFragment`/`ExecutionFragment`；查看类选项（"5"~"8"）分支不动。单一拦截点保证两种键盘行为一致。

### 决策 5：进入弹窗时机

`OptionsFragment.onInitData` 末尾判断设备命中时弹窗提示。使用现有 `Dialog.notifyDialog` 通知型弹窗（仅"确定"按钮），确认后停留在选项页，不自动返回。

## Risks / Trade-offs

- [弹窗文案为中文硬编码，与现有代码风格一致但暂不支持多语言] → 与现有弹窗处理方式保持一致；后续统一国际化时一并处理。
- [宿主应用误把说明写进 `unsupportedDevice`] → 匹配逻辑按型号忽略大小写匹配，误填只会导致匹配不到（不误伤），注解 Javadoc 明确两参数用途。
- [`Dialog.notifyDialog` 具体签名与线程约束需在实现时核对（工具类内部有新线程 + 等待弹窗显示的逻辑）] → tasks 中安排先核对 Dialog API 再接入，必要时在 UI 线程调用对应重载。
- [仅拦截选项页入口，若未来新增进入执行的入口需同步拦截] → 拦截逻辑收敛为 `OptionsFragment` 内私有判断方法，新入口复用时易发现。

## Migration Plan

纯新增能力，注解参数均带默认值，宿主应用无需改动即可升级；后续可按需为具体测试项声明 `unsupportedDevice`/`unsupportedDeviceDes`。无回滚风险。
