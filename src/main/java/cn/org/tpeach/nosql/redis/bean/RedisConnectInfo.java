package cn.org.tpeach.nosql.redis.bean;

import cn.org.tpeach.nosql.constant.PublicConstant;
import cn.org.tpeach.nosql.tools.StringUtils;
import lombok.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Base64;
import java.util.Objects;

/**
 * @author tyz
 * @Title: RedisConnectInfo
 * @ProjectName RedisLark
 * @Description: TODO
 * @date 2019-06-23 16:26
 */
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class RedisConnectInfo implements Serializable,Cloneable {
    private static final long serialVersionUID = -8218404487749784420L;
    //连接类型 0默认
    // 考虑后期支持 1 ssh 2 ssl
    @Getter
    @Setter
    private short connType;
    //主键
    @Getter
    @Setter
    private String id;
    //0单机，1集群
    @Getter
    @Setter
    private int structure;
    //连接名
    @Getter
    @Setter
    private String name;
    //连接主机
    @Getter
    @Setter
    private String host;
    //端口 默认
    @Getter
    @Setter
    private int port ;
    //redis密码
    @Getter
    @Setter
    private String auth;


    @Override
    public boolean equals(Object o) {
        if (this == o){
            return true;
        }
        if (o == null || getClass() != o.getClass()){
            return false;
        }
        RedisConnectInfo that = (RedisConnectInfo) o;
        return connType == that.connType &&
                structure == that.structure &&
                port == that.port &&
                Objects.equals(id, that.id) &&
                Objects.equals(name, that.name) &&
                Objects.equals(host, that.host) &&
                Objects.equals(auth, that.auth);
    }

    @Override
    public int hashCode() {
        return Objects.hash(connType, id, structure, name, host, port, auth);
    }


    @Override
    public Object clone() throws CloneNotSupportedException {
        RedisConnectInfo clone = (RedisConnectInfo) super.clone();
        clone.setId(StringUtils.getUUID());
        return clone;
    }
}
