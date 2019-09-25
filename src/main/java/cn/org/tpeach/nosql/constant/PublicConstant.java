package cn.org.tpeach.nosql.constant;

import java.awt.*;
import java.io.File;

import javax.swing.ImageIcon;

import cn.org.tpeach.nosql.tools.IOUtil;
import cn.org.tpeach.nosql.view.common.ServiceManager;

/**
 * @author tyz
 * @Title: PublicConstant
 * @ProjectName RedisLark
 * @Description: TODO
 * @date 2019-06-23 20:47
 * @since 1.0.0
 */
public class PublicConstant {

    public final static boolean ISDEBUG = true;
    public final static String RESOURCE_CLASSNAME = "cn.org.tpeach.nosql.i18n.PlatformResource";
    public final static String NAMESPACE_SPLIT = ":";

    public final static class ConnType {

        public final static short DEFAULT = 0;
        public final static short SSH = 2;
        public final static short SSL = 2;
    }

    public final static class CharacterEncoding {

        public final static String UTF_8 = "UTF-8";

    }

    /**
     * https://www.easyicon.net/
     * <p>
     * Title: PublicConstant.java</p>
     *
     * @author taoyz
     * @date 2019年8月28日
     * @version 1.0
     */
    public final static class Image {
        //https://www.easyicon.net/1210880-home_icon.html

        public static ImageIcon home = IOUtil.getImageIcon("image/base/home.png");
        //https://www.easyicon.net/1211616-github_media_social_icon.html
        public static ImageIcon github = IOUtil.getImageIcon("image/base/github@2x.png");
        public static ImageIcon changeLog = IOUtil.getImageIcon("image/base/changeLog@2x.png");
        public static ImageIcon logo = IOUtil.getImageIcon("image/base/logo.png");
        public static ImageIcon logo_16 = IOUtil.getImageIcon("image/base/logo.png", 16, 16);
        public static ImageIcon redis_db = IOUtil.getImageIcon("image/base/redisDb.png", 16, 16);
        public static ImageIcon redis_server = IOUtil.getImageIcon("image/tree/redis_server.png");
        //https://www.easyicon.net/28750-database_icon.html
        public static ImageIcon database = IOUtil.getImageIcon("image/tree/database.png");
        public static ImageIcon key_icon = IOUtil.getImageIcon("image/tree/key.png");
        public static ImageIcon folder_database = IOUtil.getImageIcon("image/tree/folder_database.png");
        public static ImageIcon arrow_down_blue = IOUtil.getImageIcon("image/base/arrow_down.png", 12, 12);
        public static ImageIcon arrow_right_blue = IOUtil.getImageIcon("image/base/arrow_right.png", 12, 12);
        public static ImageIcon arrow_left_blue = IOUtil.getImageIcon("image/base/arrow_left.png", 12, 12);
        public static ImageIcon arrow_up_blue = IOUtil.getImageIcon("image/base/arrow_up.png", 12, 12);
        public static ImageIcon separator_v = IOUtil.getImageIcon("image/base/separator_v.png");
        public static ImageIcon separator_v_solid = IOUtil.getImageIcon("image/base/separator_v_solid.png");
        public static ImageIcon resultset_refresh = IOUtil.getImageIcon("image/menu/resultset_refresh.png");
        public static ImageIcon refresh = IOUtil.getImageIcon("image/base/refresh.png");
        public static ImageIcon loading01 = IOUtil.getImageIcon("image/base/loading01.png");
        public static ImageIcon loading02 = IOUtil.getImageIcon("image/base/loading02.png");
        public static ImageIcon loading03 = IOUtil.getImageIcon("image/base/loading03.png");
        public static ImageIcon loading04 = IOUtil.getImageIcon("image/base/loading04.png");
        public static ImageIcon loading05 = IOUtil.getImageIcon("image/base/loading05.png");
        public static ImageIcon loading06 = IOUtil.getImageIcon("image/base/loading06.png");
        public static ImageIcon loading07 = IOUtil.getImageIcon("image/base/loading07.png");
        //菜單
        public static ImageIcon copy = IOUtil.getImageIcon("image/menu/copy.png");
        public static ImageIcon paste = IOUtil.getImageIcon("image/menu/paste.png");
        public static ImageIcon cut = IOUtil.getImageIcon("image/menu/cut.png");
        public static ImageIcon edit = IOUtil.getImageIcon("image/menu/edit01.png");
        public static ImageIcon delete = IOUtil.getImageIcon("image/menu/delete.png");
        public static ImageIcon connect = IOUtil.getImageIcon("image/menu/connect.png");
        public static ImageIcon disconnect = IOUtil.getImageIcon("image/menu/disconnect.png");
        public static ImageIcon attribute = IOUtil.getImageIcon("image/menu/Attr.png");
        public static ImageIcon rename = IOUtil.getImageIcon("image/menu/ic_more_rename.png");
        public static ImageIcon open = IOUtil.getImageIcon("image/menu/open.png");
        public static ImageIcon menu_refresh = IOUtil.getImageIcon("image/menu/menu_fresh.png");
        public static ImageIcon data_reset = IOUtil.getImageIcon("image/menu/data_reset.png");
        public static ImageIcon cmd_console = IOUtil.getImageIcon("image/menu/cmd_console.png", 14, 14);
        public static ImageIcon object_add = IOUtil.getImageIcon("image/menu/add_key.png");

