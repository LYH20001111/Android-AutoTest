# Tasks: 运行部分不连续案例

## 1. 准备工作区明文

- [x] 1.1 从 git HEAD 导出待改文件明文（OptionsFragment.java、ExecutionFragment.java、ExecutionDetailsFragment.java、BaseTestCase.java），注意保持 UTF-8 无 BOM 字节一致（工作区源码为 TSZ# 加密格式，不得直接编辑工作区文件）
- [x] 1.2 用导出的明文副本进行后续修改，完成后写回工作区对应路径

## 2. OptionsFragment：选项入口与编号

- [x] 2.1 Option 常量新增 `RUN_PART_NONCONTINUOUS_CASES = "4"`；`VIEW_ALL_CASES` 改为 `"5"`、`VIEW_ABANDON_CASES` 改为 `"6"`、`VIEW_UNEXECUTED_CASES` 改为 `"7"`、`VIEW_FAILED_CASES` 改为 `"8"`
- [x] 2.2 onInitData 菜单文案新增"4. 运行部分不连续案例"，原 4-7 项依次改为 5-8（查看所有/废弃/未执行/失败案例详情）
- [x] 2.3 onInsertKeyEvent 的 switch 中 `case Option.RUN_PART_NONCONTINUOUS_CASES` 与 2/3 同分支，跳转 ExecutionFragment

## 3. BaseTestCase：按案例号列表执行

- [x] 3.1 新增公共方法 `runPartCases(Class<? extends BaseTestCase> clz, int[] ids)`：按方法名排序取得全部 @TestCase 方法数组（与 runCase 相同的 filter+sorted 逻辑），按 ids 顺序映射为 Method[]（越界 id 忽略），调用 `runTestCases(clz, runMethods)`

## 4. ExecutionDetailsFragment：新执行分支

- [x] 4.1 新增构造重载 `(Class clz, BaseTestCase testItem, String option, int[] testIds)`，保留现有 6 参构造不变；新增字段 `int[] testIds` 与静态导入 `RUN_PART_NONCONTINUOUS_CASES`
- [x] 4.2 onActionAfterInitData 新增 `case RUN_PART_NONCONTINUOUS_CASES`：tvLine2Message 显示待执行案例号列表（如"1、3、5"），调用 `testItem.runPartCases(clz, testIds)`

## 5. ExecutionFragment：收集与编辑交互

- [x] 5.1 新增字段 `List<Integer> selectedIds`（保持加入顺序）；actionByOption 新增 `case RUN_PART_NONCONTINUOUS_CASES`：保留键盘可见，postValue 展示全部案例（viewCaseDetails），显示待执行列表控件行，隐藏 ll_line2 的固定提示文案
- [x] 5.2 onOK 分流：输入框有数字 → 校验（0 ≤ id < testItemCasesNum，非法则 Dialog.notifyDialog 提示并清空）→ 已存在则 postValue 红色提示"已在待执行列表" → 否则加入 selectedIds、清空输入、刷新列表显示、postValue 提示加入成功；输入框为空 → selectedIds 非空则 postDelayed(100ms) 后跳转 ExecutionDetailsFragment（新构造重载，传 int[] 转换后的数组），为空则提示先添加案例
- [x] 5.3 点击待执行列表 TextView（`tv_selected_ids` 有效区域）触发编辑 → `Dialog.listActionDialog` 单弹窗（标题"选择要编辑的案例号"，列表 = 已选案例号，底部"取消/删除/修改"三按钮）→ 回调 onResult(selectedIndex, actionIndex)：取消/未选中忽略、删除按索引移除、修改走 5.5；列表为空时点击无响应；进入新分支时 postValue 提示"点击上方待执行列表可编辑已选案例"
- [x] 5.4 新增私有方法刷新待执行列表显示（"已选案例（N）：1、3、5"），在加入/编辑后调用
- [x] 5.5 修改流程：点击弹窗"修改"按钮（已选中目标）→ `Dialog.editDialog` 输入新案例号（onlyNumber）→ 校验（0 ≤ 新号 < testItemCasesNum；新号不与 selectedIds 中其他项重复，重复则 postValue 红色提示）→ `selectedIds.set(目标索引, 新号)` → 刷新显示并 postValue 提示修改成功
- [x] 5.6 Dialog 工具类新增静态方法 `listActionDialog(context, title, items, listener)`（标题 + 单选列表 + 底部三按钮"取消/删除/修改"）与监听接口 ListActionDialogListener，回调 `onResult(int selectedIndex, int actionIndex)`（0=取消、1=删除、2=修改）；未选中时点删除/修改忽略

## 6. 布局与资源

- [x] 6.1 auto_test_execution_fragment.xml：ll_line2 与分隔线之间新增 `tv_selected_ids`（待执行列表，默认 gone，可点击作为编辑入口）；ll_line3 不新增任何按钮，保持原 RUN_PART_CASES 用途
- [x] 6.2 strings.xml 调整文案：移除"请选择操作"标题（execution_select_operation_title）；"删除/修改"操作文案（execution_operation_delete/execution_operation_modify）保留复用为弹窗三按钮文案；保留选择要编辑的案例号标题、请输入新的案例号提示、已在待执行列表提示

## 7. 编译验证与清理

- [x] 7.1 写回全部明文改动到工作区，git status 确认仅预期文件变更
- [x] 7.2 全量编译（clean 后 compileDebugJavaWithJavac，使用本地 Gradle 8.6 + 可用的 AGP 版本，参考既有构建环境记忆），确认 BUILD SUCCESSFUL
- [x] 7.3 删除全部临时导出/脚本文件；如临时改过 build.gradle 则恢复原版本
