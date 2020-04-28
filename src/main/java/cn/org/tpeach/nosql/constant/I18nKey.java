package cn.org.tpeach.nosql.constant;

/**
 * @author tyz
 * @Title: PublicConstant
 * @ProjectName RedisLark
 * @Description: 国际化key
 * @date 2019-06-23 20:47
 * @since 1.0.0
 */
public class I18nKey {
	
	private static final String REDIS = "REDIS_";

	/**
	 * 国际化对应key
	 */
	public enum RedisResource {
		/**窗口标题*/
		FRAME_TITLE,
		/**设置*/
		SETTING,
		/**帮助*/
		HELP,
		REMAME,
		/**文件*/
		FILE,

		MENU_FLUSH,
		/**新建菜单*/
		MENU_ADD,
		MENU_DEL,
		MENU_EDIT,
		MENU_CONSOLE,
		MENU_DISCONNECT,
		MENU_RELOAD,
		MENU_TOTAL,
		MENU_ADDKEY,
		MENU_FILTERKEY,
		MENU_ATTR,
		MENU_OPENKEY,
		MENU_REMOVEKEY,
		
		//确认
		OK,
		ADD,
		//取消
		CANCEL,
		NEW,
		SERVER,
		CONFIG,
		MONITOR,
		ABOUT,
		CONNECT,
		COPY,
		OPENNEWTAB,
		PLAINTEXT,
		TEXT,
		GRID,
        HOME,
        LOG,
        DELETE,
        DELETES,
		TEST,
		SUCCESS,
		FAIL,
		RETRY,
		CONNECTING,
		SERVERINFO,
		SIM_CHINESE,
		ENGLISH,
		ENABLED,
		DISABLED,
		NAME,
		ADDRESS,
		PORT,
		PASSWORD,
		CLUSTER,
		UN_KNOW,
		EXCEPTION,
		ACTIVATE,
		PUB_SUB,
		DELETEBYKEY,
		GETBYKEY,
		/**不支持的版本异常*/
		VERSION_NOTSUPPORT;


		public String getKey() {
			return REDIS+this.name();
		}


	}
	
}
