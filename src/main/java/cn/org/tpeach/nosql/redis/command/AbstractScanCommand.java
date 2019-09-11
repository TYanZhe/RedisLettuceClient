package cn.org.tpeach.nosql.redis.command;

import redis.clients.jedis.ScanParams;

public abstract class AbstractScanCommand<T> extends JedisDbCommand<T>  {
    protected String key;
    protected String cursor;
    protected ScanParams scanParams;
    public AbstractScanCommand(String id, int db,String key,String cursor,Integer count) {
        super(id, db);
        this.key = key;
        this.cursor = cursor;
        this.scanParams = new ScanParams();
        this.scanParams.count(count);
        this.scanParams.match("*");
    }

    public AbstractScanCommand(String id, int db, String cursor,Integer count) {
        this(id,db,null,cursor,count);
    }

    public void match(byte[] pattern){
        this.scanParams.match(pattern);
    }
    public void match(String pattern){
        this.scanParams.match(pattern);
    }
}
