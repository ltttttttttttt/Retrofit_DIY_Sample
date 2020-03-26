package com.lt.retrofitdemo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.lt.retrofitdemo.http.HttpFunctions
import com.lt.retrofitdemo.http.ObserverCallBack
import com.lt.retrofitdemo.http.callbackOf

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        HttpFunctions.instance.getJson(object : ObserverCallBack {
            override fun handleResult(data: String?, encoding: Int, method: Int) {
                data.print()
            }
        }, "1")

        HttpFunctions.instance.getJson(callbackOf<Any> {
            success {
                it.print()
            }
            //如果不处理失败的情况,可以不写failed
            failed {
                it.print()
            }
        }, "2")
    }
}