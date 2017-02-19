package com.example.he.jockbook.Utility.imageloader.loader;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.FileDescriptor;

/**
 * 图片压缩
 * 压缩步骤：
 * 1、获取BitmapFactory.Options，并将属性inJustDecodeBounds设置为true
 * 2、加载图片
 * 3、设置BitmapFactory.Options的inSampleSize,inSampleSize为2的指数;
 * 4、将属性inJustDecodeBounds设置为false
 * 5、重新加载图片
 * Created by he on 2017/1/9.
 */

public class ImageResizer {
    public ImageResizer() {
    }

    public Bitmap decodeSampledBitmapFromResoures(Resources res, int resId, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res,resId,options);
        options.inSampleSize=calculateInSiampleSize(options,reqWidth,reqHeight);
        options.inJustDecodeBounds=false;
        return BitmapFactory.decodeResource(res, resId);
    }


    public Bitmap decodeSampleBitmapFromFileDescriptor(FileDescriptor descriptor,int reqWidth,int reqHeight){

        final BitmapFactory.Options options=new BitmapFactory.Options();
        options.inJustDecodeBounds=true;
        BitmapFactory.decodeFileDescriptor(descriptor,null,options);
        options.inSampleSize=calculateInSiampleSize(options,reqWidth,reqHeight);
        options.inJustDecodeBounds=false;
        return BitmapFactory.decodeFileDescriptor(descriptor,null,options);
    }




    private int calculateInSiampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        if (reqWidth == 0 || reqHeight == 0)
            return 1;
        final int width = options.outWidth;
        final int heigth = options.outHeight;
        int inSampleSize = 1;

        if (heigth > reqHeight || width > reqWidth) {
            final int halfHeight = heigth / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }

        }
        return inSampleSize;
    }


}
