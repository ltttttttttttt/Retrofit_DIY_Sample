package com.lt.retrofitdemo.http

/**
 * creator: lt  2020/3/26  lt.dygzs@qq.com
 *
 * get请求
 * @param url               请求链接
 * @param isEncryption      是否加密
 * @param callbackName      回调的参数名
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class GET(
    val url: String,
    val isEncryption: Boolean = true,
    val callbackName: String = "_callback"
)