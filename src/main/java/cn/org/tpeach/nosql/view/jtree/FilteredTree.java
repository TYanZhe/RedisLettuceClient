package cn.org.tpeach.nosql.view.jtree;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;

import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

class FilteredTree {
	private class PlainBelliedSneech {
		public String toString() {
			return "Plain Bellied Sneech";
		}
	}

	private class StarBelliedSneech {
		public String toString() {
			return "Star Bellied Sneech";
		}
	}

	private class FilteredTreeModel extends DefaultTreeModel {
		private boolean mShowStarBelliedSneeches = true;
		private DefaultMutableTreeNode mRoot;

		FilteredTreeModel(DefaultMutableTreeNode root) {
			super(root);
			mRoot = root;
		}

		public Object getChild(Object parent, int index) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) parent;

			if (mShowStarBelliedSneeches)
				return node.getChildAt(index);

			int pos = 0;
			for (int i = 0, cnt = 0; i < node.getChildCount(); i++) {
				if (((DefaultMutableTreeNode) node.getChildAt(i)).getUserObject() instanceof PlainBelliedSneech) {
					if (cnt++ == index) {
						pos = i;
						break;
					}
				}
			}

			return node.getChildAt(pos);
		}

		public int getChildCount(Object parent) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) parent;

			if (mShowStarBelliedSneeches)
				return node.getChildCount();

			int childCount = 0;
			Enumeration children = node.children();
			while (children.hasMoreElements()) {
				if (((DefaultMutableTreeNode) children.nextElement()).getUserObject() instanceof PlainBelliedSneech)
					childCount++;
			}

			return childCount;
		}

		public boolean getShowStarBelliedSneeches() {
			return mShowStarBelliedSneeches;
		}

		public void setShowStarBelliedSneeches(boolean showStarBelliedSneeches) {
			if (showStarBelliedSneeches != mShowStarBelliedSneeches) {
				mShowStarBelliedSneeches = showStarBelliedSneeches;
				Object[] path = { mRoot };
				int[] childIndices = new int[root.getChildCount()];
				Object[] children = new Object[root.getChildCount()];
				for (int i = 0; i < root.getChildCount(); i++) {
					childIndices[i] = i;
					children[i] = root.getChildAt(i);
				}
				fireTreeStructureChanged(this, path, childIndices, children);

			}
		}
	}

	private FilteredTree() {
		final DefaultMutableTreeNode root = new DefaultMutableTreeNode("Root");

		DefaultMutableTreeNode parent;
		DefaultMutableTreeNode child;

		for (int i = 0; i < 2; i++) {
			parent = new DefaultMutableTreeNode(new PlainBelliedSneech());
			root.add(parent);
			for (int j = 0; j < 2; j++) {
				child = new DefaultMutableTreeNode(new StarBelliedSneech());
				parent.add(child);
				for (int k = 0; k < 2; k++)
					child.add(new DefaultMutableTreeNode(new PlainBelliedSneech()));
			}
			for (int j = 0; j < 2; j++)
				parent.add(new DefaultMutableTreeNode(new PlainBelliedSneech()));

			parent = new DefaultMutableTreeNode(new StarBelliedSneech());
			root.add(parent);
			for (int j = 0; j < 2; j++) {
				child = new DefaultMutableTreeNode(new PlainBelliedSneech());
				parent.add(child);
				for (int k = 0; k < 2; k++)
					child.add(new DefaultMutableTreeNode(new StarBelliedSneech()));
			}
			for (int j = 0; j < 2; j++)
				parent.add(new DefaultMutableTreeNode(new StarBelliedSneech()));

		}

		final FilteredTreeModel model = new FilteredTreeModel(root);
		JTree tree = new JTree(model);
		tree.setShowsRootHandles(true);
		tree.putClientProperty("JTree.lineStyle", "Angled");
		tree.setRootVisible(false);

		JScrollPane sp = new JScrollPane(tree);
		sp.setPreferredSize(new Dimension(200, 400));

		final JCheckBox check = new JCheckBox("Show Star Bellied Sneeches");
		check.setSelected(model.getShowStarBelliedSneeches());

		check.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				model.setShowStarBelliedSneeches(check.isSelected());
			}
		});

		JPanel panel = new JPanel(new BorderLayout());
		panel.add(check, BorderLayout.NORTH);
		panel.add(sp, BorderLayout.CENTER);

		JOptionPane.showOptionDialog(null, panel, "Sneeches on Beeches", JOptionPane.DEFAULT_OPTION,
				JOptionPane.PLAIN_MESSAGE, null, new String[0], null);

		System.exit(0);
	}

	public static void main(String[] argv) {
		new FilteredTree();
	}
}