        //详情页面
        //https://www.iconfont.cn/collections/detail?spm=a313x.7781069.0.da5a778a4&cid=12191
        public static ImageIcon overtime_done = IOUtil.getImageIcon("image/page/overtime_done.png");
        public static ImageIcon cancel = IOUtil.getImageIcon("image/page/cancel_red.png");
        //https://thenounproject.com/term/remove-row/502136/
        public static ImageIcon row_add = IOUtil.getImageIcon("image/page/add-row.png");
        public static ImageIcon row_add_green = IOUtil.getImageIcon("image/page/add-row-green.png");
        //https://www.iconsdb.com/soylent-red-icons/delete-row-icon.html
        public static ImageIcon row_delete = IOUtil.getImageIcon("image/page/delete-row.png");

        public static ImageIcon result_first = IOUtil.getImageIcon("image/page/result_first.png");
        public static ImageIcon result_last = IOUtil.getImageIcon("image/page/result_last.png");
        public static ImageIcon result_next = IOUtil.getImageIcon("image/page/result_next.png");
        public static ImageIcon result_previous = IOUtil.getImageIcon("image/page/result_previous.png");
        //https://www.easyicon.net/1182036-loop_icon.html
        public static ImageIcon page_reload = IOUtil.getImageIcon("image/page/sync.png");
        public static ImageIcon grid = IOUtil.getImageIcon("image/page/grid_16px_1205810.png");
        public static ImageIcon text = IOUtil.getImageIcon("image/page/translate.png");

        public static ImageIcon close = IOUtil.getImageIcon("image/page/close.png");
        //https://www.easyicon.net/iconsearch/iconset:Material-Design-icons/7/?m=yes&f=iconsetid&s=
        //https://www.easyicon.net/1117056-help_icon.html
        public static ImageIcon help = IOUtil.getImageIcon("image/base/help@1.5x.png");
        public static ImageIcon monitor = IOUtil.getImageIcon("image/base/monitor.png");
        public static ImageIcon settings = IOUtil.getImageIcon("image/base/setting.png");
        public static ImageIcon server = IOUtil.getImageIcon("image/base/datatable.png");
        //https://www.iconfont.cn/collections/detail?spm=a313x.7781069.0.da5a778a4&cid=1903
        public static ImageIcon config = IOUtil.getImageIcon("image/base/config.png");
        
        public static ImageIcon atom = IOUtil.getImageIcon("image/base/atom.png");
        public static ImageIcon about = IOUtil.getImageIcon("image/base/about.png");
        public static ImageIcon expanded = IOUtil.getImageIcon("image/base/expanded.png");
        public static ImageIcon foled = IOUtil.getImageIcon("image/base/foled.png");
        public static ImageIcon gray_arrow_down = IOUtil.getImageIcon("image/base/gray_arrow_down.png");
        public static ImageIcon gray_arrow_left = IOUtil.getImageIcon("image/base/gray_arrow_left.png");
        public static ImageIcon gray_arrow_right = IOUtil.getImageIcon("image/base/gray_arrow_right.png");
        public static ImageIcon gray_arrow_up = IOUtil.getImageIcon("image/base/gray_arrow_up.png");
        public static ImageIcon donate = IOUtil.getImageIcon("image/base/donate.png");
        public static ImageIcon wechatpay = IOUtil.getImageIcon("image/base/wechatpay.jpg",150,150);
        public static ImageIcon alipay = IOUtil.getImageIcon("image/base/alipay.jpg",150,150);
        public static ImageIcon arrow_circle_down = IOUtil.getImageIcon("image/base/arrow_circle_down.png" );
        public static ImageIcon arrow_circle_right = IOUtil.getImageIcon("image/base/arrow_circle_right.png" );
//        public static ImageIcon tempIcon = IOUtil.getImageIcon("image/base/箭头.png" );
    }


//    public final static String REDIS_CONFIG_PATH = System.getProperty("user.home") + File.separatorChar + ".RedisLark.conf";
    public final static String REDIS_CONFIG_PATH = ServiceManager.getInstance().getPath() + File.separatorChar + ".RedisLark.conf";

    public final static class RColor {

        public static Color toolBarGridBackground = new Color(62, 120, 207);
//                public static Color toolBarGridBackground = new Color(35,38,46);
        // 文本框等默认边框颜色
        public static Color defalutInputColor = new Color(150, 150, 150);
        // 文本框等选中边框颜色
        public static Color selectInputColor = new Color(83, 164, 227);

        public static Color themeColor = new Color(206, 221, 237);
//                public static Color themeColor = new Color(57,61,73);
        public static Color tableSelectBackground2 = new Color(229,243,255);
        //文本框灰色
        public static Color grapInputColor = new Color(225, 225, 225);

        public static Color tableHeaderForeground = Color.BLACK;
        public static Color tableHeaderBackground = new Color(219,229,241);
        public static Color tableSelectBackground = new Color(126, 186, 234);
        public static Color tableForeground = Color.BLACK;
        public static Color tableGridColor = new Color(240,240,240);
        public static Color menuItemSelectionBackground = new Color(156, 206, 248);
        public static Color tableOddForeground = new Color(218, 228, 240) ;
        public static Color tableEvenBackground = new Color(254, 254, 254);
        public static Color statePanelColor = new Color(180, 222, 239);
    }

    public final static class StingType{
        public static final  String BINARY = "Binary";
        public static final  String TEXT = "Text";
        public static final  String INDEX = "Index";
    }


    public final static class FontConstant{
        public static final Font baseFont = new Font("宋体", Font.PLAIN,14);
    }
}
