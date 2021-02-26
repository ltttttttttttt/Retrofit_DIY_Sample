package com.lt.retrofitdemo.source

import android.os.Handler
import android.os.Looper
import android.os.Message
import com.lt.retrofitdemo.http.ObserverCallBack
import com.lt.retrofitdemo.source.AsyncHttpRequest.FAILURE_HTTP
import com.zhy.http.okhttp.OkHttpUtils
import com.zhy.http.okhttp.builder.OkHttpRequestBuilder
import com.zhy.http.okhttp.callback.FileCallBack
import com.zhy.http.okhttp.callback.StringCallback
import com.zhy.http.okhttp.request.RequestCall
import okhttp3.MediaType
import org.json.JSONObject
import java.io.File


/**
 * creator: lt  2020/1/30--14:29    lt.dygzs@qq.com
 * effect : 异步数据请求,kt协程封装
 * warning: 参考   https://github.com/hongyangAndroid/okhttputils
 */
object AsyncHttpRequest2 {
    const val isUsePostJson = false//使用post json的方式访问

    /**
     * 加密方式,注意:这里的map用了同一个对象
     */
    private fun encryption(map: MutableMap<String, String>): MutableMap<String, String> {
        return mutableMapOf()
    }

    /**
     * 解密
     */
    private fun decrypt(s: String): String = ""

    /**
     * 检查请求头
     */
    private fun getHeaders(methodKey: Int): MutableMap<String, String>? {
        // TODO by lt 2020/2/4 13:58 在此处设置请求头
        return null
    }

    private val handler by lazy {
        Handler(Looper.getMainLooper()) {
            val objs = it.obj as Array<Any?>
            when (it.arg1) {
                AsyncHttpRequest.GET -> {
                    startAsyncHttpGet(
                            objs[0].toString(),
                            objs[1] as Int,
                            objs[2] as? ObserverCallBack,
                            objs[3] as? String,
                            objs[4] as? MutableMap<String, String>,
                            objs[5],
                            objs[6] as Boolean
                    )
                }
                AsyncHttpRequest.POST -> {
                    startAsyncHttpPost(
                            objs[0].toString(),
                            objs[1] as Int,
                            objs[2] as? ObserverCallBack,
                            objs[3] as? String,
                            objs[4] as? MutableMap<String, String>,
                            objs[5],
                            objs[6] as? MutableList<String>,
                            objs[7] as? MutableList<String>,
                            objs[8] as Boolean
                    )
                }
                else -> throw IllegalStateException("handler 没有选择请求方式")
            }
            return@Handler true
        }
    }

    /***
     * 同步get请求方法,需要在子线程调用
     *
     * @param url                   请求地址
     * @param methodKey             请求的方法对应的int值（整个项目中唯一不重复的）
     * @param callBack              异步回调,只回调失败
     * @param map                   请求参数集合
     * @param tag                   取消用的tag,OkHttpUtils.cancelTag(this);可以取消请求
     * @param isEncryption          是否加密
     */
    fun httpGet(url: String,
                methodKey: Int,
                callBack: ObserverCallBack?,
                map: MutableMap<String, String>? = null,
                tag: Any? = null,
                isEncryption: Boolean = true): String {
        if (checkNetwork(callBack, methodKey, false)) return ""
        try {
            val json = decrypt(getHttpJson(url, map, methodKey, isEncryption, tag))
            val jsonObject = JSONObject(json)
            if (InitJson.jsonIsSuccess(jsonObject))
                return jsonObject.optString(InitJson.DATA)
            else
//                post {
                    callBack?.handleResult(json, FAILURE_HTTP, methodKey)
//                }
        } catch (e: Exception) {
//            post {
                callBack?.handleResult(e.toString(), FAILURE_HTTP, methodKey)
//            }
        }
        return ""
    }

