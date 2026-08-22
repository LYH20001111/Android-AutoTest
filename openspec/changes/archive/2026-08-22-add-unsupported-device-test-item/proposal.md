# Proposal: @TestItem 增加测试项级不支持设备能力

## Why

目前 `@TestCase` 支持 `unsupportedDevice` 按案例粒度声明不支持的设备型号，但某些测试项整体在特定设备上无法适用（硬件缺失、外设不存在等）。此时只能逐个案例标注，且用户进入测试项后仍可触发运行，体验不佳。需要在测试项粒度声明"整项不适用"，并在界面入口提前拦截、给出可由宿主应用自定义的原因说明。

## What Changes

- `@TestItem` 注解新增参数 `String[] unsupportedDevice() default {};`，语义与 `@TestCase.unsupportedDevice` 一致（支持 `Build.MODEL` 或 `Build.MANUFACTURER + " " + Build.MODEL`，忽略大小写匹配）。当前设备命中时，该测试项下所有案例视为不适用。
- `@TestItem` 注解新增参数 `String unsupportedDeviceDes() default "";`，由宿主应用设置"为什么不适用"的说明文案，用于弹窗展示（选择新增参数而非复用 `unsupportedDevice` 改返回值，理由见 design.md，与现有 `abandon`/`abandonDes` 模式保持一致）。
- 点击测试项进入 OptionsFragment 时，若当前设备命中 `unsupportedDevice`，SHALL 弹窗提示该测试项不适用当前设备，并展示 `unsupportedDeviceDes` 文案（未设置时展示默认提示）。
- OptionsFragment 中点击运行类选项（"1" 运行所有案例、"2" 运行某个案例、"3" 运行部分连续案例、"4" 运行部分不连续案例）时，若设备命中，SHALL 不进入执行流程，改为再次弹窗提示不适用信息。
- 查看类选项（"5" 至 "8"）行为不受影响，可正常进入查看。
- 提取设备型号匹配判断为可复用的工具方法，供 `@TestCase` 现有逻辑与本次新增逻辑共用。

## Capabilities

### New Capabilities

- `test-execution/unsupported-device-test-item`: 测试项级别的不支持设备声明、入口弹窗提示、运行选项拦截与查看选项放行行为。

### Modified Capabilities

- （无）

## Impact

- `auto-test` 模块：
  - `annotation/TestItem.java`：新增 `unsupportedDevice`、`unsupportedDeviceDes` 参数及 `Members` 常量。
  - `fragment/OptionsFragment.java`：进入时弹窗提示；"1"~"4" 选项拦截弹窗；"5"~"8" 保持不变。
  - 设备型号匹配逻辑：从 `BaseTestCase.isDeviceUnsupported` 提取为公共工具（如 `util/DeviceUtils`），`BaseTestCase` 改为调用公共实现。
  - `res/values/strings.xml`：新增弹窗文案相关字符串资源。
- 宿主应用（`app` 模块）：`TestItem1`/`TestItem2` 可按需使用新参数作为示例，无破坏性变更（均有默认值）。
- 兼容性：注解新增带默认值的参数，向后兼容。
