## Context

auto-test 是 `com.android.library` 模块（AGP 8.1 / Gradle 8.2），已应用 `maven-publish` 插件，且 `local-maven-repo` 中已存在一次历史发布（`com.github.LYH20001111:Android-AutoTest:2.0.04`，POM 中已带传递依赖声明）。现存问题（见 proposal.md - Why）：

- `publishing` 块没有配置任何 `repositories`，发布任务实际无处可发；`version` 硬编码为 `1.0.4`，与 `appVersionName`（2.0.04）脱节
- app 通过 `implementation project(':auto-test')` 源码依赖，历史上还用过 `local-repo` 文件方式消费 AAR——文件方式不携带依赖元数据，这正是"应用必须手抄依赖清单"的根因
- 根 `build.gradle` 中残留被注释的 `fat-aar:1.3.6` classpath，说明曾尝试内嵌方案但未落地（fat-aar 不支持 AGP 8.x）
- `settings.gradle` 使用 `FAIL_ON_PROJECT_REPOS`，仓库必须在 `dependencyResolutionManagement` 中集中注册

## Goals / Non-Goals

**Goals:**

- 发布链路一条命令可用：产物（AAR + POM + .module + 校验和）落入项目内 `local-maven-repo` 并纳入 Git
- 消费方一行坐标接入，依赖经 POM/Gradle 元数据自动传递，漏引依赖导致的运行时崩溃从机制上消除
- app 模块成为外部接入方式的"活体验证"：与外部应用完全相同的消费路径
- 发布版本号与 `appVersionName` 单一事实来源对齐

**Non-Goals:**

- 不做 fat-aar / 依赖内嵌（含"仅内嵌纯 Java 库"的混合方案），理由见决策 1
- 不发布到私有 Nexus / mavenLocal（用户已确认本期仅项目内仓库；Nexus 留作后续）
- 不改动 auto-test 的业务代码与功能行为
- 不处理 app 自有功能依赖（zxing、poi、lottie 等）的归属，它们本就不属于 auto-test

## Decisions

### 决策 1：用 Maven 传递依赖，而不是把依赖打进 AAR

选择：`maven-publish` 生成的 POM + Gradle 模块元数据声明全部传递依赖，消费方构建工具自动解析。

否掉的备选：

- **fat-aar 内嵌**：`com.github.kezong:fat-aar` 最后版本 1.3.8 也只支持到 AGP 7.x，本项目 AGP 8.1 下不可用；且内嵌 AndroidX 会在消费方也带 AndroidX 时产生 duplicate class 冲突，资源合并、R 类、consumer proguard 都很脆弱。根目录残留的注释 classpath 即为前次尝试未果的证据
- **混合方案（仅内嵌纯 Java 库）**：实现复杂度最高（需要手工改 POM 去除已内嵌依赖的声明），收益有限——传递依赖已解决"消费方不写依赖清单"的核心诉求

### 决策 2：发布仓库为项目内 `local-maven-repo` 目录

```groovy
publishing {
    repositories {
        maven {
            name = 'localMavenRepo'
            url = uri("${rootDir}/local-maven-repo")
        }
    }
}
```

选择理由：随 Git 分发，消费方克隆仓库（或拷贝该目录）即可用，无网络、凭据依赖；与历史发布产物目录一致，坐标不变。

发布命令为 AGP 自动生成的原生任务：

```
./gradlew :auto-test:publishReleasePublicationToLocalMavenRepoRepository
```

该任务自动依赖 `assembleRelease`，即"单命令完成打包+发布"。现有 `assembleAutoTest` 聚合任务保留为"只出 AAR 文件"的用途，不别名包装发布任务（Gradle 原生任务名已足够清晰，写进 README）。

同版本重复发布：文件型 Maven 仓库允许覆盖，产物与校验和文件被新内容替换；`2.0.04` 的历史发布将被本次产物覆盖。

### 决策 3：版本号单一事实来源