    /***
     * 同步post请求方法,需要在子线程调用
     *
     * @param url                   请求地址
     * @param methodKey             请求的方法对应的int值（整个项目中唯一不重复的）
     * @param callBack              异步回调,只回调失败
     * @param map                   请求参数集合
     * @param tag                   取消用的tag,OkHttpUtils.cancelTag(this);可以取消请求
     * @param files                 上传的文件集合
     * @param fileNames             上传的文件名字集合,如果不传则使用默认的
     * @param isEncryption          是否加密
     */
    fun httpPost(url: String,
                 methodKey: Int,
                 callBack: ObserverCallBack?,
                 map: MutableMap<String, String>? = null,
                 tag: Any? = null,
                 files: MutableList<String>? = null,
                 fileNames: MutableList<String>? = null,
                 isEncryption: Boolean = true): String {
        if (checkNetwork(callBack, methodKey, false)) return ""
        try {
            val json = decrypt(postHttpJson(url, map, methodKey, isEncryption, tag, files, fileNames))
            val jsonObject = JSONObject(json)
            if (InitJson.jsonIsSuccess(jsonObject))
                return jsonObject.optString(InitJson.DATA)
            else
//                post {
                    callBack?.handleResult(json, FAILURE_HTTP, methodKey)
//                }
        } catch (e: Exception) {
//            post {
                callBack?.handleResult(e.toString(), FAILURE_HTTP, methodKey)
//            }
        }
        return ""
    }

    /***
     * 异步get请求方法
     *
     * @param url                   请求地址
     * @param methodKey             请求的方法对应的int值（整个项目中唯一不重复的）
     * @param callBack              异步回调
     * @param map                   请求参数集合
     * @param tag                   取消用的tag,OkHttpUtils.cancelTag(this);可以取消请求
     * @param isWeakReference       回调是否使用软引用
     * @param isEncryption          是否加密
     */
    fun asyncHttpGet(url: String,
                     methodKey: Int,
                     callBack: ObserverCallBack?,
                     map: MutableMap<String, String>? = null,
                     tag: Any? = null,
                     isWeakReference: Boolean = true,
                     isEncryption: Boolean = true) {
        if (checkNetwork(callBack, methodKey)) return
        val obtain = Message.obtain()
        obtain.arg1 = AsyncHttpRequest.GET
        obtain.obj = arrayOf(url,
                methodKey,
                if (isWeakReference) null else callBack,
                if (isWeakReference) CallBackTask.instance.add(callBack) else null,
                map,
                tag,
                isEncryption
        )
        handler.sendMessage(obtain)
    }

    /***
     * post请求方法
     *
     * @param url                   请求地址
     * @param methodKey             请求的方法对应的int值（整个项目中唯一不重复的）
     * @param callBack              异步回调
     * @param map                   请求参数集合
     * @param tag                   取消用的tag,OkHttpUtils.cancelTag(this);可以取消请求
     * @param files                 上传的文件集合
     * @param fileNames             上传的文件名字集合,如果不传则使用默认的
     * @param isWeakReference       回调是否使用软引用
     * @param isEncryption          是否加密
     */
    fun asyncHttpPost(
            url: String,
            methodKey: Int,
            callBack: ObserverCallBack?,
            map: MutableMap<String, String>? = null,
            tag: Any? = null,
            files: MutableList<String>? = null,
            fileNames: MutableList<String>? = null,
            isWeakReference: Boolean = true,
            isEncryption: Boolean = true
    ) {
        if (checkNetwork(callBack, methodKey)) return
        val obtain = Message.obtain()
        obtain.arg1 = AsyncHttpRequest.POST
        obtain.obj = arrayOf(url,
                methodKey,
                if (isWeakReference) null else callBack,
                if (isWeakReference) CallBackTask.instance.add(callBack) else null,
                map,
                tag,
                files,
                fileNames,
                isEncryption
        )
        handler.sendMessage(obtain)
    }

    /**
     * 下载文件,没有做解密
     *
     * @param url                   请求地址
     * @param callBack              异步回调,重写inProgress()可以监听进度
     * @param map                   请求参数集合
     * @param tag                   取消用的tag,OkHttpUtils.cancelTag(this);可以取消请求
     * @param sendType              请求类型：get和post
     * @param isEncryption          是否加密
     */
    fun download(
        url: String,
        callBack: FileCallBack?,
        map: MutableMap<String, String>? = null,
        tag: Any? = null,
        sendType: Int = AsyncHttpRequest.GET,
        isEncryption: Boolean = true
    ) {
        if (checkNetwork()) return
        val newMap = if (isEncryption && map != null) encryption(map) else map
        val okHttpUtils: OkHttpRequestBuilder<*> = if (sendType == AsyncHttpRequest.GET) {
            OkHttpUtils.get().params(newMap)
        } else {
            OkHttpUtils.post().params(newMap)
        }
        okHttpUtils.url(url)
                .tag(tag)
                .headers(getHeaders(0))
                .build()
                .connTimeOut(35000L)
                .readTimeOut(35000L)
                .writeTimeOut(35000L)
                .execute(callBack)
    }

