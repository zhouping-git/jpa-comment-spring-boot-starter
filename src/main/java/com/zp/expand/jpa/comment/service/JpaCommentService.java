package com.zp.expand.jpa.comment.service;

import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.StrUtil;
import com.zp.expand.jpa.comment.pojo.dto.TableAndColumnCommentDto;
import com.zp.expand.jpa.comment.pojo.dto.TableCommentDTO;
import com.zp.expand.jpa.comment.annotation.ColumnComment;
import com.zp.expand.jpa.comment.annotation.TableComment;
import com.zp.expand.jpa.comment.pojo.dto.ColumnCommentDTO;
import org.hibernate.internal.SessionFactoryImpl;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.persister.entity.SingleTableEntityPersister;
import org.hibernate.persister.walking.spi.AttributeDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: JPA 字段注释处理类
 * @Author: zhoup
 * @Date: 2021/6/8
**/
public class JpaCommentService {

    public static Logger logger = LoggerFactory.getLogger(JpaCommentService.class);

    private EntityManager entityManager;

    AlterCommentService alterCommentService;

    Map<String, TableCommentDTO> dtoMap;

    boolean merge,automatic;

    Map<String, TableCommentDTO> oldMap;

    public void init() {
        dtoMap = findAllTableAndColumn();

        //增量更新时缓存原始数据
        if(merge){
            List<TableAndColumnCommentDto> list = alterCommentService.getOldComment();
            if(list.size() > 0){
                oldMap = new HashMap();
                for(TableAndColumnCommentDto dto : list){
                    if(oldMap.containsKey(dto.getTableName()) && StrUtil.isNotBlank(dto.getColumnName())){
                        TableCommentDTO tableDto = oldMap.get(dto.getTableName());
                        List<ColumnCommentDTO> columnList = tableDto.getColumnCommentDTOList();
                        ColumnCommentDTO columnDto = new ColumnCommentDTO();
                        columnDto.setName(dto.getColumnName());
                        columnDto.setComment(dto.getColumnComment());
                        columnList.add(columnDto);

                        tableDto.setColumnCommentDTOList(columnList);
                        oldMap.put(dto.getTableName(), tableDto);
                    }else{
                        TableCommentDTO tableDto = new TableCommentDTO();
                        tableDto.setName(dto.getTableName());
                        tableDto.setComment(dto.getTableComment());

                        if(StrUtil.isNotBlank(dto.getColumnName())) {
                            List<ColumnCommentDTO> columnList = new ArrayList<>();
                            ColumnCommentDTO columnDto = new ColumnCommentDTO();
                            columnDto.setName(dto.getColumnName());
                            columnDto.setComment(dto.getColumnComment());
                            columnList.add(columnDto);

                            tableDto.setColumnCommentDTOList(columnList);
                        }
                        oldMap.put(dto.getTableName(), tableDto);
                    }
                }
            }
        }

        logger.info("JpaCommentService 初始化成功...");

        if(automatic){
            logger.info("JpaCommentService 启动更新表和字段注释...");
            alterAllTableAndColumn();
        }
    }

    /**
     * 设置当前 schema 用于中途修改schema
     * @Author: zhoup
     * @Date: 2021/6/8
      * @param schema 模式 mysql来说就是database
     * @Return: void
    **/
    public void setCurrentSchema(String schema) {
        alterCommentService.setSchema(schema);
    }

    /**
     * 用于中途修改 数据源的可能
     * @Author: zhoup
     * @Date: 2021/6/8
      * @param jdbcTemplate
     * @Return: void
    **/
    public void setCurrentJdbcTemplate(JdbcTemplate jdbcTemplate) {
        alterCommentService.setJdbcTemplate(jdbcTemplate);
    }

    /**
     * 更新整个数据库的表注释和字段注释，非空情况下才更新
     * @Author: zhoup
     * @Date: 2021/6/8
      * @param
     * @Return: void
    **/
    public void alterAllTableAndColumn() {
        Map<String, TableCommentDTO> dtoMap = findAllTableAndColumn();
        dtoMap.forEach((k, v) -> {
            try {
                alterSingleTableAndColumn(k);
            } catch (Exception e) {
                logger.error("tableName '{}' ALTER comment exception ", k, e);
            }
        });
    }

