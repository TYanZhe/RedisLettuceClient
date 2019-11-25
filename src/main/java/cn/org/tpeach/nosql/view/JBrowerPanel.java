package cn.org.tpeach.nosql.view;

import cn.org.tpeach.nosql.constant.PublicConstant;
import cn.org.tpeach.nosql.tools.CollectionUtils;
import cn.org.tpeach.nosql.tools.IOUtil;
import cn.org.tpeach.nosql.tools.StringUtils;
import cn.org.tpeach.nosql.tools.SwingTools;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker;
import javafx.concurrent.Worker.State;
import javafx.embed.swing.JFXPanel;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.scene.web.WebHistory;
import javafx.scene.web.WebView;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.w3c.dom.NodeList;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.html.HTMLAnchorElement;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Pattern;

@Getter
@Setter
@Builder
class Bookmark{
    private String id;
    private String name;
    private String url;
    private int type = 0;//1 本地 0 网站

}
class BrowerTab extends Tab{

    private static final String HOME_PAGE = "html/home.html";
    private static final String BLANK = "_blank";
    private static final String TARGET = "target";
    private static final String CLICK = "click";
    private static final String DEFAULT_TEXT = "新标签页";
    public static String SEARCH = "https://www.baidu.com/s?wd=";
    private static final String PROTOCOL = "^(http|ftp|https):\\/\\/\\S*";
    private static final String URL_REG_EXP = "((http|ftp|https):\\/\\/)?[\\w\\-_]+(\\.[\\w\\-_]+)+([\\w\\-\\.,@?^=%&amp;:/~\\+#]*[\\w\\-\\@?^=%&amp;/~\\+#])?";
    private static final Image homeImage =new Image("image/base/home.png");
    private final WebView webview ;
    private final WebHistory webHistory;
    private final HBox addressBox;
    private  TextField urlTextField;
    private final AnchorPane anchorPane;
    private AnchorPane bookmarkPane;
    private TextField bookmarkName ;
    private TextField bookmarkAddress ;
    private Button back;
    private Button forward ;
    private Button refresh ;
    private Button plus  ;
    private JBrowerPanel browerPanel;
    public static BrowerTab build(JBrowerPanel browerPanel,Bookmark bookmark){

        HBox bookmarkBox = new HBox();
        bookmarkBox.setPadding(new Insets(3.0D));
        bookmarkBox.setSpacing(10);
        bookmarkBox.setPrefHeight(25D);
        bookmarkBox.setMaxHeight(25D);
        bookmarkBox.setStyle(  "-fx-border-color: #B6B4B6 ;-fx-border-width: 0 0 1px 0" );
        BrowerTab browerTab = new BrowerTab(bookmarkBox, bookmark);
        browerTab.browerPanel = browerPanel;
        if(CollectionUtils.isEmpty(browerPanel.getBookmarkBoxList() )){
            bookmarkBox.getChildren().addAll(browerTab.createNode("home", 1,"主页", HOME_PAGE, new Tooltip("主页"),homeImage),
                    browerTab.createNode( 0,"Redis", "https://redis.io/", new Tooltip("主页"),new Image(PublicConstant.Image.redis_db)));
        }

        //初始化书签 解决共享标签不显示
        if(CollectionUtils.isNotEmpty(browerPanel.getBookmarkBoxList())){
            HBox hBox = browerPanel.getBookmarkBoxList().get(0);
            ObservableList<Node> children = hBox.getChildren();
            for (Node child : children) {
                if(child instanceof Label){
                    Label label = (Label) child;
                    Bookmark userData = (Bookmark) label.getUserData();
                    Node graphic = label.getGraphic();
                    Image image = null;
                    if(graphic != null && graphic instanceof ImageView){
                        image = ((ImageView)graphic).getImage();
                    }
                    bookmarkBox.getChildren().add(browerTab.createNode(userData.getId(),userData.getType(),label.getText(),userData.getUrl(),label.getTooltip(),image));
                }
            }
        }

        browerPanel.getBookmarkBoxList().add(bookmarkBox);
        return browerTab;
    }

