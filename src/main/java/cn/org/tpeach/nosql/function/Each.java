package cn.org.tpeach.nosql.function;

/**
 * @author tyz
 * @Title: Each
 * @ProjectName RedisLark
 * @Description: TODO
 * @date 2019-06-25 20:36
 * @since 1.0.0
 */
@FunctionalInterface
public interface Each<T> {
	public void foreach(int i, T t);

}
