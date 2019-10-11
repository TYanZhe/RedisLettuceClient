package cn.org.tpeach.nosql.redis.command.string;

import cn.org.tpeach.nosql.enums.RedisVersion;
import cn.org.tpeach.nosql.redis.command.JedisDbCommand;
import cn.org.tpeach.nosql.redis.command.RedisLarkContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author tyz
 * @Title: GetRangeString
 * @ProjectName RedisLark
 * @Description: TODO
 * @date 2019-06-26 21:57
 * @since 1.0.0
 */
public class GetRangeString extends JedisDbCommand<byte[]> {
    final static Logger logger = LoggerFactory.getLogger(GetRangeString.class);
    private byte[] key;
    private long startOffset;
    private long endOffset;
    /**
     * 命令：GETRANGE key start end
     * @param id
     * @param db
     * @param key
     * @param startOffset
     * @param endOffset
     */
    public GetRangeString(String id, int db, byte[] key,long startOffset,long endOffset) {
        super(id,db);
        this.key = key;
        this.startOffset = startOffset;
        this.endOffset = endOffset;
    }

    @Override
    public String sendCommand() {
        return "GETRANGE "+byteToStr(key) +" "+startOffset+" "+endOffset;
    }

    /**
     * 返回key中字符串值的子字符串，字符串的截取范围由start和end两个偏移量决定(包括start和end在内)。
     * 负数偏移量表示从字符串最后开始计数，-1表示最后一个字符，-2表示倒数第二个，以此类推。
     * GETRANGE通过保证子字符串的值域(range)不超过实际字符串的值域来处理超出范围的值域请求。
     * 时间复杂度：O(N)，N为要返回的字符串的长度。
     * 复杂度最终由返回值长度决定，但因为从已有字符串中建立子字符串的操作非常廉价(cheap)，
     * 所以对于长度不大的字符串，该操作的复杂度也可看作O(1)。
     * @param redisLarkContext
     * @return 截取得出的子字符串。
     */
    @Override
    public byte[] concreteCommand(RedisLarkContext<byte[], byte[]> redisLarkContext) {
        super.concreteCommand(redisLarkContext);
        final byte[] response = redisLarkContext.getrange(key,startOffset,endOffset);
        return response;
    }

    @Override
    public RedisVersion getSupportVersion() {
        // TODO  在<=2.0的版本里，GETRANGE被叫作SUBSTR。
        return RedisVersion.REDIS_2_0;
    }
}
