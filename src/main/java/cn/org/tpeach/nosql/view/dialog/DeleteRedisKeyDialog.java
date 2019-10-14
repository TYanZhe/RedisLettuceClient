/**
 * 
 */
package cn.org.tpeach.nosql.view.dialog;

import cn.org.tpeach.nosql.controller.BaseController;
import cn.org.tpeach.nosql.controller.ResultRes;
import cn.org.tpeach.nosql.redis.bean.RedisConnectInfo;
import cn.org.tpeach.nosql.redis.bean.RedisTreeItem;
import cn.org.tpeach.nosql.redis.command.JedisCommand;
import cn.org.tpeach.nosql.redis.service.IRedisConfigService;
import cn.org.tpeach.nosql.redis.service.IRedisConnectService;
import cn.org.tpeach.nosql.service.ServiceProxy;
import cn.org.tpeach.nosql.tools.CollectionUtils;
import cn.org.tpeach.nosql.tools.StringUtils;
import cn.org.tpeach.nosql.tools.SwingTools;
import cn.org.tpeach.nosql.view.component.EasyJSP;
import cn.org.tpeach.nosql.view.component.OnlyReadArea;
import cn.org.tpeach.nosql.view.jtree.RTreeNode;
import io.lettuce.core.KeyScanCursor;
import lombok.Getter;
import lombok.Setter;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
　 * <p>Title: DeleteRedisKeyDialog.java</p> 
　 * @author taoyz 
　 * @date 2019年8月27日 
　 * @version 1.0 
 */
public class DeleteRedisKeyDialog extends BaseDialog<RTreeNode,Long>{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8995563355788221649L;
	IRedisConfigService redisConfigService = ServiceProxy.getBeanProxy("redisConfigService", IRedisConfigService.class);
	IRedisConnectService redisConnectService = ServiceProxy.getBeanProxy("redisConnectService", IRedisConnectService.class);
	Collection<byte[]> keys;
	String totalKeys;
	@Getter
	@Setter
	private String keyPattern;
	private RedisTreeItem redisTreeItem;
	private int db;
	public DeleteRedisKeyDialog(JFrame parent, boolean modal, Image icon, RTreeNode t) {
		super(parent, modal, icon, t);
	}

	public DeleteRedisKeyDialog(JFrame parent, boolean modal, RTreeNode t) {
		super(parent, modal, t);
	}

	public DeleteRedisKeyDialog(JFrame parent, Image icon, RTreeNode t) {
		super(parent, icon, t);
	}

	public DeleteRedisKeyDialog(JFrame parent, RTreeNode t) {
		super(parent, t);
		
	}


