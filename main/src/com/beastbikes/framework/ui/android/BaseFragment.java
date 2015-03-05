package com.beastbikes.framework.ui.android;

import android.app.Activity;
import android.support.v4.app.Fragment;

import com.android.volley.RequestQueue;
import com.beastbikes.framework.android.schedule.AsyncTaskQueue;
import com.beastbikes.framework.android.schedule.AsyncTaskQueueFactory;
import com.beastbikes.framework.android.schedule.AsyncTaskQueueManager;
import com.beastbikes.framework.android.schedule.RequestQueueFactory;
import com.beastbikes.framework.android.schedule.RequestQueueManager;

/**
 * The base fragment
 * 
 * @author johnson
 * 
 */
public abstract class BaseFragment extends Fragment implements
		RequestQueueManager, AsyncTaskQueueManager {

	private RequestQueue requestQueue;
	private AsyncTaskQueue asyncTaskQueue;

	@Override
	public RequestQueue getRequestQueue() {
		return this.requestQueue;
	}

	@Override
	public AsyncTaskQueue getAsyncTaskQueue() {
		return this.asyncTaskQueue;
	}

	@Override
	public void onAttach(Activity activity) {
		if (activity instanceof RequestQueueManager) {
			this.requestQueue = ((RequestQueueManager) activity)
					.getRequestQueue();
		} else {
			this.requestQueue = RequestQueueFactory.newRequestQueue(activity);
		}

		if (activity instanceof AsyncTaskQueueManager) {
			this.asyncTaskQueue = ((AsyncTaskQueueManager) activity).getAsyncTaskQueue();
		} else {
			this.asyncTaskQueue = AsyncTaskQueueFactory.newTaskQueue(activity);
		}

		super.onAttach(activity);
	}

	@Override
	public void onStop() {
		this.requestQueue.cancelAll(this);
		this.asyncTaskQueue.cancelAll(this);
		super.onStop();
	}

	@Override
	public void onDetach() {
		this.requestQueue.cancelAll(this);
		this.asyncTaskQueue.cancelAll(this);
		this.requestQueue.stop();
		this.asyncTaskQueue.stop();
		super.onDestroy();
	}

}
