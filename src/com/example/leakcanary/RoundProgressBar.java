package com.example.leakcanary;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.*;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;


/**
 * Splash界面,右上角的跳过按钮
 *
 * @author laipengxu
 */
public class RoundProgressBar extends View {
    /**
     * 画笔对象的引用
     */
    private Paint paint;

    /**
     * 圆环的颜色
     */
    private int roundColor;

    /**
     * 圆环进度的颜色
     */
    private int roundProgressColor;

    /**
     * 中间进度百分比的字符串的颜色
     */
    private int textColor;

    /**
     * 中间进度百分比的字符串的字体
     */
    private float textSize;

    /**
     * 圆环的宽度
     */
    private float roundWidth;

    /**
     * 最大进度
     */
    private int max;

    /**
     * 当前进度
     */
    private int progress = 0;
    /**
     * 是否显示中间的进度
     */
    private boolean textIsDisplayable;

    /**
     * 进度的风格，实心或者空心
     */
    private int style;

    public static final int STROKE = 0;
    public static final int FILL = 1;

    private int interval = 8;  // 第二个圆环距离大圆外边缘的距离，根据ui自己调整
    private String text; // 中间要显示的文字
    private float lineWidth = 1;  // 中间圆环的宽度

    private long mProgressingTime = 3000; // 进度时长(默认是3000毫秒)
    private int mProgressColor;

    public RoundProgressBar(Context context) {
        this(context, null);
    }

    public RoundProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundProgressBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        paint = new Paint();

        TypedArray mTypedArray = context.obtainStyledAttributes(attrs,
                R.styleable.RoundProgressBar);

        //获取自定义属性和默认值
        // 最大圆的颜色(默认是透明黑)
        roundColor = mTypedArray.getColor(R.styleable.RoundProgressBar_roundColor, getResources().getColor(android.R.color.holo_red_light));
        // 中心笑小圆环颜色(默认是白色)
        roundProgressColor = mTypedArray.getColor(R.styleable.RoundProgressBar_roundProgressColor, getResources().getColor(android.R.color.white));
        // 进度圆环颜色(默认是橙色)
        mProgressColor = mTypedArray.getColor(R.styleable.RoundProgressBar_progressColor, getResources().getColor(android.R.color.darker_gray));
        // 文本颜色(默认是白色)
        textColor = mTypedArray.getColor(R.styleable.RoundProgressBar_textColor, Color.WHITE);
        // 文本大小(默认是14sp)
        textSize = mTypedArray.getDimensionPixelOffset(R.styleable.RoundProgressBar_textSize, 14);
        text = mTypedArray.getString(R.styleable.RoundProgressBar_text);
        if (TextUtils.isEmpty(text)) {
            text = getResources().getString(R.string.skip_splash_activity); // 中间文字默认是"跳过"
        }
        roundWidth = mTypedArray.getDimension(R.styleable.RoundProgressBar_roundWidth, 5);
        max = mTypedArray.getInteger(R.styleable.RoundProgressBar_max, 100);
        textIsDisplayable = mTypedArray.getBoolean(R.styleable.RoundProgressBar_textIsDisplayable, true);
        style = mTypedArray.getInt(R.styleable.RoundProgressBar_style, 0);

        mTypedArray.recycle();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int radius = (int) (getWidth() - roundWidth) / 2; //圆环的半径

        //最大圆
        int maxLayerX = getWidth() / 2;
        paint.setColor(roundColor);
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
        canvas.drawCircle(maxLayerX, maxLayerX, maxLayerX, paint);

        //中间圆圈
        paint.setColor(roundProgressColor);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(lineWidth); //暂时写死在这里，根据自己项目调整该值大小，如果有需要也可写入attr中
        canvas.drawCircle(maxLayerX, maxLayerX, maxLayerX - interval, paint); //画出圆环

        //中间的文字
        paint.setColor(textColor);
        paint.setTextSize(textSize);
        paint.setStrokeWidth(0);
        paint.setTypeface(Typeface.DEFAULT);
        float textWidth = paint.measureText(text);
        Paint.FontMetricsInt fontMetrics = paint.getFontMetricsInt();
        int baseline = (getMeasuredHeight() - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top;
        canvas.drawText(text, maxLayerX - textWidth / 2, baseline, paint);

        //设置进度是实心还是空心
        paint.setStrokeWidth(10); //设置圆环的宽度
        paint.setColor(mProgressColor);  //设置进度的颜色
        paint.setStyle(Paint.Style.FILL);
        float displacement = interval + lineWidth * 2;
        RectF oval = new RectF(maxLayerX - radius + displacement, maxLayerX - radius + displacement,
                maxLayerX + radius - displacement, maxLayerX + radius - displacement);  //用于定义的圆弧的形状和大小的界限

        switch (style) {
            case STROKE: {
                paint.setStyle(Paint.Style.STROKE);
                canvas.drawArc(oval, 0, 360 * progress / max, false, paint);  //根据进度画圆弧
                break;
            }
            case FILL: {
                paint.setStyle(Paint.Style.FILL_AND_STROKE);
                if (progress != 0)
                    canvas.drawArc(oval, 0, 360 * progress / max, true, paint);  //根据进度画圆弧
                break;
            }
        }

    }

    public void start() {

        // 进度置为0
        progress = 0;
        handler.sendEmptyMessageDelayed(111, 100);//0.1秒变动一次

    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 111) {
                if (progress < 100) {
                    progress += 100 / (mProgressingTime / 100);
                    setProgress(progress);
                    handler.sendEmptyMessageDelayed(111, 100);//0.1秒变动一次,这个也可以使用线程睡眠的方法
                }

            }

        }
    };

    public void stop() {
        progress = 100;
        if (handler!=null){
            handler.removeCallbacksAndMessages(null);
            handler = null;
        }

    }

    public synchronized int getMax() {
        return max;
    }

    /**
     * 设置进度的最大值
     *
     * @param max
     */
    public synchronized void setMax(int max) {
        if (max < 0) {
            throw new IllegalArgumentException("max not less than 0");
        }
        this.max = max;
    }

    /**
     * 获取进度.需要同步
     *
     * @return
     */
    public synchronized int getProgress() {
        return progress;
    }

    /**
     * 设置进度，此为线程安全控件，由于考虑多线的问题，需要同步
     * 刷新界面调用postInvalidate()能在非UI线程刷新
     *
     * @param progress
     */
    public synchronized void setProgress(int progress) {
        if (progress < 0) {
            throw new IllegalArgumentException("progress not less than 0");
        }
        if (progress > max) {
            progress = max;
        }
        if (progress <= max) {
            this.progress = progress;
            postInvalidate();
        }

    }

    /**
     * 设置进度进行时长
     */
    public void setProgressingTime(long progressingTime) {
        mProgressingTime = progressingTime;
    }
}