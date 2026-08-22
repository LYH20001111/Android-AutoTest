# Tasks: @TestItem 增加测试项级不支持设备能力

## 1. 注解与公共工具

- [x] 1.1 在 `annotation/TestItem.java` 新增 `String[] unsupportedDevice() default {};` 与 `String unsupportedDeviceDes() default "";` 参数（含 Javadoc，说明型号填写规则与 `@TestCase.unsupportedDevice` 一致），并在 `Members` 接口补充对应常量；验证模块编译通过
- [x] 1.2 新建 `util/DeviceUtils.java`，提供静态方法 `isDeviceUnsupported(String[] unsupportedDevices)`：空列表返回 false，否则按 `Build.MODEL` 与 `Build.MANUFACTURER + " " + Build.MODEL` 忽略大小写匹配；验证编译通过
- [x] 1.3 将 `base/item/BaseTestCase.java` 中私有方法 `isDeviceUnsupported` 改为委托调用 `DeviceUtils.isDeviceUnsupported`（保留原调用点不动）；验证编译通过且案例级"设备不支持"跳过逻辑行为不变

## 2. OptionsFragment 入口弹窗与选项拦截

- [x] 2.1 在 `fragment/OptionsFragment.java` 增加私有判断方法：读取 `clz.getAnnotation(TestItem.class)` 的 `unsupportedDevice()`，调用 `DeviceUtils` 判定当前设备是否不适用；验证编译通过
- [x] 2.2 在 `onInitData` 末尾：设备不适用时调用 `Dialog.notifyDialog` 弹窗，文案为"该测试项不适用当前设备型号：xxx"，并追加注解 `unsupportedDeviceDes`（非空时），确认后停留在选项页；验证：命中设备进入该测试项弹窗出现且展示自定义说明，未设置说明时展示默认设备型号提示
- [x] 2.3 在 `onInsertKeyEvent` 中对运行类选项（`RUN_ALL_CASES`、`RUN_ONE_CASE`、`RUN_PART_CASES`、`RUN_PART_NONCONTINUOUS_CASES`）增加拦截：设备不适用时弹窗提示同样的不适用信息（含原因说明或默认提示），不创建 `ExecutionDetailsFragment`/`ExecutionFragment`；验证：软键盘与物理键盘下点击"1"~"4"均只弹窗不进入执行页
- [x] 2.4 确认查看类选项（`VIEW_ALL_CASES` 至 `VIEW_FAILED_CASES`，"5"~"8"）分支不加任何拦截；验证：不适用设备上点击"5"~"8"仍正常进入查看页

## 3. 文案资源与示例

- [x] 3.1 在 `auto-test/src/main/res/values/strings.xml` 新增不适用提示相关字符串资源（入口提示、拦截提示、默认设备型号说明格式），OptionsFragment 引用资源而非硬编码；验证编译通过且弹窗文案取自资源
- [x] 3.2 在 `app` 模块选取一个测试项（如 `TestItem2`）的 `@TestItem` 注解添加 `unsupportedDevice` 与 `unsupportedDeviceDes` 示例（型号选一台当前测试机可命中的真实型号）；验证宿主模块编译通过

## 4. 集成验证

- [ ] 4.1 构建并在真机上端到端验证规格全部场景：未声明测试项行为不变；命中设备进入弹自定义原因提示；"1"~"4"被拦截并弹窗；"5"~"8"正常查看；`@TestCase` 级 `unsupportedDevice` 原有跳过与"设备不支持"记录不受影响
