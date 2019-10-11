package cn.org.tpeach.nosql.redis.command.server;

import cn.org.tpeach.nosql.enums.RedisVersion;
import cn.org.tpeach.nosql.redis.command.JedisCommand;
import cn.org.tpeach.nosql.redis.command.RedisLarkContext;

public class ClientListCommand extends JedisCommand<String> {
    /**
     *
     * @param id
     */
    public ClientListCommand(String id) {
        super(id);
    }

    @Override
    public String sendCommand() {
        return "CLIENT LIST";
    }

    /**
     *
     * @param redisLarkContext
     * @return
     * ddr=127.0.0.1:43167 fd=7 age=24 idle=6 flags=N db=0 sub=0 psub=0 multi=-1 qbuf=0 qbuf-free=0 obl=0 oll=0 omem=0 events=r cmd=get
     * 命令返回多行字符串，这些字符串按以下形式被格式化：
     *
     * 每个已连接客户端对应一行（以 LF 分割）
     * 每行字符串由一系列 属性=值 形式的域组成，每个域之间以空格分开
     * 以下是域的含义：
     *
     * addr ： 客户端的地址和端口
     * fd ： 套接字所使用的文件描述符
     * age ： 以秒计算的已连接时长
     * idle ： 以秒计算的空闲时长
     * flags ： 客户端 flag
     * db ： 该客户端正在使用的数据库 ID
     * sub ： 已订阅频道的数量
     * psub ： 已订阅模式的数量
     * multi ： 在事务中被执行的命令数量
     * qbuf ： 查询缓冲区的长度（字节为单位， 0 表示没有分配查询缓冲区）
     * qbuf-free ： 查询缓冲区剩余空间的长度（字节为单位， 0 表示没有剩余空间）
     * obl ： 输出缓冲区的长度（字节为单位， 0 表示没有分配输出缓冲区）
     * oll ： 输出列表包含的对象数量（当输出缓冲区没有剩余空间时，命令回复会以字符串对象的形式被入队到这个队列里）
     * omem ： 输出缓冲区和输出列表占用的内存总量
     * events ： 文件描述符事件
     * cmd ： 最近一次执行的命令
     * 客户端 flag 可以由以下部分组成：
     *
     * O ： 客户端是 MONITOR 模式下的附属节点（slave）
     * S ： 客户端是一般模式下（normal）的附属节点
     * M ： 客户端是主节点（master）
     * x ： 客户端正在执行事务
     * b ： 客户端正在等待阻塞事件
     * i ： 客户端正在等待 VM I/O 操作（已废弃）
     * d ： 一个受监视（watched）的键已被修改， EXEC 命令将失败
     * c : 在将回复完整地写出之后，关闭链接
     * u : 客户端未被阻塞（unblocked）
     * A : 尽可能快地关闭连接
     * N : 未设置任何 flag
     * 文件描述符事件可以是：
     *
     * r : 客户端套接字（在事件 loop 中）是可读的（readable）
     * w : 客户端套接字（在事件 loop 中）是可写的（writeable）
     */
    @Override
    public String concreteCommand(RedisLarkContext<byte[], byte[]> redisLarkContext) {
        return redisLarkContext.clientList();
    }

    @Override
    public RedisVersion getSupportVersion() {
        return RedisVersion.REDIS_2_4;
    }
}
