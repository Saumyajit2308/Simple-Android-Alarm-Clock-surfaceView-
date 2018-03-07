package com.example.h.analogclock.widget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.text.format.Time;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.example.h.analogclock.R;

import java.util.TimeZone;


public class ClockViewSurface extends SurfaceView implements SurfaceHolder.Callback {

    public static final int DEGREE_PER_MINUTE = 6;
    public static final int DEGREE_PER_SECOND = 6;
    public static final int HAND_TAIL_LENGTH = 25;

    private SurfaceHolder holder;
    private SurfaceViewThread mSurfaceViewThread;
    private boolean hasSurface;

    private final Handler mHandler = new Handler();
    private Bitmap scaled;
    private Bitmap bitmap;

    private Time mCalendar;
    private float mSeconds;
    private float mMinutes;
    private float mHour;

    private boolean mAttached;

    private int clockFormat = 12;
    private int angle = 360 / clockFormat;

    private Paint markerPaint;
    private Paint mp;
    private Paint textPaint;
    private Paint circlePaint;
    private Paint mHourHandPaint;
    private Paint mMinuteHandPaint;
    private Paint mSecondHandPaint;

    int[] glassGradientColors;
    float[] glassGradientPositions;

    private int textHeight;
    private int flag;

    public ClockViewSurface(Context context) {
        super(context);
        if (!isInEditMode()) {
            init();
        }
    }