`publication.version` 不再硬编码，直接引用模块的 `appVersionName`（当前 `2.0.04`）。今后升版本只改一处，坐标版本、产物文件名自动跟随。

### 决策 4：`api` 与 `implementation` 的划分以"app 删重复依赖后能否编译"为准

Maven 元数据中 `implementation` 依赖对消费方是 **runtime 可见、compile 不可见**。当前 POM 里只有 `jxl`、`viewbinding` 是 compile（api）范围，其余（appcompat、material、constraintlayout、room 等）都是 runtime。若 app 自身源码直接 import 了这些类，删掉重复声明后编译会失败。

处理原则：以"从 app 中删除某依赖声明后编译是否通过"为判据——失败则把该依赖在 auto-test 中从 `implementation` 提升为 `api`，重新发布；通过则保持 `implementation`。预期至少 `appcompat`、`material` 需要提升为 `api`（app 的 Activity/主题通常直接依赖它们），以实际验证结果为准。

### 决策 5：消费侧仓库注册放在 `dependencyResolutionManagement`

`FAIL_ON_PROJECT_REPOS` 模式下，仓库必须注册在 `settings.gradle`：

```groovy
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven { url 'https://jitpack.io' }
        maven { url = uri("${rootDir}/local-maven-repo") }  // 新增
    }
}
```

### 决策 6：退役 `local-repo` 文件分发模块

从 `settings.gradle` 移除 `include ':local-repo'` 与 `include ':local-repo:auto-test'`（其 `build.gradle` 依赖"扫描磁盘根目录的 aar/jar"的脆弱逻辑）。目录文件暂保留在磁盘上不动，仅退出构建，避免误删历史产物；后续可视情况删除。

## Risks / Trade-offs

- **同版本覆盖发布 + Gradle 缓存** → Gradle 对已解析的模块有缓存（默认 24h），覆盖同版本后消费方可能拿到旧缓存。缓解：约定"每次对外发布递增版本号"；确需覆盖时在 README 提示消费方使用 `--refresh-dependencies`
- **开发流程变慢** → app 从源码依赖变为发布物依赖，改 auto-test 后需先跑发布命令。缓解：发布命令单一且增量构建下较快；在 README 写明开发循环
- **传递依赖带入老库** → 如 `reflections:0.9.10` 会传递 guava 15，auto-test 内靠 `force guava:18.0` 压制，但 `force` 不会传递给消费方（`exclude` 会）。缓解：验证阶段在 app 中确认构建与运行正常；若出现 guava 相关冲突，在 auto-test 侧用 `exclude` 或 `api` 显式声明 guava 版本，使元数据携带该约束
- **`api` 提升扩大依赖暴露面** → 更多依赖进入消费方编译类路径。权衡：这正是消费方壳工程（app）所需的；对外部应用而言多几个编译期可见依赖无实质风险
- **历史 2.0.04 产物被覆盖** → 覆盖后旧产物不可追溯，但有 Git 历史可查；发布配置变更与产物更新在同一提交中完成

## Migration Plan

1. 修改 `auto-test/build.gradle`：补 `repositories`、版本对齐 `appVersionName`
2. 执行发布命令，确认 `local-maven-repo` 产物齐全（aar / pom / .module / 校验和）
3. `settings.gradle` 注册仓库、移除 `local-repo` includes
4. 修改 `app/build.gradle`：坐标替换 `project(':auto-test')`，逐个删除重复依赖，编译失败者按决策 4 提升 `api` 后重新发布
5. `./gradlew :app:assembleDebug` + 真机/模拟器安装跑通主要功能（启动、测试执行、报告导出）验证无 ClassNotFound
6. 更新 README 接入文档；提交（含 `local-maven-repo` 产物）

回滚：以上均为 Git 跟踪文件的修改，整体 revert 对应提交即可恢复源码依赖模式。

## Open Questions

- README 中是否需要同时保留"文件方式接入"的旧说明一段过渡期——默认直接替换为新说明，实现时若发现外部团队仍依赖旧文档再补注记
