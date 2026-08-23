## Why

当前不适用设备的测试项在列表中通过 `setBackgroundColor()` 将 `ll_item_type` 的背景替换为纯色，导致原有的 `@drawable/auto_test_ripple_effect` 水波纹点击效果丢失。用户点击不适用测试项时没有任何视觉反馈，体验不佳。

## What Changes

- 修改 `MyRecycleAdapter.onBindViewHolder` 中不适用测试项的背景设置方式，从 `setBackgroundColor()` 替换为 `setBackgroundTintList()` 方式，保留 `@drawable/auto_test_ripple_effect` 作为背景 drawable
- 不适用测试项的视觉效果（灰色背景 + 灰色文字 + 提示文字）保持不变，仅修复点击水波纹缺失的问题
- 适用设备的测试项行为不受任何影响

## Capabilities

### New Capabilities

无新增能力。

### Modified Capabilities

- `test-execution/unsupported-device-test-item`: 修改"不适用测试项在列表中的视觉指示"需求中的"不适用测试项仍可点击进入"场景，增加水波纹视觉反馈的期望

## Impact

- **MyRecycleAdapter.java**: 修改 `onBindViewHolder` 中不适用测试项的背景设置方式，从 `setBackgroundColor()` 改为 `setBackgroundTintList()` + `ColorStateList`
- **auto_test_ripple_effect.xml**: 无需修改，继续使用现有水波纹 drawable
- **auto_test_colors.xml**: 无需修改，继续保持现有颜色资源
- **auto_test_item_type.xml**: 无需修改，布局中的 `android:background="@drawable/auto_test_ripple_effect"` 保持不变