    /**
     * 上传,没有做解密
     *
     * @param url                   请求地址
     * @param callBack              异步回调,重写inProgress()可以监听进度
     * @param map                   请求参数集合
     * @param tag                   取消用的tag,OkHttpUtils.cancelTag(this);可以取消请求
     * @param sendType              请求类型：get和post
     * @param isEncryption          是否加密
     */
    fun upload(
        url: String,
        callBack: StringCallback?,
        map: MutableMap<String, String>? = null,
        tag: Any? = null,
        sendType: Int = AsyncHttpRequest.POST,
        isEncryption: Boolean = true
    ) {
        if (checkNetwork()) return
        val newMap = if (isEncryption && map != null) encryption(map) else map
        val okHttpUtils: OkHttpRequestBuilder<*> = if (sendType == AsyncHttpRequest.GET) {
            OkHttpUtils.get().params(newMap)
        } else {
            OkHttpUtils.post().params(newMap)
        }
        okHttpUtils.url(url)
                .tag(tag)
                .headers(getHeaders(0))
                .build()
                .connTimeOut(35000L)
                .readTimeOut(35000L)
                .writeTimeOut(35000L)
                .execute(callBack)
    }

    /**
     * 开始get请求
     */
    private fun startAsyncHttpGet(url: String,
                                  methodKey: Int,
                                  callBack: ObserverCallBack?,
                                  callBackKey: String?,
                                  map: MutableMap<String, String>?,
                                  tag: Any?,
                                  isEncryption: Boolean) {
//        launchIO {
            try {
                val stringBody = getHttpJson(url, map, methodKey, isEncryption, tag)
                val observerCallBack = callBack ?: CallBackTask.instance.justGet(callBackKey)
                if (observerCallBack != null) {
                    val data = if (isEncryption) decrypt(stringBody) else stringBody
//                    launchMain {
//                        ExceptionUtil.releaseTryException({
//                            observerCallBack.handleResult(data, SUCCESS_HTTP, methodKey)
//                        }, {
//                            R.string.service_busy.showToast()
//                        })
//                        CallBackTask.instance.remove(callBackKey)
//                    }
                }
            } catch (e: Exception) {
                val observerCallBack = callBack ?: CallBackTask.instance.justGet(callBackKey)
                if (observerCallBack != null) {
//                    launchMain {
//                        ExceptionUtil.releaseTryException({
//                            observerCallBack.handleResult(e.toString(), FAILURE_HTTP, methodKey)
//                        }, {
//                            R.string.service_busy.showToast()
//                        })
//                        CallBackTask.instance.remove(callBackKey)
//                    }
                }
            }
//        }
    }

    //同步获取json数据,需要catch异常
    private fun getHttpJson(url: String, map: MutableMap<String, String>?, methodKey: Int, isEncryption: Boolean, tag: Any?): String {
//        "get请求== $url${
//            kotlin.run {
//                val sb = StringBuilder("?")
//                map?.forEach {
//                    sb.append(it.key)
//                            .append('=')
//                            .append(it.value)
//                            .append('&')
//                }
//                sb.deleteCharAt(sb.length - 1)
//                sb.toString()
//            }
//        }".w2("$methodKey http")
        val newMap = if (isEncryption && map != null) {
            val encryption = encryption(map)
//            "参数加密后== ${
//                kotlin.run {
//                    val sb = StringBuilder()
//                    map.forEach {
//                        sb.append(it.key)
//                                .append('=')
//                                .append(it.value)
//                                .append('&')
//                    }
//                    if (sb.isNotEmpty())
//                        sb.deleteCharAt(sb.length - 1)
//                    sb.toString()
//                }
//            }".w2("$methodKey http")
            encryption
        } else
            map
        val builder = OkHttpUtils.get()
                .url(url)
                .tag(tag)
                .headers(getHeaders(methodKey))
                .params(newMap)
        val stringBody = builder.build()
                .connTimeOut(20000L)
                .readTimeOut(20000L)
                .writeTimeOut(20000L)
                .execute().body()!!.string()
        return stringBody
    }

