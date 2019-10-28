package cn.org.tpeach.nosql.redis.service.impl;

import cn.org.tpeach.nosql.annotation.Component;
import cn.org.tpeach.nosql.bean.PageBean;
import cn.org.tpeach.nosql.enums.RedisStructure;
import cn.org.tpeach.nosql.enums.RedisType;
import cn.org.tpeach.nosql.exception.ServiceException;
import cn.org.tpeach.nosql.framework.BeanContext;
import cn.org.tpeach.nosql.redis.bean.RedisClientBo;
import cn.org.tpeach.nosql.redis.bean.RedisConnectInfo;
import cn.org.tpeach.nosql.redis.bean.RedisKeyInfo;
import cn.org.tpeach.nosql.redis.bean.SlowLogBo;
import cn.org.tpeach.nosql.redis.command.RedisLarkContext;
import cn.org.tpeach.nosql.redis.command.connection.PingCommand;
import cn.org.tpeach.nosql.redis.command.connection.SelectCommand;
import cn.org.tpeach.nosql.redis.command.hash.*;
import cn.org.tpeach.nosql.redis.command.key.*;
import cn.org.tpeach.nosql.redis.command.list.*;
import cn.org.tpeach.nosql.redis.command.server.*;
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
import cn.org.tpeach.nosql.redis.service.IRedisConfigService;
import cn.org.tpeach.nosql.redis.service.IRedisConnectService;
import cn.org.tpeach.nosql.tools.*;
import io.lettuce.core.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.IntStream;


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
    private IRedisConfigService redisConfigService = BeanContext.getBean("redisConfigService",IRedisConfigService.class);
    private final int maxCount = 10000;
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
    public byte[] readString(String id, int db, byte[] key) {
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



        SelectCommand selectCommand = new SelectCommand(id, 0);
        RedisConnectInfo connectInfo = redisConfigService.getRedisConfigById(id);
        int dbAmount = connectInfo.getDbAmount();
        for (int i = 0; i < dbAmount; i++) {
            try {
                selectCommand.setDb(i);
                executeJedisCommand(selectCommand);
            } catch (Exception e) {
                dbAmount = i;
                break;
            }
        }
//        int dbAmount = 1;
//        if(RedisStructure.SINGLE.equals(redisStructure)){
//            dbAmount = 16;
//        }
        final String[] s = new String[dbAmount];
        IntStream.range(0, dbAmount).forEach(dbIndex -> s[dbIndex] = "db" + dbIndex + "("
                + executeJedisCommand(new DbSizeCommand(id, dbIndex)) + ")");
        return s;

    }

    /* (non-Javadoc)
     * @see cn.org.tpeach.nosql.redis.service.IRedisConnectService#getDbKeySize(java.lang.String, int)
     */
    @Override
    public Long getDbKeySize(String id, int db,boolean printLog) {
        DbSizeCommand dbSizeCommand = new DbSizeCommand(id, db);
        dbSizeCommand.setPrintLog(printLog);
        return super.executeJedisCommand(new DbSizeCommand(id, db));
    }
    @Override
    public Long getDbKeySize(String id, int db ) {
        return getDbKeySize(id,db,true);
    }
    @Override
    public KeyScanCursor getKeys(String id, int db,boolean totalPattren) {
        return this.getKeys(id, db, "*",false );
    }

    @Override
    public KeyScanCursor getKeys(String id, int db, String pattern,boolean totalPattren) {
        int totalPattrenCount = 0;
        final KeyScanCursor keyScanCursor = new KeyScanCursor();
        List<byte[]> collection = null;
        final String allPattern = "*";
        int count ;
        int index = maxCount;
        if(StringUtils.isNotBlank(pattern) && !allPattern.equals(pattern.trim())) {
            pattern = pattern.trim();
            final DbSizeCommand dbSizeCommand = new DbSizeCommand(id, db);
            dbSizeCommand.setPrintLog(false);
            final Long aLong = dbSizeCommand.execute();
            if(aLong != null){
                count = aLong.intValue();
            }else{
                count = maxCount;
            }
        }else{
            pattern = allPattern;
            count = maxCount;
        }
        final RedisStructure redisStructure = super.executeJedisCommand(new RedisStructureCommand(id));
        if(RedisStructure.SINGLE.equals(redisStructure)){
            ScanCursor cursor = ScanCursor.INITIAL;
            ScanCommand command = new ScanCommand(id, db, cursor, maxCount);
            command.match(pattern);
            KeyScanCursor<byte[]> scanResult = command.execute();
            collection = scanResult.getKeys();
            if(CollectionUtils.isNotEmpty(collection)){
                totalPattrenCount+= collection.size();
            }
            while (!scanResult.isFinished() && totalPattren){
                cursor = ScanCursor.of(scanResult.getCursor());
                command = new ScanCommand(id, db, cursor, maxCount);
                command.match(pattern);
                scanResult = command.execute();
                if(count > index && (collection == null || collection.size() < maxCount)){
                    if(CollectionUtils.isNotEmpty(scanResult.getKeys())){
                        if(collection.size() + scanResult.getKeys().size() > maxCount){
                            collection.addAll(scanResult.getKeys().subList(0,maxCount - collection.size()));
                        }else{
                            collection.addAll(scanResult.getKeys());
                        }

                    }
                }
                if(CollectionUtils.isNotEmpty(scanResult.getKeys())){
                    totalPattrenCount+= scanResult.getKeys().size();
                }
            }
        }else if(RedisStructure.CLUSTER.equals(redisStructure)){
//            #!/bin/sh
//            redis-cli -c -p PORT -h IP cluster nodes | awk '{if($3=="master" || $3=="myself,master") print $2}' | awk -v var_pattern="$1" -F[:@] '{system("redis-cli -c -p "$2" -h "$1" keys "var_pattern)}'
            final ScanIteratorCommand command = new ScanIteratorCommand(id, maxCount, pattern);
            final ScanIterator<byte[]> scan = super.executeJedisCommand(command);
            collection = new ArrayList<>(maxCount);
            for (int i = 0; scan.hasNext(); i++,totalPattrenCount++) {
                byte[] next = scan.next();
                if(i < maxCount){
                    collection.add(next);
                }
            }

        }

        if(collection != null){
            Collections.sort(collection,(o1,o2)->{
                String s1 = StringUtils.byteToStr(o1);
                String s2 = StringUtils.byteToStr(o2);
                if(s1 == null){
                    return -1;
                }else if(s2 == null){
                    return 1;
                }
                return s1.toUpperCase().compareTo(s2.toUpperCase());
            });
            keyScanCursor.getKeys().addAll(collection);
        }
        keyScanCursor.setCursor(totalPattrenCount+"");
        return keyScanCursor;
//        return super.executeJedisCommand(new KeysCommand(id, db, pattern));
    }

    @Override
    public KeyScanCursor<byte[]> getKeysForCursor(String id, int db, String pattern, String cursor) {
        ScanCursor scanCursor;
        final String allPattern = "*";
        if (StringUtils.isBlank(cursor)) {
            scanCursor = ScanCursor.INITIAL;
        } else {
            scanCursor = ScanCursor.of(cursor);
        }
        if (StringUtils.isNotBlank(pattern) && !allPattern.equals(pattern.trim())) {
            pattern = pattern.trim();
        } else {
            pattern = allPattern;
        }
        ScanCommand scanCommand = new ScanCommand(id, db, scanCursor, maxCount);
        scanCommand.match(pattern);
        return super.executeJedisCommand(scanCommand);
    }
    @Override
    public Long deleteKeys(String id, int db, byte[]... keys) {
        return super.executeJedisCommand(new DelKeysCommand(id, db, keys));
    }

    @Override
    public Long deleteKeys(String id, int db, String pattern,Integer totalCount) {
        Long number = 0L;
        //解决数量太大 loading卡顿问题
        int maxCount = 2000;
        if(StringUtils.isBlank(pattern)){
            return number;
        }
        final RedisStructure redisStructure = super.executeJedisCommand(new RedisStructureCommand(id));
        byte[][] keys;
        List<byte[]> resultKeys;
        if(RedisStructure.SINGLE.equals(redisStructure)) {
            ScanCursor scanCursor = ScanCursor.INITIAL;
            ScanCommand scanCommand = new ScanCommand(id, db, scanCursor, maxCount);
            scanCommand.match(pattern);
            KeyScanCursor<byte[]> keyScanCursor = scanCommand.execute();
            resultKeys = keyScanCursor.getKeys();

            if (CollectionUtils.isNotEmpty(resultKeys)) {
                keys = new byte[resultKeys.size()][];
                resultKeys.toArray(keys);
                resultKeys.clear();
                number += this.deleteKeys(id, db, keys);
            }
            while (!keyScanCursor.isFinished()) {
                scanCursor = ScanCursor.of(keyScanCursor.getCursor());
                scanCommand = new ScanCommand(id, db, scanCursor, maxCount);
                scanCommand.match(pattern);
                keyScanCursor = scanCommand.execute();
                resultKeys = keyScanCursor.getKeys();
                if (CollectionUtils.isNotEmpty(resultKeys)) {
                    keys = new byte[resultKeys.size()][];
                    resultKeys.toArray(keys);
                    resultKeys.clear();
                    number += this.deleteKeys(id, db, keys);
                }
            }
            //优化删除不完全
            if (totalCount != null && number < totalCount) {
                final DbSizeCommand dbSizeCommand = new DbSizeCommand(id, db);
                dbSizeCommand.setPrintLog(false);
                final Long aLong = dbSizeCommand.execute();
                if (aLong != null) {
//                    logger.info("尝试删除未扫描到keys:{},当前库key总数:{}", totalCount - number, aLong);
                    int count = aLong.intValue();
                    while (count > 0) {
                        count = count - maxCount;
                        scanCommand = new ScanCommand(id, db, ScanCursor.INITIAL, maxCount);
                        scanCommand.match(pattern);
                        keyScanCursor = scanCommand.execute();
                        resultKeys = keyScanCursor.getKeys();
                        if (CollectionUtils.isNotEmpty(resultKeys)) {
                            keys = new byte[resultKeys.size()][];
                            resultKeys.toArray(keys);
                            resultKeys.clear();
                            number += this.deleteKeys(id, db, keys);
                        }
                    }
                }
            }
        }else{
            final ScanIteratorCommand command = new ScanIteratorCommand(id, maxCount, pattern);
            final ScanIterator<byte[]> scan = super.executeJedisCommand(command);
            resultKeys = new ArrayList<>(maxCount);
            while(scan.hasNext()) {
                resultKeys.add(scan.next());
                if(resultKeys.size() >= maxCount){
                    keys = new byte[resultKeys.size()][];
                    resultKeys.toArray(keys);
                    resultKeys.clear();
                    number += this.deleteKeys(id, db, keys);
                }
            }
            if(CollectionUtils.isNotEmpty(resultKeys)){
                keys = new byte[resultKeys.size()][];
                resultKeys.toArray(keys);
                resultKeys.clear();
                number += this.deleteKeys(id, db, keys);
            }
        }
        return number;
    }

    @Override
    public Boolean expireKey(String id, int db, byte[] key, int seconds) {
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
    public RedisKeyInfo getRedisKeyInfo(String id, int db, byte[] key,ScanCursor cursor,String pattern, PageBean pageBean) {
        if (pageBean == null) {
            pageBean = new PageBean();
        }
        if(StringUtils.isNotBlank(pattern)){
            pattern = "*"+pattern.trim()+"*";
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
        int size = getSizeAndQueryKeyInfo(id, db, key, cursor, pattern, pageBean, type, redisKeyInfo);
        redisKeyInfo.setSize(size);
        pageBean.setTotal(size);
        Long idleTime = super.executeJedisCommand(new ObjectIdletime(id, db, key));
        redisKeyInfo.setIdleTime(idleTime);
        redisKeyInfo.setPageBean(pageBean);
        return redisKeyInfo;
    }

    private int getSizeAndQueryKeyInfo(String id, int db, byte[] key, ScanCursor cursor, String pattern, PageBean pageBean, RedisType type, RedisKeyInfo redisKeyInfo) {
        //获取值
        int size = 0;
        switch (type) {
            case STRING:
                redisKeyInfo.setValue(super.executeJedisCommand(new GetString(id, db, key)));
                size = 1;
                break;
            case LIST:
                size = super.executeJedisCommand(new LlenList(id, db, key)).intValue();
                pageBean.setTotal(size);
                List<byte[]> list = super.executeJedisCommand(new LrangeList(id, db, key, pageBean.getStartIndex(), pageBean.getEndIndex() - 1));
                redisKeyInfo.setValueList(list);
                break;
            case SET:
                size = super.executeJedisCommand(new ScardSet(id, db, key)).intValue();
                pageBean.setTotal(size);
//                Set<String> set = super.executeJedisCommand(new SmembersSet(id, db, key));
//                ScanResult<String> sscanResult = super.executeJedisCommand(new SscanSet(id, db, key, pageBean.getStartIndex() + "", pageBean.getRows()));

                SscanSet sscanSetCommand;
                if(pageBean.isFirstPage()) {
                    sscanSetCommand = new SscanSet(id, db, key, ScanCursor.INITIAL, pageBean.getRows());
                }else {
                    sscanSetCommand =  new SscanSet(id, db, key,cursor, pageBean.getRows());
                }
                if(StringUtils.isNotBlank(pattern)){
                    sscanSetCommand.match(pattern);
                    sscanSetCommand.count(size);
                }
                ValueScanCursor<byte[]> sscanResult = super.executeJedisCommand(sscanSetCommand);
                List<byte[]> values = sscanResult.getValues();
//                if(CollectionUtils.isEmpty(values)  && StringUtils.isNotBlank(pattern)){
//                    sscanSetCommand.count(size);
//                    sscanResult = super.executeJedisCommand(sscanSetCommand);
//                    values = sscanResult.getValues();
//                }

                if(values.size() > pageBean.getRows()){
                    for(int i = values.size() -1;i>=pageBean.getRows();i--){
                        values.remove(i);
                    }
                }
                redisKeyInfo.setValueSet(values);
                Collections.sort(values,(o1, o2)->StringUtils.compareToLength(StringUtils.byteToStr(o1), StringUtils.byteToStr(o2)));
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


                HscanHash hscanHashCommand;
                if(pageBean.isFirstPage()) {
                    hscanHashCommand = new HscanHash(id, db, key, ScanCursor.INITIAL , pageBean.getRows());
                }else {
                    hscanHashCommand = new HscanHash(id, db, key, cursor , pageBean.getRows());
                }
                if(StringUtils.isNotBlank(pattern)){
                    hscanHashCommand.match(pattern);
                    hscanHashCommand.count(size);
                }
                MapScanCursor<byte[], byte[]> hscanResult = super.executeJedisCommand(hscanHashCommand);
                Map<byte[], byte[]> resultMap = hscanResult.getMap();
//                if(MapUtils.isEmpty(resultMap) && StringUtils.isNotBlank(pattern)) {
//                    hscanHashCommand.count(size);
//                    hscanResult = super.executeJedisCommand(hscanHashCommand);
//                    resultMap = hscanResult.getMap();
//                }
                if(resultMap.size() > pageBean.getRows()){
                    Set<byte[]> keySet = resultMap.keySet();
                    Iterator<byte[]> iterator = keySet.iterator();
                    int index = 0;
                    while (iterator.hasNext()){
                        iterator.next();
                        index ++;
                        if(index > pageBean.getRows()){
                            iterator.remove();
                        }
                    }
                }
                redisKeyInfo.setValueHash(resultMap);
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
                ZscanSet zscanSetCommand;
                if(pageBean.isFirstPage()) {
                    zscanSetCommand =  new ZscanSet(id, db, key, ScanCursor.INITIAL, pageBean.getRows());
                }else {
                    zscanSetCommand = new ZscanSet(id, db, key, cursor, pageBean.getRows());
                }
                if(StringUtils.isNotBlank(pattern)){
                    zscanSetCommand.match(pattern);
                    zscanSetCommand.count(size);
                }
                ScoredValueScanCursor<byte[]> zscanResult = super.executeJedisCommand(zscanSetCommand);
                List<ScoredValue<byte[]>> zscanResultValues = zscanResult.getValues();
//                if(CollectionUtils.isEmpty(zscanResultValues)  && StringUtils.isNotBlank(pattern)){
//                    zscanSetCommand.count(size);
//                    zscanResult = super.executeJedisCommand(zscanSetCommand);
//                    zscanResultValues = zscanResult.getValues();
//                }

                if(zscanResultValues.size() > pageBean.getRows()){
                    for(int i = zscanResultValues.size() -1;i>=pageBean.getRows();i--){
                        zscanResultValues.remove(i);
                    }
                }
                if(CollectionUtils.isNotEmpty(zscanResultValues)){
                    Collections.sort(zscanResultValues,(o1,o2)->StringUtils.compareToLength(StringUtils.byteToStr(o1.getValue()), StringUtils.byteToStr(o2.getValue())));
                }
                redisKeyInfo.setCursor(new ScanCursor(zscanResult.getCursor(),zscanResult.isFinished()));
                redisKeyInfo.setValueZSet(zscanResultValues);
                break;
            default:
                break;
        }
        return size;
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
        byte[] key =  newKeyInfo.getKey();
        switch (oldKeyInfo.getType()) {
            case STRING:
                super.executeJedisCommand(new SetString(id, db, key, newKeyInfo.getValue()));
                break;
            case LIST:
                //查看下标的值是否相等
                List<byte[]> list = super.executeJedisCommand(new LrangeList(id, db, key, newKeyInfo.getIndex(), newKeyInfo.getIndex()));
                if(CollectionUtils.isEmpty(list)){
                    throw new ServiceException("添加失败，数据已更新");
                }
                byte[] oldValue = list.get(0);
                if(!Arrays.equals(oldKeyInfo.getValue(), oldValue)) {
                    throw new ServiceException("the row has been changed and can't be update now.Reload and try again");
                }
                super.executeJedisCommand(new LsetList(id, db, key, newKeyInfo.getIndex(), newKeyInfo.getValue()));
                break;
            case SET:
                super.executeJedisCommand(new SremSet(id, db, key, oldKeyInfo.getValue()));
                super.executeJedisCommand(new SAddSet(id, db, key, newKeyInfo.getValue()));
                break;
            case HASH:
                if(!oldKeyInfo.getField().equals(newKeyInfo.getField())){
                    super.executeJedisCommand(new HdelHash(id,db,key,oldKeyInfo.getField()));
                }
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
    public Long deleteRowKeyInfo(String id,int db,byte[] key,byte[] valueOrField,int index,RedisType type) {
        Long count = 1L;
        switch (type) {
            case STRING:
                break;
            case LIST:
                //查看下标的值是否相等
                List<byte[]> list = super.executeJedisCommand(new LrangeList(id, db, key, index, index));
                if(CollectionUtils.isEmpty(list)){
                    throw new ServiceException("删除失败，数据已更新");
                }
                byte[] oldValue = list.get(0);
                if(!Arrays.equals(valueOrField, oldValue)) {
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
    public Boolean remamenx(String id, int db, byte[] oldkey, byte[] newkey) {
        return  super.executeJedisCommand(new RenameNxCommand(id,db,oldkey,newkey));
    }
    @Override
    public Map<String, String> getConnectInfo(String id,boolean isFresh){
        return getConnectInfo(id,isFresh,true);
    }
    @Override
    public Map<String, String> getConnectInfo(String id,boolean isFresh,boolean printLog) {
        RedisLarkContext redisLarkContext = RedisLarkPool.getRedisLarkContext(id);
        if(redisLarkContext == null){
            return new HashMap<>(0);
        }
        InfoCommand infoCommand = new InfoCommand(id, isFresh);
        infoCommand.setPrintLog(printLog);
        return super.executeJedisCommand(infoCommand);
    }
    @Override
    public List<RedisClientBo>  clientList(String id){
        return clientList(id,true);
    }
    @Override
    public List<RedisClientBo>  clientList(String id,boolean printLog){
        ClientListCommand clientListCommand = new ClientListCommand(id);
        clientListCommand.setPrintLog(printLog);
        String clientStr = super.executeJedisCommand(clientListCommand);
        List<RedisClientBo> resultList = new ArrayList<>();
        if(StringUtils.isNotBlank(clientStr)){
            String[] split = clientStr.split("\n");
            if(!ArraysUtil.isEmpty(split)){
                Map<String,Object>  map = null;
                for (String s : split) {
                    String[] mapper = s.split(" ");
                    if(!ArraysUtil.isEmpty(mapper)){
                        map = new HashMap<>(mapper.length);
                        for (String str : mapper) {
                            String[] keyValue = str.split("=");
                            if(keyValue.length == 2){
                                map.put(keyValue[0].replace("-","_"),keyValue[1]);
                            }else if(keyValue.length == 1){
                                map.put(keyValue[0].replace("-","_"),"");
                            }
                        }
                        if(MapUtils.isNotEmpty(map)){
                            try {
                                RedisClientBo redisClientBo = ReflectUtil.mapToObject(map, RedisClientBo.class);
                                resultList.add(redisClientBo);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
        Collections.sort(resultList,(o1,o2)->-o1.getAge().compareTo(o2.getAge()));
        return resultList;
    }
    @Override
    public List<SlowLogBo> slowlogGet(String id,boolean printLog){
        SlowlogGetCommand slowlogGetCommand = new SlowlogGetCommand(id);
        slowlogGetCommand.setPrintLog(printLog);
        return super.executeJedisCommand(slowlogGetCommand);
    }
    @Override
    public List<SlowLogBo> slowlogGet(String id){
        return slowlogGet(id,true);
    }
}