    private BrowerTab(HBox bookmarkBox, Bookmark bookmark) {
        this.setOnClosed(event -> {
            TabPane tabPane = browerPanel.getTabPane();
            ObservableList<Tab> tabs = tabPane.getTabs();
            if(CollectionUtils.isEmpty(tabs)){
                tabPane.getTabs().add(BrowerTab.build(browerPanel,null));
            }
        });
        VBox vBox = new VBox();
        //浏览器
        webview = new WebView();
        webHistory = webview.getEngine().getHistory();
        //地址栏
        addressBox = new HBox();
        //书签
        initAddress();
        initWebViewEvent();
        initBookMarkPanl();


        vBox.getChildren().add(addressBox);
        vBox.getChildren().add(bookmarkBox);
        vBox.getChildren().add(webview);

        AnchorPane.setLeftAnchor(vBox, 0D);
        AnchorPane.setRightAnchor(vBox, 0D);
        AnchorPane.setBottomAnchor(vBox, 0D);
        AnchorPane.setTopAnchor(vBox, 0D);

        anchorPane = new AnchorPane(vBox,bookmarkPane);
        this.setContent(anchorPane);
        this.setText(DEFAULT_TEXT);
        this.setClosable(true);
        if(bookmark == null){
            bookmark = Bookmark.builder().url(HOME_PAGE).type(1).build();
        }
        this.sendRequest(bookmark);
        this.setOnCloseRequest((event) -> {

        });
    }

    private void initBookMarkPanl() {
        bookmarkPane = new AnchorPane();
        bookmarkPane.setVisible(false);
        bookmarkPane.setLayoutY(89.0);
        bookmarkPane.setPrefSize(344,139);
        bookmarkPane.setStyle("-fx-border-radius: 3px;-fx-border-color: dodgerblue;-fx-border-style: solid;-fx-background-color: white;");
        AnchorPane.setTopAnchor(bookmarkPane,89.0);

        Label titlelabel = new Label("书签管理");
        Label namelabel = new Label("名称");
        Label addresslabel = new Label("地址");
        bookmarkName = new TextField();
        bookmarkAddress = new TextField();
        Button save = new Button("保存");
        Button close = new Button("X");

//        titlelabel.setBackground(new Background(new BackgroundFill(Color.BLUE,null,null)));
        titlelabel.setAlignment(Pos.CENTER);
        titlelabel.setContentDisplay(ContentDisplay.CENTER);
        titlelabel.setTextAlignment(TextAlignment.CENTER);
        titlelabel.setLayoutX(6.0);
        titlelabel.setLayoutY(14.0);
        titlelabel.setPrefWidth(344);
        titlelabel.setFont(Font.font(15.0));
        AnchorPane.setRightAnchor(titlelabel,0.0);
        AnchorPane.setLeftAnchor(titlelabel,0.0);
        AnchorPane.setTopAnchor(titlelabel,0.0);

        namelabel.setLayoutX(6.0);
        namelabel.setLayoutY(34.0);
        AnchorPane.setLeftAnchor(namelabel,6.0);
        AnchorPane.setTopAnchor(namelabel,34.0);

        addresslabel.setLayoutX(6.0);
        addresslabel.setLayoutY(69.0);
        AnchorPane.setLeftAnchor(addresslabel,6.0);
        AnchorPane.setTopAnchor(addresslabel,69.0);

        bookmarkName.setLayoutX(40.0);
        bookmarkName.setLayoutY(34.0);
        bookmarkName.setPrefSize(299.0,23.0);
        AnchorPane.setTopAnchor(bookmarkName,34.0);

        bookmarkAddress.setLayoutX(40.0);
        bookmarkAddress.setLayoutY(69.0);
        bookmarkAddress.setPrefSize(299.0,23.0);
        AnchorPane.setTopAnchor(bookmarkAddress,69.0);

        save.setLayoutX(145.0);
        save.setLayoutY(104.0);
        save.setMnemonicParsing(false);
        AnchorPane.setTopAnchor(save,104.0);

        close.setLayoutX(284.0);
        close.setLayoutY(3.0);
        close.setMnemonicParsing(false);
        AnchorPane.setTopAnchor(close,0.0);
        AnchorPane.setRightAnchor(close,0.0);

        save.setOnAction(e->saveBookmark());
        close.setOnAction(e->hideBookmark());


        bookmarkPane.getChildren().addAll(titlelabel,namelabel,addresslabel,bookmarkName,bookmarkAddress,save,close);
    }

