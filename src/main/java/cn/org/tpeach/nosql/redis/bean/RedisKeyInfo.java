package cn.org.tpeach.nosql.redis.bean;

import cn.org.tpeach.nosql.bean.PageBean;
import cn.org.tpeach.nosql.enums.RedisType;
import io.lettuce.core.ScanCursor;
import io.lettuce.core.ScoredValue;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author tyz
 * @Title: RedisKeyInfo
 * @ProjectName RedisLark
 * @Description: TODO
 * @date 2019-07-07 8:50
 * @since 1.0.0
 */
@Data
public class RedisKeyInfo {
    private String id;
    private int db;
    private byte[] key;
    private byte[] value;
    private Long ttl;
    private RedisType type;
    private int size;
    private int index;
    private Double score;
    private byte[] field;
    private Long idleTime;
    private Map<byte[],byte[]> valueHash;
    private List<byte[]> valueList;
    private List<byte[]> valueSet;
    private List<ScoredValue<byte[]>> valueZSet;
    private PageBean pageBean;
    private ScanCursor cursor;

    public void clearBaseInfo(){
        this.ttl = null;
        this.type = null;
        this.idleTime = null;
        this.size = -1;
    }
    
    
}