	@Override
	public void initDialog(RTreeNode node) {
		super.setMinWidth(800);
		super.setMinHeight(460);
		
		if(node == null) {
			SwingTools.showMessageErrorDialog(null,"未知错误，节点获取失败");
			this.isError = true;
		}
		this.redisTreeItem = (RedisTreeItem) t.getUserObject();
		this.keyPattern = redisTreeItem.getOriginName()+":*";
		CountDownLatch countDownLatch = new CountDownLatch(1);
		this.containerMap.put("countDownLatch",countDownLatch);
		LoadingDialog.showLoading(true,()->{
			try {
				countDownLatch.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		});
	}
	private JLabel getLable(String text,int horizontalAlignment ) {
		Font font = new Font("宋体",Font.PLAIN,14);
		JLabel label = new JLabel(text,horizontalAlignment);
		label.setFont(font);
		return label;
	}


	@Override
	protected void contextUiImpl(JPanel contextPanel, JPanel btnPanel) {
		try {
			ResultRes<KeyScanCursor<byte[]>> dispatcher = BaseController.dispatcher(() -> redisConnectService.getKeys(redisTreeItem.getId(), redisTreeItem.getDb(), keyPattern, true));
			if (dispatcher.isRet()) {
				keys = dispatcher.getData().getKeys();
				totalKeys = dispatcher.getData().getCursor();
			} else {
				this.isError = true;
				SwingTools.showMessageErrorDialog(null, "获取" + keyPattern + "失败");
			}
			RedisTreeItem item = (RedisTreeItem) t.getUserObject();
			if (CollectionUtils.isEmpty(keys)) {
				this.isError = true;
				SwingTools.showMessageErrorDialog(null, keyPattern + "匹配key数量为0");
				super.close();
				return;
			}

			RedisConnectInfo redisConfig = redisConfigService.getRedisConfigById(item.getId());


			JLabel titleLabel = new JLabel("Delete Keys", JLabel.LEFT);
			JLabel serverLabel = this.getLable("Redis Server:", JLabel.LEFT);
			JLabel dbIndexLabel = this.getLable("Database Index:", JLabel.LEFT);
			JLabel keyPatternLabel = this.getLable("Key Pattern:", JLabel.LEFT);
			JLabel affectedKeyLabel = this.getLable("show Keys:" + keys.size(), JLabel.LEFT);
			JLabel totalPattenLabel = this.getLable("Affected Keys:" + totalKeys, JLabel.LEFT);
			titleLabel.setPreferredSize(new Dimension(150, 28));
			serverLabel.setPreferredSize(new Dimension(150, 22));
			dbIndexLabel.setPreferredSize(new Dimension(150, 22));
			keyPatternLabel.setPreferredSize(new Dimension(150, 22));

			JLabel keyPatternTextLabel = this.getLable(keyPattern, JLabel.LEFT);
			JLabel serverTextLabel = this.getLable(redisConfig.getHost(), JLabel.LEFT);
			JLabel dbIndexTextLabel = this.getLable(item.getDb() + "", JLabel.LEFT);

			super.contextUiImpl(contextPanel, btnPanel);
			((JComponent) contextPanel.getParent()).setBorder(new EmptyBorder(10, 20, 35, 20));
			contextPanel.getParent().setBackground(Color.WHITE);
			btnPanel.setBackground(Color.WHITE);
			contextPanel.setLayout(new BorderLayout());
			JPanel topPanel = new JPanel();
			JPanel keysPanel = new JPanel();
			contextPanel.add(topPanel, BorderLayout.NORTH);
			contextPanel.add(keysPanel, BorderLayout.CENTER);
			topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
			topPanel.setBackground(Color.WHITE);

			Box hBox1 = Box.createHorizontalBox();
			hBox1.add(titleLabel);
			hBox1.add(Box.createHorizontalGlue());
			Box hBox2 = Box.createHorizontalBox();
			hBox2.add(affectedKeyLabel);
			hBox2.add(Box.createHorizontalGlue());
			Box hBox3 = Box.createHorizontalBox();
			hBox3.add(totalPattenLabel);
			hBox3.add(Box.createHorizontalGlue());
			topPanel.add(Box.createVerticalStrut(10));
			topPanel.add(hBox1);
			topPanel.add(new JSeparator(JSeparator.HORIZONTAL));
			topPanel.add(createRowBox(serverLabel, serverTextLabel));
			topPanel.add(createRowBox(dbIndexLabel, dbIndexTextLabel));
			topPanel.add(createRowBox(keyPatternLabel, keyPatternTextLabel));
			topPanel.add(Box.createVerticalStrut(5));


			topPanel.add(hBox2);
			topPanel.add(Box.createVerticalStrut(5));
			topPanel.add(hBox3);
			topPanel.add(Box.createVerticalStrut(5));
			OnlyReadArea textArea = new OnlyReadArea(10, 100, 1000);
			keys.forEach(s -> textArea.println(StringUtils.showHexStringValue(s)));
			JScrollPane scrollPane = new EasyJSP(textArea).hiddenHorizontalScrollBar();
			contextPanel.add(scrollPane);
		}finally {
			final CountDownLatch countDownLatch = (CountDownLatch) containerMap.get("countDownLatch");
			countDownLatch.countDown();
		}


	}
	
	
	@Override
	protected void submit(ActionEvent e) {
        int conform = SwingTools.showConfirmDialogYNC(null, "是否确认删除？", "删除确认");
        if(conform == JOptionPane.YES_OPTION){
            super.submit(()->{
                ResultRes<Long> dispatcher = BaseController.dispatcher(() ->redisConnectService.deleteKeys(redisTreeItem.getId(), redisTreeItem.getDb(),keyPattern));
                if(dispatcher.isRet()) {
                    consumer.accept(dispatcher.getData());
                }else {
                    SwingTools.showMessageErrorDialog(null,dispatcher.getMsg());
                }
                return !dispatcher.isRet();
            });
        }
	}

	private Box createRowBox(JLabel label,JLabel context) {
		Box box = Box.createHorizontalBox();
		box.add(label);
		box.add(context);
		box.add(Box.createHorizontalGlue());
		return box;
	}

	@Override
	public boolean isNeedBtn() {
		return true;
	}

	
}
