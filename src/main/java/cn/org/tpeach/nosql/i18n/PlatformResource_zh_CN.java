package cn.org.tpeach.nosql.i18n;

import cn.org.tpeach.nosql.constant.I18nKey;

import java.util.ListResourceBundle;

public class PlatformResource_zh_CN extends ListResourceBundle {
    static final Object[][] CONTENTS = new String[][]
            {
                    {I18nKey.RedisResource.FRAME_TITLE.getKey(), "Redis连接工具"},

                    {I18nKey.RedisResource.VERSION_NOTSUPPORT.getKey(), "Redis服务器版本低，不支持本功能"},
                    {I18nKey.RedisResource.SETTING.getKey(), "设置"},
                    {I18nKey.RedisResource.HELP.getKey(), "帮助"},
                    {I18nKey.RedisResource.REMAME.getKey(), "重命名"},
                    {I18nKey.RedisResource.FILE.getKey(), "文件"},
                    {I18nKey.RedisResource.HOME.getKey(), "首页"},
                    {I18nKey.RedisResource.LOG.getKey(), "日志"},
                    //菜单相关
                    
                    {I18nKey.RedisResource.MENU_ADD.getKey(), "添加"},
                    {I18nKey.RedisResource.MENU_DEL.getKey(), "删除"},
                    {I18nKey.RedisResource.MENU_EDIT.getKey(), "编辑"},
                    {I18nKey.RedisResource.MENU_CONSOLE.getKey(), "控制台"},
                    {I18nKey.RedisResource.MENU_DISCONNECT.getKey(), "断开连接"},
                    {I18nKey.RedisResource.MENU_RELOAD.getKey(), "重新加载"},
                    {I18nKey.RedisResource.MENU_TOTAL.getKey(), "总数统计"},
                    {I18nKey.RedisResource.MENU_FLUSH.getKey(), "清空"},
                    {I18nKey.RedisResource.MENU_ADDKEY.getKey(), "添加键"},
                    {I18nKey.RedisResource.MENU_FILTERKEY.getKey(), "过滤键"},
                    {I18nKey.RedisResource.MENU_ATTR.getKey(), "属性"},
                    {I18nKey.RedisResource.MENU_OPENKEY.getKey(), "打开"},
                    {I18nKey.RedisResource.MENU_REMOVEKEY.getKey(), "删除"},
                    {I18nKey.RedisResource.DELETE.getKey(), "删除"},
                    {I18nKey.RedisResource.DELETES.getKey(), "批量删除"},
                    {I18nKey.RedisResource.OK.getKey(), "确认"},
                    {I18nKey.RedisResource.CANCEL.getKey(), "取消"},
                    {I18nKey.RedisResource.ADD.getKey(), "添加"},
                    {I18nKey.RedisResource.NEW.getKey(), "创建"},
                    {I18nKey.RedisResource.SERVER.getKey(), "服务"},
                    {I18nKey.RedisResource.CONFIG.getKey(), "配置"},
                    {I18nKey.RedisResource.MONITOR.getKey(), "监控"},
                    {I18nKey.RedisResource.ABOUT.getKey(), "关于"},
                    {I18nKey.RedisResource.CONNECT.getKey(), "连接"},
                    {I18nKey.RedisResource.COPY.getKey(), "复制"},
                    {I18nKey.RedisResource.OPENNEWTAB.getKey(), "打开新的标签"},
                    {I18nKey.RedisResource.PLAINTEXT.getKey(), "文本"},
                    {I18nKey.RedisResource.TEXT.getKey(), "文本"},
                    {I18nKey.RedisResource.GRID.getKey(), "网格"},
                    {I18nKey.RedisResource.TEST.getKey(), "测试"},
                    {I18nKey.RedisResource.SUCCESS.getKey(), "成功"},
                    {I18nKey.RedisResource.FAIL.getKey(), "失败"},
                    {I18nKey.RedisResource.RETRY.getKey(), "重试"},
                    {I18nKey.RedisResource.CONNECTING.getKey(), "连接中"},
                    {I18nKey.RedisResource.SERVERINFO.getKey(), "服务信息"},
                    {I18nKey.RedisResource.SIM_CHINESE.getKey(), "简体中文"},
                    {I18nKey.RedisResource.ENGLISH.getKey(), "英文"},
                    {I18nKey.RedisResource.ENABLED.getKey(), "启用"},
                    {I18nKey.RedisResource.DISABLED.getKey(), "禁用"},
                    {I18nKey.RedisResource.NAME.getKey(), "名称"},
                    {I18nKey.RedisResource.ADDRESS.getKey(), "地址"},
                    {I18nKey.RedisResource.PORT.getKey(), "端口"},
                    {I18nKey.RedisResource.PASSWORD.getKey(), "密码"},
                    {I18nKey.RedisResource.CLUSTER.getKey(), "集群"},
                    {I18nKey.RedisResource.UN_KNOW.getKey(), "未知"},
                    {I18nKey.RedisResource.EXCEPTION.getKey(), "异常"},
                    {I18nKey.RedisResource.ACTIVATE.getKey(), "设为活动对象"},
            };

    @Override
    protected Object[][] getContents() {
        return CONTENTS;
    }

}