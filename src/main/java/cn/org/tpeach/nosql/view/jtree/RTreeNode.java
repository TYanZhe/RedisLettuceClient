/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.org.tpeach.nosql.view.jtree;

import lombok.Getter;
import lombok.Setter;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;

/**
 *
 * @author smart
 */
@Getter
@Setter
public class RTreeNode extends DefaultMutableTreeNode {

	private static final long serialVersionUID = 2813177397472285476L;
	private boolean isSelected = false;
	private boolean enabled = true;
	private boolean isVisible = true;
	private int selectionMode = 0;
	private Integer width;
	private Integer height;
	private ImageIcon icon = null;
	private String iconName = null;
	public ImageIcon closedIcon;// 用于显示无扩展的非叶节点的图标
	public ImageIcon closedIconSelected;// 父节点关闭时选中图标
	public ImageIcon openIcon;// 用于显示扩展的非叶节点的图标
	public ImageIcon openIconSelected;// 父节点展开后选中图标
	public ImageIcon leafIcon;// 用于显示叶节点的图标
	public ImageIcon leafIconSelected;// 子节点选中图标

	public Color BackgroundSelectionColor;// 选中背景色
	public Color ForegroundSelectionColor;// 选中前景色
	public Color BackgroundNonSelectionColor;// 未选中时背景色
	public Color ForegroundNonSelectionColor;// 未选中背景色

	public RTreeNode() {
		super();
	}

	public RTreeNode(Object userObject) {
		super(userObject);
	}

	public RTreeNode(Object userObject, ImageIcon icon) {
		super(userObject);
		this.icon = icon;
	}

	/**
	 * 
	 * @param userObject
	 * @param allowsChildren
	 * @param isSelected
	 * @param enabled
	 * @param isVisible
	 * @param icon
	 */
	public RTreeNode(Object userObject, boolean allowsChildren, boolean isSelected, boolean enabled,
			boolean isVisible, ImageIcon icon) {

		super(userObject, allowsChildren);

		this.isSelected = isSelected;

		this.enabled = enabled;

		this.isVisible = isVisible;

		this.icon = icon;

		setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);

	}

	/**
	 * 使用StringIcon(String name, ImageIcon icon)实例化将采用默认的图标
	 * 
	 * @param name
	 *            显示名称
	 * @param closedIcon
	 *            用于显示无扩展的非叶节点的图标
	 * @param closedIconselected
	 *            父节点关闭时选中图标
	 * @param openIcon
	 *            用于显示扩展的非叶节点的图标
	 * @param openIconselected
	 *            父节点展开后选中图标
	 * @param leafIcon
	 *            用于显示叶节点的图标
	 * @param leafIconselected
	 *            子节点选中图标
	 */
	public RTreeNode(String name, ImageIcon closedIcon, ImageIcon closedIconSelected, ImageIcon openIcon,
			ImageIcon openIconSelected, ImageIcon leafIcon, ImageIcon leafIconSelected) {
		this.closedIcon = closedIcon;
		this.closedIconSelected = closedIconSelected;
		this.openIcon = openIcon;
		this.openIconSelected = openIconSelected;
		this.leafIcon = leafIcon;
		this.leafIconSelected = leafIconSelected;
	}

	/**
	 * 设置Tree各状态图标
	 * 
	 * @param closedIcon
	 *            用于显示无扩展的非叶节点的图标
	 * @param closedIconselected
	 *            父节点关闭时选中图标
	 * @param openIcon
	 *            用于显示扩展的非叶节点的图标
	 * @param openIconselected
	 *            父节点展开后选中图标
	 * @param leafIcon
	 *            用于显示叶节点的图标
	 * @param leafIconselected
	 *            子节点选中图标
	 */
	public void setTreeIcon(ImageIcon closedIcon, ImageIcon closedIconSelected, ImageIcon openIcon,
			ImageIcon openIconSelected, ImageIcon leafIcon, ImageIcon leafIconSelected) {
		this.closedIcon = closedIcon;
		this.closedIconSelected = closedIconSelected;
		this.openIcon = openIcon;
		this.openIconSelected = openIconSelected;
		this.leafIcon = leafIcon;
		this.leafIconSelected = leafIconSelected;
	}

	/**
	 * 设置根叶目录的图标,只有这两个状态
	 * 
	 * @param rootIcon
	 *            根目录图标closedIcon,closedIconSelected,openIcon,openIconSelected
	 * @param leafIcon
	 *            叶目录图标leafIcon,leafIconSelected
	 */
	public void setTreeRootLeafIcon(ImageIcon rootIcon, ImageIcon leafIcon) {
		this.closedIcon = rootIcon;
		this.closedIconSelected = rootIcon;
		this.openIcon = rootIcon;
		this.openIconSelected = rootIcon;
		this.leafIcon = leafIcon;
		this.leafIconSelected = leafIcon;
	}

	/**
	 * 设置根叶目录的图标,只有这三个状态
	 * 
	 * @param rootCloseIcon
	 *            根目录折叠时图标closedIcon,closedIconSelected
	 * @param rootOpenIcon
	 *            根目录展开时图标openIcon,openIconSelected
	 * @param leafIcon
	 *            叶目录图标leafIcon,leafIconSelected
	 */
	public void setTreeRootLeafIcon(ImageIcon rootCloseIcon, ImageIcon rootOpenIcon, ImageIcon leafIcon) {
		this.closedIcon = rootCloseIcon;
		this.closedIconSelected = rootCloseIcon;
		this.openIcon = rootOpenIcon;
		this.openIconSelected = rootOpenIcon;
		this.leafIcon = leafIcon;
		this.leafIconSelected = leafIcon;
	}

	/**
	 * 设置颜色
	 * 
	 * @param BackgroundSelectionColor
	 *            选中背景色
	 * @param ForegroundSelectionColor
	 *            选中前景色
	 * @param BackgroundNonSelectionColor
	 *            未选中时背景色
	 * @param ForegroundNonSelectionColor
	 *            未选中背景色
	 */
	public void setColor(Color BackgroundSelectionColor, Color ForegroundSelectionColor,
			Color BackgroundNonSelectionColor, Color ForegroundNonSelectionColor) {
		this.BackgroundSelectionColor = BackgroundSelectionColor;
		this.ForegroundSelectionColor = ForegroundSelectionColor;
		this.BackgroundNonSelectionColor = BackgroundNonSelectionColor;
		this.ForegroundNonSelectionColor = ForegroundNonSelectionColor;

	}

	

}
