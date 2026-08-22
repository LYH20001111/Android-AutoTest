## 1. 资源文件准备

- [x] 1.1 在 `auto_test_colors.xml` 中新增 `test_item_unsupported_bg`（浅灰色背景）和 `test_item_unsupported_text`（灰色文字）颜色资源，验证资源编译通过
- [x] 1.2 在 `auto_test_strings.xml` 中新增 `test_item_unsupported_hint` 字符串（如"当前设备不支持"），验证资源编译通过
- [x] 1.3 在 `auto_test_item_type.xml` 中新增 `tv_unsupported_hint` TextView，放置在 `tv_description` 下方，默认 `android:visibility="gone"`，文字颜色引用 `test_item_unsupported_text`，验证布局预览正常

## 2. Item 类扩展

- [x] 2.1 在 `Item` 类中新增 `isUnsupportedOnCurrentDevice()` 方法，通过 `clz.getAnnotation(TestItem.class)` 获取 `unsupportedDevice` 数组，调用 `DeviceUtils.isDeviceUnsupported()` 判断，验证返回 boolean 值正确
- [x] 2.2 验证 `Item` 现有构造行为和 `getName()` / `getDescription()` 不受影响

## 3. MyRecycleAdapter 视觉适配

- [x] 3.1 在 `onBindViewHolder` 中，对每个 position 调用 `itemList.get(position).isUnsupportedOnCurrentDevice()` 获取不适用状态
- [x] 3.2 当不适用状态为 true 时：设置 `ll_item_type` 背景为 `test_item_unsupported_bg`，设置 `tv_item` 和 `tv_description` 文字颜色为 `test_item_unsupported_text`，设置 `tv_unsupported_hint` 显示并填充提示文字；当为 false 时：恢复默认样式
- [x] 3.3 验证点击行为不受影响：不适用测试项点击后仍能进入 `OptionsFragment`，弹窗和运行拦截逻辑与现有一致