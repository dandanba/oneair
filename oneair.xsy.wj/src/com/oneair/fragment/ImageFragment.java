package com.oneair.fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.oneair.xsy.R;
public class ImageFragment extends DialogFragment {
	public static DialogFragment getInstance(int[] resArray) {
		final DialogFragment fragment = new ImageFragment();
		final Bundle args = new Bundle();
		args.putIntArray("res_array", resArray);
		fragment.setArguments(args);
		return fragment;
	}

	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				removeMessages(1);
				if (mResIndex < mResArray.length) {
					mImage.setImageResource(mResArray[mResIndex]);
					mResIndex++;
					sendEmptyMessageDelayed(1, 2 * 60 * 1000);
				} else {
					dismiss();
				}
				break;
			default:
				break;
			}
		}
	};
	private int[] mResArray;
	private int mResIndex;
	private ImageView mImage;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mResArray = getArguments().getIntArray("res_array");
		setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.fragment_image, container);
		mImage = (ImageView) view.findViewById(R.id.image);
		mHandler.sendEmptyMessage(1);
		return view;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mHandler.removeMessages(0);
	}
}
