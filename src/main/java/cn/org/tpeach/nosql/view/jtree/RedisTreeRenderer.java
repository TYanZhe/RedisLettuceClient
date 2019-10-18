package cn.org.tpeach.nosql.view.jtree;

import cn.org.tpeach.nosql.constant.PublicConstant;
import cn.org.tpeach.nosql.enums.RedisType;
import cn.org.tpeach.nosql.redis.bean.RedisTreeItem;
import cn.org.tpeach.nosql.tools.StringUtils;
import lombok.Getter;
import lombok.Setter;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeNode;
import java.awt.*;
import java.util.Enumeration;

/**
 * 树节点渲染器
 * 
 * @author:
 * @date: 2019-08-20
 */
public class RedisTreeRenderer extends DefaultTreeCellRenderer {

	private static final long serialVersionUID = 2301557473384045953L;
	// 通过mouseEnter判定当前鼠标是否悬停
	private boolean mouseEnter = false;
	public static int mouseRow = -1;
	@Getter
	@Setter
	public JTextField keyFilterField;
	private Color backgroundColor;

	public RedisTreeRenderer() {
		super();
		// 设置false背景色无效
		setOpaque(true);
	}

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf,
			int row, boolean hasFocus) {

		this.hasFocus = hasFocus;

		// 取得節點
		RTreeNode treeNode = (RTreeNode) value;
		RTreeNode selectNode = (RTreeNode) tree.getLastSelectedPathComponent();

		// 取得路徑
		TreeNode[] paths = treeNode.getPath();
		Object userObject = treeNode.getUserObject();
		Icon icon = null;
		if (userObject instanceof RedisTreeItem) {
			RedisTreeItem redisTreeItem = (RedisTreeItem) userObject;
			if (StringUtils.isNotBlank(redisTreeItem.getTipText())) {
				this.setToolTipText(redisTreeItem.getTipText());
			}
			// 按路径层次赋予不同的图标
			if(RedisType.LOADING.equals(redisTreeItem.getType())) {
				icon = treeNode.getIcon();
				this.setText(redisTreeItem.getName());
			}else {
				// 设置文字
				if (paths.length > 3 && !redisTreeItem.getType().equals(RedisType.KEY) && !RedisType.LOADING.equals(redisTreeItem.getType())) {
					if(treeNode.isEnabled()) {
						this.setText(redisTreeItem.getName() + "(" + treeNode.getLeafCount() + ")");
					}else {
						this.setText(redisTreeItem.getName() + "(0)");
					}

				} else {
					this.setText(redisTreeItem.getName());
				}
				if (paths.length == 2) {
					// 设置图标 服务图标
					// 按展開情況再賦予不同的圖標
					if (expanded) {
						icon = PublicConstant.Image.redis_server;
					} else {
						icon = PublicConstant.Image.redis_server;
//					icon = PublicConstant.Image.redis_db;
					}

				} else if (paths.length == 3) {
					icon = PublicConstant.Image.database;
				} else if (paths.length > 3) {
					if (redisTreeItem.getType().equals(RedisType.KEY)) {
						icon = PublicConstant.Image.key_icon;
					} else {
						icon = PublicConstant.Image.folder_database;
					}

				} else {
					// 默认图标
				}
			}
			if (!tree.isEnabled() || !treeNode.isEnabled()) {
				setEnabled(false);
				LookAndFeel laf = UIManager.getLookAndFeel();
				Icon disabledIcon = laf.getDisabledIcon(tree, icon);
				if (disabledIcon != null){
					icon = disabledIcon;
				}

				setDisabledIcon(icon);
			} else {
				setEnabled(true);
				setIcon(icon);
			}

//			filterTreeNode(treeNode, redisTreeItem);

		}
		
		this.setIcon(icon);
		// 通过mouseRow判断鼠标是否悬停在当前行
		if (mouseRow == row) {
			mouseEnter = true;
		} else {
			mouseEnter = false;
		}


//		setComponentOrientation(tree.getComponentOrientation());
		selected = sel;
		if(selected){
			this.setBackground(new Color(230, 244, 254));
		}else{
			this.setBackground(Color.WHITE);
		}
		return this;

	}

	/**
	 * @param treeNode
	 * @param redisTreeItem
	 */
	private void filterTreeNode(RTreeNode treeNode, RedisTreeItem redisTreeItem) {
		if(keyFilterField != null  ){
			if (matchesFilter(treeNode)) {
				if (redisTreeItem.getType().equals(RedisType.KEY) && StringUtils.isNotBlank(keyFilterField.getText())) {
					this.setForeground(Color.RED);
				} else {
					this.setForeground(Color.BLACK);
				}

//			if(null != treeNode.getWidth() && null != treeNode.getHeight()){
//				this.setPreferredSize( new Dimension( treeNode.getWidth(), treeNode.getHeight()) );
//			}
//			this.setPreferredSize( new Dimension( 200, 28) );
			}else if (containsMatchingChild(treeNode)) {
				this.setForeground(Color.GRAY);
				if(null != treeNode.getWidth() && null != treeNode.getHeight()){
//				this.setPreferredSize( new Dimension( treeNode.getWidth(), treeNode.getHeight()) );

				}
//			this.setPreferredSize( new Dimension( 200, 28) );
			}else {
				if(this.getPreferredSize().width != 0){
					treeNode.setWidth(this.getPreferredSize().width);
				}
				if(this.getPreferredSize().height != 0){
					treeNode.setHeight(this.getPreferredSize().height);
				}
//			this.setPreferredSize( new Dimension( 0, 0 ) );
			}
		}
	}

	@Override
	public void paint(Graphics g) {
		if(!selected){
			if (mouseEnter) {
				if (backgroundColor == null) {
					backgroundColor = this.getBackground();
				}
				this.setBackground(new Color(230, 244, 254));
			} else {
				this.setBackground(backgroundColor);
			}
		}


		super.paint(g);

	}
	private boolean matchesFilter(DefaultMutableTreeNode node) {
		if(keyFilterField == null){
			return true;
		}
		return node.toString().contains(keyFilterField.getText());
	}

	private boolean containsMatchingChild(DefaultMutableTreeNode node) {
		Enumeration<DefaultMutableTreeNode> e = node.breadthFirstEnumeration();
		while (e.hasMoreElements()) {
			if (matchesFilter(e.nextElement())) {
				return true;
			}
		}

		return false;
	}
}