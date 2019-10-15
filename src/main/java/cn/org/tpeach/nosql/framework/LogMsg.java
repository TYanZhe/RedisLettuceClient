package cn.org.tpeach.nosql.framework;

import lombok.Getter;

import java.time.LocalDateTime;
@Getter
public class LogMsg {
    private LocalDateTime receiveTime;
    private String msg;

    public LogMsg(String msg,LocalDateTime receiveTime) {
        this.msg = msg;
        this.receiveTime = receiveTime;
    }
}
