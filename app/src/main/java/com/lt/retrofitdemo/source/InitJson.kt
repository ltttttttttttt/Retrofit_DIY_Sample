package com.lt.retrofitdemo.source

import org.json.JSONObject

/**
 * 创    建:  lt  2017/3/26--16:38
 * 作    用:  解析json
 * 注意事项:
 */

object InitJson {

    const val CODE = "code"//判断的code
    const val CODE_OK = "200"//成功的code
    const val MSG = "msg"//消息字段
    const val DATA = "data"//data字段

//    @JvmStatic
//    fun initJson(baseHttp: BaseHttp, data: String?, encoding: Int, method: Int) {
//        val (encoding, data, msg, method) = analysisJson(data, encoding, method)
//        when (encoding) {
//            AsyncHttpRequest.SUCCESS_HTTP ->
//                baseHttp.onHttpSuccess(
//                        data as? String ?: "",
//                        msg as? String ?: "",
//                        method as Int)
//            AsyncHttpRequest.FAILURE_HTTP ->
//                baseHttp.onHttpFailed(data as? String
//                        ?: "", msg as? String ?: "", method as Int)
//            AsyncHttpRequest.FAILURE_NETWORK ->
//                baseHttp.onHttpFailed(data as? String
//                        ?: "", msg as? String ?: "", method as Int)
//        }
//    }

    //解析json
    fun analysisJson(data: String?, encoding: Int, method: Int): Array<Any?> {
//        return when (encoding) {
//            AsyncHttpRequest.SUCCESS_HTTP -> {
//                return ExceptionUtil.releaseTryException({
//                    val jsonObject = JSONObject(data ?: "")
//                    if (jsonIsSuccess(jsonObject))
//                        return@releaseTryException arrayOf(AsyncHttpRequest.SUCCESS_HTTP,
//                                jsonObject.optString(DATA),
//                                jsonObject.optString(MSG) ?: "",
//                                method)
//                    else
//                        return@releaseTryException arrayOf(AsyncHttpRequest.FAILURE_HTTP,
//                                data,
//                                jsonObject.optString(MSG) ?: "",
//                                method)
//                }, {
//                    return arrayOf(AsyncHttpRequest.FAILURE_HTTP,
//                            data,
//                            R.string.respose_net.toText(),
//                            method)
//                })!!
//            }
//            AsyncHttpRequest.FAILURE_HTTP -> {
//                return arrayOf(AsyncHttpRequest.FAILURE_HTTP,
//                        data,
//                        R.string.respose_net.toText(),
//                        method)
//            }
//            AsyncHttpRequest.FAILURE_NETWORK -> {
//                return arrayOf(AsyncHttpRequest.FAILURE_NETWORK,
//                        data,
//                        R.string.netnotenble.toText(),
//                        method)
//            }
//            else -> null!!
//        }
        return null!!
    }

    fun jsonIsSuccess(jsonObject: JSONObject) =
            jsonObject.optString(CODE) == CODE_OK
}
