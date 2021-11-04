package com.zp.expand.jpa.comment.service.impl;

import com.zp.expand.jpa.comment.service.AlterCommentService;
import com.zp.expand.jpa.comment.pojo.dto.TableAndColumnCommentDto;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

/**
 * @Description: pg库-修改表注释和字段注释
 * @Author: zhoup
 * @Date: 2021/6/8
**/
public class PgSqlAlterCommentServiceImpl implements AlterCommentService {
    private String schema;

    @Override
    public void setSchema(String schema) {
        this.schema = schema;
    }

    private JdbcTemplate jdbcTemplate;

    @Override
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * 1SCHEMA 2表名称 3注释
     */
    String updateTableComment = "COMMENT ON TABLE %s.%s IS '%s';";

    /**
     * 1SCHEMA 2表名称 3字段名称 4字段注释
     */
    String updateColumnComment = "COMMENT ON COLUMN %s.%s.%s IS '%s'";

    /**
     * 查询数据库原始表和字段注释
    **/
    String selectTableAndColumn = "select " +
            " c.relname as table_name," +
            " cast(obj_description(relfilenode,'pg_class') as varchar) as table_comment," +
            " a.attname as column_name," +
            " (" +
            " select" +
            "   d.description" +
            " from pg_description d" +
            " where d.objoid=a.attrelid" +
            " and d.objsubid=a.attnum" +
            " ) as column_comment" +
            " from pg_class c,pg_attribute a,pg_type t" +
            " where a.attnum>0" +
            " and a.attrelid=c.oid" +
            " and a.atttypid=t.oid" +
            " and c.relname in (" +
            " select " +
            "   tablename " +
            " from pg_tables " +
            " where schemaname=?" +
            " and position('_2' in tablename)=0" +
            ")" +
            " order by c.relname,a.attnum";
//    String selectTableAndColumn = "select " +
//            " c.relname as table_name," +
//            " cast(obj_description(relfilenode,'pg_class') as varchar) as table_comment," +
//            " a.attname as column_name," +
//            " d.description as column_comment" +
//            " from pg_class c,pg_attribute a,pg_type t,pg_description d" +
//            " where a.attnum>0 " +
//            " and a.attrelid=c.oid " +
//            " and a.atttypid=t.oid " +
//            " and d.objoid=a.attrelid " +
//            " and d.objsubid=a.attnum" +
//            " and c.relname in (" +
//            " select " +
//            " tablename " +
//            " from pg_tables " +
//            " where schemaname=?" +
//            " and position('_2' in tablename)=0" +
//            ")" +
//            " order by c.relname,a.attnum";

    @Override
    public void alterTableComment(String tableName, String tableComment) {
        jdbcTemplate.update(String.format(updateTableComment, schema, tableName.toUpperCase(), tableComment));
    }

    @Override
    public void alterColumnComment(String tableName, String columnName, String columnComment) {
        jdbcTemplate.update(String.format(updateColumnComment, schema, tableName.toUpperCase(), columnName.toUpperCase(), columnComment));
    }

    @Override
    public List<TableAndColumnCommentDto> getOldComment(){
        return jdbcTemplate.query(selectTableAndColumn, new BeanPropertyRowMapper<TableAndColumnCommentDto>(TableAndColumnCommentDto.class), schema);
    }

    @Override
    public String getSchema() {
        return schema;
    }
}
