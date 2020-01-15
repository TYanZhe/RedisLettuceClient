package cn.org.tpeach.nosql.document.generator;

import cn.org.tpeach.nosql.document.*;

import java.util.ArrayList;

public class GeneratorMain {

    public static void main(String[] args) throws IllegalAccessException {
        RetMsg retMsg = RetMsg.buildSucess();
        RptOnlineTransPo bo = new RptOnlineTransPo();
        bo.build();
        ArrayList<Object> objects = new ArrayList<>();
        objects.add(bo);
        retMsg.putData("rows",objects);
        retMsg.putData("count",1);
        InterferfaceCondition.create("1.3","车辆充电记录审核列表",
                InterfaceDesc.buildGet_Form("/mon/audit/analysis/queryList").desc("车辆充电记录审核列表"),
                bo,
                retMsg);

    }
}
