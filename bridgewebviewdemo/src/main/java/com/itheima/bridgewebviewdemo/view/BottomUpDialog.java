package com.itheima.bridgewebviewdemo.view;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.itheima.bridgewebviewdemo.R;


/**
 *
 */
public class BottomUpDialog extends Dialog implements View.OnClickListener{

    private Button btnPhone;
    private OnPhoneClickListener listener;
    private Button btnCancel;

    public BottomUpDialog(Context context) {
        super(context, R.style.custom_dlg);
        setContentView(R.layout.dialog_layout);
        btnPhone = (Button)findViewById(R.id.btnPhone);
        btnCancel = (Button)findViewById(R.id.btnCancel);
        btnPhone.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        getWindow().setGravity(Gravity.BOTTOM);
    }

    public BottomUpDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    protected BottomUpDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public void setContent(String phone){
        btnPhone.setText(phone);
    }

    public void setOnPhoneClickListener(OnPhoneClickListener listener){
        this.listener = listener;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnPhone:
                dismiss();
                listener.onPhoneClick();
                break;
            case R.id.btnCancel:
                dismiss();
                break;
        }
    }

    public interface OnPhoneClickListener{
        void onPhoneClick();
    }
}
