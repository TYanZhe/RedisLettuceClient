package cn.org.tpeach.nosql.view;

import cn.org.tpeach.nosql.constant.RedisInfoKeyConstant;
import cn.org.tpeach.nosql.redis.bean.RedisTreeItem;
import cn.org.tpeach.nosql.redis.service.IRedisConnectService;
import cn.org.tpeach.nosql.service.ServiceProxy;
import cn.org.tpeach.nosql.tools.MapUtils;
import cn.org.tpeach.nosql.tools.StringUtils;
import lombok.Getter;
import lombok.Setter;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

@Getter
@Setter
public class StatePanel extends JPanel {
    private  JLabel connectStateLabel;
    private JLabel redisServerVersionLabel;
    private JLabel clientCountLabel;

    IRedisConnectService redisConnectService = ServiceProxy.getBeanProxy("redisConnectService", IRedisConnectService.class);
    public StatePanel( ) {
        this.connectStateLabel = getLable();
        this.redisServerVersionLabel = getLable();
        this.clientCountLabel = getLable();

        this.connectStateLabel.setForeground(new java.awt.Color(255, 0, 51));
        this.connectStateLabel.setText("未连接到服务");




        this.addStrut(connectStateLabel);
        this.addStrut(clientCountLabel);
        this.add(Box.createHorizontalGlue());
        //版本信息
        this.add(redisServerVersionLabel);
        this.add(Box.createHorizontalStrut(5));

    }
    private void addStrut(Component comp){
        this.add(Box.createHorizontalStrut(5));
        this.add(comp);

    }
    private JLabel getLable(){
        JLabel label = new JLabel();
        label.setFont(new Font("黑体",Font.PLAIN,14));
        label.setForeground(Color.BLUE.brighter().brighter());
        return label;
    }

    public void doUpdateStatus(RedisTreeItem redisTreeItem){
        Map<String, String> connectInfo = redisConnectService.getConnectInfo(redisTreeItem.getId());
        if(MapUtils.isNotEmpty(connectInfo)){
            //设置连接
            this.connectStateLabel.setForeground(Color.GREEN.darker().darker());
            String connectName = redisTreeItem.getName();
            RedisTreeItem tempItem = redisTreeItem;
            while (tempItem.getParentItem() != null){
                tempItem = tempItem.getParentItem();
                if(tempItem.getParentItem() == null){
                    connectName = tempItem.getName();
                }
            }
            this.connectStateLabel.setText("已成功连接到:"+connectName);
            //版本信息
            String version = connectInfo.get(RedisInfoKeyConstant.redisVersion);
            if(StringUtils.isNotBlank(version)){
                this.redisServerVersionLabel.setText("Redis版本:"+version);
            }
            String connectedClients = connectInfo.get(RedisInfoKeyConstant.connectedClients);
            if(StringUtils.isNotBlank(version)){
                this.clientCountLabel.setText("客户端数量:"+connectedClients);
            }
        }else{
            this.connectStateLabel.setForeground(new java.awt.Color(255, 0, 51));
            this.connectStateLabel.setText("未连接到服务");
            this.clientCountLabel.setText("");
            this.redisServerVersionLabel.setText("");
        }
    }
}
