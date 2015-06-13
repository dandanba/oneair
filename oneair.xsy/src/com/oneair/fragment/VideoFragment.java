package com.oneair.fragment;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import android.content.Context;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import com.oneair.video.DensityUtil;
import com.oneair.video.FullScreenVideoView;
import com.oneair.video.LightnessController;
import com.oneair.video.VolumnController;
import com.oneair.xsy.R;
public class VideoFragment extends DialogFragment implements OnClickListener, OnSeekBarChangeListener {
	public static DialogFragment getInstance(String path) {
		final DialogFragment fragment = new VideoFragment();
		final Bundle args = new Bundle();
		args.putString("path", path);
		fragment.setArguments(args);
		return fragment;
	}

	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 1:
				if (mVideo.getCurrentPosition() > 0) {
					mPlayTime.setText(formatTime(mVideo.getCurrentPosition()));
					int progress = mVideo.getCurrentPosition() * 100 / mVideo.getDuration();
					mSeekBar.setProgress(progress);
					if (mVideo.getCurrentPosition() > mVideo.getDuration() - 100) {
						mPlayTime.setText("00:00");
						mSeekBar.setProgress(0);
					}
					mSeekBar.setSecondaryProgress(mVideo.getBufferPercentage());
				} else {
					mPlayTime.setText("00:00");
					mSeekBar.setProgress(0);
				}
				break;
			case 2:
				showOrHide();
				break;
			default:
				break;
			}
		}
	};
	// 自定义VideoView
	private FullScreenVideoView mVideo;
	// 头部View
	private View mTopView;
	// 底部View
	private View mBottomView;
	// 视频播放拖动条
	private SeekBar mSeekBar;
	private ImageView mPlay;
	private TextView mPlayTime;
	private TextView mDurationTime;
	// 音频管理器
	private AudioManager mAudioManager;
	// 屏幕宽高
	private float width;
	private float height;
	// 视频播放时间
	private int playTime;
	// 自动隐藏顶部和底部View的时间
	private static final int HIDE_TIME = 5000;
	// 声音调节Toast
	private VolumnController volumnController;
	// 原始屏幕亮度
	private int orginalLight;
	private float mLastMotionX;
	private float mLastMotionY;
	private int startX;
	private int startY;
	private int threshold;
	private boolean isClick = true;
	private OnTouchListener mTouchListener = new OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			final float x = event.getX();
			final float y = event.getY();
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				mLastMotionX = x;
				mLastMotionY = y;
				startX = (int) x;
				startY = (int) y;
				break;
			case MotionEvent.ACTION_MOVE:
				float deltaX = x - mLastMotionX;
				float deltaY = y - mLastMotionY;
				float absDeltaX = Math.abs(deltaX);
				float absDeltaY = Math.abs(deltaY);
				// 声音调节标识
				boolean isAdjustAudio = false;
				if (absDeltaX > threshold && absDeltaY > threshold) {
					if (absDeltaX < absDeltaY) {
						isAdjustAudio = true;
					} else {
						isAdjustAudio = false;
					}
				} else if (absDeltaX < threshold && absDeltaY > threshold) {
					isAdjustAudio = true;
				} else if (absDeltaX > threshold && absDeltaY < threshold) {
					isAdjustAudio = false;
				} else {
					return true;
				}
				if (isAdjustAudio) {
					if (x < width / 2) {
						if (deltaY > 0) {
							lightDown(absDeltaY);
						} else if (deltaY < 0) {
							lightUp(absDeltaY);
						}
					} else {
						if (deltaY > 0) {
							volumeDown(absDeltaY);
						} else if (deltaY < 0) {
							volumeUp(absDeltaY);
						}
					}
				} else {
					if (deltaX > 0) {
						forward(absDeltaX);
					} else if (deltaX < 0) {
						backward(absDeltaX);
					}
				}
				mLastMotionX = x;
				mLastMotionY = y;
				break;
			case MotionEvent.ACTION_UP:
				if (Math.abs(x - startX) > threshold || Math.abs(y - startY) > threshold) {
					isClick = false;
				}
				mLastMotionX = 0;
				mLastMotionY = 0;
				startX = (int) 0;
				if (isClick) {
					showOrHide();
				}
				isClick = true;
				break;
			default:
				break;
			}
			return true;
		}
	};
	private String path;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		path = getArguments().getString("path");
		setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.fragment_video, container);
		volumnController = new VolumnController(getActivity());
		mVideo = (FullScreenVideoView) view.findViewById(R.id.videoview);
		mPlayTime = (TextView) view.findViewById(R.id.play_time);
		mDurationTime = (TextView) view.findViewById(R.id.total_time);
		mPlay = (ImageView) view.findViewById(R.id.play_btn);
		mSeekBar = (SeekBar) view.findViewById(R.id.seekbar);
		mTopView = view.findViewById(R.id.top_layout);
		mBottomView = view.findViewById(R.id.bottom_layout);
		mAudioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
		width = DensityUtil.getWidthInPx(getActivity());
		height = DensityUtil.getHeightInPx(getActivity());
		threshold = DensityUtil.dip2px(getActivity(), 18);
		orginalLight = LightnessController.getLightness(getActivity());
		mPlay.setOnClickListener(this);
		mSeekBar.setOnSeekBarChangeListener(this);
		playVideo();
		return view;
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			height = DensityUtil.getWidthInPx(getActivity());
			width = DensityUtil.getHeightInPx(getActivity());
		} else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			width = DensityUtil.getWidthInPx(getActivity());
			height = DensityUtil.getHeightInPx(getActivity());
		}
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public void onPause() {
		super.onPause();
		LightnessController.setLightness(getActivity(), orginalLight);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mHandler.removeMessages(0);
		mHandler.removeCallbacksAndMessages(null);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.play_btn:
			if (mVideo.isPlaying()) {
				mVideo.pause();
				mPlay.setImageResource(R.drawable.video_btn_down);
			} else {
				mVideo.start();
				mPlay.setImageResource(R.drawable.video_btn_on);
			}
			break;
		default:
			break;
		}
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		mHandler.postDelayed(hideRunnable, HIDE_TIME);
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		mHandler.removeCallbacks(hideRunnable);
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		if (fromUser) {
			int time = progress * mVideo.getDuration() / 100;
			mVideo.seekTo(time);
		}
	}

	private void backward(float delataX) {
		int current = mVideo.getCurrentPosition();
		int backwardTime = (int) (delataX / width * mVideo.getDuration());
		int currentTime = current - backwardTime;
		mVideo.seekTo(currentTime);
		mSeekBar.setProgress(currentTime * 100 / mVideo.getDuration());
		mPlayTime.setText(formatTime(currentTime));
	}

	private void forward(float delataX) {
		int current = mVideo.getCurrentPosition();
		int forwardTime = (int) (delataX / width * mVideo.getDuration());
		int currentTime = current + forwardTime;
		mVideo.seekTo(currentTime);
		mSeekBar.setProgress(currentTime * 100 / mVideo.getDuration());
		mPlayTime.setText(formatTime(currentTime));
	}

	private void volumeDown(float delatY) {
		int max = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		int current = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		int down = (int) (delatY / height * max * 3);
		int volume = Math.max(current - down, 0);
		mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);
		int transformatVolume = volume * 100 / max;
		volumnController.show(transformatVolume);
	}

	private void volumeUp(float delatY) {
		int max = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		int current = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		int up = (int) ((delatY / height) * max * 3);
		int volume = Math.min(current + up, max);
		mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);
		int transformatVolume = volume * 100 / max;
		volumnController.show(transformatVolume);
	}

	private void lightDown(float delatY) {
		int down = (int) (delatY / height * 255 * 3);
		int transformatLight = LightnessController.getLightness(getActivity()) - down;
		LightnessController.setLightness(getActivity(), transformatLight);
	}

	private void lightUp(float delatY) {
		int up = (int) (delatY / height * 255 * 3);
		int transformatLight = LightnessController.getLightness(getActivity()) + up;
		LightnessController.setLightness(getActivity(), transformatLight);
	}

	private void playVideo() {
		mVideo.setVideoPath(path);
		mVideo.requestFocus();
		mVideo.setOnPreparedListener(new OnPreparedListener() {
			@Override
			public void onPrepared(MediaPlayer mp) {
				mVideo.setVideoWidth(mp.getVideoWidth());
				mVideo.setVideoHeight(mp.getVideoHeight());
				mVideo.start();
				if (playTime != 0) {
					mVideo.seekTo(playTime);
				}
				mHandler.removeCallbacks(hideRunnable);
				mHandler.postDelayed(hideRunnable, HIDE_TIME);
				mDurationTime.setText(formatTime(mVideo.getDuration()));
				Timer timer = new Timer();
				timer.schedule(new TimerTask() {
					@Override
					public void run() {
						mHandler.sendEmptyMessage(1);
					}
				}, 0, 1000);
			}
		});
		mVideo.setOnCompletionListener(new OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp) {
				// mPlay.setImageResource(R.drawable.video_btn_down);
				// mPlayTime.setText("00:00");
				// mSeekBar.setProgress(0);
				dismiss();
			}
		});
		mVideo.setOnTouchListener(mTouchListener);
	}

	private Runnable hideRunnable = new Runnable() {
		@Override
		public void run() {
			showOrHide();
		}
	};

	private String formatTime(long time) {
		DateFormat formatter = new SimpleDateFormat("mm:ss");
		return formatter.format(new Date(time));
	}

	private void showOrHide() {
		if (mTopView.getVisibility() == View.VISIBLE) {
			mTopView.clearAnimation();
			Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.option_leave_from_top);
			animation.setAnimationListener(new AnimationImp() {
				@Override
				public void onAnimationEnd(Animation animation) {
					super.onAnimationEnd(animation);
					mTopView.setVisibility(View.GONE);
				}
			});
			mTopView.startAnimation(animation);
			mBottomView.clearAnimation();
			Animation animation1 = AnimationUtils.loadAnimation(getActivity(), R.anim.option_leave_from_bottom);
			animation1.setAnimationListener(new AnimationImp() {
				@Override
				public void onAnimationEnd(Animation animation) {
					super.onAnimationEnd(animation);
					mBottomView.setVisibility(View.GONE);
				}
			});
			mBottomView.startAnimation(animation1);
		} else {
			mTopView.setVisibility(View.VISIBLE);
			mTopView.clearAnimation();
			Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.option_entry_from_top);
			mTopView.startAnimation(animation);
			mBottomView.setVisibility(View.VISIBLE);
			mBottomView.clearAnimation();
			Animation animation1 = AnimationUtils.loadAnimation(getActivity(), R.anim.option_entry_from_bottom);
			mBottomView.startAnimation(animation1);
			mHandler.removeCallbacks(hideRunnable);
			mHandler.postDelayed(hideRunnable, HIDE_TIME);
		}
	}

	private class AnimationImp implements AnimationListener {
		@Override
		public void onAnimationEnd(Animation animation) {
		}

		@Override
		public void onAnimationRepeat(Animation animation) {
		}

		@Override
		public void onAnimationStart(Animation animation) {
		}
	}
}
