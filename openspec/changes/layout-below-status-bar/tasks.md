## 1. 主布局状态栏适配

- [x] 1.1 在 `auto_test_activity_main.xml` 根 `FrameLayout` 上添加 `android:fitsSystemWindows="true"`，验证编译通过且布局无报错
- [x] 1.2 在 `AutoTestMainActivity.onCreate()` 中 `setContentView()` 之前调用 `WindowCompat.setDecorFitsSystemWindows(getWindow(), true)`，验证编译通过
- [ ] 1.3 在 Android 15+ 模拟器或真机上运行 app 模块，验证主界面内容完整显示在状态栏下方，顶部无遮挡