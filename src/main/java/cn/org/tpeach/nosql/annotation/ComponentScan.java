package cn.org.tpeach.nosql.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
public @interface ComponentScan {
    String[] basePackages() default {} ;
}
