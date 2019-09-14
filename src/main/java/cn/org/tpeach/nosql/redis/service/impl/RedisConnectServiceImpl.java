package cn.org.tpeach.nosql.redis.service.impl;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import cn.org.tpeach.nosql.enums.RedisStructure;
import cn.org.tpeach.nosql.enums.RedisVersion;
import cn.org.tpeach.nosql.redis.command.RedisLarkContext;
import cn.org.tpeach.nosql.redis.command.key.*;
import cn.org.tpeach.nosql.redis.command.server.RedisStructureCommand;
import io.lettuce.core.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.org.tpeach.nosql.annotation.Component;
import cn.org.tpeach.nosql.bean.PageBean;
import cn.org.tpeach.nosql.enums.RedisType;
import cn.org.tpeach.nosql.exception.ServiceException;
import cn.org.tpeach.nosql.redis.bean.RedisConnectInfo;
import cn.org.tpeach.nosql.redis.bean.RedisKeyInfo;
import cn.org.tpeach.nosql.redis.command.connection.PingCommand;
import cn.org.tpeach.nosql.redis.command.connection.SelectCommand;
import cn.org.tpeach.nosql.redis.command.hash.HdelHash;
import cn.org.tpeach.nosql.redis.command.hash.HlenHash;
import cn.org.tpeach.nosql.redis.command.hash.HscanHash;
import cn.org.tpeach.nosql.redis.command.hash.HsetHash;
import cn.org.tpeach.nosql.redis.command.hash.HsetnxHash;
import cn.org.tpeach.nosql.redis.command.list.LdelRowList;
import cn.org.tpeach.nosql.redis.command.list.LlenList;
import cn.org.tpeach.nosql.redis.command.list.LpushList;
import cn.org.tpeach.nosql.redis.command.list.LrangeList;
import cn.org.tpeach.nosql.redis.command.list.LsetList;
import cn.org.tpeach.nosql.redis.command.list.RpushList;
import cn.org.tpeach.nosql.redis.command.server.DbSizeCommand;
import cn.org.tpeach.nosql.redis.command.server.FlushDbCommand;
import cn.org.tpeach.nosql.redis.command.set.SAddSet;
import cn.org.tpeach.nosql.redis.command.set.ScardSet;
import cn.org.tpeach.nosql.redis.command.set.SremSet;
import cn.org.tpeach.nosql.redis.command.set.SscanSet;
import cn.org.tpeach.nosql.redis.command.string.GetString;
import cn.org.tpeach.nosql.redis.command.string.SetString;
import cn.org.tpeach.nosql.redis.command.string.SetexString;
import cn.org.tpeach.nosql.redis.command.zset.ZAddSet;
import cn.org.tpeach.nosql.redis.command.zset.ZcardSet;
import cn.org.tpeach.nosql.redis.command.zset.ZremSet;
import cn.org.tpeach.nosql.redis.command.zset.ZscanSet;
import cn.org.tpeach.nosql.redis.connection.RedisLarkPool;
import cn.org.tpeach.nosql.redis.service.BaseRedisService;
import cn.org.tpeach.nosql.redis.service.IRedisConnectService;
import cn.org.tpeach.nosql.tools.CollectionUtils;
import cn.org.tpeach.nosql.tools.StringUtils;


/**
 * @author tyz
 * @Title: RedisConnectService
 * @ProjectName RedisLark
 * @Description: TODO
 * @date 2019-06-27 08:47
 * @since 1.0.0
 */
@Component("redisConnectService")
public class RedisConnectServiceImpl extends BaseRedisService implements IRedisConnectService {

    private static final Logger logger = LoggerFactory.getLogger(RedisConnectServiceImpl.class);

	@Override
	public boolean connectTest(RedisConnectInfo connectInfo) {
		String uuid = StringUtils.getUUID();
		try {
			connectInfo.setId(uuid);
			RedisLarkPool.addOrUpdateConnectInfo(connectInfo);
			String ping = super.executeJedisCommand(new PingCommand(uuid));
	        if(!"PONG".equals(ping)){
	            throw new ServiceException("Ping命令执行失败");
	        }
	        return true;
		}finally {
			RedisLarkPool.destory(uuid);
			RedisLarkPool.deleteConnectInfo(uuid);
		}
	}

