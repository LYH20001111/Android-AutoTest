## 1. 布局文件重构

- [x] 1.1 将 `auto_test_splash_layout.xml` 中原有的加载内容区域提取为 `auto_test_splash_loading_default.xml`，保留原内容（ImageView + ProgressBar + TextView）
- [x] 1.2 修改 `auto_test_splash_layout.xml`：使用 ViewStub 替换原来的加载内容区域，设置 `android:id="@+id/splash_loading_container"`，`android:layout_gravity="center"`

## 2. AutoTestSplashActivity 新增方法与逻辑

- [x] 2.1 在 `IAutoTestSplash` 接口中声明 `int getSplashLoadingLayoutResId()` 方法
- [x] 2.2 在 `AutoTestSplashActivity` 中实现 `@Override public int getSplashLoadingLayoutResId()`，默认返回 0
- [x] 2.3 在 `onCreate` 流程中扩展逻辑：`setContentView` 之后，获取 `getSplashLoadingLayoutResId()` 返回值
- [x] 2.4 根据返回值决定 inflate 哪个布局：非 0 则使用自定义布局 inflate ViewStub；返回 0 则 inflate 默认 `auto_test_splash_loading_default`
- [x] 2.5 仅当使用默认布局（返回 0）时，才执行 `findViewById(R.id.splash_icon).setImageResource(getSplashIconResId())`；使用自定义布局时跳过该步骤

## 3. 验证与确认

- [ ] 3.1 编译 auto-test 模块，检查是否存在编译错误
- [ ] 3.2 验证默认行为不变：宿主未重写该方法时，启动页仍显示默认加载布局
- [ ] 3.3 验证自定义布局生效：在示例 `SplashActivity` 中重写该方法返回自定义布局，验证布局能正确显示