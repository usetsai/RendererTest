package com.usetsai.renderertest.hwui;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorSpace;
import android.graphics.ColorSpace.Named;
import android.graphics.HardwareBufferRenderer;
import android.graphics.Paint;
import android.graphics.RenderNode;
import android.hardware.HardwareBuffer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.time.Duration;
import java.util.concurrent.Executors;

public class HardwareBufferRendererActivity extends Activity {

    private ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mImageView = new ImageView(this);
        mImageView.setBackgroundColor(Color.MAGENTA);
        FrameLayout layout = new FrameLayout(this);
        layout.setBackgroundColor(Color.CYAN);
        layout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        layout.addView(mImageView, new FrameLayout.LayoutParams(100, 100));
        setContentView(layout);

        HardwareBuffer buffer = HardwareBuffer.create(100, 100, HardwareBuffer.RGBA_8888, 1,
                HardwareBuffer.USAGE_GPU_SAMPLED_IMAGE | HardwareBuffer.USAGE_GPU_COLOR_OUTPUT);
        HardwareBufferRenderer renderer = new HardwareBufferRenderer(buffer);
        RenderNode node = new RenderNode("content");
        node.setPosition(0, 0, 100, 100);

        Canvas canvas = node.beginRecording();
        canvas.drawColor(Color.BLUE);

        Paint paint = new Paint();
        paint.setColor(Color.RED);
        canvas.drawRect(0f, 0f, 50f, 50f, paint);
        node.endRecording();

        renderer.setContentRoot(node);

        ColorSpace colorSpace = ColorSpace.get(Named.SRGB);
        Handler handler = new Handler(Looper.getMainLooper());
        renderer.obtainRenderRequest()
                .setColorSpace(colorSpace)
                .draw(Executors.newSingleThreadExecutor(), result -> {
                    result.getFence().await(Duration.ofMillis(3000));
                    handler.post(() -> {
                        Bitmap bitmap = Bitmap.wrapHardwareBuffer(buffer, colorSpace);
                        Bitmap copy = bitmap.copy(Config.ARGB_8888, false);
                        mImageView.setImageBitmap(copy);
                    });
                });
    }
}
