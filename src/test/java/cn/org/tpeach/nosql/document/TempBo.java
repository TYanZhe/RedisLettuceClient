package cn.org.tpeach.nosql.document;

import lombok.Data;

@RequestCondition(requireType = "json")
@Data
public class TempBo extends InterferfaceCondition{
    @RequestCondition(require = Request.Require.Required,desc = "站点id",demo = "39")
    private java.lang.Long stationId;

    @RequestCondition(require = Request.Require.Required,desc = "按日 01 按月02",demo = "01")
    private String statType;
    @RequestCondition(require = Request.Require.Required,desc = "时间yyyyMMdd yyyyMM",demo = "201911")
    private String dateTime;


}
