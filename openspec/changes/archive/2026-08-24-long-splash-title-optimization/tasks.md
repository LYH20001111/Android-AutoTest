## 1. 修改默认布局文件

- [x] 1.1 在 `auto_test_splash_loading_default.xml` 的 `splash_title` TextView 上添加 `app:autoSizeTextType="uniform"` 属性，启用自动缩放，并在构建后验证布局文件编译通过
- [x] 1.2 在 `splash_title` TextView 上设置 `app:autoSizeMinTextSize="24sp"` 和 `app:autoSizeMaxTextSize="50sp"`，限定字号缩放范围，运行后验证短标题保持 50sp、长标题自动缩小到合适字号
- [x] 1.3 在 `splash_title` TextView 上设置 `android:maxLines="1"` 确保单行显示，并添加 `android:ellipsize="end"` 作为极长文本的兜底截断，运行后验证超长标题在缩至 24sp 后仍超出时出现省略号

## 2. 验证与测试

- [x] 2.1 构建项目并运行到设备/模拟器，验证默认短标题（`R.string.auto_test`）显示效果与修改前一致（50sp 单行居中）
- [x] 2.2 临时修改 `SplashActivity.getSplashTitle()` 返回较长标题（如 "PaymentService for Android AutoTest Platform"），验证自动缩放生效且单行展示
- [x] 2.3 临时修改 `SplashActivity.getSplashTitle()` 返回极长标题（如超过 30 个汉字），验证字号缩至 24sp 后出现省略号截断