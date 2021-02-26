package com.lt.retrofitdemo.source

import com.alibaba.fastjson.JSONObject
import com.lt.retrofitdemo.http.ObserverCallBack
import com.lt.retrofitdemo.source.AsyncHttpRequest2
import com.lt.retrofitdemo.source.HttpFunctions
import okhttp3.Call
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.jvm.javaType
import kotlin.reflect.jvm.kotlinFunction

/**
 * creator: lt  2020/2/28  lt.dygzs@qq.com
 * effect : 动态代理处理类
 * warning: 最低版本26以后,可以直接用java的反射机制来获取名称,并且去掉kt的元数据注解
 */
class HttpFunctionsHandler : InvocationHandler {

    /**
     * 处理动态代理
     */
    override fun invoke(proxy: Any, method: Method, args: Array<out Any?>?): Any? {
        if (method.declaringClass == Any::class.java) {
            //处理Object类的方法
            return (if (args == null) method.invoke(this) else method.invoke(this, *args))
        }
        if (method.returnType == HttpCall::class.java) {
            return HttpCall<Any>(method, args)
        }
        if (!args.isNullOrEmpty()) {
            val last = args.last()
            if (last != null && last is Continuation<*>) {
//                launchIO {
//                    val kFunction = method.kotlinFunction ?: return@launchIO
//                    val annotation = method.getAnnotation(POST::class.java)
//                            ?: method.getAnnotation(GET::class.java)
//                            ?: throw RuntimeException("${method.name} from of HttpFunctions not add annotation ,or added error annotation.")
//                    val any = when (annotation) {
//                        is POST -> postMethod(kFunction, annotation, args)
//                        is GET -> getMethod(kFunction, annotation, args)
//                        else -> throw RuntimeException("${method.name} from of HttpFunctions not add annotation ,or added error annotation.")
//                    } ?: cancelAndThrow()
//                    (last as Continuation<Any>).resume(any)
//                }
                return kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED
            }
        }
//        return ThreadPool.submitToCacheThreadPool {
//            val kFunction = method.kotlinFunction ?: return@submitToCacheThreadPool
//            val annotation = method.getAnnotation(POST::class.java)
//                    ?: method.getAnnotation(GET::class.java)
//                    ?: throw RuntimeException("${method.name} from of HttpFunctions not add annotation ,or added error annotation.")
//            when (annotation) {
//                is POST -> asyncPostMethod(kFunction, annotation, args)
//                is GET -> asyncGetMethod(kFunction, annotation, args)
//            }
//        }
        return null
    }

