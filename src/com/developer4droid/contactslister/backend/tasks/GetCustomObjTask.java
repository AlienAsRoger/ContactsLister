package com.developer4droid.contactslister.backend.tasks;


import com.developer4droid.contactslister.backend.interfaces.TaskUpdateInterface;

import java.lang.reflect.Type;

public class GetCustomObjTask<T> extends AbstractUpdateGsonTask<T,String> {

	public GetCustomObjTask(TaskUpdateInterface<T> taskFace, Class<T> clazz) {
		super(clazz, taskFace);
	}

	@Override
	protected Integer doTheTask(String... urls) {
		String url = urls[0];
        int result = getJsonData(url);
		return result;
	}


    
	@Override
	protected Type getListType() {
        return taskFace.getListType();
	}

}
