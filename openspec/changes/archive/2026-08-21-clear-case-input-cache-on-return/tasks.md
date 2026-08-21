# Tasks: 执行开始后清空案例输入缓存

## 1. 准备工作区明文

- [x] 1.1 从 git HEAD 导出 ExecutionFragment.java 明文（`git show HEAD:auto-test/src/main/java/com/hudou/autotest/fragment/ExecutionFragment.java`），注意保持 UTF-8 无 BOM 字节一致（工作区源码为 TSZ# 加密格式，不得直接编辑工作区文件）
- [x] 1.2 用导出的明文副本进行后续修改，完成后写回工作区对应路径

## 2. ExecutionFragment：跳转执行时清空输入状态

- [x] 2.1 选项 4（RUN_PART_NONCONTINUOUS_CASES）跳转 Runnable 内：**先**构造 ExecutionDetailsFragment（此时 `selectedIds.stream().mapToInt(...).toArray()` 完成参数拷贝）并 commit，**后**执行 `selectedIds.clear()`、`refreshSelectedIds()`（恢复"已选案例（0）："）、`viewBinding.tvCaseId.setText("")`；顺序不可颠倒，否则传入空列表
- [x] 2.2 选项 3（RUN_PART_CASES）btnEndId 校验通过并跳转的分支内：重置 `beginId = INVALID_VALUE; endId = INVALID_VALUE;`，并恢复按钮默认文案 `btnBeginId.setText(getString(R.string.begin_id))`、`btnEndId.setText(getString(R.string.end_id))`

## 3. 编译验证与清理

- [x] 3.1 写回明文改动到工作区，git status 确认仅预期文件（ExecutionFragment.java）变更
- [x] 3.2 全量编译（clean 后 compileDebugJavaWithJavac，使用本地 Gradle 8.6 + 可用的 AGP 版本，参考既有构建环境记忆），确认 BUILD SUCCESSFUL
- [x] 3.3 删除全部临时导出文件
