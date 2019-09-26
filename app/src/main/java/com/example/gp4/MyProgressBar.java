package com.example.gp4;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class MyProgressBar extends View {
    private int curValue; //현재값 저장
    private int maxValue; //최대값 저장
    private int lineColor; //진행바 색 저장

    private static long startTime; // 시작 시간
    private boolean isStart; // 작동 여부 저장( T: 작동, F: 멈춤)
    private boolean isFinish; // 심박수 측정 완료 여부( T: 완료, F: 아직)
    private Handler mHandler = new Handler();

    public MyProgressBar(Context context) {
        super(context);
    }

    public MyProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs,R.styleable.MyProgressBar,0,0);

        try{
            this.curValue = a.getInteger(R.styleable.MyProgressBar_curValue,0);
            this.maxValue = a.getInteger(R.styleable.MyProgressBar_maxValue,100);
            this.lineColor = a.getColor(R.styleable.MyProgressBar_lineColor,0xffc0b2d1);
            this.isStart = false;
            this.isFinish = false;
            this.startTime = 0;
        }finally {

        }

    }


    public boolean isFinish() { return isFinish; }

    public void setFinish(boolean finish) { isFinish = finish; }

    public boolean isStart() { return isStart; }

    public void setStart(boolean start) { isStart = start; }

    public static long getStartTime() {
        return startTime;
    }

    public static void setStartTime(long startTime) {
        MyProgressBar.startTime = startTime;
    }

    public int getLineColor() {
        return lineColor;
    }

    public void setLineColor(int color) {
        this.lineColor = color;
        invalidate();
        requestLayout();
    }

    public int getCurValue() {
        return curValue;
    }

    public void setCurValue(int curValue) {
        this.curValue = curValue;
        invalidate();
        requestLayout();
    }

    public int getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
        invalidate(); // 다시 그리도록
        requestLayout(); // 레이아웃에 맞는 사이즈 조정
    }

    // Handler mHandler = new Handler();

    @Override
    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);

        if(isStart && !isFinish) { // 시작했으면서, 끝나지 않았을 때에

        int width  =  this.getMeasuredWidth();
        int height = this.getMeasuredHeight();

        // 진행바 그리기 속성
        Paint circle = new Paint();
        circle.setColor(this.lineColor);
        circle.setStrokeWidth(30);
        circle.setAntiAlias(false);
        circle.setStyle(Paint.Style.STROKE);

        //startTime = System.currentTimeMillis();
       // Log.v("중요", "중요 myProgress 시작시간 : "+startTime);

        // 진행바 그리기
        if(curValue != 0) {
            canvas.drawArc(new RectF(100, 100, width-100, height-100), -90,
                    ((float) this.curValue / (float) this.maxValue * 360), false, circle);
        }

        // 텍스트 그리기 속성
        Paint textp = new Paint();
        textp.setColor(Color.BLACK);
        textp.setTextSize(100);
        textp.setTextAlign(Paint.Align.CENTER);

        if( !isFinish ) {
            canvas.drawText(this.curValue + "초", width / 2, height / 2, textp); // 텍스트 그리기
        }

        // 1초후에 invalidate()호출을 통해 해당 View를 다시 그리게 한다.
//        mHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                curValue +=1;
//                invalidate();
//                requestLayout();
//                //Log.v("중요", "중요 1초 지남");
//            }
//        }, 1000);

        }
    }
}
