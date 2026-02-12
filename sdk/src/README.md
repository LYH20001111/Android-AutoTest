# NSDK

工程模块说明：
- `nsdkcommon`：内外置公用部分。
- `nsdk`：内置模块，发布时编译成 aar 需要包含 `nsdkcommon` 代码。
- `nsdkexternal`：外置模块，发布时需要编译两个 aar：
  - 包含 `nsdkcommon` 代码的 aar：可单独使用，在只操作外置模块时使用。
  - 不包含 `nsdkcommon` 代码的 aar：不可单独使用，是作为 `nsdk` 的插件包，在使用内置模块的同时，如果需要操作外置模块，再引入此插件包。需要依赖 `nsdk` 使用。
- `nsdkplugincard`：扩展卡插件包，不可单独使用，需要搭配 `nsdk` 或者 `nsdkexternal` 使用。
- `dukptmacverification`：这是为了满足客户定制需求的测试，在外置报文添加 ksn+mac 校验。目前不会使用此功能，暂时无用。
- `nsdkdemo`：包含内外置接口的调用测试示例。
- `nsdkextdemo`：外置接口的调用测试示例。