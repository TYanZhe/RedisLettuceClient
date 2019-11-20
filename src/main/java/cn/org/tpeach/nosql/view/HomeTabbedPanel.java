/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.org.tpeach.nosql.view;

import cn.org.tpeach.nosql.constant.PublicConstant;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/**
 *
 * @author tyz
 */
public class HomeTabbedPanel extends javax.swing.JPanel {

	private static final long serialVersionUID = -5054008338538674819L;
	private int index = 0;

	public HomeTabbedPanel() {

		this.setLayout(new GridLayout(2,1));
		JPanel topPanel = new JPanel();
		JPanel textPanel = new JPanel();
		topPanel.setBackground(Color.WHITE);
		textPanel.setBackground(Color.WHITE);
		this.add(topPanel);
		this.add(textPanel);
		ImageIcon imageIcon = new ImageIcon();
		JLabel jLabel  = new  JLabel();
		jLabel.setIcon(imageIcon);
		jLabel.setBorder(BorderFactory.createEmptyBorder(0, 30, 0, 10));
		topPanel.setLayout(new BoxLayout(topPanel,BoxLayout.X_AXIS));
		topPanel.add(jLabel);
		topPanel.add(Box.createHorizontalGlue());
		ComponentAdapter componentAdapter = new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				if(index > 0){
					topPanel.removeComponentListener(this);
					return;
				}
				imageIcon.setImage(PublicConstant.Image.getImageIcon(PublicConstant.Image.logo).getImage().getScaledInstance(topPanel.getHeight(), topPanel.getHeight(),
						Image.SCALE_DEFAULT));
				topPanel.updateUI();
				index++;

			}
		};
		topPanel.addComponentListener(componentAdapter);
		textPanel.setLayout(new GridLayout());
		JTextArea area = new JTextArea();
		area.setText("Redis is an open source (BSD licensed), in-memory data structure store, used as a database, cache and message broker. It supports data structures such as strings, hashes, lists, sets, sorted sets with range queries, bitmaps, hyperloglogs, geospatial indexes with radius queries and streams. Redis has built-in replication, Lua scripting, LRU eviction, transactions and different levels of on-disk persistence, and provides high availability via Redis Sentinel and automatic partitioning with Redis Cluster");
		area.setLineWrap(true);
		area.setEditable(false);
		area.setFont(new Font("宋体",Font.PLAIN,18));
		area.setForeground(new Color(153,153,153));
		area.setBorder(BorderFactory.createEmptyBorder(25, 10, 0, 10));
		textPanel.add(area);

	}




}
