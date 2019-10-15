package cn.org.tpeach.nosql.i18n;

import java.util.ListResourceBundle;

import cn.org.tpeach.nosql.constant.I18nKey;

public class PlatformResource_en_US extends ListResourceBundle {
    static final Object[][] contents = new String[][]
            {
                    {I18nKey.RedisResource.FRAME_TITLE.getKey(), "Redis-Connect"},

                    {I18nKey.RedisResource.VERSION_NOTSUPPORT.getKey(), "The version of redis server is too low to support this function"},
                    {I18nKey.RedisResource.SETTING.getKey(), "Setting"},
                    {I18nKey.RedisResource.HELP.getKey(), "Help"},
                    {I18nKey.RedisResource.REMAME.getKey(), "Rename"},
                    {I18nKey.RedisResource.FILE.getKey(), "File"},
                    {I18nKey.RedisResource.HOME.getKey(), "Home"},
                    {I18nKey.RedisResource.LOG.getKey(), "Log"},
                  //菜单相关
                    {I18nKey.RedisResource.MENU_ADD.getKey(), "Add"},
                    {I18nKey.RedisResource.MENU_DEL.getKey(),"Delete"},
                    {I18nKey.RedisResource.MENU_EDIT.getKey(), "Edit"},
                    {I18nKey.RedisResource.MENU_CONSOLE.getKey(), "Console"},
                    {I18nKey.RedisResource.MENU_DISCONNECT.getKey(),"Disconnect"},
                    {I18nKey.RedisResource.MENU_RELOAD.getKey(), "Reload"},
                    {I18nKey.RedisResource.MENU_TOTAL.getKey(), "Total"},
                    {I18nKey.RedisResource.MENU_FLUSH.getKey(), "Flush"},
                    {I18nKey.RedisResource.MENU_ADDKEY.getKey(), "Add Key"},
                    {I18nKey.RedisResource.MENU_FILTERKEY.getKey(), "Filter Key"},
                    {I18nKey.RedisResource.MENU_ATTR.getKey(), "Attr"},
                    {I18nKey.RedisResource.MENU_OPENKEY.getKey(), "Open"},
                    {I18nKey.RedisResource.MENU_REMOVEKEY.getKey(), "Delete"},

                    {I18nKey.RedisResource.DELETE.getKey(), "Delete"},
                    {I18nKey.RedisResource.DELETES.getKey(), "Batches deletes"},
                    {I18nKey.RedisResource.OK.getKey(), "OK"},
                    {I18nKey.RedisResource.CANCEL.getKey(), "Cancel"},
                    {I18nKey.RedisResource.ADD.getKey(), "Add"},
                    {I18nKey.RedisResource.NEW.getKey(), "New"},
                    {I18nKey.RedisResource.SERVER.getKey(), "Server"},
                    {I18nKey.RedisResource.CONFIG.getKey(), "Config"},
                    {I18nKey.RedisResource.MONITOR.getKey(), "Monitor"},
                    {I18nKey.RedisResource.ABOUT.getKey(), "About"},
                    {I18nKey.RedisResource.CONNECT.getKey(), "Connect"},
                    {I18nKey.RedisResource.COPY.getKey(), "Copy"},
                    {I18nKey.RedisResource.OPENNEWTAB.getKey(), "Open new tab"},
                    {I18nKey.RedisResource.PLAINTEXT.getKey(), "Plain Text"},
                    {I18nKey.RedisResource.TEXT.getKey(), "Text"},
                    {I18nKey.RedisResource.GRID.getKey(), "Grid"},
                    {I18nKey.RedisResource.TEST.getKey(), "Test"},
                    {I18nKey.RedisResource.SUCCESS.getKey(), "Success"},
                    {I18nKey.RedisResource.FAIL.getKey(), "Fail"},
                    {I18nKey.RedisResource.RETRY.getKey(), "Retry"},
                    {I18nKey.RedisResource.CONNECTING.getKey(), "Connecting"},
                    {I18nKey.RedisResource.SERVERINFO.getKey(), "Server Info"},
                    {I18nKey.RedisResource.SIM_CHINESE.getKey(), "Chinese(simplified)"},
                    {I18nKey.RedisResource.ENGLISH.getKey(), "English"},
            };

    @Override
    protected Object[][] getContents() {
        return contents;
    }

}