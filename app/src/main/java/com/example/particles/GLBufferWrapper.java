package com.example.particles;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;


public class GLBufferWrapper {

    public static FloatBuffer WrapFloat(float[] javaArray)
    {
        FloatBuffer buffer;
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(javaArray.length * 4);
        byteBuffer.order(ByteOrder.nativeOrder());
        buffer = byteBuffer.asFloatBuffer();
        buffer.put(javaArray);
        buffer.position(0);
        return buffer;
    }

    public static ShortBuffer WrapShort(short[] javaArray)
    {
        ShortBuffer buffer;
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(javaArray.length * 4);
        byteBuffer.order(ByteOrder.nativeOrder());
        buffer = byteBuffer.asShortBuffer();
        buffer.put(javaArray);
        buffer.position(0);
        return buffer;
    }

    public static IntBuffer WrapInt(int[] javaArray)
    {
        IntBuffer buffer;
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(javaArray.length * 4);
        byteBuffer.order(ByteOrder.nativeOrder());
        buffer = byteBuffer.asIntBuffer();
        buffer.put(javaArray);
        buffer.position(0);
        return buffer;
    }
}
