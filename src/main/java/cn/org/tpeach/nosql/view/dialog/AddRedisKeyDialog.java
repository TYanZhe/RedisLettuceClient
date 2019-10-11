/**
 *
 */
package cn.org.tpeach.nosql.view.dialog;

import cn.org.tpeach.nosql.controller.BaseController;
import cn.org.tpeach.nosql.controller.ResultRes;
import cn.org.tpeach.nosql.enums.RedisType;
import cn.org.tpeach.nosql.redis.bean.RedisKeyInfo;
import cn.org.tpeach.nosql.redis.bean.RedisTreeItem;
import cn.org.tpeach.nosql.redis.command.JedisCommand;
import cn.org.tpeach.nosql.redis.service.IRedisConnectService;
import cn.org.tpeach.nosql.service.ServiceProxy;
import cn.org.tpeach.nosql.tools.StringUtils;
import cn.org.tpeach.nosql.tools.SwingTools;
import cn.org.tpeach.nosql.view.component.PlaceholderTextField;
import cn.org.tpeach.nosql.view.component.RComboBox;
import cn.org.tpeach.nosql.view.component.RTextArea;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * <p>
 * Title: AddRedisKeyDialog.java
 * </p>
 *
 * @author taoyz @date 2019年8月24日 @version 1.0
 */
public class AddRedisKeyDialog extends KeyDialog<RedisTreeItem, RedisKeyInfo> {

    /**
     *
     */
    private static final long serialVersionUID = -8149532555947009204L;
    protected int minHeight = 300;
    private int strSetListWidth = 390;
    private int zSetHashWidth = 433;
    // 组件
    private JPanel panel, textAreaPanel, scorePanel, hashTextAreaPanel;
    private JLabel keyLable, ttlLable, typeLable, scoreLable, valueLabel, valueHashLabel;

    private PlaceholderTextField keyField, ttlField, scoreField, hashKeyField;
    private JComboBox<RedisType> typeField;
    private RTextArea valueArea, valueHashArea;
    private RedisTreeItem treeItem;
    IRedisConnectService redisConnectService = ServiceProxy.getBeanProxy("redisConnectService",
            IRedisConnectService.class);

    public AddRedisKeyDialog(JFrame parent, Image icon, RedisTreeItem t) {
        super(parent, icon, t);
    }

    public AddRedisKeyDialog(JFrame parent, RedisTreeItem t) {
        super(parent, t);
    }

    @Override
    protected void contextUiImpl(JPanel contextPanel, JPanel btnPanel) {
        super.contextUiImpl(contextPanel, btnPanel);
        panel = new JPanel();
        keyLable = new JLabel("数据键名:", JLabel.RIGHT);
        ttlLable = new JLabel("数据时效:", JLabel.RIGHT);
        typeLable = new JLabel("数据类型:", JLabel.RIGHT);
        scoreLable = new JLabel("数据分数:", JLabel.RIGHT);
        valueLabel = new JLabel("数据键值:", JLabel.RIGHT);
        valueHashLabel = new JLabel("数据键值:", JLabel.RIGHT);

        keyField = new PlaceholderTextField(20);
        keyField.setPlaceholder("key");
        ttlField = new PlaceholderTextField("-1", 20);
        ttlField.setPlaceholder("ttl,单位：秒");
        RedisType[] redisType = {RedisType.STRING, RedisType.LIST, RedisType.SET,
            RedisType.ZSET, RedisType.HASH};
        typeField = new RComboBox<>(redisType);
        scoreField = new PlaceholderTextField(20);
        scoreField.setPlaceholder("score");
        valueArea = new RTextArea(5, 20);
        // 文本域中的文本为自动换行
        valueArea.setLineWrap(true);
        valueHashArea = new RTextArea(5, 20);
        hashKeyField = new PlaceholderTextField(20);
        hashKeyField.setPlaceholder("field");

        panel.add(SwingTools.createTextRow(keyLable, keyField, this.getWidth(), rowHeight));
        panel.add(SwingTools.createTextRow(ttlLable, ttlField, this.getWidth(), rowHeight));
        panel.add(SwingTools.createTextRow(typeLable,typeField,0.28,0.72,this.getWidth(),rowHeight,null,new Insets(10, 10, 0, 0),new Insets(10, 10, 0, 30)));

        scorePanel = SwingTools.createTextRow(scoreLable, scoreField, this.getWidth(), rowHeight);
        panel.add(scorePanel);
        textAreaPanel = SwingTools.createTextRow(valueLabel,valueArea.getJScrollPane(),0.28,0.72,this.getWidth(), (int) (rowHeight * 3.5),null,new Insets(10, 10, 0, 0),new Insets(10, 10, 0, 30));

        panel.add(textAreaPanel);
        hashTextAreaPanel = (JPanel) createHashTextAreaRow(valueHashLabel, hashKeyField, valueHashArea.getJScrollPane());
        panel.add(hashTextAreaPanel);

        contextPanel.setLayout(new BorderLayout());
        contextPanel.add(panel, BorderLayout.CENTER);

        //添加默认string
        changeType(RedisType.STRING);
        // 监听事件

        typeField.addItemListener(e -> changeType((RedisType) e.getItem()));
    }

