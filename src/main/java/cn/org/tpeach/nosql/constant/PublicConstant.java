package cn.org.tpeach.nosql.constant;

import cn.org.tpeach.nosql.bean.DicBean;
import cn.org.tpeach.nosql.tools.CollectionUtils;
import cn.org.tpeach.nosql.tools.GsonUtil;
import cn.org.tpeach.nosql.tools.IOUtil;
import cn.org.tpeach.nosql.tools.StringUtils;
import cn.org.tpeach.nosql.view.common.ServiceManager;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

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
        /** 7位ASCII字符，也叫作ISO646-US、Unicode字符集的基本拉丁块 */
        public static final String US_ASCII = "US-ASCII";

        /** ISO 拉丁字母表 No.1，也叫作 ISO-LATIN-1 */
        public static final String ISO_8859_1 = "ISO-8859-1";

        /** 8 位 UCS 转换格式 */
        public static final String UTF_8 = "UTF-8";

        /** 16 位 UCS 转换格式，Big Endian（最低地址存放高位字节）字节顺序 */
        public static final String UTF_16BE = "UTF-16BE";

        /** 16 位 UCS 转换格式，Little-endian（最高地址存放低位字节）字节顺序 */
        public static final String UTF_16LE = "UTF-16LE";

        /** 16 位 UCS 转换格式，字节顺序由可选的字节顺序标记来标识 */
        public static final String UTF_16 = "UTF-16";

        /** 中文超大字符集 */
        public static final String GBK = "GBK";

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
    @Slf4j
    public final static class Image {
        //https://www.easyicon.net/1210880-home_icon.html

        private static Map<String, ImageIcon> map = new ConcurrentHashMap<>();
        private static Queue<DicBean> queue = new ConcurrentLinkedQueue<>();

        public final static String home = "image/base/home.png";
        //https://www.easyicon.net/1211616-github_media_social_icon.html
        public final static String github = "image/base/github@2x.png";
        public final static String changeLog = "image/base/changeLog@2x.png";
        public final static String logo = "image/base/logo.png";
        public final static String logo_16 = "image/base/logo.png" ;
        public final static String redis_db = "image/base/redisDb.png" ;
        public final static String redis_server = "image/tree/redis_server.png";
        //https://www.easyicon.net/28750-database_icon.html
        public final static String database = "image/tree/database.png";
        public final static String key_icon = "image/tree/key.png";
        public final static String folder_database = "image/tree/folder_database.png";
        public final static String arrow_down_blue = "image/base/arrow_down.png" ;
        public final static String arrow_right_blue = "image/base/arrow_right.png" ;
        public final static String arrow_left_blue = "image/base/arrow_left.png" ;
        public final static String arrow_up_blue = "image/base/arrow_up.png" ;
        public final static String separator_v = "image/base/separator_v.png";
        public final static String separator_v_solid = "image/base/separator_v_solid.png";
        public final static String resultset_refresh = "image/menu/resultset_refresh.png";
        public final static String refresh = "image/base/refresh.png";
        public final static String loading01 = "image/base/loading01.png";
        public final static String loading02 = "image/base/loading02.png";
        public final static String loading03 = "image/base/loading03.png";
        public final static String loading04 = "image/base/loading04.png";
        public final static String loading05 = "image/base/loading05.png";
        public final static String loading06 = "image/base/loading06.png";
        public final static String loading07 = "image/base/loading07.png";
        public final static String arrow_down_scroll = "image/base/arrow_down_scroll.png";
        public final static String arrow_up_scroll = "image/base/arrow_up_scroll.png";
        public final static String arrow_right_scroll = "image/base/arrow_right_scroll.png";
        public final static String arrow_left_scroll = "image/base/arrow_left_scroll.png";
        //菜單
        public final static String copy = "image/menu/copy.png";
        public final static String paste = "image/menu/paste.png";
        public final static String cut = "image/menu/cut.png";
        public final static String edit = "image/menu/edit01.png";
        public final static String delete = "image/menu/delete.png";
        public final static String connect = "image/menu/connect.png";
        public final static String disconnect = "image/menu/disconnect.png";
        public final static String attribute = "image/menu/Attr.png";
        public final static String rename = "image/menu/ic_more_rename.png";
        public final static String open = "image/menu/open.png";
        public final static String menu_refresh = "image/menu/menu_fresh.png";
        public final static String data_reset = "image/menu/data_reset.png";
        public final static String cmd_console = "image/menu/cmd_console.png";
        public final static String object_add = "image/menu/add_key.png";
        public final static String active_data = "image/menu/active_data.png";


        //详情页面
        //https://www.iconfont.cn/collections/detail?spm=a313x.7781069.0.da5a778a4&cid=12191
        public final static String overtime_done = "image/page/overtime_done.png";
        public final static String cancel = "image/page/cancel_red.png";
        //https://thenounproject.com/term/remove-row/502136/
        public final static String row_add = "image/page/add-row.png";
        public final static String row_add_green = "image/page/add-row-green.png";
        //https://www.iconsdb.com/soylent-red-icons/delete-row-icon.html
        public final static String row_delete = "image/page/delete-row.png";

        public final static String result_first = "image/page/result_first.png";
        public final static String result_last = "image/page/result_last.png";
        public final static String result_next = "image/page/result_next.png";
        public final static String result_previous = "image/page/result_previous.png";
        //https://www.easyicon.net/1182036-loop_icon.html
        public final static String page_reload = "image/page/sync.png";
        public final static String grid = "image/page/grid_16px_1205810.png";
        public final static String text = "image/page/translate.png";

        public final static String close = "image/page/close.png";
        //https://www.easyicon.net/iconsearch/iconset:Material-Design-icons/7/?m=yes&f=iconsetid&s=
        //https://www.easyicon.net/1117056-help_icon.html
        public final static String help = "image/base/help@1.5x.png";
        public final static String monitor = "image/base/monitor.png";
        public final static String settings = "image/base/setting.png";
        public final static String server = "image/base/datatable.png";
        public final static String server16 = "image/base/datatable.png" ;
        //https://www.iconfont.cn/collections/detail?spm=a313x.7781069.0.da5a778a4&cid=1903
        public final static String config = "image/base/config.png";

        public final static String atom = "image/base/atom.png";
        public final static String about = "image/base/about.png";
        public final static String st_san_config = "image/base/st_san_config.png";
        public final static String expanded = "image/base/expanded.png";
        public final static String foled = "image/base/foled.png";
        public final static String gray_arrow_down = "image/base/gray_arrow_down.png";
        public final static String gray_arrow_left = "image/base/gray_arrow_left.png";
        public final static String gray_arrow_right = "image/base/gray_arrow_right.png";
        public final static String gray_arrow_up = "image/base/gray_arrow_up.png";
        public final static String donate = "image/base/donate.png";
        public final static String wechatpay = "image/base/wechatpay.jpg" ;
        public final static String alipay = "image/base/alipay.jpg" ;
        public final static String arrow_circle_down = "image/base/arrow_circle_down.png" ;
        public final static String arrow_circle_right = "image/base/arrow_circle_right.png" ;
        public final static String switch_on = "image/base/switch_on.png" ;
        public final static String switch_off = "image/base/switch_off.png";
        //        public final static String tempIcon = "image/base/箭头.png";
        public final static String loading_g = "image/base/loading_g%s.gif";
        public final static String  command = "image/base/command.png";
        public final static String loading_o = "image/base/o_loading.gif";
        public final static String tool = "image/base/tool.png" ;
        public final static String tool_web = "image/base/toolweb.png" ;
        public final static String batchImport = "image/base/batchImport.png" ;
        public static synchronized ImageIcon getImageIcon(String path){
            return  getImageIcon(path,null,null);
        }
        public static synchronized ImageIcon getImageIcon(String path,Integer width,Integer height){
            if(StringUtils.isBlank(path)){
                return null;
            }
            ImageIcon imageIcon = null;
            String key = width != null && height != null? path+width+height : path;
            if(map.containsKey(key)){
               imageIcon = map.get(key);
                updateCatchTime(key);
            }else{
               if(width != null && height != null){
                   imageIcon = IOUtil.getImageIcon(path,width,height);
               }else{
                   imageIcon = IOUtil.getImageIcon(path );
               }
               map.put(key,imageIcon);
               queue.add(DicBean.builder().code(key).value(System.currentTimeMillis()+"").build());
           }
           if(CollectionUtils.isNotEmpty(queue)){
               DicBean peek = queue.peek();
               //30分钟不使用则移除
               while (peek != null && (System.currentTimeMillis() - Long.valueOf(peek.getValue())) > 1800000){
                   peek = queue.poll();
                   if(peek != null){
                       map.remove(peek.getCode());
                   }
                   peek = queue.peek();
               }
           }
           return imageIcon;
        }

        private static void updateCatchTime(String key) {
            if(CollectionUtils.isNotEmpty(queue)){
                Iterator<DicBean> iterator = queue.iterator();
                while (iterator.hasNext()){
                    DicBean next = iterator.next();
                    if(key.equals(next.getCode())){
                        //更新時間
                        next.setValue(System.currentTimeMillis()+"");
                        iterator.remove();
                        boolean offer = queue.offer(next);
                        if(!offer){
                            log.error("獲取圖像添加失敗");
                        }
                        break;
                    }

                }
            }
        }

        public static synchronized void replaceImageCatch(String path,ImageIcon imageIcon){
            if (StringUtils.isBlank(path) || imageIcon == null) {
                return;
            }
            map.put(path,imageIcon);
            updateCatchTime(path);
        }
        public static boolean catchContainsKey(String path,Integer width,Integer height) {
            String key = width != null && height != null ? path + width + height : path;
            return map.containsKey(key);
        }
        public static boolean catchContainsKey(String path) {
            return map.containsKey(path);
        }
        public static synchronized void removeImageCatch(String path,Integer width,Integer height) {
            if (StringUtils.isBlank(path)) {
                return;
            }
            String key = width != null && height != null ? path + width + height : path;
            map.remove(key);
            Iterator<DicBean> iterator = queue.iterator();
            while (iterator.hasNext()) {
                DicBean next = iterator.next();
                if (key.equals(next.getCode())) {
                    iterator.remove();
                    break;

                }
            }
        }
        public static synchronized void removeImageCatch(String path) {
            removeImageCatch(path,null,null);
        }
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

    public final static class ProjectEnvironment{
        public static final  String DEV = "dev";
        public static final  String TEST = "test";
        public static final  String BETA = "beta";
        public static final  String RELEASE = "release";
    }
}
