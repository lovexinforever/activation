package com.tim.activatecodeview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * description: 自定义view 激活码 输入框
 * Created by yang.ding on 2017/12/28.
 */

public class ActivationCodeView extends RelativeLayout {

    /**
     * 父布局
     */
    private LinearLayout mContainerLl;

    /**
     * 输入框(用来输入,但是不显示)
     */
    private EditText mInputEt;

    /**
     * 存储TextView的数据 数量由自定义控件的属性传入
     */
    private TextView[] mTextViews;

    /**
     * 输入框数量
     */
    private int mEtNumber;

    /**
     * 每个输入框中文字个数
     */
    private int mTvCountNumber;

    /**
     * 输入框宽度
     */
    private int mEtWidth;

    /**
     * 透明度
     */
    private int mAlpha;

    /**
     * 输入框分割线
     */
    private Drawable mEtDividerDrawable;

    /**
     * 输入框文字颜色
     */
    private int mEtTextColor;

    /**
     * 输入框文字大小
     */
    private float mEtTextSize;

    /**
     * 输入框获取焦点时背景
     */
    private Drawable mEtBackgroundDrawableFocus;
    /**
     * 输入框没有焦点时背景
     */
    private Drawable mEtBackgroundDrawableNormal;

    /**
     * 文本监听
     */
    private ActivationCodeTextWatcher mTextWatcher = new ActivationCodeTextWatcher();

    public ActivationCodeView(Context context) {
        this(context,null);
    }

