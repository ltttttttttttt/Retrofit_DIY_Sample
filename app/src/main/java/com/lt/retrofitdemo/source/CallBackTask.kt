package com.lt.retrofitdemo.source

import android.text.TextUtils
import com.lt.retrofitdemo.http.ObserverCallBack
import java.lang.ref.WeakReference

/**
 * 创    建:  lt  2016/10/14--15:59
 * 作    用:  解耦网络请求
 * 注意事项:   如果网络请求的回调是一个匿名内部类,且没有其他强引用,则很有可能会被回收掉,解决方案:推荐,设置为this;可用:把匿名内部类赋值为变量,在onDestroy时置为null;
 */
class CallBackTask<T> private constructor(private val isMultithreading: Boolean/*是否运行在多线程环境下(多线程会进行加锁操作,相对耗时)*/) {
    private var id = Int.MIN_VALUE//唯一的key,依次递增

    /**
     * 存储CallBack的map
     * key为对象的类名+hashCode+id
     * value为对象的弱引用
     */
    private val map = HashMap<String, WeakReference<T?>?>()

    private val ADD = 0
    private val REMOVE_KEY = 1
    private val REMOVE_OBJECT = 2
    private val GET = 3
    private val CLEAR_NULL = 4
    private val CLEAR_ALL = 5
    private val JUST_GET = 6
    /**
     * 将回调的引用存储在此,并返回key
     */
    fun add(value: T?): String {
        return if (isMultithreading) doubleThread(ADD, value).toString() else singleThread(ADD, value).toString()
    }

    /**
     * 根据key移除引用
     */
    fun remove(key: String?) {
        if (isMultithreading) doubleThread(REMOVE_KEY, key) else singleThread(REMOVE_KEY, key)
    }

    /**
     * 移除传入的对象
     */
    fun remove(value: T?) {
        if (isMultithreading) doubleThread(REMOVE_OBJECT, value) else singleThread(REMOVE_OBJECT, value)
    }

    /**
     * 根据key取出并移除回调,需要判断返回值是否为null,若为null可能已经销毁,获取完成后自动移除引用,适用于大多数场景
     */
    operator fun get(key: String?): T? {
        return (if (isMultithreading) doubleThread(GET, key) else singleThread(GET, key)) as? T
    }

    /**
     * 只是取出回调,不移除
     * 经测试,在多线程遍历时,只是单纯获取并不会触发异常
     */
    fun justGet(key: String?): T? {
        return (if (isMultithreading) doubleThread(JUST_GET, key) else singleThread(JUST_GET, key)) as? T
    }

    /**
     * 清除所有是null的对象
     */
    fun clearNull() {
        if (isMultithreading) doubleThread(CLEAR_NULL, null) else singleThread(CLEAR_NULL, null)
    }

    /**
     * 清除所有引用
     */
    fun clearAll() {
        if (isMultithreading) doubleThread(CLEAR_ALL, null) else singleThread(CLEAR_ALL, null)
    }

    /**
     * 单线程方法,isMultithreading为false时执行,需要调用者保证调用的都是在同一个线程,否则可能抛出ConcurrentModificationException
     */
    private fun singleThread(state: Int, obj: Any?): Any? {
        when (state) {
            ADD -> {
                obj ?: return ""
                val key = obj.javaClass.name + Integer.toHexString(obj.hashCode()) + id++
                map[key] = WeakReference(obj as T)
                return key
            }
            REMOVE_KEY -> {
                if (obj == null || map.size == 0) return null
                val stringKey = obj.toString()
                if (TextUtils.isEmpty(stringKey)) return null
                map.remove(stringKey)
            }
            REMOVE_OBJECT -> {
                if (obj == null || map.size == 0) return null
                val objKeyPrefix = obj.javaClass.name + Integer.toHexString(obj.hashCode())
                val iterator = map.keys.iterator()
                while (iterator.hasNext())
                    if (iterator.next().contains(objKeyPrefix)) iterator.remove()
            }
            GET -> {
                if (obj == null || map.size == 0) return null
                val stringKey = obj.toString()
                if (TextUtils.isEmpty(stringKey)) return null
                return map.remove(stringKey)?.get()
            }
            CLEAR_NULL -> {
                if (map.size == 0) return null
                val iterator = map.entries.iterator()
                while (iterator.hasNext())
                    if (iterator.next().value?.get() == null) iterator.remove()
            }
            CLEAR_ALL -> {
                map.clear()
                id = Int.MIN_VALUE
            }
            JUST_GET -> {
                if (obj == null || map.size == 0) return null
                val stringKey = obj.toString()
                if (TextUtils.isEmpty(stringKey)) return null
                return map[stringKey]?.get()
            }
        }
        return null
    }

    /**
     * 多线程方法,isMultithreading为true时执行,无需担心抛异常,但是效率略低(相对于单线程方法)
     */
    @Synchronized
    private fun doubleThread(state: Int, obj: Any?): Any? {
        return singleThread(state, obj)
    }

    companion object {
        /**
         * 获取单实例,由于网络请求在应用中基本一直会用到,所以直接使用饿汉单例
         * ps:如果有需求需要同时存其他种类的回调,可以再创建一个这样的方法,new的泛型改一下就行
         */
        @JvmStatic
        val instance = CallBackTask<ObserverCallBack>(false)
    }
}