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
public class RenameNxCommand extends JedisDbCommand<Boolean> {
    private byte[] oldkey;
    private byte[] newkey;
    /**
     * 命令：RENAME key newkey
     * @param id
     */
    public RenameNxCommand(String id, int db, final byte[] oldkey, final byte[] newkey) {
        super(id,db);
        this.oldkey = oldkey;
        this.newkey = newkey;
    }
    @Override
    public String sendCommand() {
        return "RENAME "+byteToStr(oldkey) +" "+byteToStr(newkey);
    }
    /**
     * 当且仅当newkey不存在时，将key改为newkey。
     * 出错的情况和RENAME一样(key不存在时报错)。
     * 时间复杂度：O(1)
     * @param redisLarkContext
     * @return 修改成功时，返回1。如果newkey已经存在，返回0。
     */
    @Override
    public Boolean concreteCommand(RedisLarkContext<byte[], byte[]> redisLarkContext) {
        super.concreteCommand(redisLarkContext);
;        final Boolean response = redisLarkContext.renamenx(oldkey,newkey);
        return response;
    }

    @Override
    public RedisVersion getSupportVersion() {
        return RedisVersion.REDIS_1_0;
    }
}