    public ActivationCodeView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public ActivationCodeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs,defStyleAttr);
    }


    /**
     * 初始化
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        LayoutInflater.from(context).inflate(R.layout.layout_activation_code, this);
        mContainerLl = (LinearLayout) this.findViewById(R.id.container_et);
        mInputEt = (EditText) this.findViewById(R.id.et);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ActivationCodeView, defStyleAttr, 0);
        mEtNumber = typedArray.getInteger(R.styleable.ActivationCodeView_icv_et_number, 1);
        mTvCountNumber = typedArray.getInteger(R.styleable.ActivationCodeView_icv_text_count_num, 1);
        mAlpha = typedArray.getInteger(R.styleable.ActivationCodeView_icv_text_bg_alpha,255);
        mEtWidth = typedArray.getDimensionPixelSize(R.styleable.ActivationCodeView_icv_et_width, 42);
        mEtDividerDrawable = typedArray.getDrawable(R.styleable.ActivationCodeView_icv_et_divider_drawable);
        mEtTextSize = typedArray.getDimensionPixelSize(R.styleable.ActivationCodeView_icv_et_text_size, 16);
        mEtTextColor = typedArray.getColor(R.styleable.ActivationCodeView_icv_et_text_color, Color.BLACK);
        mEtBackgroundDrawableFocus = typedArray.getDrawable(R.styleable.ActivationCodeView_icv_et_bg_focus);
        mEtBackgroundDrawableNormal = typedArray.getDrawable(R.styleable.ActivationCodeView_icv_et_bg_normal);
        //释放资源
        typedArray.recycle();


        // 当xml中未配置时 这里进行初始配置默认图片
        if (mEtDividerDrawable == null) {
            mEtDividerDrawable = context.getResources().getDrawable(R.drawable.shape_divider_identifying);
        }

        if (mEtBackgroundDrawableFocus == null) {
            mEtBackgroundDrawableFocus = context.getResources().getDrawable(R.drawable.shape_icv_et_bg_focus);
        }

        if (mEtBackgroundDrawableNormal == null) {
            mEtBackgroundDrawableNormal = context.getResources().getDrawable(R.drawable.shape_icv_et_bg_normal);
        }

        initUI();
    }

    /**
     * 初始化 UI
     */
    private void initUI() {
        initTextViews(getContext(), mEtNumber, mEtWidth, mEtDividerDrawable, mEtTextSize, mEtTextColor);
        initEtContainer(mTextViews);
        setListener();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // 设置当 高为 warpContent 模式时的默认值 为 50dp
        int mHeightMeasureSpec = heightMeasureSpec;

        int heightMode = MeasureSpec.getMode(mHeightMeasureSpec);
        if (heightMode == MeasureSpec.AT_MOST) {
            mHeightMeasureSpec = MeasureSpec.makeMeasureSpec((int) dp2px(50, getContext()), MeasureSpec.EXACTLY);
        }

        super.onMeasure(widthMeasureSpec, mHeightMeasureSpec);
    }


    //初始化TextView
    private void initTextViews(Context context, int etNumber, int etWidth, Drawable etDividerDrawable, float etTextSize, int etTextColor) {
        // 设置 editText 的输入长度
        mInputEt.setCursorVisible(false);//将光标隐藏
        mInputEt.setFilters(new InputFilter[]{new InputFilter.LengthFilter(etNumber)}); //最大输入长度
        // 设置分割线的宽度
        if (etDividerDrawable != null) {
            etDividerDrawable.setBounds(0, 0, etDividerDrawable.getMinimumWidth(), etDividerDrawable.getMinimumHeight());
            mContainerLl.setDividerDrawable(etDividerDrawable);
        }
        mTextViews = new TextView[etNumber];
        for (int i = 0; i < mTextViews.length; i++) {
            TextView textView = new TextView(context);
            textView.setTextSize(etTextSize);
            textView.setTextColor(etTextColor);
            textView.setWidth(etWidth);
            textView.setHeight(etWidth);

            if (i == 0) {
                textView.setBackgroundDrawable(mEtBackgroundDrawableFocus);
            } else {
                textView.setBackgroundDrawable(mEtBackgroundDrawableNormal);
            }
            //設置透明度
            textView.getBackground().setAlpha(mAlpha);
            textView.setGravity(Gravity.CENTER);

            textView.setFocusable(false);

            mTextViews[i] = textView;
        }
    }

    //初始化存储TextView 的容器
    private void initEtContainer(TextView[] mTextViews) {
        for (int i = 0; i < mTextViews.length; i++) {
            mContainerLl.addView(mTextViews[i]);
        }
    }


    private void setListener() {
        // 监听输入内容
        mInputEt.addTextChangedListener(mTextWatcher);

        // 监听删除按键
        mInputEt.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN) {
                    onKeyDelete();
                    return true;
                }
                return false;
            }
        });
    }


    // 给TextView 设置文字
    private void setText(String inputContent) {
        for (int i = 0; i < mTextViews.length; i++) {
            TextView tv = mTextViews[i];
            if (tv.getText().toString().trim().equals("") || tv.getText().toString().length() < mTvCountNumber) {
                tv.setText(tv.getText().toString() + inputContent);
                // 添加输入完成的监听
                if (inputCompleteListener != null) {
                    inputCompleteListener.inputComplete();
                }
                //光标跳转到下一个
                if(tv.getText().toString().length() == mTvCountNumber) {
                    tv.setBackgroundDrawable(mEtBackgroundDrawableNormal);
                    if (i < mEtNumber - 1) {
                        mTextViews[i + 1].setBackgroundDrawable(mEtBackgroundDrawableFocus);
                    }
                }

                break;
            }
        }
    }

    // 监听删除
    private void onKeyDelete() {
        for (int i = mTextViews.length - 1; i >= 0; i--) {
            TextView tv = mTextViews[i];
            if (!tv.getText().toString().trim().equals("")) {

                tv.setText(tv.getText().subSequence(0,tv.getText().length()-1));
                // 添加删除完成监听
                if (inputCompleteListener != null) {
                    inputCompleteListener.deleteContent();
                }
                tv.setBackgroundDrawable(mEtBackgroundDrawableFocus);
                if (i < mEtNumber - 1) {
                    mTextViews[i + 1].setBackgroundDrawable(mEtBackgroundDrawableNormal);
                }
                break;
            }
        }
    }


    /**
     * 获取输入文本
     *
     * @return string
     */
    public String getInputContent() {
        StringBuffer buffer = new StringBuffer();
        for (TextView tv : mTextViews) {
            buffer.append(tv.getText().toString().trim());
        }
        return buffer.toString();
    }

    /**
     * 删除输入内容
     */
    public void clearInputContent() {
        for (int i = 0; i < mTextViews.length; i++) {
            if (i == 0) {
                mTextViews[i].setBackgroundDrawable(mEtBackgroundDrawableFocus);
            } else {
                mTextViews[i].setBackgroundDrawable(mEtBackgroundDrawableNormal);
            }
            mTextViews[i].setText("");
        }
    }

    /**
     * 设置输入框个数
     * @param etNumber
     */
    public void setEtNumber(int etNumber) {
        this.mEtNumber = etNumber;
        mInputEt.removeTextChangedListener(mTextWatcher);
        mContainerLl.removeAllViews();
        initUI();
    }


    /**
     * 获取输入的位数
     *
     * @return int
     */
    public int getEtNumber() {
        return mEtNumber;
    }

    // 输入完成 和 删除成功 的监听
    private InputCompleteListener inputCompleteListener;

    public void setInputCompleteListener(InputCompleteListener inputCompleteListener) {
        this.inputCompleteListener = inputCompleteListener;
    }

    public interface InputCompleteListener {
        void inputComplete();

        void deleteContent();
    }


    public float dp2px(float dpValue, Context context) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpValue, context.getResources().getDisplayMetrics());
    }

    public float sp2px(float spValue, Context context) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                spValue, context.getResources().getDisplayMetrics());
    }

    private class ActivationCodeTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            String inputStr = editable.toString();
            if (inputStr != null && !inputStr.equals("")) {
                setText(inputStr);
                mInputEt.setText("");
            }
        }
    }
}

