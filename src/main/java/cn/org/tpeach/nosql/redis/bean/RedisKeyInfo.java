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
    private String key;
    private Long ttl;
    private RedisType type;
    private int size;
    private String value;
    private int index;
    private Double score;
    private String field;
    private Long idleTime;
    private Map<String,String> valueHash;
    private List<String> valueList;
    private List<String> valueSet;
    private List<ScoredValue<String>> valueZSet;
    private PageBean pageBean;
    private ScanCursor cursor;
    
    
}
