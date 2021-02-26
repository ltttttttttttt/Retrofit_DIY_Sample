package com.lt.retrofitdemo.source

import com.lt.retrofitdemo.http.ObserverCallBack
import org.json.JSONObject
import java.lang.reflect.Type

/**
 * creator: lt  2020/4/16  lt.dygzs@qq.com
 * effect : dsl方式封装网络请求
 * warning:
 */

/**
 * 使用匿名内部类的方式快捷使用网络请求
 * 默认情况下,自动弹出加载窗,自动关闭加载窗,自动转换类,自动弹出错误的toast
 * ps:如果返回的是list,请重新设置type;由于匿名内部类会引用上层类,所以重量级的网络请求不要使用(所有接口都少用)
 */
inline fun <reified T> callBackOf(crossinline initDsl: OCBDsl<T>.() -> Unit = {}): ObserverCallBack {
//    val activity = AppManager.currentActivity()
//    val dsl = OCBDsl<T>()
//    dsl.type = T::class.java
//    dsl.initDsl()
//    if (dsl.isAutoShowWaitDialog)
//        activity?.showWaitDialog()
//    val baseHttp = object : BaseHttp {
//        override fun onHttpSuccess(data: String, msg: String?, method: Int) {
//            data.w2("$method http")
//            privateDismissWaitDialog()
//            dsl.getSuccess()?.invoke(data.json2Any(dsl.type))
//        }
//
//        override fun onHttpFailed(data: String?, message: String?, method: Int) {
//            data.e2("$method f_http")
//            privateDismissWaitDialog()
//            if (dsl.isFailedShowToast)
//                message.showToast()
//            if (dsl.getFailed() != null && data != null) {
//                val jsonObject = JSONObject(data)
//                if (jsonObject.has(InitJson.DATA))
//                    dsl.getFailed()?.invoke(jsonObject.getString(InitJson.DATA))
//            }
//            dsl.getFailedData()?.invoke(data)
//        }
//
//        override fun privateDismissWaitDialog() {
//            if (dsl.isAutoDismissWaitDialog)
//                activity?.dismissWaitDialog()
//        }
//    }
//    return ObserverCallBack { data, encoding, method ->
//        InitJson.initJson(baseHttp, data, encoding, method)
//    }
    return null!!
}

/**
 * ObserverCallBack的dsl
 */
class OCBDsl<T> {
    /**
     * 成功的回调
     */
    private var success: ((data: T?) -> Unit)? = null

    /**
     * 失败的回调,拿到失败code
     */
    private var failed: ((code: String) -> Unit)? = null

    /**
     * 失败的回调,拿到失败完整数据
     */
    private var failedData: ((data: String?) -> Unit)? = null

    /**
     * 是否自动弹出加载窗
     */
    var isAutoShowWaitDialog = true

    /**
     * 是否自动关闭加载窗
     */
    var isAutoDismissWaitDialog = true

    /**
     * 是否在失败的时候并弹出message的toast
     */
    var isFailedShowToast = true

    /**
     *  生成类的type(class),如果是list,则需要手动修改type,比如 listTypeOf<Any>()
     */
    var type: Type? = null

    fun success(success: (data: T?) -> Unit) {
        this.success = success
    }

    fun getSuccess() = success

    fun failed(failed: (code: String) -> Unit) {
        this.failed = failed
    }

    fun getFailed() = failed

    fun failedData(failedData: (data: String?) -> Unit) {
        this.failedData = failedData
    }

    fun getFailedData() = failedData
}