package com.zp.expand.jpa.comment.pojo.dto;


import java.io.Serializable;

/**
 * @Description: 表字段信息实体
 * @Author: zhoup
 * @Date: 2021/6/8
**/
public class ColumnCommentDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;//字段名

    private String comment;//字段注释

    private boolean appoint = false;//标注表是否添加@ColumnComment注解

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public boolean isAppoint() {
        return appoint;
    }

    public void setAppoint(boolean appoint) {
        this.appoint = appoint;
    }

    @Override
    public String toString() {
        return "ColumnCommentDTO{" +
                "name='" + name + '\'' +
                ", comment='" + comment + '\'' +
                ", appoint='" + appoint + '\'' +
                '}';
    }
}
