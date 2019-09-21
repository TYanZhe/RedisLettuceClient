package cn.org.tpeach.nosql.redis.command.key;

import cn.org.tpeach.nosql.enums.RedisVersion;
import cn.org.tpeach.nosql.redis.command.JedisDbCommand;
import cn.org.tpeach.nosql.redis.command.RedisLarkContext;

/**
 * @author tyz
 * @Title: TypeCommand
 * @ProjectName RedisLark
 * @Description: TODO
 * @date 2019-09-03 10:26
 * @since 1.0.0
 */
public class RenameCommand extends JedisDbCommand<String> {
    private String oldkey;
    private String newkey;
    /**
     * 命令：RENAME key newkey
     * @param id
     */
    public RenameCommand(String id, int db, final String oldkey, final String newkey) {
        super(id,db);
        this.oldkey = oldkey;
        this.newkey = newkey;
    }

    @Override
    public String sendCommand() {
        return "RENAME "+oldkey +" "+newkey;
    }

    /**
     * 将key改名为newkey。
     * 当key和newkey相同或者key不存在时，返回一个错误。
     * 当newkey已经存在时，RENAME命令将覆盖旧值。
     * 时间复杂度：O(1)
     * @param redisLarkContext
     * @return 改名成功时提示OK，失败时候返回一个错误。
     */
    @Override
    public String concreteCommand(RedisLarkContext redisLarkContext) {
        super.concreteCommand(redisLarkContext);
;        final String response = redisLarkContext.rename(oldkey,newkey);
        return response;
    }

    @Override
    public RedisVersion getSupportVersion() {
        return RedisVersion.REDIS_1_0;
    }
}
