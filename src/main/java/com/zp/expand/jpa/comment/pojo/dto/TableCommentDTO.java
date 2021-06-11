package com.zp.expand.jpa.comment.pojo.dto;


import java.io.Serializable;
import java.util.List;

/**
 * @Description: 表信息实体
 * @Author: zhoup
 * @Date: 2021/6/8
 **/
public class TableCommentDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;//表名

    private String comment;//表注释

    private boolean appoint = false;//标注表是否添加@TableComment注解

    private List<ColumnCommentDTO> columnCommentDTOList;

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

    public List<ColumnCommentDTO> getColumnCommentDTOList() {
        return columnCommentDTOList;
    }

    public void setColumnCommentDTOList(List<ColumnCommentDTO> columnCommentDTOList) {
        this.columnCommentDTOList = columnCommentDTOList;
    }

    public boolean isAppoint() {
        return appoint;
    }

    public void setAppoint(boolean appoint) {
        this.appoint = appoint;
    }

    @Override
    public String toString() {
        return "TableCommentDTO{" +
                "name='" + name + '\'' +
                ", comment='" + comment + '\'' +
                ", appoint='" + appoint + '\'' +
                ", columnCommentDTOList=" + columnCommentDTOList +
                '}';
    }
}
