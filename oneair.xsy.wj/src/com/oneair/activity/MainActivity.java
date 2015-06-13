package com.oneair.activity;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVObject;
import com.oneair.Constants;
import com.oneair.fragment.ImageFragment;
import com.oneair.fragment.VideoFragment;
import com.oneair.utils.AlarmUtils;
import com.oneair.xsy.R;
import com.thingstec.ble.DeviceServiceActivity;
public class MainActivity extends DeviceServiceActivity {
	private static final String TAG = MainActivity.class.getSimpleName();
	private static final int MAX = 15;
	private static final int MIN = 9;
	private final static String[] NAMES = new String[] { "湿度", "温度", "PM2.5", "PM1.0", "PM10" };
	private final static SimpleDateFormat DATE_FORMAT_ZH = new SimpleDateFormat("yyyy年MM月dd日");
	private final static SimpleDateFormat TIME_FORMAT_ZH = new SimpleDateFormat("E HH:mm");
	private boolean mIsConnect;
	private PendingIntent mOperation;
	private WakeLock mWakeLock;
	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			showVideoDialog();
		}
	};
	private long mCurrentTime, mPluseTime, mSaveTime, mChangeTime, mImageTime;
	private TextView mDateText, mTimeText;
	private TextView mHumidityText, mTemperatureText, mPm25Text, mPm1Text, mPm10Text;
	private TextView mHumidityLabel, mTemperatureLabel, mPm25Label, mPm1Label, mPm10Label;
	private ImageView mEnvImage;
	private int mVideoIndex, mEnvIndex;
	private File[] mVideos;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AVOSCloud.initialize(this, Constants.APP_ID, Constants.APP_KEY);
		setContentView(R.layout.activity_main);
		initViews();
		initDatas();
		final int[] resArray = new int[] { R.drawable.v1, R.drawable.v2, };
		ImageFragment.getInstance(resArray).show(getSupportFragmentManager(), "image");
	}

	private void initDatas() {
		mVideos = new File(Constants.VIDEO_PATH).listFiles();
		if (mVideos == null) {
			Toast.makeText(this, "外卡中没有视频文件", Toast.LENGTH_LONG).show();
			return;
		}
		mOperation = PendingIntent.getBroadcast(this, 0, new Intent("MYALARMRECEIVER"), 0);
		AlarmUtils.startRepeatingAlarm(this, Constants.ONEAIR_DELAY, mOperation);
		registerReceiver();
	}

	private void initViews() {
		mEnvImage = (ImageView) findViewById(R.id.env_image);
		mDateText = (TextView) findViewById(R.id.date_text);
		mTimeText = (TextView) findViewById(R.id.time_text);
		mHumidityText = (TextView) findViewById(R.id.humidity_text);
		mTemperatureText = (TextView) findViewById(R.id.temperature_text);
		mPm25Text = (TextView) findViewById(R.id.pm25_text);
		mPm1Text = (TextView) findViewById(R.id.pm1_text);
		mPm10Text = (TextView) findViewById(R.id.pm10_text);
		mHumidityLabel = (TextView) findViewById(R.id.humidity_label);
		mTemperatureLabel = (TextView) findViewById(R.id.temperature_label);
		mPm25Label = (TextView) findViewById(R.id.pm25_label);
		mPm1Label = (TextView) findViewById(R.id.pm1_label);
		mPm10Label = (TextView) findViewById(R.id.pm10_label);
		mHumidityLabel.setText(NAMES[0]);
		mTemperatureLabel.setText(NAMES[1]);
		mPm25Label.setText(NAMES[2]);
		mPm1Label.setText(NAMES[3]);
		mPm10Label.setText(NAMES[4]);
	}

	@Override
	protected void onResume() {
		super.onResume();
		PowerManager pManager = ((PowerManager) getSystemService(Context.POWER_SERVICE));
		mWakeLock = pManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, TAG);
		mWakeLock.acquire();
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (null != mWakeLock) {
			mWakeLock.release();
		}
	}

	@Override
	protected void onDestroy() {
		unregisterReceiver();
		AlarmUtils.cancelRepeatingAlarm(this, mOperation);
		super.onDestroy();
	}

	@Override
	public void onBackPressed() {
	}

	@Override
	public void updateConnectionState(int resourceId) {
		Toast.makeText(this, resourceId, Toast.LENGTH_LONG).show();
		if (resourceId == R.string.disconnected) {
			mIsConnect = false;
			reconnect();
		} else if (resourceId == R.string.connected) {
			mIsConnect = true;
		}
	}

	@Override
	public void displayData(String data) {
		final long current = System.currentTimeMillis();
		if (current - mCurrentTime > 60 * 1000) {
			mCurrentTime = current;
			setDate(current);
		}
		if (TextUtils.isEmpty(data)) {
			Log.i(TAG, "-----null-----");
			return;
		}
		Log.i(TAG, data);
		if (current - mPluseTime > 10 * 1000) {
			mPluseTime = current;
			setData(data);
		}
		if (current - mSaveTime > 10 * 60 * 1000) {
			mSaveTime = current;
			saveInBackground(data);
		}
		if (current - mChangeTime > 1 * 60 * 1000) {
			mChangeTime = current;
			changeBackground();
		}
		if (current - mImageTime > 6 * 60 * 1000) {
			mImageTime = current;
			final int[] resArray = new int[] { R.drawable.v1, R.drawable.v2, };
			ImageFragment.getInstance(resArray).show(getSupportFragmentManager(), "image");
		}
	}

	@Override
	public String getDeviceAddress() {
		return Constants.DEVICE_ADDRESS;
	}

	private void registerReceiver() {
		final IntentFilter intentFilter = new IntentFilter("MYALARMRECEIVER");
		registerReceiver(mReceiver, intentFilter);
	}

	private void unregisterReceiver() {
		unregisterReceiver(mReceiver);
	}

	private void setDate(long current) {
		final Date now = new Date(current);
		mDateText.setText(DATE_FORMAT_ZH.format(now));
		mTimeText.setText(TIME_FORMAT_ZH.format(now));
	}

	private void changeBackground() {
		if (mEnvIndex == 0) {
			mEnvIndex = 1;
			mEnvImage.setImageResource(R.drawable.env_front);
		} else {
			mEnvIndex = 0;
			mEnvImage.setImageResource(R.drawable.env_front2);
		}
	}

	private void saveInBackground(String data) {
		final String[] sa = data.split(":");
		final int humidity = Integer.parseInt(sa[0]);
		final int temperature = Integer.parseInt(sa[1]);
		int pm25 = Integer.parseInt(sa[2]);
		int pm1 = Integer.parseInt(sa[3]);
		int pm10 = Integer.parseInt(sa[4]);
		final AVObject testObject = new AVObject(Constants.TAG);
		testObject.put("humidity", humidity);
		testObject.put("temperature", temperature);
		testObject.put("pm25", pm25);
		testObject.put("pm1", pm1);
		testObject.put("pm10", pm10);
		testObject.saveInBackground();
	}

	private void setData(String data) {
		final String[] sa = data.split(":");
		final int humidity = Integer.parseInt(sa[0]);
		final int temperature = Integer.parseInt(sa[1]);
		int pm25 = Integer.parseInt(sa[2]);
		int pm1 = Integer.parseInt(sa[3]);
		int pm10 = Integer.parseInt(sa[4]);
		mHumidityText.setText(humidity / 10 + "." + (humidity % 10) + "%");
		mTemperatureText.setText((temperature / 10 - 4) + "." + (temperature % 10) + "℃");
		mPm25Text.setText((pm25 > MAX ? random(pm25) : pm25) + "μg/m³");
		mPm1Text.setText((pm1 > MAX ? random(pm1) : pm1) + "μg/m³");
		mPm10Text.setText((pm10 > MAX ? random(pm10) : pm10) + "μg/m³");
	}

	private int random(int value) {
		return value / 5;
		// (数据类型)(最小值+Math.random()*(最大值-最小值+1))
		// return (int) (MIN + Math.random() * (MAX - MIN + 1));
	}

	private void showVideoDialog() {
		if (mVideos != null) {
			mVideoIndex++;
			if (mVideoIndex >= mVideos.length) {
				mVideoIndex = 0;
			}
			try {
				VideoFragment.getInstance(mVideos[mVideoIndex].getAbsolutePath()).show(getSupportFragmentManager(), "video");
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}
}
