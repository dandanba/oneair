package com.oneair;

import java.io.File;
import android.os.Environment;

public class Constants {
	public static boolean CLOUD_READ = true; // 从云端读取还是蓝牙读取
	public static String PACKAGE_NAME = "com.oneair.sanyuki"; // 程序的包名
	public static String UMENG_KEY = "5598a8c867e58e432e0004ce"; // 对应 PACKAGE_NAME 的有盟key
	public final static String DEVICE_ADDRESS = "B4:99:4C:4C:D2:B2"; // 蓝牙的 device address
	public final static String AVOS_APP_TAG = "sanyuki";
	public static String AVOS_APP_ID = "eawa684d1i26c65rtrakerb47lph5ldo6cexw0go904x81tx";
	public static String AVOS_APP_KEY = "1ocx5vu2egfyhsud3rifpxv7t21kb6uu2lv8ywdr6erzl32i";
	public final static String VIDEO_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + //
			File.separator + "oneair" + File.separator;
	public final static long VIDEO_CHANGE_DELAY = 10 * 60 * 1000;
}
