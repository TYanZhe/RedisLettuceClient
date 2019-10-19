package cn.org.tpeach.nosql.view.jtree;

import cn.org.tpeach.nosql.enums.RedisType;
import cn.org.tpeach.nosql.redis.bean.RedisTreeItem;
import cn.org.tpeach.nosql.tools.StringUtils;

import java.util.LinkedList;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.compile;

public class TreeNodeBuilder {

    private String textToMatch;
    private Pattern pattern;

    public TreeNodeBuilder(String textToMatch) {
        this.textToMatch = textToMatch;
        if(StringUtils.isNotBlank(textToMatch)){
            pattern = compile(".*" + textToMatch.trim().replaceAll("\\*", ".*") + ".*");
        }

    }

    public RTreeNode prune(RTreeNode root) {
        LinkedList<RTreeNode> list = new LinkedList<RTreeNode>();
        int childCount = root.getChildCount()-1;
        for (int i = childCount; i >= 0; i--) {
            RTreeNode node = (RTreeNode) root.getChildAt(i);
            if (node.getChildCount() == 0) {
                removeBadLeaves(node);
            } else {
                list.add(node);
            }
        }
        RTreeNode tempNode;
        while (!list.isEmpty()) {
            tempNode = list.removeFirst();
            int count = tempNode.getChildCount()-1;
            for (int i = count; i >= 0; i--) {
                RTreeNode node = (RTreeNode) tempNode.getChildAt(i);
                if (node.getChildCount() == 0) {
                    removeBadLeaves(node);
                } else {
                    list.add(node);
                }
            }
        }
        return root;
    }


    public void removeBadLeaves(RTreeNode node) {
        RedisTreeItem redisTreeItem = (RedisTreeItem) node.getUserObject();
        if (redisTreeItem.getType().equals(RedisType.KEY) ) {
            String name = redisTreeItem.getName();
            if(pattern!= null && !pattern.matcher(name).find()){
                RTreeNode parent = (RTreeNode) node.getParent();
                parent.remove(node);
                RedisTreeItem item = (RedisTreeItem) parent.getUserObject();
                while (parent.getChildCount() == 0 && !parent.isRoot() &&
                        (item.getType().equals(RedisType.KEY) || item.getType().equals(RedisType.KEY_NAMESPACE))){
                    node = parent;
                    parent = (RTreeNode) node.getParent();
                    parent.remove(node);
                    item = (RedisTreeItem) parent.getUserObject();
                }
            }

        }
    }
}