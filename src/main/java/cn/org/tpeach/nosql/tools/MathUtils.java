package cn.org.tpeach.nosql.tools;



import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * http://www.itdaan.com/keywords/%E6%95%B0%E5%AD%97%E8%BD%AC%E6%8D%A2%E5%B7%A5%E5%85%B7MathUtils.html</br>
 * BigDecimal详解:https://www.cnblogs.com/qynprime/p/8028397.html
 * @author: taoyz
 * @date: 2019/3/19 14:24
 * @description:
 */
public final class MathUtils {
    /**默认的除法精度*/
    private static final int DEF_DIV_SCALE = 10;
    /**默认的四舍五入精度*/
    private static final int ROUND_SCALE = 2;
    private static final String EMPTHSTR=" ";

    private MathUtils(){}

    /**
     * 获取BigDecimal 支持String  BigInteger Number </br>
     * value 不能为空
     * @param value
     * @return
     */
    public static BigDecimal getBigDecimal(Object value) {
        if(value == null){
            //            return BigDecimal.ZERO;
            throw new IllegalArgumentException("The value must not be null");
        }
        BigDecimal ret = null;
        if (value instanceof String) {
            value = value.toString().trim();
            if("".equals(value)){
//                    ret = BigDecimal.ZERO;
                throw new IllegalArgumentException("The value must not be null");
            }else{
                String regex="^[-+]?\\d*(\\.\\d+)*$";
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher((String) value);
                if(!matcher.matches()){
                    throw new NumberFormatException("For input string: \""+value+"\"");
                }
                ret = new BigDecimal((String) value);
            }

        }else if (value instanceof Number) {
            ret = new BigDecimal(String.valueOf(value));
        }else if (value instanceof BigInteger) {
            ret = new BigDecimal((BigInteger) value);
        }else if (value instanceof char[]) {
            ret = new BigDecimal((char[]) value);
        }else if (value instanceof BigDecimal) {
            ret = (BigDecimal) value;
        } else {
//          ret = BigDecimal.ZERO;
            String valueInfo = "";
            try{valueInfo = value.getClass().getName();}catch (Exception e){}
            throw new IllegalArgumentException("Cannot parse "+valueInfo);
        }
        return ret;
    }

    /**
     * double数组转换为String数组
     * @param arr
     * @return
     */
    private static String[] double2StringArr(double[] arr){
        if(arr !=null && arr.length > 0){
            String[] temp = new String[arr.length];
            for (int i = 0; i < arr.length; i++) {
                temp[i] = Double.toString(arr[i]);
            }
            return  temp;
        }
        return null;
    }
    /**
     * 提供精确的加法运算。
     * @param scale 精度, 为空时不设置精度
     * @param v1 加数
     * @param v2 被加数
     * @param numbers 不定个数的被加数
     * @return
     */
    public static String add(Integer scale,String v1,String v2,String... numbers) {
        if (scale != null && scale < 0) {
            throw new IllegalArgumentException("The scale must be a positive integer or zero");
        }
        if(v1 == null && v1.replaceAll(EMPTHSTR, "").isEmpty()){
            throw new IllegalArgumentException("The v1 must not be null");
        }
        if(v2 == null && v2.replaceAll(EMPTHSTR, "").isEmpty()){
            throw new IllegalArgumentException("The v2 must not be null");
        }
        BigDecimal result = getBigDecimal(v1).add(getBigDecimal(v2));
        if(numbers != null && numbers.length >0){
            for (String number : numbers) {
                result  =  result.add(getBigDecimal(number));
            }
        }
        if(scale == null){
            return result.toPlainString();
        }
        return result.setScale(scale,BigDecimal.ROUND_HALF_UP).toPlainString();

    }

