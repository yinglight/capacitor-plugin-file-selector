package com.valley.file.selector.media;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;

import com.valley.file.selector.Util;

import java.lang.ref.SoftReference;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 异步加载文件的图标和缩略图，大多数是单线程。
 */
public class FileIconLoader implements Handler.Callback {

    private static final String LOADER_THREAD_NAME = "FileIconLoader";

    /**
     * UI线程发送自身消息类型，并指示某些图片需要加载
     */
    private static final int MESSAGE_REQUEST_LOADING = 1;

    /**
     *UI线程发送自身消息类型，并指示某些图片已经加载
     */
    private static final int MESSAGE_ICON_LOADED = 2;

    private static abstract class ImageHolder {
        public static final int NEEDED = 0;

        public static final int LOADING = 1;

        public static final int LOADED = 2;

        int state;

        public static ImageHolder create() {
            return new BitmapHolder();
        }

        public abstract boolean setImageView(ImageView v);

        public abstract boolean isNull();

        public abstract void setImage(Object image);
    }

    private static class BitmapHolder extends ImageHolder {
        SoftReference<Bitmap> bitmapRef;

        @Override
        public boolean setImageView(ImageView v) {
            if (bitmapRef.get() == null)
                return false;
            v.setImageBitmap(bitmapRef.get());
            return true;
        }

        @Override
        public boolean isNull() {
            return bitmapRef == null;
        }

        @Override
        public void setImage(Object image) {
            bitmapRef = image == null ? null : new SoftReference<Bitmap>((Bitmap) image);
        }
    }

    /**
     * 一个软缓存图片缩略图，key是文件路径
     */
    private final static ConcurrentMap<String, ImageHolder> mImageCache = new ConcurrentHashMap<String, ImageHolder>();

    /**
     * 从ImageView到图片ID的映射。请注意在这个图片正在请求加载已经开始，这个图片ID可能改变
     */
    private final ConcurrentMap<ImageView, FileId> mPendingRequests = new ConcurrentHashMap<ImageView, FileId>();

    /**
     * 发送给UI消息的处理程序
     */
    private final Handler mMainThreadHandler = new Handler(this);

    /**
     * 该线程代表从数据库加载图片。第一次请求创建。
     */
    private LoaderThread mLoaderThread;

    /**
     * 确保我们一次仅发送一个MESSAGE_PHOTOS_NEEDED实例的门。
     */
    private boolean mLoadingRequested;

    /**
     * 图片加载暂停指示标志
     */
    private boolean mPaused;

    private final Context mContext;

    private final Util.Category type;

    public FileIconLoader(Context context, Util.Category type) {
        mContext = context;
        this.type = type;
    }

    public static class FileId {
        public String mPath;

        public long mId; // 数据库ID

        public FileId(String path, long id) {
            mPath = path;
            mId = id;
        }
    }

    /**
     * 加载照片到图片视图中。如果照片已经缓存，就直接展示。否则从数据库请求加载。
     * @param id, database id
     */
    public boolean loadIcon(ImageView view, String path, long id) {
        boolean loaded = loadCachedIcon(view, path);
        if (loaded) {
            mPendingRequests.remove(view);
        } else {
            FileId p = new FileId(path, id);
            mPendingRequests.put(view, p);
            if (!mPaused) {
                // 发送一个请求加载一些图片
                requestLoading();
            }
        }
        return loaded;
    }

    public void cancelRequest(ImageView view) {
        mPendingRequests.remove(view);
    }

    /**
     * 检查照片是否在缓存中，如果是，设置照片到视图中，否则，设置照片状态给
     * {@link BitmapHolder#NEEDED}
     */
    private boolean loadCachedIcon(ImageView view, String path) {
        ImageHolder holder = mImageCache.get(path);

        if (holder == null) {
            holder = ImageHolder.create();
            if (holder == null)
                return false;

            mImageCache.put(path, holder);
        } else if (holder.state == ImageHolder.LOADED) {
            if (holder.isNull()) {
                return true;
            }


            // 失败设置 imageview 意味着引用被GC释放，我们需要重载图片。
            if (holder.setImageView(view)) {
                return true;
            }
        }

        holder.state = ImageHolder.NEEDED;
        return false;
    }

