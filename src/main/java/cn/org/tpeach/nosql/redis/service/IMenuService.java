package cn.org.tpeach.nosql.redis.service;

import cn.org.tpeach.nosql.redis.bean.RedisTreeItem;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;

public interface IMenuService {
	/**
	 * 切换语言
	 * @param language
	 * @param country
	 */
	boolean switchLanguage(String language, String country);
	
	
	JMenuBar initMenu();
	/**
	 * 初始化redisServer弹出菜单
	 * @return
	 */
	JPopupMenu initRedisTreePannelPopMenu(JTree tree);
	/**
	 * 初始化redisServer弹出菜单
	 * @return
	 */
	JPopupMenu initRedisServerPopMenu(JTree tree);

	/**
	 * 初始化RedisDB弹出菜单
	 * @return
	 */
	JPopupMenu initRedisDBPopMenu(JTree tree);
	/**
	 * 初始化RedisKey弹出菜单
	 * @return
	 */
	JPopupMenu initRedisKeyPopMenu(JTree tree,JTabbedPane topTabbedPane) ;

	void createDataTab(JTree tree, JTabbedPane topTabbedPane, DefaultMutableTreeNode node , RedisTreeItem item);
}
