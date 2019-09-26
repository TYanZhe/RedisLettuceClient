package cn.org.tpeach.nosql.bean;

import cn.org.tpeach.nosql.constant.PublicConstant;
import cn.org.tpeach.nosql.tools.StringUtils;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TableColumnBean {

    private String type;
    private String value;
    private int index;


    public TableColumnBean(String type, String value,int index) {
        this.type = type;
        this.value = value;
        if(!StringUtils.isText(value)){
          this.type = PublicConstant.StingType.BINARY;
        }
        this.index = index;
    }

    public String getShowValue() {
        if(StringUtils.isText(value)){
            return value;
        }else{
            return  getHexStringValue(value);
        }
    }

    public static String getHexStringValue(String value) {
        char[] chars = value.toCharArray();
        StringBuffer sb = new StringBuffer();
        for (char c : chars) {
            Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
            if((c >= 32 && c <= 126) || (c==8 || c==9 || c==10 || c==13) || (c >= 0x4E00 &&  c <= 0x9FA5)
                    || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                    || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                    || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                    || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
                    || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                    || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS){
                sb.append(c);
            }else{
                int intNum = c;
                String s = "0x" + StringUtils.intToHexStringSmall(intNum);
                sb.append(s);
            }
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return  getShowValue();
    }
}
