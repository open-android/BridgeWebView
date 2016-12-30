package com.itheima.bridgewebviewdemo;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.WebView;
import android.widget.Toast;

import com.itheima.bridgewebviewdemo.view.BottomUpDialog;
import com.tencent.connect.share.QQShare;
import com.tencent.open.utils.ThreadManager;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by youliang.ji on 2016/12/23.
 */

public class JavaSctiptMethods {
    private WebView webView;
    private Activity mActivity;

    public JavaSctiptMethods(Activity mContext, WebView webView) {
        this.mActivity = mContext;
        this.webView = webView;
    }

    /**
     * 统一分发js调用android分发
     */
    public void send(String[] jsons) {
        final String str = jsons[0];
        showLog(str);
        try {
            JSONObject json = new JSONObject(str);
            String action = json.optString("action");//js传递过来的动作，比如callPhone代表拨号，share2QQ代表分享到QQ，其实就是H5和android通信协议（自定义的）
            if (!TextUtils.isEmpty(action)) {
                if (action.equals("toast")) {
                    showToast(str);
                } else if (action.equals("callPhone")) {
                    callphone(str);
                } else if (action.equals("share2QQ")) {
                    share2QQ(str);
                } else if (action.equals("getHotelData")) {
                    getHotelData(str);
                } else if(action.equals("showCallPhoneDialog")){//底部弹出拨号对话框
                    final BottomUpDialog btmDlg = new BottomUpDialog(mActivity);
                    btmDlg.setContent(json.optString("phone"));
                    btmDlg.setOnPhoneClickListener(new BottomUpDialog.OnPhoneClickListener() {
                        @Override
                        public void onPhoneClick() {
                            callphone(str);//拨号
                            btmDlg.dismiss();
                        }
                    });
                    btmDlg.show();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取酒店详情数据
     */
    private void getHotelData(String str) {
        try {
            //解析js callback方法
            JSONObject mJson = new JSONObject(str);
            String callback = mJson.optString("callback");//解析js回调方法


            JSONObject json = new JSONObject();
            json.put("hotel_name", "维多利亚大酒店");
            json.put("order_status", "已支付");
            json.put("orderId", "201612291809626");
            json.put("seller", "携程");
            json.put("expire_time", "2017年1月6日 23:00");
            json.put("price", "688.0");
            json.put("back_price", "128.0");
            json.put("pay_tpye", "支付宝支付");
            json.put("room_size", "3间房");
            json.put("room_count", "3");
            json.put("in_date", "2017年1月6日 12:00");
            json.put("out_date", "2017年1月8日 12:00");
            json.put("contact", "赵子龙先生");
            json.put("phone", "18888888888");
            json.put("server_phone", "0755-85699309");
            json.put("address", "深圳市宝安区兴东地铁站旁边");
            showLog("android收到js消息:"+str);

            //调用js方法必须在主线程
//            webView.loadUrl("javascript:"+callback+"(" + json.toString() + ")");
            invokeJavaScript(callback, json.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 统一管理所有android调用js方法
     *
     * @param callback js回调方法名
     * @param json     传递json数据
     */
    private void invokeJavaScript(final String callback, final String json) {
        showToast("回调js方法："+callback+", 参数："+json);

        if(TextUtils.isEmpty(callback)) return;
        //调用js方法必须在主线程
        webView.post(new Runnable() {
            @Override
            public void run() {
                webView.loadUrl("javascript:" + callback + "(" + json + ")");
            }
        });
    }

    public void callphone(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            System.out.println("Demo callphone方法被调用:" + jsonObject.toString());
            //解析json
            String phone = jsonObject.optString("phone");

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//拨号：android 6.0运行时权限
                if (mActivity.checkSelfPermission(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_DENIED) {
                    mActivity.requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 100);
                }
            }
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone));
            mActivity.startActivity(intent);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void showToast(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            String msg = jsonObject.optString("msg");
            Toast.makeText(mActivity, msg, Toast.LENGTH_SHORT).show();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 分享到QQ
     *
     * @param jsonStr
     */
    public void share2QQ(String jsonStr) {
        try {
            JSONObject json = new JSONObject(jsonStr);
            Log.e("result", json.toString());
            //解析js传递过来的分享参数
            JSONObject mJson = new JSONObject(json.toString());
            String title = mJson.optString("title");
            String url = mJson.optString("url");
            String summary = mJson.optString("summary");
            String imgUrl = mJson.optString("imgUrl");

            //调用QQ分享SDK
            final Tencent tencent = Tencent.createInstance("222222", mActivity);
            final Bundle params = new Bundle();
            params.putString(QQShare.SHARE_TO_QQ_TITLE, title);
            params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, url);
            params.putString(QQShare.SHARE_TO_QQ_SUMMARY, summary);
            params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, imgUrl);

            ThreadManager.getMainHandler().post(new Runnable() {

                @Override
                public void run() {
                    if (null != tencent) {
                        tencent.shareToQQ(mActivity, params, new IUiListener() {
                            @Override
                            public void onComplete(Object o) {
                                Toast.makeText(mActivity, "" + "分享成功", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onError(UiError uiError) {
                                Toast.makeText(mActivity, "" + "分享失败", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onCancel() {
                                Toast.makeText(mActivity, "取消分析", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showLog(String msg) {
        Log.i("result", "" + msg);
    }

}
