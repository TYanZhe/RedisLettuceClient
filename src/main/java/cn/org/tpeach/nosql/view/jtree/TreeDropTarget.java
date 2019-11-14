package cn.org.tpeach.nosql.view.jtree;

import cn.org.tpeach.nosql.redis.bean.RedisConnectInfo;
import cn.org.tpeach.nosql.redis.bean.RedisTreeItem;
import cn.org.tpeach.nosql.redis.service.IRedisConfigService;
import cn.org.tpeach.nosql.service.ServiceProxy;
import cn.org.tpeach.nosql.tools.StringUtils;
import cn.org.tpeach.nosql.view.common.ServiceManager;
import lombok.Getter;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.*;
import java.util.Iterator;
import java.util.Optional;
import java.util.Vector;

public class TreeDropTarget  implements DropTargetListener {
    IRedisConfigService redisConfigService = ServiceProxy.getBeanProxy("redisConfigService", IRedisConfigService.class);
    JTree targetTree;
    @Getter
    DropTarget target;
    public TreeDropTarget(JTree targetTree) throws HeadlessException {
        this.targetTree = targetTree;
        target = new DropTarget(targetTree, this);
    }
    /*
     * Drop Event Handlers
     */
    private TreeNode getNodeForEvent(DropTargetDragEvent dtde) {
        Point p = dtde.getLocation();
        DropTargetContext dtc = dtde.getDropTargetContext();
        JTree tree = (JTree) dtc.getComponent();
        TreePath path = tree.getClosestPathForLocation(p.x, p.y);
        return (TreeNode) path.getLastPathComponent();
    }
    @Override
    public void dragEnter(DropTargetDragEvent dtde) {
//        System.out.println("拖拽目标进入组件区域");
    }

    @Override
    public void dragOver(DropTargetDragEvent dtde) {
//                System.out.println("dragOver: 拖拽目标在组件区域内移动");
    }

    @Override
    public void dropActionChanged(DropTargetDragEvent dtde) {
//                System.out.println("dropActionChanged: 当前 drop 操作被修改");
    }

    @Override
    public void dragExit(DropTargetEvent dte) {
//                System.out.println("dragExit: 拖拽目标离开组件区域");
    }

    @Override
    public void drop(DropTargetDropEvent dtde) {
        Point pt = dtde.getLocation();
        DropTargetContext dtc = dtde.getDropTargetContext();
        JTree tree = (JTree) dtc.getComponent();
        TreePath parentpath = tree.getPathForLocation(pt.x, pt.y);
        //        TreePath parentpath = tree.getClosestPathForLocation(pt.x, pt.y);
        TreeNode targetNode = null;
        if(parentpath != null){
            targetNode = (DefaultMutableTreeNode) parentpath.getLastPathComponent();
        }
        if(targetNode == null){
            dtde.rejectDrop();
            return;
        }
        Object[] path = parentpath.getPath();
        if(path.length < 2){
            dtde.rejectDrop();
            return;
        }
        if(path.length > 2){
            int length = path.length;
            while (length>2){
                targetNode = targetNode.getParent();
                length-- ;
            }
        }

        try {
            Transferable tr = dtde.getTransferable();
            DataFlavor[] flavors = tr.getTransferDataFlavors();
            for (int i = 0; i < flavors.length; i++) {
                if (tr.isDataFlavorSupported(flavors[i])) {
                    dtde.acceptDrop(dtde.getDropAction());
                    RTreeNode node = (RTreeNode) tr.getTransferData(flavors[i]);
                    RTreeNode moveNode = moveNode(node, (RTreeNode) targetNode);
                    if(moveNode.getChildren() != null){
                        Vector children = moveNode.getChildren();
                        Iterator iterator = children.iterator();
                        while (iterator.hasNext()){
                            DefaultMutableTreeNode next = (DefaultMutableTreeNode) iterator.next();
                            next.setParent(moveNode);
                        }
                    }

                    Optional.ofNullable(ServiceManager.getInstance().findoOriginalRedisTreeNode(tree, (RTreeNode) targetNode)).ifPresent(r -> moveNode(node, r));
                    tree.updateUI();
                    dtde.dropComplete(true);
                    dtde.rejectDrop();
                    //更新配置
                    RedisTreeItem item = (RedisTreeItem) ((RTreeNode) targetNode).getUserObject();
                    RedisTreeItem moveNodeItem = (RedisTreeItem) node.getUserObject();
                    RedisConnectInfo targetConn = redisConfigService.getRedisConfigById(item.getId());
                    if(targetConn != null){
                        redisConfigService.moveRedisconfig(redisConfigService.getRedisConfigById(moveNodeItem.getId()),targetConn);
                    }
                    return;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            dtde.rejectDrop();
        }

    }

    private RTreeNode moveNode(RTreeNode node, RTreeNode targetNode) {
        DefaultMutableTreeNode parent = (DefaultMutableTreeNode) targetNode.getParent();
        RTreeNode newNode = RTreeNode.copyNode(node);
        newNode.setId(StringUtils.getUUID());
        newNode.setChildren(node.getChildren());
        parent.insert(newNode,parent.getIndex(targetNode)+1);
        return newNode;
    }
}
