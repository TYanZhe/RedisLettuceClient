package cn.org.tpeach.nosql.controller;

import cn.org.tpeach.nosql.exception.ServiceException;
import cn.org.tpeach.nosql.service.ServiceProxy;
import io.lettuce.core.RedisException;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author tyz
 * @Title: RedisMainFrame
 * @ProjectName RedisLark
 * @Description:
 * @date 2019-06-30 22:11
 * @since 1.0.0
 */
@Slf4j
public abstract class BaseController {

    public static <T, R> ResultRes<R> dispatcher(T params, Function<T, R> function) {
        try {
            //成功返回
//            log.debug("请求参数"+params);
            return new ResultRes<R>(true, function.apply(params), null);
        } catch (Exception e) {
            if(e instanceof ServiceException || e instanceof RedisException){
                log.error("业务接口异常",e);
                return new ResultRes<R>(false, null,  e.getMessage());
            }else{
                log.error("服务接口异常",e);
                return new ResultRes<R>(false, null,  ServiceProxy.getStackTrace(e));
            }
        }
    }

    public static <T> ResultRes<T> dispatcher(Supplier<T> function) {
        try {
            //成功返回
            T t = function.get();
//            logger.debug("请求返回："+t);
            return new ResultRes<T>(true, t, null);
        } catch (Exception e) {
            if(e instanceof ServiceException){
                log.error("业务接口异常",e);
                return new ResultRes<T>(false, null,  e.getMessage());
            }else if(e instanceof RedisException){
                log.error("业务接口异常",e);
                String msg = e.getMessage();
                Throwable ex = e;
                while (ex.getCause() != null){
                    ex = ex.getCause();
                    msg = ex.getMessage();
                }
                return new ResultRes<T>(false, null, msg);
            } else {
                log.error("服务接口异常",e);
//                if(e instanceof  JedisException){
//                    return new ResultRes<T>(false, null,  e.getMessage());
//                }
                return new ResultRes<T>(false, null,  ServiceProxy.getStackTrace(e));
            }

        }
    }

}
