## Context

见 proposal.md - Why。当前 auto-test 已用 `com.github.LYH20001111:Android-AutoTest` 坐标发布到本地仓库；外部希望仅通过一行依赖引入。

## Goals / Non-Goals

**Goals:**

- JitPack 可自动从 GitHub 构建 auto-test AAR，并对外提供 `com.github.LYH20001111:Android-AutoTest:x.y.z` 坐标
- 外部消费方只需在 settings.gradle 声明 jitpack.io 仓库 + 一行 `implementation` 即可引入
- README 中提供明确接入步骤

**Non-Goals:**

- 不改变现有本地发布流程（publishAutoTest）
- 不在 POM 中修改 groupId/artifactId（保持 com.github.LYH20001111/Android-AutoTest）
- 不启用 mavenLocal/Nexus 等其它仓库的发布（非本次范围）

## Decisions

### 决策 1：POM 元数据补充

在 auto-test/build.gradle 的 publishing 块中添加 JitPack 必需的 SCM/developer 字段：

```groovy
publishing {
    publications {
        release(MavenPublication) {
            // ... existing config
            pom {
                name = 'Android AutoTest'
                description = 'Android automated test framework'
                url = 'https://github.com/LYH20001111/Android-AutoTest'
                licenses {
                    license {
                        name = 'The Apache License, Version 2.0'
                        url = 'http://www.apache.org/licenses/LICENSE-2.0.txt'
                    }
                }
                developers {
                    developer {
                        id = 'LYH20001111'
                        name = 'Liu YiHong'
                        email = ''
                    }
                }
                scm {
                    connection = 'scm:git:git://github.com/LYH20001111/Android-AutoTest.git'
                    developerConnection = 'scm:git:ssh://git@github.com/LYH20001111/Android-AutoTest.git'
                    url = 'https://github.com/LYH20001111/Android-AutoTest'
                }
            }
        }
    }
}
```

### 决策 2：版本号策略

- 用 Git tag（如 v2.0.04、v2.0.05）作为版本号，触发 JitPack 构建新的 AAR
- 发新版本时递增根 build.gradle 的 autotestVersionName，然后打 tag → push → JitPack 构建
- 注意：JitPack 会拉取源码重新编译，不是使用本地已发布的 aar/pom

### 决策 3：依赖声明保持不变

auto-test 的依赖配置继续用之前优化后的版本（reflections + listenablefuture 排除等），JitPack 构建时将自动包含传递依赖。

## Risks / Trade-offs

- **仓库必须公开**：JitPack 只能构建公开的 GitHub 仓库；如果仓库私有，JitPack 无法访问
- **每次构建的是最新源码**：JitPack 基于 tag 构建，不是读取你本地的 local-maven-repo；发布新版本后，依赖会自动变为新构建的版本
- **构建稳定性**：JitPack 对 Android 库支持存在一定脆弱性（网络、缓存、构建超时），但通常稳定可用
