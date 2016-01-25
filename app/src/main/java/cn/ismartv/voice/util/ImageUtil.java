package cn.ismartv.voice.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
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
        final int reflectionGap = 4;//原图与倒影之间的间隙
        int index = 0;
        Bitmap originalImage = BitmapFactory.decodeResource(imageView.getContext().getResources(), resId); // 获得图片资源
        // 获得图片的长宽
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();

        Matrix matrix = new Matrix();
        matrix.preScale(1, -1); // 实现图片的反转
        Bitmap reflectionImage = Bitmap.createBitmap(originalImage, 0, 0, width, height, matrix, false); // 创建反转后的图片Bitmap对象，图片高是原图的一半
        Bitmap bitmapWithReflection = Bitmap.createBitmap(width, (height + height), Bitmap.Config.ARGB_8888); // 创建标准的Bitmap对象，宽和原图一致，高是原图的1.5倍


        Canvas canvas = new Canvas(bitmapWithReflection);
        canvas.drawBitmap(originalImage, 0, 0, null); // 创建画布对象，将原图画于画布，起点是原点位置
        Paint paint = new Paint();
        canvas.drawRect(0, height, width, height + reflectionGap, paint);


        canvas.drawBitmap(reflectionImage, 0, height + reflectionGap, null); // 将反转后的图片画到画布中

        paint = new Paint();
        LinearGradient shader = new LinearGradient(
                0,
                originalImage.getHeight(),
                0,
                bitmapWithReflection.getHeight() + reflectionGap,
                0x70ffffff, 0x00ffffff, Shader.TileMode.MIRROR);// 创建线性渐变LinearGradient对象
        paint.setShader(shader); // 绘制
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));//倒影遮罩效果
        canvas.drawRect(0, height, width, bitmapWithReflection.getHeight() + reflectionGap, paint); // 画布画出反转图片大小区域，然后把渐变效果加到其中，就出现了图片的倒影效果

        imageView.setImageBitmap(bitmapWithReflection); // 设置带倒影的Bitmap
        //设置ImageView的大小，可以根据图片大小设置
        // imageView.setLayoutParams(newmyGallery.LayoutParams(width,height));
//        imageView.setLayoutParams(new ViewGroup.LayoutParams(250, 500));//设置ImageView的大小，可根据需要设置固定宽高
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);//将图片按比例缩放
    }
}
