package com.beastbikes.framework.ui.android;

import android.app.Activity;
import android.support.v4.app.Fragment;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.beastbikes.framework.android.schedule.RequestQueueManager;

/**
 * The base fragment
 * 
 * @author johnson
 * 
 */
public abstract class BaseFragment extends Fragment implements
		RequestQueueManager {

	private RequestQueue requestQueue;

	@Override
	public RequestQueue getRequestQueue() {
		return this.requestQueue;
	}

	@Override
	public void onAttach(Activity activity) {
		if (activity instanceof RequestQueueManager) {
			this.requestQueue = ((RequestQueueManager) activity)
					.getRequestQueue();
		} else {
			this.requestQueue = Volley.newRequestQueue(activity);
		}

		super.onAttach(activity);
	}

	@Override
	public void onStop() {
		this.requestQueue.cancelAll(this);
		super.onStop();
	}

	@Override
	public void onDetach() {
		this.requestQueue.cancelAll(this);
		this.requestQueue.stop();
		super.onDestroy();
	}

}
