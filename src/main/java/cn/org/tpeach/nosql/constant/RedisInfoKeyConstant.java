package cn.org.tpeach.nosql.constant;

public class RedisInfoKeyConstant {
    /**版本*/
    public final static String redisVersion = "redis_version";
    /**服务模式*/
    public final static String redisMode = "redis_mode";

    /**系统版本*/
    public final static String OS = "os";
    /**进程id*/
    public final static String processId = "process_id";
    /**端口*/
    public final static String tcpPort = "tcp_port";
    /** */
    public final static String uptimeInSeconds = "uptime_in_seconds";
    /** */
    public final static String uptimeInDays = "uptime_in_days";
    /**连接客户端数量*/
    public final static String connectedClients = "connected_clients";
    /**最长的输出列表*/
    public final static String clientLongestOutputList = "client_longest_output_list";
    /**最大输入缓存*/
    public final static String clientBiggestInputBuf = "client_biggest_input_buf";
    /**阻塞客户端数量*/
    public final static String blockedClients = "blocked_clients";
    /**使用内存*/
    public final static String usedMemory = "used_memory";
    /**使用内存*/
    public final static String usedMemoryHuman = "used_memory_human";
    /**分配内存*/
    public final static String usedMemoryRss = "used_memory_rss";
    /**使用内存高峰*/
    public final static String usedMemoryPeak = "used_memory_peak";
    /**使用内存高峰*/
    public final static String usedMemoryPeakHuman = "used_memory_peak_human";
    /**内存碎片率*/
    public final static String memFragmentationRatio = "mem_fragmentation_ratio";
    /**内存分配器*/
    public final static String memAllocator = "mem_allocator";


    /** 已连接的客户端总数*/
    public final static String totalConnectionsReceived = "total_connections_received";
    /**已执行过的命令总数*/
    public final static String totalCommandsProcessed = "total_commands_processed";
    /** 服务每秒执行数量*/
    public final static String instantaneousOpsOerSec = "instantaneous_ops_per_sec";
    /**服务输入网络流量 */
    public final static String totalNetInputBytes = "total_net_input_bytes";
    /** 服务输出网络流量*/
    public final static String totalNetOutputBytes = "total_net_output_bytes";
    /**输入带宽*/
    public final static String instantaneousIutputKbps = "instantaneous_input_kbps";
    /** 输出带宽*/
    public final static String instantaneousOutputKbps = "instantaneous_output_kbps";
    /** 拒绝连接客户端数*/
    public final static String rejectedConnections = "rejected_connections";
    /**服务主进程在核心态累积CPU耗时 */
    public final static String usedCpuSys = "used_cpu_sys";
    /**服务主进程在用户态累积CPU耗时 */
    public final static String usedCpuUser = "used_cpu_user";
    /**服务后台进程在核心态累积CPU耗时 */
    public final static String usedCpuSysChildren = "used_cpu_sys_children";
    /**服务后台进程在用户态累积CPU耗时 */
    public final static String usedCpuUserChildren = "used_cpu_user_children";






}
