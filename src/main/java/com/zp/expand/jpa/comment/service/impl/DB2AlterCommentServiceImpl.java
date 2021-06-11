package com.zp.expand.jpa.comment.service.impl;

import com.zp.expand.jpa.comment.service.AlterCommentService;
import com.zp.expand.jpa.comment.pojo.dto.TableAndColumnCommentDto;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

/**
 * @Description: db2库-修改表注释和字段注释
 * @Author zhoup
 * @Date 2021/6/8
 **/
public class DB2AlterCommentServiceImpl implements AlterCommentService {
    private String schema;
    private JdbcTemplate jdbcTemplate;

    /**
     * 修改表注释：1、表名；2、注释内容
     * @Author: zhoup
     * @Date: 2021/6/8
    **/
    String updateTableComment = "COMMENT ON TABLE %s IS '%s';";
    /**
     * 修改表注释：1、表名；2、列名；3、注释内容
     * @Author: zhoup
     * @Date: 2021/6/8
     **/
    String updateColumnComment = "COMMENT ON COLUMN %s.%s IS '%s';";

    String selectTableAndColumn = "SELECT" +
//            "--    VARCHAR(TABSCHEMA,10) AS TABSCHEMA, --模式名\n" +
            "    VARCHAR(tab.TABNAME,50)   AS table_name," +
//            "--    TYPE, --类型(T: 表, V:视图, N:昵称)\n" +
//            "--    CARD, --记录数(最新一次RUNSTATS统计)\n" +
//            "--    DEC(AVGROWCOMPRESSIONRATIO,5,2) AS COMPRESS_RATIO, --压缩比例\n" +
//            "--    LASTUSED, --最近一次访问日期(增删改查)\n" +
//            "--    CREATE_TIME, --表的创建时间\n" +
//            "--    TBSPACE, --所属表空间(非PARTITION表)\n" +
            "    tab.REMARKS AS table_column," +
            "    col.COLNAME AS column_name," +
            "    col.REMARKS AS column_comment" +
//            "    col.TYPENAME --字段类型\n" +
            " FROM SYSCAT.TABLES tab" +
            " LEFT JOIN SYSCAT.COLUMNS col" +
            " ON col.TABNAME = tab.TABNAME" +
            " AND col.TABSCHEMA = ?" +
            " WHERE tab.TABSCHEMA = ?" +
            " AND tab.TYPE = 'T';";

    @Override
    public String getSchema() {
        return schema;
    }

    @Override
    public void setSchema(String schema) {
        this.schema = schema;
    }

    @Override
    public void alterTableComment(String tableName, String tableComment) {
        jdbcTemplate.update(String.format(updateTableComment, tableName.toUpperCase(), tableComment));
    }

    @Override
    public void alterColumnComment(String tableName, String columnName, String columnComment) {
        jdbcTemplate.update(updateColumnComment, tableName.toUpperCase(), columnName.toUpperCase(), columnComment);
    }

    @Override
    public List<TableAndColumnCommentDto> getOldComment(){
        return jdbcTemplate.query(selectTableAndColumn, new BeanPropertyRowMapper<TableAndColumnCommentDto>(TableAndColumnCommentDto.class), schema, schema);
    }

    @Override
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
}
