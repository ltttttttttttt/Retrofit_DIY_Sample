package com.lt.retrofitdemo.source

/**
 * creator: lt  2020/2/28  lt.dygzs@qq.com
 * effect : post请求
 *
 * @param url               请求链接,http://或https://开头就使用全链接,否则自动拼接
 * @param isEncryption      是否加密
 * @param isWeakReference   网络请求的引用方式
 * @param callbackName      回调的参数名
 * @param tagName           用于取消请求的tag的参数名
 * @param filesName         上传的文件集合的参数名
 * @param fileNamesName     上传的文件名字集合,如果不传则使用默认的的参数名
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class POST(
        val url: String,
        val isEncryption: Boolean = true,
        val isWeakReference: HttpWeakReferenceEnum = HttpWeakReferenceEnum.AUTO,
        val callbackName: String = "_callback",
        val tagName: String = "_callback",
        val filesName: String = "_files",
        val fileNamesName: String = "_fileNames"
)