    /* (non-Javadoc)
	 * @see cn.org.tpeach.nosql.view.dialog.BaseDialog#initDialog(java.lang.Object)
     */
    @Override
    public void initDialog(RedisTreeItem treeItem) {
        this.setTitle("新增Key");
        if (treeItem == null || StringUtils.isBlank(treeItem.getId())) {
            SwingTools.showMessageErrorDialog(this, "id未绑定");
            this.isError = true;
        }
        this.treeItem = treeItem;
    }
    @Override
	public void setMinimumSize() {
        this.setMinHeight(minHeight);
        this.setMinWidth(minWidth);
		super.setMinimumSize();
	}
    private void changeType(RedisType redisType) {
        switch (redisType) {
            case STRING:
            case LIST:
            case SET:
                this.setSize(1, strSetListWidth);
                this.scorePanel.setVisible(false);
                this.hashTextAreaPanel.setVisible(false);
                this.textAreaPanel.setVisible(true);

                break;
            case HASH:
                this.setSize(1, zSetHashWidth);
                this.scorePanel.setVisible(false);
                this.hashTextAreaPanel.setVisible(true);
                this.textAreaPanel.setVisible(false);
                this.setSize(1, zSetHashWidth);
                break;
            case ZSET:
                this.setSize(1, zSetHashWidth);
                this.scorePanel.setVisible(true);
                this.hashTextAreaPanel.setVisible(false);
                this.textAreaPanel.setVisible(true);
                break;
            default:
                break;
        }
    }

    @Override
    protected void submit(ActionEvent e) {
        if (consumer == null) {
            SwingTools.showMessageErrorDialog(this, "未绑定回调事件");
            return;
        }
        String key = keyField.getText();
        Long ttl = -1L;
        try {
            ttl = Long.valueOf(ttlField.getText());
            if (ttl != -1 && ttl < 0) {
                SwingTools.showMessageErrorDialog(this, "请输入大于0的失效，不设置为-1");
                return;
            }
        } catch (NumberFormatException ex) {
            SwingTools.showMessageErrorDialog(this, "请输入正确时效");
            return;
        }
        RedisType type = (RedisType) typeField.getSelectedItem();
        String score = scoreField.getText();
        String value = valueArea.getText();
        String filedHash = hashKeyField.getText();
        String valueHash = valueHashArea.getText();

        if (StringUtils.isBlank(key)) {
            SwingTools.showMessageErrorDialog(this, "请输入键名");
            return;
        } else if (RedisType.UNKNOWN == type) {
            SwingTools.showMessageErrorDialog(this, "请选择类型");
            return;
        }

        RedisKeyInfo keyInfo = new RedisKeyInfo();
        keyInfo.setId(treeItem.getId());
        keyInfo.setDb(treeItem.getDb());
        keyInfo.setKey(StringUtils.strToByte(key));
        keyInfo.setTtl(ttl);
        keyInfo.setType(type);
        switch (type) {
            case ZSET:
                if (StringUtils.isBlank(score)) {
                    SwingTools.showMessageErrorDialog(this, "请输入分数");
                    return;
                }
                try {
                    keyInfo.setScore(Double.valueOf(score));
                } catch (NumberFormatException ez) {
                    SwingTools.showMessageErrorDialog(this, "请输入正确的分数");
                    return;
                }
            case STRING:
            case LIST:
            case SET:
                if (StringUtils.isBlank(value)) {
                    SwingTools.showMessageErrorDialog(this, "请输入键值");
                    return;
                }
                keyInfo.setValue(StringUtils.strToByte(value));
                break;
            case HASH:
                keyInfo.setValue(StringUtils.strToByte(valueHash));
                keyInfo.setField(StringUtils.strToByte(filedHash));
                break;
        }
        this.okBtn.setEnabled(false);
        ResultRes<?> res = BaseController.dispatcher(() -> redisConnectService.addSingleKeyInfo(keyInfo));
        if (res.isRet()) {
            consumer.accept(keyInfo);
            this.dispose();
        } else {
            SwingTools.showMessageErrorDialog(this, "未知错误：添加失败");
        }

        this.okBtn.setEnabled(true);
    }

	@Override
	public boolean isNeedBtn() {
		return true;
	}

}
