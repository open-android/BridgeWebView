package com.itheima.view;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * js和android通信桥梁WebView，封装好js和安卓通信机制
 * Created by youliang.ji on 2016/12/23.
 */

public class BridgeWebView extends WebView {

    /***
     * js调用android方法的映射字符串
     **/
    private static final String JS_INTERFACE = "jsInterface";

    public BridgeWebView(Context context) {
        super(context);
    }

    public BridgeWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BridgeWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 注册js和android通信桥梁对象
     *
     * @param obj 桥梁类对象,该对象提供方法让js调用,默认开启JavaScriptEnabled=true
     */
    public void addBridgeInterface(Object obj) {
        this.getSettings().setJavaScriptEnabled(true);
        addJavascriptInterface(new MyJavaScriptMethod(obj), JS_INTERFACE);
    }

    /**
     * 注册js和android通信桥梁对象
     * @param obj 桥梁类对象,该对象提供方法让js调用
     * @param url 默认开启JavaScriptEnabled=true
     */
    public void addBridgeInterface(Object obj, String url) {
        this.getSettings().setJavaScriptEnabled(true);
        addJavascriptInterface(new MyJavaScriptMethod(obj), JS_INTERFACE);
        loadUrl(url);
    }

    /**
     * 回调js方法
     * @param json 参数，json格式字符串
     */
    public void callbackJavaScript(String json){

    }

    private void invokeJavaScript(String callback, String params){

    }

    /**
     * 内置js桥梁类
     * Created by youliang.ji on 2016/12/23.
     */

    public class MyJavaScriptMethod {

        private Object mTarget;
        private Method targetMethod;


        public MyJavaScriptMethod(Object targer) {
            this.mTarget = targer;
        }

        /**
         * 内置桥梁方法
         * @param method 方法名
         * @param json   js传递参数，json格式
         */
        @JavascriptInterface
        public void invokeMethod(String method, String[] json) {
            Class<?>[] params = new Class[]{String[].class};
            try {
                Method targetMethod = this.mTarget.getClass().getDeclaredMethod(method, params);
                targetMethod.invoke(mTarget, new Object[]{json});//反射调用js传递过来的方法，传参

            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

        }
    }

}
