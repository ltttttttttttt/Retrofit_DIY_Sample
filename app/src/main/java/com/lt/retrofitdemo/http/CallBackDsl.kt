package com.lt.retrofitdemo.http

import com.alibaba.fastjson.JSONObject
import com.lt.retrofitdemo.print

/**
 * creator: lt  2020/3/26  lt.dygzs@qq.com
 * effect : 网络请求回调的sdl封装
 * warning:
 */
/**
 * 使用dsl的callback
 * ps: CallBackDsl.()这种语法相当于CallBackDsl的一个扩展函数,把CallBackDsl当做这个函数的this,所以该函数中可以不用this.就可以调用CallBackDsl的参数和方法
 */
inline fun <reified T> callbackOf(initDsl: CallBackDsl<T>.() -> Unit): ObserverCallBack {
    val dsl = CallBackDsl<T>()
    dsl.initDsl()//初始化dsl
    if (dsl.isAutoShowLoading)
        "Show loading dialog".print()
    return object : ObserverCallBack {
        override fun handleResult(data: String?, encoding: Int, method: Int) {
            if (dsl.isAutoShowLoading)
                "Dismiss loading dialog".print()
            //可以在这里根据业务判断是否请求成功
            //引入fastjson来解析json    implementation 'com.alibaba:fastjson:1.2.67'
            val bean = JSONObject.parseObject(data, T::class.java)
            if (bean != null) {
                dsl.mSuccess?.invoke(bean)
            } else {
                dsl.mFailed?.invoke(data)
            }
        }
    }
}

class CallBackDsl<T> {
    /**
     * 网络请求成功的回调
     */
    var mSuccess: ((bean: T) -> Unit)? = null

    fun success(listener: (bean: T) -> Unit) {
        mSuccess = listener
    }

    /**
     * 网络请求失败的回调
     */
    var mFailed: ((data: String?) -> Unit)? = null

    fun failed(listener: (data: String?) -> Unit) {
        mFailed = listener
    }

    /**
     * 是否自动弹出和关闭loading
     */
    var isAutoShowLoading = true
}