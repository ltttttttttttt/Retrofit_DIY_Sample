package com.lt.retrofitdemo.source;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

import com.lt.retrofitdemo.http.ObserverCallBack;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.PostFormBuilder;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 异步数据请求
 */
final public class AsyncHttpRequest {

    public static final int POST = 1; // post 提交
    public static final int GET = 2; // get 提交

    public static final int SUCCESS_HTTP = 0;//成功
    public static final int FAILURE_HTTP = 1;//失败
    public static final int FAILURE_NETWORK = 2;//网络失败

    private AsyncHttpRequest() {
    }

    private static Handler handler;

    @SuppressLint("HandlerLeak")
    private static void sendToHandler(Message message) {
        if (handler == null) {
            handler = new Handler() {
                @Override
                public void handleMessage(@NonNull Message msg) {
                    Object[] objs = (Object[]) msg.obj;
                    startCallBack(
                            (int) objs[0],
                            objs[1].toString(),
                            (Map<String, String>) objs[2],
                            (List<String>) objs[3],
                            (List<String>) objs[4],
                            objs[5].toString(),
                            (int) objs[6]
                    );
                }
            };
        }
        handler.sendMessage(message);
    }

    /**
     * 加密
     */
    public static HashMap<String, String> encryption(Map<String, String> map) {
        HashMap<String, String> params = new HashMap<>();
        JSONObject jsonobj = new JSONObject(map);
//        params.put("s", DesUtil.desCrypto(jsonobj.toString()));
        return params;
    }

    public static String decrypt(String s) {
//        return DesUtil.decrypt(s);
        return "";
    }

    /***
     * get和post请求方法
     *
     * @param sendType 请求类型：get和post
     * @param url      请求地址
     * @param map      post使用到的
     * @param callBack 异步回调
     * @param i        请求的方法对应的int值（整个项目中唯一不重复的）
     */
    public static void requestGetOrPost(int sendType, String url,
                                        Map<String, String> map, ObserverCallBack callBack, int i) {
        requestGetOrPost(sendType, url, map, null, null, callBack, i);
    }

    /***
     * get和post请求方法
     *
     * @param sendType  请求类型：get和post
     * @param url       请求地址
     * @param map       post使用到的
     * @param callBack  异步回调
     * @param files     文件集合
     * @param i        请求的方法对应的int值（整个项目中唯一不重复的）
     */
    public static void requestGetOrPost(final int sendType, final String url,
                                        final Map<String, String> map, final List<String> files, final List<String> fileNames,
                                        ObserverCallBack callBack, final int i) {
//        if (!NetUtil.isNetworkAvailable(App.getInstance().getApplicationContext())) {
//            if (callBack != null)
//                callBack.handleResult("", FAILURE_NETWORK, i);
//            else
//                ToastUtil.showToast(App.getInstance().getApplicationContext().getResources().getString(R.string.netnotenble));
//            return;
//        }
        final String callBackKey = CallBackTask.getInstance().add(callBack);
        //利用handler,使方法出栈,可使callback被释放(不用final修饰callback)
        Message obtain = Message.obtain();
        obtain.obj = new Object[]{
                sendType,
                url,
                map,
                files,
                fileNames,
                callBackKey,
                i
        };
        sendToHandler(obtain);
    }

    static void startCallBack(final int sendType, final String url,
                              final Map<String, String> map, final List<String> files, final List<String> fileNames,
                              final String callBackId, final int i) {
        switch (sendType) {
            case POST:
//                if (ListExtendAttrKt.getNullSize(files) == 0)
//                    post(url, map, files, fileNames, callBackId, i);
//                else
//                    ThreadPool.INSTANCE.submitToCacheThreadPool(new Function0<Unit>() {
//                        @Override
//                        public Unit invoke() {
//                            post(url, map, files, fileNames, callBackId, i);
//                            return Unit.INSTANCE;
//                        }
//                    });
                break;
            case GET:
//                if (Config.getIS_DEBUG()) {
//                    StringBuilder sb = new StringBuilder();
//                    sb.append(url);
//                    if (map != null) {
//                        if (!url.endsWith("?")) {
////                        sb.append("&");
//                            sb.append("?");
//
//                        }
//                        Set<String> keySet = map.keySet();
//                        for (String key : keySet) {
//                            String value = map.get(key);
//                            if (value == null) {
//                                continue;
//                            }
//                            value = value.replace(" ", "");
//                            sb.append(key).append("=").append(value).append("&");
//                        }
//                        if (sb.toString().endsWith("&")) {
//                            sb.deleteCharAt(sb.toString().length() - 1);
//                        }
//                    }
//                    LogUtil.w2("http", "接口get==" + sb.toString());
//                    if (map != null) {
//                        LogUtil.w2("http", url + map.toString());
//                    }
//                }
//                OkHttpUtils
//                        .get().url(url).params(map).tag(App.getInstance().getApplicationContext())
//                        .build().connTimeOut(20000)
//                        .execute(new StringCallback() {
//                            @Override
//                            public void onError(Call call, final Exception e, int id) {
//                                HandlerPool.INSTANCE.post(0, HandlerPool.DEFAULT_TAG, new Function0<Unit>() {
//                                    @Override
//                                    public Unit invoke() {
//                                        try {
//                                            ObserverCallBack callBack = CallBackTask.getInstance().get(callBackId);
//                                            if (callBack == null)
//                                                return Unit.INSTANCE;
//                                            callBack.handleResult(e.toString(), FAILURE_HTTP,
//                                                    i);
//                                        } catch (Exception e1) {
//                                            LogUtil.e2("httpError", "AsyncHttpRequest.onError : " + e1);
//                                            if (Config.getIS_DEBUG())
//                                                Toast.makeText(App.getInstance().getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
//                                        }
//                                        return Unit.INSTANCE;
//                                    }
//                                });
//                            }
//
//                            @Override
//                            public void onResponse(String response, int id) {
//                                try {
//                                    ObserverCallBack callBack = CallBackTask.getInstance().get(callBackId);
//                                    if (callBack == null)
//                                        return;
//                                    callBack.handleResult(decrypt(response), SUCCESS_HTTP, i);
//                                } catch (Exception e) {
//                                    LogUtil.e2("httpError", "AsyncHttpRequest.onResponse : " + e);
//                                    if (Config.getIS_DEBUG())
//                                        Toast.makeText(App.getInstance().getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
//                                }
//                            }
//
//                        });
                break;
        }
    }

