package cn.org.tpeach.nosql.service;

import cn.org.tpeach.nosql.framework.LarkFrame;
import cn.org.tpeach.nosql.framework.LarkLog;
import cn.org.tpeach.nosql.framework.LogMsg;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.Observable;
import java.util.Observer;

/**
 * 日志面板接受日志
 */
@Slf4j
public class LogAreaAppender implements Observer {

    @Override
    public void update(Observable o, Object arg) {

        try{
            LarkLog larkLog = (LarkLog) o;
            LogMsg logMsg = larkLog.getLogMsg();
            Color  color = (Color) arg;
            String msg = logMsg.getMsg();
            if(msg != null && msg.length()>10*1024){
                msg = msg.substring(0,10*1024)+"......(ignore:"+(msg.length()-10*1024)+")";
            }
            if(logMsg.getReceiveTime() != null){
                LarkFrame.logArea.println(logMsg.getReceiveTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"))+" "+msg, color);
            }else{
                LarkFrame.logArea.println(msg, color);
            }

//            if (logMsg != null) {
//            }
        }catch (Exception e){
            log.error("LogAreaAppender 接受消息异常",e);
        }


    }
}