    /**
     * post请求
     */
    private fun startAsyncHttpPost(
            url: String,
            methodKey: Int,
            callBack: ObserverCallBack?,
            callBackKey: String?,
            map: MutableMap<String, String>?,
            tag: Any?,
            files: MutableList<String>?,
            fileNames: MutableList<String>?,
            isEncryption: Boolean
    ) {
//        launchIO {
//            try {
//                val stringBody = postHttpJson(url, map, methodKey, isEncryption, tag, files, fileNames)
//                val observerCallBack = callBack ?: CallBackTask.instance.justGet(callBackKey)
//                if (observerCallBack != null) {
//                    val data = if (isEncryption) decrypt(stringBody) else stringBody
//                    launchMain {
//                        ExceptionUtil.releaseTryException({
//                            observerCallBack.handleResult(data, SUCCESS_HTTP, methodKey)
//                        }, {
//                            R.string.service_busy.showToast()
//                        })
//                        CallBackTask.instance.remove(callBackKey)
//                    }
//                }
//            } catch (e: Exception) {
//                val observerCallBack = callBack ?: CallBackTask.instance.justGet(callBackKey)
//                if (observerCallBack != null) {
//                    launchMain {
//                        ExceptionUtil.releaseTryException({
//                            observerCallBack.handleResult(e.toString(), FAILURE_HTTP, methodKey)
//                        }, {
//                            R.string.service_busy.showToast()
//                        })
//                        CallBackTask.instance.remove(callBackKey)
//                    }
//                }
//            }
//        }
    }

    //同步获取json数据,需要catch异常
    private fun postHttpJson(url: String, map: MutableMap<String, String>?, methodKey: Int, isEncryption: Boolean, tag: Any?, files: MutableList<String>?, fileNames: MutableList<String>?): String {
//        "post请求== $url $map".w2("$methodKey httpUrl")
        val newMap = if (isEncryption && map != null) {
            val encryption = encryption(map)
//            "参数加密后== $map".w2("$methodKey http")
            encryption
        } else
            map
//        val stringBody = if (!isUsePostJson) {
//            val builder = OkHttpUtils
//                    .post()
//                    .url(url)
//                    .tag(tag)
//                    .params(newMap)
//                    .headers(getHeaders(methodKey))
//            if (files.nullSize != 0) {
//                val activity = AppManager.currentActivity()
//                files?.forEachIndexed { index, s ->
//                    val file = try {
//                        if (activity == null) {
//                            File(s)
//                        } else {
//                            val file = File(ImageCompress.start(activity, s))
//                            "附加压缩图片$index: ${file.absolutePath}  ,size=${file.length()}".w2("$methodKey http")
//                            file
//                        }
//                    } catch (e: Exception) {
//                        val file = File(s)
//                        "附加文件$index: ${file.absolutePath}  ,size=${file.length()}".w2("$methodKey http")
//                        file
//                    }
//                    val name = if (fileNames.nullSize > index) fileNames!![index] else "file"
//                    builder.addFile(name, file.name, file)
//                }
//                builder.build()
//                        .connTimeOut(35000L)
//                        .readTimeOut(35000L)
//                        .writeTimeOut(35000L)
//            } else {
//                builder.build()
//                        .connTimeOut(20000L)
//                        .readTimeOut(20000L)
//                        .writeTimeOut(20000L)
//            }
//        } else {
//            OkHttpUtils
//                    .postString()
//                    .url(url)
//                    .tag(tag)
//                    .content(map.toJson())
//                    .headers(getHeaders(methodKey))
//                    .mediaType(MediaType.parse("application/json; charset=utf-8"))
//                    .build()
//        }.execute().body()!!.string()
//        return stringBody
        return ""
    }