    private static void post(String url, Map<String, String> map, List<String> files, List<String> fileNames, final String callBackId, final int i) {
        Map<String, String> encryptionMap = null;
//        LogUtil.w2(i + " http", "接口post==" + url + (map != null ? map.toString() : ""));
        if (map != null && map.size() != 0) {
            encryptionMap = encryption(map);
//            LogUtil.w2(i + " http", "加密后==" + encryptionMap.toString());
        }//todo 使用des加密请求参数
        PostFormBuilder pb = OkHttpUtils.post().url(url);
        //上传文件
//        if (0 != ListExtendAttrKt.getNullSize(files)) {
//            for (int j = 0, k = files.size(); j < k; j++) {
//                try {
//                    File file = new File(ImageCompress.start(AppManager.INSTANCE.currentActivity(), files.get(j)));
//                    String fileName = "file";
//                    //String fileName = "img" + j;
//                    if (ListExtendAttrKt.getNullSize(fileNames) > j)
//                        fileName = fileNames.get(j);
//                    pb.addFile(fileName, file.getName(), file);
//                    LogUtil.w2("http", "附加压缩图片" + j + ": " + file.getAbsolutePath() + "  ,size=" + file.length());
//                } catch (Exception e) {
//                    LogUtil.e2("httpError", "AsyncHttpRequest.requestGetOrPost : " + e);
//                    try {
//                        File file = new File(files.get(j));
//                        String fileName = "file";
//                        //String fileName = "file" + j;
//                        if (ListExtendAttrKt.getNullSize(fileNames) > j)
//                            fileName = fileNames.get(j);
//                        pb.addFile(fileName, file.getName(), file);
//                        LogUtil.w2("http", "附加文件" + j + ": " + file.getAbsolutePath() + "  ,size=" + file.length());
//                    } catch (Exception e1) {
//                        LogUtil.e2("httpError", "AsyncHttpRequest.requestGetOrPost : " + e1);
//                    }
//                }
//            }
//        }
//        pb.params(encryptionMap).tag(App.getInstance().getApplicationContext())
//                .build().connTimeOut(20000)//todo 可能大文件需要更长的超时时间
//                .execute(new StringCallback() {
//
//                    @Override
//                    public void onError(Call call, final Exception e, int id) {
//                        HandlerPool.INSTANCE.post(0, HandlerPool.DEFAULT_TAG, new Function0<Unit>() {
//                            @Override
//                            public Unit invoke() {
//                                try {
//                                    ObserverCallBack callBack = CallBackTask.getInstance().get(callBackId);
//                                    if (callBack == null)
//                                        return Unit.INSTANCE;
//                                    callBack.handleResult(e.toString(), FAILURE_HTTP,
//                                            i);
//                                } catch (Exception e1) {
//                                    LogUtil.e2("httpError", "AsyncHttpRequest.onError : " + e1);
//                                    if (Config.getIS_DEBUG())
//                                        Toast.makeText(App.getInstance().getApplicationContext(), e1.toString(), Toast.LENGTH_LONG).show();
//                                }
//                                return Unit.INSTANCE;
//                            }
//                        });
//                    }
//
//                    @Override
//                    public void onResponse(String response, int id) {
//                        try {
//                            ObserverCallBack callBack = CallBackTask.getInstance().get(callBackId);
//                            if (callBack == null)
//                                return;
//                            callBack.handleResult(decrypt(response), SUCCESS_HTTP,
//                                    i);
//                        } catch (Exception e) {
//                            LogUtil.e2("httpError", "AsyncHttpRequest.onResponse : " + e);
//                            if (Config.getIS_DEBUG())
//                                Toast.makeText(App.getInstance().getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
//                        }
//                    }
//
//                });
    }
}
