package code.jesse.palette.tools;

import android.util.Log;
import android.util.SparseArray;

import java.util.LinkedList;
import java.util.Queue;

/**
 * 用于循环利用一些可复用的对象，例如View。 内部实现是基于recycleKey的队列，每个recycleKey都有自己的一个队列，单进单出。需要注意的是 这个Helper设计是基于单线程模型的，因此不要把它用在多线程的情况下。
 *
 * @author zhulantian@gmail.com
 */
public class RecycleBin<T> {

    private static final String LOG_TAG = "RecycleBin";

    private final SparseArray<Queue<T>> map;

    public RecycleBin() {
        this.map = new SparseArray<Queue<T>>();
    }

    /**
     * 添加一个循环对象。
     *
     * @param recycleKey the recycle key.
     * @param obj        recyclable object.
     */
    public void put(int recycleKey, T obj) {
        Queue<T> queue = map.get(recycleKey);
        if (queue == null) {
            queue = new LinkedList<T>();
            map.put(recycleKey, queue);
        }
        queue.add(obj);
        // Log.d(LOG_TAG, "[Put] recycleKey=" + recycleKey + ", queueSize=" + queue.size());
    }

    /**
     * 获取一个循环使用的对象，若没有则返回null.
     *
     * @param recycleKey the recycle key.
     * @return recyclable object.
     */
    public T get(int recycleKey) {
        Queue<T> queue = map.get(recycleKey);
        if (queue != null) {
            T object = queue.poll();
            if (queue.isEmpty()) {
                map.remove(recycleKey);
            }
            Log.d(LOG_TAG, "[Hit] recycleKey=" + recycleKey);
            return object;
        }
        // Log.d(LOG_TAG, "[Miss] recycleKey=" + recycleKey);
        return null;
    }

    /**
     * 清空指定recycleKey的队列。
     *
     * @param recycleKey the recycle key.
     */
    public void clear(int recycleKey) {
        map.remove(recycleKey);
    }

    public void clear() {
        map.clear();
    }
}
