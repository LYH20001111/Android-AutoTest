## Context

当前实现（参见 proposal.md - Why）中，`TestItem` 注解的 `unsupportedDevice` 仅在用户点击进入 `OptionsFragment` 时才被检测并弹窗提示。列表层（`MyRecycleAdapter`）渲染时没有任何设备兼容性判断，所有测试项外观一致。现有代码中 `DeviceUtils.isDeviceUnsupported()` 已提供设备匹配能力，可直接复用。

## Goals / Non-Goals

**Goals:**
- 在 `MyRecycleAdapter` 绑定时判断每个测试项是否不适用当前设备
- 不适用测试项显示暗化背景/文字 + 简短提示文字
- 点击行为不受影响，仍可进入选项页查看详情

**Non-Goals:**
- 不改变 `OptionsFragment` 中已有的弹窗逻辑和运行拦截逻辑
- 不修改 `DeviceUtils` 工具类
- 不做案例级（`TestCase`）的列表视觉指示，仅针对测试项级

## Decisions

### 1. 数据获取方式：扩展 `Item` 类 + 复用 `DeviceUtils`

**选择：** 在 `Item` 类中新增 `isUnsupportedOnCurrentDevice()` 方法。

**理由：**
- `Item` 构造函数已通过 `ReflectionUtils.getAnnotationValue()` 读取 `TestItem` 注解的 `name` 和 `description`，扩展读取 `unsupportedDevice` 数组是自然的延伸
- 避免在 `MyRecycleAdapter` 中直接进行反射操作，保持适配器关注视图绑定
- 适配器只需调用 `item.isUnsupportedOnCurrentDevice()` 即可，测试友好

**替代方案考虑：**
- 在适配器中直接反射读取 `TestItem` 注解：增加适配器职责，且每个 position 都要反射，性能略差
- 在 `Item` 中预计算 `isUnsupported` 状态：需要在构造时传入设备信息，不够灵活；改为懒判断方法更合适

### 2. 视觉实现方式：XML 新增控件 + 运行时控制可见性

**选择：** 在 `auto_test_item_type.xml` 中新增 `tv_unsupported_hint` TextView，默认 `android:visibility="gone"`，在 `onBindViewHolder` 中根据不适用状态控制显示。

**理由：**
- 无状态变化时的零开销（`GONE` 不占布局空间）
- 与现有 `tv_description` 布局模式一致
- 颜色和文字均通过资源文件配置，便于主题化

**替代方案考虑：**
- 动态创建 TextView：代码冗余，不利于布局预览
- 仅改变背景色不加文字提示：信息量不足，用户无法理解为何变暗

### 3. 颜色方案

**选择：** 新增颜色资源 `test_item_unsupported_bg`（浅灰色背景）和 `test_item_unsupported_text`（灰色文字），在 `onBindViewHolder` 中动态设置。

**理由：**
- 通过资源文件集中管理，便于后续调整
- 保持与现有 `@color/white` 背景和 `@color/black` 文字风格一致的管理方式

## Risks / Trade-offs

- **ListView 性能影响**：每个 item 绑定都调一次 `DeviceUtils.isDeviceUnsupported()`（内部是字符串数组遍历），但数据量极小（测试项数量通常 < 50），性能影响可忽略。→ 可接受
- **反射调用开销**：`Item` 构造时已通过 `ReflectionUtils` 读取注解值，新增读取 `unsupportedDevice` 数组不增加额外开销。→ 无新增风险
- **视觉一致性**：暗化后的列表项可能与应用的其他 UI 状态（如 disabled 状态）混淆。→ 通过添加明确提示文字来区分，而非仅依赖颜色变化