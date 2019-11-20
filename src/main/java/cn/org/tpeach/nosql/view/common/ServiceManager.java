/**
 * 
 */
package cn.org.tpeach.nosql.view.common;

import cn.org.tpeach.nosql.constant.PublicConstant;
import cn.org.tpeach.nosql.controller.BaseController;
import cn.org.tpeach.nosql.controller.ResultRes;
import cn.org.tpeach.nosql.enums.RedisType;
import cn.org.tpeach.nosql.framework.LarkFrame;
import cn.org.tpeach.nosql.redis.bean.RedisConnectInfo;
import cn.org.tpeach.nosql.redis.bean.RedisTreeItem;
import cn.org.tpeach.nosql.redis.connection.RedisLarkPool;
import cn.org.tpeach.nosql.redis.service.IRedisConfigService;
import cn.org.tpeach.nosql.redis.service.IRedisConnectService;
import cn.org.tpeach.nosql.service.ServiceProxy;
import cn.org.tpeach.nosql.tools.ArraysUtil;
import cn.org.tpeach.nosql.tools.CollectionUtils;
import cn.org.tpeach.nosql.tools.StringUtils;
import cn.org.tpeach.nosql.tools.SwingTools;
import cn.org.tpeach.nosql.view.RedisMainWindow;
import cn.org.tpeach.nosql.view.RedisTabbedPanel;
import cn.org.tpeach.nosql.view.StatePanel;
import cn.org.tpeach.nosql.view.component.RTabbedPane;
import cn.org.tpeach.nosql.view.jtree.RTreeNode;

