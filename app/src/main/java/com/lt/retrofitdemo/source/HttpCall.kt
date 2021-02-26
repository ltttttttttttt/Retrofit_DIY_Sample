package com.lt.retrofitdemo.source

import com.alibaba.fastjson.JSONObject
import com.lt.retrofitdemo.http.ObserverCallBack
import com.lt.retrofitdemo.source.retrofit2.Callback
import com.lt.retrofitdemo.source.retrofit2.Response
import okhttp3.Call
import okhttp3.Request
import okhttp3.ResponseBody
import okio.Timeout
import java.lang.reflect.Method
import java.lang.reflect.Type
import kotlin.reflect.KFunction
import kotlin.reflect.jvm.javaType
import kotlin.reflect.jvm.kotlinFunction

/**
 * creator: lt  2020/6/5  lt.dygzs@qq.com
 * effect : 类似retrofit2 Call的实现类,用来做协程调用
 * warning:
 */
class HttpCall<T>(val method: Method, val args: Array<out Any?>?) {
    var call: Call? = null
    var tType: Type? = null
    var callback: ObserverCallBack? = null
    var kFunction: KFunction<*>? = null
    var annotation: Annotation? = null
    var methodId = 0

    fun execute(): Response<T> {
//        if (call == null) {
//            val kFunction = method.kotlinFunction
//                    ?: throw NoSuchMethodException("${method.name} not find")
//            val annotation = method.getAnnotation(POST::class.java)
//                    ?: method.getAnnotation(GET::class.java)
//                    ?: throw RuntimeException("${method.name} from of HttpFunctions not add annotation ,or added error annotation.")
//            val (call, callback) = when (annotation) {
//                is POST -> HttpFunctionsHandler.postCall(kFunction, annotation, args)
//                is GET -> HttpFunctionsHandler.getCall(kFunction, annotation, args)
//                else -> throw RuntimeException("${method.name} from of HttpFunctions not add annotation ,or added error annotation.")
//            }
//            this.methodId = HttpFunctionsHandler.getMethodId(kFunction)
//            this.call = call
//            this.tType = kFunction.returnType.arguments.first().type!!.javaType
//            this.callback = callback
//            this.kFunction = kFunction
//            this.annotation = annotation
//        }
//
//        val execute = try {
//            call?.execute()
//        } catch (e: Exception) {
//            "LtHttpCall.execute 23 : message:${e.message}".e2()
//            HandlerPool.post {
//                callback?.handleResult("", AsyncHttpRequest.FAILURE_HTTP, methodId)
//            }
//            null
//        }
//        var json = execute?.body()?.string()
//        if (json?.isNotEmpty() == true) {
//            val annotation = annotation
//            if (
//                    (annotation is POST && annotation.isEncryption)
//                    || (annotation is GET && annotation.isEncryption)
//            )
//                json = NativeLibUtil.o(json)
//            val (encoding, data, msg, method) = InitJson.analysisJson(json, AsyncHttpRequest.SUCCESS_HTTP, 0)
//            when (encoding) {
//                AsyncHttpRequest.SUCCESS_HTTP -> {
//                    "成功结果== $json".w2("$method s_http")
//                    if (tType == String::class.java && data != null)
//                        return Response.success(data as T)
//                    val t = JSONObject.parseObject<Any?>(data as? String, tType)
//                    if (t != null)
//                        return Response.success(t as T)
//                }
//                AsyncHttpRequest.FAILURE_HTTP ->
//                    HandlerPool.post {
//                        "网络失败结果== $json".w2("$method f_http")
//                        callback?.handleResult(json, AsyncHttpRequest.FAILURE_HTTP, methodId)
//                    }
//                AsyncHttpRequest.FAILURE_NETWORK ->
//                    HandlerPool.post {
//                        "失败结果== $json".e2("$method f_http")
//                        callback?.handleResult(json, AsyncHttpRequest.FAILURE_NETWORK, methodId)
//                    }
//            }
//        } else
//            "结果为空== $json".e2("$method f_http")
        return Response.error(0, ResponseBody.create(null, ""))
    }

    //回调在子线程
    fun enqueue(callback: Callback<T>?) {
//        ThreadPool.submitToCacheThreadPool {
//            val execute = try {
//                execute()
//            } catch (e: Exception) {
//                callback?.onFailure(this, e)
//                null
//            }
//            if (execute?.body() != null)
//                callback?.onResponse(this, execute)
//            else
//                callback?.onFailure(this, null)
//        }
    }

    fun isExecuted(): Boolean? = call?.isExecuted

    fun cancel() = call?.cancel()

    fun isCanceled(): Boolean? = call?.isCanceled

    fun clone(): HttpCall<T> = HttpCall<T>(method, args).apply {
        this.call = this@HttpCall.call
        this.annotation = this@HttpCall.annotation
        this.kFunction = this@HttpCall.kFunction
        this.tType = this@HttpCall.tType
        this.callback = this@HttpCall.callback
        this.methodId = this@HttpCall.methodId
    }

    fun request(): Request? = call?.request()

    fun timeout(): Timeout? = call?.timeout()
}