package cn.org.tpeach.nosql.tools;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author tyz
 * @Title: DateUtils
 * @ProjectName redisLark-Github
 * @Description: TODO
 * @date 2019-10-04 14:39
 * @since 1.0.0
 */
public class DateUtils {
    private static final ConcurrentMap<String, DateTimeFormatter> FORMATTER_CACHE = new ConcurrentHashMap<>();

    private static final int PATTERN_CACHE_SIZE = 500;
    /**
     * Date转换为格式化时间
     * @param date date
     * @param pattern 格式
     * @return
     */
    public static String format(Date date, String pattern){
        return format(LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()), pattern);
    }

    /**
     * localDateTime转换为格式化时间
     * @param localDateTime localDateTime
     * @param pattern 格式
     * @return
     */
    public static String format(LocalDateTime localDateTime, String pattern){
        DateTimeFormatter formatter = createCacheFormatter(pattern);
        return localDateTime.format(formatter);
    }
    /**
     *  时间戳转换为格式化时间
     * @param timeStamp timeStamp
     * @param pattern 格式
     * @return
     */
    public static String format(long timeStamp, String pattern){
        DateTimeFormatter formatter = createCacheFormatter(pattern);
        return formatter.format(LocalDateTime.ofInstant(Instant.ofEpochMilli(timeStamp), ZoneId.systemDefault()));
    }
    /**
     * 格式化字符串转为Date
     * @param time 格式化时间
     * @param pattern 格式
     * @return
     */
    public static Date parseDate(String time, String pattern){
        return Date.from(parseLocalDateTime(time, pattern).atZone(ZoneId.systemDefault()).toInstant());

    }

    /**
     * 格式化字符串转为LocalDateTime
     * @param time 格式化时间
     * @param pattern 格式
     * @return
     */
    public static LocalDateTime parseLocalDateTime(String time, String pattern){
        DateTimeFormatter formatter = createCacheFormatter(pattern);
        return LocalDateTime.parse(time, formatter);
    }

    /**
     * 在缓存中创建DateTimeFormatter
     * @param pattern 格式
     * @return
     */
    private static DateTimeFormatter createCacheFormatter(String pattern){
        if (StringUtils.isBlank(pattern)) {
            throw new IllegalArgumentException("Invalid pattern specification");
        }
        DateTimeFormatter formatter = FORMATTER_CACHE.get(pattern);
        if(formatter == null){
            if(FORMATTER_CACHE.size() < PATTERN_CACHE_SIZE){
                formatter = DateTimeFormatter.ofPattern(pattern);
                DateTimeFormatter oldFormatter = FORMATTER_CACHE.putIfAbsent(pattern, formatter);
                if(oldFormatter != null){
                    formatter = oldFormatter;
                }
            }
        }

        return formatter;
    }
    public static String getDatePoor(long millisDiff) {
        long nd = 1000 * 24 * 60 * 60;
        long nh = 1000 * 60 * 60;
        long nm = 1000 * 60;
        long ns = 1000;
        // 获得两个时间的毫秒时间差异

        // 计算差多少天
        long day = millisDiff / nd;
        // 计算差多少小时
        long hour = millisDiff % nd / nh;
        // 计算差多少分钟
        long min = millisDiff % nd % nh / nm;
        // 计算差多少秒//输出结果
        long sec = millisDiff % nd % nh % nm / ns;
        return day + " day " + hour + " hour " + min + " min "+sec +" sec";
    }
}
