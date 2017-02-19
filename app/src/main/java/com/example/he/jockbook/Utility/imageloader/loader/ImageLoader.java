package com.example.he.jockbook.Utility.imageloader.loader;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.StatFs;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.widget.ImageView;

import com.example.he.jockbook.R;
import com.example.he.jockbook.Utility.imageloader.utils.MyUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by he on 2017/1/9.
 */

public class ImageLoader {

    private static final String TAG = "imageLoader";

    private static final int MESSAGE_POST_RESULT = 1;

    private static final int TAG_KEY_URI = R.id.imageloader_uri;//将每个image和url绑定


    //线程池必要的参数
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int CORE_POOL_SIZE = CPU_COUNT + 1;
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;
    private static final long KEEP_ALIVE = 10L;

    //线程工厂，为线程池提供创建新线程的功能
    private static final ThreadFactory mThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "ImageLoader+" + mCount.get());
        }
    };
    //用于异步操作加载图片
    public final static Executor threadPoolExecutor = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(), mThreadFactory);

    //异步操作加载图片后，主线程更新UI
    private Handler mMainHandler = new Handler(Looper.getMainLooper()) {

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case MESSAGE_POST_RESULT:
                    LoaderResult result = (LoaderResult) msg.obj;
                    ImageView imageView = result.imageView;
                    String uri = (String) imageView.getTag(TAG_KEY_URI);
                    //为了解决由于View复用导致的列表错乱的问题，将imageView和uri绑定
                    if (uri.equals(result.uri)) {
                        imageView.setImageBitmap(result.bitmap);
                    }
                    break;
                default:
                    break;
            }

            super.handleMessage(msg);
        }
    };

    private static final long DISK_CACHE_SIZE = 1024 * 1024 * 50;//设置磁盘缓存的大小
    private static final int IO_BUFFER_SIZE = 8 * 1024;//设置I/O流缓存大小
    private static final int DISK_CACHE_INDEX = 0;//因为DiskLruCache设置的是每个节点对应1个数据，所以是0，用于取出缓存用
    private boolean mIsDiskLruCacheCreated = false;//判断本次存储文件是否创建

    private Context mContext;
    private ImageResizer mImageResizer = new ImageResizer();
    private LruCache<String, Bitmap> lruCache;
    private DiskLruCache diskLruCache;

    private ImageLoader(Context context) {
        mContext = context.getApplicationContext();
        //初始化内存缓存
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int cacheSize = maxMemory / 8;
        lruCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getRowBytes() * value.getHeight() / 1024;
            }
        };

        //创建磁盘缓存目录
        File diskCacheDir = getDiskCacheDirFile(mContext, "Bitmap");
        if (!diskCacheDir.exists()) {
            diskCacheDir.mkdirs();
        }

        //检测磁盘内存是否足够
        if (getUsableSpace(diskCacheDir) > DISK_CACHE_SIZE) {
            try {
                diskLruCache = diskLruCache.open(diskCacheDir, 1, 1, DISK_CACHE_SIZE);
                mIsDiskLruCacheCreated = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 创建一个ImageLoader实例
     *
     * @param context
     * @return
     */

    public static ImageLoader build(Context context) {
        return new ImageLoader(context);
    }


    /**
     * 异步加载图片的接口
     *
     * @param uri
     * @param imageView
     */
    public void bindBitmap(final String uri, final ImageView imageView) {
        bindBitmap(uri, imageView, 0, 0);
    }

    public void bindBitmap(final String uri, final ImageView imageView, final int reqWidth, final int reqHeight) {
        imageView.setTag(TAG_KEY_URI, uri);//将图片和uri绑定
        Bitmap bitmap = loadBitmapFromMemCache(uri);
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
            return;
        }

        Runnable loadBitmapTask = new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = loadBitmap(uri, reqWidth, reqHeight);
                if (bitmap != null) {
                    LoaderResult result = new LoaderResult(bitmap, imageView, uri);
                    mMainHandler.obtainMessage(MESSAGE_POST_RESULT, result).sendToTarget();
                }
            }
        };
        threadPoolExecutor.execute(loadBitmapTask);
    }


    /**
     * 从内存、本地磁盘、网络中加载图片
     *
     * @param uri
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public Bitmap loadBitmap(String uri, int reqWidth, int reqHeight) {
        Bitmap bitmap = loadBitmapFromMemCache(uri);
        if (bitmap != null) {
            return bitmap;
        }

        try {
            bitmap = loadBitmapFromDiskCache(uri, reqWidth, reqHeight);
            if (bitmap != null)
                return bitmap;

            bitmap = loadBitmapFormHttp(uri, reqWidth, reqHeight);

        } catch (IOException e) {
            e.printStackTrace();
        }

        //本地磁盘不足，无法缓存，直接从网络中获取
        if (bitmap == null && !mIsDiskLruCacheCreated) {
            bitmap = downloadBitmapFromUrl(uri);
        }

        return bitmap;

    }


    /**
     * 从内存中加载图片
     *
     * @param url
     * @return
     */
    private Bitmap loadBitmapFromMemCache(String url) {
        final String key = hashKeyForUrl(url);
        Bitmap bitmap = lruCache.get(key);
        return bitmap;
    }


    /**
     * 从本地磁盘上加载图片
     *
     * @param url
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    private Bitmap loadBitmapFromDiskCache(String url, int reqWidth, int reqHeight) throws IOException {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            Log.d(TAG, "你将在主线程中加载图片，不推荐这样使用");
        }
        if (diskLruCache == null)
            return null;
        Bitmap bitmap = null;
        String key = hashKeyForUrl(url);
        DiskLruCache.Snapshot snapshot = diskLruCache.get(key);
        if (snapshot != null) {
            FileInputStream fileInput = (FileInputStream) snapshot.getInputStream(DISK_CACHE_INDEX);
            FileDescriptor descriptor = fileInput.getFD();
            //将压缩后的图片取出
            bitmap = mImageResizer.decodeSampleBitmapFromFileDescriptor(descriptor, reqWidth, reqHeight);
            if (bitmap != null) {
                //将该缓存加入到内存中
                addBitmapToMemoryCache(key, bitmap);
            }
        }

        return bitmap;
    }


    /**
     * 从网络中下载图片并保存到本地磁盘
     *
     * @return
     */
    private Bitmap loadBitmapFormHttp(String url, int reqWidth, int reqHeight) throws IOException {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            throw new RuntimeException("主线程不能开启网络连接");
        }
        if (diskLruCache == null)
            return null;
        String key = hashKeyForUrl(url);
        DiskLruCache.Editor editor = diskLruCache.edit(key);
        if (editor != null) {
            OutputStream output = editor.newOutputStream(DISK_CACHE_INDEX);
            if (downloadUrlToStream(url, output)) {
                editor.commit();
            } else {
                editor.abort();//保存本地缓存失败回退
            }
            diskLruCache.flush();
        }
        return loadBitmapFromDiskCache(url, reqWidth, reqHeight);
    }


    /**
     * 从网络中下载图片并保存到输出流中
     *
     * @param urlString
     * @param outputStream
     * @return
     */
    private boolean downloadUrlToStream(String urlString, OutputStream outputStream) {
        HttpURLConnection connection = null;
        BufferedOutputStream out = null;
        BufferedInputStream in = null;
        try {
            final URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            in = new BufferedInputStream(connection.getInputStream(), IO_BUFFER_SIZE);
            out = new BufferedOutputStream(outputStream, IO_BUFFER_SIZE);


            int b;
            while ((b = in.read()) != -1) {
                out.write(b);
            }
            return true;

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            MyUtils.close(out);
            MyUtils.close(in);
        }
        return false;
    }


    /**
     * 直接从网络中下载图片不保存到本地磁盘中
     *
     * @param urlString
     * @return
     */
    private Bitmap downloadBitmapFromUrl(String urlString) {
        Bitmap bitmap = null;
        HttpURLConnection connection = null;
        BufferedInputStream input = null;

        try {
            final URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            input = new BufferedInputStream(connection.getInputStream(), IO_BUFFER_SIZE);
            bitmap = BitmapFactory.decodeStream(input);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            connection.disconnect();
            MyUtils.close(input);
        }
        return bitmap;
    }


    /**
     * 将图片加入到内存缓存中
     *
     * @param key
     * @param bitmap
     */
    private void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (lruCache.get(key) == null) {
            lruCache.put(key, bitmap);
        }
    }


    /**
     * 将url转换为key，将url的MD5值作为Key
     *
     * @param url
     * @return
     */
    private String hashKeyForUrl(String url) {
        String key;
        try {
            final MessageDigest mDigest = MessageDigest.getInstance("MD5");
            mDigest.update(url.getBytes());
            key = bytesToHexString(mDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            key = String.valueOf(url.hashCode());
            e.printStackTrace();
        }
        return key;
    }

    private String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }


    /**
     * 创建文件夹用于保存磁盘缓存
     *
     * @param context
     * @param name
     * @return
     */
    private File getDiskCacheDirFile(Context context, String name) {
        boolean externalStorageAvailable = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        final String cachePath;
        if (externalStorageAvailable) {
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        return new File(cachePath + File.separator + name);
    }

    /**
     * 获取磁盘剩余空间大小
     *
     * @param path
     * @return
     */
    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    private long getUsableSpace(File path) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            return path.getUsableSpace();
        }
        final StatFs statFs = new StatFs(path.getPath());
        return statFs.getBlockSizeLong() * statFs.getAvailableBlocksLong();
    }


    /**
     * 图片信息的封装
     */
    private static class LoaderResult {
        public ImageView imageView;
        public String uri;
        public Bitmap bitmap;

        public LoaderResult(Bitmap bitmap, ImageView imageView, String uri) {
            this.bitmap = bitmap;
            this.imageView = imageView;
            this.uri = uri;
        }
    }


}
