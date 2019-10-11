package cn.org.tpeach.nosql.bean;

import cn.org.tpeach.nosql.constant.PublicConstant;
import cn.org.tpeach.nosql.tools.StringUtils;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TableColumnBean {

    private String type;
    private byte[] value;
    private int index;


    public TableColumnBean(String type, byte[] value,int index) {
        this.type = type;
        this.value = value;
        if(!StringUtils.isText(value)){
          this.type = PublicConstant.StingType.BINARY;
        }
        this.index = index;
    }

    public String getShowValue() {
    	return StringUtils.showHexStringValue(value);
    }



    @Override
    public String toString() {
        return  getShowValue();
    }
}
