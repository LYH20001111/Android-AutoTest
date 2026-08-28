## MODIFIED Requirements

### Requirement: 单坐标接入

消费方应用 SHALL 能够通过"声明项目内 Maven 仓库 + 添加一行 `com.github.LYH20001111:Android-AutoTest:<version>` 依赖坐标"的方式接入 auto-test，且无需再手动声明 auto-test 内部使用的任何第三方或 AndroidX 依赖，也无需配置任何依赖版本强制（force）或全局排除（exclude）策略。

#### Scenario: 全新应用接入

- **WHEN** 一个全新应用仅在其构建配置中声明项目内 Maven 仓库并添加一行 auto-test 依赖坐标，然后构建并运行 auto-test 提供的全部功能
- **THEN** 构建成功，且运行过程中不因缺少 auto-test 所需的依赖类而崩溃（无 ClassNotFoundException / NoClassDefFoundError）

#### Scenario: 消费方未声明任何 auto-test 内部依赖

- **WHEN** 消费方的依赖列表中除 auto-test 坐标外没有任何 auto-test 内部依赖（如 material、room、jxl、fastjson）
- **THEN** auto-test 的全部功能仍可正常使用

#### Scenario: 消费方不配置 resolutionStrategy

- **WHEN** 消费方未声明任何 guava force 或全局 exclude，仅依赖 auto-test 坐标进行构建与运行
- **THEN** 构建成功，依赖版本仲裁结果与 auto-test 内部一致（guava 解析为 18.0，无 listenablefuture 重复类冲突），运行不因依赖类冲突或缺失而崩溃

### Requirement: 发布版本与库版本一致

发布版本 SHALL 与 auto-test 模块对外公布的版本号（versionName）保持一致，消费方 SHALL 能通过坐标中的版本号精确锁定所用库版本；版本号（versionName）与版本码（versionCode）SHALL 定义在单一位置（根 `build.gradle` 的 `ext`），auto-test 发布与 app 依赖坐标统一引用。

#### Scenario: 版本号一致

- **WHEN** 根 `build.gradle` 中 autotestVersionName 为 X 且执行发布
- **THEN** Maven 仓库中生成的坐标版本即为 X，产物文件名中包含 X

#### Scenario: 单一来源修改生效

- **WHEN** 开发者仅修改根 `build.gradle` 中的 autotestVersionName / autotestVersionCode
- **THEN** auto-test 发布坐标版本、BuildConfig 版本、产物文件名与 app 依赖坐标版本全部跟随更新，无需再修改其他文件

#### Scenario: 重复发布同版本

- **WHEN** 对同一版本号再次执行发布
- **THEN** 仓库中的产物被更新为该次发布的最新内容，不产生残留的旧文件造成混淆

### Requirement: 一键发布

项目 SHALL 提供根级命令 `./gradlew publishAutoTest` 完成"构建 release AAR 并发布到项目内 Maven 仓库"，发布产物 SHALL 包含 AAR、POM、Gradle 模块元数据及对应的校验和文件。

#### Scenario: 单命令发布

- **WHEN** 开发者在项目根目录执行 `./gradlew publishAutoTest`
- **THEN** 项目内 Maven 仓库目录中出现当前 autotestVersionName 版本的 aar、pom、.module 及校验和文件，命令成功退出

#### Scenario: 发布后产物可被解析

- **WHEN** 发布完成后消费方立即解析该坐标
- **THEN** 解析成功，元数据中的依赖声明与 auto-test 当前的依赖配置一致（reflections 的 findbugs annotations 排除已体现，无冗余 force/exclude）

### Requirement: 仓库内示例应用按外部方式接入

本仓库的 app 模块 SHALL 与外部应用使用完全相同的接入方式（Maven 仓库 + 依赖坐标），不得依赖 auto-test 的源码工程依赖；app 模块 SHALL 不重复声明 auto-test 已提供的依赖，且 SHALL 不包含 resolutionStrategy 配置。

#### Scenario: app 以坐标方式构建

- **WHEN** app 模块仅声明 auto-test 的 Maven 坐标及其自身功能所需的依赖（如 zxing、poi、lottie）并执行构建
- **THEN** 构建成功，app 的全部功能（含 auto-test 提供的功能）可正常运行

#### Scenario: 依赖声明不重复

- **WHEN** 检查 app 模块的依赖声明
- **THEN** 不存在与 auto-test 内部依赖重复的声明（app 自身功能专属依赖除外）

#### Scenario: app 无 resolutionStrategy

- **WHEN** 检查 app 模块的构建脚本
- **THEN** 不存在 `configurations.all { resolutionStrategy ... }` 等全局依赖策略配置；app 仅允许对其自身功能依赖（如 zxing）做依赖级排除
