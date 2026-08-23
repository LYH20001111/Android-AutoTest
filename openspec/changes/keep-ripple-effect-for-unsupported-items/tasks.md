## 1. 修改 MyRecycleAdapter 背景设置方式

- [ ] 1.1 在 `MyRecycleAdapter.onBindViewHolder` 中，将不适用测试项分支的 `holder.llItemType.setBackgroundColor(...)` 替换为 `holder.llItemType.setBackgroundTintList(ColorStateList.valueOf(...))`，验证不适用测试项背景色 `#F0F0F0` 保持不变且点击时显示水波纹
- [ ] 1.2 在正常测试项分支中，添加 `holder.llItemType.setBackgroundTintList(null)` 以清除 RecyclerView 视图复用导致的 tint 残留，验证正常测试项背景色恢复为白色且水波纹正常