## 1. 版本单一来源与发布任务

- [x] 1.1 根 `build.gradle` 新增 `ext { autotestVersionCode = 20004; autotestVersionName = '2.0.04' }` 与 `tasks.register('publishAutoTest')`（dependsOn `:auto-test:publishReleasePublicationToLocalMavenRepoRepository`）。验证：`./gradlew tasks --group publishing` 出现 publishAutoTest
- [x] 1.2 `auto-test/build.gradle`：`appVersionCode`/`appVersionName` 改为引用 `rootProject.ext.autotestVersionCode`/`autotestVersionName`。验证：`./gradlew :auto-test:properties` 中版本相关配置无报错，发布 version 仍为 2.0.04

## 2. 依赖策略清理

- [x] 2.1 `auto-test/build.gradle`：删除 `configurations.all` 中的 `force guava 18.0` 与 `exclude jsr305/annotations`，**保留一行** `exclude group: 'com.google.guava', module: 'listenablefuture'`（实测必要：concurrent-futures 引入 listenablefuture:1.0，与 guava 18.0 内嵌类重复）；`reflections` 依赖改为 `implementation('org.reflections:reflections:0.9.10') { exclude group: 'com.google.code.findbugs' }`。验证：`./gradlew :auto-test:dependencyInsight --dependency guava --configuration releaseRuntimeClasspath` 显示 guava 解析为 18.0（无 forced 标注）
- [x] 2.2 `app/build.gradle`：删除 `configurations.all { resolutionStrategy ... }` 整块；依赖坐标改为 `implementation "com.github.LYH20001111:Android-AutoTest:${rootProject.ext.autotestVersionName}"`；zxing 与 lottie 依赖行均加依赖级排除 `exclude group: 'com.google.guava', module: 'listenablefuture'`（两者都会经 appcompat→core→concurrent-futures 引入 listenablefuture，属 app 自有依赖树，auto-test POM 排除覆盖不到）。验证：`./gradlew :app:assembleRelease` 成功且 `./gradlew :app:dependencies --configuration releaseRuntimeClasspath` 中 listenablefuture 出现次数为 0，guava 为 18.0 且无 forced 标注

## 3. 发布与端到端验证

- [x] 3.1 执行 `./gradlew publishAutoTest`。验证：命令成功；`local-maven-repo/com/github/LYH20001111/Android-AutoTest/2.0.04/` 下 aar/pom/.module/校验和更新
- [x] 3.2 检查发布 POM/.module：每个依赖节点保留 `listenablefuture` 排除；`reflections` 节点含 findbugs 排除；无 jsr305/annotations 全局排除残留；无 guava force 痕迹；依赖列表与 auto-test 当前配置一致。验证：grep POM/.module 确认
- [x] 3.3 执行 `./gradlew :app:assembleRelease`。验证：构建成功，无依赖冲突告警
- [x] 3.4 安装到真机并跑通核心路径（启动 → 测试项执行 → 设置页），全程无 ClassNotFoundException / NoClassDefFoundError / 重复类错误。验证：adb 安装启动 + logcat 检查

## 4. 文档与收尾

- [ ] 4.1 更新 README 发布章节：版本号在根 build.gradle 的 ext 修改、命令改为 `./gradlew publishAutoTest`、移除对 resolutionStrategy 的任何暗示。验证：按 README 步骤可完成一次发布
- [ ] 4.2 提交全部变更（根/auto-test/app 的 build.gradle、README、local-maven-repo 更新产物）。验证：`git status` 仅包含本次变更相关文件
