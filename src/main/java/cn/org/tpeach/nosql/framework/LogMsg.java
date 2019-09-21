package cn.org.tpeach.nosql.framework;

import lombok.Getter;

import java.time.LocalDateTime;
@Getter
public class LogMsg {
    private LocalDateTime receiveTime = LocalDateTime.now();
    private String msg;

    public LogMsg(String msg) {
        this.msg = msg;
    }
}