    //检查网络是否可用[isMainThread]表示是在主线程调用的
    private fun checkNetwork(callBack: ObserverCallBack? = null, methodKey: Int = 0, isMainThread: Boolean = true): Boolean {
//        if (!NetUtil.isNetworkAvailable(App.getInstance().applicationContext)) {
//            if (callBack != null) {
//                if (isMainThread)
//                    callBack.handleResult(R.string.netnotenble.toText(),
//                        AsyncHttpRequest.FAILURE_NETWORK, methodKey)
//                else
//                    HandlerPool.post {
//                        callBack.handleResult(R.string.netnotenble.toText(),
//                            AsyncHttpRequest.FAILURE_NETWORK, methodKey)
//                    }
//            } else
//                R.string.netnotenble.showToast()
//            return true
//        }
        return false
    }

    /**
     * 获取对应的Response,可以获取okHttp3的call
     */
    fun getResponse(url: String, map: MutableMap<String, String>?, methodKey: Int, isEncryption: Boolean, tag: Any?): RequestCall {
//        "get请求== $url${
//            kotlin.run {
//                val sb = StringBuilder("?")
//                map?.forEach {
//                    sb.append(it.key)
//                            .append('=')
//                            .append(it.value)
//                            .append('&')
//                }
//                sb.deleteCharAt(sb.length - 1)
//                sb.toString()
//            }
//        }".w2("$methodKey http")
        val newMap = if (isEncryption && map != null) {
            val encryption = encryption(map)
//            "参数加密后== ${
//                kotlin.run {
//                    val sb = StringBuilder()
//                    map.forEach {
//                        sb.append(it.key)
//                                .append('=')
//                                .append(it.value)
//                                .append('&')
//                    }
//                    if (sb.isNotEmpty())
//                        sb.deleteCharAt(sb.length - 1)
//                    sb.toString()
//                }
//            }".w2("$methodKey http")
            encryption
        } else
            map
        val builder = OkHttpUtils.get()
                .url(url)
                .tag(tag)
                .headers(getHeaders(methodKey))
                .params(newMap)
        return builder.build()
                .connTimeOut(20000L)
                .readTimeOut(20000L)
                .writeTimeOut(20000L)
    }

    /**
     * 获取对应的Response,可以获取okHttp3的call
     */
    fun postResponse(url: String, map: MutableMap<String, String>?, methodKey: Int, isEncryption: Boolean, tag: Any?, files: MutableList<String>?, fileNames: MutableList<String>?): RequestCall {
//        "post请求== $url $map".w2("$methodKey httpUrl")
        val newMap = if (isEncryption && map != null) {
            val encryption = encryption(map)
//            "参数加密后== $map".w2("$methodKey http")
            encryption
        } else
            map
        return null!!
//        return if (!isUsePostJson) {
//            val builder = OkHttpUtils
//                    .post()
//                    .url(url)
//                    .tag(tag)
//                    .params(newMap)
//                    .headers(getHeaders(methodKey))
//            if (files.nullSize != 0) {
//                val activity = AppManager.currentActivity()
//                files?.forEachIndexed { index, s ->
//                    val file = try {
//                        if (activity == null) {
//                            File(s)
//                        } else {
//                            val file = File(ImageCompress.start(activity, s))
//                            "附加压缩图片$index: ${file.absolutePath}  ,size=${file.length()}".w2("$methodKey http")
//                            file
//                        }
//                    } catch (e: Exception) {
//                        val file = File(s)
//                        "附加文件$index: ${file.absolutePath}  ,size=${file.length()}".w2("$methodKey http")
//                        file
//                    }
//                    val name = if (fileNames.nullSize > index) fileNames!![index] else "file"
//                    builder.addFile(name, file.name, file)
//                }
//                builder.build()
//                        .connTimeOut(35000L)
//                        .readTimeOut(35000L)
//                        .writeTimeOut(35000L)
//            } else {
//                builder.build()
//                        .connTimeOut(20000L)
//                        .readTimeOut(20000L)
//                        .writeTimeOut(20000L)
//            }
//        } else {
//            OkHttpUtils
//                    .postString()
//                    .url(url)
//                    .tag(tag)
//                    .content(map.toJson())
//                    .headers(getHeaders(methodKey))
//                    .mediaType(MediaType.parse("application/json; charset=utf-8"))
//                    .build()
//        }
    }
}