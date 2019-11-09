package cn.org.tpeach.nosql.document;

import java.util.ArrayList;


public class GenerateInterface {

    public static void main(String[] args) throws IllegalAccessException {
        RetMsg retMsg = RetMsg.buildSucess();
        CustomPeriodBo customPeriodBo = new CustomPeriodBo();
        customPeriodBo.build();
        ArrayList<Object> objects = new ArrayList<>();
        objects.add(customPeriodBo);
        retMsg.putData("sharp",1);
        retMsg.putData("peak",1);
        retMsg.putData("offPeak",1);
        retMsg.putData("shoulder",1);
        InterferfaceCondition.create("3.5","桩使用率",
                InterfaceDesc.buildGet_Form("/busi/api/v0.1/qryOrderUseRateByStationId?statType=02&dateTime=201911&stationId=121").desc("桩使用率"),
                new TempBo().build(),
                retMsg);

    }


}
