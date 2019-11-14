package cn.org.tpeach.nosql.view.jtree;

import cn.org.tpeach.nosql.tools.SerializedClone;
import cn.org.tpeach.nosql.view.common.ServiceManager;

import javax.swing.*;
import javax.swing.plaf.TreeUI;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.*;
import java.io.IOException;
import java.util.Optional;

class TreeDragSourceListener implements DragSourceListener{
    JDialog dragComponent;
    Component treeCellRendererComponent;
    DefaultMutableTreeNode treeNode;
    JTree sourceTree;
    public TreeDragSourceListener(JTree tree,TreePath path,JDialog dragComponent) {
        this.sourceTree = tree;
        TreeCellRenderer cellRenderer = tree.getCellRenderer();
        TreeUI ui = tree.getUI();
        treeNode = (DefaultMutableTreeNode) path.getLastPathComponent();
        Component c = cellRenderer.getTreeCellRendererComponent(tree, treeNode,
                tree.isPathSelected(path), tree.isExpanded(path), treeNode.isLeaf(), ui.getRowForPath(tree, path), false);
        treeCellRendererComponent = SerializedClone.clone(c);
        this.dragComponent = dragComponent;
        Container contentPane = dragComponent.getContentPane();
        contentPane.removeAll();
        contentPane.add(treeCellRendererComponent);
    }

    @Override
    public void dragEnter(DragSourceDragEvent dsde) {
        DragSourceContext dsc = dsde.getDragSourceContext(); //得到拖拽源的上下文引用
        //设置拖拽时的光标形状
        int action = dsde.getDropAction();
        dsc.setCursor(DragSource.DefaultMoveDrop);
        dragComponent.setVisible(true);
    }

    @Override
    public void dragOver(DragSourceDragEvent dsde) {
        Dimension preferredSize = treeCellRendererComponent.getPreferredSize();
        dragComponent.setLocation(dsde.getX()-10,dsde.getY()-10);
        if(preferredSize.width != 0){
            dragComponent.setBounds(dsde.getX()+10,dsde.getY()-((preferredSize.height+10)/2),preferredSize.width+10,preferredSize.height+10);
        }else{
            dragComponent.setLocation(dsde.getX()-10,dsde.getY()-10);
        }
    }

    @Override
    public void dropActionChanged(DragSourceDragEvent dsde) {

    }

    @Override
    public void dragExit(DragSourceEvent dse) {

    }

    @Override
    public void dragDropEnd(DragSourceDropEvent dsde) {
        dragComponent.setVisible(false);
        dragComponent.dispose();

        if (dsde.getDropSuccess() && (dsde.getDropAction() == DnDConstants.ACTION_MOVE)) {
            DefaultTreeModel model = (DefaultTreeModel) sourceTree.getModel();
            model.removeNodeFromParent(treeNode);
            Optional.ofNullable(ServiceManager.getInstance().findoOriginalRedisTreeNode(sourceTree, (RTreeNode) treeNode)).ifPresent(r -> model.removeNodeFromParent(r));
            return;
        }

    }
}
public class TreeDragSource extends DragSource implements   DragGestureListener {
    public static DataFlavor TREE_PATH_FLAVOR = new DataFlavor(TreeNode.class, "TreeNode");
    JTree sourceTree;
    DefaultMutableTreeNode oldNode;

    public TreeDragSource(JTree sourceTree, int actions) throws HeadlessException {
        this.sourceTree = sourceTree;
        super.createDefaultDragGestureRecognizer(sourceTree, actions, this);

    }

    @Override
    public void dragGestureRecognized(DragGestureEvent dge) {
        // 获取点击所在树节点路径
        TreePath pathForLocation = sourceTree.getPathForLocation(dge.getDragOrigin().x, dge.getDragOrigin().y);
        if(pathForLocation == null){
            return;
        }
        sourceTree.setSelectionPath(pathForLocation);
        TreePath treePath = sourceTree.getSelectionPath();
        if ((treePath == null) || (treePath.getPathCount() <= 1)) {
            // We can't move the root node or an empty selection
            return;
        }
        Object[] path = pathForLocation.getPath();
        if(path.length != 2){
            return;
        }
        oldNode = (DefaultMutableTreeNode) treePath.getLastPathComponent();
        Transferable transferable = new Transferable() {
            @Override
            public DataFlavor[] getTransferDataFlavors() {
                return new DataFlavor[]{TREE_PATH_FLAVOR};
            }

            @Override
            public boolean isDataFlavorSupported(DataFlavor flavor) {
                return flavor.getRepresentationClass() == TreeNode.class;
            }

            @Override
            public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
                if (isDataFlavorSupported(flavor)) {
                    return  oldNode;
                } else {
                    throw new UnsupportedFlavorException(flavor);
                }
            }
        };
        JDialog dragComponent = new JDialog();
        dragComponent.setModal(false);
        dragComponent.setUndecorated(true);
        dragComponent.setSize(50,30);
        super.startDrag(dge, DragSource.DefaultMoveNoDrop, transferable, new TreeDragSourceListener(sourceTree,treePath,dragComponent));
    }

}
