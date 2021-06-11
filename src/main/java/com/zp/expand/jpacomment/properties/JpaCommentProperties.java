package com.zp.expand.jpacomment.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @Description: 属性配置文件
 * @Author: zhoup
 * @Date: 2021/6/8
**/
@ConfigurationProperties(prefix = "zp.expand.jpa.comment")
public class JpaCommentProperties {

    private boolean enable = true;//配置开关，是否开启注释功能
    private boolean automatic = false;//是否在项目启动时自动执行，默认不自动执行
    private boolean merge = true;//是否采用增量更新方式

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public boolean isAutomatic() {
        return automatic;
    }

    public void setAutomatic(boolean automatic) {
        this.automatic = automatic;
    }

    public boolean isMerge() {
        return merge;
    }

    public void setMerge(boolean merge) {
        this.merge = merge;
    }
}
