package com.zp.expand.jpacomment.config;

import com.zp.expand.jpacomment.service.JpaCommentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * @Description: 自动执行配置类
 * @Author zhoup
 * @Date 2021/6/8
 **/
//@Configuration
//@AutoConfigureAfter({JpaCommentService.class})
//@ConditionalOnProperty(prefix = "zp.expand.jpa.comment", value = {"enable","automatic"}, havingValue = "true")
public class JpaCommentAutomaticConfig {
//    public static Logger logger = LoggerFactory.getLogger(JpaCommentAutomaticConfig.class);
//    @Resource
//    private JpaCommentService jpaCommentService;
//
//    /**
//     * 默认重新生成全库表注释
//     * @Author: zhoup
//     * @Date: 2021/6/8
//      * @param
//     * @Return: void
//    **/
//    @PostConstruct
//    public void init(){
//        jpaCommentService.alterAllTableAndColumn();
//        logger.info("Jpa-comment 自动同步数据库表和字段注释完成...");
//    }
}