    /**
     * 提供精确的加法运算。
     * @param v1 加数
     * @param v2 被加数
     * @param numbers 不定个数的被加数
     * @return
     */
    public static String add(String  v1,String v2,String... numbers) {
        return  add(null,v1,v2,numbers);
    }
    /**
     * 提供精确的加法运算。
     * @param v1 加数
     * @param v2 被加数
     * @param scale 精度, 为空时不设置精度
     * @param numbers 不定个数的被加数
     * @return
     */
    public static String addScale(int scale,double v1,double v2,double... numbers) {
        return  add(scale,Double.toString(v1),Double.toString(v1),double2StringArr(numbers));
    }
    /**
     * 提供精确的加法运算
     * @param v1 加数
     * @param v2 被加数
     * @param numbers 不定个数的被加数 至少需要一个
     * @return double
     */
    public static String add(double  v1,double  v2,double ... numbers) {
        return add(Double.toString(v1),Double.toString(v2),double2StringArr(numbers));
    }
    /**
     * 提供精确的减法运算。
     * @param scale 精度, 为空时不设置精度
     * @param v1 减数
     * @param v2 被减数
     * @param numbers 不定个数的被减数
     * @return
     */
    public static String subtract(Integer scale,String v1,String v2,String... numbers) {
        if (scale != null && scale < 0) {
            throw new IllegalArgumentException("The scale must be a positive integer or zero");
        }
        if(v1 == null && v1.replaceAll(EMPTHSTR, "").isEmpty()){
            throw new IllegalArgumentException("The v1 must not be null");
        }
        if(v2 == null && v2.replaceAll(EMPTHSTR, "").isEmpty()){
            throw new IllegalArgumentException("The v2 must not be null");
        }
        BigDecimal result = getBigDecimal(v1).subtract(getBigDecimal(v2));
        if(numbers != null && numbers.length >0){
            for (String number : numbers) {
                result  =  result.subtract(getBigDecimal(number));
            }
        }
        if(scale == null){
            return result.toPlainString();
        }
        return result.setScale(scale,BigDecimal.ROUND_HALF_UP).toPlainString();

    }

    /**
     * 提供精确的减法运算。
     * @param v1 减数
     * @param v2 被减数
     * @param numbers 不定个数的被减数
     * @return
     */
    public static String subtract(String  v1,String v2,String... numbers) {
        return  subtract(null,v1,v2,numbers);
    }
    /**
     * 提供精确的减法运算。
     * @param scale 精度, 为空时不设置精度
     * @param v1 减数
     * @param v2 被减数
     * @param numbers 不定个数的被减数
     * @return
     */
    public static String subtractScale(int scale,double v1,double v2,double... numbers) {
        return  subtract(scale,Double.toString(v1),Double.toString(v1),double2StringArr(numbers));
    }
    /**
     * 提供精确的减法运算
     * @param v1 加数
     * @param v2 被减数
     * @param numbers 不定个数的被减数
     * @return double
     */
    public static String subtract(double  v1,double  v2,double ... numbers) {
        return subtract(Double.toString(v1),Double.toString(v2),double2StringArr(numbers));
    }


    /**
     * 提供精确的乘法运算。
     * @param scale 精度, 为空时不设置精度
     * @param v1 乘数
     * @param v2 乘减数
     * @param numbers 不定个数的被减数
     * @return
     */
    public static String multiply(Integer scale,String v1,String v2,String... numbers) {
        if (scale != null && scale < 0) {
            throw new IllegalArgumentException("The scale must be a positive integer or zero");
        }
        if(v1 == null && v1.replaceAll(EMPTHSTR, "").isEmpty()){
            throw new IllegalArgumentException("The v1 must not be null");
        }
        if(v2 == null && v2.replaceAll(EMPTHSTR, "").isEmpty()){
            throw new IllegalArgumentException("The v2 must not be null");
        }
        BigDecimal result = getBigDecimal(v1).multiply(getBigDecimal(v2));
        if(numbers != null && numbers.length >0){
            for (String number : numbers) {
                result  =  result.multiply(getBigDecimal(number));
            }
        }
        if(scale == null){
            return result.toPlainString();
        }
        return result.setScale(scale,BigDecimal.ROUND_HALF_UP).toPlainString();

    }

