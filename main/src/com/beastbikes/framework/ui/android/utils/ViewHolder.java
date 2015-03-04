package com.beastbikes.framework.ui.android.utils;

import android.content.Context;
import android.view.View;

/**
 * The {@link ViewHolder} is used by view adapter
 * 
 * @author johnson
 * 
 * @param <T>
 */
public abstract class ViewHolder<T> {

	private final View view;

	/**
	 * Create an instance with the specified view
	 * 
	 * @param v
	 *            The root view to hold
	 */
	protected ViewHolder(View v) {
		this.view = v;
		v.setTag(this);
		ViewIntrospector.introspect(v, this);
	}

	/**
	 * Returns the context
	 * 
	 * @return the context
	 */
	public Context getContext() {
		return this.view.getContext();
	}

	/**
	 * Bind the specified view
	 * 
	 * @param v
	 *            view
	 */
	@SuppressWarnings("unchecked")
	public final void bind(View v) {
		this.onBind((T) v.getTag());
	}

	public abstract void onBind(T t);

}
