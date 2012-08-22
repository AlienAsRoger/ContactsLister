/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.developer4droid.contactslister.backend.image_load;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.*;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This helper class download images from the Internet and binds those with the
 * provided ImageView.
 * <p/>
 * <p>
 * It requires the INTERNET permission, which should be added to your
 * application's manifest file.
 * </p>
 * <p/>
 * A local cache of downloaded images is maintained internally to improve
 * performance.
 */
public class EnhancedImageDownloader {
    private static final String LOG_TAG = "EnhancedImageDownloader";

    public enum Mode {
        NO_ASYNC_TASK, NO_DOWNLOADED_DRAWABLE, CORRECT
    }

    private Mode mode = Mode.CORRECT;

    private static final int REQUIRED_IMAGE_SIZE = 36;
    private File cacheDir;

    private static Resources resources;

    public EnhancedImageDownloader(Context context) {
        resources = context.getResources();
        // Find the dir to save cached images
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
            cacheDir = new File(android.os.Environment.getExternalStorageDirectory(), getApplicationCacheDir(context.getPackageName()));
        else
            cacheDir = context.getCacheDir();

        if (!cacheDir.exists())
            cacheDir.mkdirs();
    }

    /**
     * Download the specified image from the Internet and binds it to the
     * provided ImageView. The binding is immediate if the image is found in the
     * cache and will be done asynchronously otherwise. A null bitmap will be
     * associated to the ImageView if an error occurs.
     *
     * @param url    The URL of the image to download.
     * @param holder The ImageView to bind the downloaded image to.
     */
    public void download(String url, ProgressImageView holder) {
        Bitmap bitmap = getBitmapFromCache(url, holder);

        if (bitmap == null) {
            forceDownload(url, holder);
        } else {
            cancelPotentialDownload(url, holder.imageView);
            holder.imageView.setImageBitmap(bitmap);
            holder.progress.setVisibility(View.INVISIBLE);
        }
    }
    /**
     * @param url The URL of the image that will be retrieved from the cache.
     * @return The cached bitmap or null if it was not found.
     */
    private Bitmap getBitmapFromCache(String url, ProgressImageView pHolder) {
        // I identify images by hashcode. Not a perfect solution, good for the
        // demo.
        String filename = String.valueOf(url.hashCode());
        File f = new File(cacheDir, filename);

        // from SD cache
        // if file is stored so simply read it, do not resize
//        if(!loadedBmpList.contains(url)){
            Bitmap bmp = readFile(f);
            if(bmp != null){
//                loadedBmpList.add(url);
                pHolder.bitmap = bmp;
                addBitmapToCache(url, pHolder);
            }
//        }

        // First try the hard reference cache
        synchronized (sHardBitmapCache) {
            final ProgressImageView holder = sHardBitmapCache.get(url);
            if (holder != null) {
                // Bitmap found in hard cache
                // Move element to first position, so that it is removed last
                sHardBitmapCache.remove(url);
                sHardBitmapCache.put(url, holder);
                return holder.bitmap;
            }
        }

        // Then try the soft reference cache
        SoftReference<ProgressImageView> bitmapReference = sSoftBitmapCache.get(url);
        if (bitmapReference != null) {
            final ProgressImageView holder = bitmapReference.get();
            if (holder != null) {
                // Bitmap found in soft cache
                return holder.bitmap;
            } else {
                // Soft reference has been Garbage Collected
                sSoftBitmapCache.remove(url);
            }
        }

        return null;
    }

    /*
      * Same as download but the image is always downloaded and the cache is not
      * used. Kept private at the moment as its interest is not clear. private
      * void forceDownload(String url, ImageView view) { forceDownload(url, view,
      * null); }
      */

    /**
     * Same as download but the image is always downloaded and the cache is not
     * used. Kept private at the moment as its interest is not clear.
     */
    private void forceDownload(String url, ProgressImageView holder) {
        // State sanity: url is guaranteed to never be null in
        // DownloadedDrawable and cache keys.
        if (url == null) {
            holder.imageView.setImageDrawable(null);
            return;
        }

        if (cancelPotentialDownload(url, holder.imageView)) {
            BitmapDownloaderTask task;
            switch (mode) {
                case NO_ASYNC_TASK: {
                    holder.bitmap = downloadBitmap(url);
                    addBitmapToCache(url, holder);
                    holder.imageView.setImageBitmap(holder.bitmap);
                }
                break;

                case NO_DOWNLOADED_DRAWABLE: {
                    holder.imageView.setMinimumHeight(50);
                    task = new BitmapDownloaderTask(holder);
                    task.executeTask(url);
                }
                break;

                case CORRECT: {
                    task = new BitmapDownloaderTask(holder);
                    EnhDownloadedDrawable enhDownloadedDrawable = new EnhDownloadedDrawable(task, holder);
                    holder.imageView.setImageDrawable(enhDownloadedDrawable);
                    holder.imageView.setMinimumHeight(50);
                    task.executeTask(url);
                }
                break;
            }
        }
    }

