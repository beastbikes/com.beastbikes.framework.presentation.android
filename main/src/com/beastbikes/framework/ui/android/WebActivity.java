package com.beastbikes.framework.ui.android;

import java.io.IOException;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

public class WebActivity extends BaseActivity {

	public static final String EXTRA_TITLE = "title";

	public static final String EXTRA_URL = "url";

	public static final String EXTRA_ENTER_ANIMATION = "enter_animation";

	public static final String EXTRA_EXIT_ANIMATION = "exit_animation";

	public static final String EXTRA_NONE_ANIMATION = "none_animation";

	private static final String DEFAULT_ERROR_PAGE_PATH = "webkit/error.html";

	private static final String DEFAULT_ERROR_PAGE_URL = "file:///android_asset/" + DEFAULT_ERROR_PAGE_PATH;

	private static final String TAG = "WebActivity";

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

		this.browser.setWebViewClient(new WebViewClient() {

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				// TODO
				return super.shouldOverrideUrlLoading(view, url);
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				Log.v(TAG, "Loading " + url);
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				Log.v(TAG, "Loading " + url + " complete");
			}

			@Override
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				final AssetManager am = getAssets();
				try {
					final String[] errorPage = am.list(DEFAULT_ERROR_PAGE_PATH);
					if (errorPage != null && errorPage.length > 0) {
						view.loadUrl(DEFAULT_ERROR_PAGE_URL);
					}
				} catch (IOException e) {
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
				this.browser.loadUrl(url);
			}
		}
	}

	@Override
	public void finish() {
		super.finish();
		super.overridePendingTransition(0, this.exitAnim);
	}

}
