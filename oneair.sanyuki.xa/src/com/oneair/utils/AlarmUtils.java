package com.oneair.utils;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.os.SystemClock;
public class AlarmUtils {
	public static void startRepeatingAlarm(Context context, long intervalMillis, PendingIntent operation) {
		if (operation == null) {
			return;
		}
		final AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), intervalMillis, operation);
	}

	public static void cancelRepeatingAlarm(Context context, PendingIntent operation) {
		if (operation == null) {
			return;
		}
		final AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		am.cancel(operation);
	}
}