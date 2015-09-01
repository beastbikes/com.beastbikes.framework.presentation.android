package com.beastbikes.framework.ui.android;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.ViewGroup.LayoutParams;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

public class WebActivity extends BaseActivity {

	public static final String EXTRA_TITLE = "title";

	public static final String EXTRA_HTTP_HEADERS = "additional_http_headers";

	public static final String EXTRA_ENTER_ANIMATION = "enter_animation";

	public static final String EXTRA_EXIT_ANIMATION = "exit_animation";

	public static final String EXTRA_NONE_ANIMATION = "none_animation";

	private WebViewClient defaultWebViewClient;

	private WebChromeClient defaultWebChromeClient;

	private int enterAnim;

	private int exitAnim;

	private int noneAnim;

	private FrameLayout container;

	private WebView browser;

	public WebActivity() {
		this.defaultWebViewClient = new DefaultWebViewClient(this);
		this.defaultWebChromeClient = new DefaultWebChromeClient(this);
	}

	@Override
	@SuppressLint("SetJavaScriptEnabled")
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final Intent intent = getIntent();
		if (intent == null) {
			this.finish();
			return;
		}

		final ActionBar bar = getActionBar();
		if (bar != null) {
			bar.setDisplayHomeAsUpEnabled(true);
		}

		this.browser = new WebView(this);
		this.container = new FrameLayout(this);
		this.container.addView(this.browser, new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		this.setContentView(this.container, new FrameLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

		this.setupBrowser();
	}
	
	protected WebView getBrowser() {
		return this.browser;
	}

	@Override
	public void finish() {
		super.finish();
		super.overridePendingTransition(0, this.exitAnim);
	}

	protected WebViewClient getWebViewClient() {
		return this.defaultWebViewClient;
	}

	protected WebChromeClient getWebChromeClient() {
		return this.defaultWebChromeClient;
	}

	protected void setWebViewClient(WebViewClient webViewClient) {
		this.defaultWebViewClient = webViewClient;
	}

	protected void setWebChromeClient(WebChromeClient webChromeClient) {
		this.defaultWebChromeClient = webChromeClient;
	}

	protected boolean handleURL(String url) {
		return false;
	}

	@SuppressLint("SetJavaScriptEnabled")
	protected void setupBrowser() {
		final Intent intent = getIntent();

		final WebSettings settings = this.browser.getSettings();
		settings.setDisplayZoomControls(false);
		settings.setBuiltInZoomControls(false);
		settings.setJavaScriptEnabled(true);
		settings.setLoadsImagesAutomatically(true);
		settings.setSupportZoom(false);

		this.browser.setDownloadListener(new DownloadListener() {
			@Override
			public void onDownloadStart(String url, String userAgent,
					String contentDisposition, String mimetype,
					long contentLength) {
				final DownloadManager dm = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
				final Request request = new Request(Uri.parse(url));

				if (!TextUtils.isEmpty(userAgent)) {
					request.addRequestHeader("User-Agent", userAgent);
				}

				if (!TextUtils.isEmpty(contentDisposition)) {
					request.addRequestHeader("Content-Disposition", contentDisposition);
				}

				if (!TextUtils.isEmpty(mimetype)) {
					request.setMimeType(mimetype);
				}

				request.allowScanningByMediaScanner();
				request.setAllowedNetworkTypes(Request.NETWORK_WIFI);
				dm.enqueue(request);
			}
		});
		this.browser.setWebChromeClient(getWebChromeClient());
		this.browser.setWebViewClient(getWebViewClient());

		this.enterAnim = intent.getIntExtra(EXTRA_ENTER_ANIMATION, 0);
		this.exitAnim = intent.getIntExtra(EXTRA_EXIT_ANIMATION, 0);
		this.noneAnim = intent.getIntExtra(EXTRA_NONE_ANIMATION, 0);
		super.overridePendingTransition(this.enterAnim, this.noneAnim);

		final String title = intent.getStringExtra(EXTRA_TITLE);
		if (!TextUtils.isEmpty(title)) {
			this.setTitle(title);
		}

		final String url = intent.getDataString();
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
