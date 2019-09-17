/**
 *
 */
package cn.org.tpeach.nosql.view.dialog;

import cn.org.tpeach.nosql.controller.BaseController;
import cn.org.tpeach.nosql.controller.ResultRes;
import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import cn.org.tpeach.nosql.redis.bean.RedisKeyInfo;
import cn.org.tpeach.nosql.redis.service.IRedisConnectService;
import cn.org.tpeach.nosql.service.ServiceProxy;
import cn.org.tpeach.nosql.tools.StringUtils;
import cn.org.tpeach.nosql.tools.SwingTools;
import cn.org.tpeach.nosql.view.component.PlaceholderTextField;
import cn.org.tpeach.nosql.view.component.RTextArea;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import lombok.Setter;

/**
 * <p>
 * Title: AddRowDialog.java</p>
 *
 * @author taoyz
 * @date 2019年9月4日
 * @version 1.0
 */
public class AddRowDialog extends KeyDialog<RedisKeyInfo, RedisKeyInfo> {

    /**
     *
     */
    private static final long serialVersionUID = 2648311460220989594L;
    private JPanel panel, textAreaPanel, scorePanel;
    private JLabel fieldHashLabel, scoreLable, valueLabel, valueHashLabel;
    private PlaceholderTextField scoreField;
    private RTextArea valueArea, valueHashArea, hashKeyArea;
    IRedisConnectService redisConnectService = ServiceProxy.getBeanProxy("redisConnectService", IRedisConnectService.class);
    @Setter
    private boolean isLeftList;
    /**
     * @param parent
     * @param t
     */
    public AddRowDialog(JFrame parent, RedisKeyInfo t) {
        super(parent, t);

    }

    @Override
    public void initDialog(RedisKeyInfo t) {
        this.setTitle("添加行");
        if (t == null) {
            this.isError = true;
        }

    }
    @Override
	public void setMinimumSize() {
		this.setMinimumSize(getAdaptDialogMinimumSize(minWidth,minHeight));
	}
    @Override
    protected void contextUiImpl(JPanel contextPanel, JPanel btnPanel) {
        super.contextUiImpl(contextPanel, btnPanel);
        panel = new JPanel();
        scoreLable = new JLabel("数据分数:", JLabel.RIGHT);
        valueLabel = new JLabel("数据键值:", JLabel.RIGHT);
        valueHashLabel = new JLabel("散列键值:", JLabel.RIGHT);
        fieldHashLabel = new JLabel("散列字段:", JLabel.RIGHT);

        scoreField = new PlaceholderTextField(20);
        scoreField.setPlaceholder("score");
        hashKeyArea = new RTextArea(4, 20);
        hashKeyArea.setLineWrap(true);
        valueHashArea = new RTextArea(4, 20);
        valueHashArea.setLineWrap(true);

        switch (t.getType()) {
            case STRING:
                break;
            case LIST:
            case SET:
                valueArea = new RTextArea(11, 20);
                textAreaPanel = (JPanel) SwingTools.createTextRow(valueLabel, valueArea.getJScrollPane(), 0.2, 0.8, this.getWidth(), rowHeight * 6, null, new Insets(10, 0, 0, 0), new Insets(10, 10, 0, 30));
                panel.add(textAreaPanel);
                break;
            case HASH:
                panel.add(SwingTools.createTextRow(fieldHashLabel, hashKeyArea.getJScrollPane(), this.getWidth(), (int) (rowHeight * 3)));
                JPanel valueHashAreaPanel = (JPanel) SwingTools.createTextRow(valueHashLabel, valueHashArea.getJScrollPane(), 0.3, 0.7, this.getWidth(), (int) (rowHeight * 3), null, new Insets(13, 10, 0, 0), new Insets(13, 10, 0, 30));
                panel.add(valueHashAreaPanel);
            case ZSET:
                valueArea = new RTextArea(8, 20);
                scorePanel = SwingTools.createTextRow(scoreLable, scoreField, this.getWidth(), rowHeight);
                panel.add(scorePanel);
                textAreaPanel = (JPanel) SwingTools.createTextRow(valueLabel, valueArea.getJScrollPane(), this.getWidth(), rowHeight * 5);
                panel.add(textAreaPanel);

                break;
            default:
                break;
        }

//		hashTextAreaPanel = (JPanel) createHashTextAreaRow(valueHashLabel, hashKeyField,valueHashArea.getJScrollPane());
//		panel.add(hashTextAreaPanel);
        contextPanel.setLayout(new BorderLayout());
        contextPanel.add(panel, BorderLayout.CENTER);
    }

    @Override
    protected void submit(ActionEvent e) {
        if (consumer == null) {
            SwingTools.showMessageErrorDialog(this, "未绑定回调事件");
            return;
        }
        switch (t.getType()) {
            case ZSET:
                if (StringUtils.isBlank(scoreField.getText())) {
                    SwingTools.showMessageErrorDialog(this, "请输入分数");
                    return;
                }
                try {
                    t.setScore(Double.valueOf(scoreField.getText()));
                } catch (NumberFormatException ez) {
                    SwingTools.showMessageErrorDialog(this, "请输入正确的分数");
                    return;
                }
            case STRING:
            case LIST:
            case SET:
                if (StringUtils.isBlank(valueArea.getText())) {
                    SwingTools.showMessageErrorDialog(this, "请输入键值");
                    return;
                }
                t.setValue(valueArea.getText());
                break;
            case HASH:
                if (StringUtils.isBlank(valueHashArea.getText())) {
                    SwingTools.showMessageErrorDialog(this, "请输入键值");
                    return;
                }
                if (StringUtils.isBlank(hashKeyArea.getText())) {
                    SwingTools.showMessageErrorDialog(this, "请输入Field");
                    return;
                }
                t.setField(hashKeyArea.getText());
                t.setValue(valueHashArea.getText());
                break;
 
        }
        this.okBtn.setEnabled(false);
        ResultRes<?> res = BaseController.dispatcher(() -> redisConnectService.addRowKeyInfo(t,isLeftList));
        if (res.isRet()) {
            consumer.accept(t);
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