    /**
     * 提供精确的乘法运算。
     * @param v1 乘数
     * @param v2 乘减数
     * @param numbers 不定个数的被减数
     * @return
     */
    public static String multiply(String  v1,String v2,String... numbers) {
        return  multiply(null,v1,v2,numbers);
    }
    /**
     * 提供精确的乘法运算。
     * @param scale 精度, 为空时不设置精度
     * @param v1 乘数
     * @param v2 乘减数
     * @param numbers 不定个数的被减数
     * @return
     */
    public static String multiplyScale(int scale,double v1,double v2,double... numbers) {
        return  multiply(scale,Double.toString(v1),Double.toString(v1),double2StringArr(numbers));
    }
    /**
     * 提供精确的乘法运算。
     * @param v1 乘数
     * @param v2 乘减数
     * @param numbers 不定个数的被减数
     * @return
     */
    public static String multiply(double  v1,double  v2,double ... numbers) {
        return multiply(Double.toString(v1),Double.toString(v2),double2StringArr(numbers));
    }
    /**
     *  提供（相对）精确的除法运算。当发生除不尽的情况时，由scale参数指定精度，以后的数字四舍五入。
     * @param v1    :除数
     * @param v2 :被除数
     * @param numbers 不定个数的被减数
     * @param scale :精度
     * @return double
     */
    public static String divide(int scale,String  v1,String  v2, String... numbers) {
        if (scale < 0) {
            throw new IllegalArgumentException("The scale must be a positive integer or zero");
        }
        BigDecimal result ;
        if(numbers != null && numbers.length >0){
            result = getBigDecimal(v1).divide(getBigDecimal(v2));
            BigDecimal v ;
            for (int i = 0; i < numbers.length; i++) {
                v = getBigDecimal(numbers[i]);
                if(BigDecimal.ZERO.compareTo(v) == 0){
                    throw new IllegalArgumentException("The dividend cannot be zero");
                }
                if(i == numbers.length -1){
//                result = result.add(new BigDecimal(numbers[i]), new MathContext(scale,RoundingMode.HALF_UP));
                    result =  result.divide(getBigDecimal(numbers[i]),scale, BigDecimal.ROUND_HALF_UP);
                }else{
                    result =  result.divide(getBigDecimal(numbers[i]));
                }

            }
        }else{
            result =  getBigDecimal(v1).divide(getBigDecimal(v2),scale, BigDecimal.ROUND_HALF_UP);
        }
        return result.toPlainString();
    }
    /**
     *  提供（相对）精确的除法运算。当发生除不尽的情况时，由scale参数指定精度，以后的数字四舍五入。
     * @param v1    :除数
     * @param v2 :被除数
     * @param numbers 不定个数的被减数
     * @param scale :精度
     * @return double
     */
    public static String divideScale(int scale,double  v1,double  v2, double ... numbers) {
        return divide(scale,Double.toString(v1),Double.toString(v2),double2StringArr(numbers));
    }
    /**
     *  提供（相对）精确的除法运算，当发生除不尽的情况时，精确到小数点以后10位，以后的数字四舍五入。
     * @param v1    :除数
     * @param numbers :被除数
     * @return double
     */
    public static String divide(double  v1,double  v2, double ... numbers) {
        return divideScale(DEF_DIV_SCALE,v1,v2,numbers);
    }
    /**
     *  提供（相对）精确的除法运算，当发生除不尽的情况时，精确到小数点以后10位，以后的数字四舍五入。
     * @param v1    :除数
     * @param numbers :被除数
     * @return double
     */
    public static String divide(String  v1,String  v2, String... numbers) {
        return divide(DEF_DIV_SCALE,v1,v2,numbers);
    }