    private void initAddress() {
        addressBox.setSpacing(3D);
        addressBox.setPrefHeight(25D);
        addressBox.setPadding(new Insets(3D));
//        addressBox.setStyle("-fx-background-color: #336699;");
        //回退，前进、刷新，地址输入框
        back = getButton("image/brower/left.png");
        forward = getButton("image/brower/right.png");
        refresh = getButton("image/brower/refresh.png");

        plus = getButton("image/brower/plus.png");
        back.setOnAction((event) -> {
            Integer current = webHistory.currentIndexProperty().getValue();
            if (current > 0) {
                webHistory.go(-1);
            }
        });
        forward.setOnAction((event) -> {
            Integer current = webHistory.currentIndexProperty().getValue();
            if (current < webHistory.getEntries().size() - 1) {
                webHistory.go(1);
            }
        });
        refresh.setOnAction((event) -> {
            webview.getEngine().reload();
        });
        plus.setOnAction((event) -> {
            showBookmarkPane(getText(),urlTextField.getText(),null);
        });

        urlTextField = new TextField();
        urlTextField.setPromptText("输入网址或搜索内容");
        urlTextField.setStyle("-fx-background-radius: 10px;-fx-background-color: #F1F3F4");
        urlTextField.setOnKeyReleased((event) -> {
            if (KeyCode.ENTER.equals(event.getCode())) {//回车加载页面
                analysisAddress();
            }
        });
        urlTextField.setOnMouseClicked((event) -> {
            if (urlTextField.getText().length() == urlTextField.getCaretPosition()) {
                urlTextField.selectAll();
            }
        });
        addressBox.getChildren().addAll(back,forward,refresh,urlTextField,plus);

        HBox.setHgrow(urlTextField, Priority.ALWAYS);
        addressBox.widthProperty().addListener(observable -> {

        });

    }
    private void analysisAddress() {
        String text = urlTextField.getText();
        Pattern p = Pattern.compile(URL_REG_EXP);
        if (p.matcher(text).matches()) {
            if (!Pattern.compile(PROTOCOL).matcher(text).matches()) {
                text = "http://" + text;
            }
            webview.getEngine().load(text);
        } else if (StringUtils.isNotBlank(text)) {
            webview.getEngine().load(SEARCH + text);
        }
    }
    public void pack(Integer w, Integer h){

//        anchorPane.resize(w,h);
//        urlTextField.setPrefWidth(this.anchorPane.getWidth()-back.getWidth()*4-addressBox.getSpacing() * 8 );
    }
    /**
     * 隐藏书签管理窗口
     */
    @FXML
    public void hideBookmark() {
        bookmarkPane.setVisible(false);
    }
    private void saveBookmark() {
        addBookmark(bookmarkName.getText(), bookmarkAddress.getText());
        hideBookmark();


    }
    public Label createNode(int type,String name, String url, Tooltip tooltip, Image image){
       return createNode(StringUtils.getUUID(),type,name,url,tooltip,image);
    }
    public Label createNode(String id,int type,String name, String url,Tooltip tooltip, Image image){
        Label node = new Label(name);
        if(image != null){
            ImageView imageView = new ImageView(image);
            node.setGraphic(imageView);
        }
        Bookmark bookmark = Bookmark.builder().id(id).name(name).url(url).type(type).build();
        node.setMaxWidth(100);
        node.setUserData(bookmark);
        node.setTooltip(tooltip);
        node.setCursor(Cursor.HAND);
        node.setOnMouseClicked((event) -> {

            if (MouseButton.SECONDARY.equals(event.getButton())) {
                showBookmarkPane(node.getText(), bookmark.getUrl(), bookmark);
            } else if (event.isControlDown()) {//CTRL+单击事件
                BrowerTab mt = (BrowerTab) this.getTabPane().getSelectionModel().getSelectedItem();
                mt.sendRequest(bookmark);
            } else {
                BrowerTab mt = BrowerTab.build(browerPanel,bookmark);
                this.getTabPane().getTabs().add(mt);
                this.getTabPane().getSelectionModel().select(mt);
            }
        });
        return node;
    }
    /**
     * 添加书签
     *
     * @param name
     * @param url
     */
    public void addBookmark(String name, String url) {
        Bookmark data = (Bookmark) bookmarkPane.getUserData();
        Tooltip tooltip = new Tooltip("右键点击修改\n左键单击新标签页打开书签\nCTRL+左键单击当前页打开书签\n" + name + "\n" + url);
        if (data != null) {//修改书签
            List<HBox> bookmarkBoxList = browerPanel.getBookmarkBoxList();
            for (HBox hBox : bookmarkBoxList) {
                ObservableList<Node> children = hBox.getChildren();
                for (Node child : children) {
                    Bookmark userData = (Bookmark) child.getUserData();
                    if(data.getId().equals(userData.getId())){
                        Label node = (Label) child;
                        userData.setName(name);
                        userData.setUrl(url);
                        node.setText(name);
                        node.setTooltip(tooltip);
                    }
                }
            }

        } else {//添加书签
            Label node = createNode(0,name,url,tooltip,null);
            browerPanel.getBookmarkBoxList().forEach(b->b.getChildren().add(node));

        }
    }

