package com.zp.expand.jpacomment.pojo.dto;

/**
 * @Description: TODO
 * @Author zhoup
 * @Date 2021/6/8
 **/
public class TableAndColumnCommentDto {

    private String tableName;
    private String tableComment;
    private String columnName;
    private String columnComment;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getTableComment() {
        return tableComment;
    }

    public void setTableComment(String tableComment) {
        this.tableComment = tableComment;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getColumnComment() {
        return columnComment;
    }

    public void setColumnComment(String columnComment) {
        this.columnComment = columnComment;
    }

}
