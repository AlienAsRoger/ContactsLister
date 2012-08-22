package com.developer4droid.contactslister.backend.interfaces;

import android.content.Context;
import android.view.View;
import android.widget.Toast;
import com.developer4droid.contactslister.statics.StaticData;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public abstract class AbstractUpdateListener<T> implements TaskUpdateInterface<T> {

	private View progressView;
	private Context context;
	protected boolean useList;
    protected boolean useCache = true;

	public AbstractUpdateListener(Context context, View progressView) {
		this.progressView = progressView;
		this.context = context;
	}

	@Override
	public void showProgress(boolean show) {
		if(progressView != null)
			progressView.setVisibility(show ? View.VISIBLE : View.INVISIBLE);

	}

	@Override
	public boolean useList() {
		return useList;
	}

    @Override
    public boolean useCache() {
        return useCache;
    }

	@Override
	public void updateListData(List<T> itemsList) {
	}

	@Override
	public void updateData(T returnedItem) {

	}

    @Override
    public Type getListType() {
        return new TypeToken<List<T>>() { }.getType();
    }

	@Override
	public void errorHandle(Integer resultCode) {
		switch (resultCode) {
		case StaticData.UNKNOWN_ERROR:
			Toast.makeText(context, "error occured", Toast.LENGTH_SHORT).show();
			break;
		default: break;
		}
	}

	@Override
	public Context getMeContext() {
		return context;
	}
}
