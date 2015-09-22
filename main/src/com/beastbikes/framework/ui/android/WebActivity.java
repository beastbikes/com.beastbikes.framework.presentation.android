package com.beastbikes.framework.ui.android;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
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
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.ViewGroup.LayoutParams;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import com.beastbikes.framework.android.utils.PackageUtils;

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

	private String userAgent;

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

		final String ver = PackageUtils.getVersionName(this);
		final String ext = getPackageName() + "/" + ver;
		final WebSettings settings = this.browser.getSettings();
		settings.setAppCacheEnabled(true);
		settings.setAppCachePath(getCacheDir().getAbsolutePath());
		settings.setUserAgentString(settings.getUserAgentString() + " " + ext);
		settings.setBuiltInZoomControls(false);
		settings.setDatabaseEnabled(true);
		settings.setDomStorageEnabled(true);
		settings.setDisplayZoomControls(false);
		settings.setJavaScriptEnabled(true);
		settings.setSupportZoom(false);
		
		this.userAgent = settings.getUserAgentString();

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
			this.browser.loadUrl(url, getRequestHeaders());
		}
	}

	public String getUserAgent() {
		if (!TextUtils.isEmpty(this.userAgent))
			return this.userAgent;

		try {
			final Method getDefaultUserAgent = WebSettings.class.getMethod(
					"getDefaultUserAgent", new Class[] { Context.class });
			return String.valueOf(getDefaultUserAgent.invoke(WebSettings.class,
					this));
		} catch (Exception e) {
		}

		return "Android " + Build.VERSION.RELEASE + " " + getPackageName()
				+ "/" + PackageUtils.getVersionName(this);
	}
	
	public Map<String, String> getRequestHeaders() {
		final Map<String, String> headers = new HashMap<String, String>();
		final Intent intent = getIntent();
		if (null != intent) {
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
		}

		return Collections.unmodifiableMap(headers);
	}

}
