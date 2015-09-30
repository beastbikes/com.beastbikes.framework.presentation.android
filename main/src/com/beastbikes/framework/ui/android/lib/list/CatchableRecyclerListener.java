/**
 * 
 */
package com.beastbikes.framework.ui.android.lib.list;

import android.util.Log;
import android.view.View;
import android.widget.AbsListView.RecyclerListener;


/**
 *
 *
 */
public abstract class CatchableRecyclerListener implements RecyclerListener {
	public abstract void intlOnMovedToScrapHeap(View view);

	@Override
	public void onMovedToScrapHeap(View view) {
		try {
			intlOnMovedToScrapHeap(view);
		} catch (final Error t) {
			Log.e("CatchableRecyclerListener", t.getMessage(), t);

			throw (Error) t.fillInStackTrace();
		}
	}
}
