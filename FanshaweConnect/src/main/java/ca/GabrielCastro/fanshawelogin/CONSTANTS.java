package ca.GabrielCastro.fanshawelogin;

public interface CONSTANTS {

	public static final String PREFS_NAME = "FanshaweAutoLogOn";

	public static final String KEY_USERNAME   = "userKey";
	public static final String KEY_PASSWD     = "passKey";
	public static final String KEY_FIRST_NAME = "first_name";
	public static final String KEY_LAST_NAME  = "last_name";

    String KEY_AUTO_CONNECT = "auto_connect_wifi";

    public static final String LAST_VERSION_KEY = "lastVersionRan";
	public static final String KEY_PERSON = "thePeresonsName";
	public static final float MAJOR_MINOR = 0.0F;

	public static final byte DEBUG_ALL_HTTP = 0x01; // 0000 0001
	public static final byte DEBUG_ALL_CON_STATE_CHANGE = 0x02;// 0000 0010
	public static final byte DEBUG_ALL_LOGONS = 0x04;// 0000 0100
	public static final byte DEBUG_THREAD_LONGER = 0x08;// 0000 1000
	public static final byte DEBUG_FORCE_INIT = 0x10;// 0001 0000

    String SECOND_URL
            = "https://www.fanshaweonline.ca/d2l/lp/homepage/LegacyWidgetViewer.d2l" +
            "?ou=29533" +
            "&WidgetPath=%2fd2l%2flms%2fwidgets%2flinks%2fuser_links.d2l" +
            "&WidgetId=13" +
            "&d2l_headingLevel=3" +
            "&d2l_body_type=3";

	public static byte DEBUG_LEVEL = (DEBUG_ALL_HTTP
			| DEBUG_ALL_CON_STATE_CHANGE | DEBUG_ALL_LOGONS | DEBUG_THREAD_LONGER);// 0000

}
