## Why

另一个程序希望引入 auto-test，但不想拷贝 `local-maven-repo` 目录。通过 JitPack 可从 GitHub 自动构建并分发 AAR，外部消费方只需声明 jitpack.io 仓库 + 一行坐标即可引入。

## What Changes

- **JitPack 支持**：在 auto-test 模块的 maven-publish 中补充 JitPack 所需的元数据（developerConnection、scm、url），使 groupId/artifactId/version 与当前约定一致（com.github.LYH20001111:Android-AutoTest:x.y.z）
- **标签化发布**：用 Git tag（如 v2.0.04）作为版本号触发 JitPack 构建；后续每次发布递增 tag
- **接入文档**：README 新增“通过 JitPack 引入”章节，提供设置示例和注意事项

## Capabilities

### New Capabilities

- *(无新能力)*

### Modified Capabilities

- *(无需求变更)*

## Impact

- **auto-test/build.gradle**：发布 POM 增加 JitPack 所需字段
- **README.md**：新增 JitPack 接入章节，提供 jitpack.io 仓库配置 + implementation 一行
