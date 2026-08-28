## Context

见 proposal.md - Why。已核实的事实（2026-08-28 实测）：

- `org.reflections:reflections:0.9.10` 的 POM 顶层属性声明 `guava.version=18.0`，即 guava 18.0 是 reflections 自身请求的版本——`force 'com.google.guava:guava:18.0'` 与 Gradle 默认版本仲裁结果相同，纯冗余
- app 与 auto-test 的 `releaseRuntimeClasspath` 中 listenablefuture、jsr305 均不存在（dependencyInsight 无匹配），对应 exclude 为空操作
- reflections 传递依赖 `com.google.code.findbugs:annotations:2.0.1`（其又带 jsr305 1.3.9），当前被全局 exclude 屏蔽——这是整段配置中唯一有实际作用的规则
- auto-test 与 app 源码均无 `com.google.common` 直接引用，guava 仅服务于 reflections 内部

## Goals / Non-Goals

**Goals:**

- 消费方（含 app）完全不需要 resolutionStrategy 配置即可构建与运行
- 发布的 POM 元数据干净：无冗余排除，仅保留 reflections 的 findbugs annotations 排除
- 版本号/版本码单一事实来源，发布新版本只改一处
- `./gradlew publishAutoTest` 一条命令完成发布

**Non-Goals:**

- 不升级 reflections / guava 等依赖版本（行为不变，仅清理配置）
- 不改变发布坐标的 groupId / artifactId / 仓库位置
- 不做版本号自动递增（发布新版本仍由开发者显式改数字，避免误发布）

## Decisions

### 决策 1：全局 resolutionStrategy 改为最小化排除

移除 auto-test 与 app 中的 `force guava 18.0` 与 `exclude jsr305/annotations`；**保留 `exclude listenablefuture`**。

**实施中修正（2026-08-28 实测）**：`force guava` 确为冗余（reflections 自带 18.0），但 `exclude listenablefuture` 并非空操作——`androidx.concurrent:concurrent-futures:1.1.0`（经 androidx.core 传递引入）依赖 `com.google.guava:listenablefuture:1.0`，而 guava 18.0 的 jar 内嵌了同名 `com.google.common.util.concurrent.ListenableFuture` 类，两者共存会在 dex 合并时报 `Duplicate class`。删除该排除后 `:app:checkReleaseDuplicateClasses` 已实测失败。guava 18.0 经 reflections 保证存在，故排除 listenablefuture 不会缺类。

- auto-test 保留一行全局排除（会随 maven-publish 写入 POM 每个依赖节点，消费方自动继承）：
  ```groovy
  configurations.all {
      exclude group: 'com.google.guava', module: 'listenablefuture'
  }
  ```
- auto-test 中 `reflections` 依赖改为依赖级排除：
  ```groovy
  implementation('org.reflections:reflections:0.9.10') {
      exclude group: 'com.google.code.findbugs'
  }
  ```
  （jsr305 随 findbugs annotations 一起被排除，无需单独声明）
- app 无任何 `configurations.all`；但其自身依赖 `zxing-android-embedded:4.2.0` 会经 legacy-support-v4 传递引入 concurrent-futures → listenablefuture（app 自有依赖树，auto-test 的 POM 排除覆盖不到），故在 zxing 依赖行做依赖级排除：
  ```groovy
  implementation('com.journeyapps:zxing-android-embedded:4.2.0') {
      exclude group: 'com.google.guava', module: 'listenablefuture'
  }
  ```

**为什么保留 findbugs exclude**：reflections 会传递 `com.google.code.findbugs:annotations:2.0.1`（及 jsr305 1.3.9）。它本身无害，但保留排除维持现状类路径，且只需一行。

**为什么 guava 不需要显式声明**：reflections 已声明 18.0，Gradle 仲裁即得 18.0；auto-test 无直接 guava 引用，无需 api 暴露。

### 决策 2：版本单一来源放在根 build.gradle 的 ext

```groovy
// 根 build.gradle
ext {
    autotestVersionCode = 20004
    autotestVersionName = '2.0.04'
}
```

- auto-test/build.gradle：`def appVersionCode = rootProject.ext.autotestVersionCode`、`def appVersionName = rootProject.ext.autotestVersionName`（保留原变量名，最小 diff；发布 version、BuildConfig、输出文件名全部随之引用）
- app/build.gradle：`implementation "com.github.LYH20001111:Android-AutoTest:${rootProject.ext.autotestVersionName}"`

**备选：gradle.properties**。同样可行，但 ext 与现有 build.gradle 结构一致且支持非字符串类型（versionCode 是 int），故选 ext。settings.gradle 的 `dependencyResolutionManagement` 读取 rootProject.ext 时机在项目评估后，无问题（app 依赖声明在项目配置阶段求值，rootProject.ext 已就绪）。

### 决策 3：根级 publishAutoTest 任务

```groovy
// 根 build.gradle
tasks.register('publishAutoTest') {
    group = 'publishing'
    description = '构建并发布 auto-test AAR 到项目内 local-maven-repo'
    dependsOn ':auto-test:publishReleasePublicationToLocalMavenRepoRepository'
}
```

使用 `tasks.register`（惰性，配置阶段不触发）。任务名不带 `:` 前缀，`./gradlew publishAutoTest` 即可在根目录执行。

### 决策 4：发布后验证与提交

重新发布 2.0.04（覆盖同版本产物，POM 元数据更新），随后：

- `./gradlew :app:assembleRelease` 确认 app 无 resolutionStrategy 也构建通过
- 真机安装跑核心路径，确认 guava 仍为 18.0、无 ClassNotFound/重复类
- 提交变更（含更新后的 local-maven-repo 产物）

## Risks / Trade-offs

- **findbugs annotations 若被其他依赖带入** → 依赖图实测无其他来源；如未来出现，依赖级 exclude 只作用于 reflections 节点，其他来源的 annotations 不受影响（行为可预期，不会比现状更差）
- **版本单一来源改动面** → 涉及 3 个 build.gradle，但全部为机械替换；发布前用 `./gradlew publishAutoTest` 端到端验证
- **同版本覆盖发布 + Gradle 缓存** → 与上期一致：README 已约定每次发布递增版本号；本次为内部验证覆盖 2.0.04，构建用 `--refresh-dependencies` 可消除缓存影响
- **rootProject.ext 在配置期顺序** → 根 build.gradle 的 ext 在子项目配置前求值，无时序问题

## Migration Plan

1. 根 build.gradle：加 ext 版本 + publishAutoTest 任务
2. auto-test/build.gradle：版本引用 ext；reflections 依赖加 exclude；删 configurations.all
3. app/build.gradle：坐标版本引用 ext；删 configurations.all
4. 执行 `./gradlew publishAutoTest` 发布，检查 POM（无 listenablefuture/jsr305/annotations 全局排除；reflections 节点有 findbugs 排除）
5. `./gradlew :app:assembleRelease` + 真机安装验证（guava 18.0、无崩溃）
6. README 发布流程更新；提交

回滚：git revert 对应提交即可恢复原配置；local-maven-repo 产物随提交回滚。

## Open Questions

- 无
