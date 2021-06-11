package com.zp.expand.jpacomment.enums;


import com.zp.expand.jpacomment.behavior.IEnum;

/**
 * 数据库枚举类
 * @Author: zhoup
 * @Date: 2021/6/4
  * @param
 * @Return:
**/
public enum DbTypeEnum implements IEnum {
    /**
     * dbtype
     */
    MYSQL {
        @Override
        public String getCode() {
            return "1";
        }

        @Override
        public String getValue() {
            return "MYSQL";
        }
    },
    SQLSERVER {
        @Override
        public String getCode() {
            return "2";
        }

        @Override
        public String getValue() {
            return "MICROSOFT SQL SERVER";
        }
    },
    ORACLE {
        @Override
        public String getCode() {
            return "3";
        }

        @Override
        public String getValue() {
            return "ORACLE";
        }
    },
    POSTGRESQL {
        @Override
        public String getCode() {
            return "4";
        }

        @Override
        public String getValue() {
            return "POSTGRESQL";
        }
    },
    DB2 {
        @Override
        public String getCode() {
            return "5";
        }

        @Override
        public String getValue() {
            return "DB2";
        }
    }
}
