package com.zp.expand.jpa.comment.annotation;

import java.lang.annotation.*;


@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface ColumnComment {

    /**
     * 字段注释
     * @return String
     */
    String value() default "";
}
