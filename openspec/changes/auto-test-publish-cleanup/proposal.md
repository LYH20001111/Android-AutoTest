## Why

auto-test 与 app 中都残留一段 `configurations.all { resolutionStrategy { force guava 18.0; exclude listenablefuture/jsr305/annotations } }`。实测依赖图显示：guava 18.0 本就是 reflections 0.9.10 声明的版本，`force` 是空操作；listenablefuture、jsr305 当前根本不在依赖图中，对应 exclude 也是空操作；只有 `exclude annotations`（reflections 传递的 findbugs annotations 2.0.1）有实际作用。消费方（尤其外部应用）被迫照抄这段配置，违背"单坐标接入"的目标。

同时发布流程繁琐：发新版本需要分别修改 auto-test 与 app 两处版本号，再执行冗长的原生发布任务名，容易遗漏。

## What Changes

- **删除冗余依赖策略**：移除 auto-test 与 app 中 `configurations.all` 里的 `force guava 18.0`（reflections 自带声明 18.0，冗余）与 `exclude jsr305/annotations`；**保留 `exclude listenablefuture`**（实测必要：androidx concurrent-futures 引入 listenablefuture:1.0，与 guava 18.0 内嵌类重复，删除即报 Duplicate class）——auto-test 保留这一行全局排除并随 POM 发布；`reflections` 的 findbugs 排除改为依赖级；app 不写任何 `configurations.all`，仅在其自身依赖 zxing 一行做依赖级排除
- **版本号单一来源**：在根 `build.gradle` 定义 `autotestVersionName` / `autotestVersionCode`，auto-test 的发布版本、BuildConfig、产物文件名与 app 的依赖坐标统一引用，改一处即可
- **一键发布任务**：新增根级任务 `./gradlew publishAutoTest`，依赖 auto-test 的发布任务，直接输出到 `local-maven-repo`
- **README 更新**：发布章节改为"改根 build.gradle 版本号 → 跑 publishAutoTest"的流程，移除对 resolutionStrategy 的隐含依赖说明

## Capabilities

### New Capabilities

- *(无新能力)*

### Modified Capabilities

- `aar-distribution`: 一键发布命令由原生任务名简化为 `publishAutoTest`；发布版本号与版本码的单一事实来源移至根 `build.gradle`；发布的依赖元数据仅保留 reflections 的 findbugs 排除，不再需要消费方配置 resolutionStrategy；app 模块不包含 resolutionStrategy 配置

## Impact

- **build.gradle（根）**：新增 `ext { autotestVersionName/autotestVersionCode }` 与 `publishAutoTest` 任务
- **auto-test/build.gradle**：删除 `configurations.all`；`appVersionName/appVersionCode` 引用根 `ext`；`reflections` 依赖行加 `exclude`；发布版本引用 `autotestVersionName`
- **app/build.gradle**：删除 `configurations.all`；依赖坐标版本引用 `autotestVersionName`
- **README.md**：发布流程章节更新
- **local-maven-repo/**：重新发布 2.0.04，POM 元数据更新（移除冗余排除、保留 reflections 排除）
