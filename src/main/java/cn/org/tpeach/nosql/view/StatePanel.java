package cn.org.tpeach.nosql.view;

import lombok.Getter;
import lombok.Setter;

import javax.swing.*;
import java.awt.*;

@Getter
@Setter
public class StatePanel extends JPanel {
    private  JLabel connectStateLabel;
    private JLabel redisServerVersionLabel;
    private JLabel clientCountLabel;


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
}
