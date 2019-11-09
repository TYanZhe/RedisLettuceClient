package cn.org.tpeach.nosql.document;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ResponseBody {
    String require() default Request.Require.Required;
    String desc() default "";
    String demo() default "";
}
