package com.xyw.smartlock.common;

public class HttpServerAddress {
	//内蒙
	private static final String URL = "http://114.55.253.105";
	//航天中电科技    北京航天长征飞行器研究所
//	private static final String URL = "http://www.xywlock.com:8081";
	//公司
//	private static final String URL = "http://www.xywlock.com:81";
	//中能瑞通
//	private static final String URL = "http://192.168.99.243:8080";

	public static final String BASE_URL = URL + "/smartlock/ajax/lockapi.ashx";
	public static final String UPLOADS = URL + "/uploads/";
	public static final String BASE_IMAGE = URL + "/uploads/";
	
	public static final String REGISTER = BASE_URL+"?m=Register";
	public static final String RESTPASS = BASE_URL+"?m=restpass";
	public static final String MODPASS = BASE_URL+"?m=modpass";
	//重新获取当区域ID，区域名称和开始时间，结束时间
	public static final String USERINFO = BASE_URL+"?m=GetLoginInfo";
	public static final String SETOPZONE = BASE_URL+"?m=setopzone";
	public static final String SETOPTYPE = BASE_URL+"?m=setoptype";
	public static final String UPLOADFILE = BASE_URL+"?m=uploadfile";
	public static final String INSETTLOCKTASK = BASE_URL+"?m=insertlocktask";
	public static final String GETLOCKTASKLIST = BASE_URL+"?m=GetLockTaskList";
	public static final String CHECKLOCKTASK = BASE_URL+"?m=checklocktask";
	public static final String DELETELOCKTASK = BASE_URL+"?m=deletelocktask";
	public static final String PDLOCKMAXID = BASE_URL+"?m=getlockmaxid";
	public static final String PDWRITEID = BASE_URL+"?m=insertlockminfo";
	public static final String GETLOCKPASSWD = BASE_URL+"?m=getlockpasswd";
	public static final String SETLOCKPASSWDNUM = BASE_URL+"?m=setlockpasswdnum";
	public static final String HEARD = BASE_URL+"?m=heard";
}
