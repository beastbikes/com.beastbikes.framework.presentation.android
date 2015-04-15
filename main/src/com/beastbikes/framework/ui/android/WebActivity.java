package com.beastbikes.framework.ui.android;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beastbikes.framework.ui.android.utils.Toasts;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.ViewGroup.LayoutParams;
import android.webkit.ConsoleMessage;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

public class WebActivity extends BaseActivity {

	public static final String EXTRA_TITLE = "title";

	public static final String EXTRA_URL = "url";

	public static final String EXTRA_HTTP_HEADERS = "additional_http_headers";

	public static final String EXTRA_ENTER_ANIMATION = "enter_animation";

	public static final String EXTRA_EXIT_ANIMATION = "exit_animation";

	public static final String EXTRA_NONE_ANIMATION = "none_animation";

	private static final String WEBKIT = "webkit";

	private static final String ERROR_HTML = "error.html";

	private static final String DEFAULT_ERROR_PAGE_URL = "file:///android_asset/" + WEBKIT + "/" + ERROR_HTML;

	private static final String TAG = "WebActivity";

	private static final Logger logger = LoggerFactory.getLogger(TAG);

	private int enterAnim;

	private int exitAnim;

	private int noneAnim;

	private FrameLayout container;

	private WebView browser;

	@Override
	@SuppressLint("SetJavaScriptEnabled")
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final ActionBar bar = getActionBar();
		if (bar != null) {
			bar.setDisplayHomeAsUpEnabled(true);
		}

		this.browser = new WebView(this);
		this.container = new FrameLayout(this);
		this.container.addView(this.browser, new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		super.setContentView(this.container, new FrameLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

		final WebSettings settings = this.browser.getSettings();
		settings.setDisplayZoomControls(false);
		settings.setBuiltInZoomControls(false);
		settings.setJavaScriptEnabled(true);
		settings.setLoadsImagesAutomatically(true);
		settings.setSupportZoom(false);

		this.browser.setWebChromeClient(new WebChromeClient() {

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

		});
		this.browser.setWebViewClient(new WebViewClient() {

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				// TODO
				return super.shouldOverrideUrlLoading(view, url);
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				logger.info("Loading " + url);
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				logger.info("Loading " + url + " complete");
			}

			@Override
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				logger.info(String.format("Load %s failed, error %d (%s)",
						failingUrl, errorCode, description));

				final AssetManager am = getAssets();
				try {
					final String[] webkit = am.list(WEBKIT);
					if (null == webkit)
						return;

					for (int i = 0; i < webkit.length; i++) {
						if (ERROR_HTML.equalsIgnoreCase(webkit[i])) {
							view.loadUrl(DEFAULT_ERROR_PAGE_URL);
						}
					}
				} catch (IOException e) {
					logger.warn("Default error page not found", e);
				}
			}

		});

		final Intent intent = getIntent();
		if (null != intent) {
			this.enterAnim = intent.getIntExtra(EXTRA_ENTER_ANIMATION, 0);
			this.exitAnim = intent.getIntExtra(EXTRA_EXIT_ANIMATION, 0);
			this.noneAnim = intent.getIntExtra(EXTRA_NONE_ANIMATION, 0);
			super.overridePendingTransition(this.enterAnim, this.noneAnim);

			final String title = intent.getStringExtra(EXTRA_TITLE);
			if (!TextUtils.isEmpty(title)) {
				this.setTitle(title);
			}

			final String url = intent.getStringExtra(EXTRA_URL);
			if (!TextUtils.isEmpty(url)) {
				final Map<String, String> headers = new HashMap<String, String>();
				final Bundle bundle = intent.getBundleExtra(EXTRA_HTTP_HEADERS);

				if (null != bundle && bundle.size() > 0) {
					final Set<String> names = bundle.keySet();
					for (final String key : names) {
						final String value = bundle.getString(key);

						if (!TextUtils.isEmpty(value)) {
							headers.put(key, value);
						}
					}
				}

				this.browser.loadUrl(url, headers);
			}
		}
	}

	@Override
	public void finish() {
		super.finish();
		super.overridePendingTransition(0, this.exitAnim);
	}

}
