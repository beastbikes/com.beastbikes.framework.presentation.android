package com.beastbikes.framework.ui.android;

import android.app.Activity;
import android.os.Bundle;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.beastbikes.framework.android.schedule.RequestQueueManager;

/**
 * The base activity
 * 
 * @author johnson
 * 
 */
public abstract class BaseActivity extends Activity implements
		RequestQueueManager {

	private RequestQueue requestQueue;

	@Override
	public final RequestQueue getRequestQueue() {
		return this.requestQueue;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestQueue = Volley.newRequestQueue(this);
	}

	@Override
	protected void onStop() {
		this.requestQueue.cancelAll(this);
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		this.requestQueue.cancelAll(this);
		this.requestQueue.stop();
		super.onDestroy();
	}

}