    @Override
    public String readString(String id, int db, String key) {
        return super.executeJedisCommand(new GetString(id, db, key));
    }

//    @Override
//    @Deprecated
//    public Integer getDbAmount(String id) {
//        return super.executeJedisCommand(new GetDbAmount(id));
//    }

    @Override
    public String[] getDbAmountAndSize(String id) {
        final RedisStructure redisStructure = super.executeJedisCommand(new RedisStructureCommand(id));
        String ping = super.executeJedisCommand(new PingCommand(id));
        if(!"PONG".equals(ping)){
            throw new ServiceException("Ping命令执行失败");
        }



//        SelectCommand selectCommand = new SelectCommand(id, 0);
//        int dbAmount = 100;
//        for (int i = 0; i < dbAmount; i++) {
//            try {
//                selectCommand.setDb(i);
//                executeJedisCommand(selectCommand);
//            } catch (Exception e) {
//                dbAmount = i;
//                break;
//            }
//        }
        int dbAmount = 1;
        if(RedisStructure.SINGLE.equals(redisStructure)){
            dbAmount = 16;
        }
        final String[] s = new String[dbAmount];
        IntStream.range(0, dbAmount).forEach(dbIndex -> s[dbIndex] = "db" + dbIndex + "("
                + executeJedisCommand(new DbSizeCommand(id, dbIndex)) + ")");
        return s;

    }

    /* (non-Javadoc)
	 * @see cn.org.tpeach.nosql.redis.service.IRedisConnectService#getDbKeySize(java.lang.String, int)
     */
    @Override
    public Long getDbKeySize(String id, int db) {
        return super.executeJedisCommand(new DbSizeCommand(id, db));
    }

    @Override
    public Collection<String> getKeys(String id, int db) {
        final Collection<String> keys = this.getKeys(id, db, "*");
        return keys;
    }

    @Override
    public Collection<String> getKeys(String id, int db, String pattern) {

        final RedisStructure redisStructure = super.executeJedisCommand(new RedisStructureCommand(id));
        if(RedisStructure.SINGLE.equals(redisStructure)){
            ScanCommand command = new ScanCommand(id, db, ScanCursor.INITIAL, 10000);
            if(StringUtils.isNotBlank(pattern)) {
            	command.match(pattern);
            }
            KeyScanCursor<String> scanResult = command.execute();
            return scanResult.getKeys();
        }else if(RedisStructure.CLUSTER.equals(redisStructure)){
            final ScanIterator<String> scan = super.executeJedisCommand(new ScanIteratorCommand(id, 10000, pattern));
            final List<String> collect = scan.stream().collect(Collectors.toList());
            Collections.sort(collect);
            return collect;
        }
        return null;
//        return super.executeJedisCommand(new KeysCommand(id, db, pattern));
    }

    @Override
    public Long deleteKeys(String id, int db, String... keys) {
        return super.executeJedisCommand(new DelKeysCommand(id, db, keys));
    }

    @Override
    public Boolean expireKey(String id, int db, String key, int seconds) {
        if (seconds < 0) {
            return super.executeJedisCommand(new PersistCommand(id, db, key));
        }
        return super.executeJedisCommand(new ExpireCommand(id, db, key, seconds));
    }

