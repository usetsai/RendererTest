package com.usetsai.renderertest.hwui;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.PixelCopy;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

public class HardwareCanvasSurfaceViewActivity extends Activity implements Callback {
    private SurfaceView mSurfaceView;
    private RenderingThread mThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout content = new FrameLayout(this);

        mSurfaceView = new SurfaceView(this);
        mSurfaceView.getHolder().addCallback(this);

        Button button = new Button(this);
        button.setText("Copy bitmap from SurfaceView");
        button.setOnClickListener((View v) -> {
            final Bitmap b = Bitmap.createBitmap(
                    mSurfaceView.getWidth(), mSurfaceView.getHeight(),
                    Bitmap.Config.ARGB_8888);
            PixelCopy.request(mSurfaceView, b,
                    (int result) -> {
                        if (result != PixelCopy.SUCCESS) {
                            Toast.makeText(HardwareCanvasSurfaceViewActivity.this,
                                    "Failed to copy Pixel.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(HardwareCanvasSurfaceViewActivity.this,
                                    "Success to copy Pixel size:" + b.getByteCount(), Toast.LENGTH_SHORT).show();
                        }
                    }, mSurfaceView.getHandler());
        });

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(button, LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        layout.addView(mSurfaceView, LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);

        content.addView(layout, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT));
        setContentView(content);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mThread = new RenderingThread(holder);
        mThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        mThread.setSize(width, height);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (mThread != null) mThread.stopRendering();
    }

    private static class RenderingThread extends Thread {
        private final SurfaceHolder mSurface;
        private volatile boolean mRunning = true;
        private int mWidth, mHeight;

        public RenderingThread(SurfaceHolder surface) {
            mSurface = surface;
        }

        void setSize(int width, int height) {
            mWidth = width;
            mHeight = height;
        }

        @Override
        public void run() {
            float x = 0.0f;
            float y = 0.0f;
            float speedX = 5.0f;
            float speedY = 3.0f;

            Paint paint = new Paint();
            paint.setColor(0xff00ff00);

            while (mRunning && !Thread.interrupted()) {
                final Canvas canvas = mSurface.lockHardwareCanvas();
                try {
                    canvas.drawColor(0x00000000, PorterDuff.Mode.CLEAR);
                    canvas.drawRect(x, y, x + 20.0f, y + 20.0f, paint);
                } finally {
                    mSurface.unlockCanvasAndPost(canvas);
                }

                if (x + 20.0f + speedX >= mWidth || x + speedX <= 0.0f) {
                    speedX = -speedX;
                }
                if (y + 20.0f + speedY >= mHeight || y + speedY <= 0.0f) {
                    speedY = -speedY;
                }

                x += speedX;
                y += speedY;

                try {
                    Thread.sleep(15);
                } catch (InterruptedException e) {
                    // Interrupted
                }
            }
        }

        void stopRendering() {
            interrupt();
            mRunning = false;
        }
    }
}
