package cn.org.tpeach.nosql.redis.command.server;

import cn.org.tpeach.nosql.enums.RedisVersion;
import cn.org.tpeach.nosql.redis.command.JedisCommand;
import cn.org.tpeach.nosql.redis.command.RedisLarkContext;
import cn.org.tpeach.nosql.redis.command.string.GetString;
import cn.org.tpeach.nosql.tools.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * @author tyz
 * @Title: InfoCommand
 * @ProjectName RedisLark
 * @Description: TODO
 * @date 2019-07-04 08:48
 * @since 1.0.0
 */
public class InfoCommand extends JedisCommand<Map<String,String>> {
	final static Logger logger = LoggerFactory.getLogger(InfoCommand.class);
	private boolean newInfo = false;
	/**
	 * 命令: Info
	 * @param id
	 */
	public InfoCommand(String id) {
		super(id);
	}
	public InfoCommand(String id,boolean newInfo) {
		super(id);
		this.newInfo = newInfo;
	}
	@Override
	public String sendCommand() {
//		return "INFO";
		//在redisLark中打印日志
		return null;
	}


	/**
	 * 返回关于 Redis 服务器的各种信息和统计值。
	 * 时间复杂度：O(1)
	 * @param redisLarkContext
	 * @return 给定配置参数的值。
	 * redis_version:2.4.4             # Redis 的版本</br>
	 * redis_git_sha1:00000000</br>
	 * redis_git_dirty:0</br>
	 * arch_bits:32</br>
	 * multiplexing_api:epoll</br>
	 * process_id:903                  # 当前 Redis 服务器进程id</br>
	 * uptime_in_seconds:24612         # 运行时间(以秒计算)</br>
	 * uptime_in_days:0                # 运行时间(以日计算)</br>
	 * lru_clock:283730</br>
	 * used_cpu_sys:3.38</br>
	 * used_cpu_user:2.15</br>
	 * used_cpu_sys_children:0.11</br>
	 * used_cpu_user_children:0.00</br>
	 * connected_clients:1             # 连接的客户端数量</br>
	 * connected_slaves:0              # 从属服务器的数量</br>
	 * client_longest_output_list:0</br>
	 * client_biggest_input_buf:0</br>
	 * blocked_clients:0</br>
	 * used_memory:557304              # Redis 分配的内存总量</br>
	 * used_memory_human:544.24K</br>
	 * used_memory_rss:17879040        # Redis 分配的内存总量(包括内存碎片)</br>
	 * used_memory_peak:565904</br>
	 * used_memory_peak_human:552.64K</br>
	 * mem_fragmentation_ratio:32.08   # 内存碎片比率</br>
	 * mem_allocator:jemalloc-2.2.5    # 目前使用的内存分配库</br>
	 * loading:0</br>
	 * aof_enabled:0</br>
	 * changes_since_last_save:2       # 上次保存数据库之后，执行命令的次数</br>
	 * bgsave_in_progress:0            # 后台进行中的 save 操作的数量</br>
	 * last_save_time:1324042687       # 最后一次成功保存的时间点，以 UNIX 时间戳格式显示</br>
	 * bgrewriteaof_in_progress:0      # 后台进行中的 aof 文件修改操作的数量</br>
	 * total_connections_received:16   # 运行以来连接过的客户端的总数量</br>
	 * total_commands_processed:87     # 运行以来执行过的命令的总数量</br>
	 * expired_keys:0                  # 运行以来过期的 key 的数量</br>
	 * evicted_keys:0</br>
	 * keyspace_hits:14                # 命中 key 的次数</br>
	 * keyspace_misses:14              # 不命中 key 的次数</br>
	 * pubsub_channels:0               # 当前使用中的频道数量</br>
	 * pubsub_patterns:0               # 当前使用的模式的数量</br>
	 * latest_fork_usec:314</br>
	 * vm_enabled:0                    # 是否开启了 vm</br>
	 * role:master</br>
	 * db0:keys=6,expires=0            # 各个数据库的 key 的数量，以及带有生存期的 key 的数量</br>
	 * db1:keys=6,expires=0</br>
	 * db2:keys=1,expires=0</br>
	 */
	@Override
	public Map<String,String> concreteCommand(RedisLarkContext redisLarkContext) {
		if(newInfo && MapUtils.isNotEmpty(redisLarkContext.getRedisInfo())){
			redisLarkContext.getRedisInfo().clear();
		}
		final Map<String,String> response = redisLarkContext.getInfo(this.isPrintLog());
		return response;
	}

	@Override
	public RedisVersion getSupportVersion() {
		return RedisVersion.REDIS_2_0;
	}
}
