package com.developer4droid.contactslister.backend.tasks;


import com.developer4droid.contactslister.backend.interfaces.TaskUpdateInterface;
import com.developer4droid.contactslister.statics.StaticData;

import java.lang.reflect.Type;

public class JsonFromStringTask<T> extends AbstractUpdateGsonTask<T,String> {

	public JsonFromStringTask(TaskUpdateInterface<T> taskFace, Class<T> clazz) {
		super(clazz, taskFace);
	}

	@Override
	protected Integer doTheTask(String... jsonStrData) {

		item = parseJson(jsonStrData[0]);
		if(item != null)
			result = StaticData.RESULT_OK;
		return result;
	}




    
	@Override
	protected Type getListType() {
        return taskFace.getListType();
	}

}
