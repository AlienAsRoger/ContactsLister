package com.developer4droid.contactslister.backend.tasks;

import android.os.AsyncTask;
import android.os.Build;
import com.developer4droid.contactslister.backend.interfaces.TaskUpdateInterface;
import com.developer4droid.contactslister.statics.StaticData;

import java.util.List;

public abstract class AbstractUpdateTask<T,Input> extends AsyncTask<Input, Void, Integer> {

	TaskUpdateInterface<T> taskFace;
	T item;
	List<T> itemList;
	boolean useList;
    int result;

    AbstractUpdateTask(TaskUpdateInterface<T> taskFace) {
		this.taskFace = taskFace;
		useList = taskFace.useList();
        result = StaticData.EMPTY_DATA;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		taskFace.showProgress(true);
	}

	@Override
	protected Integer doInBackground(Input... params) {
        if(isCancelled())
            return StaticData.EMPTY_DATA;
		return doTheTask(params);
	}

	protected abstract Integer doTheTask(Input... params);

	@Override
	protected void onPostExecute(Integer result) {
		super.onPostExecute(result);
		taskFace.showProgress(false);
        if(isCancelled())
            return;

		if (result == StaticData.RESULT_OK) {
			if(useList)
				taskFace.updateListData(itemList);
			else
				taskFace.updateData(item);
		}else {
			taskFace.errorHandle(result);
		}
	}


    public AbstractUpdateTask<T, Input> executeTask(Input... input){
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB){
            executeOnExecutor(THREAD_POOL_EXECUTOR, input);
        }else
            execute(input);
        return this;
    }

}
