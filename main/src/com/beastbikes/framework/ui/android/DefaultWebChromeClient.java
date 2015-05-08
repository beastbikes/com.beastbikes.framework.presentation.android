package com.beastbikes.framework.ui.android;

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.webkit.ConsoleMessage;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.beastbikes.framework.ui.android.utils.Toasts;

public class DefaultWebChromeClient extends WebChromeClient {

	private static final String TAG = "DefaultWebChromeClient";

	private static final Logger logger = LoggerFactory.getLogger(TAG);

	private final WebActivity webActivity;

	public DefaultWebChromeClient(WebActivity webActivity) {
		this.webActivity = webActivity;
	}

	@Override
	public void onProgressChanged(WebView view, int newProgress) {
		this.webActivity.setProgress(newProgress);
	}

	@Override
	public boolean onJsAlert(WebView view, String url, String message,
			JsResult result) {
		Toasts.show(view.getContext(), message);
		result.confirm();
		return true;
	}

	@Override
	public boolean onConsoleMessage(ConsoleMessage cm) {
		final String msg = String.format(Locale.getDefault(),
				"%s#%d: %s", cm.sourceId(), cm.lineNumber(),
				cm.message());

		switch (cm.messageLevel()) {
		case DEBUG:
			logger.debug(msg);
			break;
		case ERROR:
			logger.error(msg);
			break;
		case LOG:
			logger.info(msg);
			break;
		case TIP:
			logger.trace(msg);
			break;
		case WARNING:
			logger.warn(msg);
			break;
		}

		return super.onConsoleMessage(cm);
	}
	
}
