## Why

外部应用接入 auto-test 的 AAR 时，必须手动照抄 auto-test 内部使用的大量第三方依赖（material、room、jxl、fastjson 等）。应用方往往不清楚哪个功能需要哪个依赖，漏引任何一个都会在运行时崩溃（ClassNotFoundException / NoClassDefFoundError）。根因是当前 AAR 以文件方式（local-repo / flatDir）分发，不携带依赖元数据；本仓库 app 模块中大量重复的依赖声明就是这一问题的体现。

## What Changes

- **auto-test 发布链路完善**：修复并固化 `maven-publish` 配置，将 release AAR 连同完整的 POM / Gradle 元数据（含全部传递依赖声明）发布到项目内 `local-maven-repo` 目录，并提交入库
- **版本号对齐**：发布版本与 `appVersionName`（当前 2.0.04）保持一致，消除现有配置中发布版本（1.0.4）与产物版本不一致的问题
- **一键发布任务**：提供一条命令完成"打包 + 发布到 local-maven-repo"，替代/整合现有 `assembleAutoTest`
- **app 模块切换为 Maven 坐标消费**：移除 `implementation project(':auto-test')`，改为注册 `local-maven-repo` 仓库并以 `implementation 'com.github.LYH20001111:Android-AutoTest:<version>'` 接入，同时删除与 auto-test 重复的依赖声明（app 自有的 zxing / poi / lottie 等保留）
- **外部应用接入文档**：在 README 中提供"一个仓库地址 + 一行依赖坐标"的接入说明
- **遗留清理**：废弃以文件方式分发 AAR 的 `local-repo` 模块（保留与否在实现阶段确认）

注意：app 切换为 Maven 坐标后，本地开发流程变为"修改 auto-test 源码 → 重新发布到 local-maven-repo → 再构建 app"，不再是源码级即时联调。

## Capabilities

### New Capabilities

- `aar-distribution`: auto-test 库的对外分发能力——通过项目内 Maven 仓库发布带完整传递依赖元数据的 AAR，消费方仅需声明仓库与一行依赖坐标即可使用全部功能，无需手动引入任何 auto-test 内部依赖

### Modified Capabilities

- *(无——本变更为构建与分发方式变更，不改变现有 `splash-screen`、`test-execution` 的规格行为)*

## Impact

- **auto-test/build.gradle**：完善 `publishing` 配置（repositories 指向 `local-maven-repo`、版本对齐 `appVersionName`、保留 guava/findbugs 排除策略在元数据中的体现）
- **app/build.gradle**：删除 `project(':auto-test')` 与重复依赖，改为 Maven 坐标依赖
- **settings.gradle**：`dependencyResolutionManagement` 注册 `local-maven-repo` 仓库；`local-repo` 相关 include 可能移除
- **根 build.gradle**：清理注释掉的 fat-aar classpath 等遗留内容
- **local-maven-repo/**：新增发布产物（aar、pom、.module 及校验文件），需纳入 Git
- **README.md**：新增外部应用接入说明
- **开发流程**：app 与 auto-test 之间由源码依赖变为发布物依赖，本地开发需先执行发布任务
