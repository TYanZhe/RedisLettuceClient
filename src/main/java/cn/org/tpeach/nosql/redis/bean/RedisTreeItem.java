package cn.org.tpeach.nosql.redis.bean;

import java.awt.Color;

import javax.swing.ImageIcon;

import cn.org.tpeach.nosql.constant.PublicConstant;
import cn.org.tpeach.nosql.enums.RedisType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author tyz
 * @Title: RedisTreeItem
 * @ProjectName RedisLark
 * @Description:
 * @date 2019-06-30 22:11
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RedisTreeItem {
	/**
	 * 连接id
	 */
	private String id;
	public RedisTreeItem parentItem;
	private Integer db;
	private String key;
	private String name;
	private String originName;
	private RedisType type;
	private String path;
	//设置工具提示字符串
	public String tipText;

	public String getParentName(){
		String parentName = this.getName();
		RedisTreeItem tempItem = this;
		while (tempItem.getParentItem() != null){
			tempItem = tempItem.getParentItem();
			if(tempItem.getParentItem() == null){
				parentName = tempItem.getName();
			}
		}
		return parentName;
	}


	public void updateKeyName(String name){
		if(RedisType.KEY.equals(this.getType())){
			this.setName(name);
			this.setTipText(name);
			this.setKey(name);
			this.setOriginName(name);
			int i = this.getPath().lastIndexOf("/");
			if(i != -1){
				this.setPath(this.getPath().substring(0,i+1)+name);
			}
		}
	}

	
}
