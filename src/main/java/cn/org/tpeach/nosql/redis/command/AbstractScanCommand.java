package cn.org.tpeach.nosql.redis.command;

import io.lettuce.core.ScanArgs;
import io.lettuce.core.ScanCursor;


public abstract class AbstractScanCommand<T> extends JedisDbCommand<T>  {
    protected String key;
    protected ScanCursor scanCursor;
    protected ScanArgs scanArgs;
    public AbstractScanCommand(String id, int db,String key,ScanCursor scanCursor,Integer count) {
        super(id, db);
        this.key = key;
        this.scanCursor = scanCursor;
        this.scanArgs = ScanArgs.Builder.limit(count);
        this.scanArgs.match("*");
    }

    public AbstractScanCommand(String id, int db,ScanCursor scanCursor,Integer count) {
        this(id,db,null,scanCursor,count);
    }

    public void match(String pattern){
        this.scanArgs.match(pattern);
    }
    

}
