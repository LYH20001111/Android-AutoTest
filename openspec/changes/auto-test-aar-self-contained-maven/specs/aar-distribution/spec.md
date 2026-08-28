## Purpose

定义 auto-test 库的对外分发能力：通过项目内 Maven 仓库发布带完整传递依赖元数据的 AAR，使任何消费方应用仅凭"一个仓库地址 + 一行依赖坐标"即可使用 auto-test 的全部功能，杜绝因漏引依赖导致的运行时崩溃。

## ADDED Requirements

### Requirement: 单坐标接入

消费方应用 SHALL 能够通过"声明项目内 Maven 仓库 + 添加一行 `com.github.LYH20001111:Android-AutoTest:<version>` 依赖坐标"的方式接入 auto-test，且无需再手动声明 auto-test 内部使用的任何第三方或 AndroidX 依赖。

#### Scenario: 全新应用接入

- **WHEN** 一个全新应用仅在其构建配置中声明项目内 Maven 仓库并添加一行 auto-test 依赖坐标，然后构建并运行 auto-test 提供的全部功能
- **THEN** 构建成功，且运行过程中不因缺少 auto-test 所需的依赖类而崩溃（无 ClassNotFoundException / NoClassDefFoundError）

#### Scenario: 消费方未声明任何 auto-test 内部依赖

- **WHEN** 消费方的依赖列表中除 auto-test 坐标外没有任何 auto-test 内部依赖（如 material、room、jxl、fastjson）
- **THEN** auto-test 的全部功能仍可正常使用

### Requirement: 传递依赖元数据完整

发布到 Maven 仓库的产物 SHALL 携带完整的依赖元数据（POM 及 Gradle 模块元数据），其中声明 auto-test 运行所需的全部依赖及其版本；构建工具 SHALL 能据此自动解析并下载这些依赖。

#### Scenario: 依赖自动解析

- **WHEN** 消费方构建工具解析 auto-test 依赖坐标
- **THEN** auto-test 在库模块中声明的全部运行时依赖均被自动纳入消费方的运行时类路径，无需消费方干预

#### Scenario: 依赖冲突可控

- **WHEN** 消费方应用自身已引入与 auto-test 相同但版本不同的依赖
- **THEN** 构建工具按标准版本仲裁规则选择一个版本完成构建，不出现重复类冲突

### Requirement: 发布版本与库版本一致

发布版本 SHALL 与 auto-test 模块对外公布的版本号（versionName）保持一致，消费方 SHALL 能通过坐标中的版本号精确锁定所用库版本。

#### Scenario: 版本号一致

- **WHEN** auto-test 模块的 versionName 为 X 且执行发布
- **THEN** Maven 仓库中生成的坐标版本即为 X，产物文件名中包含 X

#### Scenario: 重复发布同版本

- **WHEN** 对同一版本号再次执行发布
- **THEN** 仓库中的产物被更新为该次发布的最新内容，不产生残留的旧文件造成混淆

### Requirement: 一键发布

项目 SHALL 提供单条命令完成"构建 release AAR 并发布到项目内 Maven 仓库"，发布产物 SHALL 包含 AAR、POM、Gradle 模块元数据及对应的校验和文件。

#### Scenario: 单命令发布

- **WHEN** 开发者在项目根目录执行该发布命令
- **THEN** 项目内 Maven 仓库目录中出现该版本的 aar、pom、.module 及校验和文件，命令成功退出

#### Scenario: 发布后产物可被解析

- **WHEN** 发布完成后消费方立即解析该坐标
- **THEN** 解析成功，元数据中的依赖声明与 auto-test 当前的依赖配置一致

### Requirement: 仓库内示例应用按外部方式接入

本仓库的 app 模块 SHALL 与外部应用使用完全相同的接入方式（Maven 仓库 + 依赖坐标），不得依赖 auto-test 的源码工程依赖；app 模块 SHALL 不重复声明 auto-test 已提供的依赖。

#### Scenario: app 以坐标方式构建

- **WHEN** app 模块仅声明 auto-test 的 Maven 坐标及其自身功能所需的依赖（如 zxing、poi、lottie）并执行构建
- **THEN** 构建成功，app 的全部功能（含 auto-test 提供的功能）可正常运行

#### Scenario: 依赖声明不重复

- **WHEN** 检查 app 模块的依赖声明
- **THEN** 不存在与 auto-test 内部依赖重复的声明（app 自身功能专属依赖除外）

### Requirement: 接入文档

项目 README SHALL 包含外部应用接入说明，至少涵盖：仓库地址声明方式、依赖坐标写法、版本号获取位置。

#### Scenario: 按文档接入成功

- **WHEN** 外部开发者仅依据 README 中的接入说明配置其应用
- **THEN** 能够成功引入并使用 auto-test，无需额外口头沟通或查阅源码
