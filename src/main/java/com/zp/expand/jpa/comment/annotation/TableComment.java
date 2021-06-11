package com.zp.expand.jpa.comment.annotation;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface TableComment {

    /**
     * 字段注释
     * @return String
     */
    String value() default "";
}