    public ClockViewSurface(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode()) {
            init();
        }

    }

    public ClockViewSurface(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (!isInEditMode()) {
            init();
        }
    }

    public void setClockFormat(int clockFormat) {
        this.clockFormat = clockFormat;
        angle = 360 / clockFormat;
    }
    public void changeColor1() {
        this.flag = 1;
        mSecondHandPaint.setColor(getResources().getColor(R.color.black));
        mMinuteHandPaint.setColor(getResources().getColor(R.color.black));
        mHourHandPaint.setColor(getResources().getColor(R.color.black));
        markerPaint.setColor(getResources().getColor(R.color.black));
        mp.setColor(getResources().getColor(R.color.black));

    }
    public void changeColor2() {
        this.flag = 1;
        mSecondHandPaint.setColor(getResources().getColor(R.color.secondHandColorRed));
        mMinuteHandPaint.setColor(getResources().getColor(R.color.secondHandColorRed));
        mHourHandPaint.setColor(getResources().getColor(R.color.secondHandColorRed));
        markerPaint.setColor(getResources().getColor(R.color.secondHandColorRed));
        mp.setColor(getResources().getColor(R.color.secondHandColorRed));
    }
    public void changeColor3() {
        this.flag = 1;
        mSecondHandPaint.setColor(getResources().getColor(R.color.green));
        mMinuteHandPaint.setColor(getResources().getColor(R.color.green));
        mHourHandPaint.setColor(getResources().getColor(R.color.green));
        markerPaint.setColor(getResources().getColor(R.color.green));
        mp.setColor(getResources().getColor(R.color.green));
    }
    public void changeColor4() {
        this.flag = 1;
        mSecondHandPaint.setColor(getResources().getColor(R.color.blue));
        mMinuteHandPaint.setColor(getResources().getColor(R.color.blue));
        mHourHandPaint.setColor(getResources().getColor(R.color.blue));
        markerPaint.setColor(getResources().getColor(R.color.blue));
        mp.setColor(getResources().getColor(R.color.blue));
    }


    private void init() {
        holder = getHolder();
        holder.addCallback(this);
        hasSurface = false;
        setZOrderOnTop(true);
        holder.setFormat(PixelFormat.TRANSLUCENT);

        Resources r = this.getResources();

        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circlePaint.setColor(r.getColor(R.color.background_color));
        circlePaint.setStrokeWidth(1);
        circlePaint.setStyle(Paint.Style.FILL_AND_STROKE);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(r.getColor(R.color.text_color));

        textHeight = 48;
        textPaint.setTextSize(textHeight);

        markerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        markerPaint.setColor(r.getColor(R.color.black));
        markerPaint.setStrokeWidth(8);

        mp = new Paint(Paint.ANTI_ALIAS_FLAG);
        mp.setColor(r.getColor(R.color.black));
        mp.setStrokeWidth(5);

        mHourHandPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mHourHandPaint.setStrokeWidth(5);
        mHourHandPaint.setColor(r.getColor(R.color.text_color));

        mMinuteHandPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mMinuteHandPaint.setStrokeWidth(5);
        mMinuteHandPaint.setColor(r.getColor(R.color.text_color));

        mSecondHandPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mSecondHandPaint.setStrokeWidth(3);
        mSecondHandPaint.setColor(r.getColor(R.color.black));

        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.nu_pogodi);

    }

    @Override
    protected void onMeasure(int wMeasureSpec, int hMeasureSpec) {
        int measureHeight = measure(hMeasureSpec);
        int measureWidth = measure(wMeasureSpec);

        int d = Math.min(measureHeight, measureWidth);

        setMeasuredDimension(d, d);
    }

    private int measure(int measureSpec) {
        int result = 0;

        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.UNSPECIFIED) {
            result = 200;
        } else {
            result = specSize;
        }

        return result;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        if (!mAttached) {
            mAttached = true;
            IntentFilter filter = new IntentFilter();

            filter.addAction(Intent.ACTION_TIME_TICK);
            filter.addAction(Intent.ACTION_TIME_CHANGED);
            filter.addAction(Intent.ACTION_TIMEZONE_CHANGED);

            getContext().registerReceiver(mIntentReceiver, filter, null, mHandler);
        }


        mCalendar = new Time();

        // Make sure we update to the current time
        onTimeChanged();

        // tick the seconds
        post(mClockTick);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mAttached) {
            getContext().unregisterReceiver(mIntentReceiver);
            mAttached = false;
        }
    }

    public void resume() {
        // Create and start the graphics update thread
        if (mSurfaceViewThread == null) {
            mSurfaceViewThread = new SurfaceViewThread();

            if (hasSurface) {
                mSurfaceViewThread.start();
            }
        }
    }

    public void pause() {
        // Kill the graphics update thread
        if (mSurfaceViewThread != null) {
            mSurfaceViewThread.requestExitAndWait();
            mSurfaceViewThread = null;
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d("----", "surfaceCreated");
        hasSurface = true;
        if (mSurfaceViewThread != null) {
            mSurfaceViewThread.start();
        } else {
            resume();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (mSurfaceViewThread != null) {
            mSurfaceViewThread.onWindowResize(width, height);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d("----", "surfaceDestroyed");
        hasSurface = false;
        pause();
    }

    class SurfaceViewThread extends Thread {
        private boolean done;

        SurfaceViewThread() {
            super();
            done = false;
        }

        @Override
        public void run() {
            SurfaceHolder surfaceHolder = holder;

            while (!done) {
                Canvas canvas = null;
                try {
                    canvas = surfaceHolder.lockCanvas();
                    if (canvas != null) {
                        int measureWidth = getMeasuredWidth();
                        int measureHeight = getMeasuredHeight();

                        int px = measureWidth / 2;
                        int py = measureHeight / 2;

                        int radius = Math.min(px, py);

                        // Draw the background
                        canvas.drawCircle(px, py, radius, circlePaint);

                        // Rotate our perspective so that the 'top' is
                        // facing the current bearing
                        canvas.save();

                        scaled = Bitmap.createScaledBitmap(bitmap, (int) (radius / 1.5),(int) (radius / 1.5), true);
                        canvas.drawBitmap(scaled, px - scaled.getWidth() / 2,
                                py - scaled.getHeight() / 2, null);

                        // Draw the marker and text every 30 degrees
                        for (int i = 0; i < clockFormat; i++) {
                            // Draw a marker
                            canvas.drawLine(px, py - radius, px, py - radius + 22, markerPaint);

                            canvas.rotate(30, px, py);
                        }


                        for (int i = 0; i < 60; i++) {
                            // Draw a marker
                            canvas.drawLine(px, py - radius, px, py - radius + 15, mp);

                            canvas.rotate(6, px, py);
                        }

                        // Draw numbers
                        for (int i = 1; i <= clockFormat; i++) {
                            int textWidth = (int) textPaint.measureText(String.valueOf(i));

                            int dx = (int) ((radius - 50) * Math.sin(Math.toRadians(i * angle)));
                            int dy = (int) ((radius - 50) * Math.cos(Math.toRadians(i * angle)));

                            int cardinalX = -textWidth / 2;

                            canvas.drawText(String.valueOf(i), px + dx + cardinalX,
                                    py - dy + textHeight / 3,
                                    textPaint);
                        }

                        // Draw hour hand
                        int dx = (int) ((radius / 3) * Math
                                .sin(Math.toRadians(mHour * angle + (angle * mMinutes / 60))));
                        int dy = (int) ((radius / 3) * Math
                                .cos(Math.toRadians(mHour * angle + (angle * mMinutes / 60))));
                        Log.d("----", "dx= " + dx + ", dy= " + dy);

                        canvas.drawLine(
                                (float) (px - HAND_TAIL_LENGTH * Math
                                        .sin(Math.toRadians(
                                                mHour * angle + (angle * mMinutes / 60)))),
                                (float) (py + HAND_TAIL_LENGTH * Math
                                        .cos(Math.toRadians(
                                                mHour * angle + (angle * mMinutes / 60)))),
                                px + dx, py - dy, mHourHandPaint);

                        // Draw minute hand
                        dx = (int) ((radius / 1.5) * Math
                                .sin(Math.toRadians(mMinutes * DEGREE_PER_MINUTE)));
                        dy = (int) ((radius / 1.5) * Math
                                .cos(Math.toRadians(mMinutes * DEGREE_PER_MINUTE)));

                        canvas.drawLine((float) (px - HAND_TAIL_LENGTH * Math
                                        .sin(Math.toRadians(mMinutes * DEGREE_PER_MINUTE))),
                                (float) (py + HAND_TAIL_LENGTH * Math
                                        .cos(Math.toRadians(mMinutes * DEGREE_PER_MINUTE))),
                                px + dx,
                                py - dy, mMinuteHandPaint);

                        // Draw second hand
                        dx = (int) ((radius / 1.2) * Math
                                .sin(Math.toRadians(mSeconds * DEGREE_PER_SECOND)));
                        dy = (int) ((radius / 1.2) * Math
                                .cos(Math.toRadians(mSeconds * DEGREE_PER_SECOND)));

                        canvas.drawLine((float) (px - 2 * HAND_TAIL_LENGTH * Math
                                        .sin(Math.toRadians(mSeconds * DEGREE_PER_SECOND))),
                                (float) (py + 2 * HAND_TAIL_LENGTH * Math.cos(
                                        Math.toRadians(mSeconds * DEGREE_PER_SECOND))), px + dx,
                                py - dy,
                                mSecondHandPaint);

                    }
                }
                finally {
                    if (canvas != null) {
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    }
                }
            }
        }

        public void requestExitAndWait() {
            // Mark this thread as complete and combine into
            // the main application thread

            done = true;
            try {
                join();
            } catch (InterruptedException ex) {

            }
        }

        public void onWindowResize(int w, int h) {
            // Deal with a change in the available surface size.
        }
    }

    private void onTimeChanged() {
        mCalendar.setToNow();

        int hour = mCalendar.hour;
        int minute = mCalendar.minute;
        int second = mCalendar.second;

        mSeconds = second;
        mMinutes = minute;
        mHour = hour;
    }

    private final Runnable mClockTick = new Runnable () {

        @Override
        public void run() {
            onTimeChanged();
            ClockViewSurface.this.postDelayed(mClockTick, 1000);
        }
    };

    private final BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_TIMEZONE_CHANGED)) {
                String tz = intent.getStringExtra("time-zone");
                mCalendar = new Time(TimeZone.getTimeZone(tz).getID());
            }

            onTimeChanged();

        }
    };
}
