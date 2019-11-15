package cn.org.tpeach.nosql.controller;

import cn.org.tpeach.nosql.constant.PublicConstant;
import cn.org.tpeach.nosql.exception.ServiceException;
import cn.org.tpeach.nosql.framework.LarkFrame;
import cn.org.tpeach.nosql.service.ServiceProxy;
import cn.org.tpeach.nosql.view.StatePanel;
import io.lettuce.core.RedisException;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CountDownLatch;
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
        return dispatcher(params,function,true);
    }
    public static <T, R> ResultRes<R> dispatcher(T params, Function<T, R> function,boolean isNeedGlassPanl) {
        ResultRes[] res = new ResultRes[1];
        CountDownLatch countDownLatch = new CountDownLatch(1);
        StatePanel.showLoading(()->{
            try {
                //成功返回
                res[0] = new ResultRes<R>(true, function.apply(params), null);
            } catch (Exception e) {
                if (e instanceof ServiceException || e instanceof RedisException) {
                    if(!PublicConstant.ProjectEnvironment.RELEASE.equals(LarkFrame.getProjectEnv())) {
                        log.error("业务接口异常", e);
                    }
                    res[0] =  new ResultRes<R>(false, null, e.getMessage());
                } else {
                    log.error("服务接口异常", e);
                    res[0] =  new ResultRes<R>(false, null, ServiceProxy.getStackTrace(e));
                }
            }finally {
                countDownLatch.countDown();
            }
        },isNeedGlassPanl,true);
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return res[0];
    }
    public static <T> ResultRes<T> dispatcher(Supplier<T> function ) {
       return dispatcher(function,true);
    }

    public static <T> ResultRes<T> dispatcher(Supplier<T> function,boolean isNeedGlassPanl) {
        ResultRes[] res = new ResultRes[1];
        CountDownLatch countDownLatch = new CountDownLatch(1);
        StatePanel.showLoading(()->{
            try {
                //成功返回
                T t = function.get();
                res[0] =  new ResultRes<T>(true, t, null);
            } catch (Exception e) {
                if(e instanceof ServiceException){
                    if(!PublicConstant.ProjectEnvironment.RELEASE.equals(LarkFrame.getProjectEnv())) {
                        log.error("业务接口异常", e);
                    }
                    res[0] =  new ResultRes<T>(false, null,  e.getMessage());
                }else if(e instanceof RedisException){
                    if(!PublicConstant.ProjectEnvironment.RELEASE.equals(LarkFrame.getProjectEnv())) {
                        log.error("业务接口异常", e);
                    }
                    String msg = e.getMessage();
                    Throwable ex = e;
                    while (ex.getCause() != null){
                        ex = ex.getCause();
                        msg = ex.getMessage();
                    }
                    res[0] =  new ResultRes<T>(false, null, msg);
                } else {
                    log.error("服务接口异常",e);
                    res[0] =  new ResultRes<T>(false, null,  ServiceProxy.getStackTrace(e));
                }

            }finally {
                countDownLatch.countDown();
            }
        },isNeedGlassPanl,true);
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return res[0];
    }

}
