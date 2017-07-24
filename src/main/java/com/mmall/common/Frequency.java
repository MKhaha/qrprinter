package com.mmall.common;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * Created by guotao on 2017/7/21.
 * com.mmall.common
 * qrprinter
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface Frequency {
    String name() default "all";
    int time()  default 0;
    int limit()  default 0;
}
