package cn.org.tpeach.nosql.document;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE,ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestCondition {

    public static class RequireType{
        public static final String FORM = "form";
        public static final String JSON = "json";
    }
    String require() default Request.Require.Required;
    String desc() default "";
    String demo() default "";
    String requireType() default RequireType.FORM;
}
