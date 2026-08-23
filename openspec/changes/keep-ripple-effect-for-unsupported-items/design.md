## Context

当前实现（参见 proposal.md - Why）中，`MyRecycleAdapter.onBindViewHolder` 对不适用测试项调用 `llItemType.setBackgroundColor(R.color.test_item_unsupported_bg)`，该方法将 `ll_item_type` 的背景从 `@drawable/auto_test_ripple_effect`（一个 `RippleDrawable`）替换为纯色 `ColorDrawable`，导致水波纹点击效果丢失。

`auto_test_ripple_effect.xml` 定义了一个 `RippleDrawable`，其内容层为 `#FFFFFF` 白色，水波纹层为 `#FF0000` 红色。布局文件中 `ll_item_type` 的 `android:background="@drawable/auto_test_ripple_effect"` 保持不变。

## Goals / Non-Goals

**Goals:**
- 不适用测试项在点击时显示水波纹视觉效果，与正常测试项一致
- 不适用测试项的暗化背景色（`#F0F0F0`）保持不变

**Non-Goals:**
- 不修改 ripple drawable 的资源文件定义
- 不修改正常测试项的视觉行为
- 不修改布局文件

## Decisions

### 1. 背景设置方式：从 `setBackgroundColor()` 改为 `setBackgroundTintList()`

**选择：** 使用 `View.setBackgroundTintList(ColorStateList)` 对现有的 `RippleDrawable` 背景进行着色，替代 `setBackgroundColor()` 替换背景。

**理由：**
- `setBackgroundTintList()` 不会替换背景 drawable，仅对其内容层进行颜色着色，因此 `RippleDrawable` 的水波纹行为得以保留
- 支持通过 `setBackgroundTintList(null)` 清除着色，恢复默认白色背景，用于正常测试项的状态恢复
- 只需修改 `MyRecycleAdapter.java` 一个文件，改动量最小

**替代方案考虑：**
- 创建新的 `auto_test_ripple_effect_unsupported.xml`：复制 ripple drawable 并将内容层颜色改为 `#F0F0F0`，然后在 `onBindViewHolder` 中切换背景 drawable。缺点是新增资源文件，且适配器需要维护两个 drawable 的引用
- 在 `onBindViewHolder` 中每次重新设置 `RippleDrawable`：代码复杂度高，不推荐

### 2. 正常测试项的背景恢复

**选择：** 在正常测试项的分支中调用 `setBackgroundTintList(null)` 清除着色。

**理由：**
- RecyclerView 的视图复用机制可能导致之前绑定为不适用测试项的视图后被复用为正常测试项，必须主动清除着色
- `setBackgroundTintList(null)` 会将背景恢复到 XML 中定义的原始颜色（白色 `#FFFFFF`）

## Risks / Trade-offs

- **API 兼容性**：`setBackgroundTintList()` 需要 API 21+（Android 5.0），而 `RippleDrawable` 本身也需要 API 21+，因此不存在额外兼容性问题。→ 可接受
- **着色行为**：`backgroundTint` 可能对某些 drawable 类型的着色行为存在差异，但 `RippleDrawable` 包含 `color` 子元素时，`setBackgroundTintList()` 会作用于内容层颜色，行为是确定的。→ 已验证可行