    /**
     * 方法说明：比较大小
     *
     * @param v1
     * @param v2
     * @return -1: 小于  , 0: 等于 ,1: 大于
     */
    public static int compareTo(String v1, String v2){
        if(v1 == null || v1.replaceAll(EMPTHSTR, "").isEmpty()){
            throw new IllegalArgumentException("The v1 must not be null");
        }
        if(v2 == null || v2.replaceAll(EMPTHSTR, "").isEmpty()){
            throw new IllegalArgumentException("The v2 must not be null");
        }
        return getBigDecimal(v1).compareTo(getBigDecimal(v2));
    }
    /**
     *  提供精确的四舍五入运算
     * @param v :浮点数
     * @param scale :精度
     * @return double
     */
    public static double round(double v, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException("The scale must be a positive integer or zero");
        }
        return BigDecimal.valueOf(v).setScale(scale,RoundingMode.UP).doubleValue();
    }
    /**
     *  提供精确的四舍五入运算
     * @param v :浮点数
     * @param scale :精度
     * @return double
     */
    public static double roundByNumberFormat(double v, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException("The scale must be a positive integer or zero");
        }
        NumberFormat nf = NumberFormat.getNumberInstance();
        // 保留两位小数
        nf.setMaximumFractionDigits(scale);
        // 如果不需要四舍五入，可以使用RoundingMode.DOWN
        nf.setRoundingMode(RoundingMode.UP);
        return Double.valueOf(nf.format(v));
    }
    /**
     *  提供精确的四舍五入运算
     * @param v :浮点数
     * @param scale :精度
     * @return double
     */
    public static double roundByDecimalFormat(double v, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException("The scale must be a positive integer or zero");
        }
        StringBuffer sb = new StringBuffer("#");
        for (int i = 0;i < scale ;i++){
            if(i==0){
                sb.append(".");
            }
            sb.append("0");
        }
        DecimalFormat df = new DecimalFormat(sb.toString());
        return Double.valueOf(df.format(v));
    }
    /**
     *  提供精确的四舍五入运算 精确到小数点以后2位，以后的数字四舍五入。
     * @param v :浮点数
     * @return double
     */
    public static double round(double v) {
        return round(v,ROUND_SCALE);
    }
    /**
     * 转换为百分比
     * @param scale 精度, 为空时不设置精度
     * @param v1
     * @param flag 返回值是否包含% true 包含 false 不包含
     * @return
     */
    public static String toPercent(Integer scale,boolean flag,Number v1) {
        BigDecimal b1 = getBigDecimal(v1);
//        if(BigDecimal.ZERO.compareTo(b1) == -1 || BigDecimal.ONE.compareTo(b1) == 1){
//            throw new IllegalArgumentException("The v2 must be greater than or equal to 0 and less than or equal to 1");
//        }
        b1 = b1.movePointRight(2);
        String result = null ;
        if(scale != null){
            if (scale < 0) {
                throw new IllegalArgumentException("The scale must be a positive integer or zero");
            }
            result = b1.setScale(scale, BigDecimal.ROUND_HALF_UP).toPlainString();
        }else{
            result = b1.toPlainString();
        }
        if(flag) {
            result+="%";
        }
        return result;
    }


    /**
     * @param v1 大于等于0并且小于等于1的正数
     * @param flag
     * @return
     */
    public static String toPercent(Number v1,boolean flag) {
        return toPercent(null,flag,v1);
    }

    /**
     * 获取前者所占后者百分比
     * @param small
     * @param big
     * @param flag 返回值是否包含% true 包含 false 不包含
     * @return
     */
    public static String percentage(String small,String big,boolean flag) {
        return percentage(small,big,null,flag);
    }

    /**
     * 获取前者所占后者百分比
     * @param small
     * @param big
     * @param scale 精度, 为空时不设置精度
     * @param flag 返回值是否包含% true 包含 false 不包含
     * @return
     */
    public static String percentage(String small,String big,Integer scale,boolean flag) {
        if(small == null || small.replaceAll(EMPTHSTR, "").isEmpty()){
            throw new IllegalArgumentException("The small must not be null");
        }
        if(big == null || big.replaceAll(EMPTHSTR, "").isEmpty()){
            throw new IllegalArgumentException("The big must not be null");
        }
        return toPercent(scale,flag,Double.valueOf(divide(small,big)));
    }
    /**
     * 
     * @param v1
     * @param v2
     * @return
     */
    public static String getBigNum(String v1, String v2) {
        if(v1 == null || v1.replaceAll(EMPTHSTR, "").isEmpty()){
            throw new IllegalArgumentException("The v1 must not be null");
        }
        if(v2 == null || v2.replaceAll(EMPTHSTR, "").isEmpty()){
            throw new IllegalArgumentException("The v2 must not be null");
        }
        if(compareTo(v1,v2) == 1){
            return  v1;
        }else{
            return v2;
        }
    }
    /**
     * 
     * @param v1
     * @param v2
     * @return
     */
    public static String getSmallNum(String v1, String v2) {
        if(v1 == null || v1.replaceAll(EMPTHSTR, "").isEmpty()){
            throw new IllegalArgumentException("The v1 must not be null");
        }
        if(v2 == null || v2.replaceAll(EMPTHSTR, "").isEmpty()){
            throw new IllegalArgumentException("The v2 must not be null");
        }
        if(compareTo(v1,v2) == -1){
            return  v1;
        }else{
            return v2;
        }
    }
    
    public static String getAverage(int scale,double... numbers) {
        if(numbers != null &&  numbers.length > 0 ){
            return divide(scale,add(0,0,numbers), numbers.length+"");
        }

        return "0";
    }

    /**
     * short转byte
     */
    public static byte[] toBytes(short s) {
        return new byte[] { (byte) (s & 0x00FF), (byte) ((s & 0xFF00) >> 8) };
    }


    /**
     * getAvgEnergy for int[]
     *
     * @param voiceEnergy
     * @return
     */
    public static int getAvgEnergy(int[] voiceEnergy) {
        int tmpSum = 0;
        int voiceLen = voiceEnergy.length;
        for (int i = 0; i < voiceLen; i++) {
            tmpSum += Math.abs(voiceEnergy[i]);
        }
        return tmpSum / voiceLen;
    }

    /**
     * byte[] to int []
     *
     * @param byteData
     * @return
     */
    public static int[] convertToInt(byte[] byteData) {
        if (byteData == null || byteData.length == 0){
            return null;
        }
        int[] data = new int[byteData.length / 2];
        for (int i = 0; i < byteData.length; i = i + 2) {
            data[i / 2] = arr2int(byteData[i], byteData[i + 1]);
        }
        return data;
    }

    /**
     * arr2int
     *
     * @param arr0
     * @param arr1
     * @return
     */
    public static int arr2int(byte arr0, byte arr1) {
        int iLow = arr0;
        int iHigh = arr1;
        // Merge high-order and low-order byte to form a 16-bit double value.
        short i = (short) ((iHigh << 8) | (0xFF & iLow));
        return (int) i;
    }

    /**
     * short转换至字节数组
     *
     * @param s
     * @return
     */
    public static byte[] shortToByteArray(short s) {
        byte[] targets = new byte[2];
        for (int i = 0; i < 2; i++) {
            int offset = (targets.length - 1 - i) * 8;
            targets[i] = (byte) ((s >>> offset) & 0xff);
        }
        return targets;
    }


    /**
     * unsigned short转byte
     */
    public static byte[] unsignedShortToBytes(int s) {
        return new byte[] { (byte) (s & 0x000000FF), (byte) ((s & 0x0000FF00) >> 8) };
    }

    /**
     * int转byte
     */
    public static byte[] toBytes(int s) {
        return new byte[] { (byte) (s & 0x000000FF), (byte) ((s & 0x0000FF00) >> 8), (byte) ((s & 0x00FF0000) >> 16),
                (byte) ((s & 0xFF000000) >> 24) };
    }

    /**
     * byte转int
     */
    public static int toInt(byte[] b) {
        return b[0] & 0xff | (b[1] & 0xff) << 8 | (b[2] & 0xff) << 16 | (b[3] & 0xff << 24);
    }

    /**
     * byte转long
     */
    public static long toUnsignedInt(byte[] b) {
        return b[0] & 0xff | (b[1] & 0xff) << 8 | (b[2] & 0xff) << 16 | (b[3] << 24);
    }

    /**
     * byte转short
     */
    public static short toShort(byte[] b) {
        // return (short) (b[0] << 24 | (b[1] & 0xff) << 16) ;
        return (short) (((b[1] << 8) | b[0] & 0xff));
    }

    /**
     * byte转unsigned short
     */
    public static int toUnsignedShort(byte[] b) {
        return (b[0] << 24 | (b[1] & 0xff) << 16);
    }

    /**
     * Assume the long is used as unsigned int
     *
     * @param s
     * @return
     */
    public static byte[] unsignedIntToBytes(long s) {
        return new byte[] { (byte) (s & 0x00000000000000FF), (byte) ((s & 0x000000000000FF00) >> 8),
                (byte) ((s & 0x0000000000FF0000) >> 16), (byte) ((s & 0x00000000FF000000) >> 24) };
    }

    /**
     * float转换byte
     *
     * @param x
     */
    public static byte[] putFloat(float x) {
        byte[] b = new byte[4];
        int l = Float.floatToIntBits(x);
        for (int i = 0; i < 4; i++) {
            b[i] = new Integer(l).byteValue();
            l = l >> 8;
        }
        return b;
    }

    /**
     * 通过byte数组取得float
     *
     * @param b
     * @return
     */
    public static float getFloat(byte[] b) {
        int l;
        l = b[0];
        l &= 0xff;
        l |= ((long) b[1] << 8);
        l &= 0xffff;
        l |= ((long) b[2] << 16);
        l &= 0xffffff;
        l |= ((long) b[3] << 24);
        return Float.intBitsToFloat(l);
    }

    /**
     * 将data字节型数据转换为0~255 (0xFF 即BYTE)。
     * @param data
     * @return
     */
    public int getUnsignedByte (byte data){
        return data&0x0FF;
    }

    /**
     * 将data字节型数据转换为0~65535 (0xFFFF 即 WORD)。
     * @param data
     * @return
     */
    public int getUnsignedByte (short data){
        return data&0x0FFFF;
    }

    /**
     * 将int数据转换为0~4294967295 (0xFFFFFFFF即DWORD)。
     * @param data
     * @return
     */
    public long getUnsignedIntt (int data){
        return data&0x0FFFFFFFFl;
    }

    public static void main(String[] args) {
//        System.out.println("加法测试add："+MathUtils.add( 2,453,990,4.98080350234,83.093,80.3450388,900.0835853853085353));
//      System.out.println("加法测试add："+MathUtils.add(3,"0324.394382","1.2"));
//        System.out.println("加法测试addScale："+MathUtils.addScale( 4,2,453,990,4.98080350234,83.093,80.3450388,900.0835853853085353));
//        System.out.println("减法测试subtract："+ MathUtils.subtract(2, 242432.034323, 02433243, 977.9329732732));
//        System.out.println("减法测试subtractScale："+ MathUtils.subtractScale(2, 242432.034323, 02433243, 977.9329732732));
//        System.out.println("乘法测试multiply："+MathUtils.multiply(5, 3.9));
//        System.out.println("乘法测试multiplyScale："+MathUtils.multiplyScale(5, 3.9,90.83));
        System.out.println("除法测试divide："+MathUtils.divide(4.5,1.3));
        System.out.println("除法测试divideScale："+MathUtils.divideScale(10000,4.5,1.3));
//        BigDecimal bigDecimal = getBigDecimal("");
//        System.out.println(round(4.921243));
//        System.out.println("compareTo测试："+compareTo("34.901", "34.91"));
        System.out.println(Double.valueOf(getBigNum("453", "+472")));
        System.out.println(getBigDecimal("+432"));
        System.out.println(percentage("25.988","33",2,true));

        System.out.println("平均数："+getAverage(0,24,36));
        DecimalFormat df = new DecimalFormat("#");
        System.out.println(df.format(353.9424));
    }
}
