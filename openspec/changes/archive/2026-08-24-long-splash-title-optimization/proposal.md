## Why

`AutoTestSplashActivity` 的 `getSplashTitle()` 允许宿主应用自定义启动页标题。目前默认布局（`auto_test_splash_loading_default.xml`）中标题 TextView 宽度为 `match_parent`、字号为 `50sp`，当宿主设置较长标题时（如包含产品全称、版本号等），文本会自动换行，多行 text 在启动页短暂展示期间严重影响视觉观感，降低品牌展示的专业度。

## What Changes

- 分析并选择一种或多种方案来解决长标题换行问题，方案包括但不限于：
  - **Auto-shrink 自适应字号**：使用 `app:autoSizeTextType="uniform"` 自动缩小字号以适应单行显示
  - **Ellipsize 省略号**：限制单行，超出部分以省略号结尾（`android:singleLine="true"` + `android:ellipsize="end"`）
  - **Marquee 跑马灯**：单行滚动显示长文本（`android:singleLine="true"` + `android:ellipsize="marquee"`）
  - **限制最大行数**：允许最多 2 行，超出省略（`android:maxLines="2"` + `android:ellipsize="end"`）
  - **标题+副标题双行布局**：主标题（短固定）+ 副标题（长文本，小字号），将长文本降级为副标题
- 修改 `auto_test_splash_loading_default.xml` 中 `splash_title` TextView 的布局属性
- 若方案涉及代码逻辑变更，修改 `AutoTestSplashActivity.java` 中标题设置相关代码
- 不改变 `getSplashTitle()` 接口签名，保持向后兼容

## Capabilities

### New Capabilities
- `splash-screen`: 启动页能力，覆盖启动页布局与品牌展示

### Modified Capabilities
- 无现有规格变更

## Impact

- `auto-test/src/main/res/layout/auto_test_splash_loading_default.xml`：`splash_title` TextView 布局属性调整
- `auto-test/src/main/java/com/hudou/autotest/base/activity/AutoTestSplashActivity.java`：仅当方案需要代码配合时修改（如 marquee 需要 `setSelected(true)`）
- 纯布局/UI 优化，不涉及接口变更、不新增依赖库