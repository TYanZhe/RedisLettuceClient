package cn.org.tpeach.nosql.view;

import cn.org.tpeach.nosql.controller.BaseController;
import cn.org.tpeach.nosql.controller.ResultRes;
import cn.org.tpeach.nosql.framework.LarkFrame;
import cn.org.tpeach.nosql.redis.bean.RedisTreeItem;
import cn.org.tpeach.nosql.redis.command.AbstractLarkRedisPubSubListener;
import cn.org.tpeach.nosql.redis.connection.RedisLarkPool;
import cn.org.tpeach.nosql.redis.service.IRedisConnectService;
import cn.org.tpeach.nosql.service.ServiceProxy;
import cn.org.tpeach.nosql.tools.StringUtils;
import cn.org.tpeach.nosql.tools.SwingTools;
import cn.org.tpeach.nosql.view.component.OnlyReadTextPane;
import cn.org.tpeach.nosql.view.component.PlaceholderTextField;
import cn.org.tpeach.nosql.view.component.RButton;
import cn.org.tpeach.nosql.view.component.RTextArea;
import cn.org.tpeach.nosql.view.jtree.RTreeNode;
import io.lettuce.core.cluster.models.partitions.RedisClusterNode;
import lombok.Getter;

import javax.swing.*;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.util.concurrent.TimeUnit;

/**
 * @author tyz
 * @Title: PubSubPanel
 * @ProjectName RedisLark
 * @Description: TODO
 * @date 2019-11-27 19:36
 * @since 1.0.0
 */
public class PubSubPanel extends JPanel {
    @Getter
    private String connectId;
    @Getter
    private RTreeNode node;
    private RTextArea textArea = new RTextArea();
    private OnlyReadTextPane messageTextPane= new OnlyReadTextPane();
    private PlaceholderTextField channelTextField = new PlaceholderTextField(20);
    private JLabel messageLabel = new JLabel( );
    private RButton sendButton = new RButton("发送");
    private IRedisConnectService redisConnectService = ServiceProxy.getBeanProxy("redisConnectService", IRedisConnectService.class);

    private AbstractLarkRedisPubSubListener larkRedisPubSubListener = new AbstractLarkRedisPubSubListener<byte[], byte[]>() {
        @Override
        public void message(RedisClusterNode node, byte[] channel, byte[] message) {
            System.out.println("1 message:"+message);
        }

        @Override
        public void message(RedisClusterNode node, byte[] pattern, byte[] channel, byte[] message) {
            messageTextPane.println("[pattern:"+StringUtils.byteToStr(pattern)+",channel:"+StringUtils.byteToStr(channel)+ "]("+node.getUri().getPort()+"): " +StringUtils.byteToStr(message));
        }

        @Override
        public void subscribed(RedisClusterNode node, byte[] channel, long count) {
            System.out.println("3 subscribed:"+count);
        }

        @Override
        public void psubscribed(RedisClusterNode node, byte[] pattern, long count) {
            messageTextPane.println("Psubscribed("+  node.getUri().getPort()+") "+StringUtils.byteToStr(pattern)+" ["+count+"] success...");

        }

        @Override
        public void unsubscribed(RedisClusterNode node, byte[] channel, long count) {
            System.out.println("5 unsubscribed:"+count);
        }

        @Override
        public void punsubscribed(RedisClusterNode node, byte[] pattern, long count) {

            messageTextPane.println("Punsubscribed("+  node.getUri().getPort()+") "+StringUtils.byteToStr(pattern)+" ["+count+"] success...",Color.ORANGE.darker().darker());
        }


        @Override
        public void message(byte[] pattern, byte[] channel, byte[] message) {
            messageTextPane.println("[pattern:"+StringUtils.byteToStr(pattern)+",channel:"+StringUtils.byteToStr(channel)+ "]: " +StringUtils.byteToStr(message));
        }

        @Override
        public void subscribed(byte[] channel, long count) {
            System.out.println("8 subscribed:"+count);
        }

        @Override
        public void psubscribed(byte[] pattern, long count) {
            messageTextPane.println("Psubscribe "+StringUtils.byteToStr(pattern)+" ["+count+"] success...");
        }

        @Override
        public void unsubscribed(byte[] channel, long count) {
            System.out.println("10 unsubscribed:"+count);
        }
        @Override
        public void message(byte[] channel, byte[] message) {
            System.out.println("11 message:"+message);
        }

        @Override
        public void punsubscribed(byte[] pattern, long count) {
            messageTextPane.println("Punsubscribed "+StringUtils.byteToStr(pattern)+" ["+count+"] success...",Color.ORANGE.darker().darker());
        }
    };


