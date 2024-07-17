package com.example.particles;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.GLU;
import android.opengl.GLUtils;
import android.util.Log;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.security.PrivateKey;
import java.util.Random;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class GLRender implements Renderer {

    private Bitmap mBitmap;

    public float touchXn, touchYn, aspect;

    // 最大的粒子数量
    public final static int MAX_PARTICLES = 1000;

    // 切换效果
    boolean rainbow = true;
    // 随机种子
    Random random = new Random();
    // 减慢粒子速度
    float slowdown = 0.5f;

    // 速度
    float xspeed = 1;
    float yspeed = 3;

    //缩放
    float zoom = -30.0f;

    int loop;
    int col = 0;


    // 纹理
    int mTexture[];

    // 颜色数组
    static float colors[][]=
    {
            {0.0f,  0.8f,   0.3f},
            {0.0f,  0.75f,  0.1f},
            {0.0f,  0.4f,   0.0f},
            {0.2f, 1.0f,   0.0f},
            {0.2f,  1.0f,   0.0f},
            {0.2f,  1.0f,   0.71f},
            {0.2f,  1.0f,   0.2f},
            {0.2f,  0.75f,  0.1f},
            {0.2f,  0.8f,   0.0f},
            {0.2f, 0.8f,   0.0f},
            {0.0f,  0.83f,   0.0f},
            {0.0f,  0.5f,   0.75f},
    };

    // 粒子数组
    Particle particles[] = new Particle[MAX_PARTICLES];


    public GLRender(Context context)
    {
        mBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.glowdot);
        mTexture = new int[1];
    }

    @Override
    public void onDrawFrame(GL10 gl)
    {
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

        gl.glMatrixMode(GL10.GL_MODELVIEW);

        gl.glLoadIdentity();

        GLU.gluLookAt(gl, 0,0,3, 0,0,0, 0,1,0);

        draw(gl);
    }

    private void draw(GL10 gl)
    {
        FloatBuffer vertices = GLBufferWrapper.WrapFloat(new float[12]);
        FloatBuffer texcoords = GLBufferWrapper.WrapFloat(new float[8]);

        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertices);
        gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, texcoords);

        gl.glLoadIdentity();

        // 修改每一颗粒子
        for (loop = 0; loop < MAX_PARTICLES; loop++)
        {
            // 粒子是否被激活
            if (!particles[loop].active)
                continue;

            float x = particles[loop].x;
            float y = particles[loop].y;

            // z轴方向可能会放大
            float z = particles[loop].z + zoom;

            gl.glColor4f(particles[loop].r, particles[loop].g, particles[loop].b, particles[loop].life);

            texcoords.clear();
            vertices.clear();
            /* Top Right */
            texcoords.put(1.0f);
            texcoords.put(1.0f);
            vertices.put(x + 1.5f);
            vertices.put(y + 1.5f);
            vertices.put(z);

            /* Top Left */
            texcoords.put(0.0f);
            texcoords.put(1.0f);
            vertices.put(x - 1.5f);
            vertices.put(y - 1.5f);
            vertices.put(z);

            /* Bottom Right */
            texcoords.put(1.0f);
            texcoords.put(0.0f);
            vertices.put(x + 1.5f);
            vertices.put(y - 1.5f);
            vertices.put(z);

            /* Bottom Left */
            texcoords.put(0.0f);
            texcoords.put(0.0f);
            vertices.put(x - 1.5f);
            vertices.put(y - 1.5f);
            vertices.put(z);

            // 绘制四边形
            gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0,  4);

            // 通过x,y,z3个方向的速度分辨改变粒子的位置
            particles[loop].x += particles[loop].xi / (slowdown * 100);
            particles[loop].y += particles[loop].yi / (slowdown * 100);
            particles[loop].z += particles[loop].zi / (slowdown * 100);

            // x,y,z方向上的重力加速度
            particles[loop].xi += particles[loop].xg;
            particles[loop].yi += particles[loop].yg;
            particles[loop].zi += particles[loop].zg;

            // 控制粒子的生命逐渐衰减
            particles[loop].life -= particles[loop].fade;

            // 如果这个粒子死亡之后就重新绘制一个
            if( particles[loop].life < 0.0f)
            {
                float xi, yi, zi;

                xi = xspeed + rand()%60 - 32.0f;
                yi = yspeed + rand()%60 - 30.0f;
                zi = rand()%60 - 30.0f;
                resetParticle(loop, col, xi, yi, zi);
            }

        }

        gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height)
    {
        Log.i("GLRender", "onSurfaceChanged:: w: " + width + " h: " + height);
        aspect = (float)width/height;

        gl.glViewport(0,0,width,height);

        gl.glMatrixMode(GL10.GL_PROJECTION);

        gl.glLoadIdentity();

        gl.glFrustumf(-aspect, aspect, -1, 1, 1, 200);
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        Log.i("GLRender", "opengl version: " + gl10.glGetString(GL10.GL_VERSION));

        gl10.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);

        gl10.glClearColor(0.3F, 0.3F, 0.3F, 1.0F);      // 不要不写小数点！否则会没用。

        /* 注意：不能进行深度测试 */
        gl10.glDisable(GL10.GL_DEPTH_TEST);

        gl10.glEnable(GL10.GL_BLEND);

        gl10.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE);

        // 对采样点进行修正（按质量最好的方式）
        gl10.glHint(GL10.GL_POINT_SMOOTH, GL10.GL_NICEST);

        LoadTextures(gl10);

        initData();
    }

    private int rand()
    {
        return Math.abs(random.nextInt(1000));
    }

    private void initData()
    {
        for(loop = 0; loop < MAX_PARTICLES; loop++)
        {
            int color =  loop*(12/MAX_PARTICLES);
            float xi, yi ,zi;

            xi = (rand() % 50 - 26.0f) * 10.0f;
            yi = zi = (rand() % 50 - 25.0f) * 10.0f;

            resetParticle(loop, color, xi, yi, zi);
        }
    }

    private void resetParticle(int num, int color, float xDir, float yDir, float zDir)
    {
        Particle tmp = new Particle();

        tmp.active = true;

        tmp.life = 1.0f;

        tmp.fade = (float)(rand()%100)/1000.0f + 0.0003f;

        tmp.r = colors[color][0];

        tmp.g = colors[color][1];

        tmp.b = colors[color][2];

        tmp.x = touchXn * Math.abs(zoom);

        tmp.y = touchYn * Math.abs(zoom);

        tmp.z = 0.0f;

        tmp.xi = xDir;

        tmp.yi = yDir;

        tmp.zi = zDir;

        tmp.xg = 0.0f;

        tmp.yg = -0.5f;

        tmp.zg = 0.0f;

        particles[num] = tmp;
    }

    private void LoadTextures(GL10 gl)
    {
        gl.glEnable(GL10.GL_TEXTURE_2D);

        IntBuffer textureBuffer = GLBufferWrapper.WrapInt(new int[]{1});
        gl.glGenTextures(1, textureBuffer);
        mTexture[0] = textureBuffer.get();
        gl.glBindTexture(GL10.GL_TEXTURE_2D, mTexture[0]);

        gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
        gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);

        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, mBitmap, 0);
    }

}

























