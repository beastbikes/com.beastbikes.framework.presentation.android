package com.beastbikes.framework.ui.android;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.beastbikes.framework.android.schedule.RequestQueueManager;

/**
 * The base fragment activity
 * 
 * @author johnson
 * 
 */
public abstract class BaseFragmentActivity extends FragmentActivity implements
		RequestQueueManager {

	private RequestQueue requestQueue;

	@Override
	public RequestQueue getRequestQueue() {
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
