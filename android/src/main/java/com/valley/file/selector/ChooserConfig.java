package com.valley.file.selector;

// 文件管理器配置
public class ChooserConfig {
    // 是否多选
    public boolean multiple;
    // 能选择最大文件数目
    public int max;

    public ChooserConfig(boolean multiple, int max) {
        this.multiple = multiple;
        this.max = max;
    }
}
