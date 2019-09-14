/**
 * 
 */
package cn.org.tpeach.nosql.view.common;

import java.util.*;

import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import cn.org.tpeach.nosql.constant.PublicConstant;
import cn.org.tpeach.nosql.controller.BaseController;
import cn.org.tpeach.nosql.controller.ResultRes;
import cn.org.tpeach.nosql.enums.RedisType;
import cn.org.tpeach.nosql.redis.bean.RedisTreeItem;
import cn.org.tpeach.nosql.redis.service.IRedisConfigService;
import cn.org.tpeach.nosql.redis.service.IRedisConnectService;
import cn.org.tpeach.nosql.service.ServiceProxy;
import cn.org.tpeach.nosql.tools.ArraysUtil;
import cn.org.tpeach.nosql.tools.CollectionUtils;
import cn.org.tpeach.nosql.tools.StringUtils;
import cn.org.tpeach.nosql.tools.SwingTools;
import cn.org.tpeach.nosql.view.jtree.RTreeNode;

/**
　 * <p>Title: ServiceManager.java</p> 
　 * @author taoyz 
　 * @date 2019年8月27日 
　 * @version 1.0 
 */
public class ServiceManager {
	
	private static final class SingleHolder{
		private static ServiceManager instance = new ServiceManager();
	}
	private ServiceManager(){}
	public static ServiceManager getInstance() {
		return SingleHolder.instance;
	}
	IRedisConfigService redisConfigService = ServiceProxy.getBeanProxy("redisConfigService", IRedisConfigService.class);
	IRedisConnectService redisConnectService = ServiceProxy.getBeanProxy("redisConnectService", IRedisConnectService.class);
	/**
	 * 获取jar所在目录
	 * 
	 * @return
	 */
	public  String getPath() {
		String path = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
		if (System.getProperty("os.name").contains("dows")) {
			path = path.substring(1, path.length());
		}
		if (path.contains("jar")) {
			path = path.substring(0, path.lastIndexOf("."));
			return path.substring(0, path.lastIndexOf("/"));
		}
		return path.replace("target/classes/", "");
	}
	/**
	 * 
	 * @param treeNode
	 * @param redisTreeItem
	 * @param keys
	 */
	public void drawRedisKeyTree(RTreeNode treeNode, final RedisTreeItem redisTreeItem, final Collection<String> keys) {
		RedisTreeItem treeItem = (RedisTreeItem) treeNode.getUserObject();
		boolean isReload = RedisType.KEY_NAMESPACE.equals(treeItem.getType());
		if (CollectionUtils.isNotEmpty(keys)) {
//			Set<String> sortKeys = new TreeSet<String>(Comparator.naturalOrder());
			List<String> sortKeys = new ArrayList<>(keys);
//			sortKeys.addAll(keys);
			keys.clear();
			Collections.sort(sortKeys,String.CASE_INSENSITIVE_ORDER);
			List<RTreeNode> lastRedisNodeList = new ArrayList<>();
			String lastKeys = null;
			for (String key : sortKeys) {
				String pattern = PublicConstant.NAMESPACE_SPLIT;
				if (key != null && key.contains(pattern)) {
					String[] keySpace = key.split(pattern);
					if (!ArraysUtil.isEmpty(keySpace)) {
						RTreeNode tempNode = treeNode;
						int index = -1;
						int parentIndex = index;
						if (StringUtils.isNotBlank(lastKeys)) {
							String[] lastKeysSpace = lastKeys.split(pattern);
							for (int i = 0; i < keySpace.length; i++) {
								if (i >= lastKeysSpace.length || !keySpace[i].equals(lastKeysSpace[i])) {
									break;
								}
								index = i;
							}
							if (index != -1) {
								// 叶子节点相同
								int size = lastRedisNodeList.size();
								if (index == lastRedisNodeList.size() - 1) {
									parentIndex = index - 1;
								} else {
									parentIndex = index;
								}
								tempNode = lastRedisNodeList.get(parentIndex);
								// 移除后面的节点
								for (int i = size - 1; i > parentIndex; i--) {
									lastRedisNodeList.remove(i);
								}
							} else {
								lastRedisNodeList.clear();
							}
						}
						for (int i = parentIndex + 1; i < keySpace.length; i++) {
							String nameSpaceKey = keySpace[i];
							RedisTreeItem parentItem = (RedisTreeItem) tempNode.getUserObject();
							if (i == keySpace.length - 1) {
								tempNode = SwingTools.addKeyTreeNode(tempNode, parentItem, key, key,
										parentItem.getPath() + "/" + nameSpaceKey, key);
							} else {
								if (!isReload) {
									tempNode = SwingTools.addKeyNamespaceTreeNode(tempNode, parentItem, key,
											nameSpaceKey, parentItem.getPath() + "/" + nameSpaceKey, nameSpaceKey);
								} else if(treeItem.getName().equals(nameSpaceKey)){
										isReload = false;
								}
							}
							lastRedisNodeList.add(tempNode);
						}
						lastKeys = key;
					}

				} else {
					SwingTools.addKeyTreeNode(treeNode, redisTreeItem, key, key, redisTreeItem.getPath() + "/" + key,
							key);
				}
			}

		}
	}
	
	public void openConnectRedisTree(RTreeNode treeNode,RedisTreeItem redisTreeItem,JTree redisTree) {
		SwingTools.addLoadingTreeNode(redisTree,treeNode,redisTreeItem,
				()->BaseController.dispatcher(() -> redisConnectService.getDbAmountAndSize(redisTreeItem.getId())),
				res ->{
					if (res.isRet()) {
						ArraysUtil.each(res.getData(), (index, item) -> SwingTools.addDatabaseTreeNode(treeNode,redisTreeItem, item, index, redisTreeItem.getPath() + "/" + item));
						redisTree.expandPath(new TreePath(treeNode.getPath()));
					} else {
						SwingTools.showMessageErrorDialog(null, res.getMsg(), "连接数据库异常");
					}

				});
	}
	
	public void openDbRedisTree(RTreeNode treeNode, RedisTreeItem redisTreeItem, JTree redisTree, JTextField keyFilterField) {
		SwingTools.addLoadingTreeNode(redisTree,treeNode,redisTreeItem,
				()->BaseController.dispatcher(() -> redisConnectService.getKeys(redisTreeItem.getId(), redisTreeItem.getDb(),keyFilterField.getText())),
				resDatabase ->{
					if (resDatabase.isRet()) {
						final Collection<String> keys = resDatabase.getData();
						treeNode.removeAllChildren();
						this.drawRedisKeyTree(treeNode, redisTreeItem, keys);

						DefaultTreeModel defaultModel = (DefaultTreeModel)redisTree.getModel();
						String name = redisTreeItem.getName();
						redisTreeItem.setName(name.substring(0, name.lastIndexOf("("))+"("+redisConnectService.getDbKeySize(redisTreeItem.getId(), redisTreeItem.getDb())+")");
//            SwingTools.expandTreeNode(redisTree, treeNode);
						redisTree.expandPath(new TreePath(treeNode.getPath()));
						defaultModel.reload(treeNode);

					}else{
						SwingTools.showMessageErrorDialog(null, resDatabase.getMsg(), "连接数据库异常");//TODO 国际化
					}
				});

	}
}
