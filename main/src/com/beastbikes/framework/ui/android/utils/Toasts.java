package com.beastbikes.framework.ui.android.utils;

import android.content.Context;
import android.widget.Toast;

public final class Toasts {

	private static Toast instance;

	private Toasts() {
	}

	public static void show(Context context, int resId, int duration) {
		show(context, context.getString(resId), duration);
	}

	public synchronized static void show(Context context, CharSequence text,
			int duration) {
		if (null == instance) {
			instance = Toast.makeText(context, text, duration);
		} else {
			instance.setText(text);
		}

		instance.show();
	}

	public static void show(Context context, int resId) {
		show(context, resId, Toast.LENGTH_SHORT);
	}

	public static void show(Context context, CharSequence text) {
		show(context, text, Toast.LENGTH_SHORT);
	}

}
