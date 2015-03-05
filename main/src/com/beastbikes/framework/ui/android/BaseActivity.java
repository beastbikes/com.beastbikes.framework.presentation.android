package com.beastbikes.framework.ui.android;

import android.app.Activity;
import android.os.Bundle;

import com.android.volley.RequestQueue;
import com.beastbikes.framework.android.schedule.AsyncTaskQueue;
import com.beastbikes.framework.android.schedule.AsyncTaskQueueFactory;
import com.beastbikes.framework.android.schedule.AsyncTaskQueueManager;
import com.beastbikes.framework.android.schedule.RequestQueueFactory;
import com.beastbikes.framework.android.schedule.RequestQueueManager;

/**
 * The base activity
 * 
 * @author johnson
 * 
 */
public abstract class BaseActivity extends Activity implements
		RequestQueueManager, AsyncTaskQueueManager {

	private RequestQueue requestQueue;
	private AsyncTaskQueue asyncTaskQueue;

	@Override
	public final RequestQueue getRequestQueue() {
		return this.requestQueue;
	}

	@Override
	public AsyncTaskQueue getAsyncTaskQueue() {
		return this.asyncTaskQueue;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestQueue = RequestQueueFactory.newRequestQueue(this);
		this.asyncTaskQueue = AsyncTaskQueueFactory.newTaskQueue(this);
	}

	@Override
	protected void onStop() {
		this.requestQueue.cancelAll(this);
		this.asyncTaskQueue.cancelAll(this);
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		this.requestQueue.cancelAll(this);
		this.asyncTaskQueue.cancelAll(this);
		this.requestQueue.stop();
		this.asyncTaskQueue.stop();
		super.onDestroy();
	}

}
