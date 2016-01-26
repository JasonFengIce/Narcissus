package cn.ismartv.voice.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.widget.ImageView;

/**
 * Created by huaijie on 1/19/16.
 */
public class ImageUtil {

    public static void createReflectedImages(ImageView imageView, int resId) {
//        final int reflectionGap = 4;//原图与倒影之间的间隙
        Bitmap originalImage = BitmapFactory.decodeResource(imageView.getContext().getResources(), resId); // 获得图片资源
        createReflectedImages(imageView, originalImage);
    }


    public static void createReflectedImages(ImageView imageView, Bitmap originalImage) {
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();
        int imageWidth = imageView.getLayoutParams().width;
        int imageHeight = imageView.getLayoutParams().height;

//        float reflectRate = ((width * imageHeight / (float) imageWidth) - height) / height;

        float bitmapWithReflectionHeight = width * imageHeight / (float) imageWidth;

        Matrix matrix = new Matrix();
        matrix.preScale(1, -1); // 实现图片的反转
        Bitmap reflectionImage = Bitmap.createBitmap(originalImage, 0, 0, width, height, matrix, false); // 创建反转后的图片Bitmap对象，图片高是原图的一半


        Bitmap bitmapWithReflection = Bitmap.createBitmap(width, (int) bitmapWithReflectionHeight, Bitmap.Config.ARGB_8888); // 创建标准的Bitmap对象，宽和原图一致，高是原图的1.5倍


        Canvas canvas = new Canvas(bitmapWithReflection);
        canvas.drawBitmap(originalImage, 0, 0, null); // 创建画布对象，将原图画于画布，起点是原点位置
        Paint paint = new Paint();
//        canvas.drawRect(0, height, width, height + reflectionGap, paint);


        canvas.drawBitmap(reflectionImage, 0, height, null); // 将反转后的图片画到画布中

        LinearGradient shader = new LinearGradient(
                0,
                originalImage.getHeight(),
                0,
                2 * height,
                0x00000000, 0xff000000, Shader.TileMode.MIRROR);// 创建线性渐变LinearGradient对象
        paint.setShader(shader); // 绘制
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));//倒影遮罩效果
        canvas.drawRect(0, height, width, 2 * height, paint); // 画布画出反转图片大小区域，然后把渐变效果加到其中，就出现了图片的倒影效果

        paint.reset();
        paint.setColor(Color.BLACK);
        canvas.drawRect(0, 2 * height, width, bitmapWithReflection.getHeight(), paint);
        imageView.setImageBitmap(bitmapWithReflection); // 设置带倒影的Bitmap
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);//将图片按比例缩放
    }
}
