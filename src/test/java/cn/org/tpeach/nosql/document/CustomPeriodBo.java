package cn.org.tpeach.nosql.document;

import lombok.Data;

/**
 * 
 * @Title: CustomPeriodBo
 * @Description: 用户自定义时间段（统计桩使用效率）
 * @author 
 * @date 2019-11-06 16:28:43
 */ 
@RequestCondition(requireType = "json")
@Data
public class CustomPeriodBo extends InterferfaceCondition{
	/**自增id*/
	private java.lang.Integer customId; 
	/**站点id*/
	@RequestCondition(require = Request.Require.Optional,desc = "站点id",demo = "39")
	@ResponseBody(require = Request.Require.Optional,desc = "站点id",demo = "39")
	private java.lang.Long stationId;
	@RequestCondition(require = Request.Require.Optional,desc = "管理单位",demo = "tsts")
	@ResponseBody(require = Request.Require.Optional,desc = "管理单位",demo = "tsts")
	private String orgCode;
	/**时段序号*/
	@ResponseBody(require = Request.Require.Optional,desc = "时段序号",demo = "1")
	private java.lang.Integer periodNo; 
	/**开始时间*/
	@ResponseBody(require = Request.Require.Optional,desc = "开始时间",demo = "00:00")
	private java.lang.String beginTime; 
	/**结束时间*/
	@ResponseBody(require = Request.Require.Optional,desc = "结束时间",demo = "11:00")
	private java.lang.String endTime; 
	/**更新时间*/
	@ResponseBody(require = Request.Require.Optional,desc = "更新时间",demo = "2019-11-06 15:50:35")
	private java.util.Date updateTime; 
	/**操作账户*/
	@ResponseBody(require = Request.Require.Optional,desc = "操作账户",demo = "SYSADMIN")
	private java.lang.String operator;
//	CustomPeriodBo customPeriodBo = CustomPeriodBo.builder().customId(15).periodNo(1).operator("SYSADMIN").beginTime("00:00").endTime("24:00").stationId(1L).updateTime(new Date()).build();






}