    public long getDbId(String path) {
        String volumeName = "external";
        Uri uri = MediaStore.Images.Media.getContentUri(volumeName);
        String selection = MediaStore.Files.FileColumns.DATA + "=?";
        ;
        String[] selectionArgs = new String[]{
                path
        };

        String[] columns = new String[]{
                MediaStore.Files.FileColumns._ID, MediaStore.Files.FileColumns.DATA
        };

        Cursor c = mContext.getContentResolver()
                .query(uri, columns, selection, selectionArgs, null);
        if (c == null) {
            return 0;
        }
        long id = 0;
        if (c.moveToNext()) {
            id = c.getLong(0);
        }
        c.close();
        return id;
    }

    /**
     * 停止正在加载的图片，杀掉图片加载器线程和清除所有缓存。
     */
    public void stop() {
        pause();

        if (mLoaderThread != null) {
            mLoaderThread.quit();
            mLoaderThread = null;
        }

        clear();
    }

    public void clear() {
        mPendingRequests.clear();
        mImageCache.clear();
    }

    public void pause() {
        mPaused = true;
    }

    public void resume() {
        mPaused = false;
        if (!mPendingRequests.isEmpty()) {
            requestLoading();
        }
    }

    /**
     * 向该线程本身发送消息以开始加载图像。 如果
     * 当前视图包含多个图像视图，所有这些图像视图将
     * 有机会先要求他们各自的照片
     * 请求被执行。 这使我们可以批量加载图像。
     *
     */
    private void requestLoading() {
        if (!mLoadingRequested) {
            mLoadingRequested = true;
            mMainThreadHandler.sendEmptyMessage(MESSAGE_REQUEST_LOADING);
        }
    }

    /**
     * 处理主线程请求
     */
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MESSAGE_REQUEST_LOADING: {
                mLoadingRequested = false;
                if (!mPaused) {
                    if (mLoaderThread == null) {
                        mLoaderThread = new LoaderThread();
                        mLoaderThread.start();
                    }

                    mLoaderThread.requestLoading();
                }
                return true;
            }

            case MESSAGE_ICON_LOADED: {
                if (!mPaused) {
                    processLoadedIcons();
                }
                return true;
            }
        }
        return false;
    }

    /**
     * Goes over pending loading requests and displays loaded photos. If some of
     * the photos still haven't been loaded, sends another request for image
     * loading.
     */
    private void processLoadedIcons() {
        Iterator<ImageView> iterator = mPendingRequests.keySet().iterator();
        while (iterator.hasNext()) {
            ImageView view = iterator.next();
            FileId fileId = mPendingRequests.get(view);
            boolean loaded = loadCachedIcon(view, fileId.mPath);
            if (loaded) {
                iterator.remove();
            }
        }

        if (!mPendingRequests.isEmpty()) {
            requestLoading();
        }
    }

    /**
     * The thread that performs loading of photos from the database.
     */
    private class LoaderThread extends HandlerThread implements Handler.Callback {
        private Handler mLoaderThreadHandler;

        public LoaderThread() {
            super(LOADER_THREAD_NAME);
        }

        /**
         * Sends a message to this thread to load requested photos.
         */
        public void requestLoading() {
            if (mLoaderThreadHandler == null) {
                mLoaderThreadHandler = new Handler(getLooper(), this);
            }
            mLoaderThreadHandler.sendEmptyMessage(0);
        }

        /**
         * Receives the above message, loads photos and then sends a message to
         * the main thread to process them.
         */
        public boolean handleMessage(Message msg) {
            Iterator<FileId> iterator = mPendingRequests.values().iterator();
            while (iterator.hasNext()) {
                FileId id = iterator.next();
                ImageHolder holder = mImageCache.get(id.mPath);
                if (holder != null && holder.state == ImageHolder.NEEDED) {
                    // Assuming atomic behavior
                    holder.state = ImageHolder.LOADING;
                    if (id.mId == 0)
                        id.mId = getDbId(id.mPath);
                    if (id.mId == 0) {
                        Log.e("FileIconLoader", "Fail to get dababase id for:" + id.mPath);
                    }
                    holder.setImage(getImageThumbnail(id.mId));
                    holder.state = BitmapHolder.LOADED;
                    mImageCache.put(id.mPath, holder);
                }
            }

            mMainThreadHandler.sendEmptyMessage(MESSAGE_ICON_LOADED);
            return true;
        }

        private static final int MICRO_KIND = 3;

        private Bitmap getImageThumbnail(long id) {
            if (type == Util.Category.image) {
                return MediaStore.Images.Thumbnails.getThumbnail(mContext.getContentResolver(), id, MICRO_KIND, null);
            } else {
                return MediaStore.Video.Thumbnails.getThumbnail(mContext.getContentResolver(), id, MICRO_KIND, null);
            }

        }
    }
}