    public PubSubPanel(RTreeNode node) {
        RedisTreeItem item = (RedisTreeItem) node.getUserObject();
        this.node = node;
        this.connectId = item.getId();
        this.setLayout(new BorderLayout());
        //消息内容
        JPanel messagePanel = new JPanel();
        messagePanel.setLayout(new BorderLayout());

        JPanel subscribePane = initSubscribePanel();

        Color color = new Color(245,245,245);
        messageTextPane.setCandy(color);
        messageTextPane.setBackground(color);
        messagePanel.add(messageTextPane.getJsp(),BorderLayout.CENTER);
        messagePanel.add(subscribePane,BorderLayout.EAST);

        //发送消息
        JPanel textTopPanel = new JPanel();

        JCheckBox enterSendCheck = new JCheckBox("回车发送");
        RButton clearButton = new RButton("清屏");
        clearButton.addActionListener(e->messageTextPane.setText(""));
        enterSendCheck.setSelected(true);
        enterSendCheck.setBackground(Color.WHITE);
        channelTextField.setMaximumSize(new Dimension(255,28));
        textTopPanel.setPreferredSize(new Dimension(0,28));
        textTopPanel.setBackground(Color.WHITE);
        textTopPanel.setLayout(new BoxLayout(textTopPanel,BoxLayout.X_AXIS));
        textTopPanel.add(Box.createHorizontalStrut(5));
        textTopPanel.add(new JLabel("发送频道 :"));
        textTopPanel.add(Box.createHorizontalStrut(10));
        textTopPanel.add(channelTextField);
        textTopPanel.add(Box.createHorizontalStrut(10));
        textTopPanel.add(enterSendCheck);
        textTopPanel.add(Box.createHorizontalStrut(10));
        textTopPanel.add(clearButton);
        textTopPanel.add(Box.createHorizontalGlue());
        textTopPanel.add(messageLabel);
        messageLabel.setForeground(Color.RED);
        textTopPanel.add(Box.createHorizontalStrut(15));
        textTopPanel.setBorder(BorderFactory.createEmptyBorder(5,0,2,0));

        JPanel textPanel = new JPanel();

        textPanel.setLayout(new BorderLayout());
        textPanel.setBackground(Color.WHITE);
        textPanel.setPreferredSize(new Dimension(0,150));

        textArea.setBorder(BorderFactory.createEmptyBorder(5,5,0,5));
        JPanel borderPanel = new JPanel();

        borderPanel.setBorder(BorderFactory.createEmptyBorder(5,0,5,0));
        borderPanel.setBackground(Color.WHITE);
        borderPanel.setPreferredSize(new Dimension(0,38));
        borderPanel.setLayout(new BoxLayout(borderPanel,BoxLayout.X_AXIS));
        borderPanel.add(Box.createHorizontalGlue());
        borderPanel.add(sendButton);
        borderPanel.add(Box.createHorizontalStrut(15));
        textPanel.add(textTopPanel,BorderLayout.PAGE_START);
        textArea.setLineWrap(true);
        JScrollPane jScrollPane = textArea.getJScrollPane();
        jScrollPane.setBorder(BorderFactory.createEmptyBorder());
        textPanel.add(jScrollPane,BorderLayout.CENTER);
        textPanel.add(borderPanel,BorderLayout.PAGE_END);

        textArea.requestFocus();
        SwingTools.enterPressesWhenFocused(textArea,e-> sendMessage(enterSendCheck.isSelected()));
        sendButton.addActionListener(e-> sendMessage(true));
        this.add(messagePanel,BorderLayout.CENTER);
        this.add(textPanel,BorderLayout.PAGE_END);
        BaseController.dispatcher(()->{
            redisConnectService.ping(connectId);
            redisConnectService.addListener(connectId,larkRedisPubSubListener);
            return null;
        },false,true);
    }
    public void close() {
        if(this.node.getChildCount()<=0){
            RedisLarkPool.connectMap.remove(node);
            RedisLarkPool.destory(connectId);
        }else{
            RedisLarkPool.closePubSub(connectId);
        }
    }
    private JPanel initSubscribePanel() {
        JPanel subscribePane = new JPanel();
        subscribePane.setPreferredSize(new Dimension(230,0));
        subscribePane.setBackground(Color.WHITE);
        subscribePane.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(0,0,1,0),
                BorderFactory.createMatteBorder(0,0,1,0,Color.GRAY)));
        RButton psubscribeButton = new RButton("PSUBSCRIBE ");
        RButton  punsubscribeButton = new RButton("PUNSUBSCRIBE");
        RTextArea psubscribeTextField = new RTextArea();
        RTextArea punsubscribeTextField = new RTextArea();
        psubscribeTextField.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        punsubscribeTextField.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        psubscribeButton.setMinimumSize(new Dimension(130, 28));
        psubscribeButton.setMaximumSize(new Dimension(130, 28));
        psubscribeButton.setPreferredSize(new Dimension(130, 28));

        punsubscribeButton.setMinimumSize(new Dimension(130, 28));
        punsubscribeButton.setMaximumSize(new Dimension(130, 28));
        punsubscribeButton.setPreferredSize(new Dimension(130, 28));

        subscribePane.setLayout(new BoxLayout(subscribePane,BoxLayout.Y_AXIS));
        Box box = Box.createHorizontalBox();
        box.add(Box.createHorizontalStrut(5));
        box.add(psubscribeTextField);
        box.add(Box.createHorizontalStrut(5));
        subscribePane.add(Box.createVerticalStrut(5));
        subscribePane.add(box);

        box = Box.createHorizontalBox();
        box.add(Box.createHorizontalGlue());
        box.add(psubscribeButton);
        box.add(Box.createHorizontalGlue());
        subscribePane.add(Box.createVerticalStrut(5));
        subscribePane.add(box);

        box = Box.createHorizontalBox();
        box.add(Box.createHorizontalStrut(5));
        box.add(punsubscribeTextField);
        box.add(Box.createHorizontalStrut(5));
        subscribePane.add(Box.createVerticalStrut(5));
        subscribePane.add(box);

        box = Box.createHorizontalBox();
        box.add(Box.createHorizontalGlue());
        box.add(punsubscribeButton);
        box.add(Box.createHorizontalGlue());
        subscribePane.add(Box.createVerticalStrut(5));
        subscribePane.add(box);
        subscribePane.add(Box.createVerticalStrut(5));

        SwingTools.addMouseClickedListener(psubscribeButton,e->{
            String text = psubscribeTextField.getText();
            if(StringUtils.isBlank(text)){
                messageInfo( "订阅频道不能为空",true);
                return;
            }
            try{
                psubscribeButton.setEnabled(false);
                ResultRes<Object> resultRes = BaseController.dispatcher(()->{
                    redisConnectService.psubscribe(connectId,StringUtils.strToByte(text));
                    return null;
                },true,true);
                if(resultRes.isRet()){
                    this.messageInfo("psubscribe "+text+" success!",false);
                }else{
                    SwingTools.showMessageErrorDialog(null,"psubscribe "+text+" fail!"+resultRes.getMsg());
                }
            }finally {
                psubscribeButton.setEnabled(true);
            }

        });
        SwingTools.addMouseClickedListener(punsubscribeButton,e->{
            String text = punsubscribeTextField.getText();
            if(StringUtils.isBlank(text)){
                messageInfo( "取消频道不能为空",true);
                return;
            }
            try{
                psubscribeButton.setEnabled(false);
                ResultRes<Object> resultRes = BaseController.dispatcher(() -> {
                    redisConnectService.punsubscribe(connectId, StringUtils.strToByte(text));
                    return null;
                }, true, true);
                if(resultRes.isRet()){
                    this.messageInfo("punsubscribe "+text+" success!",false);
                }else{
                    SwingTools.showMessageErrorDialog(null,"punsubscribe "+text+" fail!"+resultRes.getMsg());
                }
            }finally {
                psubscribeButton.setEnabled(true);
            }
        });
        return subscribePane;
    }

    private void messageInfo(String info,boolean error){
        if(StringUtils.isBlank(info)){
            return;
        }
        if(error){
            messageLabel.setForeground(Color.RED);
        }else{
            messageLabel.setForeground(Color.GREEN);
        }
        LarkFrame.executorService.schedule(()->{
            if(info.equals(messageLabel.getText())){
                messageLabel.setText("");
            }
        },800, TimeUnit.MILLISECONDS);
        messageLabel.setText(info);
    }

    private void sendMessage(boolean allowSend){
        if(allowSend){
            sendButton.setEnabled(false);
            try {
                String channelTextFieldText = channelTextField.getText();
                String text = textArea.getText();
                if (StringUtils.isBlank(channelTextFieldText)) {
                    messageInfo("发送频道不能为空", true);
                    return;
                }
                if (StringUtils.isBlank(text)) {
                    messageInfo("发送内容不能为空", true);
                    return;
                }
                ResultRes<Long> resultRes = BaseController.dispatcher(() -> redisConnectService.publish(connectId, StringUtils.strToByte(channelTextFieldText), StringUtils.strToByte(text)), false, true);
                if(resultRes.isRet()){
                    String sendChanel =   ":[" + resultRes.getData() + "]_"+channelTextFieldText;
                    StyledDocument docs = (StyledDocument) messageTextPane.getDocument();
                    SimpleAttributeSet attrSet = messageTextPane.getAttrSet();
                    StyleConstants.setAlignment(attrSet, StyleConstants.ALIGN_RIGHT);
                    messageTextPane.println(text+ " "+sendChanel,Color.BLUE.brighter().brighter());
                    attrSet.addAttribute(StyleConstants.Alignment,StyleConstants.ALIGN_LEFT);

                    textArea.setText("");
                }else{
                    this.messageInfo("发送失败，请重试",true);
                }

                textArea.requestFocus();

            }finally {
                sendButton.setEnabled(true);
            }
        }
    }
}