    @Override
    public RedisKeyInfo addSingleKeyInfo(RedisKeyInfo keyInfo) {
        if (keyInfo == null) {
            return null;
        }
        //TODO 校验key是否存在

        switch (keyInfo.getType()) {
            case STRING:
                if (keyInfo.getTtl() == -1) {
                    super.executeJedisCommand(new SetString(keyInfo.getId(), keyInfo.getDb(), keyInfo.getKey(), keyInfo.getValue()));
                } else {
                    super.executeJedisCommand(new SetexString(keyInfo.getId(), keyInfo.getDb(), keyInfo.getKey(), keyInfo.getTtl().intValue(), keyInfo.getValue()));
                }
                break;
            case LIST:
                super.executeJedisCommand(new RpushList(keyInfo.getId(), keyInfo.getDb(), keyInfo.getKey(), keyInfo.getValue()));
                break;
            case SET:
                super.executeJedisCommand(new SAddSet(keyInfo.getId(), keyInfo.getDb(), keyInfo.getKey(), keyInfo.getValue()));
                break;
            case HASH:
                super.executeJedisCommand(new HsetHash(keyInfo.getId(), keyInfo.getDb(), keyInfo.getKey(), keyInfo.getField(), keyInfo.getValue()));
                break;
            case ZSET:
                super.executeJedisCommand(new ZAddSet(keyInfo.getId(), keyInfo.getDb(), keyInfo.getKey(), keyInfo.getScore(), keyInfo.getValue()));
                break;
            default:
                logger.error("未知的类型：" + keyInfo);
                break;
        }
        if (keyInfo.getTtl() != -1 && keyInfo.getType() != RedisType.STRING) {
            super.executeJedisCommand(new ExpireCommand(keyInfo.getId(), keyInfo.getDb(), keyInfo.getKey(), keyInfo.getTtl().intValue()));
        }
        return keyInfo;

    }

    /**
     *
     * @param id
     * @param db
     * @param key
     * @param pageBean 设置page 当前页 rows每页显示数量
     * @return
     */
    @Override
    public RedisKeyInfo getRedisKeyInfo(String id, int db, String key,ScanCursor cursor, PageBean pageBean) {
        if (pageBean == null) {
            pageBean = new PageBean();
        }
        //获取类型
        String typeStr = super.executeJedisCommand(new TypeCommand(id, db, key));
        RedisType type = RedisType.getRedisType(typeStr);
        if (type == RedisType.UNKNOWN) {
            throw new ServiceException("key不存在");
        }
        //获取ttl
        Long ttl = super.executeJedisCommand(new TTLCommand(id, db, key));

        RedisKeyInfo redisKeyInfo = new RedisKeyInfo();
        redisKeyInfo.setId(id);
        redisKeyInfo.setType(type);
        redisKeyInfo.setTtl(ttl);
        redisKeyInfo.setKey(key);
        redisKeyInfo.setDb(db);
        redisKeyInfo.setCursor(ScanCursor.INITIAL);
        //获取值
        int size = 0;
        switch (type) {
            case STRING:
                redisKeyInfo.setValue(super.executeJedisCommand(new GetString(id, db, key)));
                if (StringUtils.isNotBlank(redisKeyInfo.getValue())) {
                    size = 1;
                }
                break;
            case LIST:
                size = super.executeJedisCommand(new LlenList(id, db, key)).intValue();
                pageBean.setTotal(size);
                List<String> list = super.executeJedisCommand(new LrangeList(id, db, key, pageBean.getStartIndex(), pageBean.getEndIndex() - 1));
                redisKeyInfo.setValueList(list);
                break;
            case SET:
                size = super.executeJedisCommand(new ScardSet(id, db, key)).intValue();
                pageBean.setTotal(size);
//                Set<String> set = super.executeJedisCommand(new SmembersSet(id, db, key));
//                ScanResult<String> sscanResult = super.executeJedisCommand(new SscanSet(id, db, key, pageBean.getStartIndex() + "", pageBean.getRows()));
                ValueScanCursor<String> sscanResult;
                if(pageBean.isFirstPage()) {
                	sscanResult = super.executeJedisCommand(new SscanSet(id, db, key, ScanCursor.INITIAL, pageBean.getRows()));
                }else {
                	sscanResult = super.executeJedisCommand(new SscanSet(id, db, key,cursor, pageBean.getRows()));
                }
               
                redisKeyInfo.setValueSet(sscanResult.getValues());
                redisKeyInfo.setCursor(new ScanCursor(sscanResult.getCursor(),sscanResult.isFinished()));
                break;
            case HASH:
                //hscan field数量 >= 512，开始分页
                size = super.executeJedisCommand(new HlenHash(id, db, key)).intValue();
                pageBean.setTotal(size);
                Map<String, String> map = new LinkedHashMap<>();
//                ScanResult<Map.Entry<String, String>> hscanResult = super.executeJedisCommand(new HscanHash(id, db, key, pageBean.getStartIndex() + "", pageBean.getRows()));
//                String cursor = hscanResult.getCursor();// 返回0 说明遍历完成
//                List<Map.Entry<String, String>> scanResult = hscanResult.getResult();
//                if(CollectionUtils.isNotEmpty(scanResult)){
//                    for (Map.Entry<String, String> entry : scanResult) {
//                        map.put(entry.getKey(),entry.getValue());
//                    }
//                }
                
                MapScanCursor<String, String> hscanResult;
                if(pageBean.isFirstPage()) {
                	hscanResult =  super.executeJedisCommand(new HscanHash(id, db, key, ScanCursor.INITIAL , pageBean.getRows()));
                }else {
                	hscanResult = super.executeJedisCommand(new HscanHash(id, db, key, cursor , pageBean.getRows()));
                }
                redisKeyInfo.setValueHash(hscanResult.getMap());
                redisKeyInfo.setCursor(new ScanCursor(hscanResult.getCursor(),hscanResult.isFinished()));
                break;
            case ZSET:
                size = super.executeJedisCommand(new ZcardSet(id, db, key)).intValue();
                pageBean.setTotal(size);
//                Set<Tuple> zset = super.executeJedisCommand(new ZrangeWithScoresSet(id, db, key, pageBean.getStartIndex(), pageBean.getEndIndex() - 1));
//                ScanResult<Tuple> zscanResult = super.executeJedisCommand(new ZscanSet(id, db, key, pageBean.getStartIndex() + "", pageBean.getRows()));
//                List<Tuple> result2 = zscanResult.getResult();
//                Set<Tuple> zset = new LinkedHashSet<>();
//                if(CollectionUtils.isNotEmpty(result2)){
//                	zset.addAll(result2);
//                }
                ScoredValueScanCursor<String> zscanResult;
                if(pageBean.isFirstPage()) {
                	zscanResult =  super.executeJedisCommand(new ZscanSet(id, db, key, ScanCursor.INITIAL, pageBean.getRows()));
                }else {
                	zscanResult = super.executeJedisCommand(new ZscanSet(id, db, key, cursor, pageBean.getRows()));
                }

                redisKeyInfo.setCursor(new ScanCursor(zscanResult.getCursor(),zscanResult.isFinished()));
                redisKeyInfo.setValueZSet(zscanResult.getValues());
                break;
            default:
                break;
        }
        redisKeyInfo.setSize(size);
        pageBean.setTotal(size);
        Long idleTime = super.executeJedisCommand(new ObjectIdletime(id, db, key));
        redisKeyInfo.setIdleTime(idleTime);
        redisKeyInfo.setPageBean(pageBean);
        return redisKeyInfo;
    }

