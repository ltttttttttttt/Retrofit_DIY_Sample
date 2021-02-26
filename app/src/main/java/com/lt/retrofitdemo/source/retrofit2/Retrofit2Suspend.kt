package com.lt.retrofitdemo.source.retrofit2

/**
 * creator: lt  2020/6/5  lt.dygzs@qq.com
 * effect : retrofit2协程解决方案修改版
 * warning: 参考:https://github.com/square/retrofit/blob/master/retrofit/src/main/java/retrofit2/KotlinExtensions.kt
 */

import com.lt.retrofitdemo.source.HttpCall

//suspend fun <T : Any> HttpCall<T>.await(): T {
//    return suspendCancellableCoroutine { continuation ->
//        continuation.invokeOnCancellation {
//            cancel()
//        }
//        enqueue(object : Callback<T> {
//            override fun onResponse(call: HttpCall<T>, response: Response<T>) {
//                if (response.isSuccessful) {
//                    val body = response.body()
//                    if (body != null) {//对象为空的话不响应
//                        continuation.resume(body)
//                    } else {
//                        "Retrofit2Suspend.onResponse 32 : 数据为空".w()
//                        continuation.cancel()
//                    }
//                } else {
//                    "Retrofit2Suspend.onResponse 31 : code:${response.code()},message:${response.message()}".e2()
//                    HandlerPool.post {
//                        callback?.handleResult("", AsyncHttpRequest.FAILURE_HTTP, methodId)
//                    }
//                    continuation.cancel()
//                }
//            }
//
//            override fun onFailure(call: HttpCall<T>, t: Throwable) {
//                "Retrofit2Suspend.onFailure 39 : ${t.message}".e2()
//                HandlerPool.post {
//                    callback?.handleResult("", AsyncHttpRequest.FAILURE_HTTP, methodId)
//                }
//                continuation.cancel()
//            }
//        })
//    }
//}