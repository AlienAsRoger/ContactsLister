package com.developer4droid.contactslister.backend.tasks;

import android.util.Log;
import com.developer4droid.contactslister.backend.interfaces.TaskUpdateInterface;
import com.developer4droid.contactslister.statics.StaticData;
import com.google.gson.Gson;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import javax.net.ssl.SSLException;
import java.io.*;
import java.lang.reflect.Type;
import java.util.List;

abstract class AbstractUpdateGsonTask<T,Input> extends AbstractUpdateTask<T,Input> {

	Class<T> clazz;
	private static final String TAG = "AbstractUpdateGsonTask";
    private File cacheDir;

	AbstractUpdateGsonTask(Class<T> clazz, TaskUpdateInterface<T> taskFace) {
		super(taskFace);
		this.clazz = clazz;
	}

	int getJsonData(String url) {

		// Instantiate the custom HttpClient
		DefaultHttpClient httpClient = new DefaultHttpClient();

		Log.d(TAG, "retrieving from url = " + url);


		final HttpGet httpPost = new HttpGet(url);
		try {
			HttpResponse response = httpClient.execute(httpPost);
			final int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode != HttpStatus.SC_OK) {
				Log.e(TAG, "Error " + statusCode + " while retrieving dat from " + url);
				return StaticData.UNKNOWN_ERROR;
			}

			final HttpEntity entity = response.getEntity();
			if (entity != null) {
				InputStream inputStream = null;
				try {

//					String responseText =  EntityUtils.toString(response.getEntity());   // don't remove for quick debug
//					Log.d(TAG, "received raw JSON response = " + responseText);
					inputStream = entity.getContent();


					if (useList) {
						itemList = parseJson2List(inputStream);
//						Log.d(TAG, "received JSON list object = " + parseServerRequestList(itemList));
						if(itemList.size() > 0)
							result = StaticData.RESULT_OK;
					} else {
						item = parseJson(inputStream);
						if(item != null)
							result = StaticData.RESULT_OK;
//						Log.d(TAG, "received JSON object = " + parseServerRequest(item));
					}

				} finally {
					if (inputStream != null) {
						inputStream.close();
					}
					entity.consumeContent();
				}
			}
        } catch (SSLException e){
            Log.e(TAG, "I/O error while retrieving data from " + url, e);
            result = StaticData.UNKNOWN_ERROR;
        } catch (IOException e) {
			httpPost.abort();
			Log.e(TAG, "I/O error while retrieving data from " + url, e);
			result = StaticData.UNKNOWN_ERROR;
		} catch (IllegalStateException e) {
			httpPost.abort();
			Log.e(TAG, "Incorrect URL: " + url, e);
			result = StaticData.UNKNOWN_ERROR;
		} catch (Exception e) {
			httpPost.abort();
			Log.e(TAG, "Error while retrieving data from " + url, e);
			result = StaticData.UNKNOWN_ERROR;
		} finally {
			httpClient.getConnectionManager().shutdown();
		}
		return result;
	}

	T parseJson(String jRespString) {
		Gson gson = new Gson();
		return gson.fromJson(jRespString, clazz);
	}

    T parseJson(InputStream jRespString) {
		Gson gson = new Gson();
		Reader reader = new InputStreamReader(jRespString);
		return gson.fromJson(reader, clazz);
	}

	protected abstract Type getListType();

	List<T> parseJson2List(InputStream jRespString) {
		Gson gson = new Gson();
		Reader reader = new InputStreamReader(jRespString);
		Type t = getListType();
        return gson.fromJson(reader, t);
	}

	String parseServerRequest(T jRequest) {
		Gson gson = new Gson();
		return gson.toJson(jRequest);
	}

	String parseServerRequestList(List<T> jRequest) {
		Gson gson = new Gson();
		return gson.toJson(jRequest);
	}

}
