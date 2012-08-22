package com.developer4droid.contactslister.backend.interfaces;

import android.content.Context;

import java.lang.reflect.Type;
import java.util.List;

/**
 * TaskUpdateInterface.java
 *
 *
 * @author Alexey Schekin (schekin@azoft.com)
 * @created 07.11.2011
 * @modified 07.11.2011
 * @version 1.0.1
 */
public interface TaskUpdateInterface<T> {
	boolean useList();

	boolean useCache();

	void showProgress(boolean show);

	void updateListData(List<T> listObjects);

	void updateData(T returnedItem);

	void errorHandle(Integer resultCode);

	Context getMeContext();

    Type getListType();
}