    companion object {
        val isUseMethodId = true//是否使用方法id来判断

        //同步get call
        fun getCall(method: KFunction<*>, annotation: GET, args: Array<out Any?>?): Pair<Call, ObserverCallBack?> {
            var callback: ObserverCallBack? = null
            var tag: Any? = null
            var dataMap: MutableMap<String, String>? = null
            //获取各种参数
            method.parameters
                    .forEachIndexed { index: Int, kParameter: KParameter ->
                        if (index == 0)
                            return@forEachIndexed
                        when (kParameter.name) {
                            annotation.callbackName -> {
                                callback = args?.get(index - 1) as? ObserverCallBack
                                if (annotation.callbackName == annotation.tagName)
                                    tag = callback
                            }
                            annotation.tagName -> {
                                tag = args?.get(index - 1)
                            }
                            else -> {
                                kParameter.name?.let {
                                    if (dataMap == null)
                                        dataMap = mutableMapOf()
                                    dataMap!![it] = args?.get(index - 1)?.toString() ?: ""
                                }
                            }
                        }
                    }

            return AsyncHttpRequest2.getResponse(checkUrl(annotation.url),
                    dataMap,
                    getMethodId(method),
                    annotation.isEncryption,
                    tag).buildCall(null) to callback
        }

        //同步get请求
        fun getMethod(method: KFunction<*>, annotation: GET, args: Array<out Any?>?): Any? {
            var callback: ObserverCallBack? = null
            var tag: Any? = null
            var dataMap: MutableMap<String, String>? = null
            //获取各种参数
            method.parameters
                    .forEachIndexed { index: Int, kParameter: KParameter ->
                        if (index == 0)
                            return@forEachIndexed
                        when (kParameter.name) {
                            annotation.callbackName -> {
                                callback = args?.get(index - 1) as? ObserverCallBack
                                if (annotation.callbackName == annotation.tagName)
                                    tag = callback
                            }
                            annotation.tagName -> {
                                tag = args?.get(index - 1)
                            }
                            else -> {
                                kParameter.name?.let {
                                    if (dataMap == null)
                                        dataMap = mutableMapOf()
                                    dataMap!![it] = args?.get(index - 1)?.toString() ?: ""
                                }
                            }
                        }
                    }

            val data = AsyncHttpRequest2.httpGet(checkUrl(annotation.url),
                    getMethodId(method),
                    callback,
                    dataMap,
                    tag,
                    annotation.isEncryption)
            return JSONObject.parseObject(data, method.returnType.javaType)
        }

        //同步post call
        fun postCall(method: KFunction<*>, annotation: POST, args: Array<out Any?>?): Pair<Call, ObserverCallBack?> {
            var callback: ObserverCallBack? = null
            var tag: Any? = null
            var files: MutableList<String>? = null
            var fileNames: MutableList<String>? = null
            var dataMap: MutableMap<String, String>? = null
            //获取各种参数
            method.parameters
                    .forEachIndexed { index: Int, kParameter: KParameter ->
                        if (index == 0)
                            return@forEachIndexed
                        when (kParameter.name) {
                            annotation.callbackName -> {
                                callback = args?.get(index - 1) as? ObserverCallBack
                                if (annotation.callbackName == annotation.tagName)
                                    tag = callback
                            }
                            annotation.tagName -> {
                                tag = args?.get(index - 1)
                            }
                            annotation.filesName -> {
                                files = args?.get(index - 1) as? MutableList<String>
                            }
                            annotation.fileNamesName -> {
                                fileNames = args?.get(index - 1) as? MutableList<String>
                            }
                            else -> {
                                kParameter.name?.let {
                                    if (dataMap == null)
                                        dataMap = mutableMapOf()
                                    dataMap!![it] = args?.get(index - 1)?.toString() ?: ""
                                }
                            }
                        }
                    }

            return AsyncHttpRequest2.postResponse(checkUrl(annotation.url),
                    dataMap,
                    getMethodId(method),
                    annotation.isEncryption,
                    tag,
                    files,
                    fileNames
            ).buildCall(null) to callback
        }

        //同步post请求
        fun postMethod(method: KFunction<*>, annotation: POST, args: Array<out Any?>?): Any? {
            var callback: ObserverCallBack? = null
            var tag: Any? = null
            var files: MutableList<String>? = null
            var fileNames: MutableList<String>? = null
            var dataMap: MutableMap<String, String>? = null
            //获取各种参数
            method.parameters
                    .forEachIndexed { index: Int, kParameter: KParameter ->
                        if (index == 0)
                            return@forEachIndexed
                        when (kParameter.name) {
                            annotation.callbackName -> {
                                callback = args?.get(index - 1) as? ObserverCallBack
                                if (annotation.callbackName == annotation.tagName)
                                    tag = callback
                            }
                            annotation.tagName -> {
                                tag = args?.get(index - 1)
                            }
                            annotation.filesName -> {
                                files = args?.get(index - 1) as? MutableList<String>
                            }
                            annotation.fileNamesName -> {
                                fileNames = args?.get(index - 1) as? MutableList<String>
                            }
                            else -> {
                                kParameter.name?.let {
                                    if (dataMap == null)
                                        dataMap = mutableMapOf()
                                    dataMap!![it] = args?.get(index - 1)?.toString() ?: ""
                                }
                            }
                        }
                    }

            val data = AsyncHttpRequest2.httpPost(checkUrl(annotation.url),
                    getMethodId(method),
                    callback,
                    dataMap,
                    tag,
                    files,
                    fileNames,
                    annotation.isEncryption
            )
            return JSONObject.parseObject(data, method.returnType.javaType)
        }

        //处理异步get请求方法
        fun asyncGetMethod(method: KFunction<*>, annotation: GET, args: Array<out Any?>?) {
            var callback: ObserverCallBack? = null
            var tag: Any? = null
            var dataMap: MutableMap<String, String>? = null
            //获取各种参数
            method.parameters
                    .forEachIndexed { index: Int, kParameter: KParameter ->
                        if (index == 0)
                            return@forEachIndexed
                        when (kParameter.name) {
                            annotation.callbackName -> {
                                callback = args?.get(index - 1) as? ObserverCallBack
                                if (annotation.callbackName == annotation.tagName)
                                    tag = callback
                            }
                            annotation.tagName -> {
                                tag = args?.get(index - 1)
                            }
                            else -> {
                                kParameter.name?.let {
                                    if (dataMap == null)
                                        dataMap = mutableMapOf()
                                    dataMap!![it] = args?.get(index - 1)?.toString() ?: ""
                                }
                            }
                        }
                    }

            AsyncHttpRequest2.asyncHttpGet(checkUrl(annotation.url),
                    getMethodId(method),
                    callback,
                    dataMap,
                    tag,
                    checkUseWeakReference(annotation.isWeakReference, callback),
                    annotation.isEncryption
            )
        }

        //处理异步post请求方法
        fun asyncPostMethod(method: KFunction<*>, annotation: POST, args: Array<out Any?>?) {
            var callback: ObserverCallBack? = null
            var tag: Any? = null
            var files: MutableList<String>? = null
            var fileNames: MutableList<String>? = null
            var dataMap: MutableMap<String, String>? = null
            //获取各种参数
            method.parameters
                    .forEachIndexed { index: Int, kParameter: KParameter ->
                        if (index == 0)
                            return@forEachIndexed
                        when (kParameter.name) {
                            annotation.callbackName -> {
                                callback = args?.get(index - 1) as? ObserverCallBack
                                if (annotation.callbackName == annotation.tagName)
                                    tag = callback
                            }
                            annotation.tagName -> {
                                tag = args?.get(index - 1)
                            }
                            annotation.filesName -> {
                                files = args?.get(index - 1) as? MutableList<String>
                            }
                            annotation.fileNamesName -> {
                                fileNames = args?.get(index - 1) as? MutableList<String>
                            }
                            else -> {
                                kParameter.name?.let {
                                    if (dataMap == null)
                                        dataMap = mutableMapOf()
                                    dataMap!![it] = args?.get(index - 1)?.toString() ?: ""
                                }
                            }
                        }
                    }

            AsyncHttpRequest2.asyncHttpPost(checkUrl(annotation.url),
                    getMethodId(method),
                    callback,
                    dataMap,
                    tag,
                    files,
                    fileNames,
                    checkUseWeakReference(annotation.isWeakReference, callback),
                    annotation.isEncryption
            )
        }

        //检查是否需要使用软引用
        fun checkUseWeakReference(isWeakReference: HttpWeakReferenceEnum, callback: ObserverCallBack?): Boolean {
//            return when (isWeakReference) {
//                HttpWeakReferenceEnum.AUTO -> callback is BaseHttp
//                HttpWeakReferenceEnum.WEAK -> true
//                HttpWeakReferenceEnum.STRONG -> false
//            }
            return false
        }

        //检查是否是完整链接,并返回完整链接
        fun checkUrl(url: String): String {
            if (url.startsWith("http") && (url.startsWith("http://") || url.startsWith("https://"))) {
                return url
            }
//            return HttpConfig.ROOT_URL.toString() + url
            return ""
        }

        fun getMethodId(method: KFunction<*>): Int =
                try {
                    if (isUseMethodId)
                        functionsClazz.getMethod(method.getUpperName()).invoke(HttpFunctions) as Int
                    else
                        0
                } catch (e: Exception) {
                    0
                }

        //存放方法id的类的class
        val functionsClazz = HttpFunctions.Companion::class.java

        //通过方法名获取id名
        fun KFunction<*>.getUpperName(): String =
                StringBuilder("get").apply {
                    name.forEach {
                        if (it.isUpperCase())
                            append('_').append(it)
                        else
                            append(it.toUpperCase())
                    }
                }.toString()
    }
}
