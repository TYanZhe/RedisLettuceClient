package cn.org.tpeach.nosql.view;

import bsh.util.JConsole;
import cn.org.tpeach.nosql.constant.ConfigConstant;
import cn.org.tpeach.nosql.constant.PublicConstant;
import cn.org.tpeach.nosql.redis.bean.RedisConnectInfo;
import cn.org.tpeach.nosql.tools.CollectionUtils;
import cn.org.tpeach.nosql.tools.ConfigParser;
import cn.org.tpeach.nosql.tools.ReflectUtil;
import cn.org.tpeach.nosql.tools.StringUtils;
import cn.org.tpeach.nosql.view.component.EasyJSP;
import cn.org.tpeach.nosql.view.component.OnlyReadTextPane;
import cn.org.tpeach.nosql.view.component.RTabbedPane;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.CodecException;
import io.netty.handler.codec.redis.*;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.Getter;
import lombok.Setter;

import javax.swing.*;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

class RConsole extends JConsole{
    private JTextPane jTextPane;
    @Override
    public void setBackground(Color bg) {
        super.setBackground(bg);
        JTextPane textPane = getTextPane();
        if(textPane != null){
            textPane.setBackground(bg);
        }

    }

    public JTextPane getTextPane() {
        if(jTextPane == null){
            Object text = ReflectUtil.getSuperField(this, "text");
            if(text != null && text instanceof JTextPane ){
                jTextPane = (JTextPane) text;
            }
        }
        return jTextPane;
    }


}


class ConsoleTextPane extends OnlyReadTextPane implements KeyListener {
    @Getter
    @Setter
    private String prefix;
    public ConsoleTextPane() {
        this.setBackground(Color.BLACK);
    }

    @Override
    public void init() {
        jsp = new EasyJSP(this).hiddenHorizontalScrollBar();
        this.setCandy(Color.BLACK);
        setOpaque(false);
        this.setFont(new Font("黑体",Font.PLAIN,15));
        setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        setForeground(Color.GREEN.darker().darker().darker());
        addKeyListener(this);
        clear();
    }

    @Override
    public  boolean isEditable() {
        return true;
    }

    @Override
    public void keyTyped(KeyEvent e) {
        ConsoleTextPane.this.setCaretPosition(this.getDocument().getLength());
    }

    @Override
    public void keyPressed(KeyEvent e) {
        ConsoleTextPane.this.setCaretPosition(this.getDocument().getLength());
        if(e.getKeyCode() == KeyEvent.VK_ENTER){
            //获取最后一行数据

            //发送命令


            //接收命令后
            if(StringUtils.isNotBlank(prefix)){
                print( prefix+">",Color.WHITE);
            }

        }

    }

    @Override
    public void keyReleased(KeyEvent e) {
        ConsoleTextPane.this.setCaretPosition(this.getDocument().getLength());
    }
}

public class ConsolePanel extends JPanel {
/*    ConsoleTextPane jTextPane = new ConsoleTextPane();
    *//**
     * 最多显示条数
     *//*
    @Getter
    @Setter
    protected int maxEntries = 1000;
    *//**
     * 已经在显示的条数
     *//*
    private int entries = 0;


    public String getPrefix() {
        return jTextPane.getPrefix();
    }

    public void setPrefix(String prefix) {
        jTextPane.setPrefix(prefix);
    }

    public ConsolePanel() {
        this.setBackground(Color.BLACK);
        this.setLayout(new BorderLayout());
        this.add(jTextPane.getJsp(),BorderLayout.CENTER);
    }

    public synchronized void println(String s) {
        jTextPane.println(s);
    }
    public synchronized void println(String s, Color fontColor) {
        jTextPane.println(s,fontColor);
    }
    public synchronized void print(String s) {
        jTextPane.print(s);
    }
    public synchronized void print(String s, Color fontColor) {
        jTextPane.print(s,fontColor);
    }

    public void initFinish() {
        jTextPane.requestFocus();
    }*/
    RConsole console;
    @Getter
    @Setter
    private String prompt;
    @Getter
    @Setter
    private String promptFormat;
    private RTabbedPane logTabbedPane;
    private RedisConnectInfo connectInfo;
    private  BufferedReader bufInput;
    static  SimpleAttributeSet attrs = new SimpleAttributeSet();
    static {
        StyleConstants.setForeground(attrs, Color.WHITE);
    }

