package com.zp.expand.jpacomment.service.impl;

import com.zp.expand.jpacomment.pojo.dto.TableAndColumnCommentDto;
import com.zp.expand.jpacomment.service.AlterCommentService;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description: sqlServer库-修改表注释和字段注释
 * @Author: zhoup
 * @Date: 2021/6/8
 **/
public class SqlServerAlterCommentServiceImpl implements AlterCommentService {
    /**
     * 默认 schema 为 dbo
     */
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
     * 1表名称 2字段名称
     */
    String checkTableCommentExistsSql = "SELECT COUNT(*) FROM ::fn_listextendedproperty ('MS_Description','SCHEMA',?,'TABLE',?,NULL,NULL)";

    /**
     * 1注释 2表名称 3字段名称
     */
    String updateTableComment = " execute sp_updateextendedproperty 'MS_Description',?,'SCHEMA',?,'TABLE',?";

    /**
     * 1注释 2表名称 3字段名称
     */
    String createTableComment = " execute sp_addextendedproperty 'MS_Description',?,'SCHEMA',?,'TABLE',?";

    /**
     * 1表名称 2字段名称
     */
    String checkColumnCommentExistsSql = "SELECT COUNT(*) FROM ::fn_listextendedproperty ('MS_Description','SCHEMA',?,'TABLE',?,'COLUMN',?)";

    /**
     * 1注释 2表名称 3字段名称
     */
    String updateColumnComment = " execute sp_updateextendedproperty 'MS_Description',?,'SCHEMA',?,'TABLE',?,'COLUMN',? ";

    /**
     * 1注释 2表名称 3字段名称
     */
    String createColumnComment = " execute sp_addextendedproperty 'MS_Description',?,'SCHEMA',?,'TABLE',?,'COLUMN',? ";

    @Override
    public void alterTableComment(String tableName, String tableComment) {
        Integer count = jdbcTemplate.queryForObject(checkTableCommentExistsSql, Integer.class, schema, tableName);
        if (count == null || count.equals(0)) {
            jdbcTemplate.update(createTableComment, tableComment, schema, tableName);
        } else {
            jdbcTemplate.update(updateTableComment, tableComment, schema, tableName);
        }
    }

    @Override
    public void alterColumnComment(String tableName, String columnName, String columnComment) {
        Integer count = jdbcTemplate.queryForObject(checkColumnCommentExistsSql, Integer.class, schema, tableName, columnName);
        if (count == null || count.equals(0)) {
            jdbcTemplate.update(createColumnComment, columnComment, schema, tableName, columnName);
        } else {
            jdbcTemplate.update(updateColumnComment, columnComment, schema, tableName, columnName);
        }
    }

    /**
     * 暂时未实现查询方法
     * @Author: zhoup
     * @Date: 2021/6/8
     * @param
     * @Return: java.util.List<com.zp.expand.jpacomment.pojo.dto.TableAndColumnCommentDto>
     **/
    @Override
    public List<TableAndColumnCommentDto> getOldComment(){
        return new ArrayList<>();
    }

    @Override
    public String getSchema() {
        return schema;
    }
}
