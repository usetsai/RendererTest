package com.usetsai.renderertest;

import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.usetsai.renderertest.hwui.HardwareBufferRendererActivity;
import com.usetsai.renderertest.hwui.HardwareCanvasSurfaceViewActivity;
import com.usetsai.renderertest.hwui.HardwareCanvasTextureViewActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout layout = new LinearLayout(this);
        layout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        layout.setOrientation(LinearLayout.VERTICAL);

        addButton(layout, "start to HardwareBufferRendererActivity",
                HardwareBufferRendererActivity.class);
        addButton(layout, "start to HardwareCanvasSurfaceViewActivity",
                HardwareCanvasSurfaceViewActivity.class);
        addButton(layout, "start to HardwareCanvasTextureViewActivity",
                HardwareCanvasTextureViewActivity.class);

        setContentView(layout);
    }

    private void addButton(LinearLayout layout, String text, Class<?> cls) {
        Button button = new Button(this);
        button.setText(text);
        button.setOnClickListener(v -> startActivity(new Intent(this, cls)));
        layout.addView(button, LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
    }
}