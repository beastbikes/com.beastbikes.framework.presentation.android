package com.beastbikes.framework.ui.android;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.annotation.TargetApi;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.HttpResponseCache;
import android.text.TextUtils;
import android.webkit.URLUtil;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class DefaultWebViewClient extends WebViewClient {

	private static final String TAG = "DefaultWebViewClient";

	private static final String WEBKIT = "webkit";

	private static final String ERROR_HTML = "error.html";

	private static final String DEFAULT_ERROR_PAGE_URL = "file:///android_asset/"
			+ WEBKIT + "/" + ERROR_HTML;

	private static final String API_HOST = "api.beastbikes.com";

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
	@TargetApi(21)
	public WebResourceResponse shouldInterceptRequest(WebView view,
			WebResourceRequest request) {
		final String url = request.getUrl().toString();
		final Uri uri = Uri.parse(url);
		final String host = uri.getHost();
		if (host.equalsIgnoreCase(API_HOST)) {
			return null;
		}
		
		final Map<String, String> headers = new HashMap<String, String>(
				request.getRequestHeaders());
		final Map<String, String> addtional = this.webActivity
				.getRequestHeaders();
		if (null != addtional) {
			headers.putAll(addtional);
		}

		final String method = request.getMethod();
		return this.shouldInterceptRequest(view, method, url, headers);
	}

	@Override
	public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
		final Uri uri = Uri.parse(url);
		final String host = uri.getHost();
		if (host.equalsIgnoreCase(API_HOST)) {
			return null;
		}
		return this.shouldInterceptRequest(view, "GET", url,
				this.webActivity.getRequestHeaders());
	}

	protected WebResourceResponse shouldInterceptRequest(WebView view,
			String method, String url, Map<String, String> headers) {
		logger.debug("Intercepting " + url);

		if (!URLUtil.isNetworkUrl(url)
				|| null == HttpResponseCache.getDefault())
			return null;

		try {
			final HttpURLConnection conn = (HttpURLConnection) new URL(url)
					.openConnection();
			conn.setRequestMethod(method);
			conn.setRequestProperty("User-Agent",
					this.webActivity.getUserAgent());
			conn.setRequestProperty("Accept-Language", Locale.getDefault()
					.getLanguage());

			if (null != headers) {
				for (final Map.Entry<String, String> header : headers
						.entrySet()) {
					conn.setRequestProperty(header.getKey(), header.getValue());
				}
			}

			conn.setUseCaches(true);
			conn.connect();

			final String contentType = conn.getContentType();
			final String contentEncoding = conn.getContentEncoding();
			final String mimeType = TextUtils.isEmpty(contentType) ? URLConnection
					.guessContentTypeFromName(url) : contentType.replaceAll(
					";\\s*.+$", "");
			final String encoding = TextUtils.isEmpty(contentEncoding) ? "utf-8"
					: contentEncoding;
			return new WebResourceResponse(mimeType, encoding,
					conn.getInputStream());
		} catch (IOException e) {
			logger.error("Intercepting " + url + " error", e);
		}

		return null;
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
