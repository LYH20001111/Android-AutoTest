## 1. 配置 JitPack 发布元数据

- [x] 1.1 auto-test/build.gradle：在 publishing.publishers 块中添加 pom 元数据（name/description/url/licenses/developers/scm），确保 groupId/artifactId 与当前一致。验证：grep auto-test/build.gradle | grep -iE "developer|scm" 看到新增字段；JitPack 会从源码构建并生成自己的 POM
- [x] 1.2 构建并验证 auto-test 编译通过：./gradlew :auto-test:assembleRelease。验证：BUILD SUCCESSFUL，无新增错误

## 2. 打标签并发布到 JitPack

- [x] 2.1 创建 Git tag v2.0.04（或最新版本）：git tag v2.0.04 -a -m 'Version 2.0.04'。验证：git tag -l | grep v2.0.04（本地标签已创建），需要手动 git push origin v2.0.04 推送；push 成功后 GitHub 上看到 tag 存在
- [ ] 2.2 push tag 到远程仓库：git push origin v2.0.04。验证：push 成功；GitHub 上看到 tag 存在
- [ ] 2.3 JitPack 自动构建（或手动触发）：访问 https://jitpack.com/LYH20001111/Android-AutoTest/v2.0.04 等待构建完成。验证：构建状态为 "Build successful"，并提供 Maven coordinates

## 3. 文档与收尾

- [x] 3.1 更新 README：新增“通过 JitPack 引入”章节，提供 settings.gradle jitpack.io 声明 + implementation 一行示例。验证：外部工程按步骤可成功引入
- [ ] 3.2 commit/push 变更（auto-test/build.gradle、README）。验证：git push 成功
