## Context

当前 `auto_test_splash_loading_default.xml` 中 `splash_title` TextView 使用 `match_parent` 宽度 + `50sp` 字号 + `wrap_content` 高度。当宿主通过 `getSplashTitle()` 设置较长标题时，文字自动换行，在启动页短暂展示期间（默认 1200ms）出现多行文本，视觉上不美观。参见 proposal.md - Why 及 specs/splash-screen/spec.md 中关于长标题显示的要求。

## Goals / Non-Goals

**Goals:**
- 长标题在单行内完整显示，不出现意外换行
- 短标题（如默认值）视觉表现与当前完全一致
- 向后兼容，不修改 `getSplashTitle()` 接口签名
- 实现简单，不引入第三方依赖

**Non-Goals:**
- 不修改启动页整体布局结构
- 不支持多行标题展示（刻意限制为单行）
- 不改动 `getSplashLoadingLayoutResId()` 自定义布局——自定义布局由宿主自行管理
- 不涉及国际化或 RTL 适配（当前仅中文场景）

## Decisions

### D1: 选用 `app:autoSizeTextType="uniform"` 自动缩放方案

选择 AndroidX 核心库内置的 `AutoSizeTextView` 特性（`app:autoSizeTextType="uniform"`）作为主要方案。

**理由：**
- 完全基于 AndroidX AppCompat 内置能力，零额外依赖
- API 14+ 兼容，无需考虑版本问题
- 自动在预设字号范围内缩放，始终填满单行宽度
- 配合 `android:maxLines="1"` 确保不换行
- 文本始终完整显示，不丢失信息（相比 ellipsize 方案）

**备选方案对比：**

| 方案 | 优势 | 劣势 |
|------|------|------|
| `autoSizeTextType="uniform"` | 文本完整可见，零依赖，平滑缩放 | 极长文本会缩到很小 |
| `singleLine=true` + `ellipsize="end"` | 实现最简单 | 长文本截断，信息丢失 |
| `singleLine=true` + `ellipsize="marquee"` | 可展示完整文本 | 需要 focus/selected，启动页短暂展示期内无法滚动 |
| `maxLines="2"` + `ellipsize="end"` | 允许有限换行 | 仍可能换行，不够美观 |
| 标题+副标题布局 | 结构清晰 | 需要修改布局结构，增加开发成本 |

### D2: 设置最小字号保护

搭配 `app:autoSizeMinTextSize` 设置最小字号下限（推荐 24sp），当自动缩放小于该值时，启用 `ellipsize="end"` 截断。

**理由：** 极长文本（如超过 30 个汉字）即使缩到 24sp 也可能超出屏幕宽度，此时应截断而非继续缩小到不可读的程度。

### D3: 仅修改默认布局，不动代码

本方案纯属 XML 布局属性调整，无需修改 `AutoTestSplashActivity.java` 代码。

**理由：** `autoSizeTextType` 等属性是 TextView 的原生布局属性，在 XML 中声明即可生效。仅在需要 marquee 动画时才需要 `setSelected(true)` 代码配合，本方案不采用 marquee。

## Risks / Trade-offs

- **[兼容性风险] `autoSizeTextType` 需要 `app` namespace** → 确保布局文件顶部声明了 `xmlns:app="http://schemas.android.com/apk/res-auto"`，当前项目使用 DataBinding，已自动支持
- **[极长文本风险] 极端长标题会缩到很小** → 通过 `app:autoSizeMinTextSize` 设置下限（24sp），低于下限时用 `ellipsize` 截断，视觉上不会出现过小文字
- **[短标题回归风险] 短标题字号未保持 50sp** → 通过设置 `app:autoSizeMaxTextSize="50sp"` 和 `app:autoSizeMinTextSize="24sp"`，确保短文本仍以 50sp 显示，视觉无变化