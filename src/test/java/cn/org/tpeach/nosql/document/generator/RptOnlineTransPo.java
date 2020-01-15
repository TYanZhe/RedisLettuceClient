package cn.org.tpeach.nosql.document.generator;

import cn.org.tpeach.nosql.document.InterferfaceCondition;
import cn.org.tpeach.nosql.document.Request;
import cn.org.tpeach.nosql.document.RequestCondition;
import cn.org.tpeach.nosql.document.ResponseBody;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @Title: RptOnlineTransPo
 * @Description: TODO
 * @author 
 * @date 2019-12-10 09:28:00
 */ 
@Data
@NoArgsConstructor
public class RptOnlineTransPo  extends InterferfaceCondition {
	/**自增id*/
	@RequestCondition(require = Request.Require.Optional,desc = "自增id",demo = "39")
	@ResponseBody(require = Request.Require.Optional,desc = "自增id",demo = "39")
	private java.lang.Integer id;
	/**在线交易记录ID*/
	private Long onlineTransId;
	/**线路id*/
	@RequestCondition(require = Request.Require.Optional,desc = "线路ID")
	private String lineId;
	@ResponseBody(desc = "线路编号",demo = "39")
	private String lineNo;
	/**车辆id*/
	@RequestCondition(require = Request.Require.Optional,desc = "车辆ID")
	private Long busId;
	/**站点id*/
	@RequestCondition(desc = "站点id",demo = "39")
	private Long stationId;
	/**充电桩ID*/
	@RequestCondition(desc = "桩id",demo = "34")
	private Long pileId;
	/**枪id*/
	@RequestCondition(desc = "枪id",demo = "1234")
	private Long gunId;

	@ResponseBody(require = Request.Require.Optional,desc = "站点名称",demo = "XXX站")
	private String  stationName;
	@ResponseBody(require = Request.Require.Optional,desc = "桩名-枪名",demo = "1号桩-A枪")
	private String  pileGunName;
	@ResponseBody(require = Request.Require.Optional,desc = "车牌号",demo = "粤xxxxx")
	private String  plateNo;
	/**管理单位*/
	@RequestCondition(desc = "管理单位",demo = "SDTC")
	@ResponseBody(require = Request.Require.Optional,desc = "管理单位",demo = "SDTC")
	private String orgCode;
	@ResponseBody(desc = "管理单位名称",demo = "顺德TC")
	private String orgCodeName;
	/**充电开始时间*/
	@RequestCondition(desc = "充电开始时间",demo ="2019-12-20 12:23:23")
	@ResponseBody(desc = "充电开始时间",demo = "2019-12-20 12:23:23")
	private String  beginTime;
	/**充电结束时间*/
	@RequestCondition(desc = "充电结束时间",demo ="2019-12-20 12:23:23")
	@ResponseBody(desc = "充电结束时间",demo = "2019-12-20 12:23:23")
	private String  endTime;
	/**总充电量*/
	@RequestCondition(desc = "总充电量",demo = "39.03")
	@ResponseBody(desc = "总充电量",demo = "39.03")
	private String tPq;
	/**尖期电量*/
	@RequestCondition(desc = "尖期电量",demo = "39.03")
	@ResponseBody(desc = "尖期电量",demo = "39.03")
	private String tinePq;
	/**峰期电量*/
	@RequestCondition(desc = "峰期电量",demo = "39.03")
	@ResponseBody(desc = "峰期电量",demo = "39.03")
	private String peakPq;
	/**平期电量*/
	@RequestCondition(desc = "平期电量",demo = "39.03")
	@ResponseBody(desc = "平期电量",demo = "39.03")
	private String flatPq;
	/**谷期电量*/
	@RequestCondition(desc = "谷期电量",demo = "39.03")
	@ResponseBody(desc = "谷期电量",demo = "39.03")
	private String valleyPq;
	/**数据更新时间*/
	@ResponseBody(desc = "数据更新时间",demo = "2019-12-20 12:23:23")
	private String updateTime;
 
}