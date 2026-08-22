## Why

测试项当前已支持通过 `TestItem.unsupportedDevice` 注解声明不适用设备型号，并在用户点击进入测试项后弹窗提示。但用户需要逐个点击测试项才能发现不适用信息，体验不佳。希望在列表层面就能直观看到哪些测试项不适用当前设备，减少不必要的点击操作。

## What Changes

- 在 `MyRecycleAdapter` 中，对绑定测试项列表项时，判断该测试项是否不适用当前设备
- 不适用当前设备的测试项列表项视觉上变暗（降低背景/文字透明度或改变背景色）
- 在不适用测试项的列表项上添加简短的不适用提示文字（如"当前设备不支持"）
- 不适用测试项的点击行为保持不变（点击后仍可进入查看详情，但运行类操作仍被拦截）

## Capabilities

### New Capabilities

无新增能力。

### Modified Capabilities

- `test-execution/unsupported-device-test-item`: 增加一条需求：不适用当前设备的测试项在列表视图中应通过视觉样式（如暗化背景、添加提示文字）直观区分，无需用户点击即可识别。

## Impact

- **MyRecycleAdapter.java**: 修改 `onBindViewHolder`，在绑定视图时判断设备是否命中 `unsupportedDevice`，并根据结果设置不同的视觉样式
- **Item.java**: 可能需要扩展 `Item` 类或适配器直接通过反射读取 `TestItem` 注解的 `unsupportedDevice` 和 `unsupportedDeviceDes` 信息
- **auto_test_item_type.xml**: 可能需要添加一个用于显示不适用提示的 `TextView` 控件
- **DeviceUtils.java**: 已有 `isDeviceUnsupported()` 方法可直接复用，无需修改