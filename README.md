# OpenGLES - Particles 
使用Android Studio开发，皆在演示使用OpenGLES 1.0实现粒子系统的使用方法。

使用方法： 
1.找到工程目录下的APK文件夹。 
2.安装APK文件夹下的APK程序。 
3.打开程序。 
4.手指在屏幕上移动，场景里的烟花也会随之移动，可观察烟花效果。

框架： MainActivity + GLSurfaceView. 顶点数据在GLSurfaceView的数组里定义，渲染循环里不断发射出一个以图片为贴图的小矩形，渲染时随机调整矩形的颜色，达到烟花效果。
