package cn.org.tpeach.nosql.annotation;

import java.lang.annotation.*;

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
