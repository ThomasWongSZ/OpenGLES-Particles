package com.example.particles;

import android.annotation.SuppressLint;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewTreeObserver;

import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {

    private GLSurfaceView mGLSurfaceView;

    private GLRender mGLRenderer;
    
    private float halfWidth, halfHeight;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 实例化GLSurfaceView (EGL处理opengles 与窗口系统）
        mGLSurfaceView = new GLSurfaceView(this);

        mGLRenderer = new GLRender(this);
        // 设置渲染器
        mGLSurfaceView.setRenderer(mGLRenderer);

        setContentView(mGLSurfaceView);

        mGLSurfaceView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // 移除监听以防重复调用
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    mGLSurfaceView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    mGLSurfaceView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }

                // 此时可以获取尺寸
                halfWidth = mGLSurfaceView.getWidth()/2;
                halfHeight = mGLSurfaceView.getHeight()/2;
                Log.i("Main", "halfWidth  = " + halfWidth + " halfHeight = " + halfHeight);
            }
        });

        mGLSurfaceView.setOnTouchListener((v, event) -> {
            float touchXs, touchYs;
            switch(event.getAction()){
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_MOVE:
                    // 当滑动时的处理
                {
                    touchXs = event.getRawX() - halfWidth;
                    touchYs = -(event.getRawY() - halfHeight);
                    mGLRenderer.touchYn = touchYs / halfHeight * 1.0f;     // 1.0是NEAR平面的距离
                    mGLRenderer.touchXn = (touchXs/ halfWidth) *  mGLRenderer.aspect * 1.0f;
                    //Log.i("Main", "touch Xn  = " + mGLRenderer.touchXn + " touch Yn = " + mGLRenderer.touchYn);
                }
                break;
            }
            return true;
        });

    }
}