package com.lt.retrofitdemo.http

import java.lang.reflect.Proxy

/**
 * creator: lt  2020/3/17--14:45    lt.dygzs@qq.com
 * effect :
 * warning:
 */
interface HttpFunctions {
    /**
     * 获取玩安卓的json数据
     * @param cid 这个接口的参数(虽然不知道有什么用emmm)
     */
    @GET("article/list/0/json?", isEncryption = false)
    fun getJson(
        _callback: ObserverCallBack?,
        cid: String
    )

    companion object {
        val LOGIN = autoCreateKey()

        private var autoKey = 0
        //生成自增的key,可以用type来进行特殊判断
        private fun autoCreateKey(type: Int = 1): Int = type * 100000 + ++autoKey

        /**
         * 动态代理单例对象
         */
        val instance: HttpFunctions = getHttpFunctions()

        //获取动态代理实例对象
        private fun getHttpFunctions(): HttpFunctions {
            //这样可以不使用实现类就实现动态代理,主要是1,2两个参数的区别
            val clazz = HttpFunctions::class.java
            return Proxy.newProxyInstance(
                clazz.classLoader,
                arrayOf(clazz),
                HttpFunctionsHandler()
            ) as HttpFunctions
        }
    }
}