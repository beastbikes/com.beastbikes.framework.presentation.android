package com.beastbikes.framework.ui.android;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.net.http.HttpResponseCache;
import android.text.TextUtils;
import android.webkit.URLUtil;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class DefaultWebViewClient extends WebViewClient {

	private static final String TAG = "DefaultWebViewClient";

	private static final String WEBKIT = "webkit";

	private static final String ERROR_HTML = "error.html";

	private static final String DEFAULT_ERROR_PAGE_URL = "file:///android_asset/" + WEBKIT + "/" + ERROR_HTML;

	private static final Logger logger = LoggerFactory.getLogger(TAG);

	private final WebActivity webActivity;

	public DefaultWebViewClient(WebActivity webActivity) {
		this.webActivity = webActivity;
	}

	@Override
	public boolean shouldOverrideUrlLoading(WebView view, String url) {
		logger.debug("Override loadding " + url);

		return this.webActivity.handleURL(url)
				|| super.shouldOverrideUrlLoading(view, url);
	}

	@Override
	public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
		logger.debug("Intercepting " + url);
		if (!URLUtil.isNetworkUrl(url) || null == HttpResponseCache.getDefault())
			return super.shouldInterceptRequest(view, url);

		try {
			final URLConnection conn = new URL(url).openConnection();
			conn.setRequestProperty("User-Agent", view.getSettings().getUserAgentString());
			conn.setRequestProperty("Accept-Language", Locale.getDefault().getLanguage());
			conn.setUseCaches(true);
			conn.connect();

			final String contentType = conn.getContentType();
            final String contentEncoding = conn.getContentEncoding();
            final String mimeType = TextUtils.isEmpty(contentType)
                    ? URLConnection.guessContentTypeFromName(url)
                    : contentType.replaceAll(";\\s*.+$", "");
            final String encoding = TextUtils.isEmpty(contentEncoding)
                    ? "utf-8" : contentEncoding;
            return new WebResourceResponse(mimeType, encoding, conn.getInputStream());
		} catch (IOException e) {
			logger.error("Intercepting " + url + " error", e);
		}

		return super.shouldInterceptRequest(view, url);
	}

	@Override
	public void onPageStarted(WebView view, String url, Bitmap favicon) {
		logger.debug("Loading " + url);
	}

	@Override
	public void onPageFinished(WebView view, String url) {
		logger.debug("Loading " + url + " complete");
	}

	@Override
	public void onReceivedError(WebView view, int errorCode,
			String description, String failingUrl) {
		logger.error(String.format("Load %s failed, error %d (%s)", failingUrl,
				errorCode, description));

		final AssetManager am = view.getContext().getAssets();
		try {
			final String[] webkit = am.list(WEBKIT);
			if (null == webkit)
				return;

			for (int i = 0; i < webkit.length; i++) {
				if (!ERROR_HTML.equalsIgnoreCase(webkit[i]))
					continue;

				view.loadUrl(DEFAULT_ERROR_PAGE_URL);
				break;
			}
		} catch (IOException e) {
			logger.warn("Default error page not found", e);
		}
	}

}
