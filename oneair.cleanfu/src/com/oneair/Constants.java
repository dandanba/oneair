package com.oneair;

import java.io.File;
import android.os.Environment;

public class Constants {
	public static boolean CLOUD_READ = false; // 从云端读取还是蓝牙读取
	public static String PACKAGE_NAME = "com.oneair.cleanfu"; // 程序的包名
	public static String UMENG_CLOUD_KEY = "55a33cc967e58e7c69001f46"; // 对应 PACKAGE_NAME 的有盟key com.oneair.cleanfu.cloud
	public static String UMENG_BLUETOOTH_KEY = "55a33d7067e58e63d400071f"; // 对应 PACKAGE_NAME 的有盟key com.oneair.cleanfu.bluetooth
	public final static String DEVICE_ADDRESS = "B4:99:4C:4C:D5:0B"; // 蓝牙的 device address
	public final static String AVOS_APP_TAG = "cleanfu";
	public static String UPDATE_ID = "559f45ebe4b0d6d68fe82811"; // 编辑的id
	public static String AVOS_APP_ID = "eawa684d1i26c65rtrakerb47lph5ldo6cexw0go904x81tx";
	public static String AVOS_APP_KEY = "1ocx5vu2egfyhsud3rifpxv7t21kb6uu2lv8ywdr6erzl32i";
	public final static String VIDEO_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + //
			File.separator + "oneair" + File.separator;
	public final static long VIDEO_CHANGE_DELAY = 10 * 60 * 1000;
}