    /**
     * 移除书签
     *
     * @param node
     */
    public static void removeBookmark(Node node) {

    }

    /**
     * 显示书签管理窗口
     *
     * @param name
     * @param url
     * @param userData 编辑传需要修改的节点
     */
    public void showBookmarkPane(String name, String url, Object userData) {
        Double width = bookmarkPane.getScene().getWidth();
        AnchorPane.setLeftAnchor(bookmarkPane, (width - 344d) / 2);
        bookmarkAddress.setText(url);
        bookmarkName.setText(name);
        bookmarkPane.setVisible(true);
        bookmarkPane.setUserData(userData);
    }

    private void initWebViewEvent() {
        webview.getEngine().locationProperty().addListener((ObservableValue<? extends String> ov, final String oldLoc, final String loc) -> {
            if (!(loc.contains("setting.html") && loc.contains("file:"))) {
                urlTextField.setText(loc);
            }
        });
        webview.getEngine().titleProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue == null ){
                if(this.getText() == null){
                    setText(DEFAULT_TEXT);
                    setTooltip(null);
                }
                return;
            }
            setText(newValue);
            setTooltip(new Tooltip(newValue));
            Image btnImg = new Image("https://www.baidu.com/favicon.ico");
            ImageView imageView = new ImageView(btnImg);
            setGraphic(imageView);
        });
        webview.getEngine().documentProperty().addListener((observable, ov, document) -> {
            if (document != null) {
                NodeList nodeList = document.getElementsByTagName("a");
                for (int i = 0; i < nodeList.getLength(); i++) {
                    org.w3c.dom.Node node = nodeList.item(i);
                    org.w3c.dom.Node targetNode = node.getAttributes().getNamedItem(TARGET);
                    if (targetNode != null) {
                        String target = targetNode.getTextContent();
                        if (BLANK.equals(target)) {//页面中target="_blank"的在新标签中打开
                            EventTarget eventTarget = (EventTarget) node;
                            eventTarget.addEventListener(CLICK, (Event evt) -> {
                                HTMLAnchorElement anchorElement = (HTMLAnchorElement) evt.getCurrentTarget();
                                String href = anchorElement.getHref();
                                BrowerTab mt = BrowerTab.build(browerPanel,Bookmark.builder().url(href).build());
                                getTabPane().getTabs().add(mt);
                                getTabPane().getSelectionModel().select(mt);
                                evt.preventDefault();
                            }, false);
                        }
                    }
                }
            }
        });
        webview.getEngine().getLoadWorker().stateProperty().addListener(new ChangeListener<State>() {
                    @Override
                    public void changed(ObservableValue ov, State oldState, State newState) {
                        switch (newState){
                            case FAILED:
                                loadHtml("html/error.html");
                                break;
                            case SUCCEEDED:
                            case READY:
                            case RUNNING:
                            case CANCELLED:
                            case SCHEDULED:
                                break;
                            default:
                                break;
                        }
                        if (newState == Worker.State.SUCCEEDED) {

                        }

                    }
                });
    }
    private Button getButton(String path){
        return getButton(null,path);
    }
    private Button getButton(String text,String path){
        Button btn ;
        if(StringUtils.isNotBlank(text)){
             btn = new Button(text);
        }else{
            btn = new Button();
        }
        btn.setStyle("-fx-background-color: white");
        btn.setPrefWidth(25D);
        if(StringUtils.isNotBlank(path)){
            Image btnImg = new Image(path);
            ImageView imageView = new ImageView(btnImg);
            //给按钮设置图标
            btn.setGraphic(imageView);
        }
        return btn;
    }

    /**
     * 发送请求
     * @param bookmark
     */
    private void sendRequest(Bookmark bookmark){
        if(0 == bookmark.getType()){
            webview.getEngine().load(bookmark.getUrl());
        }else{
            loadHtml(bookmark.getUrl());
        }

    }

    private void loadHtml(String url){
        try {
            String error = IOUtil.getString(url);
            webview.getEngine().loadContent(error);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

public class JBrowerPanel extends JFXPanel {
    private static final class SingleHolder{
        private static JBrowerPanel instance = new JBrowerPanel();
    }
    public static JBrowerPanel getInstance() {
        return JBrowerPanel.SingleHolder.instance;
    }
    private static final String url = "https://redis.readthedocs.io/en/2.4/";
    @Getter
    List<HBox> bookmarkBoxList = new ArrayList<>();
    List<Thread> threadList = new Vector<>();
    @Getter
    TabPane tabPane  ;
    public JBrowerPanel( ) {
        init();
    }

    private JBrowerPanel init() {
        threadList.forEach(t->t.interrupt());
        CountDownLatch countDownLatch = new CountDownLatch(1);
        Group root = new Group();
        Scene scene = new Scene(root);
        JBrowerPanel.this.setScene(scene);
        root.setAutoSizeChildren(true);
        tabPane = new TabPane();
        AnchorPane.setLeftAnchor(tabPane, 0D);
        AnchorPane.setRightAnchor(tabPane, 0D);
        AnchorPane.setBottomAnchor(tabPane, 0D);
        AnchorPane.setTopAnchor(tabPane, 0D);
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.SELECTED_TAB);
        Platform.runLater(()-> {
            try {
                threadList.add(Thread.currentThread());
                tabPane.getTabs().add(BrowerTab.build(JBrowerPanel.this,null));
                root.getChildren().addAll(tabPane);
            }finally {
                countDownLatch.countDown();
            }
        });
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        SwingTools.fillWidthPanel(this, (w, h)->{
            tabPane.setPrefSize(w,h);
            tabPane.setMinWidth(w);
            tabPane.resize(w,h);
            Iterator<Tab> iterator = tabPane.getTabs().iterator();
            while (iterator.hasNext()){
                Tab next = iterator.next();
                if(next instanceof BrowerTab){
                    ((BrowerTab)next).pack(w,h);
                }
            }

        });
        return this;
    }



}