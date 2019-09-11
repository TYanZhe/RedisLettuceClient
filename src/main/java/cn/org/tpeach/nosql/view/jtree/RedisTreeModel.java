/**
 * 
 */
package cn.org.tpeach.nosql.view.jtree;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

/**
　 * <p>Title: RedisTreeModel.java</p> 
　 * @author taoyz 
　 * @date 2019年8月20日 
　 * @version 1.0 
 */
public class RedisTreeModel extends DefaultTreeModel{


	private static final long serialVersionUID = 8174867046429069823L;

	/**
	 * @param root
	 */
	public RedisTreeModel(TreeNode root) {
		super(root);
	}

	public RedisTreeModel(TreeNode root, boolean asksAllowsChildren) {
		super(root, asksAllowsChildren);
	}
	
	@Override
	public void valueForPathChanged(TreePath path, Object newValue) {
		Object obj = ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject();
		super.valueForPathChanged(path, obj);
	}



}
