package com.dumai.loan.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dumai.loan.R;
import com.dumai.loan.base.BaseActivity;
import com.dumai.loan.util.SharedUtils;
import com.dumai.loan.util.view.ToolbarHelper;
import com.dumai.loan.view.gesture.GestureContentView;
import com.dumai.loan.view.gesture.GestureDrawline;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 2017-12-12 15:07:16
 * 手势绘制/校验界面
 * haoruigang
 */
public class GestureVerifyActivity extends BaseActivity {
    /**
     * 手机号码
     */
    public static final String PARAM_PHONE_NUMBER = "PARAM_PHONE_NUMBER";
    /**
     * 意图
     */
    public static final String PARAM_INTENT_CODE = "PARAM_INTENT_CODE";
    @BindView(R.id.text_title)
    TextView mTextTitle;
    @BindView(R.id.text_cancel)
    TextView mTextCancel;
    @BindView(R.id.top_layout)
    RelativeLayout mTopLayout;
    @BindView(R.id.user_logo)
    ImageView mImgUserLogo;
    @BindView(R.id.text_phone_number)
    TextView mTextPhoneNumber;
    @BindView(R.id.text_tip)
    TextView mTextTip;
    @BindView(R.id.text_forget_gesture)
    TextView mTextForget;
    @BindView(R.id.gesture_container)
    FrameLayout mGestureContainer;
    private GestureContentView mGestureContentView;
    // private TextView mTextOther;
    private String mParamPhoneNumber;
    private long mExitTime = 0;
    private int mParamIntentCode;

    private String getGpsd = "";

    private String nameString, psdString;
    private boolean isbackground = false;
    private int psd_count = 5;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_gesture_verify;
    }

    @Override
    protected void initToolbar(ToolbarHelper toolbarHelper) {

    }

    @Override
    protected void init() {
        super.init();
        getGpsd = SharedUtils.get(GestureVerifyActivity.this, "gesturePsd", "")
                .toString();
        nameString = SharedUtils
                .get(GestureVerifyActivity.this, "username", "username").toString();

        isbackground = (Boolean) SharedUtils.get(GestureVerifyActivity.this,
                "isbackground", false);
        ObtainExtraData();
        setUpViews();
    }

    private void ObtainExtraData() {
        mParamPhoneNumber = getIntent().getStringExtra(PARAM_PHONE_NUMBER);
        mParamIntentCode = getIntent().getIntExtra(PARAM_INTENT_CODE, 0);
    }

    private void setUpViews() {
        // mTextOther = (TextView) findViewById(R.id.text_other_account);

        mTextPhoneNumber.setText(nameString);
        mImgUserLogo.setImageResource(R.drawable.me_icon);
        // 初始化一个显示各个点的viewGroup
        mGestureContentView = new GestureContentView(this, true, getGpsd,
                new GestureDrawline.GestureCallBack() {

                    @Override
                    public void onGestureCodeInput(String inputCode) {

                    }

                    @Override
                    public void checkedSuccess() {
                        mGestureContentView.clearDrawlineState(0L);


                        if (isbackground) {
                            SharedUtils.put(GestureVerifyActivity.this,
                                    "isbackground", false);
                            GestureVerifyActivity.this.finish();
                        } else {
                            //保存登录成功后的信息
                            SharedUtils.putBoolean(GestureVerifyActivity.this, "LoginSuccess", true);
                            GestureVerifyActivity.this.finish();
                        }
                    }

                    @Override
                    public void checkedFail() {
                        psd_count--;
                        if (psd_count <= 0) {
                            SharedUtils.remove(GestureVerifyActivity.this, "gesturePsd");
                            Intent intent = new Intent(GestureVerifyActivity.this, LoginActivity.class);
                            // 打开新的Activity
                            startActivity(intent);
                            finish();
                        }

                        mGestureContentView.clearDrawlineState(1300L);
                        mTextTip.setVisibility(View.VISIBLE);
                        mTextTip.setText(Html.fromHtml("<font color='#c70c1e'>密码错误,还可再试</font>"
                                + "<font color='#c70c1e'>"
                                + psd_count
                                + "</font>"
                                + "<font color='#c70c1e'>次</font>"));
                        // 左右移动动画
                        Animation shakeAnimation = AnimationUtils
                                .loadAnimation(GestureVerifyActivity.this,
                                        R.anim.shake);
                        mTextTip.startAnimation(shakeAnimation);
                    }
                });
        // 设置手势解锁显示到哪个布局里面
        mGestureContentView.setParentView(mGestureContainer);
    }

    private String getProtectedMobile(String phoneNumber) {
        if (TextUtils.isEmpty(phoneNumber) || phoneNumber.length() < 11) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        builder.append(phoneNumber.subSequence(0, 3));
        builder.append("****");
        builder.append(phoneNumber.subSequence(7, 11));
        return builder.toString();
    }

    @OnClick({R.id.text_cancel, R.id.text_forget_gesture})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.text_cancel:
                this.finish();
                break;
            case R.id.text_forget_gesture:
                SharedUtils.clear(GestureVerifyActivity.this);
                Intent intent = new Intent(GestureVerifyActivity.this,
                        LoginActivity.class);
                // 打开新的Activity
                startActivity(intent);
                finish();
                break;
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_BACK:

                return false;
        }
        return true;
    }
}
