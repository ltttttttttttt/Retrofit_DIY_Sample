package com.lt.retrofitdemo.source

import com.lt.retrofitdemo.http.ObserverCallBack
import java.lang.reflect.Proxy

/**
 * creator: lt  2020/2/27    lt.dygzs@qq.com
 * effect : 网络请求方法封装,使用动态代理和kt反射,比retrofit使用更方便
 * warning: 1.网络请求方法只需要在接口内注册即可,其他自动处理
 *          2.需要在方法上加上get或post注解,value为后半段url
 *          3.方法的参数名就是请求用的参数名
 *          4.在companion object内有Int类型的id,规则为方法名的大写,如果是驼峰大写,前面就加一个_
 *
 *          5.可以使用suspend函数配合协程达到快捷调用的目的,需要加上返回值,如果请求成功就会直接返回,如果失败会走回调(不会使用软引用),暂时取消方法找不到能用的
 */
interface HttpFunctions {

    @POST("2")
    fun get1(
        page: Int,
        q: String,
        _callback: ObserverCallBack?,
        channel: Int = 0
    )

    @POST("1")
    fun toPassword(
            title: String,
            url: String,
            photo_url: String,
            channel: Int = 0,
            _callback: ObserverCallBack? = null
    ): HttpCall<String>


    companion object {
        val GET_1 = autoCreateKey()


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