import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

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
		try {
			path = java.net.URLDecoder.decode(path, PublicConstant.CharacterEncoding.UTF_8);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
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
	public void drawRedisKeyTree(JTree jTree,RTreeNode treeNode, final RedisTreeItem redisTreeItem, final List<byte[]> keys,boolean findOriginalNode) {
		if(findOriginalNode){
			RTreeNode rTreeNode = findoOriginalRedisTreeNode(jTree, treeNode);
			if(rTreeNode != null){
				this.drawRedisKeyTree(jTree,rTreeNode,redisTreeItem,keys,false);
			}
		}
		if(treeNode.getChildCount() > 0){
			treeNode.removeAllChildren();
		}

		RedisTreeItem treeItem = (RedisTreeItem) treeNode.getUserObject();
		boolean isReload = RedisType.KEY_NAMESPACE.equals(treeItem.getType());
		if (CollectionUtils.isNotEmpty(keys)) {
//			Set<String> sortKeys = new TreeSet<String>(Comparator.naturalOrder());
//			List<byte[]> sortKeys = new ArrayList<>(keys);
//			sortKeys.addAll(keys);
//			keys.clear();
//			Collections.sort(sortKeys,String.CASE_INSENSITIVE_ORDER);
			List<byte[]> sortKeys = keys;
			List<RTreeNode> lastRedisNodeList = new ArrayList<>();
			String lastKeys = null;
			RedisConnectInfo connectInfo = redisConfigService.getRedisConfigById(redisTreeItem.getId());
			String pattern = connectInfo.getNameSpaceSepartor();
			if(StringUtils.isBlank(pattern)){
				pattern = PublicConstant.NAMESPACE_SPLIT;
			}
			for (byte[] keySrc : sortKeys) {
				String key = StringUtils.byteToStr(keySrc);
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
								tempNode = SwingTools.addKeyTreeNode(tempNode, parentItem, keySrc, StringUtils.showHexStringValue(key),
										parentItem.getPath() + "/" + nameSpaceKey, key);
							} else {
								if (!isReload) {
									tempNode = SwingTools.addKeyNamespaceTreeNode(tempNode, parentItem, keySrc,
											StringUtils.showHexStringValue(nameSpaceKey), parentItem.getPath() + "/" + nameSpaceKey, nameSpaceKey);
								} else if(treeItem.getName().equals(nameSpaceKey)){
										isReload = false;
								}
							}
							lastRedisNodeList.add(tempNode);
						}
						lastKeys = key;
					}

				} else {
					SwingTools.addKeyTreeNode(treeNode, redisTreeItem, keySrc, StringUtils.showHexStringValue(key), redisTreeItem.getPath() + "/" + key,
							key);
				}
			}

		}
	}
	
	public void openConnectRedisTree(StatePanel statePanel,RTreeNode treeNode, RedisTreeItem redisTreeItem, JTree redisTree ) {
		String connectId = StringUtils.getUUID();
		RedisLarkPool.connectMap.put(treeNode,connectId);
		treeNode.incrConnecting(1);
		SwingTools.addLoadingTreeNode(redisTree,treeNode,redisTreeItem,
				()->BaseController.dispatcher(() -> redisConnectService.getDbAmountAndSize(redisTreeItem.getId()),false,true),
				res ->{
					treeNode.incrConnecting(-1);
					if(RedisLarkPool.connectMap.get(treeNode) != null && connectId.equals(RedisLarkPool.connectMap.get(treeNode))){
						RedisLarkPool.connectMap.remove(treeNode);
						statePanel.doUpdateStatus(redisTreeItem);
						if (res.isRet()) {
							ArraysUtil.each(res.getData(), (index, item) -> SwingTools.addDatabaseTreeNode(treeNode,redisTreeItem, item, index, redisTreeItem.getPath() + "/" + item));
							RTreeNode rTreeNode = findoOriginalRedisTreeNode(redisTree, treeNode);
							if(rTreeNode != null){
								ArraysUtil.each(res.getData(), (index, item) -> SwingTools.addDatabaseTreeNode(rTreeNode,redisTreeItem, item, index, redisTreeItem.getPath() + "/" + item));
							}
							redisTree.expandPath(new TreePath(treeNode.getPath()));
						} else {
							SwingTools.showMessageErrorDialog(null, res.getMsg(), "连接数据库异常");
						}
					}
				});
	}
	
	public void openDbRedisTree(RTreeNode treeNode, RedisTreeItem redisTreeItem, JTree redisTree, JTextField keyFilterField,boolean reload) {
		treeNode.incrConnecting(1);
		((RTreeNode) treeNode.getParent()).incrConnecting(1);
		SwingTools.addLoadingTreeNode(redisTree,treeNode,redisTreeItem,
				()->BaseController.dispatcher(() ->  redisConnectService.getKeys(redisTreeItem.getId(), redisTreeItem.getDb(),keyFilterField.getText() ,false),false,true) ,
				resDatabase ->{
					treeNode.incrConnecting(-1);
					((RTreeNode) treeNode.getParent()).incrConnecting(-1);
					if (resDatabase.isRet()) {
						final List<byte[]> keys = resDatabase.getData().getKeys();
						this.drawRedisKeyTree(redisTree,treeNode, redisTreeItem, keys,true);
						DefaultTreeModel defaultModel = (DefaultTreeModel)redisTree.getModel();
						if(reload){
							String name = redisTreeItem.getName();
							redisTreeItem.setName(name.substring(0, name.lastIndexOf("("))+"("+redisConnectService.getDbKeySize(redisTreeItem.getId(), redisTreeItem.getDb())+")");
						}
//            SwingTools.expandTreeNode(redisTree, treeNode);
						redisTree.expandPath(new TreePath(treeNode.getPath()));
						defaultModel.reload(treeNode);

					}else{
						SwingTools.showMessageErrorDialog(null, resDatabase.getMsg(), "连接数据库异常");//TODO 国际化
					}
				});

	}

	public void renameKey(JTree tree, RTreeNode treeNode, Consumer<String> success){
		RedisTreeItem redisTreeItem = (RedisTreeItem) treeNode.getUserObject();
		String name = SwingTools.showInputDialog(null, "NEW NAME:", "Rename key", StringUtils.showHexStringValue( redisTreeItem.getKey()));
		//取消
		if (name == null) {
			return;
		}
		if (StringUtils.isNotBlank(name)) {
			byte[] nameByte = StringUtils.strToByte(name);
			ResultRes<Boolean> resultRes = BaseController.dispatcher(() -> redisConnectService.remamenx(redisTreeItem.getId(), redisTreeItem.getDb(), redisTreeItem.getKey(), nameByte));

			if (!resultRes.isRet()) {
				SwingTools.showMessageErrorDialog(null, "Rename Failed: " + resultRes.getMsg());
			} else if (!resultRes.getData()) {
				SwingTools.showMessageErrorDialog(null, "Rename Failed: Key with new name already exist in database or original key was removed");
			}else{
				redisTreeItem.updateKeyName(nameByte,name);
				tree.updateUI();
				if(success != null){
					success.accept(name);
				}
			}
		}else{
			SwingTools.showMessageErrorDialog(null, "Rename Failed: Name cannot be null" );
			this.renameKey(tree,treeNode,success);
		}
	}

	public void removeNodeForRedisTabbedPanel(RTreeNode node, RTabbedPane topTabbedPane) {
		findNodeByRedisTabbedPanel(true,node,topTabbedPane,index->topTabbedPane.remove(index));
	}


	public void findNodeByRedisTabbedPanel(boolean mult,RTreeNode node, JTabbedPane topTabbedPane,Consumer<Integer> tabIndex){
		int tabCount = topTabbedPane.getTabCount();
		for (int i = tabCount - 1; i >= 0; i--) {
			Component component = topTabbedPane.getComponentAt(i);
			if (component instanceof RedisTabbedPanel) {
				RedisTabbedPanel tabPanel = (RedisTabbedPanel) component;
				if (tabPanel.getTreeNode().equals( node )) {
					tabIndex.accept(i);
					if(!mult){
						break;
					}
				}
			}
		}
	}
	public int findComponentIndexByTabbedPanel(Component component, JTabbedPane topTabbedPane ){
		int tabCount = topTabbedPane.getTabCount();
		for (int i = tabCount - 1; i >= 0; i--) {
			Component c = topTabbedPane.getComponentAt(i);
			if (c instanceof RedisTabbedPanel && c == component) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * 如果node节点是复制的  查找原始节点  否则返回空
	 * @param tree
	 * @param node
	 * @return
	 */
	public RTreeNode findoOriginalRedisTreeNode(JTree tree,RTreeNode node){
		RTreeNode root = ((RedisMainWindow) LarkFrame.frame).root;
		if(root != tree.getModel().getRoot()){
			Enumeration enumeration = root.children();
			LinkedList<RTreeNode> list = new LinkedList<>();
			while (enumeration.hasMoreElements()){
				RTreeNode child = getTreeNode(node, enumeration, list);
				if (child != null) {
					return child;
				}
			}
			while (!list.isEmpty()){
				RTreeNode rTreeNode = list.removeFirst();
				enumeration = rTreeNode.children();
				while (enumeration.hasMoreElements()){
					RTreeNode child = getTreeNode(node, enumeration, list);
					if (child != null) {
						return child;
					}
				}
			}
		}
		return null;
	}

	private RTreeNode getTreeNode(RTreeNode node, Enumeration enumeration, LinkedList<RTreeNode> list) {
		RTreeNode child = (RTreeNode) enumeration.nextElement();
		if (child.equals(node)) {
			return child;
		}
		if (child.getChildCount() != 0) {
			list.add(child);
		}
		return null;
	}


}
