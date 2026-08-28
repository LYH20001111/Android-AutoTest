## 1. 发布链路（auto-test 模块）

- [x] 1.1 修改 `auto-test/build.gradle`：`publishing.repositories` 新增指向 `${rootDir}/local-maven-repo` 的 maven 仓库（名称 `localMavenRepo`）；`publication.version` 改为引用 `appVersionName`（2.0.04），删除硬编码 `1.0.4`。验证：`./gradlew :auto-test:tasks --group publishing` 能看到 `publishReleasePublicationToLocalMavenRepoRepository` 任务
- [x] 1.2 执行 `./gradlew :auto-test:publishReleasePublicationToLocalMavenRepoRepository`。验证：`local-maven-repo/com/github/LYH20001111/Android-AutoTest/2.0.04/` 下生成 aar、pom、.module、sources（若配置）及各校验和文件，命令成功退出
- [x] 1.3 检查生成的 POM / .module 元数据。验证：auto-test 的全部运行时依赖（appcompat、material、room-runtime、jxl、fastjson、gson、reflections、slf4j、commons-cli、swiperefreshlayout、navigation-runtime、core-splashscreen、constraintlayout、recyclerview、auto-service-annotations）均在依赖列表中，findbugs/jsr305 与 guava listenablefuture 的排除策略已体现

## 2. 消费侧切换（仓库注册 + app 模块）

- [x] 2.1 `settings.gradle` 的 `dependencyResolutionManagement.repositories` 中注册 `local-maven-repo` 仓库；移除 `include ':local-repo'` 与 `include ':local-repo:auto-test'`（目录文件保留，仅退出构建）。验证：`./gradlew projects` 中不再出现 local-repo 子项目，且配置阶段无报错
- [x] 2.2 `app/build.gradle`：移除 `implementation project(':auto-test')`，新增 `implementation 'com.github.LYH20001111:Android-AutoTest:2.0.04'`；删除与 auto-test 重复的依赖声明（appcompat、material、constraintlayout、jxl、room-runtime/room-compiler、swiperefreshlayout），保留 app 自有依赖（zxing、zxing-android-embedded、poi、commons-lang3、lottie）与测试依赖。验证：`./gradlew :app:dependencies --configuration releaseRuntimeClasspath` 中 auto-test 坐标出现且其传递依赖被解析
- [x] 2.3 执行 `./gradlew :app:assembleRelease`。若编译失败提示找不到某依赖类，按 design.md 决策 4 将该依赖在 auto-test 中提升为 `api` 并重新执行 1.2 发布，直至编译通过。验证：`:app:assembleRelease` 成功（预期至少 appcompat、material 需提升 `api`，以实际为准）
- [x] 2.4 确认 app 的 `configurations.all` 中 guava force/exclude 与 auto-test 一致且构建无 guava 相关冲突。验证：`./gradlew :app:assembleRelease` 输出无 guava/listenablefuture 冲突告警

## 3. 端到端验证

- [x] 3.1 安装 app 到真机或模拟器，跑通核心路径：启动页 → 测试执行（至少一项测试项完整执行）→ 测试报告/记录查看与导出。验证：全程无 ClassNotFoundException / NoClassDefFoundError 崩溃，功能表现与改造前一致（注：Excel 文件落盘受 Android 12 分区存储限制，属改造前既有行为，与依赖打包无关；jxl 代码路径已执行且无类加载错误）
- [x] 3.2 模拟"全新消费方"视角复核：确认 app 的依赖声明中不存在任何 auto-test 内部依赖的重复声明（app 自有功能依赖除外）。验证：人工检查 `app/build.gradle` dependencies 块，对照 1.3 的依赖清单无交集

## 4. 文档与收尾

- [x] 4.1 更新 `README.md`
- [x] 4.2 清理根 `build.gradle` 中被注释的 `fat-aar` classpath 残留。验证：根 `build.gradle` 无 fat-aar 相关内容，`./gradlew :app:assembleDebug` 仍成功
- [ ] 4.3 将 `local-maven-repo` 产物与全部构建脚本变更一并提交。验证：`git status` 中 `local-maven-repo/` 下产物已纳入跟踪，干净克隆（或同事拉取）后不重新发布也能直接构建 app