    /* (non-Javadoc)
	 * @see cn.org.tpeach.nosql.redis.service.IRedisConnectService#flushDb(java.lang.String, int)
     */
    @Override
    public String flushDb(String id, int db) {
        return super.executeJedisCommand(new FlushDbCommand(id, db));
    }

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.org.tpeach.nosql.redis.service.IRedisConnectService#updateKeyInfo(cn.org.
	 * tpeach.nosql.redis.bean.RedisKeyInfo,
	 * cn.org.tpeach.nosql.redis.bean.RedisKeyInfo)
	 */
	@Override
	public RedisKeyInfo updateKeyInfo(RedisKeyInfo newKeyInfo, RedisKeyInfo oldKeyInfo) {
		String id = newKeyInfo.getId();
		int db = newKeyInfo.getDb();
		String key =  newKeyInfo.getKey();
		System.out.println("newKeyInfo>>>" + newKeyInfo);
		System.out.println("oldKeyInfo>>>" + oldKeyInfo);
		switch (oldKeyInfo.getType()) {
			case STRING:
				super.executeJedisCommand(new SetString(id, db, key, newKeyInfo.getValue()));
				break;
			case LIST:
	             //查看下标的值是否相等
                List<String> list = super.executeJedisCommand(new LrangeList(id, db, key, newKeyInfo.getIndex(), newKeyInfo.getIndex()));
                if(CollectionUtils.isEmpty(list)){
                    throw new ServiceException("添加失败，数据已更新");
                }
                String oldValue = list.get(0);
                if(!oldKeyInfo.getValue().equals(oldValue)){
                    throw new ServiceException("the row has been changed and can't be update now.Reload and try again");
                }
				super.executeJedisCommand(new LsetList(id, db, key, newKeyInfo.getIndex(), newKeyInfo.getValue()));
				break;
			case SET:
				super.executeJedisCommand(new SremSet(id, db, key, oldKeyInfo.getValue()));
				super.executeJedisCommand(new SAddSet(id, db, key, newKeyInfo.getValue()));
				break;
			case HASH:
				super.executeJedisCommand(new HsetHash(id, db, key, newKeyInfo.getField(), newKeyInfo.getValue()));
				break;
			case ZSET:
				super.executeJedisCommand(new ZremSet(id, db, key, oldKeyInfo.getValue()));
				super.executeJedisCommand(new ZAddSet(id, db, key, newKeyInfo.getScore(), newKeyInfo.getValue()));
				break;
			default:
				throw new ServiceException("未知的类型：" + oldKeyInfo.getType());

		}
		return oldKeyInfo;

	}

    @Override
    public RedisKeyInfo addRowKeyInfo(RedisKeyInfo keyInfo, boolean isLeftList) {

        switch (keyInfo.getType()) {
            case STRING:
                break;
            case LIST:
                if (isLeftList) {
                    super.executeJedisCommand(new LpushList(keyInfo.getId(), keyInfo.getDb(), keyInfo.getKey(), keyInfo.getValue()));
                } else {
                    super.executeJedisCommand(new RpushList(keyInfo.getId(), keyInfo.getDb(), keyInfo.getKey(), keyInfo.getValue()));
                }

                break;
            case SET:
                super.executeJedisCommand(new SAddSet(keyInfo.getId(), keyInfo.getDb(), keyInfo.getKey(), keyInfo.getValue()));
                break;
            case HASH:
                Boolean res = super.executeJedisCommand(new HsetnxHash(keyInfo.getId(), keyInfo.getDb(), keyInfo.getKey(), keyInfo.getField(), keyInfo.getValue()));
                if (!res) {
                    throw new ServiceException("Value with the same key already exist");
                }
                break;
            case ZSET:
                super.executeJedisCommand(new ZAddSet(keyInfo.getId(), keyInfo.getDb(), keyInfo.getKey(), keyInfo.getScore(), keyInfo.getValue()));
                break;
            default:
                logger.error("未知的类型：" + keyInfo);
                throw new ServiceException("未知的类型：" + keyInfo);

        }
        return keyInfo;
    }

    @Override
    public Long deleteRowKeyInfo(String id,int db,String key,String valueOrField,int index,RedisType type) {
        Long count = 1L;
        switch (type) {
            case STRING:
                break;
            case LIST:
                //查看下标的值是否相等
                List<String> list = super.executeJedisCommand(new LrangeList(id, db, key, index, index));
                if(CollectionUtils.isEmpty(list)){
                    throw new ServiceException("删除失败，数据已更新");
                }
                String oldValue = list.get(0);
                if(!valueOrField.equals(oldValue)){
                    throw new ServiceException("the row has been changed and can't be deleted row.Reload and try again");
                }
                super.executeJedisCommand(new LdelRowList(id, db, key, index));
                break;
            case SET:
                super.executeJedisCommand(new SremSet(id, db, key,valueOrField));
                break;
            case HASH:
                 count = super.executeJedisCommand(new HdelHash(id, db, key,valueOrField));
                break;
            case ZSET:
                super.executeJedisCommand(new ZremSet(id, db, key,valueOrField));
                break;
            default:
                logger.error("未知的类型：" + type);
                throw new ServiceException("未知的类型：" + type);

        }
        return count;
    }

    @Override
    public Boolean remamenx(String id, int db, String oldkey, String newkey) {
        return  super.executeJedisCommand(new RenameNxCommand(id,db,oldkey,newkey));
    }


}