    List<Thread> list = new Vector<>();
    public ConsolePanel(RedisConnectInfo connectInfo, RTabbedPane logTabbedPane) {
        this.connectInfo = connectInfo;
        this.logTabbedPane = logTabbedPane;
        setLayout(new BorderLayout());
        console = new RConsole();
        console.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14) );
        console.setHorizontalScrollBar(new EasyJSP().getHorizontalScrollBar());
        console.setVerticalScrollBar(new EasyJSP().getVerticalScrollBar());
        console.setBackground(new Color(49,59,62));

        add(console,BorderLayout.CENTER);
    }

    public void start() {
        Thread thread = Thread.currentThread();
        list.add(thread);
        promptFormat = "["+connectInfo.getName()+"](%s)$> ";
        prompt = String.format(promptFormat,"0");
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 20000);
            bootstrap.option(ChannelOption.TCP_NODELAY, true);
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<Channel>() {
                        @Override
                        protected void initChannel(Channel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new RedisDecoder());
                            pipeline.addLast(new RedisBulkStringAggregator());
                            pipeline.addLast(new RedisArrayAggregator());
                            pipeline.addLast(new RedisEncoder());
                            // 若60s没有收到消息，调用userEventTriggered方法
                            pipeline.addLast(new IdleStateHandler(0, 0, 60, TimeUnit.SECONDS));
                            pipeline.addLast(new RedisClientHandler(console,ConsolePanel.this));
                        }
                    });

            String host = connectInfo.getHost();
            int port = connectInfo.getPort();
            ChannelFuture lastWriteFuture = null;
            String line;
            ChannelFuture connect = bootstrap.connect(host, port);


            console.print("Redis Console(type 'quit' to exit)",new Font(Font.MONOSPACED,Font.BOLD,16), Color.WHITE);
            console.println();
            JTextPane jtp = console.getTextPane();
            StyledDocument doc = jtp.getStyledDocument();
            doc.setCharacterAttributes(doc.getLength(), 1, ConsolePanel.attrs, true);
            jtp.setCaretColor(Color.WHITE);