    /**
     * Returns true if the current download has been canceled or if there was no
     * download in progress on this image view. Returns false if the download in
     * progress deals with the same url. The download is not stopped in that
     * case.
     */
    private static boolean cancelPotentialDownload(String url, ImageView imageView) {
        BitmapDownloaderTask bitmapDownloaderTask = getBitmapDownloaderTask(imageView);

        if (bitmapDownloaderTask != null) {
            String bitmapUrl = bitmapDownloaderTask.url;
            if ((bitmapUrl == null) || (!bitmapUrl.equals(url))) {
                bitmapDownloaderTask.cancel(true);
            } else {
                // The same URL is already being downloaded.
                return false;
            }
        }
        return true;
    }

    /**
     * @param imageView Any imageView
     * @return Retrieve the currently active download task (if any) associated
     *         with this imageView. null if there is no such task.
     */
    private static BitmapDownloaderTask getBitmapDownloaderTask(ImageView imageView) {
        if (imageView != null) {
            Drawable drawable = imageView.getDrawable();
            if (drawable instanceof EnhDownloadedDrawable) {
                EnhDownloadedDrawable downloadedDrawable = (EnhDownloadedDrawable) drawable;
                return downloadedDrawable.getBitmapDownloaderTask();
            }
        }
        return null;
    }


    /*
      * An InputStream that skips the exact number of bytes provided, unless it
      * reaches EOF.
      */
    static class FlushedInputStream extends FilterInputStream {
        public FlushedInputStream(InputStream inputStream) {
            super(inputStream);
        }

        @Override
        public long skip(long n) throws IOException {
            long totalBytesSkipped = 0L;
            while (totalBytesSkipped < n) {
                long bytesSkipped = in.skip(n - totalBytesSkipped);
                if (bytesSkipped == 0L) {
                    int b = read();
                    if (b < 0) {
                        break; // we reached EOF
                    } else {
                        bytesSkipped = 1; // we read one byte
                    }
                }
                totalBytesSkipped += bytesSkipped;
            }
            return totalBytesSkipped;
        }
    }

