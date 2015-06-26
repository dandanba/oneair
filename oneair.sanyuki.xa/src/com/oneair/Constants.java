package com.oneair;

import java.io.File;
import android.os.Environment;

public class Constants {
	public static String APP_ID = "eawa684d1i26c65rtrakerb47lph5ldo6cexw0go904x81tx";
	public static String APP_KEY = "1ocx5vu2egfyhsud3rifpxv7t21kb6uu2lv8ywdr6erzl32i";
	public final static String TAG = "sanyukixa";
	public final static String SD_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;
	public final static String VIDEO_PATH = SD_PATH + TAG + File.separator;
	public final static long ONEAIR_DELAY = 10 * 60 * 1000;
	public final static String DEVICE_ADDRESS = "B4:99:4C:4C:CC:37";// 西安展示
	// "B4:99:4C:65:6D:DA";// 上海展会
	// "B4:99:4C:4C:D2:B2"// 望京的sanyuki
	// "B4:99:4C:4C:CC:37" xsywj // 望京的西少爷
	// "B4:99:4C:65:6D:DA" sanyuki // 望京的sanyuki
	// "B4:99:4C:53:9F:E9" xsy // 五道口的西少爷
}