/*            jtp.addKeyListener(new KeyListener() {
                @Override
                public void keyTyped(KeyEvent e) {

                }

                @Override
                public void keyPressed(KeyEvent e) {

                }

                @Override
                public void keyReleased(KeyEvent e) {
                    if ((e.getKeyCode() == KeyEvent.VK_C)
                            && (((InputEvent) e)
                            .isControlDown())) {
                        bye();
                        thread.interrupt();
                    }
                }
            });*/
            console.print("Connecting...",PublicConstant.RColor.tableSelectBackground);
            console.println();

            connect.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if( !future.isSuccess() ){
                        console.print(future.cause().getMessage(), new Color(255,92,51));
                        thread.interrupt();
                    }
                }
            });
            Channel channel =connect.sync().channel();
            console.print("Connected.",Color.ORANGE);
            console.println();
            console.print(prompt,Color.WHITE);

            if(StringUtils.isNotBlank(connectInfo.getAuth())){
                console.print("Auth ******",PublicConstant.RColor.tableSelectBackground);
                console.println();
                lastWriteFuture = channel.writeAndFlush("Auth "+connectInfo.getAuth());
            }
            console.requestFocus();
            bufInput = new BufferedReader(console.getIn());

            while ((line = bufInput.readLine()) != null) {
                if (line.equalsIgnoreCase("quit")){
                    break;
                }else if(line.equals(";")){
                    line="\n";
                }
                //发送
                lastWriteFuture = channel.writeAndFlush(StringUtils.decodeUnicode(line));
                lastWriteFuture.addListener(new GenericFutureListener<ChannelFuture>() {
                    @Override
                    public void operationComplete(ChannelFuture future) throws Exception {
                        if (!future.isSuccess()) {
                            console.print(future.cause().getMessage());
                        }
                    }
                });
            }

            if (lastWriteFuture != null) {
                lastWriteFuture.sync();
            }
            bye();

        }catch (InterruptedIOException e){

        } catch (Exception e){
            e.printStackTrace();
        }finally {

            try {
                bufInput.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            group.shutdownGracefully();
        }
    }
    private void bye(){
        console.print(" bye ");
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        logTabbedPane.remove(this);
    }
    public void close(){
        list.forEach(t->t.interrupt());
    }

}
class RedisClientHandler extends ChannelDuplexHandler {
    private static final ByteBuf HEARTBEAT_SEQUENCE = Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("PING", CharsetUtil.UTF_8));
    RConsole console;
    ConsolePanel consolePanel;
    String selectComand = null;
    private boolean isHeart = false;
    public RedisClientHandler(RConsole console, ConsolePanel consolePanel) {
        this.console = console;
        this.consolePanel = consolePanel;
    }

    /**
     * 发送 redis 命令
     * @param ctx
     * @param msg
     * @param promise
     */
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
        String encoding = ConfigParser.getInstance().getString(ConfigConstant.Section.CHARACTER_ENCODING, ConfigConstant.CHARACTER, PublicConstant.CharacterEncoding.UTF_8);
        String s = ((String) msg);
        if(s.toUpperCase().contains("SELECT")){
            selectComand = s;
        }else{
            selectComand = null;
        }
        char[] chars = s.toCharArray();
        List<RedisMessage> children = new ArrayList<>();
        StringBuffer stringBuffer = new StringBuffer();
        boolean start = false;
        boolean quotationMarksStart = false;
        boolean quotationMarksStart2 = false;
        boolean transferredMeaning = false;
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            if(Character.isSpaceChar(c)){
                if(quotationMarksStart || quotationMarksStart2){
                    stringBuffer.append(c);
                }else if(start) {
                    writeCommand(ctx,stringBuffer,children);
                    start = false;
                }

            }else {
                boolean flag = true;
                if(transferredMeaning){
                    transferredMeaning = false;
                }else{
                    if(c == '"'){
                        if(!quotationMarksStart2) {
                            quotationMarksStart = !quotationMarksStart;
                            if(!quotationMarksStart){
                                writeCommand(ctx,stringBuffer,children);
                                start = false;

                            }
                            flag = false;

                        }
                    }else if(c == '\''){
                        if(!quotationMarksStart) {
                            quotationMarksStart2 = !quotationMarksStart2;
                            if(!quotationMarksStart2){
                                writeCommand(ctx,stringBuffer,children);
                                start = false;
                            }
                            flag = false;
                        }
                    }else if(c == '\\'){
                        if(quotationMarksStart || quotationMarksStart2){
                            transferredMeaning = true;
                            flag = false;
                        }
                    }
                }
                if(flag){
                    stringBuffer.append(c);
                    start = true;
                }
            }
        }
        if(stringBuffer.length()>0){
            writeCommand(ctx,stringBuffer,children);
        }
        RedisMessage request = new ArrayRedisMessage(children);
        ctx.write(request, promise);

    }

    private void writeCommand(ChannelHandlerContext ctx,StringBuffer stringBuffer,List<RedisMessage> children){
        ByteBufAllocator alloc = ctx.alloc();
        ByteBuf buffer = alloc.buffer();
        buffer.writeBytes(stringBuffer.toString().getBytes());
        children.add(new FullBulkStringRedisMessage(buffer));
        stringBuffer.delete(0,stringBuffer.length());
    }
    @Override
    public void close(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        ctx.close(promise);

    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if(evt instanceof IdleStateEvent) {
            // 发送心跳到远端
            List<RedisMessage> children = new ArrayList<>(1);
            children.add(new FullBulkStringRedisMessage(HEARTBEAT_SEQUENCE.duplicate()));
            RedisMessage request = new ArrayRedisMessage(children);
            ctx.writeAndFlush(request)
                    .addListener(ChannelFutureListener.CLOSE_ON_FAILURE);    // 关闭连接
            isHeart = true;
        } else {
            // 传递给下一个处理程序
            super.userEventTriggered(ctx, evt);
        }
    }

    /**
     * 接收 redis 响应数据
     * @param ctx
     * @param msg
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        RedisMessage redisMessage = (RedisMessage) msg;
        //心跳
        if (msg instanceof SimpleStringRedisMessage && isHeart && "PONG".equalsIgnoreCase(((SimpleStringRedisMessage) msg).content())) {
            isHeart = false;
        }else{
            // 打印响应消息
            printAggregatedRedisResponse(redisMessage);
        }
        // 是否资源
        ReferenceCountUtil.release(redisMessage);
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.close();
        console.println();
        console.print(cause.getMessage(), new Color(255,92,51));
        console.println();
        console.getTextPane().setEditable(false);
        consolePanel.close();
    }


    private  void printAggregatedRedisResponse(RedisMessage msg) {
        try {
            final AtomicBoolean hasChildren = new AtomicBoolean(false);
            final LinkedList<ArrayRedisMessage> linkedList = new LinkedList<>();
            final StringBuffer prefix = new StringBuffer();
            consolrPrintMsg(msg,m->{
                    hasChildren.set(true);
                    printArray(linkedList, ((ArrayRedisMessage) msg), prefix.toString());
                    while (!linkedList.isEmpty()){
                        ArrayRedisMessage arrayRedisMessage = linkedList.removeFirst();
                        prefix.append("  ");
                        printArray(linkedList, arrayRedisMessage,prefix.toString());
                    }
            });
            if(!hasChildren.get()){
                console.println();
            }
            console.print(consolePanel.getPrompt(), Color.WHITE);
        }finally {
            JTextPane jtp = console.getTextPane();
            StyledDocument doc = jtp.getStyledDocument();
            doc.setCharacterAttributes(doc.getLength(), 1, ConsolePanel.attrs, false);
        }

    }

    private void printArray(LinkedList<ArrayRedisMessage> linkedList, ArrayRedisMessage arrayRedisMessage,String prefix) {
        RedisMessage msg;
        final List<RedisMessage> children = arrayRedisMessage.children();
        if(CollectionUtils.isNotEmpty(children)) {
            for (int i = 0; i < children.size(); i++) {
                console.print(prefix+(i+1) + ") ");
                consolrPrintMsg(children.get(i),m->linkedList.add(m));
                console.println();
            }
        }
    }


    private void consolrPrintMsg(RedisMessage msg, Consumer<ArrayRedisMessage> arrayConsumer){
        if (msg instanceof SimpleStringRedisMessage) {
            console.print(((SimpleStringRedisMessage) msg).content(), Color.GREEN.darker().darker());
            if (selectComand != null) {
                consolePanel.setPrompt(String.format(consolePanel.getPromptFormat(), selectComand.split("\\s+")[1]));
            }
        } else if (msg instanceof ErrorRedisMessage) {
            console.print(((ErrorRedisMessage) msg).content(), new Color(255, 92, 51));
        } else if (msg instanceof IntegerRedisMessage) {
            console.print(((IntegerRedisMessage) msg).value(), Color.ORANGE);
        } else if (msg instanceof FullBulkStringRedisMessage) {
            console.print(getString((FullBulkStringRedisMessage) msg), new Color(110, 110, 255));
        } else if (msg instanceof ArrayRedisMessage) {
            if (CollectionUtils.isNotEmpty(((ArrayRedisMessage) msg).children())) {
                arrayConsumer.accept((ArrayRedisMessage) msg);
            }
        } else {
            throw new CodecException("unknown message type: " + msg);
        }
    }


    private  String getString(FullBulkStringRedisMessage msg) {
        if (msg.isNull()) {
            return "(null)";
        }
        return msg.content().toString(CharsetUtil.UTF_8);
    }

}