	/**
	 * Read file from stored hashlink on SD
	 *
	 * @param f file from which we read
	 * @return read Bitmap
	 */
	private Bitmap readFile(File f) {
		try {
			return BitmapFactory.decodeStream(new FileInputStream(f));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

    /**
     * The actual AsyncTask that will asynchronously download the image.
     */
    class BitmapDownloaderTask extends AsyncTask<String, Void, Bitmap> {
        private String url;
        private final WeakReference<ProgressImageView> holderReference;

        public BitmapDownloaderTask(ProgressImageView holder) {
            holderReference = new WeakReference<ProgressImageView>(holder);
        }

        @Override
        protected void onPreExecute() {
            holderReference.get().progress.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        /**
         * Actual download method.
         */
        @Override
        protected Bitmap doInBackground(String... params) {
            url = params[0];
            Bitmap bmp = downloadBitmap(url);

            if (bmp == null) { // in case http link was wrong
                if (holderReference != null && holderReference.get() != null && holderReference.get().placeholder != null)
                    bmp = holderReference.get().noImage; // set no image if we didn't load
            }

            return bmp;
        }

        /**
         * Once the image is downloaded, associates it to the imageView
         */
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (holderReference == null || holderReference.get() == null) {
                return;
            } else {
                holderReference.get().progress.setVisibility(View.GONE);
            }

            if (isCancelled()) {
                bitmap = null;
            }

            ProgressImageView holder = new ProgressImageView();

            holder.bitmap = bitmap;

            addBitmapToCache(url, holder);

            holder.imageView = holderReference.get().imageView;

            BitmapDownloaderTask bitmapDownloaderTask = getBitmapDownloaderTask(holder.imageView);
            // Change bitmap only if this process is still associated with it
            // Or if we don't use any bitmap to task association
            // (NO_DOWNLOADED_DRAWABLE mode)
            if ((this == bitmapDownloaderTask) || (mode != Mode.CORRECT)) {
                holder.imageView.setImageBitmap(holder.bitmap);
            }
        }

        public AsyncTask<String, Void, Bitmap> executeTask(String... input){
            if(Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB){
                executeOnExecutor(THREAD_POOL_EXECUTOR, input);
            }else
                execute(input);
            return this;
        }
    }

    private Bitmap downloadBitmap(String url) {
        // AndroidHttpClient is not allowed to be used from the main thread
        final HttpClient client = (mode == Mode.NO_ASYNC_TASK) ? new DefaultHttpClient()
                : AndroidHttpClient.newInstance("Android");
        url = url.replace(" ", "%20");
        final HttpGet getRequest = new HttpGet(url);

        try {
            HttpResponse response = client.execute(getRequest);
            final int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                Log.e("ImageDownloader", "Error " + statusCode + " while retrieving bitmap from " + url);
                return null;
            }

            final HttpEntity entity = response.getEntity();
            if (entity != null) {
                InputStream inputStream = null;
                try {
                    inputStream = entity.getContent();
                    // return BitmapFactory.decodeStream(inputStream);
                    // Bug on slow connections, fixed in future release.

                    // create descriptor
                    String filename = String.valueOf(url.hashCode());
                    File f = new File(cacheDir, filename);

                    InputStream is = new URL(url).openStream();
                    // copy stream to file
                    OutputStream os = new FileOutputStream(f); // save stream to
                    // SD
                    copyStream(is, os);
                    os.close();

                    // decode image size
                    BitmapFactory.Options o = new BitmapFactory.Options();
                    o.inJustDecodeBounds = true;
                    BitmapFactory.decodeStream(new FileInputStream(f), null, o);

                    // Find the correct scale value. It should be the power of 2.

                    int width_tmp = o.outWidth, height_tmp = o.outHeight;
                    int scale = 1;
                    while (true) {
                        if (width_tmp / 2 < REQUIRED_IMAGE_SIZE || height_tmp / 2 < REQUIRED_IMAGE_SIZE)
                            break;
                        width_tmp /= 2;
                        height_tmp /= 2;
                        scale *= 2;
                    }

                    // decode with inSampleSize
                    BitmapFactory.Options o2 = new BitmapFactory.Options();
                    o2.inSampleSize = scale;
                    return BitmapFactory.decodeStream(new FlushedInputStream(inputStream), null, o2);
//					return BitmapFactory.decodeStream(inputStream);
                } finally {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                    entity.consumeContent();
                }
            }
        } catch (OutOfMemoryError error) {
            error.printStackTrace();
        } catch (IOException e) {
            getRequest.abort();
            Log.e(LOG_TAG, "I/O error while retrieving bitmap from " + url, e);
        } catch (IllegalStateException e) {
            getRequest.abort();
            Log.e(LOG_TAG, "Incorrect URL: " + url);
        } catch (Exception e) {
            getRequest.abort();
            Log.e(LOG_TAG, "Error while retrieving bitmap from " + url, e);
        } finally {
            if ((client instanceof AndroidHttpClient)) {
                ((AndroidHttpClient) client).close();
            }
        }
        return null;
    }


    public static class EnhDownloadedDrawable extends BitmapDrawable {
        private final WeakReference<BitmapDownloaderTask> bitmapDownloaderTaskReference;

        public EnhDownloadedDrawable(BitmapDownloaderTask bitmapDownloaderTask, ProgressImageView holder) {
            super(resources, holder.placeholder);

            bitmapDownloaderTaskReference = new WeakReference<BitmapDownloaderTask>(bitmapDownloaderTask);
        }

        public BitmapDownloaderTask getBitmapDownloaderTask() {
            return bitmapDownloaderTaskReference.get();
        }

        @Override
        public int getOpacity() {
            return 0;
        }

        @Override
        public void setAlpha(int alpha) {
        }

        @Override
        public void setColorFilter(ColorFilter cf) {
        }
    }

    /*
      * Cache-related fields and methods.
      *
      * We use a hard and a soft cache. A soft reference cache is too
      * aggressively cleared by the Garbage Collector.
      */

    private static final int HARD_CACHE_CAPACITY = 30;
    private static final int DELAY_BEFORE_PURGE = 120 * 1000; // in milliseconds

    // Hard cache, with a fixed maximum capacity and a life duration
    private final HashMap<String, ProgressImageView> sHardBitmapCache = new LinkedHashMap<String, ProgressImageView>(HARD_CACHE_CAPACITY / 2, 0.75f, true) {
        /**
         *
         */
        private static final long serialVersionUID = 7891177567092447801L;

        @Override
        protected boolean removeEldestEntry(LinkedHashMap.Entry<String, ProgressImageView> eldest) {
            if (size() > HARD_CACHE_CAPACITY) {
                // Entries push-out of hard reference cache are transferred to
                // soft reference cache
                sSoftBitmapCache.put(eldest.getKey(), new SoftReference<ProgressImageView>(eldest.getValue()));
                return true;
            } else
                return false;
        }
    };

    // Soft cache for bitmaps kicked out of hard cache
    private final static ConcurrentHashMap<String, SoftReference<ProgressImageView>> sSoftBitmapCache = new ConcurrentHashMap<String, SoftReference<ProgressImageView>>(HARD_CACHE_CAPACITY / 2);

    /**
     * Adds this bitmap to the cache.
     *
     * @param holder The newly downloaded bitmap.
     */
    private void addBitmapToCache(String url, ProgressImageView holder) {
        if (holder != null) {
            synchronized (sHardBitmapCache) {
                sHardBitmapCache.put(url, holder);
            }
        }
    }

	private String getApplicationCacheDir(String packageName) {
		// path should match the specified string
		// /Android/data/<package_name>/files/
		return "Android/data/" + packageName + "/cache/";
	}

	private void copyStream(InputStream is, OutputStream os) {
		final int buffer_size = 1024;
		try {
			byte[] bytes = new byte[buffer_size];
			for (;;) {
				int count = is.read(bytes, 0, buffer_size);
				if (count == -1)
					break;
				os.write(bytes, 0, count);
			}
		} catch (Exception ignored) {
		}
	}
}