    /**
     * 更新单个数据库的表注释和字段注释，非空情况下才更新
     * @Author: zhoup
     * @Date: 2021/6/8
      * @param tableName 数据库表名称
     * @Return: void
    **/
    public void alterSingleTableAndColumn(String tableName) {
        TableCommentDTO commentDTO = dtoMap.get(tableName);
        if(merge){
            TableCommentDTO oldDto = oldMap.get(tableName);
            if(commentDTO == null && oldDto != null){
                if(StrUtil.isNotBlank(oldDto.getComment())) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("去除表 {} 的注释", oldDto.getName());
                    }
                    alterCommentService.alterTableComment(oldDto.getName(), "");
                }
                oldDto.getColumnCommentDTOList().forEach(
                        item -> {
                            if(StrUtil.isNotBlank(item.getComment())) {
                                if (logger.isDebugEnabled()) {
                                    logger.debug("去除表 {} 字段 {} 的注释", oldDto.getName(), item.getName());
                                }
                                alterCommentService.alterColumnComment(oldDto.getName(), item.getName(), "");
                            }
                        });
            }else if(commentDTO != null){
                if(oldDto != null){
                    if(!commentDTO.isAppoint()){
                        cleanTableComment(commentDTO.getName());
                    }else if(!commentDTO.getComment().equals(oldDto.getComment())){
                        updateTableComment(commentDTO.getName(), commentDTO.getComment(), commentDTO.isAppoint());
                    }
                    commentDTO.getColumnCommentDTOList().forEach(
                            item -> {
                                List<ColumnCommentDTO> tempList =oldDto.getColumnCommentDTOList().stream().filter(e -> e.getName().equals(item.getName())).collect(Collectors.toList());
                                if(tempList.size() > 0){
                                    if(!item.isAppoint()){
                                        cleanColumnComment(commentDTO.getName(), item.getName());
                                    }else if(!tempList.get(0).getComment().equals(item.getComment())){
                                        updateColumnComment(commentDTO.getName(), item.getName(), item.getComment(), item.isAppoint());
                                    }
                                }else {
                                    updateColumnComment(commentDTO.getName(), item.getName(), item.getComment(), item.isAppoint());
                                }
                            });
                }else{
                    updateTableComment(commentDTO.getName(), commentDTO.getComment(), commentDTO.isAppoint());
                    commentDTO.getColumnCommentDTOList().forEach(item -> updateColumnComment(commentDTO.getName(), item.getName(), item.getComment(), item.isAppoint()));
                }
            }else{
                logger.warn("tableName '{}' not find in JPA ", tableName);
            }
        }else {
            if (commentDTO != null) {
                updateTableComment(commentDTO.getName(), commentDTO.getComment(), commentDTO.isAppoint());
                commentDTO.getColumnCommentDTOList().forEach(item -> updateColumnComment(commentDTO.getName(), item.getName(), item.getComment(), item.isAppoint()));
            } else {
                logger.warn("tableName '{}' not find in JPA ", tableName);
            }
        }
    }

    private void updateTableComment(String tableName, String comment, boolean appoint){
        if(appoint) {
            comment = StrUtil.isNotBlank(comment) ? comment : "";
            if (logger.isDebugEnabled()) {
                logger.debug("修改表 {} 的注释为 '{}'", tableName, comment);
            }
            alterCommentService.alterTableComment(tableName, comment);
        }
    }

    private void cleanTableComment(String tableName){
        if (logger.isDebugEnabled()) {
            logger.debug("去除表 {} 的注释", tableName);
        }
        alterCommentService.alterTableComment(tableName, "");
    }

    private void updateColumnComment(String tableName, String columnName, String comment, boolean appoint){
        if(appoint) {
            comment = StrUtil.isNotBlank(comment) ? comment : "";
            if (logger.isDebugEnabled()) {
                logger.debug("修改表 {} 字段 {} 的注释为 '{}'", tableName, columnName, comment);
            }
            alterCommentService.alterColumnComment(tableName, columnName, comment);
        }
    }

    private void cleanColumnComment(String tableName, String columnName){
        if (logger.isDebugEnabled()) {
            logger.debug("去除表 {} 字段 {} 的注释", tableName, columnName);
        }
        alterCommentService.alterColumnComment(tableName, columnName, "");
    }


    public Map<String, TableCommentDTO> findAllTableAndColumn() {
        Map<String, TableCommentDTO> tableCommentMap = new HashMap<>(256);
        //通过EntityManager获取factory
        EntityManagerFactory entityManagerFactory = entityManager.getEntityManagerFactory();
//        SessionFactoryImpl sessionFactory = (SessionFactoryImpl) entityManagerFactory.unwrap(SessionFactory.class);
        SessionFactoryImpl sessionFactory = entityManagerFactory.unwrap(SessionFactoryImpl.class);
        Map<String, EntityPersister> persisterMap = sessionFactory.getMetamodel().entityPersisters();
        for (Map.Entry<String, EntityPersister> entity : persisterMap.entrySet()) {
            SingleTableEntityPersister persister = (SingleTableEntityPersister) entity.getValue();
            Class targetClass = entity.getValue().getMappedClass();
            TableCommentDTO table = new TableCommentDTO();
            // 表注释
            getTableInfo(persister, table, targetClass);
            //除主键外的属性注释
            getColumnInfo(persister, table, targetClass);
            // 主键字段注释
            getKeyColumnInfo(persister, table, targetClass);

            tableCommentMap.put(table.getName(), table);
        }

        return tableCommentMap;
    }

    private void getTableInfo(SingleTableEntityPersister persister, TableCommentDTO table, Class targetClass) {
        table.setColumnCommentDTOList(new ArrayList<>(32));
        table.setName(persister.getTableName());

        TableComment tableComment = AnnotationUtil.getAnnotation(targetClass, TableComment.class);
        if (tableComment != null) {
            table.setComment(tableComment.value());
            table.setAppoint(true);
        } else {
            table.setComment("");
            table.setAppoint(false);
        }
    }

    /**
     * 递归获取所有父类的类对象 包括自己
     * 最后的子类在第一个
     * @Author: zhoup
     * @Date: 2021/6/8
      * @param targetClass
     * @param list
     * @Return: void
    **/
    private void getAllClass(Class targetClass, List<Class> list) {
        list.add(targetClass);

        if (!Object.class.equals(targetClass.getSuperclass())) {
            getAllClass(targetClass.getSuperclass(), list);
        }
    }

    private void getColumnInfo(SingleTableEntityPersister persister, TableCommentDTO table, Class targetClass) {
        // 情况比较复杂，必须还要判断是否有父类，存在父类则还要取父类的字段信息，优先取得子类字段为依据
        List<Class> classList = new ArrayList<>(2);
        getAllClass(targetClass, classList);

        Set<String> alreadyDealField = new HashSet<>(32);
        Set<String> allColumnField = new HashSet<>(32);

        Iterable<AttributeDefinition> attributes = persister.getAttributes();
        //属性
        for (AttributeDefinition attr : attributes) {
            allColumnField.add(attr.getName());
        }

        classList.forEach(classItem -> Arrays.stream(ClassUtil.getDeclaredFields(classItem)).forEach(field -> {
            if (allColumnField.contains(field.getName())) {
                // 判断是否已经处理过
                if (!alreadyDealField.contains(field.getName())) {
                    //对应数据库表中的字段名
                    String[] columnName = persister.getPropertyColumnNames(field.getName());
                    getColumnComment(table, classItem, field.getName(), columnName);
                    alreadyDealField.add(field.getName());
                }
            }
        }));
    }

    private void getKeyColumnInfo(SingleTableEntityPersister persister, TableCommentDTO table, Class targetClass) {
        String idName = persister.getIdentifierPropertyName();
        String[] idColumns = persister.getIdentifierColumnNames();
        getColumnComment(table, targetClass, idName, idColumns);
    }

    private void getColumnComment(TableCommentDTO table, Class targetClass, String propertyName, String[] columnName) {
        ColumnComment idColumnComment = AnnotationUtil.getAnnotation(
                ClassUtil.getDeclaredField(targetClass, propertyName), ColumnComment.class);
        Arrays.stream(columnName).forEach(item -> {
            ColumnCommentDTO column = new ColumnCommentDTO();
            column.setName(item);
            if (idColumnComment != null) {
                column.setComment(idColumnComment.value());
                column.setAppoint(true);
            } else {
                column.setComment("");
                column.setAppoint(false);
            }
            table.getColumnCommentDTOList().add(column);
        });
    }

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public void setAlterCommentService(AlterCommentService alterCommentService) {
        this.alterCommentService = alterCommentService;
    }

    public void setMerge(boolean merge) {
        this.merge = merge;
    }

    public void setAutomatic(boolean automatic) {
        this.automatic = automatic;
    }
}
