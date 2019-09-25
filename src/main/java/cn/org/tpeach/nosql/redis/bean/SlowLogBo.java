package cn.org.tpeach.nosql.redis.bean;

import lombok.Data;

@Data
public class SlowLogBo {
    private Long id;
    private Long operTime;
    private Long excuteTime;
    private String cmd;

}
