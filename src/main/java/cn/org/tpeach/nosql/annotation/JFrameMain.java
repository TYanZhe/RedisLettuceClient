package cn.org.tpeach.nosql.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author tyz
 * @Title: JFrameMain
 * @ProjectName RedisLark
 * @Description: TODO
 * @date 2019-06-27 20:38
 * @since 1.0.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
@Documented
public @interface JFrameMain {
	String value();
}
