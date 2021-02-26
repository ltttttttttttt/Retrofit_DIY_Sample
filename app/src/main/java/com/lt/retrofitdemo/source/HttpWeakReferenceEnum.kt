package com.lt.retrofitdemo.source

/**
 * creator: lt  2020/2/28  lt.dygzs@qq.com
 * effect : 表示网络请求引用方式
 * warning:
 */
enum class HttpWeakReferenceEnum {
    /**
     * 自动选择方式:使用BaseHttp用弱引用,ObserverCallBack用强引用
     */
    AUTO,

    /**
     * 不论如何都用弱引用
     */
    WEAK,

    /**
     * 不论如何都用强引用
     */
    STRONG
}