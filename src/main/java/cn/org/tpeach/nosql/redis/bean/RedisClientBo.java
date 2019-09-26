package cn.org.tpeach.nosql.redis.bean;

import lombok.Data;

@Data
public class RedisClientBo {
    private String id;
    //客户端的地址和端口
    private String addr;
    // 套接字所使用的文件描述符
    private String fd;
    private String name;
    //以秒计算的已连接时长
    private Integer age;
    //以秒计算的空闲时长
    private String idle;
    //客户端 flag
    private String flags;
    //该客户端正在使用的数据库 ID
    private String db;
    //已订阅频道的数量
    private String sub;
    //已订阅模式的数量
    private String psub;
    //在事务中被执行的命令数量
    private String multi;
    //查询缓冲区的长度（字节为单位， 0 表示没有分配查询缓冲区）
    private String qbuf;
    //查询缓冲区剩余空间的长度（字节为单位， 0 表示没有剩余空间）
    private String qbuf_free;
    // 输出缓冲区的长度（字节为单位， 0 表示没有分配输出缓冲区）
    private String obl;
    //输出列表包含的对象数量（当输出缓冲区没有剩余空间时，命令回复会以字符串对象的形式被入队到这个队列里）
    private String oll;
    //输出缓冲区和输出列表占用的内存总量
    private String omem;
    //文件描述符事件
    private String events;
    //最近一次执行的命令
    private String cmd;
}
